package mobileMedical.Contacts.Manager;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.DataBase.DB.TABLES.TEL.FIELDS;
import mobileMedical.Contacts.Interface.ITel;
import mobileMedical.Contacts.Model.Tel;
import mobileMedical.Contacts.Service.TelService;

public class TelManager {
	private ITel dao;

	public TelManager(Context context) {
		dao = new TelService(context);
	}

	/**
	 * 添加一个电话号码
	 * 
	 * @param tel
	 *            电话实体
	 */
	public void addTel(Tel tel) {
		dao.insert(tel);
	}

	/**
	 * 添加一个电话号码(+1)
	 * 
	 * @param id
	 *            联系人ID号
	 * @param telName
	 *            电话类型名称
	 * @param telNumber
	 *            电话号码
	 */
	public void addTel(int id, String telName, String telNumber) {
		Tel tel = new Tel(id, telName, telNumber);
		this.addTel(tel);
	}

	/**
	 * 修改电话号码
	 * 
	 * @param tel
	 *            电话实体
	 */
	public void modifyTel(Tel tel) {
		dao.update(tel);
	}

	/**
	 * 修改电话号码(+1)
	 * 
	 * @param telId
	 *            要修改电话的主键号
	 * @param id
	 *            新的联系人ID号
	 * @param telName
	 *            新的电话类型名称
	 * @param telNumber
	 *            新的电话号码
	 */
	public void modifyTel(int telId, int id, String telName, String telNumber) {
		Tel tel = this.getTelById(telId);
		tel.setId(id);
		tel.setTelName(telName);
		tel.setTelNumber(telNumber);

		this.modifyTel(tel);
	}

	/**
	 * 根据主键删除电话号码
	 * 
	 * @param telId
	 *            电话主键号
	 */
	public void delTelById(int telId) {
		dao.delete(telId);
	}

	/**
	 * 根据联系ID号删除电话号码
	 * 
	 * @param contactId
	 *            联系人ID号
	 */
	public void delTelByContactId(int contactId) {
		List<Tel> list = this.getTelsByContactId(contactId);
		for (Tel tel : list)
			this.delTelById(tel.getTelId());
	}

	/**
	 * 根据主键号查找电话号码
	 * 
	 * @param telId
	 *            电话主键号
	 * @return 电话实体
	 */
	public Tel getTelById(int telId) {
		String condition = FIELDS.TELID + "=" + telId;
		List<Tel> list = dao.getTelsByCondition(condition);
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	/**
	 * 根据联系人ID号查找电话号码
	 * 
	 * @param contactId
	 *            联系人ID号
	 * @return 电话号码集
	 */
	public List<String> getTelNumbersByContactId(int contactId) {
		List<Tel> list = this.getTelsByContactId(contactId);
		List<String> telNumbers = new ArrayList<String>();
		for (Tel tel : list)
			telNumbers.add(tel.getTelNumber());
		return telNumbers;
	}

	/**
	 * 根据联系人ID号查找电话
	 * 
	 * @param contactId
	 *            联系人ID号
	 * @return 电话实体集
	 */
	public List<Tel> getTelsByContactId(int contactId) {
		String condition = FIELDS.ID + "=" + contactId;
		List<Tel> list = dao.getTelsByCondition(condition);
		return list;
	}

	/**
	 * 根据电话号码查找联系人ID
	 * 
	 * @param telNumber
	 *            电话号码
	 * @return 联系人ID集合
	 */
	public List<Integer> getContactIdByTelNumber(String telNumber) {
		List<Integer> contactIds = new ArrayList<Integer>();
		String condition = FIELDS.TELNUMBER + "='" + telNumber + "'";
		List<Tel> list = dao.getTelsByCondition(condition);
		for (Tel tel : list)
			contactIds.add(tel.getId());
		return contactIds;
	}
}
