package models;

import java.util.*;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Question extends Model {

	public String title;
	public String content;
	public String explanation;
	public int difficulty;
	public int second;
	public Group group;
	
	@OneToMany(cascade = CascadeType.ALL, targetEntity=Response.class)
	public List<Response> responses;
	
	@Enumerated
	public QuestionType questionType;

}