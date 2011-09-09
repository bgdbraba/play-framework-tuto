package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Answer extends Model {

	@ManyToOne
	public Question question;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = AnswerValues.class)
	public List<AnswerValues> answerValues;

	@Entity
	protected class AnswerValues extends Model {

		public String value;
	}

}
