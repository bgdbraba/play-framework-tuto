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
		User user = (User) renderArgs.get("user");
		Exam exam = new Exam(examKey, user);
		exam.save();
		System.out.println(user + " creating the new exam " + exam.examKey);
		render(exam);
	}

	public static void search(String email, String examKey) {
		List<Exam> exams = Exam.search(email, examKey,
				(User) renderArgs.get("user"));
		render(exams, email, examKey);
	}

	public static void createFirstStep(Long examId, @Required String firstname,
			@Required String lastname, @Required String email, String birthdate) {

		Date parsedDate = null;
		if (birthdate != null && birthdate.length() > 0) {
			try {
				parsedDate = new SimpleDateFormat("yyyy-MM-dd")
						.parse(birthdate);
			} catch (ParseException e) {
				// -- Add error
				// validation.errors().add(new Error(key, message, variables));
				System.out.println("Bad date");
			}
		}

		User candidate = User.findByEmail(email);
		
		Exam exam = Exam.findById(examId);

		if (validation.hasErrors()) {
			// exam.delete();
			render("Exams/create.html", exam, firstname, lastname, email, birthdate);
		}

		// New Candidate
		if (candidate == null) {
			candidate = new User(email, null, firstname, lastname, parsedDate);
			candidate.save();
		}
		
		exam.candidate = candidate;
		exam.validate();
		exam.save();
		render("Exams/create.html", exam);

	}

	public static void storeQuiz(Long examId, Long quizId) {
		Quiz quiz = Quiz.findById(quizId);
		Exam exam = Exam.findById(examId);

		if (!exam.isPaid()) {

			exam.storeQuiz(quiz);
			for (Answer answer : exam.answers) {
				answer.merge();
			}
			exam.save();

			exam.author.removeCredit(Exam.EXAM_PRICE);
			exam.paid();
			exam.save();
			sendMail(exam);
		}

		render("Exams/create.html", exam);
	}

	private static void sendMail(Exam exam) {
		// -- Stub SEND_MAIL
		String pwd = exam.candidate.changePassword();
		System.out.println("Mail has been sent to " + exam.candidate.email
				+ " for contest/" + exam.examKey + " [" + pwd + "]");

	}

	public static void searchQuiz(Long examId, String title,
			Integer difficulty, Integer minutes, Long groupType,
			Integer questions) {

		List<Quiz> quizzes = Quiz.search(title, difficulty,
				minutes != null ? 60 * minutes : null, groupType, questions);

		Exam exam = Exam.findById(examId);
		render("Exams/create.html", exam, quizzes, title, difficulty, minutes, groupType, questions);
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