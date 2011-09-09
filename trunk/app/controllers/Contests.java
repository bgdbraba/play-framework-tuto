package controllers;

import models.Answer;
import models.Exam;
import models.Question;
import play.data.validation.Required;
import play.mvc.With;

@With(Secure.class)
public class Contests extends AbstractController {

	public static void go(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		render(exam);
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