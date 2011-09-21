package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.User;
import play.Play;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.With;
import controllers.CRUD.For;

@With(Secure.class)
@For(User.class)
public class Profiles extends AbstractController {

	public static void show() {
		User user = (User) renderArgs.get("user");
		boolean creditable = user.isExaminer();
		render(user, creditable);
	}

	public static void save(@Required String firstname, @Required String lastname, @Required String password,
			@Required String confirm_password, String birthdate) {
		User user = (User) renderArgs.get("user");
		user.firstname = firstname;
		user.lastname = lastname;
		user.password = password;

		Date parsedDate = null;
		if (birthdate != null && birthdate.length() > 0) {
			try {
				parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthdate);
			} catch (ParseException e) {
				// -- Add error
				// validation.errors().add(new Error(key, message, variables));
				System.out.println("Bad date");
			}
		}
		user.birthdate = parsedDate;
		user.save();
		show();

	}

	public static void addCredit(Integer sum) {
		User user = (User) renderArgs.get("user");
		user.addCredit(sum);
		renderText(user.credit.toString());
	}
	
	public static void showUserMenu(){
		User user = (User) renderArgs.get("user");
		render("Profiles/user_menu.html", user);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Profile");
	}

}