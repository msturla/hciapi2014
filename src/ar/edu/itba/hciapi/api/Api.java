package ar.edu.itba.hciapi.api;

public class Api {
	
	private static final boolean USE_MOCK_API = false;
	
	private static HciApi instance;
	
	public static synchronized HciApi get() {
		if (instance == null) {
			if (USE_MOCK_API) {
				instance = new MockApi();
			} else {
				instance = new EiffelApi();
			}
		}
		return instance;
	}

}
