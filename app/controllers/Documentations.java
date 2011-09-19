package controllers;

import play.mvc.Before;
import models.User;

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