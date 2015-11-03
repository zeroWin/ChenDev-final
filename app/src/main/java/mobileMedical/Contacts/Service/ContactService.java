package mobileMedical.Contacts.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.DataBase.DB;
import mobileMedical.Contacts.DataBase.DBHelper;
import mobileMedical.Contacts.Interface.IContact;
import mobileMedical.Contacts.Model.Contact;


public class ContactService implements IContact {
	private DBHelper dbHelper;

	public ContactService(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * 插入联系人实体
	 */
	@Override
	public void insert(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(DB.TABLES.CONTACT.FIELDS.NAME, contact.getName());
		values.put(DB.TABLES.CONTACT.FIELDS.NAMEPINYIN, contact.getNamePinyin());
		values.put(DB.TABLES.CONTACT.FIELDS.NICKNAME, contact.getNickName());
		values.put(DB.TABLES.CONTACT.FIELDS.ADDRESS, contact.getAddress());
		values.put(DB.TABLES.CONTACT.FIELDS.COMPANY, contact.getCompany());
		values.put(DB.TABLES.CONTACT.FIELDS.BIRTHDAY, contact.getBirthday());
		values.put(DB.TABLES.CONTACT.FIELDS.NOTE, contact.getNote());
		values.put(DB.TABLES.CONTACT.FIELDS.IMAGE, contact.getImage());
		values.put(DB.TABLES.CONTACT.FIELDS.GROUPID, contact.getGroupId());
		values.put(DB.TABLES.CONTACT.FIELDS.IDNO, contact.getIdNo());
		values.put(DB.TABLES.CONTACT.FIELDS.DESCRIBE, contact.getDescribe());
		values.put(DB.TABLES.CONTACT.FIELDS.GENDER, contact.getGender());
		values.put(DB.TABLES.CONTACT.FIELDS.TYPE, contact.getType());
		values.put(DB.TABLES.CONTACT.FIELDS.FILENO, contact.getFileNo());
		values.put(DB.TABLES.CONTACT.FIELDS.HOSPITAL, contact.getHospital());
		values.put(DB.TABLES.CONTACT.FIELDS.RELATIONNAME, contact.getRelationName());
		values.put(DB.TABLES.CONTACT.FIELDS.RELATION, contact.getRelation());
		values.put(DB.TABLES.CONTACT.FIELDS.RELATIONTELE, contact.getRelationTele());
		values.put(DB.TABLES.CONTACT.FIELDS.RELATIONEMAIL, contact.getRelationEmail());
		values.put(DB.TABLES.CONTACT.FIELDS.DOCTORID, contact.getDoctorID());

		dbHelper.insert(DB.TABLES.CONTACT.TABLENAME, values);
	}

	/**
	 * 根据主键ID删除联系人
	 */
	@Override
	public void delete(int id) {
		String sql = String.format(DB.TABLES.CONTACT.SQL.DELETE, id);
		dbHelper.execSQL(sql);
	}

	/**
	 * 删除所有联系人
	 */
	@Override
	public void deleteAll() {
		String sql = DB.TABLES.CONTACT.SQL.DROPTABLE;
		dbHelper.execSQL(sql);
		dbHelper.execSQL(DB.TABLES.CONTACT.SQL.CREATE);
	}

	/**
	 * 修改联系人实体
	 */
	@Override
	public void update(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(DB.TABLES.CONTACT.FIELDS.NAME, contact.getName());
		values.put(DB.TABLES.CONTACT.FIELDS.NAMEPINYIN, contact.getNamePinyin());
		values.put(DB.TABLES.CONTACT.FIELDS.NICKNAME, contact.getNickName());
		values.put(DB.TABLES.CONTACT.FIELDS.ADDRESS, contact.getAddress());
		values.put(DB.TABLES.CONTACT.FIELDS.COMPANY, contact.getCompany());
		values.put(DB.TABLES.CONTACT.FIELDS.BIRTHDAY, contact.getBirthday());
		values.put(DB.TABLES.CONTACT.FIELDS.NOTE, contact.getNote());
		values.put(DB.TABLES.CONTACT.FIELDS.IMAGE, contact.getImage());
		values.put(DB.TABLES.CONTACT.FIELDS.GROUPID, contact.getGroupId());
		values.put(DB.TABLES.CONTACT.FIELDS.IDNO, contact.getIdNo());
		values.put(DB.TABLES.CONTACT.FIELDS.DESCRIBE, contact.getDescribe());
		values.put(DB.TABLES.CONTACT.FIELDS.GENDER, contact.getGender());
		values.put(DB.TABLES.CONTACT.FIELDS.TYPE, contact.getType());
		values.put(DB.TABLES.CONTACT.FIELDS.FILENO, contact.getFileNo());
		values.put(DB.TABLES.CONTACT.FIELDS.HOSPITAL, contact.getHospital());
		values.put(DB.TABLES.CONTACT.FIELDS.RELATIONNAME, contact.getRelationName());
		values.put(DB.TABLES.CONTACT.FIELDS.RELATION, contact.getRelation());
		values.put(DB.TABLES.CONTACT.FIELDS.RELATIONTELE, contact.getRelationTele());
		values.put(DB.TABLES.CONTACT.FIELDS.RELATIONEMAIL, contact.getRelationEmail());
		values.put(DB.TABLES.CONTACT.FIELDS.DOCTORID, contact.getDoctorID());

		dbHelper.update(DB.TABLES.CONTACT.TABLENAME, values,
				DB.TABLES.CONTACT.FIELDS.ID + "= ? ",
				new String[] { contact.getId() + "" });
	}

	/**
	 * 根据条件查找联系人
	 */
	@Override
	public List<Contact> getContactsByCondition(String condition) {
		String sql = String.format(DB.TABLES.CONTACT.SQL.SELECT, condition);
		Cursor cursor = dbHelper.rawQuery(sql);

		List<Contact> contacts = new ArrayList<Contact>();
		while (cursor.moveToNext()) {
			Contact contact = new Contact();
			contact.setId(cursor.getInt(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.ID)));
			contact.setName(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.NAME)));
			contact.setNamePinyin(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.NAMEPINYIN)));
			contact.setNickName(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.NICKNAME)));
			contact.setAddress(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.ADDRESS)));
			contact.setCompany(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.COMPANY)));
			contact.setBirthday(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.BIRTHDAY)));
			contact.setNote(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.NOTE)));
			contact.setImage(cursor.getBlob(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.IMAGE)));
			contact.setGroupId(cursor.getInt(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.GROUPID)));
			contact.setIdNo(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.IDNO)));
			contact.setDescribe(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.DESCRIBE)));
			contact.setGender(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.GENDER)));
			contact.setType(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.TYPE)));
			contact.setFileNo(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.FILENO)));
			contact.setHospital(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.HOSPITAL)));
			contact.setRelationName(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.RELATIONNAME)));
			contact.setRelation(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.RELATION)));
			contact.setRelationTele(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.RELATIONTELE)));
			contact.setRelationEmail(cursor.getString(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.RELATIONEMAIL)));
			contact.setDoctorID(cursor.getInt(cursor
					.getColumnIndex(DB.TABLES.CONTACT.FIELDS.DOCTORID)));
			contacts.add(contact);
		}
		dbHelper.close();
		return contacts;
	}

	/**
	 * 改变联系人分组
	 */
	@Override
	public void changeGroup(String sql) {
		dbHelper.execSingle(sql);
	}

}
