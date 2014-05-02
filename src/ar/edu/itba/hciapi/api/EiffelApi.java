package ar.edu.itba.hciapi.api;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.itba.hciapi.model.ProductAttribute;
import ar.edu.itba.hciapi.model.SignInResult;

import com.google.gson.reflect.TypeToken;

class EiffelApi implements HciApi {
	
	// General constants

	//TODO add extra method definitions here
	
	// Method Domains
	private static final String COMMON = "Common.groovy";
	private static final String ACCOUNT = "Account.groovy";
	
	// Methods
	private static final Method GET_ATTRIBUTES = new Method(COMMON, "GetAllAttributes");
	private static final Method GET_ATTRIBUTE_BY_ID = new Method(COMMON, "GetAttributeById");
	private static final Method SIGN_IN = new Method(ACCOUNT, "SignIn");
	
	// OutputParamNames
	private static final String LIMIT_FIELD_ATTRIBUTES = "attributes";
	private static final String LIMIT_FIELD_ATTRIBUTE = "attribute";
	
	@Override
	public void getAttributes(ApiCallback<List<ProductAttribute>> callback) {
		Type t = new TypeToken<List<ProductAttribute>>() {}.getType();
		new ApiCallTask<List<ProductAttribute>>(callback, t, GET_ATTRIBUTES,
				LIMIT_FIELD_ATTRIBUTES).execute();
	}

	@Override
	public void getAttributeById(long id,
			ApiCallback<ProductAttribute> callback) {
		Map<String, String> params = new HashMap<>();
		params.put("id", String.valueOf(id));
		Type t = new TypeToken<ProductAttribute>() {}.getType();
		new ApiCallTask<ProductAttribute>(callback, t, GET_ATTRIBUTE_BY_ID,
				LIMIT_FIELD_ATTRIBUTE, params).execute();
	}

	@Override
	public void signIn(String username, String password,
			ApiCallback<SignInResult> callback) {
		Map<String, String> params = new HashMap<>();
		params.put("username", username);
		params.put("password", password);
		Type t = new TypeToken<SignInResult>() {}.getType();
		new ApiCallTask<SignInResult>(callback, t, SIGN_IN,
				params).execute();
	}
	
	//TODO add extra api calls here

	
}
