package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Exam;
import models.Post;
import models.Question;
import models.User;
import play.Play;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Exams extends CRUD {

	public static void go(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		render(exam);
	}

	public static void beginQuiz(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		exam.nextQuestion();
		Question question = exam.quiz.questions.get(exam.currentQuestion);
		System.out.println(question.questionType);
		render(exam, question);
	}

	public static void create() {
		String examKey = Exam.generateFreeExamKey();
		Exam exam = new Exam(examKey);
		exam.save();
		render(exam);
	}

	public static void search(String title, Integer difficulty, Integer second, Long groupType) {
		List<Question> questions = Question.search(title, difficulty, second, groupType);

		render(questions);
	}

	public static void createFirstStep(Long examId, @Required String firstname, @Required String lastname,
			@Required String email, String birthdate) {

		Date parsedDate = null;
		if (birthdate != null && birthdate.length() > 0) {
			try {
				parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthdate);
			} catch (ParseException e) {
				// -- Add error
				// validation.errors().add(new Error(key, message, variables));
				System.out.println("Bad date");
			}
		}
		User candidate = new User(email, null, firstname, lastname, parsedDate);
		Exam exam = Exam.findById(examId);

		if (validation.hasErrors()) {
			exam.delete();
			render("ExamController/create.html", exam);
		}

		candidate.save();
		exam.candidate = candidate;
		exam.save();
		render("ExamController/create.html", exam);

	}

	public static void nextQuestion(String examKey) {
		beginQuiz(examKey);
	}

	public static void show(Long id) {
		Post post = Post.findById(id);
		render(post);
	}

	public static void storeCandidate(Long examId, @Required String password, @Required String confirm_password,
			String birthdate) {
		Exam exam = Exam.findById(examId);
		System.out.println(examId);
		exam.candidate.password = password;
		exam.candidate.save();
		// System.out.println("password " + password);
		// System.out.println("confirm " + confirm_password);
		// System.out.println("birth " + birthdate);
		if (validation.hasErrors()) {
			render("ExamController/go.html", exam);
		}
		beginQuiz(exam.examKey);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle", Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline", Play.configuration.getProperty("site.baseline"));
	}

}