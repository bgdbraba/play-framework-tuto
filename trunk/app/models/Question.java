package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Question extends Model {

	public String title;
	public String content;
	public String explanation;
	public int difficulty;
	public int second;

	@ManyToOne
	public GroupType groupType;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = Response.class)
	public List<Response> responses;

	public static enum QuestionType {

		MULTIPLE_CHOICE, SIMPLE_CHOICE, TEXT_ANSWER;

	}

	@Enumerated
	public QuestionType questionType;

	public Question(String title, String content, String explanation,
			int difficulty, int second, GroupType groupType,
			List<Response> responses, QuestionType questionType) {
		this(title, content, explanation, difficulty, second, groupType,
				questionType);
		this.responses = responses;
	}

	public Question(String title, String content, String explanation,
			int difficulty, int second, GroupType groupType,
			QuestionType questionType) {
		super();
		this.title = title;
		this.content = content;
		this.explanation = explanation;
		this.difficulty = difficulty;
		this.second = second;
		this.groupType = groupType;
		this.questionType = questionType;
	}

	public void addResponse(String value, boolean correct) {
		if (responses == null) {
			responses = new ArrayList<Response>();
		}
		if (value != null && value.length() > 0) {
			responses.add(new Response(value, correct));
		}
	}

	public static List<Question> search(String title, Integer difficulty,
			Integer second, Long[] groupTypes) {

		List<Question> questions = null;
		if ((title == null || title.trim().length() == 0) && difficulty == null
				&& second == null
				&& (groupTypes == null || groupTypes.length == 0)) {
			questions = new ArrayList<Question>();
		} else {
			String query = "";
			List<Object> params = new ArrayList<Object>();
			if (title != null && title.trim().length() > 0) {
				query = "title = ? ";
				params.add(title);
			}
			if (difficulty != null) {
				query += (query.length() > 0 ? "and " : "")
						+ "difficulty >= ? ";
				params.add(difficulty);
			}
			if (second != null) {
				query += (query.length() > 0 ? "and " : "") + "time >= ? ";
				params.add(second);
			}
			if (groupTypes != null && groupTypes.length > 0) {

				List<GroupType> groups = GroupType.findByIds(groupTypes);
				query += (query.length() > 0 ? "and " : "") + "groupType in (?) ";
				params.add(groups.toArray());
			}
			questions = find(query, params.toArray()).fetch();

		}
		return questions;
	}
}