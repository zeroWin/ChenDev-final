package mobileMedical.Contacts.DataBase;

import java.util.List;

import mobileMedical.Contacts.Tool.CommonUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper {

	private SQLiteHelper helper;
	private SQLiteDatabase db;

	public DBHelper(Context context) {
		helper = new SQLiteHelper(context);
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
			//CommonUtil.Log("sqy", "DBHelper", "execSQL:sql=" + sql, 'i');
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
			//CommonUtil.Log("sqy", "DBHelper", "rawQuery：sql=" + sql, 'i');
			return cursor;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * 用于统计返回首行首列的单一值
	 * 
	 * @param sql
	 *            select min(),select max(),select count(),select sum()
	 * @return
	 * @throws SQLException
	 */
	public int execSingle(String sql) throws SQLException {
		db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			//CommonUtil.Log("sqy", "DBHelper", "execSingle：sql=" + sql, 'i');
			if (cursor.moveToNext()) {
				return cursor.getInt(0);
			}
			return -1;
		} finally {
			cursor.close();
			db.close();
		}
	}

	/**
	 * 添加一条记录
	 * 
	 * @param table
	 *            表名
	 * @param values
	 *            表字段及值的HashMap
	 */
	public void insert(String table, ContentValues values) throws SQLException {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			db.insert(table, null, values);
			//CommonUtil.Log("sqy", "DBHelper", "insert:tableName=" + table, 'i');
		} finally {
			db.close();
		}
	}

	/**
	 * 根据条件修改记录
	 * 
	 * @param table
	 *            表名
	 * @param values
	 *            表字段及值的HashMap
	 * @param whereClause
	 *            Where语句部分(eg: id=?)
	 * @param whereArgs
	 *            where部分应填充的值(eg:new String[]{1});
	 */
	public void update(String table, ContentValues values, String whereClause,
			String[] whereArgs) throws SQLException {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			db.update(table, values, whereClause, whereArgs);
			//CommonUtil.Log("sqy", "DBHelper", "update:tableName=" + table, 'i');
		} finally {
			db.close();
		}
	}

	/**
	 * 删除
	 * 
	 * @param table
	 *            表名
	 * @param whereClause
	 *            where 语句部分 eg: id=?
	 * @param whereArgs
	 *            where语句部条件参数的值：eg : new String[]{"xxx"}
	 */
	public void delete(String table, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			db.delete(table, whereClause, whereArgs);
			//CommonUtil.Log("sqy", "DBHelper", "delete:tableName=" + table, 'i');
		} finally {
			db.close();
		}
	}

	/**
	 * 封装代有事务版本的操作
	 * 
	 * @param sqls
	 *            eg: List<String> sqls =new ArrayList<String>();
	 *            sqls.add("insert into table (field1,field2) values(value1,value2); "
	 *            );
	 *            sqls.add("insert into table (field1,field2) values(value1,value2); "
	 *            );
	 */
	public void execSQLWithTrans(List<String> sqls) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			db.beginTransaction();// 开始事务
			for (String sql : sqls) {
				db.execSQL(sql);
				//CommonUtil.Log("sqy", "DBHelper",
				//		"execSQLWithTrans:sql=" + sql, 'i');
			}
			db.setTransactionSuccessful();

		} finally {
			db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事务
			db.close();
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
