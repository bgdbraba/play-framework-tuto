package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Exam extends Model {

	public final static int EXAM_PRICE = 3;

	@ManyToOne
	public User candidate;

	@ManyToOne
	public Quiz quiz;

	public Calendar creationDate;
	public Calendar startingDate;
	public Calendar endingDate;
	public String examKey;
	public int currentQuestion;

	@ManyToOne
	public User author;

	@Enumerated
	public ExamState state;

	public int second;

	@OneToMany(cascade = CascadeType.PERSIST, targetEntity = Answer.class)
	public List<Answer> answers;

	public enum ExamState {

		INIT, VALIDATED, PAID, CANCELED, IN_PROGRESS, FINISHED;

	}

	public Exam(User author) {
		super();
		this.author = author;
		this.creationDate = Calendar.getInstance();
		this.currentQuestion = -1;
		this.state = ExamState.INIT;
	}

	public void initAnswers() {
		if (quiz != null) {
			answers = new ArrayList<Answer>();
			for (Question question : quiz.questions) {
				Answer answer = new Answer();
				answer.question = question;
				answers.add(answer);
			}
		}
	}

	public Exam(String examKey, User author) {
		this(author);
		this.examKey = examKey;
	}

	public static Exam findByKey(String examKey) {
		return find("byExamKey", examKey).first();
	}

	public void nextQuestion() {
		if (startingDate == null) {
			startingDate = Calendar.getInstance();
			endingDate = startingDate;
			endingDate.add(Calendar.SECOND, second);
			currentQuestion = 0;
		} else {
			currentQuestion++;
		}
		this.save();
	}

	public static String generateFreeExamKey() {
		String examKey = new Random().nextInt(100000000) + "";
		if (findByKey(examKey) != null) {
			return generateFreeExamKey();
		}
		return examKey;
	}

	public boolean isFinished() {
		return endingDate != null && endingDate.before(Calendar.getInstance());
	}

	public void storeQuiz(Quiz quiz) {
		this.quiz = quiz;
		this.second = quiz.second;
		initAnswers();
		state = ExamState.VALIDATED;
	}

	public static List<Exam> search(String email, String examKey, User user) {
		List<Exam> exams = null;
		List<Object> params = new ArrayList<Object>();

		String query = "author.id = ? ";
		params.add(user.id);

		if (email != null && email.trim().length() > 0) {
			query += " and candidate.email = ?";
			params.add(email);
		}
		if (examKey != null && examKey.trim().length() > 0) {
			query += " and examKey like ?";
			params.add(examKey);
		}

		exams = find(query, params.toArray()).fetch();
		System.out.println(query + " - > " + exams.size());

		return exams;
	}

	@Override
	public String toString() {
		return candidate + " " + examKey;
	}

	public boolean isPaid() {
		return state != ExamState.INIT && state != ExamState.VALIDATED;
	}

	public void paid() {
		state = ExamState.PAID;
	}

	public void validate() {
		state = ExamState.VALIDATED;
	}
	
	public void finish() {
		endingDate=Calendar.getInstance();
		state = ExamState.FINISHED;
	}

	public static Exam findForUser(User user) {
		Exam exam = find("byCandidateAndState", user, ExamState.PAID).first();
		return exam;
	}

}
