package models;

import java.util.ArrayList;
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

	public static List<GroupType> findByIds(Long[] groupTypeIds) {
		if (groupTypeIds == null || groupTypeIds.length == 0) {
			return new ArrayList<GroupType>();
		} else {
			String ids = "";
			for (Long g : groupTypeIds) {
				ids += ("\'" + g + "\', ");
			}
			ids = ids.substring(0, ids.length() - 2);
			return GroupType.find("from GroupType groupType where groupType.id IN (" + ids + ")").fetch();
		}
	}
}
