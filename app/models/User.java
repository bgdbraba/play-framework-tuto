package models;

import java.util.Date;

import javax.persistence.Entity;

import play.data.validation.Email;
import play.data.validation.Password;
import play.db.jpa.Model;

@Entity
public class User extends Model {

	@Email
	public String email;

	@Password
	public String password;

	public String firstname;
	public String lastname;

	public Date birthdate;

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

}