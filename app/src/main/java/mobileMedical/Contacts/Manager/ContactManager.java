package mobileMedical.Contacts.Manager;

import android.content.Context;

import java.util.List;

import mobileMedical.Contacts.DataBase.DB;
import mobileMedical.Contacts.DataBase.DB.TABLES.CONTACT.FIELDS;
import mobileMedical.Contacts.Interface.IContact;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Service.ContactService;

public class ContactManager {

	private IContact dao;

	public ContactManager(Context context) {
		dao = new ContactService(context);
	}

	/**
	 * 添加一个联系人
	 * 
	 * @param contact
	 *            联系人实体
	 */
	public void addContact(Contact contact) {
		dao.insert(contact);
	}

	/**
	 * 添加一个联系人(+1)
	 * 
	 * @param name
	 *            联系人姓名
	 * @param nickName
	 *            联系人昵称
	 * @param address
	 *            联系人住址
	 * @param company
	 *            联系人所在公司
	 * @param birthday
	 *            联系人生日
	 * @param note
	 *            联系人备注
	 * @param image
	 *            联系人头像
	 * @param groupId
	 *            联系人所以组号
	 */
	public void addContact(String name, String namePinyin, String nickName,
			String address, String company, String birthday, String note,
			byte[] image, int groupId, String idNo, String describe,
			String hospital, String type, String fileNo, String gender,
			String relationName, String relation, String relationTele,
			String relationEmail, int doctorID) {
		Contact contact = new Contact(name, namePinyin, nickName, address,
				company, birthday, note, image, groupId, idNo, describe,
				hospital, type, fileNo, gender, relationName, relation,
				relationTele, relationEmail, doctorID);
		this.addContact(contact);
	}

	/**
	 * 修改联系人
	 * 
	 * @param contact
	 *            联系人实体
	 */
	public void modifyContact(Contact contact) {
		dao.update(contact);
	}

	/**
	 * 修改联系人(+1)
	 * 
	 * @param id
	 *            联系人主键ID号
	 * @param name
	 *            联系人姓名
	 * @param nickName
	 *            联系人昵称
	 * @param address
	 *            联系人住址
	 * @param company
	 *            联系人所在公司
	 * @param birthday
	 *            联系人生日
	 * @param note
	 *            联系人备注
	 * @param image
	 *            联系人头像
	 * @param groupId
	 *            联系人所以组号
	 */
	public void modifyContact(int id, String name, String namePinyin,
			String nickName, String address, String company, String birthday,
			String note, byte[] image, int groupId, String idNo,
			String describe, String hospital, String type, String fileNo,
			String gender, String relationName, String relation,
			String relationTele, String relationEmail, int doctorID) {
		Contact contact = this.getContactById(id);
		contact.setName(name);
		contact.setNamePinyin(namePinyin);
		contact.setNickName(nickName);
		contact.setAddress(address);
		contact.setCompany(company);
		contact.setBirthday(birthday);
		contact.setNote(note);
		contact.setImage(image);
		contact.setGroupId(groupId);
		contact.setIdNo(idNo);
		contact.setDescribe(describe);
		contact.setHospital(hospital);
		contact.setType(type);
		contact.setFileNo(fileNo);
		contact.setGender(gender);
		contact.setRelationName(relationName);
		contact.setRelation(relation);
		contact.setRelationTele(relationTele);
		contact.setRelationEmail(relationEmail);
		contact.setDoctorID(doctorID);

		this.modifyContact(contact);
	}

	/**
	 * 通过主键ID删除联系人
	 * 
	 * @param id
	 *            主键ID
	 */
	public void delContact(int id) {
		dao.delete(id);
	}

	/**
	 * 通过分组号groupId删除联系人
	 * 
	 * @param groupId
	 *            分组号
	 */
	public void delContactsByGroupId(int groupId) {
		List<Contact> contacts = this.getContactsByGroupId(groupId);
		for (Contact contact : contacts)
			this.delContact(contact.getId());
	}

	/**
	 * 删除全部联系人
	 */
	public void delAllContacts() {
		dao.deleteAll();
	}

	/**
	 * 通过主键ID查找联系人
	 * 
	 * @param id
	 *            主键ID
	 * @return 联系人实体
	 */
	public Contact getContactById(int id) {
		String condition = DB.TABLES.CONTACT.FIELDS.ID + " = " + id;
		List<Contact> contacts = dao.getContactsByCondition(condition);
		if (contacts.size() > 0)
			return contacts.get(0);
		else
			return null;
	}
	
	public Contact getContactByIdNo(String idNo) {
		String condition = DB.TABLES.CONTACT.FIELDS.IDNO + " = " + idNo;
		List<Contact> contacts = dao.getContactsByCondition(condition);
		if (contacts.size() > 0)
			return contacts.get(0);
		else
			return null;
	}

	/**
	 * 通过联系人姓名name查找联系人
	 * 
	 * @param contactName
	 *            联系人姓名
	 * @return 联系人实体集
	 */
	public List<Contact> getContactsByName(String contactName) {
		String condition = FIELDS.NAME + " like '%" + contactName + "%' ";
		return dao.getContactsByCondition(condition);
	}

	/**
	 * 通过联系人姓名namePinyin查找联系人
	 * 
	 * @param contactNamePinyin
	 *            联系人姓名拼音
	 * @return 联系人实体集
	 */
	public List<Contact> getContactsByNamePinyin(String contactNamePinyin) {
		String condition = FIELDS.NAMEPINYIN + " like '" + contactNamePinyin
				+ "%' ";
		return dao.getContactsByCondition(condition);
	}

	/**
	 * 通过联系人姓名name查找特定分组的联系人
	 * 
	 * @param contactName
	 *            联系人姓名
	 * @param groupId
	 *            分组ID号
	 * @return 联系人实体集
	 */
	public List<Contact> getContactsByNameAddGroupId(String contactName,
			int groupId) {
		String condition = FIELDS.NAME + " like '%" + contactName + "%' and "
				+ FIELDS.GROUPID + " = " + groupId;
		return dao.getContactsByCondition(condition);
	}

	/**
	 * 通过联系人姓名namePinyin查找特定分组的联系人
	 * 
	 * @param contactNamePinyin
	 *            联系人姓名拼音
	 * @param groupId
	 *            分组ID号
	 * @return 联系人实体集
	 */
	public List<Contact> getContactsByNamePinyinAddGroupId(
			String contactNamePinyin, int groupId) {
		String condition = FIELDS.NAMEPINYIN + " like '" + contactNamePinyin
				+ "%' and " + FIELDS.GROUPID + " = " + groupId;
		return dao.getContactsByCondition(condition);
	}

	/**
	 * 通过分组号groupId查找联系人
	 * 
	 * @param groupId
	 *            分组号
	 * @return 联系人实体集
	 */
	public List<Contact> getContactsByGroupId(int groupId) {
		String condition = FIELDS.GROUPID + " = " + groupId;
		return dao.getContactsByCondition(condition);
	}

	/**
	 * 查找所有联系人
	 * 
	 * @return 所有联系人实体集
	 */
	public List<Contact> getAllContacts() {
		return dao.getContactsByCondition("1=1");
	}

	/**
	 * 难过联系人ID和新的分组号toGroupId改变分组
	 * 
	 * @param contactId
	 *            要改变分组的联系人ID号
	 * @param toGroupId
	 *            要移动到新分组的分组ID号
	 */
	public void changeGroupByContact(int contactId, int toGroupId) {
		String sql = String.format(DB.TABLES.CONTACT.SQL.CHANGEGROUP,
				toGroupId, DB.TABLES.CONTACT.FIELDS.ID + "=" + contactId);
		dao.changeGroup(sql);
	}
}
