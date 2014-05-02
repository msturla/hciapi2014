package ar.edu.itba.hciapi.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import ar.edu.itba.hciapi.model.ProductAttribute;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Makes a remote API call and will try to build an object of type T from the
 * json response. The caller may limit the search to a certain field by
 * supplying the limitField in the constructor call. This will try to build T
 * with a subset of the json response. Use this if you only need a specific
 * field of the response.
 * <p>
 * Error response fields are handled appropiately regardless of this choice.
 * </p>
 * <p>
 * Extra response fields that do not belong to T will be ignored, as specified
 * by the Gson documentation. Missing fields will be marked as null.
 * </p>
 * <p>
 * Extra parameters may also be supplied by the params parameter
 * </p>
 * 
 * @param <T>
 *            The type of the desired field(s) type. For example if the output
 *            field is: <code>"some_key" : [1, 2, 3]</code>, T should be a list
 *            of Integers.
 */
public class ApiCallTask<T> extends AsyncTask<Void, Void, Void> {

	private static final String TAG = "EiffelApi";

	// General constants
	private static final String BASE_API_LINK = "http://eiffel.itba.edu.ar/hci/service3/";
	private static final String METHOD_PARAM = "method";
	private static final String PARAM_BEGIN = "?";
	private static final String PARAM_SEPARATOR = "&";
	private static final String PARAM_ASSIGNMENT = "=";
	private static final String ERROR_CODE_FIELD = "code";
	private static final String ERROR_MSG_FIELD = "message";
	private static final String ERROR_RESPONSE_FIELD = "error";

	// The callback invoked with the result. Ran on the UIthread.
	private ApiCallback<T> callback;

	// The type of the type parameter T.
	private Type type;

	// the Api Method which is called
	private Method method;

	// A nullable field limit. If null, the whole json will be used to try and
	// build T.
	// If specified, will only use a subset of the json. For instance, given the
	// json:
	// { "key1":1, "key2": "hello", "key3": [1, 2, 3]}
	// if this is null, the whole json will be used to build T, and T should
	// have an int key1 field,
	// a String key2 field and a list<integer> key3 field. If limitField is
	// "key3", only [1, 2, 3]
	// will be used to build T, so T should be a list of integers.
	private String limitField;

	// A map of key value string pairs representing any extra parameters passed
	// to the API call.
	private Map<String, String> params;

	// The result of the call
	private T result;

	// Any exception thrown during the fetch process.
	private Exception exception;

	private ApiCallTask(Builder<T> builder) {
		requiredArg(builder.callback, "Callback function ");
		requiredArg(builder.type, "Type of result");
		requiredArg(builder.method, "Api Method");
		this.callback = builder.callback;
		this.type = builder.type;
		this.method = builder.method;
		this.limitField = builder.limitField;
		this.params = builder.params;
	}

	private static void requiredArg(Object obj, String arg) {
		if (obj == null)
			throw new IllegalArgumentException(arg + " can not be null.");
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		HttpURLConnection urlConnection = null;
		InputStream is = null;
		try {
			URL url = getUrl(method, params);
			Log.i(TAG, "Requesting URL: " + url.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			is = urlConnection.getInputStream();
			String response = convertStreamToString(is);

			Log.i(TAG, "Response from server: " + response);
			JSONObject obj = new JSONObject(response);
			if (obj.has(ERROR_RESPONSE_FIELD)) {
				exception = getApiException(obj
						.getJSONObject(ERROR_RESPONSE_FIELD));
			} else {
				if (limitField != null) {
					// limit our search to the specified field
					response = getParamFromJson(obj, limitField);
					if (response == null) {
						exception = new JSONException(
								"Failed to retrieve limitField " + limitField
										+ " from json: " + response);
						return null;
					}
				}
				result = new Gson().fromJson(response, type);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "Error in url: " + e.getMessage());
			exception = e;
		} catch (JSONException e) {
			Log.e(TAG, "Error while parsing Json: " + e.getMessage());
			exception = e;
		} catch (IOException e) {
			Log.e(TAG, "Error while connecting to Eiffel: " + e.getMessage());
			exception = e;
		} finally {
			close(urlConnection);
			close(is);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void unused) {
		callback.call(result, exception);
	}

	/**
	 * Builds a GET url with several parameters
	 * 
	 * @param method
	 *            The API Method.
	 * @param params
	 *            A Map containing key,value parameters to add.
	 * @return The final built URL
	 * @throws MalformedURLException
	 *             If the final URL is invalid
	 */
	private URL getUrl(Method method, Map<String, String> params)
			throws MalformedURLException {
		StringBuilder urlWithParams = new StringBuilder();
		urlWithParams.append(BASE_API_LINK).append(method.domain);
		appendParam(urlWithParams, METHOD_PARAM, method.method, PARAM_BEGIN);
		for (Entry<String, String> entry : params.entrySet()) {
			appendParam(urlWithParams, entry.getKey(), entry.getValue(),
					PARAM_SEPARATOR);
		}
		return new URL(urlWithParams.toString());
	}

	/**
	 * Appends a GET parameter to a StringBuilder containing the URL
	 * 
	 * @param builder
	 *            StringBuilder with the url
	 * @param key
	 *            The key of the parameter to add
	 * @param value
	 *            The value of the parameter to add
	 * @param beginChar
	 *            The character to begin the parameter (may be PARAM_BEGIN or
	 *            PARAM_SEPARATOR)
	 */
	private void appendParam(StringBuilder builder, String key, String value,
			String beginChar) {
		builder.append(beginChar).append(key).append(PARAM_ASSIGNMENT)
				.append(value);
	}

	/**
	 * Close a Http Connection
	 * 
	 * @param c
	 *            The connection to close
	 */
	private void close(HttpURLConnection c) {
		if (c == null)
			return;
		c.disconnect();
	}

	/**
	 * Close a resource
	 * 
	 * @param c
	 *            The resource to close
	 */
	private void close(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (IOException e) {
			Log.e(TAG, "Could not close resource: " + e.getMessage());
		}
	}

	/**
	 * Extracts a parameter from a jsonObject.
	 * 
	 * @param obj
	 *            The json object.
	 * @param paramName
	 *            The name of the field.
	 * @return The field as a string, or null if a parse error occurred.
	 */
	private String getParamFromJson(JSONObject obj, String paramName) {
		if (!obj.has(paramName))
			return null;
		try {
			return obj.getString(paramName);
		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to parse json while extracting parameter: "
							+ e.getMessage());
			return null;
		}
	}

	/**
	 * Extracts an error returned by the API from a json object
	 * 
	 * @param obj
	 *            The json object.
	 * @return An exception with a relevant message.
	 */
	private Exception getApiException(JSONObject obj) {
		try {
			return new ApiException(obj.getString(ERROR_MSG_FIELD), 
					obj.getInt(ERROR_CODE_FIELD));
		} catch (JSONException e) {
			Log.e(TAG,
					"Eiffel returned an error, but body was malformed: "
							+ e.getMessage());
			return new JSONException("API threw an error, but we were unable to parse it: "
					+ obj.toString());
		}
		
	}

	/**
	 * 
	 * @param is
	 *            Input stream to parse. Should be closed by caller.
	 * @return string from the input stream.
	 */
	private String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public static class Builder<T> {

		private ApiCallback<T> callback;
		private Type type;
		private Method method;
		private String limitField;
		private Map<String, String> params;

		public Builder() {
			params = new HashMap<String, String>();
		};

		/**
		 * Sets the callback function that will be called when this ApiCall
		 * finishes.
		 * 
		 * @param callback
		 *            The callback to be invoked
		 * @return an instance to this builder for chaining
		 */
		public Builder<T> setCallback(ApiCallback<T> callback) {
			this.callback = callback;
			return this;
		}

		/**
		 * Limits the instantiation of T to a certain subset of the result json.
		 * If this is set, field limitField will be searched for in the response
		 * json and will be the only part used to construct T.
		 * 
		 * @param limitField
		 *            The key of the json field.
		 * @return an instance to this builder for chaining
		 */
		public Builder<T> setLimitField(String limitField) {
			this.limitField = limitField;
			return this;
		}

		/**
		 * Sets the API Method.
		 * 
		 * @param method
		 *            The api method which is to be called.
		 * @return an instance to this builder for chaining
		 */
		public Builder<T> setMethod(Method method) {
			this.method = method;
			return this;
		}

		/**
		 * Adds an extra parameter to this api call
		 * 
		 * @param key
		 *            The key of the parameter
		 * @param value
		 *            The value of the parameter
		 * @return an instance to this builder for chaining
		 */
		public Builder<T> addParam(String key, String value) {
			params.put(key, value);
			return this;
		}

		/**
		 * Sets the type of the type argument. For instance, if this is a
		 * builder of a list of integers, then this should be:
		 * <code>new TypeToken&lt;List&lt;Integer&gt;&gt;() {}.getType();</code>
		 * 
		 * @param type
		 *            the type of the generic argument
		 * @return an instance to this builder for chaining
		 */
		public Builder<T> setType(Type type) {
			this.type = type;
			return this;
		}

		/**
		 * Returns an ApiCallTask instance with the information loaded on this
		 * builder.
		 * 
		 * @return an ApiCallTask instance.
		 */
		public ApiCallTask<T> build() {
			return new ApiCallTask<T>(this);
		}

	}

}
