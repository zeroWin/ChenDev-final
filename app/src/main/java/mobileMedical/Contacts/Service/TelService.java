package mobileMedical.Contacts.Service;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.DataBase.DBHelper;
import mobileMedical.Contacts.DataBase.DB.TABLES.TEL.FIELDS;
import mobileMedical.Contacts.DataBase.DB.TABLES.TEL.SQL;
import mobileMedical.Contacts.Interface.ITel;
import mobileMedical.Contacts.Model.Tel;

import android.content.Context;
import android.database.Cursor;


public class TelService implements ITel {

	private DBHelper dbHelper;

	public TelService(Context context) {
		dbHelper = new DBHelper(context);
	}

	@Override
	public void insert(Tel tel) {
		String sql = String.format(SQL.INSERT, tel.getId(), tel.getTelName(),
				tel.getTelNumber());
		dbHelper.execSQL(sql);
	}

	@Override
	public void delete(int telId) {
		String sql = String.format(SQL.DELETE, telId);
		dbHelper.execSQL(sql);
	}

	@Override
	public void update(Tel tel) {
		String sql = String.format(SQL.UPDATE, tel.getId(), tel.getTelName(),
				tel.getTelNumber(), tel.getTelId());
		dbHelper.execSQL(sql);
	}

	@Override
	public List<Tel> getTelsByCondition(String condition) {
		String sql = String.format(SQL.SELECT, condition);
		Cursor cursor = dbHelper.rawQuery(sql);

		List<Tel> list = new ArrayList<Tel>();
		while (cursor.moveToNext()) {
			Tel tel = new Tel();
			tel.setTelId(cursor.getInt(cursor.getColumnIndex(FIELDS.TELID)));
			tel.setId(cursor.getInt(cursor.getColumnIndex(FIELDS.ID)));
			tel.setTelName(cursor.getString(cursor
					.getColumnIndex(FIELDS.TELNAME)));
			tel.setTelNumber(cursor.getString(cursor
					.getColumnIndex(FIELDS.TELNUMBER)));

			list.add(tel);
		}
		dbHelper.close();
		return list;
	}

}
