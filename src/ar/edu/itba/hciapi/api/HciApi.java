package ar.edu.itba.hciapi.api;

import java.util.List;

import ar.edu.itba.hciapi.model.ProductAttribute;
import ar.edu.itba.hciapi.model.SignInResult;

public interface HciApi {
	
	
	public void getAttributes(ApiCallback<List<ProductAttribute>> callback);
	
	public void getAttributeById(long id, ApiCallback<ProductAttribute> callback);
	
	public void signIn(String username, String password,
			ApiCallback<SignInResult> callback);
}
