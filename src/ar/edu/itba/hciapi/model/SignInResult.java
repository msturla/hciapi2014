package ar.edu.itba.hciapi.model;

public class SignInResult {

	private String authenticationToken;
	private Account account;
	
	// empty constructor required by gson
	SignInResult() { };
	
	public SignInResult(String authenticationToken, Account account) {
		this.authenticationToken = authenticationToken;
		this.account = account;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public String getToken() {
		return authenticationToken;
	}
}
