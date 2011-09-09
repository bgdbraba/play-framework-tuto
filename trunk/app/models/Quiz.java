package models;

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
}
