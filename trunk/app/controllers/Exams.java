package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Answer;
import models.Exam;
import models.Quiz;
import models.User;
import play.data.validation.Required;
import play.mvc.With;
import controllers.CRUD.For;

@Check({ "EXAMINER" })
@With(Secure.class)
@For(Exam.class)
public class Exams extends AbstractController {

	public static void create() {
		String examKey = Exam.generateFreeExamKey();
		Exam exam = new Exam(examKey);
		exam.save();
		render(exam);
	}

	public static void search(String candidate, String examKey) {
		List<Exam> exams = Exam.search(candidate, examKey);

		render(exams);
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
			render("Exams/create.html", exam);
		}

		candidate.save();
		exam.candidate = candidate;
		exam.save();
		render("Exams/create.html", exam);

	}

	public static void storeQuiz(Long examId, Long quizId) {
		Quiz quiz = Quiz.findById(quizId);
		Exam exam = Exam.findById(examId);
		exam.storeQuiz(quiz);
		for (Answer answer : exam.answers) {
			answer.merge();
		}
		exam.save();
		render("Exams/create.html", exam);
	}

	public static void searchQuiz(Long examId) {

		List<Quiz> quizzes = Quiz.findAll();
		Exam exam = Exam.findById(examId);
		render("Exams/create.html", exam, quizzes);
	}

	// public static void searchQuiz(Long examId) {
	// Exam exam = null;
	// if (examId == null) {
	// exam = new Exam(Exam.generateFreeExamKey());
	// } else {
	// exam = Exam.findById(examId);
	// }
	// List<Quiz> quizzes = Quiz.findAll();
	// render("Exams/create.html", exam, quizzes);
	// }

	public static void show(Long examId) {
		Exam exam = Exam.findById(examId);
		System.out.println(exam);
		render(exam);
	}

}