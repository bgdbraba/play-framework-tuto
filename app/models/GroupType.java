package models;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;

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

	@Override
	public String toString() {
		return name;
	}

	public static List<GroupType> findByIds(Collection<Long> groupTypeIds) {
		List<GroupType> types = GroupType.find("id in ?", groupTypeIds).fetch();
		return types;
	}
}
