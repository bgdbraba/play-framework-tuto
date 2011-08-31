package models;

import java.util.Date;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Exam extends Model {

	@ManyToOne
	public User candidate;

	@ManyToOne
	public Quiz quiz;

	public Date creationDate;
	public Date startingDate;
	public String examKey;
	public int currentQuestion;
	public boolean validated;

	public Exam() {
		super();
		creationDate = new Date();
		currentQuestion = -1;
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
			startingDate = new Date();
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
}
