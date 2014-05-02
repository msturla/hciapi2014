package ar.edu.itba.hciapi.api;

/**
 * Class representing an API method.
 *
 */
class Method {
	
	public final String domain;
	public final String method;

	public Method(String domain, String method) {
		this.domain = domain;
		this.method = method;
	}
}
