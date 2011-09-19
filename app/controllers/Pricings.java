package controllers;

import play.mvc.Before;

public class Pricings extends AbstractController {

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Pricing");
	}
	
	public static void index() {
		render();
	}

}
