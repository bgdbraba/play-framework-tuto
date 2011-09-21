package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Answer.AnswerValue;

import play.db.jpa.Model;

@Entity
public class Answer extends Model {

	@ManyToOne
	public Question question;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = AnswerValue.class)
	public List<AnswerValue> answerValues;

	public void addAnswerValue(String value) {
		AnswerValue answerValue = new AnswerValue(value);
		answerValues.add(answerValue);
	}

	public void addAnswerValue(Integer response) {
		addAnswerValue(response.toString());
	}

	public void addAnswerValue(Boolean... responses) {
		for (int i=0;i<responses.length;i++) {
			if (responses[i] != null && responses[i]) {
				addAnswerValue(i+1);
			}
		}
	}

	@Entity
	public class AnswerValue extends Model {

		public AnswerValue(String value) {
			this.value = value;
		}

		public String value;
	}
	
}
