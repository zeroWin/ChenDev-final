package mobileMedical.Contacts.Manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.ImageView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.List;

import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.namespace.R;

public class SimAndSysContactManager {
	private static final int NAME_COLUMN = 0;
	private static final int NUMBER_COLUMN = 1;
	private static final String SIM_URI = "content://icc/adn";
	private Context context = null;
	private ContentResolver resolver = null;
	private TelephonyManager telMgr = null;
	private TelManager myTelMgr;
	private ContactManager myContactMgr;

	public SimAndSysContactManager(Context context) {
		this.context = context;
		resolver = context.getContentResolver();
		myTelMgr = new TelManager(context);
		myContactMgr = new ContactManager(context);
	}

	public TelephonyManager getTelephonyManager() {
		if (telMgr == null) {
			telMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
		}
		return telMgr;
	}

	public int getSimCardState() {
		return getTelephonyManager().getSimState();
	}

	/**
	 * 将SIM卡上的联系人导入到我的数据库
	 */
	public void insertContactToMyContactFromSIM() {
		Uri uri = Uri.parse(SIM_URI);
		Cursor cursor = resolver.query(uri, null, null, null, null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String name = cursor.getString(NAME_COLUMN);// 获取联系人姓名
			List<Contact> list = myContactMgr.getContactsByName(name);
			if (list.size() < 1) {// 联系人列表不存在此姓名的联系人,则把数据插入
				CommonUtil.Log("sqy", "insertContactToMyContactFromSIM", name,
						'i');
				// 将姓名转为首字母的拼音
				String namePinyin = "";
				HanyuPinyinCaseType caseType = HanyuPinyinCaseType.LOWERCASE;
				HanyuPinyinVCharType vcharType = HanyuPinyinVCharType.WITH_U_AND_COLON;
				HanyuPinyinToneType toneType = HanyuPinyinToneType.WITHOUT_TONE;
				HanyuPinyinOutputFormat output = new HanyuPinyinOutputFormat();
				output.setCaseType(caseType);
				output.setToneType(toneType);
				output.setVCharType(vcharType);
				String string = null;
				try {
					string = PinyinHelper.toHanyuPinyinString(name, output, "-");
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String[] strs = string.split("-");
				for (String str : strs)
					namePinyin = namePinyin + str.substring(0, 1);
				// 获取头像
				byte[] image = null;
				ImageView img = new ImageView(context);
				img.setImageResource(R.drawable.contact_bg_photo_default);
				Bitmap bitmap = ImageTools.getBitmapFromDrawable(img
						.getDrawable());
				image = ImageTools.getByteFromBitmap(bitmap);
				// 构建联系人
				Contact contact = new Contact(name, namePinyin, "", "", "", "",
						"", image, 0,"","","","","","","","","","",0);
				myContactMgr.addContact(contact);// 将联系人插入数据库

				List<Contact> list1 = myContactMgr.getContactsByName(name);
				int contactId = list1.get(list1.size() - 1).getId();
				String phoneNumber = cursor.getString(NUMBER_COLUMN);// 获取联系人电话
				CommonUtil.Log("sqy", "insertContactToMyContactFromSIM",
						phoneNumber, 'i');
				myTelMgr.addTel(contactId, "手机", phoneNumber);// 将电话插入数据库
			}
		}
		cursor.close();

	}

	/**
	 * 将系统数据库的联系人导入到我的数据库
	 */
	public void insertContactToMyContactFromSysContacts() {
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));// 获取姓名
			List<Contact> list = myContactMgr.getContactsByName(name);
			if (list.size() < 1) {// 如果我的联系人列表中没有此姓名的联系人,则插入
				// 将姓名转为首字母的拼音
				String namePinyin = "";
				HanyuPinyinCaseType caseType = HanyuPinyinCaseType.LOWERCASE;
				HanyuPinyinVCharType vcharType = HanyuPinyinVCharType.WITH_U_AND_COLON;
				HanyuPinyinToneType toneType = HanyuPinyinToneType.WITHOUT_TONE;
				HanyuPinyinOutputFormat output = new HanyuPinyinOutputFormat();
				output.setCaseType(caseType);
				output.setToneType(toneType);
				output.setVCharType(vcharType);
				String string = null;
				try {
					string = PinyinHelper.toHanyuPinyinString(name, output, "-");
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String[] strs = string.split("-");
				for (String str : strs)
					namePinyin = namePinyin + str.substring(0, 1);
				// 获取头像
				byte[] image = null;
				ImageView img = new ImageView(context);
				img.setImageResource(R.drawable.contact_bg_photo_default);
				Bitmap bitmap = ImageTools.getBitmapFromDrawable(img
						.getDrawable());
				image = ImageTools.getByteFromBitmap(bitmap);
				// 构建联系人
				Contact contact = new Contact(name, namePinyin, "", "", "", "",
						"", image, 0,"","","","","","","","","","",0);
				myContactMgr.addContact(contact);// 将联系人插入数据库
				// 获取此联系人的电话号码,并插入数据库
				String id = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));// 获取ID
				CommonUtil.Log("sqy",
						"insertContactToMyContactFromSysContacts", "Name is : "
								+ name, 'i');
				int isHas = Integer
						.parseInt(cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
				if (isHas > 0) {// 表明此联系人有电话号码
					List<Contact> list1 = myContactMgr.getContactsByName(name);
					int contactId = list1.get(list1.size() - 1).getId();// 联系人的ID号
					Cursor c = resolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + id, null, null);
					while (c.moveToNext()) {
						String number = c
								.getString(c
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						CommonUtil.Log("sqy",
								"insertContactToMyContactFromSysContacts",
								"Number is : " + number, 'i');
						myTelMgr.addTel(contactId, "手机", number);// 将电话插入数据库
					}
					c.close();
				}
			}
		}
		cursor.close();
	}

}
