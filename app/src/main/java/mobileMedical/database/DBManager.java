package mobileMedical.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import mobileMedical.namespace.ConstDef;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
		db.execSQL("CREATE TABLE IF NOT EXISTS doctorInfo" + "("
				+ ConstDef.DATABASE_FIELD_USERNAME + " varchar(20),"
				+ ConstDef.DATABASE_FIELD_PASSWORD + " varchar(20),"
				+ ConstDef.DATABASE_FIELD_ID + " varchar(20),"
				+ ConstDef.DATABASE_FIELD_WORK + " varchar(20),"
				+ ConstDef.DATABASE_FIELD_ROOM + " varchar(20),"
				+ ConstDef.DATABASE_FIELD_LEVEL + " varchar(20),"
				+ ConstDef.DATABASE_FIELD_TEL + " varchar(20),"
				+ ConstDef.DATABASE_FIELD_EMAIL + " TEXT,"
				+ ConstDef.DATABASE_FIELD_IMAGE + " integer)");
		db.execSQL("CREATE TABLE IF NOT EXISTS doctorSetting"
				+ "(remember integer," + ConstDef.DATABASE_FIELD_USERNAME
				+ " varchar(20)," + ConstDef.DATABASE_FIELD_PASSWORD
				+ " varchar(20))");
		db.close();
	}

	public void add(String name, String pw, String id, String work,
			String room, String level, String tel, String email) {// 插入一条数据
		db = helper.getWritableDatabase();
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL(
					"INSERT INTO doctorInfo VALUES( ?,?,?,?,?,?,?,?,?)",
					new Object[] { name, pw, id, work, room, level, tel, email ,null});
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
			db.close();
		}
	}

	public void updateOneDoctorInfo(String key, String value,String doctorId) {
		db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		if (key.equalsIgnoreCase("姓名")) {
			cv.put(ConstDef.DATABASE_FIELD_USERNAME, value);
		} else if (key.equalsIgnoreCase("单位")) {
			cv.put(ConstDef.DATABASE_FIELD_WORK, value);
		} else if (key.equalsIgnoreCase("科室")) {
			cv.put(ConstDef.DATABASE_FIELD_ROOM, value);
		} else if (key.equalsIgnoreCase("级别")) {
			cv.put(ConstDef.DATABASE_FIELD_LEVEL, value);
		} else if (key.equalsIgnoreCase("电话")) {
			cv.put(ConstDef.DATABASE_FIELD_TEL, value);
		} else if (key.equalsIgnoreCase("email邮箱")) {
			cv.put(ConstDef.DATABASE_FIELD_EMAIL, value);
		}
		db.update("doctorInfo", cv, ConstDef.DATABASE_FIELD_ID +"=?",new String[]{doctorId});
		db.close();
	}
	
	public void updateDoctorImage(String doctorId, byte[] image) {
		db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
			cv.put(ConstDef.DATABASE_FIELD_IMAGE, image);
		db.update("doctorInfo", cv, ConstDef.DATABASE_FIELD_ID +"=?",new String[]{doctorId});
		db.close();
	}
	
	public byte[] getImageByDoctorId(String doctorId) {
		db = helper.getWritableDatabase();
		byte[] image=null;
//		db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM doctorInfo where "+ConstDef.DATABASE_FIELD_ID+" = "+doctorId, null);

		if (c.moveToNext()) {
			image=c.getBlob(c.getColumnIndex(ConstDef.DATABASE_FIELD_IMAGE));
		}
		c.close();
		db.close();
		return image;
	}

	public void addNewMethod(String name, String pw, String id, String work,
			String room, String level, String tel, String email) {// 插入一条数据
		db = helper.getWritableDatabase();
		String sql = "insert into doctorInfo("
				+ ConstDef.DATABASE_FIELD_USERNAME + ","
				+ ConstDef.DATABASE_FIELD_PASSWORD + ","
				+ ConstDef.DATABASE_FIELD_ID + ","
				+ ConstDef.DATABASE_FIELD_WORK + ","
				+ ConstDef.DATABASE_FIELD_ROOM + ","
				+ ConstDef.DATABASE_FIELD_LEVEL + ","
				+ ConstDef.DATABASE_FIELD_TEL + ","
				+ ConstDef.DATABASE_FIELD_EMAIL + ") values(?,?,?,?,?,?,?,?)";
		SQLiteStatement stat = db.compileStatement(sql);
		db.beginTransaction();

		stat.bindString(1, name);
		stat.bindString(2, pw);
		stat.bindString(3, id);
		stat.bindString(4, work);
		stat.bindString(5, room);
		stat.bindString(6, level);
		stat.bindString(7, tel);
		stat.bindString(8, email);

		stat.executeInsert();
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public void addSetting(int remember, String username, String passwrod) {// 插入一条数据
		db = helper.getWritableDatabase();
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL("INSERT INTO doctorSetting VALUES( ?,?,?)",
					new Object[] { remember, username, passwrod });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
			db.close();
		}
	}

	public void updateSetting(int remember, String username, String passwrod) {
		db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("remember", remember);
		cv.put("username", username);
		cv.put("password", passwrod);
		db.update("doctorSetting", cv, null, null);
		db.close();
	}

	public Cursor querySetting() {
		db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM doctorSetting  ", null);
		return c;
	}

	public Cursor queryDoctorInfo(int id) {
		db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM doctorInfo where "
				+ ConstDef.DATABASE_FIELD_ID + " = " + id,
				null);
		return c;
	}
	
	public boolean identifyId(String id) {
		boolean result=false;
		db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM doctorInfo where "
				+ ConstDef.DATABASE_FIELD_ID + " = " + id,
				null);
		if(c.getCount()>0)
			result=false;
		else {
			result=true;
		}
		db.close();
		return result;
	}

	public int query(String username, String pw) {
		db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM doctorInfo WHERE "
				+ ConstDef.DATABASE_FIELD_USERNAME + " = ? and "
				+ ConstDef.DATABASE_FIELD_PASSWORD + " = ? ", new String[] {
				username, pw });
		if (c.getCount() != 0) {
			c.moveToNext();
			return Integer.valueOf(c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_ID)));
		}
		c.close();
		db.close();
		return -1;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		db.close();
	}
}
