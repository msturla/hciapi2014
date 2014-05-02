package ar.edu.itba.hciapi.model;

public class Account {
	
	private long id;
	private String username;
	private String firstName;
	private String lastName;
	private String gender;
	private String identityCard;
	private String email;
	private String birthDate;
	private String createdDate;
	private String lastLoginDate;
	private String lastPasswordChange;
	
	// gson requires empty constructor
	Account() { };
	
	public Account(long id, String username, String firstName, String lastName, String gender,
			String identityCard, String email, String birthDate, String createdDate, 
			String lastLoginDate, String lastPasswordChange) {
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.identityCard = identityCard;
		this.email = email;
		this.birthDate = birthDate;
		this.createdDate = createdDate;
		this.lastLoginDate = lastLoginDate;
		this.lastPasswordChange = lastPasswordChange;
	}
	
	public String getBirthDate() {
		return birthDate;
	}
	
	public String getCreatedDate() {
		return createdDate;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public long getId() {
		return id;
	}
	
	public String getIdentityCard() {
		return identityCard;
	}
	
	public String getLastLoginDate() {
		return lastLoginDate;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getLastPasswordChange() {
		return lastPasswordChange;
	}
	
	public String getUsername() {
		return username;
	}
}
