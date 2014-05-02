package ar.edu.itba.hciapi.api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

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

	/**
	 * Constructor with extra parameters and a limitField.
	 * 
	 * @param callback
	 *            The callback which will be called when the operation finishes.
	 * @param type
	 *            The type of T (the result).
	 * @param method
	 *            The Method which will be invoked on the API
	 * @param limitField
	 *            Limit the supplied json from which T is build to this field.
	 * @param params
	 *            Extra parameters to include in the request. May be empty.
	 */
	public ApiCallTask(ApiCallback<T> callback, Type type, Method method,
			String limitField, Map<String, String> params) {
		this.callback = callback;
		this.type = type;
		this.method = method;
		this.limitField = limitField;
		this.params = params;
	}

	/**
	 * Constructor with no extra parameters and a limitField.
	 * 
	 * @param callback
	 *            The callback which will be called when the operation finishes.
	 * @param type
	 *            The type of the T (the result).
	 * @param method
	 *            The Method which will be invoked on the API
	 * @param limitField
	 *            Limit the supplied json from which T is build to this field.
	 */
	public ApiCallTask(ApiCallback<T> callback, Type type, Method method,
			String limitField) {
		this(callback, type, method, limitField, new HashMap<String, String>());
	}

	/**
	 * Constructor with extra parameters and no limitField.
	 * 
	 * @param callback
	 *            The callback which will be called when the operation finishes.
	 * @param type
	 *            The type of T (the result).
	 * @param method
	 *            The Method which will be invoked on the API
	 * @param params
	 *            Extra parameters to include in the request. May be empty.
	 */
	public ApiCallTask(ApiCallback<T> callback, Type type, Method method,
			Map<String, String> params) {
		this(callback, type, method, null, params);
	}

	/**
	 * Constructor with no extra parameters and no limitField.
	 * 
	 * @param callback
	 *            The callback which will be called when the operation finishes.
	 * @param type
	 *            The type of T (the result).
	 * @param method
	 *            The Method which will be invoked on the API
	 */
	public ApiCallTask(ApiCallback<T> callback, Type type, Method method) {
		this(callback, type, method, null, new HashMap<String, String>());
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
						exception = new ApiException(
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
		String msg;
		try {
			msg = String.format("Error Code %d: %s",
					obj.getInt(ERROR_CODE_FIELD),
					obj.getString(ERROR_MSG_FIELD));
		} catch (JSONException e) {
			Log.e(TAG,
					"Eiffel returned an error, but body was malformed: "
							+ e.getMessage());
			msg = "API threw an error, but we were unable to parse it: "
					+ obj.toString();
		}
		return new ApiException(msg);
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

}
