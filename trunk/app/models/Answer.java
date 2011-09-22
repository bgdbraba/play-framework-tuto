package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.Answer.AnswerValue;
import models.Question.QuestionType;

import play.db.jpa.Model;
import utils.StringUtils;

@Entity
public class Answer extends Model {

	@ManyToOne
	public Question question;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = AnswerValue.class)
	public List<AnswerValue> answerValues;

	@Entity
	public class AnswerValue extends Model {

		public AnswerValue(String value) {
			this.value = value;
		}

		public String value;
	}

	public void addAnswerValue(String value) {
		AnswerValue answerValue = new AnswerValue(value);
		answerValues.add(answerValue);
	}

	public void addAnswerValue(Integer response) {
		addAnswerValue(response.toString());
	}

	public void addAnswerValue(Boolean... responses) {
		for (int i = 0; i < responses.length; i++) {
			if (responses[i] != null && responses[i]) {
				addAnswerValue(i + 1);
			}
		}
	}

	public boolean isCorrect() {// return true;
		if (question.questionType.equals(QuestionType.SIMPLE_CHOICE)) {

			List<Response> responses = question.responses;
			AnswerValue answer = answerValues.get(0);
			for (int i = 0; i < responses.size(); i++) {

				// System.out.println((i + 1) + " : " + responses.get(i).value
				// + " " + responses.get(i).correct + " >>> "
				// + answer.value.equals("" + (i + 1)));

				// matched
				if (responses.get(i).correct
						&& answer.value.equals("" + (i + 1))) {
					return true;
				}

			}

			return false;

		} else if (question.questionType.equals(QuestionType.MULTIPLE_CHOICE)) {

			// Browse the response
			for (int i = 0; i < question.responses.size(); i++) {
				Response response = question.responses.get(i);
				boolean okResponse = false;

				// browse the given answer
				for (AnswerValue answerValue : answerValues) {

					// System.out.println((i + 1) + " : " + response.value
					// + " " + response.correct + " >>> "
					// + answerValue.value + " " + answerValue.value.equals("" +
					// (i + 1)));

					// matched
					if (response.correct
							&& answerValue.value.equals("" + (i + 1))) {
						okResponse = true;
						break;
					}

					// error
					if (!response.correct
							&& answerValue.value.equals("" + (i + 1))) {
						return false;
					}

				}

				if (!okResponse && response.correct) {
					return false;
				}
			}

			return true;

		} else {

			// the only answer
			String answer = answerValues.get(0).value;

			// Browse the response
			for (Response response : question.responses) {

				// matched
				if (StringUtils.compareOnlyText(answer, response.value)) {
					return true;
				}

			}

			return false;

		}
	}

}
