package controllers;

import java.util.List;

import models.GroupType;
import models.Question;
import models.Quiz;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.With;
import controllers.CRUD.For;

@Check({ "MANAGER" })
@With(Secure.class)
@For(Quiz.class)
public class Quizzes extends AbstractController {

	public static void view(Long quizId) {
		Quiz quiz = Quiz.findById(quizId);
		render(quiz);
	}

	public static void storeQuiz(
			@Required(message = "Title is required") String quizTitle,
			String description,
			@Required(message = "At least one type is required") Long[] groupTypes) {

		List<GroupType> groups = GroupType.findByIds(groupTypes);

		if (validation.hasErrors()) {
			render("Quizzes/create.html", quizTitle, description, groups);
		}
		
		Quiz quiz = new Quiz(quizTitle, description, groups);
		quiz.create();
		create(quiz.id);
	}

	public static void validateQuiz(Long quizId) {
		Quiz quiz = Quiz.findById(quizId);
		quiz.validate();
		render("Quizzes/view.html", quiz);
	}

	public static void create(Long quizId) {
		if (quizId == null) {
			render();
		}
		Quiz quiz = Quiz.findById(quizId);
		render(quiz);
	}

	public static void search(String quizTitle, Integer difficulty,
			Integer second, Long groupType, Integer nbQuestion) {
		List<Quiz> quizzes = Quiz.search(quizTitle, difficulty, second,
				groupType, nbQuestion);
		render(quizzes, quizTitle, difficulty, second, groupType, nbQuestion);
	}

	public static void searchQuestions(Long quizId, String questionTitle,
			int difficulty, int second, Long groupType) {

		List<Question> questions = Question.search(questionTitle, difficulty,
				second, groupType == null ? new Long[] {}
						: new Long[] { groupType });

		Quiz quiz = Quiz.findById(quizId);

		render("Quizzes/create.html", quiz, questions, questionTitle,
				difficulty, second, groupType);
	}

	public static void toogleQuestion(Long quizId, Long questionId) {
		Quiz quiz = Quiz.findById(quizId);
		Question question = Question.findById(questionId);
		if (quiz.questions.contains(question)) {
			quiz.questions.remove(question);
			quiz.save();
			renderText(false);
		} else {
			quiz.questions.add(question);
			quiz.save();
			renderText(true);
		}
	}
	
	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Quiz");
	}
}