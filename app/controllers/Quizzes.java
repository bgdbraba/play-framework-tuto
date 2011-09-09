package controllers;

import java.util.Arrays;
import java.util.List;

import models.Question;
import models.Quiz;
import play.data.validation.Required;
import play.mvc.With;
import controllers.CRUD.For;

@Check({ "MANAGER" })
@With(Secure.class)
@For(Quiz.class)
public class Quizzes extends AbstractController { 

	public static void show(Long quizId) {
		Quiz quiz = Quiz.findById(quizId);
		render(quiz);
	}

	public static void storeQuiz(@Required String title, @Required int difficulty, @Required int minutes,
			@Required int[] groupTypes) {

		Quiz quiz = new Quiz();
		quiz.create();
		create(quiz.id);
	}

	public static void create(Long quizId) {
		if (quizId == null) {
			render();
		}
		Quiz quiz = Quiz.findById(quizId);
		render(quiz);
	}

	public static void search(String title, Integer difficulty, Integer second, Long[] groupType, Integer seconds) {
		// TODO
		List<Quiz> quizzes = Quiz.findAll();
		render(quizzes);
	}

	public static void searchQuestions(@Required int difficulty, @Required int minutes, @Required Long[] groupTypes) {
		List<Question> questions = Question.search(null, difficulty, 60 * minutes, Arrays.asList(groupTypes));
		render(questions);
	}
}