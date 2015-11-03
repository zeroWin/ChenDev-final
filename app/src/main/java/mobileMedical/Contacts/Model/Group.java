package mobileMedical.Contacts.Model;

public class Group {
	private int groupId;// 分组ID
	private String groupName;// 分组名称

	/**
	 * 无参构造函数
	 */
	public Group() {

	}

	/**
	 * 有参构造函数
	 * 
	 * @param groupName
	 */
	public Group(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * 有参构造函数(+1)
	 * 
	 * @param groupId
	 *            分组ID号
	 * @param groupName
	 *            分组名称
	 */
	public Group(int groupId, String groupName) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "Group [groupId=" + groupId + ", groupName=" + groupName + "]";
	}

}
