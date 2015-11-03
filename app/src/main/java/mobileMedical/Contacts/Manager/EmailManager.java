package mobileMedical.Contacts.Manager;

import android.content.Context;

import java.util.List;

import mobileMedical.Contacts.DataBase.DB.TABLES.EMAIL.FIELDS;
import mobileMedical.Contacts.Interface.IEmail;
import mobileMedical.Contacts.Model.Email;
import mobileMedical.Contacts.Service.EmailService;

public class EmailManager {
	private IEmail dao;

	public EmailManager(Context context) {
		dao = new EmailService(context);
	}

	/**
	 * 添加邮箱
	 * 
	 * @param email
	 *            邮箱实体
	 */
	public void addEmail(Email email) {
		dao.insert(email);
	}

	/**
	 * 添加邮箱(+1)
	 * 
	 * @param id
	 *            联系人ID
	 * @param emailName
	 *            邮箱类型名称
	 * @param emailAcount
	 *            邮箱账号
	 */
	public void addEmail(int id, String emailName, String emailAcount) {
		Email email = new Email(id, emailName, emailAcount);
		this.addEmail(email);
	}

	/**
	 * 修改邮箱
	 * 
	 * @param email
	 *            邮箱实体
	 */
	public void modifyEmail(Email email) {
		dao.update(email);
	}

	/**
	 * 修改邮箱(+1)
	 * 
	 * @param emailId
	 *            要修改的邮箱ID号
	 * @param id
	 *            新的联系人ID
	 * @param emailName
	 *            新的邮箱类型名称
	 * @param emailAcount
	 *            新的邮箱账号
	 */
	public void modifyEmail(int emailId, int id, String emailName,
			String emailAcount) {
		Email email = this.getEmailById(emailId);
		email.setId(id);
		email.setEmailName(emailName);
		email.setEmailAcount(emailAcount);

		this.modifyEmail(email);
	}

	/**
	 * 根据邮箱主键ID删除邮箱
	 * 
	 * @param emailId
	 *            邮箱主键号
	 */
	public void delEmailById(int emailId) {
		dao.delete(emailId);
	}

	/**
	 * 根据联系人ID号删除邮箱
	 * 
	 * @param contactId
	 *            联系人ID号
	 */
	public void delEmailByContactId(int contactId) {
		List<Email> emails = this.getEmailsByContactId(contactId);
		for (Email email : emails)
			this.delEmailById(email.getEmailId());
	}

	/**
	 * 根据邮箱主键ID号查找邮箱
	 * 
	 * @param emailId
	 *            邮箱主键号
	 * @return 邮箱实体
	 */
	public Email getEmailById(int emailId) {
		String condition = FIELDS.EMAILID + "=" + emailId;
		List<Email> emails = dao.getEmailsByCondition(condition);
		if (emails.size() > 0)
			return emails.get(0);
		else
			return null;
	}

	/**
	 * 根据联系人ID号查找邮箱
	 * 
	 * @param contactId
	 *            联系人ID号
	 * @return 邮箱实体集
	 */
	public List<Email> getEmailsByContactId(int contactId) {
		String condition = FIELDS.ID + "=" + contactId;
		List<Email> emails = dao.getEmailsByCondition(condition);
		return emails;
	}
}
