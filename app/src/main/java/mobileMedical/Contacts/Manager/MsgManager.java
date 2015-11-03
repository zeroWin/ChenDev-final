package mobileMedical.Contacts.Manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobileMedical.Contacts.Model.Msg;
import mobileMedical.Contacts.Service.MsgService;
import mobileMedical.Contacts.Tool.CommonUtil;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;

public class MsgManager {
	private ContentResolver contentResolver;
	private TelManager telMgr;
	private ContactManager contactMgr;
	private SmsManager smsMag = null;
	private MsgService msgService;

	public static final String SMS_URI_ALL = "content://sms/";// 获取系统所有短信
	public static final String SMS_URI_INBOX = "content://sms/inbox";// 获取系统收件箱短信
	public static final String SMS_URI_SEND = "content://sms/sent";// 获取系统发件箱短信
	public static final String SMS_URI_DRAFT = "content://sms/draft";// 获取系统草稿箱短信

	public MsgManager(Context context) {
		contentResolver = context.getContentResolver();
		telMgr = new TelManager(context);
		contactMgr = new ContactManager(context);
		smsMag = SmsManager.getDefault();
		msgService = new MsgService(context);
	}

	/**
	 * 添加一条信息(+1)
	 * 
	 * @param msg
	 *            要添加的信息实体
	 */
	public void addMsg(String address, String body, int type) {
		ContentValues cv = new ContentValues();
		cv.put("address", address);
		cv.put("body", body);
		cv.put("type", type);
		Uri uri = Uri.parse(SMS_URI_ALL);
		contentResolver.insert(uri, cv);
	}

	/**
	 * 修改信息为已读或未读
	 * 
	 * @param read
	 *            (0:未读;1:已读)
	 * @param threadId
	 */
	public void modifyMsgRead(int read, int threadId) {
		Uri uri = Uri.parse(SMS_URI_ALL);
		ContentValues values = new ContentValues();
		values.put("read", read);
		contentResolver.update(uri, values, "thread_id = ?",
				new String[] { threadId + "" });
	}

	/**
	 * 划分短信
	 * 
	 * @param content
	 * @return
	 */
	public ArrayList<String> divideMessage(String content) {
		return smsMag.divideMessage(content);
	}

	/**
	 * 发送短信
	 * 
	 * @param content
	 * @param sendPI
	 * @param deliveryPI
	 */
	public void sendMessage(String address, String content,
			PendingIntent sendPI, PendingIntent deliveryPI) {
		smsMag.sendTextMessage(address, null, content, sendPI, deliveryPI);
	}

	/**
	 * 通过联系人threadId删除信息
	 * 
	 * @param threadId
	 *            对话序列号
	 */
	public void delMsgByThreadId(int threadId) {
		Uri uri = Uri.parse(SMS_URI_ALL);
		contentResolver.delete(uri, "thread_id = ?", new String[] { threadId
				+ "" });
	}

	/**
	 * 通过信息ID号删除指定信息
	 * 
	 * @param id
	 *            信息的ID号
	 */
	public void delMsgByMsgId(int id) {
		Uri uri = Uri.parse(SMS_URI_ALL);
		contentResolver.delete(uri, "_id = ?", new String[] { id + "" });
	}

	/**
	 * 删除所有信息
	 */
	public void delAllMsg() {
		Uri uri = Uri.parse(SMS_URI_ALL);
		contentResolver.delete(uri, "1 = 1", null);
	}

	/**
	 * 根据对话序号查询所有短信
	 * 
	 * @param threadId
	 *            对话序号
	 * @return
	 */
	public List<Msg> getMsgByThreadId(int threadId) {
		Uri uri = Uri.parse(SMS_URI_ALL);
		String[] projection = new String[] { "_id", "address", "body", "date",
				"type", "read" };
		String selection = "thread_id = " + threadId;
		String sortOrder = " date asc";
		Cursor cursor = contentResolver.query(uri, projection, selection, null,
				sortOrder);

		List<Msg> msgs = new ArrayList<Msg>();
		while (cursor.moveToNext()) {
			Msg msg = new Msg();
			msg.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			msg.setThreadId(threadId);
			msg.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			msg.setContent(cursor.getString(cursor.getColumnIndex("body")));
			msg.setMsgMark(cursor.getInt(cursor.getColumnIndex("type")));
			msg.setRead(cursor.getInt(cursor.getColumnIndex("read")));
			CommonUtil.Log("sqy", "getMsgByThreadId", msg.getRead() + "", 'i');
			// 设置姓名
			String address = cursor.getString(cursor.getColumnIndex("address"));
			List<Integer> list = new ArrayList<Integer>();
			list = telMgr.getContactIdByTelNumber(address);
			if (list.size() > 0)
				msg.setPerson(contactMgr.getContactById(list.get(0)).getName());
			else
				msg.setPerson("");
			// 设置日期
			String date = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			Date d = new Date(Long.parseLong(cursor.getString(cursor
					.getColumnIndex("date"))));
			date = dateFormat.format(d);
			msg.setMsgDate(date);

			msgs.add(msg);
		}
		return msgs;
	}

	/**
	 * 获得threadId的短信数
	 * 
	 * @param threadId
	 * @return
	 */
	public int getMsgCountByThreadId(int threadId) {
		Uri uri = Uri.parse(SMS_URI_ALL);
		int smsCount = 0;
		String[] projection = new String[] { "count(*)" };
		String selection = "thread_id=" + threadId;
		Cursor cursor = contentResolver.query(uri, projection, selection, null,
				null);
		if (cursor.moveToFirst()) {
			smsCount = Integer.valueOf(cursor.getString(0));
		}
		cursor.close();
		return smsCount;
	}

	/**
	 * 根据电话号码返回对话序列号
	 * 
	 * @param telNumber
	 * @return
	 */
	public int getThreadIdByTelNumber(String telNumber) {
		Uri uri = Uri.parse(SMS_URI_ALL);
		int threadId = -1;
		String[] projection = new String[] { "thread_id" };
		String selection = "address='" + telNumber + "'";
		Cursor cursor = contentResolver.query(uri, projection, selection, null,
				null);
		if (cursor.moveToFirst()) {
			threadId = cursor.getInt(cursor.getColumnIndex("thread_id"));
		}
		cursor.close();
		return threadId;
	}

	/**
	 * 获得threadId的未读短信数
	 * 
	 * @param threadId
	 * @return
	 */
	public int getUnreadMsgCountByThreadId(int threadId) {
		Uri uri = Uri.parse(SMS_URI_ALL);
		int newSmsCount = 0;
		String[] projection = new String[] { "count(*)" };
		String selection = "thread_id=" + threadId + " and read=0";
		Cursor cursor = contentResolver.query(uri, projection, selection, null,
				null);
		if (cursor.moveToFirst()) {
			newSmsCount = Integer.valueOf(cursor.getString(cursor
					.getColumnIndex("count(*)")));
		}
		cursor.close();
		return newSmsCount;
	}

	/**
	 * 获取系统数据库每个联系人的最新的一条短信
	 * 
	 * @return 短信实体集
	 */
	public List<Msg> getEveryContactNo1Msg() {
		Uri uri = Uri.parse(SMS_URI_ALL);
		String[] projection = new String[] { "distinct " + "thread_id", "_id",
				"address", "body", "date", "type", "read" };
		String selection = "1=1) group by " + "thread_id"
				+ " order by date desc -- (";
		String sortOrder = " date desc";
		Cursor cursor = contentResolver.query(uri, projection, selection, null,
				sortOrder);

		List<Msg> msgs = new ArrayList<Msg>();
		while (cursor.moveToNext()) {
			Msg msg = new Msg();
			msg.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			msg.setThreadId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			msg.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			msg.setContent(cursor.getString(cursor.getColumnIndex("body")));
			msg.setMsgMark(cursor.getInt(cursor.getColumnIndex("type")));
			msg.setRead(cursor.getInt(cursor.getColumnIndex("read")));
			msg.setMsgCount(this.getMsgCountByThreadId(msg.getThreadId()));
			msg.setUnreadCount(this.getUnreadMsgCountByThreadId(msg
					.getThreadId()));
			// 设置姓名
			String address = cursor.getString(cursor.getColumnIndex("address"));
			List<Integer> list = new ArrayList<Integer>();
			list = telMgr.getContactIdByTelNumber(address);
			if (list.size() > 0)
				msg.setPerson(contactMgr.getContactById(list.get(0)).getName());
			else
				msg.setPerson("");
			// 设置日期
			String date = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			Date d = new Date(Long.parseLong(cursor.getString(cursor
					.getColumnIndex("date"))));
			date = dateFormat.format(d);
			msg.setMsgDate(date);

			msgs.add(msg);
		}
		return msgs;
	}

	/**
	 * 获取我的联系人列表中系统数据库每个联系人的最新的一条短信
	 * 
	 * @return 短信实体集
	 */
	public List<Msg> getMyEveryContactNo1MsgNoCount() {
		Uri uri = Uri.parse(SMS_URI_ALL);
		String[] projection = new String[] { "distinct " + "thread_id", "_id",
				"address", "body", "date", "type", "read" };
		String selection = "1=1) group by " + "thread_id"
				+ " order by date desc -- (";
		String sortOrder = " date desc";
		Cursor cursor = contentResolver.query(uri, projection, selection, null,
				sortOrder);

		List<Msg> msgs = new ArrayList<Msg>();

		while (cursor.moveToNext()) {
			Msg msg = new Msg();
			// 设置姓名
			String address = cursor.getString(cursor.getColumnIndex("address"));
			List<Integer> list = new ArrayList<Integer>();
			list = telMgr.getContactIdByTelNumber(address);
			if (list.size() > 0) {
				msg.setPerson(contactMgr.getContactById(list.get(0)).getName());
				msg.setAddress(address);
				msgs.add(msg);
			}
		}
		return msgs;
	}

	/**
	 * 将系统所有的短信插入自定义的数据库
	 */
	public void insertMsgsFromSysAllMsgs() {
		msgService.deleteAll();// 先删除所有数据再导入
		Uri uri = Uri.parse(SMS_URI_ALL);
		String[] projection = new String[] { "_id", "thread_id", "address",
				"person", "body", "date", "type" };
		String selection = "1=1) -- (";
		String sortOrder = " date desc";
		Cursor cursor = contentResolver.query(uri, projection, selection, null,
				sortOrder);
		while (cursor.moveToNext()) {
			Msg msg = new Msg();
			// 设置姓名
			msg.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			msg.setThreadId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			msg.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			msg.setContent(cursor.getString(cursor.getColumnIndex("body")));
			msg.setMsgMark(cursor.getInt(cursor.getColumnIndex("type")));
			msg.setMsgDate(cursor.getString(cursor.getColumnIndex("date")));
			msgService.insert(msg);// 将其插入到我的数据库
		}
	}

	/**
	 * 将我备份的信息还原到系统数据库
	 */
	public void insertIntoSysMsgFromMyMsg() {
		this.delAllMsg();// 先删除所有信息
		List<Msg> list = msgService.getMsgsByCondition(" 1=1 ");
		for (Msg msg : list) {
			ContentValues cv = new ContentValues();
			cv.put("address", msg.getAddress());
			cv.put("body", msg.getContent());
			cv.put("type", msg.getMsgMark());
			cv.put("date", msg.getMsgDate());
			cv.put("read", 1);
			Uri uri = Uri.parse(SMS_URI_ALL);
			contentResolver.insert(uri, cv);
		}
	}
}
