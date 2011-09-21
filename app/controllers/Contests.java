package controllers;

import java.net.MalformedURLException;
import java.util.Date;

import models.Answer;
import models.Exam;
import models.Question;
import models.Answer.AnswerValue;
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

	public static void go(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		User user = (User) renderArgs.get("user");
		boolean isUser = (user == exam.candidate);
		render(exam, isUser);
	}

	// public static void go(String examKey) {
	// User user = (User) renderArgs.get("user");
	// Exam exam = null;
	// if (examKey == null || examKey.length() == 0) {
	// exam = Exam.findForUser(user);
	// } else {
	// exam = Exam.findByKey(examKey);
	// }
	// boolean isUser = (user == exam.candidate);
	// render(exam, isUser);
	// }

	public static void beginQuiz(String examKey) throws Exception {
		Exam exam = Exam.findByKey(examKey);
		if (exam.isFinished()) {
			finish(examKey, false);
		}
		exam.nextQuestion();
		if (exam.currentQuestion >= exam.answers.size()) {
			System.out.println("########### FINISH ###########");
			for (Answer answer : exam.answers) {
				System.out.println(answer.question.title + " : ");
			}
			// FINISH
			finish(examKey, true);
		} else {
			Question question = exam.answers.get(exam.currentQuestion).question;
			render(exam, question);
		}
	}

	public static void finish(String examKey, boolean sendMail)
			throws MalformedURLException, EmailException {
		Exam exam = Exam.findByKey(examKey);
		// Send mail to exam.author
		System.out.println("Mail has been sent to " + exam.author.email
				+ " for exam " + exam.examKey);
		exam.finish();
		exam.save();
		if (sendMail) {
			sendMail(exam);
		}
		render(exam);
	}

	private static void sendMail(Exam exam) throws EmailException,
			MalformedURLException {

		HtmlEmail examinerEmail = new HtmlEmail();
		examinerEmail.addTo(exam.author.email);
		examinerEmail.setFrom("exam@liloquiz.com", "Lilo Quiz");
		examinerEmail.setSubject("Exam fininsh for " + exam.candidate.firstname
				+ " " + exam.candidate.lastname);
		examinerEmail
				.setHtmlMsg("<html><body><p><a href=\'http://localhost:9000/exam/show/"
						+ exam.examKey
						+ "\'>Exam finished</a></p></body></html>");
		// set the alternative message
		examinerEmail.setTextMsg("Exam finished");
		Mail.send(examinerEmail);

		System.out.println("Mail has been sent to " + exam.candidate.email
				+ " for finisnhed contest " + exam.examKey);

		HtmlEmail candidateEmail = new HtmlEmail();
		candidateEmail.addTo(exam.candidate.email);
		candidateEmail.setFrom("exam@liloquiz.com", "Lilo Quiz");
		candidateEmail.setSubject("You have finished your exam");

		candidateEmail
				.setHtmlMsg("<html><body><p>Exam finished</p></body></html>");
		// set the alternative message
		candidateEmail.setTextMsg("Exam finished");
		Mail.send(candidateEmail);

		System.out.println("Mail has been sent to " + exam.candidate.email
				+ " for finisnhed contest " + exam.examKey);

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
		System.out.println(type);
		if (type.equals(QuestionType.SIMPLE_CHOICE)) {
			answer.addAnswerValue(simpleResponse);
		} else if (type.equals(QuestionType.TEXT_ANSWER)) {
			answer.addAnswerValue(textResponse);
		} else {
			answer.addAnswerValue(response1, response2, response3, response4,
					response5);
		}
		System.out.println(exam.answers.size() + " rep");
		System.out.println(answer.question.title);
		System.out.println(answer.answerValues.size());
		for (AnswerValue v : answer.answerValues) {
			System.out.println(v.value);
		}
		System.out.println("##########################");
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
	public static void storeCandidate(Long examId, @Required(message = "Password is required") @Password String password,
			@Required(message = "Confirmation is required") @Equals(value = "password", message = "Different password") String confirmPassword,
			@As(value = { "yyyy-MM-dd" }) Date birthdate) 
			throws Exception {
		
		Exam exam = Exam.findById(examId);
		
		if(birthdate!=null){
			exam.candidate.birthdate=birthdate;
		}
		exam.candidate.password = password;
		exam.candidate.save();
		// System.out.println("password " + password);
		// System.out.println("confirm " + confirm_password);
		// System.out.println("birth " + birthdate);
		if (validation.hasErrors()) {
			render("Contest/go.html", exam, birthdate);
		}
		beginQuiz(exam.examKey);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Contest");
	}

}