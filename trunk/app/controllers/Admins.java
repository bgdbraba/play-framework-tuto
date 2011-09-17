package controllers;

import play.mvc.With;

@Check({ "ADMIN" })
@With(Secure.class)
public class Admins extends AbstractController {

	public static void index() {
		render("CRUD/index.html");
	}

}