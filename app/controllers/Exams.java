package controllers;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import models.Answer;
import models.Answer.AnswerValue;
import models.Exam;
import models.Exam.ExamState;
import models.Quiz;
import models.User;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Required;
import play.libs.Mail;
import play.mvc.Before;
import play.mvc.With;
import controllers.CRUD.For;

@Check({ "EXAMINER" })
@With(Secure.class)
@For(Exam.class)
public class Exams extends AbstractController {

	/**
	 * 
	 */
	public static void create() {
		String examKey = Exam.generateFreeExamKey();
		User user = (User) renderArgs.get("user");
		Exam exam = new Exam(examKey, user);
		exam.save();
//		System.out.println(user + " creating the new exam " + exam.examKey);
		render(exam);
	}

	/**
	 * 
	 * @param email
	 * @param examKey
	 */
	public static void search(String email, String examKey) {
//		System.out.println(Exam.findAll().size());
		List<Exam> exams = Exam
				.search(email, examKey, (User) renderArgs.get("user"), Arrays
						.asList(ExamState.PAID, ExamState.VALIDATED,
								ExamState.CANCELED, ExamState.IN_PROGRESS));
		render(exams, email, examKey);
	}

	/**
	 * 
	 * @param examId
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param birthdate
	 */
	public static void createFirstStep(
			Long examId,
			@Required(message = "Firstname is required") String firstname,
			@Required(message = "Lastname is required") String lastname,
			@Required(message = "Email is required") @Email(message = "Corrupted email") String email,
			@As(value = { "yyyy-MM-dd" }) Date birthdate) {

		User candidate = User.findByEmail(email);

		Exam exam = Exam.findById(examId);

		if (validation.hasErrors()) {
			render("Exams/create.html", exam, firstname, lastname, email,
					birthdate);
		}

		// New Candidate
		if (candidate == null) {
			candidate = new User(email, null, firstname, lastname, birthdate);
			candidate.save();
		}

		exam.candidate = candidate;
		exam.validate();
		exam.save();
//		System.out.println("Exam for candidate " + exam.candidate.email);
		render("Exams/create.html", exam);

	}

	/**
	 * 
	 * @param examId
	 * @param quizId
	 * @throws Exception
	 */
	public static void storeQuiz(Long examId, Long quizId) throws Exception {

		Exam exam = Exam.findById(examId);
		Quiz quiz = Quiz.findById(quizId);

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

	/**
	 * 
	 * @param exam
	 * @throws EmailException
	 * @throws MalformedURLException
	 */
	private static void sendMail(Exam exam) throws EmailException,
			MalformedURLException {
		// -- Stub SEND_MAIL

		HtmlEmail email = new HtmlEmail();
		email.addTo(exam.candidate.email);
		email.setFrom("inscription@liloquiz.com", "Lilo Quiz");
		email.setSubject("A new EXAM for you");

		// set the html message
		if (exam.candidate.password == null) {
			String pwd = exam.candidate.changePassword();
			email.setHtmlMsg("<html><body>"
					+ "<p>Hi <b>"
					+ exam.candidate.firstname
					+ " "
					+ exam.candidate.lastname
					+ "</b></p><br/>"
					+ "<p>Your can begin your quiz on <b>Lilo Quiz</b> by logging in and clicking on \"Go to my EXAM\" menu.<br/></p>"
					+ "<p>Username : <b>" + exam.candidate.email + "</b></p>"
					+ "<p>Password : <b>" + pwd + "</b></p>" + "<br/>"
					+ "<p>ExamKey  : <b>" + exam.examKey + "</b></p>" + "<br/>"
					+ "<p>Good Luck</p>" + "</body></html>");
		} else {
			email.setHtmlMsg("<html><body>"
					+ "<p>Hi <b>"
					+ exam.candidate.firstname
					+ " "
					+ exam.candidate.lastname
					+ "</b></p><br/>"
					+ "<p>Your can begin your quiz on <b>Lilo Quiz</b> by logging in and clicking on \"Go to my EXAM\" menu with your username.<br/></p>"
					+ "<p>ExamKey  : <b>" + exam.examKey + "</b></p>" + "<br/>"
					+ "<p>Good Luck</p>" + "</body></html>");
		}

		// set the alternative message
		email.setTextMsg("Your email client does not support HTML messages, too bad :(");
		Mail.send(email);
//		System.out.println("Mail has been sent to " + exam.candidate.email
//				+ " for contest/" + exam.examKey + " ["
//				+ exam.candidate.password + "]");
	}

	/**
	 * 
	 * @param examId
	 * @param quizTitle
	 * @param difficulty
	 * @param minutes
	 * @param groupType
	 * @param questions
	 */
	public static void searchQuiz(Long examId, String quizTitle,
			Integer difficulty, Integer minutes, Long groupType,
			Integer questions) {

		List<Quiz> quizzes = Quiz.search(quizTitle, difficulty,
				minutes != null ? 60 * minutes : null, groupType, questions);

		Exam exam = Exam.findById(examId);
		render("Exams/create.html", exam, quizzes, quizTitle, difficulty,
				minutes, groupType, questions);
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

	public static void view(Long examId) {
		Exam exam = Exam.findById(examId);
//		System.out.println(exam.answers.size() + " rep");
//		for (Answer answer : exam.answers) {
//			System.out.println(answer.question.title);
//			System.out.println(answer.answerValues.size());
//			for (AnswerValue v : answer.answerValues) {
//				System.out.println(v.value);
//			}
//			System.out.println("##########################");
//		}
		render(exam);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Exam");
	}

}