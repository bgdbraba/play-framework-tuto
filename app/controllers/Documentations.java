package controllers;

import models.User;
import play.mvc.With;

@Check({ "ADMIN" })
@With(Secure.class)
public class Documentations extends AbstractController { 

	public static void index() {
		User user = (User) renderArgs.get("user");
		render(user);
	}

}