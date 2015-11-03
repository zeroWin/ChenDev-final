package mobileMedical.Contacts.DataBase;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	/**
	 * 构造函数，创建数据库
	 * 
	 * @param context
	 */
	public SQLiteHelper(Context context) {
		super(context, DB.DATABASENAME, null, DB.DATABASE_VERSION);
	}

	/**
	 * 创建表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(DB.TABLES.CONTACT.SQL.CREATE);
			db.execSQL(DB.TABLES.EMAIL.SQL.CREATE);
			db.execSQL(DB.TABLES.GROUP.SQL.CREATE);
			db.execSQL(DB.TABLES.IM.SQL.CREATE);
			db.execSQL(DB.TABLES.TEL.SQL.CREATE);
			//CommonUtil.Log("sqy", "SQLiteHelper", "onCreate", 'i');
		} catch (SQLException sqlEx) {
			throw sqlEx;
		}
	}

	/**
	 * 数据库升级
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DB.TABLES.CONTACT.SQL.DROPTABLE);
		db.execSQL(DB.TABLES.EMAIL.SQL.DROPTABLE);
		db.execSQL(DB.TABLES.GROUP.SQL.DROPTABLE);
		db.execSQL(DB.TABLES.IM.SQL.DROPTABLE);
		db.execSQL(DB.TABLES.TEL.SQL.DROPTABLE);
		onCreate(db);
		//CommonUtil.Log("sqy", "SQLiteHelper", "onUpgrade", 'i');
	}

}
