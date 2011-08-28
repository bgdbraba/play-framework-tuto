package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Response extends Model{

	public String value;
	
	public boolean correct;
}
