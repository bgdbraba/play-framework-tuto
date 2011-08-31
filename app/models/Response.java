package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Response extends Model { 

	public Response(String value, boolean correct) {
		super();
		this.value = value;
		this.correct = correct;
	}

	public String value;

	public boolean correct;
}
