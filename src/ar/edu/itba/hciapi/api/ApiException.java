package ar.edu.itba.hciapi.api;

/**
 * 	An exception representing a misuse of the API.
 * 	Examples include: missing parameters, invalid parameters, etc.
 */
public class ApiException extends Exception {

	int code;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ApiException(String message, int errorCode) {
		super(message);
		this.code = errorCode;
	}
	
	
	public int getErrorCode() {
		return code;
	}

	
}
