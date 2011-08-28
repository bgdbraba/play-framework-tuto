package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Questionnaire extends Model {

	public String title;
	public String description;
	
	@OneToMany(cascade = CascadeType.ALL, targetEntity=Group.class)
	public List<Group> groups;

	@OneToMany(cascade = CascadeType.ALL, targetEntity=Question.class)
	public List<Question> questions;
	
}
