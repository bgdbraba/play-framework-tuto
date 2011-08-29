package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Exam extends Model {

	@ManyToOne
	public User candidate;

	@ManyToOne
	public Questionnaire questionnaire;

	public Date creationDate;
	public Date startingDate;
	public String examKey;
	public int currentQuestion;

	public Exam() {
		creationDate = new Date();
		currentQuestion = -1;
	}

	public static Exam findByKey(String examKey) {
		return find("byExamKey", examKey).first();
	}

	public void nextQuestion() {
		if (startingDate == null) {
			startingDate = new Date();
		}
		//currentQuestion++;
		this.save();
	}
}
