package controllers;

import controllers.CRUD.For;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;

@For(models.User.class)
public class Profiles extends CRUD {

	public static void show() {
		render();
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle", Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline", Play.configuration.getProperty("site.baseline"));
	}

}