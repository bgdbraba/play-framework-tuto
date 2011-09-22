package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.User;
import play.Play;
import play.data.binding.As;
import play.data.validation.Equals;
import play.data.validation.Password;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.With;
import controllers.CRUD.For;

@With(Secure.class)
@For(User.class)
public class Profiles extends AbstractController {

	public static void view() {
		User user = (User) renderArgs.get("user");
		boolean creditable = user.isExaminer();
		render(user, creditable);
	}

	public static void save(
			@Required(message = "Firstname is required") String firstname,
			@Required(message = "Lastname is required") String lastname,
			@Required(message = "Password is required") @Password String password,
			@Required(message = "Confirmation is required") @Equals(value = "password", message = "Different password") String confirm_password,
			@As(value = { "yyyy-MM-dd" }) Date birthdate) {
		
		User user = (User) renderArgs.get("user");
		
		if (validation.hasErrors()) {
			boolean creditable = user.isExaminer();
			render("Profiles/view.html", user, creditable, firstname, lastname, password,
					birthdate);
		}
		
		user.firstname = firstname;
		user.lastname = lastname;
		user.password = password;
		user.birthdate = birthdate;
		user.save();
		view();

	}

	public static void addCredit(Integer sum) {
		User user = (User) renderArgs.get("user");
		user.addCredit(sum);
		renderText(user.credit.toString());
	}

	public static void viewUserMenu() {
		User user = (User) renderArgs.get("user");
		render("Profiles/user_menu.html", user);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Profile");
	}

}