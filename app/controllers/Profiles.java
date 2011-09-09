package controllers;

import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.With;
import controllers.CRUD.For;

@With(Secure.class)
@For(User.class)
public class Profiles extends AbstractController {

	public static void show(Long userId) {
		render();
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle", Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline", Play.configuration.getProperty("site.baseline"));
	}

}