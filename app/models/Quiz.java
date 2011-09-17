package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Quiz extends Model {

	public String title;

	public int difficulty;

	public String description;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = GroupType.class)
	public List<GroupType> groupTypes;

	@ManyToMany(cascade = CascadeType.ALL, targetEntity = Question.class)
	public List<Question> questions;

	public int second;

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

	public static List<Quiz> search(String title, Integer difficulty, Integer second, Long groupTypeId,
			Integer questions) {

		System.out.println("#" + title);
		System.out.println("#" + difficulty);
		System.out.println("#" + second);
		System.out.println("#" + groupTypeId);
		System.out.println("#" + questions);

		List<Quiz> quizzes = null;

		if ((title == null || title.trim().length() == 0) && difficulty == null && second == null
				&& groupTypeId == null && questions == null) {
			quizzes = new ArrayList<Quiz>();
		} else {
			String query = "SELECT quiz FROM Quiz quiz JOIN quiz.groupTypes groupType WHERE quiz.id != null ";
			List<Object> params = new ArrayList<Object>();
			if (title != null && title.trim().length() > 0) {
				query = "title = ? ";
				params.add(title);
			}
			if (difficulty != null) {
				query += (query.length() > 0 ? "and " : "") + "difficulty >= ? ";
				params.add(difficulty);
			}
			if (second != null && second > 0) {
				query += (query.length() > 0 ? "and " : "") + "second >= ? ";
				params.add(second);
			}
			if (questions != null && questions > 0) {
				query += (query.length() > 0 ? "and " : "") + "questions >= ? ";
				params.add(questions);
			}
			if (groupTypeId != null) {
				query += (query.length() > 0 ? "and " : "") + "groupTypes.id = ? ";
				params.add(groupTypeId);
			}
			query = " and groupType.id > 0";
			System.out.println(query);
			quizzes = find(query).fetch();
		}

		return quizzes;
	}
}
