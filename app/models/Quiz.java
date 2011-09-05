package models;

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

	public int difficulty;

	public String description;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = GroupType.class)
	public List<GroupType> groupTypes;

	@ManyToMany(cascade = CascadeType.ALL, targetEntity = Question.class)
	public List<Question> questions;

	@Transient
	public int second = 300;

}
