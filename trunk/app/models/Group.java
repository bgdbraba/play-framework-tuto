package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Group extends Model {
	
	public String name;

}
