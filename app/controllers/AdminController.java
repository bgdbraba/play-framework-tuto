package controllers;

import play.Play;
import play.mvc.Before;
import play.mvc.Controller;

public class AdminController extends Controller {

	public static void show() {
		render();
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle", Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline", Play.configuration.getProperty("site.baseline"));
	}

}