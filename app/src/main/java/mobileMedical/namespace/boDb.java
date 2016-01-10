package mobileMedical.namespace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import devDataType.Parameters.IntParameter;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.MessageInfo;
import mobileMedical.Message.ParameterDataKeys;

public class boDb extends SQLiteOpenHelper {
	// 调用父类构造器
	public boDb(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 ** 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行. 重写onCreate方法，调用execSQL方法创建表
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建表bodb，结构为
		// userid integer，
		// testid integer，
		// measItemResultsid integer
		// resultsid integer
		// heartrate short
		// pulse short
		// bld short
		// timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
		db.execSQL("create table if not exists bodb(" + "userid integer,"
				+ "testid integer," + "measItemResultsid integer,"
				+ "resultsid integer," + "heartrate short," + "pulse short,"
				+ "bld short,"
				+ "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
		//创建表data，结构为id integer
		// sensorType integer
		// measItem integer
		// doctorID integer
		// patientID integer
		// timestamp DATETIME DEFAULT (CURRENT_TIMESTAMP)
		// measResults text
		// measResultsIdx integer
		db.execSQL("create table if not exists "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + "(" + "id integer,"
				+ "sensorType integer," + "measItem integer,"
				+ "doctorID integer," + "patientID integer,"
				+ "timestamp DATETIME DEFAULT (CURRENT_TIMESTAMP),"
				+ "measResults text," + "measResultsIdx integer)");
		// db.execSQL("create table if not exists bodyTemp(" + "userid integer,"
		// + "bt float," + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
		// db.execSQL("create table if not exists bodyTempBase("
		// + "userid integer," + "bt float,"
		// + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
		//创建表timeSetting，结构为下面
		db.execSQL("create table if not exists timeSetting("
				+ "morningFromHour integer," + "morningFromMin integer,"
				+ "morningToHour integer," + "morningToMin integer,"
				+ "noonFromHour integer," + "noonFromMin integer,"
				+ "noonToHour integer," + "noonToMin integer,"
				+ "eveningFromHour integer," + "eveningFromMin integer,"
				+ "eveningToHour integer," + "eveningToMin integer,"
				+ "nightFromHour integer," + "nightFromMin integer,"
				+ "nightToHour integer," + "nightToMin integer,"
				+ "analyFromHour integer," + "analyFromMin integer,"
				+ "analyToHour integer," + "analyToMin integer)");
		//创建表readDataFromFile，结构为下面
		db.execSQL("create table if not exists readDataFromFile("
				+ "readDataFile integer DEFAULT 0)");
		//创建表copyDataFileToSDOrAppData
		db.execSQL("create table if not exists copyDataFileToSDOrAppData("
				+ "copyDataFile integer DEFAULT 0)");
		//创建表TransIDParm
		db.execSQL("create table if not exists TransIDParm("
				+ "TransID integer DEFAULT 0)");
		// db.execSQL("create table if not exists bodyTempLast("
		// + "userid integer," + "bt float," + "isbase int,"
		// + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
		if (BuildConfig.DEBUG) {
			Log.i("bodb", "++ create bodb ++");
		}
	}

	// 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法 46.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public SQLiteDatabase getWritableDb() {
		SQLiteDatabase db = this.getWritableDatabase();
		return db;
	}

	public int getMaxTransId() {
		int result = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select max(id)as maxid from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + "", null);
		if (cursor.moveToNext()) {
			result = cursor.getInt(cursor.getColumnIndex("maxid"));
		}
		cursor.close();
		return result;
	}

	public Cursor pulseMax() {
		String args[] = { "1" };
		String[] columns = { "userid", "pulse" };
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select testid, timestamp, max(pulse)as maxpulse from bodb where userid=1 group by testid order by testid",
						null);

		// Cursor cursor = db.rawQuery
		// ("select pulse from bodb where userid=1",null);
		// Cursor cursor = db.query("bodb",null,null, null, null, null, null);
		// db.close();
		return cursor;
	}

	public Cursor pulseMin() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select testid, timestamp, min(pulse)as minpulse from bodb where userid=1 group by testid order by testid",
						null);

		// db.close();
		return cursor;
	}

	public Cursor pulseAvg() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select testid, timestamp, avg(pulse)as avepulse from bodb where userid=1 group by testid order by testid",
						null);

		// db.close();
		return cursor;
	}

	public Cursor getFirstItem() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select testid, timestamp from bodb where userid=1 group by testid order by testid limit 1",
						null);

		// db.close();
		return cursor;
	}

	public Cursor getAllStatistics() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select testid, timestamp,avg(pulse)as avepulse, min(pulse)as minpulse, max(pulse)as maxpulse, min(bld) as minspo from bodb where userid=1 group by testid order by testid",
						null);

		// db.close();
		return cursor;
	}

	// all db operation for bodytemp
	public long insertBt(int user_id, float body_temp, Date dateTime) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put("userid", user_id);

		cv.put("bt", body_temp);
		if (dateTime != null) {
			// Maybe we should use the DATTIME, and check it then change it to
			// String to make sure
			// the datetime is in correct type and value.

			SimpleDateFormat parser = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.sss");
			String time = parser.format(dateTime);
			cv.put("timestamp", time);
		}

		long row = db.insert("bodyTemp", null, cv);
		// db.close();
		return row;
	}

	public long insertBtBase(int user_id, float body_temp, Date dateTime) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put("userid", user_id);
		cv.put("bt", body_temp);
		if (dateTime != null) {
			// Maybe we should use the DATTIME, and check it then change it to
			// String

			SimpleDateFormat parser = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.sss");
			String time = parser.format(dateTime);
			cv.put("timestamp", time);
		}

		long row = db.insert("bodyTempBase", null, cv);
		// db.close();
		return row;
	}

	public void insertOneItem(int transID, int sensorType, int measItem,
			int doctorID, int patientID, String time, String measResults,
			int measResultsIdx) {

		SQLiteDatabase db = this.getWritableDb();
		db.beginTransaction();
		try {
			ContentValues cv = new ContentValues();
			cv.put(ConstDef.DATABASE_FIELD_TRANSID, transID);
			cv.put(ConstDef.DATABASE_FIELD_SENSORTYPE, sensorType);
			cv.put(ConstDef.DATABASE_FIELD_MEASITEM, measItem);
			cv.put(ConstDef.DATABASE_FIELD_DOCTORID, doctorID);
			cv.put(ConstDef.DATABASE_FIELD_PATIENTID, patientID);
			cv.put(ConstDef.DATABASE_FIELD_TIMESTAMP, time);
			cv.put(ConstDef.DATABASE_FIELD_MEASRESULTS, measResults);
			cv.put(ConstDef.DATABASE_FIELD_MEASRESULTSIDX, measResultsIdx);

			db.insert(ConstDef.DATABASE_TABLE_NAME_DATA, null, cv);
			db.setTransactionSuccessful();
			// 在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 当所有操作执行完成后结束一个事务
		db.endTransaction();
		db.close();
	}

	public Cursor getAllItemsBt() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select timestamp,measResults from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where patientID="
				+ MemberManage.patientID + " and "
				+ ConstDef.DATABASE_FIELD_MEASITEM + "="
				+ MessageInfo.MM_MI_BODY_TEMPERATURE + " and doctorID="
				+ MemberManage.doctorID
				+ " group by timestamp order by timestamp", null);
		// db.close();
		return cursor;
	}

	public Cursor getAllItemsBtBase() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select timestamp,measResults from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where (patientID="
				+ MemberManage.patientID + ") and ("
				+ ConstDef.DATABASE_FIELD_MEASITEM + "="
				+ MessageInfo.MM_MI_BODY_BASE_TEMPERATURE + ") group by timestamp order by timestamp", null);
		// db.close();
		return cursor;
	}

	/**
	 * 执行select * from data where patientID=XXX group by id
	 * order by timestamp desc，返回游标
	 * @return
	 */
	public Cursor getAllItems() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where patientID="
				+ MemberManage.patientID + " group by id order by timestamp desc", null);
		// db.close();
		return cursor;
	}

	public Cursor getOneSpecItem(int type) {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where patientID="
				+ MemberManage.patientID + " and "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = " + type
				+ " group by id order by timestamp desc", null);
		// db.close();
		return cursor;
	}

	public Cursor getTmpItem() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where patientID="
				+ MemberManage.patientID + " and ("
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = "
				+ MessageInfo.SENSORTYPE_THERMOMETER
				+ " ) group by id order by timestamp desc", null);
		// db.close();
		return cursor;
	}

	// time setting

	/**
	 * 初始化timeSetting表内容为如下所示。
	 * @return
	 */
	public long initTimeSetting() {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put("morningFromHour", 6);
		cv.put("morningFromMin", 0);
		cv.put("morningToHour", 11);
		cv.put("morningToMin", 0);

		cv.put("noonFromHour", 11);
		cv.put("noonFromMin", 0);
		cv.put("noonToHour", 18);
		cv.put("noonToMin", 0);

		cv.put("eveningFromHour", 18);
		cv.put("eveningFromMin", 0);
		cv.put("eveningToHour", 20);
		cv.put("eveningToMin", 0);

		cv.put("nightFromHour", 20);
		cv.put("nightFromMin", 0);
		cv.put("nightToHour", 24);
		cv.put("nightToMin", 0);

		cv.put("analyFromHour", 22);
		cv.put("analyFromMin", 0);
		cv.put("analyToHour", 24);
		cv.put("analyToMin", 0);

		long row = db.insert("timeSetting", null, cv);
		// db.close();
		return row;
	}

	/**
	 * 执行select * from timeSetting，返回游标
	 * @return
	 */
	public Cursor getTimeSetting() {

		SQLiteDatabase db = this.getReadableDatabase();
		// Cursor cursor = db.rawQuery
		// ("select morningFromHour,morningFromMin,morningToHour,morningToMin,noonFromHour,noonFromMin,noonToHour,noonToMin,eveningFromHour,eveningFromMin,eveningToHour,eveningToMin,nightFromHour,nightFromMin,nightToHour,nightToMin,anlysisFromHour,anlysisFromMin,anlysisToHour,anlysisToMin,from timeSetting",null);
		Cursor cursor = db.rawQuery("select * from timeSetting", null);
		// Cursor cursor = db.query("bodb",null,null, null, null, null, null);
		// db.close();
		return cursor;
	}

	public long insertReadDataFile(int value) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put("readDataFile", value);

		long row = db.insert("readDataFromFile", null, cv);
		// db.close();
		return row;
	}

	public Cursor getReadDataFile() {

		SQLiteDatabase db = this.getReadableDatabase();
		// Cursor cursor = db.rawQuery
		// ("select morningFromHour,morningFromMin,morningToHour,morningToMin,noonFromHour,noonFromMin,noonToHour,noonToMin,eveningFromHour,eveningFromMin,eveningToHour,eveningToMin,nightFromHour,nightFromMin,nightToHour,nightToMin,anlysisFromHour,anlysisFromMin,anlysisToHour,anlysisToMin,from timeSetting",null);
		Cursor cursor = db.rawQuery("select * from readDataFromFile", null);
		// Cursor cursor = db.query("bodb",null,null, null, null, null, null);
		// db.close();
		return cursor;
	}

	public void UpdateReadDataFile(int value) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("readDataFile", value);
		db.update("readDataFromFile", cv, null, null);
	}

	public long insertCopyDataFile(int value) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put("copyDataFile", value);

		long row = db.insert("copyDataFileToSDOrAppData", null, cv);
		// db.close();
		return row;
	}

	/**
	 * 执行select * from copyDataFileToSDOrAppData，返回游标
	 * @return
	 */
	public Cursor getCopyDataFile() {

		SQLiteDatabase db = this.getReadableDatabase();
		// Cursor cursor = db.rawQuery
		// ("select morningFromHour,morningFromMin,morningToHour,morningToMin,noonFromHour,noonFromMin,noonToHour,noonToMin,eveningFromHour,eveningFromMin,eveningToHour,eveningToMin,nightFromHour,nightFromMin,nightToHour,nightToMin,anlysisFromHour,anlysisFromMin,anlysisToHour,anlysisToMin,from timeSetting",null);
		Cursor cursor = db.rawQuery("select * from copyDataFileToSDOrAppData",
				null);
		// Cursor cursor = db.query("bodb",null,null, null, null, null, null);
		// db.close();
		return cursor;
	}

	public void UpdateCopyDataFile(int value) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("copyDataFile", value);
		db.update("copyDataFileToSDOrAppData", cv, null, null);
	}

	public long insertTransIDParm(int value) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put("TransID", value);

		long row = db.insert("TransIDParm", null, cv);
		// db.close();
		return row;
	}

	/**
	 * 执行select * from TransIDParm，返回游标
	 * @return
	 */
	public Cursor getTransIDParm() {

		SQLiteDatabase db = this.getReadableDatabase();
		// Cursor cursor = db.rawQuery
		// ("select morningFromHour,morningFromMin,morningToHour,morningToMin,noonFromHour,noonFromMin,noonToHour,noonToMin,eveningFromHour,eveningFromMin,eveningToHour,eveningToMin,nightFromHour,nightFromMin,nightToHour,nightToMin,anlysisFromHour,anlysisFromMin,anlysisToHour,anlysisToMin,from timeSetting",null);
		Cursor cursor = db.rawQuery("select * from TransIDParm", null);
		// Cursor cursor = db.query("bodb",null,null, null, null, null, null);
		// db.close();
		return cursor;
	}

	public void UpdateTransIDParm(int value) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("TransID", value);
		db.update("TransIDParm", cv, null, null);
	}

	public void setTime(int item, int hour, int min) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		String[] parms = new String[] { "this is a string" };

		switch (item) {
		case 1:
			cv.put("morningFromHour", hour);
			cv.put("morningFromMin", min);
			db.update("timeSetting", cv, null, null);

			break;
		case 2:
			cv.put("morningToHour", hour);
			cv.put("morningToMin", min);
			db.update("timeSetting", cv, null, null);
			break;

		case 3:
			cv.put("noonFromHour", hour);
			cv.put("noonFromMin", min);
			db.update("timeSetting", cv, null, null);
			break;

		case 4:
			cv.put("noonToHour", hour);
			cv.put("noonToMin", min);
			db.update("timeSetting", cv, null, null);
			break;

		case 5:
			cv.put("eveningFromHour", hour);
			cv.put("eveningFromMin", min);
			db.update("timeSetting", cv, null, null);
			break;

		case 6:
			cv.put("eveningToHour", hour);
			cv.put("eveningToMin", min);
			db.update("timeSetting", cv, null, null);
			break;

		case 7:
			cv.put("nightFromHour", hour);
			cv.put("nightFromMin", min);
			db.update("timeSetting", cv, null, null);
			break;

		case 8:
			cv.put("nightToHour", hour);
			cv.put("nightToMin", min);
			db.update("timeSetting", cv, null, null);
			break;

		case 9:
			cv.put("analyFromHour", hour);
			cv.put("analyFromMin", min);
			db.update("timeSetting", cv, null, null);
			break;

		case 10:
			cv.put("analyToHour", hour);
			cv.put("analyToMin", min);
			db.update("timeSetting", cv, null, null);
			break;
		default:
			break;

		}
	}

	// db for last body temp
	public void insertBt(boDb boDbHelper, double body_temp) {
		int transID = ((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).GetValue();
		int sensorType = ((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORTYPE)).GetValue();
		int measItem = ((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MEASITEM)).GetValue();
		int doctorID = ((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.DOCTORID)).GetValue();
		int patientID = ((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.PATIENTID)).GetValue();

		SQLiteDatabase db = boDbHelper.getWritableDb();
		db.beginTransaction();
		try {
			ContentValues cv = new ContentValues();
			cv.put(ConstDef.DATABASE_FIELD_TRANSID, transID);
			cv.put(ConstDef.DATABASE_FIELD_SENSORTYPE, sensorType);
			cv.put(ConstDef.DATABASE_FIELD_MEASITEM, measItem);
			cv.put(ConstDef.DATABASE_FIELD_DOCTORID, doctorID);
			cv.put(ConstDef.DATABASE_FIELD_PATIENTID, patientID);
			cv.put(ConstDef.DATABASE_FIELD_MEASRESULTS, body_temp);
			cv.put(ConstDef.DATABASE_FIELD_MEASRESULTSIDX, 0);

			db.insert(ConstDef.DATABASE_TABLE_NAME_DATA, null, cv);
			db.setTransactionSuccessful();
			// 在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 当所有操作执行完成后结束一个事务
		db.endTransaction();
		db.close();
	}

	public Cursor getBodyTempAndBodyBaseTempLast() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"SELECT A.* FROM "
								+ ConstDef.DATABASE_TABLE_NAME_DATA
								+ " A ,( select sensorType,max(timestamp) T from "
								+ ConstDef.DATABASE_TABLE_NAME_DATA
								+ " where patientID="
								+ MemberManage.patientID
								+ "  group by sensorType) B where (A.timestamp=B.T) and(A.sensorType="
								+ MessageInfo.SENSORTYPE_THERMOMETER + " )",
						null);
		return cursor;
	}

	public Cursor getDoctorRecordInfo() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where doctorID="
				+ MemberManage.doctorID
				// + " and measResultsIdx = 0 order by timestamp desc", null);
				+ " group by id order by timestamp desc", null);

		return cursor;
	}

	public int getNumberViaTransId(int transId) {
		int result = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(id) as idnum from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where id =" + transId,
				null);
		if (cursor.moveToNext()) {
			result = cursor.getInt(cursor.getColumnIndex("idnum"));
		}
		cursor.close();
		return result;
	}

	public Cursor getLatestData(int id) {

		SQLiteDatabase db = this.getReadableDatabase();
		// Cursor cursor = db.rawQuery
		// ("select morningFromHour,morningFromMin,morningToHour,morningToMin,noonFromHour,noonFromMin,noonToHour,noonToMin,eveningFromHour,eveningFromMin,eveningToHour,eveningToMin,nightFromHour,nightFromMin,nightToHour,nightToMin,anlysisFromHour,anlysisFromMin,anlysisToHour,anlysisToMin,from timeSetting",null);
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where id >" + id
				+ " order by timestamp", null);
		// Cursor cursor = db.query("bodb",null,null, null, null, null, null);
		// db.close();
		return cursor;
	}

	/**
	 * 执行select * from data where (sensorType = 体温)and (patientID = xx)
	 *  order by timestamp desc limit 0,1,并返回游标
	 * @return
	 */
	public Cursor getLatestBodyOrBaseTem() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = "
				+ MessageInfo.SENSORTYPE_THERMOMETER + " ) and ("
				+ ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID
				+ ") order by timestamp desc limit 0,1", null);
		// Cursor cursor = db.query("bodb",null,null, null, null, null, null);
		// db.close();
		return cursor;
	}

	public Cursor getSpecCursorByTime(String dateString) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_TIMESTAMP + " = '" + dateString
				+ "' ) and (" + ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID
				+ ") order by timestamp desc limit 0,1", null);
		return cursor;
	}

	public Cursor getLatestInfo(int type) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("select max(id) as maxid from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = " + type + ") and ("
				+ ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID + ") order by timestamp", null);
		int maxId = 0;
		if (cursor.moveToNext()) {
			maxId = cursor.getInt(cursor.getColumnIndex("maxid"));
		}
		cursor.close();

		Cursor cursor2 = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = " + type + ") and ("
				+ ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID + " and "
				+ ConstDef.DATABASE_FIELD_TRANSID + " = " + maxId
				+ ") order by " + ConstDef.DATABASE_FIELD_MEASRESULTSIDX
				+ " desc limit 0,1", null);
		// Cursor cursor = db.query("bodb",null,null, null, null, null, null);
		// db.close();
		return cursor2;
	}

	// get the list to draw the lines
	public Cursor getLatestInfoList(int type) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("select max(id) as maxid from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = " + type + ") and ("
				+ ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID + ") order by timestamp", null);
		int maxId = 0;
		if (cursor.moveToNext()) {
			maxId = cursor.getInt(cursor.getColumnIndex("maxid"));
		}
		cursor.close();

		Cursor cursor2 = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = " + type + " and "
				+ ConstDef.DATABASE_FIELD_TRANSID + " = '" + maxId + "') and ("
				+ ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID + ") order by timestamp", null);
		return cursor2;
	}

	public Cursor getInfoListByTransId(int type, int transId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = " + type + " and "
				+ ConstDef.DATABASE_FIELD_TRANSID + " = '" + transId
				+ "') and (" + ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID + ") order by timestamp", null);
		return cursor;
	}

	public Cursor getInfoListByTime(int type, String time) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("select " + ConstDef.DATABASE_FIELD_TRANSID
				+ " from " + ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = " + type + ") and ("
				+ ConstDef.DATABASE_FIELD_TIMESTAMP + " = '" + time
				+ "') and (" + ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID + ") order by timestamp", null);
		int transId = 0;
		if (cursor.moveToNext()) {
			transId = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_TRANSID));
		}
		cursor.close();

		Cursor cursor2 = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ( "
				+ ConstDef.DATABASE_FIELD_SENSORTYPE + " = " + type + " and "
				+ ConstDef.DATABASE_FIELD_TRANSID + " = '" + transId
				+ "') and (" + ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID + ") order by timestamp", null);
		return cursor2;
	}

	public int getCountByTime(String time) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "
				+ ConstDef.DATABASE_TABLE_NAME_DATA + " where ("
				+ ConstDef.DATABASE_FIELD_TIMESTAMP + " = '" + time
				+ "') and (" + ConstDef.DATABASE_FIELD_PATIENTID + "= "
				+ MemberManage.patientID + ")", null);
		int result = cursor.getCount();
		cursor.close();
		return result;
	}

	public void deleteData() {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "delete from " + ConstDef.DATABASE_TABLE_NAME_DATA;
		db.execSQL(sql);
		db.close();
	}

}