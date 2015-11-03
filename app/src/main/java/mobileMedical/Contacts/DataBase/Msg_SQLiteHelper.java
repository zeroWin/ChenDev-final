package mobileMedical.Contacts.DataBase;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Msg_SQLiteHelper extends SQLiteOpenHelper {

	/**
	 * 构造函数，创建数据库
	 * 
	 * @param context
	 */
	public Msg_SQLiteHelper(Context context) {
		super(context, MSG_DB.DATABASENAME, null, MSG_DB.DATABASE_VERSION);
	}

	/**
	 * 创建表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(MSG_DB.TABLES.Msg.SQL.CREATE);
			//CommonUtil.Log("msg", "SQLiteHelper", "onCreate", 'i');
		} catch (SQLException sqlEx) {
			throw sqlEx;
		}
	}

	/**
	 * 数据库升级
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(MSG_DB.TABLES.Msg.SQL.DROPTABLE);
		onCreate(db);
		//CommonUtil.Log("msg", "SQLiteHelper", "onUpgrade", 'i');
	}

}
