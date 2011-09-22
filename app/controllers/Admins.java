package controllers;

import play.mvc.Before;
import play.mvc.With;

@Check({ "ADMIN" })
@With(Secure.class)
public class Admins extends AbstractController {

	public static void index() {
		//System.out.println("ADMINISTRATION");
		render("CRUD/index.html");
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "CRUD");
	}

}