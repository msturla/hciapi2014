package ar.edu.itba.hciapi.api;

import java.lang.reflect.Type;
import java.util.List;

import ar.edu.itba.hciapi.model.ProductAttribute;
import ar.edu.itba.hciapi.model.SignInResult;

import com.google.gson.reflect.TypeToken;

class EiffelApi implements HciApi {

	// General constants

	// TODO add extra method definitions here

	// Method Domains
	private static final String COMMON = "Common.groovy";
	private static final String ACCOUNT = "Account.groovy";

	// Methods
	private static final Method GET_ATTRIBUTES = new Method(COMMON,
			"GetAllAttributes");
	private static final Method GET_ATTRIBUTE_BY_ID = new Method(COMMON,
			"GetAttributeById");
	private static final Method SIGN_IN = new Method(ACCOUNT, "SignIn");

	// OutputParamNames
	private static final String LIMIT_FIELD_ATTRIBUTES = "attributes";
	private static final String LIMIT_FIELD_ATTRIBUTE = "attribute";

	@Override
	public void getAttributes(ApiCallback<List<ProductAttribute>> callback) {
		Type t = new TypeToken<List<ProductAttribute>>() {}.getType();
		new ApiCallTask.Builder<List<ProductAttribute>>().setCallback(callback)
				.setType(t).setMethod(GET_ATTRIBUTES)
				.setLimitField(LIMIT_FIELD_ATTRIBUTES).build().execute();
	}

	@Override
	public void getAttributeById(long id, ApiCallback<ProductAttribute> callback) {
		Type t = new TypeToken<ProductAttribute>() {}.getType();
		new ApiCallTask.Builder<ProductAttribute>().setCallback(callback)
				.setType(t).setMethod(GET_ATTRIBUTE_BY_ID)
				.setLimitField(LIMIT_FIELD_ATTRIBUTE)
				.addParam("id", String.valueOf(id)).build().execute();
	}

	@Override
	public void signIn(String username, String password,
			ApiCallback<SignInResult> callback) {
		Type t = new TypeToken<SignInResult>() {}.getType();
		new ApiCallTask.Builder<SignInResult>().setCallback(callback)
				.setType(t).setMethod(SIGN_IN).addParam("username", username)
				.addParam("password", password).build().execute();
	}

	// TODO add extra api calls here

}
