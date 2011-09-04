package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.JPABase;
import play.db.jpa.Model;

@Entity
public class GroupType extends Model { 

	public String name;

	public static List<GroupType> findAll(boolean withNullValue) {
		List<GroupType> response = findAll();

		if (withNullValue) {
			GroupType nullType = new GroupType();
			nullType.name = "- All -";
			response.add(0, nullType);
		}
		
		return response;
	}
}
