package mobileMedical.Contacts.Interface;

import java.util.List;

import mobileMedical.Contacts.Model.Group;


public interface IGroup {
	public void insert(Group group);

	public void delete(int groupId);

	public void update(Group group);

	public List<Group> getGroupsByCondition(String condition);

}
