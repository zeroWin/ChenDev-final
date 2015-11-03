package mobileMedical.Contacts.Manager;

import android.content.Context;

import java.util.List;

import mobileMedical.Contacts.DataBase.DB.TABLES.IM.FIELDS;
import mobileMedical.Contacts.Interface.IIM;
import mobileMedical.Contacts.Model.IM;
import mobileMedical.Contacts.Service.IMService;

public class IMManager {
	private IIM dao;

	public IMManager(Context context) {
		dao = new IMService(context);
	}

	/**
	 * 添加一个即时信息
	 * 
	 * @param im
	 *            信息实体
	 */
	public void addIM(IM im) {
		dao.insert(im);
	}

	/**
	 * 添加一个即时信息(+1)
	 * 
	 * @param id
	 *            联系人ID
	 * @param imName
	 *            信息名称
	 * @param imAcount
	 *            信息账号
	 */
	public void addIM(int id, String imName, String imAcount) {
		IM im = new IM(id, imName, imAcount);
		this.addIM(im);
	}

	/**
	 * 修改一个即时信息
	 * 
	 * @param im
	 *            信息实体
	 */
	public void modifyIM(IM im) {
		dao.update(im);
	}

	/**
	 * 修改一个即时信息(+1)
	 * 
	 * @param imId
	 *            要修改信息的主键ID号
	 * @param id
	 *            新的联系人ID
	 * @param imName
	 *            新的信息名称
	 * @param imAcount
	 *            新的信息账号
	 */
	public void modifyIM(int imId, int id, String imName, String imAcount) {
		IM im = this.getIMById(imId);
		im.setId(id);
		im.setImName(imName);
		im.setImAcount(imAcount);

		this.modifyIM(im);
	}

	/**
	 * 通过信息主键号删除一个信息
	 * 
	 * @param imId
	 *            信息主键号
	 */
	public void delIMById(int imId) {
		dao.delete(imId);
	}

	/**
	 * 难过联系人ID号删除信息
	 * 
	 * @param contactId
	 *            联系人ID号
	 */
	public void delIMByContactId(int contactId) {
		List<IM> list = this.getIMsByContactId(contactId);
		for (IM im : list)
			this.delIMById(im.getImId());
	}

	/**
	 * 通过信息主键号查找信息
	 * 
	 * @param imId
	 *            信息主键号
	 * @return 信息实体
	 */
	public IM getIMById(int imId) {
		String condition = FIELDS.IMID + " = " + imId;
		List<IM> list = dao.getIMsByCondition(condition);
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	/**
	 * 通过联系人ID号查找信息
	 * 
	 * @param contactId
	 *            联系人ID号
	 * @return 信息实体集
	 */
	public List<IM> getIMsByContactId(int contactId) {
		String condition = FIELDS.ID + " = " + contactId;
		List<IM> list = dao.getIMsByCondition(condition);
		return list;
	}
}
