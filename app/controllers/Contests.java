package controllers;

import java.net.MalformedURLException;
import java.util.Date;

import models.Answer;
import models.Exam;
import models.Question;
import models.Question.QuestionType;
import models.User;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Before;

import play.data.binding.As;
import play.data.validation.Equals;
import play.data.validation.Password;
import play.data.validation.Required;
import play.libs.Mail;
import play.mvc.With;

@With(Secure.class)
public class Contests extends AbstractController {

	/**
	 * 
	 * @param examKey
	 */
	public static void go(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		User user = (User) renderArgs.get("user");
		boolean isUser = (user == exam.candidate);
		render(exam, isUser);
	}

	/**
	 * 
	 * @param examKey
	 * @throws Exception
	 */
	public static void beginQuiz(String examKey) throws Exception {
		Exam exam = Exam.findByKey(examKey);
		if (exam.isFinished()) {
			finish(examKey, false);
		}
		exam.nextQuestion();
		if (exam.currentQuestion >= exam.answers.size()) {
			// System.out.println("########### FINISH ###########");
			// for (Answer answer : exam.answers) {
			// System.out.println(answer.question.title + " : ");
			// }
			// FINISH
			finish(examKey, true);
		} else {
			Question question = exam.answers.get(exam.currentQuestion).question;
			render(exam, question);
		}
	}

	/**
	 * 
	 * @param examKey
	 * @param sendMail
	 * @throws MalformedURLException
	 * @throws EmailException
	 */
	public static void finish(String examKey, boolean sendMail)
			throws MalformedURLException, EmailException {
		Exam exam = Exam.findByKey(examKey);
		// Send mail to exam.author
		if (!exam.isFinished()) {
			exam.finish();
			exam.save();
			if (sendMail) {
				sendMail(exam);
			}
		}
		render(exam);
	}

	/**
	 * 
	 * @param exam
	 * @throws EmailException
	 * @throws MalformedURLException
	 */
	private static void sendMail(Exam exam) throws EmailException,
			MalformedURLException {

		HtmlEmail examinerEmail = new HtmlEmail();
		examinerEmail.addTo(exam.author.email);
		examinerEmail.setFrom("exam@liloquiz.com", "Lilo Quiz");
		examinerEmail.setSubject("Exam fininsh for " + exam.candidate.firstname
				+ " " + exam.candidate.lastname);
		examinerEmail
				.setHtmlMsg("<html><body>"
						+ "<p>Hi <b>"
						+ exam.author.firstname
						+ " "
						+ exam.author.lastname
						+ "</b></p><br/>"
						+ "<p>The exam <b>"
						+ exam.examKey
						+ "</b> is terminated, you can see the result in the \"Exam\" menu.</p>"
						+ "<p>Candidate : <b>" + exam.candidate.email
						+ "</b></p>" + "<p>ExamKey  : <b>" + exam.examKey
						+ "</b></p>" + "<br/>" + "<p>Thanks</p>"
						+ "</body></html>");

		examinerEmail.setTextMsg("Exam " + exam.examKey + "finished");
		Mail.send(examinerEmail);

		// System.out.println("Mail has been sent to " + exam.candidate.email
		// + " for finisnhed contest " + exam.examKey);

		HtmlEmail candidateEmail = new HtmlEmail();
		candidateEmail.addTo(exam.candidate.email);
		candidateEmail.setFrom("exam@liloquiz.com", "Lilo Quiz");
		candidateEmail.setSubject("You have finished your exam");

		candidateEmail.setHtmlMsg("<html><body>" + "<p>Hi <b>"
				+ exam.candidate.firstname + " " + exam.candidate.lastname
				+ "</b></p><br/>" + "<p>You have finished your exam <b>"
				+ exam.examKey + "</b>.</p>"
				+ "<p>The examiner contact you later.</p><br/>"
				+ "<p>See you soon on Lilo Quiz,</p>" + "<p>Thanks</p>"
				+ "</body></html>");
		// set the alternative message
		candidateEmail.setTextMsg("Exam finished");
		Mail.send(candidateEmail);

		// System.out.println("Mail has been sent to " + exam.candidate.email
		// + " for finisnhed contest " + exam.examKey);

	}

	/**
	 * 
	 * @param examKey
	 * @param simpleResponse
	 * @param response1
	 * @param response2
	 * @param response3
	 * @param response4
	 * @param response5
	 * @param textResponse
	 * @throws Exception
	 */
	public static void nextQuestion(String examKey, Integer simpleResponse,
			Boolean response1, Boolean response2, Boolean response3,
			Boolean response4, Boolean response5, String textResponse)
			throws Exception {

		Exam exam = Exam.findByKey(examKey);
		Answer answer = exam.answers.get(exam.currentQuestion);
		QuestionType type = answer.question.questionType;
		// System.out.println(type);
		if (type.equals(QuestionType.SIMPLE_CHOICE)) {
			answer.addAnswerValue(simpleResponse);
		} else if (type.equals(QuestionType.TEXT_ANSWER)) {
			answer.addAnswerValue(textResponse);
		} else {
			answer.addAnswerValue(response1, response2, response3, response4,
					response5);
		}
		// System.out.println(exam.answers.size() + " rep");
		// System.out.println(answer.question.title);
		// System.out.println(answer.answerValues.size());
		// for (AnswerValue v : answer.answerValues) {
		// System.out.println(v.value);
		// }
		// System.out.println("##########################");
		answer.save();
		beginQuiz(examKey);
	}

	/**
	 * 
	 * @param examId
	 * @param password
	 * @param confirmPassword
	 * @param birthdate
	 * @throws Exception
	 */
	public static void storeCandidate(
			Long examId,
			@Required(message = "Password is required") @Password String password,
			@Required(message = "Confirmation is required") @Equals(value = "password", message = "Different password") String confirmPassword,
			@As(value = { "yyyy-MM-dd" }) Date birthdate) throws Exception {

		Exam exam = Exam.findById(examId);
		User user = (User) renderArgs.get("user");
		boolean isUser = (user == exam.candidate);

		if (validation.hasErrors()) {

			render("Contests/go.html", exam, birthdate, isUser);
		}

		if (birthdate != null) {
			exam.candidate.birthdate = birthdate;
		}
		exam.candidate.password = password;
		exam.candidate.save();

		beginQuiz(exam.examKey);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Contest");
	}

}