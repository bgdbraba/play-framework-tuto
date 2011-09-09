package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Enumerated;

import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
 
	@Email
	@Required
	public String email;

	@Password
	public String password;

	@Required
	public String firstname;

	@Required
	public String lastname;

	public Date birthdate;

	public static enum Profile {

		ADMIN, MANAGER, EXAMINER;

	}

	@Enumerated
	public Profile profile;

	public User(String email, String password, String firstname, String lastname, Date birthdate) {
		super();
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.birthdate = birthdate;
	}

	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id + " - " + firstname + " " + lastname;
	}

}