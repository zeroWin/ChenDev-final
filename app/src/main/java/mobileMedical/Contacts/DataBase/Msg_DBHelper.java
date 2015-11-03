package mobileMedical.Contacts.DataBase;

import mobileMedical.Contacts.Tool.CommonUtil;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Msg_DBHelper {

	private Msg_SQLiteHelper helper;
	private SQLiteDatabase db;

	public Msg_DBHelper(Context context) {
		helper = new Msg_SQLiteHelper(context);
	}

	/**
	 * 用于执行数据的增删改操作
	 * 
	 * @param sql
	 *            以insert | update |delete打头的SQL语句
	 */
	public void execSQL(String sql) {
		db = helper.getWritableDatabase();
		try {
			db.execSQL(sql);
			//CommonUtil.Log("msg", "DBHelper", "execSQL:sql=" + sql, 'i');
		} catch (SQLException sqlEx) {
			System.out.println(sqlEx.getMessage());
		} finally {
			db.close();
		}
	}

	/**
	 * 用于执行数据的查询操作
	 * 
	 * @param sql以select打头的SQL语句
	 * @return
	 */
	public Cursor rawQuery(String sql) {
		db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			//CommonUtil.Log("msg", "DBHelper", "rawQuery：sql=" + sql, 'i');
			return cursor;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * 用于关闭表
	 */
	public void close() {
		if (db != null)
			db.close();
	}
}
