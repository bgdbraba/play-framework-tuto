package controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import models.Answer;
import models.Exam;
import models.Quiz;
import models.User;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import play.data.validation.Required;
import play.libs.Mail;
import play.mvc.Before;
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
			render("Exams/create.html", exam, firstname, lastname, email,
					birthdate);
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

	public static void storeQuiz(Long examId, Long quizId) throws Exception {
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

	private static void sendMail(Exam exam) throws EmailException,
			MalformedURLException {
		// -- Stub SEND_MAIL
		String pwd = exam.candidate.changePassword();
		HtmlEmail email = new HtmlEmail();
		email.addTo(exam.candidate.email);
		email.setFrom("inscription@liloquiz.com", "Lilo Quiz");
		email.setSubject("A new EXAM for you");
		// embed the image and get the content id
		URL url = new URL(
				"http://www.zenexity.fr/wp-content/themes/zenexity/images/logo.png");
		String cid = email.embed(url, "Zenexity logo");
		// set the html message
		email.setHtmlMsg("<html><body>Zenexity logo - <img src=\"cid:" + cid
				+ "\"><p><a href=\'http://localhost:9000/contest/"
				+ exam.examKey + "\'>New exam</a></p><p>Your password : " + pwd
				+ "</p></body></html>");
		// set the alternative message
		email.setTextMsg("Your email client does not support HTML messages, too bad :(");
		Mail.send(email);
		System.out.println("Mail has been sent to " + exam.candidate.email
				+ " for contest/" + exam.examKey + " [" + pwd + "]");
	}

	public static void searchQuiz(Long examId, String quizTitle,
			Integer difficulty, Integer minutes, Long groupType,
			Integer questions) {

		List<Quiz> quizzes = Quiz.search(quizTitle, difficulty,
				minutes != null ? 60 * minutes : null, groupType, questions);

		Exam exam = Exam.findById(examId);
		render("Exams/create.html", exam, quizzes, quizTitle, difficulty, minutes,
				groupType, questions);
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
		render(exam);
	}
	
	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Exam");
	}

}