package mobileMedical.Contacts.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Manager.GroupManager;
import mobileMedical.Contacts.Manager.TelManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.namespace.R;

public class AddRemoveContactToGroupActivity extends Activity implements
		OnClickListener {
	private TextView txtTitle;
	private ImageButton imbBack;
	private EditText edtSearch;
	private ListView lsvContacts;
	private Button btnSure, btnSelect;
	private List<Contact> contacts;
	private Map<Integer, Boolean> isChecked;
	private ContactManager contactMgr;
	private Context context;
	private TelManager telMgr;
	private int nowGroupId;// 记录当前操作的分组ID号
	private int checkedCount = 0;
	private GroupManager groupMgr;
	private boolean isAdd, isNewMsg;// true:添加联系人到分组;false:从分组移除联系人
	private int showGroupId;// 当前界面要显示的联系人的分组ID号

	public static final int RESULT_BTN_SURE = 1;
	public static final int RESULT_BTN_BACK = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity_add_contact_to_group);

		init();
		if (contacts.size() == 0) {
			if (isAdd)
				CommonUtil.Toast(context, "当前无可添加的联系人！");
			else
				CommonUtil.Toast(context, "此分组无联系人可移除！");
		}
		MyAdapter2 adapter = new MyAdapter2(context);
		lsvContacts.setAdapter(adapter);

		imbBack.setOnClickListener(this);// 返回监听事件
		btnSure.setOnClickListener(this);// 确定监听事件
		btnSelect.setOnClickListener(this);// 选择监听事件
		// 查找框监听事件
		edtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = s.toString();
				if (s.equals(""))
					refleshLsvContact();
				else {
					contacts = contactMgr.getContactsByNamePinyinAddGroupId(
							str, showGroupId);
					Collections.sort(contacts);// 按姓名拼音进行排序
					MyAdapter2 adapter = new MyAdapter2(context);
					lsvContacts.setAdapter(adapter);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		lsvContacts
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						int contactId = (int) id;
						CheckBox ckb = (CheckBox) view
								.findViewById(R.id.ckbContact);
						if (ckb.isChecked()) {
							ckb.setChecked(false);
							isChecked.put(contactId, false);
							checkedCount = checkedCount - 1;
							btnSure.setText("确定(" + checkedCount + ")");
						} else {
							ckb.setChecked(true);
							isChecked.put(contactId, true);
							checkedCount = checkedCount + 1;
							btnSure.setText("确定(" + checkedCount + ")");
						}
						if (checkedCount == isChecked.size())
							btnSelect.setText("取消全部");
						else
							btnSelect.setText("选择全部");
					}
				});
	}

	/**
	 * 刷新界面
	 */
	protected void refleshLsvContact() {
		contacts = contactMgr.getContactsByGroupId(showGroupId);
		Collections.sort(contacts);// 按姓名拼音进行排序
		MyAdapter2 adapter = new MyAdapter2(context);
		lsvContacts.setAdapter(adapter);
	}

	/**
	 * Ativity的初始化
	 */
	@SuppressLint("UseSparseArrays")
	private void init() {
		this.context = this;
		nowGroupId = getIntent().getIntExtra("groupId", 0);
		isAdd = getIntent().getBooleanExtra("isAdd", true);
		isNewMsg = getIntent().getBooleanExtra("isNewMsg", false);
		txtTitle = (TextView) findViewById(R.id.txtAddContactToGroup_Title);
		imbBack = (ImageButton) findViewById(R.id.imbAddContactToGroup_Back);
		edtSearch = (EditText) findViewById(R.id.edtAddContactToGroup_Search);
		lsvContacts = (ListView) findViewById(R.id.Lsv_contacts_group);
		btnSure = (Button) findViewById(R.id.btnSure_group);
		btnSelect = (Button) findViewById(R.id.btnSelectAllOrUnselectAll);
		contacts = new ArrayList<Contact>();
		contactMgr = new ContactManager(context);
		groupMgr = new GroupManager(context);
		telMgr = new TelManager(context);
		isChecked = new HashMap<Integer, Boolean>();
		if (!isNewMsg) {
			if (isAdd) {
				showGroupId = 0;
				contacts = contactMgr.getContactsByGroupId(showGroupId);// 得到所有未分组的联系人
				CommonUtil.Log("sqy", "AddRemoveContactToGroupActivity",
						"nowGroupId=" + nowGroupId, 'i');
				txtTitle.setText("向'"
						+ (groupMgr.getGroupById(nowGroupId).getGroupName() + "'添加联系人"));
			} else {
				showGroupId = nowGroupId;
				contacts = contactMgr.getContactsByGroupId(showGroupId);// 得到现在分组下所有的联系人
				txtTitle.setText("从'"
						+ groupMgr.getGroupById(nowGroupId).getGroupName()
						+ "'移除联系人");
			}
		} else {
			contacts = contactMgr.getAllContacts();// 得到现在分组下所有的联系人
			txtTitle.setText("添 加 收 件 人");
		}
		Collections.sort(contacts);// 按姓名拼音进行排序
		for (Contact contact : contacts)
			isChecked.put(contact.getId(), false);
		edtSearch.setHint("联系人搜索 | 共" + contacts.size() + "人");
	}

	/**
	 * OnclickListener事件监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSure_group:
			if (!isNewMsg) {
				if (isAdd) {
					for (Contact contact : contacts)
						if (isChecked.get(contact.getId())) {
							contact.setGroupId(nowGroupId);
							contactMgr.modifyContact(contact);
						}
				} else {
					for (Contact contact : contacts)
						if (isChecked.get(contact.getId())) {
							contact.setGroupId(0);
							contactMgr.modifyContact(contact);
						}
				}
				setResult(RESULT_BTN_SURE);
			} else {
				List<String> list = new ArrayList<String>();
				for (Contact contact : contacts)
					if (isChecked.get(contact.getId()))
						list.add(String.valueOf(contact.getId()));
				Intent intent = new Intent();
				String[] ids = new String[list.size()];
				ids = list.toArray(ids);
				intent.putExtra("ids", ids);
				setResult(RESULT_BTN_SURE, intent);
			}
			finish();
			break;
		case R.id.btnSelectAllOrUnselectAll:
			if (checkedCount == isChecked.size()) {// Button处于"取消全部"状态
				for (Contact contact : contacts)
					isChecked.put(contact.getId(), false);
				refleshLsvContact();
				checkedCount = 0;
				btnSure.setText("确定(" + checkedCount + ")");
				btnSelect.setText("选择全部");
			} else {// Button处于"选择全部"状态
				for (Contact contact : contacts)
					isChecked.put(contact.getId(), true);
				refleshLsvContact();
				checkedCount = isChecked.size();
				btnSure.setText("确定(" + checkedCount + ")");
				btnSelect.setText("取消全部");
			}
			break;
		case R.id.imbAddContactToGroup_Back:// 返回
			setResult(RESULT_BTN_BACK);
			finish();
			break;
		}
	}

	/**
	 * 自定义Adapter类
	 * 
	 * @author dell
	 * 
	 */
	public class MyAdapter2 extends BaseAdapter {
		private LayoutInflater inflater;

		public MyAdapter2(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return contacts.size();
		}

		@Override
		public Object getItem(int position) {
			return contacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return contacts.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.contact_vlist_contact, null);
			ImageView imgPhoto = (ImageView) convertView
					.findViewById(R.id.imgPhoto);
			TextView txtName = (TextView) convertView
					.findViewById(R.id.txtName);
			TextView txtTel = (TextView) convertView.findViewById(R.id.txtTel);
			CheckBox ckbContact = (CheckBox) convertView
					.findViewById(R.id.ckbContact);

			ckbContact.setVisibility(View.VISIBLE);

			Contact contact = contacts.get(position);

			if (isChecked.get(contact.getId()))
				ckbContact.setChecked(true);
			else
				ckbContact.setChecked(false);
			CommonUtil.Log("sqy", "MyAdapter2==>getView", position + ","
					+ contacts.size(), 'i');
			imgPhoto.setImageBitmap(ImageTools.getBitmapFromByte(contact
					.getImage()));
			txtName.setText(contact.getName());
			List<String> tels = telMgr
					.getTelNumbersByContactId(contact.getId());
			if (tels.size() > 0) {
				String tel = tels.get(0);
				txtTel.setText(tel);
			} else
				txtTel.setText("");

			return convertView;
		}
	}

	/**
	 * 拦截系统按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			setResult(RESULT_BTN_BACK);
		return super.onKeyDown(keyCode, event);
	}
}
