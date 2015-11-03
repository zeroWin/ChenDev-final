package mobileMedical.Contacts.Service;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.DataBase.DBHelper;
import mobileMedical.Contacts.DataBase.DB.TABLES.GROUP.FIELDS;
import mobileMedical.Contacts.DataBase.DB.TABLES.GROUP.SQL;
import mobileMedical.Contacts.Interface.IGroup;
import mobileMedical.Contacts.Model.Group;

import android.content.Context;
import android.database.Cursor;


public class GroupService implements IGroup {

	private DBHelper dbHelper;

	public GroupService(Context context) {
		dbHelper = new DBHelper(context);
	}

	@Override
	public void insert(Group group) {
		String sql = String.format(SQL.INSERT, group.getGroupName());
		dbHelper.execSQL(sql);
	}

	@Override
	public void delete(int groupId) {
		String sql = String.format(SQL.DELETE, groupId);
		dbHelper.execSQL(sql);
	}

	@Override
	public void update(Group group) {
		String sql = String.format(SQL.UPDATE, group.getGroupName(),
				group.getGroupId());
		dbHelper.execSQL(sql);
	}

	@Override
	public List<Group> getGroupsByCondition(String condition) {
		String sql = String.format(SQL.SELECT, condition);
		Cursor cursor = dbHelper.rawQuery(sql);

		List<Group> list = new ArrayList<Group>();
		while (cursor.moveToNext()) {
			Group group = new Group();
			group.setGroupId(cursor.getInt(cursor
					.getColumnIndex(FIELDS.GROUPID)));
			group.setGroupName(cursor.getString(cursor
					.getColumnIndex(FIELDS.GROUPNAME)));

			list.add(group);
		}
		dbHelper.close();
		return list;
	}

}
