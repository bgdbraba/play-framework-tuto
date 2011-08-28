package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Exam extends Model {

	public User candidate;
	public Questionnaire questionnaire;
	public Date creationDate;
	public String examKey;
	public int currentQuestion;
	
	public static Exam findByKey(String examKey){
		return find("byExamKey", examKey).first();
	}
}
