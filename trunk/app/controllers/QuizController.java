package controllers;

import java.util.Iterator;

import models.Question;
import models.Quiz;
import models.Response;
import play.Play;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

public class QuizController extends Controller {

	public static void show(Long quizId) {
		Quiz quiz = Quiz.findById(quizId);
		render(quiz);
	}

	public static void storeQuiz(@Required String title,
			@Required int difficulty, @Required int minutes,
			@Required int[] groupTypes) {

		// TODO

	}

	public static void create() {
		render();
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle",
				Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline",
				Play.configuration.getProperty("site.baseline"));
	}

}