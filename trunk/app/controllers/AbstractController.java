package controllers;

import java.util.List;

import models.Quiz;
import models.User;
import play.Play;
import play.mvc.Before;

public class AbstractController extends CRUD {  

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle", Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline", Play.configuration.getProperty("site.baseline"));
	}

	@Before
	static void loadRecentQuizzes() {
		List<Quiz> recentQuizzes = Quiz.findAll();
		renderArgs.put("recentQuizzes", recentQuizzes);
	}

}
