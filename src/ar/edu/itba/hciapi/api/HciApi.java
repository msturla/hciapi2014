package ar.edu.itba.hciapi.api;

import java.util.List;

import ar.edu.itba.hciapi.model.ProductAttribute;
import ar.edu.itba.hciapi.model.SignInResult;

public interface HciApi {

	/**
	 * Returns the list of available attributes.
	 * 
	 * @param callback
	 *            The callback that will be invoked upon success or error.
	 */
	public void getAttributes(ApiCallback<List<ProductAttribute>> callback);

	/**
	 * Returns a specific attribute.
	 * 
	 * @param id
	 *            The id of the attribute
	 * @param callback
	 *            The callback that will be invoked upon success or error.
	 */
	public void getAttributeById(long id, ApiCallback<ProductAttribute> callback);

	/**
	 * Signs the user in.
	 * 
	 * @param username
	 *            The username of the user
	 * @param password
	 *            The password of the user
	 * @param callback
	 *            The callback that will be invoked upon success or error.
	 */
	public void signIn(String username, String password,
			ApiCallback<SignInResult> callback);
}
