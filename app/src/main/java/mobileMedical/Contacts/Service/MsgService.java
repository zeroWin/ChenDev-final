package mobileMedical.Contacts.Service;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.DataBase.MSG_DB.TABLES.Msg.FIELDS;
import mobileMedical.Contacts.DataBase.MSG_DB.TABLES.Msg.SQL;
import mobileMedical.Contacts.DataBase.Msg_DBHelper;
import mobileMedical.Contacts.Interface.IMsg;
import mobileMedical.Contacts.Model.Msg;

public class MsgService implements IMsg {
	private Msg_DBHelper dbHelper;

	public MsgService(Context context) {
		dbHelper = new Msg_DBHelper(context);
	}

	/**
	 * 删除数据库
	 */
	public void dropTable() {
		String sql = SQL.DROPTABLE;
		dbHelper.execSQL(sql);
	}

	/**
	 * 添加信息
	 */
	@Override
	public void insert(Msg msg) {
		String sql = String.format(SQL.INSERT, msg.getId(), msg.getThreadId(),
				msg.getAddress(), msg.getPerson(), msg.getMsgMark(),
				msg.getMsgDate(), msg.getContent());
		dbHelper.execSQL(sql);
	}

	/**
	 * 删除信息
	 */
	@Override
	public void deleteAll() {
		String sql = String.format(SQL.DELETE);
		dbHelper.execSQL(sql);
	}

	/**
	 * 根据条件查询信息
	 */
	@Override
	public List<Msg> getMsgsByCondition(String condition) {
		String sql = String.format(SQL.SELECT, condition);
		Cursor cursor = dbHelper.rawQuery(sql);

		List<Msg> msgs = new ArrayList<Msg>();
		while (cursor.moveToNext()) {
			Msg msg = new Msg();
			msg.setId(cursor.getInt(cursor.getColumnIndex(FIELDS.ID)));
			msg.setThreadId(cursor.getInt(cursor
					.getColumnIndex(FIELDS.THREADID)));
			msg.setAddress(cursor.getString(cursor
					.getColumnIndex(FIELDS.ADDRESS)));
			msg.setPerson(cursor.getString(cursor.getColumnIndex(FIELDS.PERSON)));
			msg.setMsgMark(cursor.getInt(cursor.getColumnIndex(FIELDS.MSGMARK)));
			msg.setMsgDate(cursor.getString(cursor
					.getColumnIndex(FIELDS.MSGDATE)));
			msg.setContent(cursor.getString(cursor
					.getColumnIndex(FIELDS.CONTENT)));
			msgs.add(msg);
		}
		dbHelper.close();
		return msgs;
	}
}
