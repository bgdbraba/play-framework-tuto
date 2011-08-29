package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Questionnaire extends Model {

	public String title;
	
	public String description;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = GroupType.class)
	public List<GroupType> groupTypes;

	@ManyToMany(cascade = CascadeType.ALL, targetEntity = Question.class)
	public List<Question> questions;

}
