package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Question extends Model {

	public String title;
	public String content;
	public String explanation;
	public int difficulty;
	public int second;
	public GroupType groupType;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = Response.class)
	public List<Response> responses;

	public static enum QuestionType {

		MULTIPLE_CHOICE, SIMPLE_CHOICE, TEXT_ANSWER;
	}
	
	@Enumerated
	public QuestionType questionType;

}