package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Exam extends Model {

	@ManyToOne
	public User candidate;

	@ManyToOne
	public Quiz quiz;

	public Calendar creationDate;
	public Calendar startingDate;
	public Calendar endingDate;
	public String examKey;
	public int currentQuestion;
	public boolean validated;

	@OneToMany(cascade = CascadeType.PERSIST, targetEntity = Answer.class)
	public List<Answer> answers;

	public Exam() {
		super();
		creationDate = Calendar.getInstance();
		currentQuestion = -1;
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

	public Exam(String examKey) {
		this();
		this.examKey = examKey;
	}

	public static Exam findByKey(String examKey) {
		return find("byExamKey", examKey).first();
	}

	public void nextQuestion() {
		if (startingDate == null) {
			startingDate = Calendar.getInstance();
			endingDate = startingDate;
			endingDate.add(Calendar.SECOND, quiz.second);
			currentQuestion = 0;
		}
		// currentQuestion++;
		this.save();
	}

	public void validate() {
		this.validated = true;
	}

	public static String generateFreeExamKey() {
		String examKey = new Random().nextInt(10000) + "";
		if (findByKey(examKey) != null) {
			return generateFreeExamKey();
		}
		return examKey;
	}

	public boolean isFinished() {
		return endingDate.before(Calendar.getInstance());
	}

	public void storeQuiz(Quiz quiz) {
		this.quiz = quiz;
		initAnswers();
	}

}
