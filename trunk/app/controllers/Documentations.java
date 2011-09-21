package controllers;

import models.User;
import play.mvc.Before;

public class Documentations extends AbstractController {

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Documentation");
	}

	public static void index() {
		User user = (User) renderArgs.get("user");
		render(user);
	}

}