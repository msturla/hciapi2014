package ar.edu.itba.hciapi.api;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.hciapi.model.Account;
import ar.edu.itba.hciapi.model.ProductAttribute;
import ar.edu.itba.hciapi.model.SignInResult;

class MockApi implements HciApi {
	
	// ATTRIBUTES
	private static final ProductAttribute ATTR_1 = 
			new ProductAttribute(2, "marca", null);
	private static final ProductAttribute ATTR_2 = 
			new ProductAttribute(4, "tamano", null);
	private static final ProductAttribute ATTR_3 = 
			new ProductAttribute(4, "color", makeList("red", "blue", "green"));
	
	private static final String AUTH_TOKEN = "123123";
	private static final Account ACCOUNT = new Account(23, "john", "John", "Doe", "M",
			"1111111", "batman@gmail.com", "2010-02-01", "2013-09-29 00:00", "2013-10-02 14:47",
			"2013-09-29 00:00");

	@Override
	public void getAttributes(ApiCallback<List<ProductAttribute>> callback) {
		callback.call(makeList(ATTR_1, ATTR_2), null);
	}
	
	@Override
	public void getAttributeById(long id, ApiCallback<ProductAttribute> callback) {
		callback.call(ATTR_3, null);
	}
	
	@Override
	public void signIn(String username, String password,
			ApiCallback<SignInResult> callback) {
		callback.call(new SignInResult(AUTH_TOKEN, ACCOUNT), null);
		
	}
	
	/**
	 * Creates a list from a variable argument call.
	 * Warning: since java casts T... to T[] to Object[], 
	 * the caller is responsible to make sure the method 
	 * is called with an actual array of T.
	 * @param args The arguments to include in the list.
	 * @return the list with args
	 */
	@SafeVarargs
	private static <T> List<T> makeList(T... args) {
		List<T> list = new ArrayList<>();
		for (T arg : args) {
			list.add(arg);
		}
		return list;
	}
	
}
