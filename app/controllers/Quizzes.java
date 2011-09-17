package controllers;

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

	public static void storeQuiz(Long quizId, @Required String title, @Required int difficulty, @Required int second,
			@Required long[] groupTypes) {

		Quiz quiz = new Quiz();
		quiz.create();
		create(quiz.id, null);
	}

	public static void create(Long quizId, List<Question> questions) {
		System.out.println("quizId" + quizId);
		if (quizId == null) {
			render();
		}
		Quiz quiz = Quiz.findById(quizId);
		render(quiz, questions);
	}

	public static void search(String title, Integer difficulty, Integer second, Long groupType, Integer questions) {
		List<Quiz> quizzes = Quiz.search(title, difficulty, second, groupType, questions);
		render(quizzes);
	}

	public static void searchQuestions(Long quizId, String title, int difficulty, int second, Long groupType) {
		List<Question> questions =
				Question.search(null, difficulty, 60 * second, groupType != null ? new Long[]{ groupType } : null);
		System.out.println(questions.size() + " questions found");

		Quiz quiz = Quiz.findById(quizId);

		renderTemplate("Quizzes/create.html", quiz, questions);
	}
}