package controllers;

import models.User;
import models.User.Profile;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		return true;
	}

	static boolean check(String profile) {
		User user = (User) User.find("byEmail", Security.connected()).first();
		System.out.println("Need " + profile);
		return user != null && user.profile != null
				&& (user.profile.equals(Profile.ADMIN) || profile.equals(user.profile.toString()));
	}
}