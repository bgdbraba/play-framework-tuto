package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import play.db.jpa.Model;

@Entity
public class Quiz extends Model {

	public String title;

	public float difficulty;

	public String description;

	@ManyToMany(cascade = CascadeType.ALL, targetEntity = GroupType.class)
	public List<GroupType> groupTypes;

	@ManyToMany(cascade = CascadeType.ALL, targetEntity = Question.class)
	public List<Question> questions;

	public int second;

	public boolean valid;

	public Quiz(String title, String description, List<GroupType> groupTypes) {
		this();
		this.title = title;
		this.description = description;
		this.groupTypes = groupTypes;
	}

	public Quiz() {
		super();
	}

	@Override
	public String toString() {

		if (groupTypes == null || groupTypes.size() == 0) {
			return title;
		}
		String types = "";
		for (GroupType type : groupTypes) {
			types += type + ", ";
		}

		return title + "(" + types.substring(0, types.length() - 2) + ")";
	}

	public static List<Quiz> search(String title, Integer difficulty,
			Integer second, Long groupTypeId, Integer questions) {

		List<Quiz> quizzes = null;

		if ((title == null || title.trim().length() == 0) && difficulty == null
				&& second == null && groupTypeId == null && questions == null) {
			quizzes = new ArrayList<Quiz>();
		} else {
			String query = "SELECT distinct quiz FROM Quiz quiz JOIN quiz.groupTypes groupType WHERE quiz.id > 0 ";
			List<Object> params = new ArrayList<Object>();
			if (title != null && title.trim().length() > 0) {
				query += "and title = ? ";
				params.add(title);
			}
			if (difficulty != null) {
				query += "and difficulty >= ? ";
				params.add(difficulty);
			}
			if (second != null && second > 0) {
				query += "and second >= ? ";
				params.add(second);
			}
			if (questions != null && questions > 0) {
				query += "and size(quiz.questions) >= ? ";
				params.add(questions);
			}
			if (groupTypeId != null) {
				query += "and groupType.id = ? ";
				params.add(groupTypeId);
			}
			System.out.println(query);
			find(query, params.toArray());
			quizzes = find(query, params.toArray()).fetch();
		}

		return quizzes;
	}

	public void validate() {
		int diff = 0;
		int time = 0;
		for (Question q : questions) {
			System.out.println(q.difficulty);
			diff+=q.difficulty;
			time+=q.second;
		}
		
		//FIXME
		this.difficulty=new Float(Math.round((new Double(diff)/questions.size())*5))/5;
		System.out.println(this.difficulty);
		this.second=time;
		this.valid = true;
		this.save();
	}
}
