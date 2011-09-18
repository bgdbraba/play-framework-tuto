package models;

import java.util.Date;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

	@Temporal(TemporalType.DATE)
	public Date birthdate;

	public Integer credit;

	@Enumerated
	public UserState state;

	public Date creationDate;

	public int validationNumber;

	public static enum UserState {
		WAITING_VALIDATION, CREATED, CANCELED;
	}

	public static enum Profile {

		ADMIN, MANAGER, EXAMINER;

	}

	@Enumerated
	public Profile profile;

	public User(String email, String password, String firstname,
			String lastname, Date birthdate) {
		super();
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.birthdate = birthdate;
		this.state = UserState.WAITING_VALIDATION;
		this.creationDate = new Date();
		this.validationNumber = 10000000 + new Random().nextInt(1000000);
	}

	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id + " - " + firstname + " " + lastname;
	}
	
	public static User findByEmail(String email) {
		System.out.println("find with " + email);
		return User.find("byEmail", email).first();
	} 

	public String changePassword() {
		this.password = Integer.toHexString(new Random().nextInt(1000000));
		return this.password;
	}

	public boolean isAdmin() {
		return profile != null && Profile.ADMIN.equals(profile);
	}

	public boolean isManager() {
		return profile != null && Profile.MANAGER.equals(profile);
	}

	public boolean isExaminer() {
		return profile != null && Profile.EXAMINER.equals(profile);
	}

	public void addCredit(Integer sum) {
		if (this.credit == null) {
			this.credit = 0;
		}
		this.credit += sum;
		this.save();
	}

	public void removeCredit(Integer sum) {
		if (this.credit == null) {
			this.credit = 0;
		}
		this.credit -= sum;
		this.save();
	}
}