package controllers;

import models.Exam;
import models.Post;
import models.Question;
import models.Question.QuestionType;
import play.Play;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

public class ExamController extends Controller {

	public static void go(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		render(exam);
	}

	public static void beginQuestionnaire(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		exam.nextQuestion();
		Question question = exam.questionnaire.questions.get(exam.currentQuestion);
		System.out.println(question.questionType);
		render(exam, question);
	}
	
	public static void nextQuestion(String examKey) {
		beginQuestionnaire(examKey);
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
//		System.out.println("password " + password);
//		System.out.println("confirm " + confirm_password);
//		System.out.println("birth " + birthdate);
		if (validation.hasErrors()) {
			render("ExamController/go.html", exam);
		}
		beginQuestionnaire(exam.examKey);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle",
				Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline",
				Play.configuration.getProperty("site.baseline"));
	}

}