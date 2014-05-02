package ar.edu.itba.hciapi.api;

/**
 * 	An exception representing a misuse of the API.
 * 	Examples include: missing parameters, invalid parameters, etc.
 */
public class ApiException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ApiException(String message) {
		super(message);
	}

	
}
