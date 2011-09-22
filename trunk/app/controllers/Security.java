package controllers;

import play.db.jpa.JPABase;
import models.User;
import models.User.Profile;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		User user = (User) User.find("byEmail", username).first();
		if (user == null) {
			return false;
		} else {
			System.out.println(user.password + " " + password);
			 return password.equals(user.password);
		}

		
	}

	static boolean check(String profile) {
		User user = (User) User.find("byEmail", Security.connected()).first();
		return user != null
				&& user.profile != null
				&& (user.profile.equals(Profile.ADMIN) || profile
						.equals(user.profile.toString()));
	}
}