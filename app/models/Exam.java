package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
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

	public int possiblePoint;

	public int resultPoint;

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
		this.possiblePoint = 0;
		this.resultPoint = 0;
		if (this.quiz != null) {
			this.answers = new ArrayList<Answer>();
			for (Question question : this.quiz.questions) {
				this.possiblePoint += question.difficulty;
				Answer answer = new Answer();
				answer.question = question;
				this.answers.add(answer);
			}
		}
	}

	public void calculateFinalResult() {
		this.resultPoint = 0;
		if (this.answers != null) {
			for (Answer answer : this.answers) {
				if (answer.isCorrect()) {
					resultPoint += answer.question.difficulty;
				}
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

	/**
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return endingDate != null && endingDate.before(Calendar.getInstance());
	}

	/**
	 * 
	 * @param quiz
	 */
	public void storeQuiz(Quiz quiz) {
		this.quiz = quiz;
		this.second = quiz.second;
		initAnswers();
		state = ExamState.VALIDATED;
	}

	/**
	 * 
	 * @param email
	 * @param examKey
	 * @param user
	 * @param examStates
	 * @return
	 */
	public static List<Exam> search(String email, String examKey, User user,
			List<ExamState> examStates) {
		List<Exam> exams = null;
		List<Object> params = new ArrayList<Object>();

		String query = "author.id = ? and state is not null";
		params.add(user.id);

		if (email != null && email.trim().length() > 0) {
			query += " and candidate.email = ?";
			params.add(email);
		}
		if (examKey != null && examKey.trim().length() > 0) {
			query += " and examKey like ?";
			params.add(examKey);
		}

		if (examStates != null && examStates.size() > 0) {

			query += " and (";
			// I'm sorry :)
			for (ExamState state : examStates) {
				query += (" state !=  ? or");
				params.add(state);
			}
			query = query.substring(0, query.length() - 2);
			query += ")";
		}
		// System.out.println(query);

		exams = find(query, params.toArray()).fetch();
		return exams;
	}

	@Override
	public String toString() {
		return examKey;
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
		endingDate = Calendar.getInstance();
		state = ExamState.FINISHED;
		this.calculateFinalResult();
	}

	public static Exam findForUser(User user) {
		Exam exam = find("byCandidateAndState", user, ExamState.PAID).first();
		return exam;
	}

}
