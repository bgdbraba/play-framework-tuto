package models;

import java.util.*;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class User extends Model {

	public String email;
	public String password;
	public String firstname;
	public String lastname;
	public Date birthdate;


	public User(String email, String password, String firstname,
			String lastname, Date birthdate) {
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