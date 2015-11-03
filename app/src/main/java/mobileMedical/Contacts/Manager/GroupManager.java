package mobileMedical.Contacts.Manager;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.DataBase.DB.TABLES.GROUP.FIELDS;
import mobileMedical.Contacts.Interface.IGroup;
import mobileMedical.Contacts.Model.Group;
import mobileMedical.Contacts.Service.GroupService;

public class GroupManager {
	private IGroup dao;

	public GroupManager(Context context) {
		dao = new GroupService(context);
	}

	/**
	 * 添加一个分组
	 * 
	 * @param group
	 *            分组实体
	 */
	public void addGroup(Group group) {
		dao.insert(group);
	}

	/**
	 * 添加一个分组(+1)
	 * 
	 * @param groupName
	 *            分组名称
	 */
	public void addGroup(String groupName) {
		Group group = new Group(groupName);
		this.addGroup(group);
	}

	/**
	 * 分组重命名
	 * 
	 * @param group
	 *            分组实体
	 */
	public void modifyGroup(Group group) {
		dao.update(group);
	}

	/**
	 * 分组重命名(+1)
	 * 
	 * @param groupId
	 *            要修改的分组主键ID号
	 * @param groupName
	 *            分组新名称
	 */
	public void modifyGroup(int groupId, String groupName) {
		Group group = this.getGroupById(groupId);
		group.setGroupName(groupName);
		this.modifyGroup(group);
	}

	/**
	 * 根据分组的主健ID号删除分组
	 * 
	 * @param groupId
	 *            分组主键号
	 */
	public void delGroupById(int groupId) {
		dao.delete(groupId);
	}

	/**
	 * 根据分组主键ID号查找分组
	 * 
	 * @param groupId
	 *            分组主键号
	 * @return 分组实体
	 */
	public Group getGroupById(int groupId) {
		String condition = FIELDS.GROUPID + " = " + groupId;
		List<Group> groups = dao.getGroupsByCondition(condition);
		if (groups.size() > 0)
			return groups.get(0);
		else
			return null;
	}

	/**
	 * 根据分组名称查找分组
	 * 
	 * @param groupName
	 *            分组名称
	 * @return 分组实体集
	 */
	public Group getGroupsByName(String groupName) {
		String condition = FIELDS.GROUPNAME + " = " + "'" + groupName + "'";
		Group group = dao.getGroupsByCondition(condition).get(0);
		return group;
	}

	/**
	 * 查找所有分组
	 * 
	 * @return 所有分组的名称集合
	 */
	public List<String> getAllGroupName() {
		String condition = "1=1";
		List<Group> groups = dao.getGroupsByCondition(condition);
		List<String> groupNames = new ArrayList<String>();
		for (Group group : groups)
			groupNames.add(group.getGroupName());
		return groupNames;
	}

	/**
	 * 查找所有分组
	 * 
	 * @return 所有分组实体集
	 */
	public List<Group> getAllGroups() {
		String condition = "1=1";
		List<Group> groups = dao.getGroupsByCondition(condition);
		return groups;
	}
}
