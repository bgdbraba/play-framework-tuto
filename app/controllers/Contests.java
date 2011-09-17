package controllers;

import models.Answer;
import models.Exam;
import models.Question;
import models.User;

import org.junit.Before;

import play.data.validation.Required;
import play.mvc.With;

@With(Secure.class)
public class Contests extends AbstractController {

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Contest");
	}

	public static void go(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		User user = (User) renderArgs.get("user");
		boolean isUser = (user == exam.candidate);
		render(exam, isUser);
	}

	public static void beginQuiz(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		if (exam.isFinished()) {
			finish(examKey);
		}
		exam.nextQuestion();
		if (exam.currentQuestion >= exam.answers.size()) {
			System.out.println("########### FINISH ###########");
			for (Answer answer : exam.answers) {
				System.out.println(answer.question.title + " : ");
			}
			// FINISH
			finish(examKey);
		} else {
			Question question = exam.answers.get(exam.currentQuestion).question;
			render(exam, question);
		}
	}

	public static void finish(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		// Send mail to exam.author
		System.out.println("Mail has been sent to " + exam.author.email + " for exam " + exam.examKey);
		render(exam);
	}

	public static void nextQuestion(String examKey, String simpleResponse, String response1, String response2,
			String response3, String response4, String response5, Boolean correct1, Boolean correct2, Boolean correct3,
			Boolean correct4, Boolean correct5) {
		beginQuiz(examKey);
	}

	public static void storeCandidate(Long examId, @Required String password, @Required String confirm_password,
			String birthdate) {
		Exam exam = Exam.findById(examId);
		exam.candidate.password = password;
		exam.candidate.save();
		// System.out.println("password " + password);
		// System.out.println("confirm " + confirm_password);
		// System.out.println("birth " + birthdate);
		if (validation.hasErrors()) {
			render("Exams/go.html", exam);
		}
		beginQuiz(exam.examKey);
	}

}