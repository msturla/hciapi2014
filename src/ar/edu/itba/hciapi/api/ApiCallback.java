package ar.edu.itba.hciapi.api;

/**
 * Interface representing a callback that will be 
 * invoked once the API finishes an operation.
 *
 */
public interface ApiCallback<T> {

	/**
	 * A method that will be called after the API call is finished.
	 * Both parameters may be null depending on the result of the operation.
	 * 
	 * @param result The result of the operation. May be null.
	 * @param exception Exception thrown by the operation, if any.
	 */
	public void call(T result, Exception exception);
}
