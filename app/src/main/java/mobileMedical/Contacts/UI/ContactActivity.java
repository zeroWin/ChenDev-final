package mobileMedical.Contacts.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Manager.EmailManager;
import mobileMedical.Contacts.Manager.GroupManager;
import mobileMedical.Contacts.Manager.IMManager;
import mobileMedical.Contacts.Manager.MsgManager;
import mobileMedical.Contacts.Manager.TelManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Model.Email;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.Contacts.View.MyPopupWindow;
import mobileMedical.namespace.R;

public class ContactActivity extends Activity {
	private Context context;// 当前上下文对象
	private ImageButton imbDownContact, imbNewContact;// 选择分组、新建联系人
	private Button btnGroupMgr;// 联系人管理按钮
	private TextView txtContactTool;
	private List<String> groupNames;// 分组名称的集合
	private String[] mItems;// 分组名称的数据
	private List<Contact> contacts;
	private ContactManager contactMgr;
	private GroupManager groupMgr;
	private EmailManager emailMgr;
	private IMManager imMgr;
	private TelManager telMgr;
	private MsgManager msgMgr;
	private ListView lsvContact, lsvGroup;
	private MyPopupWindow popupWindow;// 自定义PopupWindow弹出选择分组列表
	private RelativeLayout rlContactTool;
	private int groupPosition = 0;// 记录当前选中的分组位置
	private EditText edtSearchContact;
	private int nowGroupPosition = 0;// 当前分组位置
	// 删除联系人的相关字段
	private boolean isContactMgr = false;// true:联系人管理界面,false:联系人列表界面
	private Map<Integer, Boolean> isChecked;// 标记联系人的CheckBox是否选中
	private Menu myMenu;// 菜单
	private int ckbCount = 0;// 当前选中联系人个数
	private int nowPosMenu;// 标记当前使用上下文菜单的项的Position
	private AlertDialog dialog;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			toNotContactMgrMenu();// 跳转到主界面
			CommonUtil.Toast(context, "批量删除成功!");
			dialog.dismiss();
		}
	};

	public static final int REQUEST_CONTACT_ITEM_CLICK = 1;
	public static final int REQUEST_NEW_CONTACT_CLICK = 2;
	public static final int REQUEST_GROUP_MGR_CLICK = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity_contact);

		init();// 初始化数据

		registerForContextMenu(lsvContact);// 注册上下文菜单

		// 选择分组
		rlContactTool.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				check();// 初始化PopupWindow
				refleshLsvGroup();

				imbDownContact.setBackgroundResource(R.drawable.contact_spinner_down);
				popupWindow.showAsDropDown(findViewById(R.id.rlToolbar01), 80,
						-10);
				CommonUtil.Log("sqy", "MainActivity", "showAsDropDown", 'i');
				lsvGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// CommonUtil.Toast(context, arg2 + "," + arg3);
						nowGroupPosition = arg2;
						txtContactTool.setText(mItems[arg2]);
						groupPosition = arg2;
						refleshLsvContact();
						popupWindow.dismiss();
					}
				});
				btnGroupMgr.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context,
								GroupMgrActivity.class);
						startActivityForResult(intent, REQUEST_GROUP_MGR_CLICK);
						popupWindow.dismiss();
					}
				});
			}
		});

		// 新建联系人
		imbNewContact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewContactActivity.class);
				startActivityForResult(intent, REQUEST_NEW_CONTACT_CLICK);
				isContactMgr = false;
				if (myMenu != null) {
					myMenu.getItem(0).setTitle("批量管理");
					myMenu.getItem(1).setEnabled(false);
					myMenu.getItem(2).setEnabled(false);
					myMenu.getItem(3).setEnabled(false);
				}
			}
		});

		// 每项联系人的监听
		lsvContact
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						CommonUtil.Log("sqy", "ContactActivity",
								"lsvContact.setOnItemClickListener:id=" + id
										+ ",position=" + position, 'i');
						int contactId = (int) id;
						// 显示联系人详细信息并可修改联系人信息
						if (!isContactMgr) {
							Intent intent = new Intent(context,
									NewContactActivity.class);
							intent.putExtra("contactId", contactId);
							startActivityForResult(intent,
									REQUEST_CONTACT_ITEM_CLICK);
						}
						// 联系人管理，可批量删除
						else {
							CheckBox ckb = (CheckBox) view
									.findViewById(R.id.ckbContact);
							if (ckb.isChecked()) {
								ckb.setChecked(false);
								isChecked.put(contactId, false);
								ckbCount--;
							} else {
								ckb.setChecked(true);
								isChecked.put(contactId, true);
								ckbCount++;
							}
						}
					}
				});
		// 搜索联系人监听
		edtSearchContact.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str = s.toString();
				if (s.equals(""))
					refleshLsvContact();
				else {
					if (groupPosition == 0)// 当前分组为"全部联系人"
						//contacts = contactMgr.getContactsByNamePinyin(str);
					    contacts = contactMgr.getContactsByName(str);
					else if (groupPosition == groupNames.size() - 1)// 当前分组为"未分组"
						contacts = contactMgr
								.getContactsByNamePinyinAddGroupId(str, 0);
					else
						// 当前分组为新建的分组
						contacts = contactMgr
								.getContactsByNamePinyinAddGroupId(
										str,
										groupMgr.getGroupsByName(
												groupNames.get(groupPosition))
												.getGroupId());
					Collections.sort(contacts);// 按姓名拼音进行排序
					MyAdapter adapter = new MyAdapter(context);
					lsvContact.setAdapter(adapter);
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
	}

	/**
	 * 数据初始化
	 */
	@SuppressLint("UseSparseArrays")
	public void init() {
		context = this;
		contactMgr = new ContactManager(context);
		groupMgr = new GroupManager(context);
		telMgr = new TelManager(context);
		emailMgr = new EmailManager(context);
		imMgr = new IMManager(context);
		msgMgr = new MsgManager(context);
		contacts = new ArrayList<Contact>();
		initGroup();
		contacts = this.getContactsByGroupPosition();
		Collections.sort(contacts);// 按姓名拼音进行排序
		imbDownContact = (ImageButton) this.findViewById(R.id.imbDownContact);
		txtContactTool = (TextView) this.findViewById(R.id.txtContactTool);
		lsvContact = (ListView) this.findViewById(R.id.Lsv_contacts);
		imbNewContact = (ImageButton) findViewById(R.id.imbNewContact);
		rlContactTool = (RelativeLayout) findViewById(R.id.rlContactTool);
		edtSearchContact = (EditText) findViewById(R.id.edtFindContact);
		isChecked = new HashMap<Integer, Boolean>();
		MyAdapter adapter = new MyAdapter(context);
		lsvContact.setAdapter(adapter);
	}

	/**
	 * 初始化分组
	 */
	public void initGroup() {
		groupNames = new ArrayList<String>();
		groupNames.add("全部联系人");
		for (String groupName : groupMgr.getAllGroupName())
			groupNames.add(groupName);
		groupNames.add("未分组");
		mItems = new String[groupNames.size()];
		mItems = groupNames.toArray(mItems);
		CommonUtil.Log("sqy", "MainActivity", "groupNames.toArray()", 'i');
	}

	/**
	 * 获取当前选择分组后要显示的联系人实体集
	 * 
	 * @return 联系人实体集
	 */
	public List<Contact> getContactsByGroupPosition() {
		int groupSize = groupNames.size();
		if (groupPosition == 0)
			return contactMgr.getAllContacts();
		if (groupPosition == groupSize - 1)
			return contactMgr.getContactsByGroupId(0);
		CommonUtil.Log("sqy", "getContactsByGroupPosition",
				groupNames.get(groupPosition), 'i');
		return contactMgr.getContactsByGroupId(groupMgr.getGroupsByName(
				groupNames.get(groupPosition)).getGroupId());
	}

	/**
	 * 刷新当前联系人列表
	 */
	public void refleshLsvContact() {
		contacts = this.getContactsByGroupPosition();
		Collections.sort(contacts);// 按姓名拼音进行排序
		if (contacts.size() == 0)
			CommonUtil.Toast(context, "当前分组无联系人");
		MyAdapter adapter = new MyAdapter(context);
		lsvContact.setAdapter(adapter);
	}

	/**
	 * 刷新当前分组列表
	 */
	public void refleshLsvGroup() {
		initGroup();
		//nowGroupPosition=0;
		//groupPosition=0;
		System.out.println("===>"+groupNames.size());
		System.out.println("===>"+groupPosition);
		System.out.println("===>"+nowGroupPosition);
		txtContactTool.setText(mItems[nowGroupPosition]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.contact_vlist_group, mItems);
		lsvGroup.setAdapter(adapter);
	}

	/*
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "ContactActivity", "onCreateContextMenu", 'i');
		menu.add(0, 10, 0, "删除联系人");
		menu.add(0, 11, 1, "编辑联系人");
		menu.add(0, 12, 2, "新建联系人");
	}

	/*
	 * 响应上下文菜单和菜单
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();// 存放当前点击项的信息
		CommonUtil.Log("menu", "ContactActivity", "onMenuItemSelected", 'i');
		if (featureId == 6)// 表明是上下文菜单
			nowPosMenu = info.position;
		switch (item.getItemId()) {
		case 10:// 删除联系人
			new AlertDialog.Builder(context)
					.setTitle("警告")
					.setMessage("您确定要删除联系人吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									contactMgr.delContact(contacts.get(
											nowPosMenu).getId());
									telMgr.delTelByContactId(contacts.get(
											nowPosMenu).getId());
									emailMgr.delEmailByContactId(contacts.get(
											nowPosMenu).getId());
									imMgr.delIMByContactId(contacts.get(
											nowPosMenu).getId());
									refleshLsvContact();
									CommonUtil.Toast(context, "删除联系人成功!");
								}
							}).setNegativeButton("取消", null).create().show();
			break;
		case 11:// 编辑联系人
			Intent intent = new Intent(context, NewContactActivity.class);
			intent.putExtra("contactId", contacts.get(nowPosMenu).getId());
			startActivityForResult(intent, REQUEST_CONTACT_ITEM_CLICK);
			break;
		case 12:// 新建联系人
			Intent intent1 = new Intent(context, NewContactActivity.class);
			startActivityForResult(intent1, REQUEST_NEW_CONTACT_CLICK);
			isContactMgr = false;
			if (myMenu != null) {
				myMenu.getItem(0).setTitle("批量管理");
				myMenu.getItem(1).setEnabled(false);
				myMenu.getItem(2).setEnabled(false);
				myMenu.getItem(3).setEnabled(false);
			}
			break;

		case 1:// 批量管理
			if (isContactMgr) {
				toNotContactMgrMenu();
			} else {
				toIsContactMgrMenu();
			}
			break;
		case 2:// 全选
			for (Contact contact : contacts)
				isChecked.put(contact.getId(), true);
			refleshLsvContact();
			ckbCount = isChecked.size();
			break;
		case 3:// 全消
			for (Contact contact : contacts)
				isChecked.put(contact.getId(), false);
			refleshLsvContact();
			ckbCount = 0;
			break;
		case 4:// 批量删除
			if (ckbCount > 0) {
				LayoutInflater inflater = LayoutInflater.from(context);
				View view = inflater.inflate(R.layout.contact_progress_dialog, null);
				((TextView) view.findViewById(R.id.progress_msg))
						.setText(" 删 除 中 . . .");
				dialog = new AlertDialog.Builder(context).setView(view)
						.create();
				new AlertDialog.Builder(context)
						.setTitle("警告")
						.setMessage("您确定要删除选中的联系人吗?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										ContactActivity.this.dialog.show();
										new Thread(new Runnable() {

											@Override
											public void run() {
												try {
													Thread.sleep(500);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
												for (Contact contact : contacts)
													if (isChecked.get(contact
															.getId())) {
														contactMgr
																.delContact(contact
																		.getId());
														telMgr.delTelByContactId(contact
																.getId());
														emailMgr.delEmailByContactId(contact
																.getId());
														imMgr.delIMByContactId(contact
																.getId());
													}
												handler.sendMessage(new Message());
											}
										}).start();
									}
								}).setNegativeButton("取消", null).create()
						.show();
			} else
				CommonUtil.Toast(context, "当前无选中项!");
			break;
		}
		return true;
	}

	private void toIsContactMgrMenu() {
		isContactMgr = true;
		myMenu.getItem(0).setTitle("取消管理");
		myMenu.getItem(1).setEnabled(true);
		myMenu.getItem(2).setEnabled(true);
		myMenu.getItem(3).setEnabled(true);
		refleshLsvContact();
		// 初始化当前界面所有联系人的选择框状态为false
		for (Contact contact : contacts)
			isChecked.put(contact.getId(), false);
	}

	private void toNotContactMgrMenu() {
		isContactMgr = false;
		myMenu.getItem(0).setTitle("批量管理");
		refleshLsvContact();
		myMenu.getItem(1).setEnabled(false);
		myMenu.getItem(2).setEnabled(false);
		myMenu.getItem(3).setEnabled(false);
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		CommonUtil.Log("menu", "ContactActivity", "onCreateOptionsMenu", 'i');
		menu.add(1, 1, 1, "批量管理").setIcon(R.drawable.contact_ic_menu_bachmanager);
		menu.add(1, 2, 2, "全选").setIcon(R.drawable.contact_ic_menu_select_all)
				.setEnabled(false);
		menu.add(1, 3, 3, "全消").setIcon(R.drawable.contact_ic_menu_unselect)
				.setEnabled(false);
		menu.add(1, 4, 4, "批量删除").setIcon(android.R.drawable.ic_menu_delete)
				.setEnabled(false);
		myMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Activity回调刷新界面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GROUP_MGR_CLICK) {
			refleshLsvGroup();
			refleshLsvContact();
			edtSearchContact.setText("");
		}
		if (requestCode == REQUEST_NEW_CONTACT_CLICK
				&& resultCode == NewContactActivity.RESULT_BTN_SAVE) {
			edtSearchContact.setText("");
			refleshLsvContact();
		}
		if (requestCode == REQUEST_CONTACT_ITEM_CLICK
				&& resultCode == NewContactActivity.RESULT_BTN_SAVE) {
			edtSearchContact.setText("");
			refleshLsvContact();
		}
	}

	/**
	 * 监听系统按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isContactMgr) {
			toNotContactMgrMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 自定义适配器
	 * 
	 * @author sqy
	 * 
	 */
	class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private View popView;
		private PopupWindow mPopupWindow;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			edtSearchContact.setHint("联系人搜索 | 共" + contacts.size() + "人");
			return contacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return contacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return contacts.get(arg0).getId();
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			arg1 = inflater.inflate(R.layout.contact_vlist_contact, null);
			ImageView imgPhoto = (ImageView) arg1.findViewById(R.id.imgPhoto);
			TextView txtName = (TextView) arg1.findViewById(R.id.txtName);
			final TextView txtTel = (TextView) arg1.findViewById(R.id.txtTel);
			CheckBox ckbContact = (CheckBox) arg1.findViewById(R.id.ckbContact);

			final Contact contact = contacts.get(arg0);
			CommonUtil.Log("sqy", "getView", arg0 + "," + contacts.size(), 'i');
			imgPhoto.setImageBitmap(ImageTools.getBitmapFromByte(contact
					.getImage()));
			txtName.setText(contact.getName());
			List<String> tels = telMgr
					.getTelNumbersByContactId(contact.getId());
			CommonUtil.Log("sqy", "getView", tels.size() + ".......", 'i');
			if (tels.size() > 0) {
				String tel = tels.get(0);
				txtTel.setText(tel);
			} else
				txtTel.setText("无");
			CommonUtil.Log("sqy", "getView", "123123", 'i');
			if (isContactMgr) {
				ckbContact.setVisibility(View.VISIBLE);
				// 设置CheckBox状态
				if (isChecked.get(contact.getId()))
					ckbContact.setChecked(true);
				else
					ckbContact.setChecked(false);
			} else {
				ckbContact.setVisibility(View.GONE);
				// 快捷打电话、发短信、发邮件的监听
				imgPhoto.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mPopupWindow == null) {
							popView = inflater.inflate(
									R.layout.contact_popup_send_msgs, null);
							mPopupWindow = new PopupWindow(popView,
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT, true);

							mPopupWindow.setFocusable(true);
							mPopupWindow.setTouchable(true);
							mPopupWindow
									.setBackgroundDrawable(new BitmapDrawable());

						}
						if (mPopupWindow.isShowing())
							mPopupWindow.dismiss();

						mPopupWindow.showAsDropDown(v);

						ImageButton imbCall = (ImageButton) popView
								.findViewById(R.id.imbCall);
						ImageButton imbMsg = (ImageButton) popView
								.findViewById(R.id.imbMsg);
						ImageButton imbEmail = (ImageButton) popView
								.findViewById(R.id.imbEmail);
						// 打电话
						imbCall.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								String num = txtTel.getText().toString();
								CommonUtil.dial(context, num);// 打电话
								mPopupWindow.dismiss();
							}
						});
						// 发短信
						imbMsg.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								String num = txtTel.getText().toString();
								int threadId = msgMgr
										.getThreadIdByTelNumber(num);
								Intent intent = new Intent(context,
										MsgEditActivity.class);
								intent.putExtra("threadId", threadId);
								intent.putExtra("contactId", contact.getId());
								startActivity(intent);
								mPopupWindow.dismiss();
							}
						});
						// 发邮件
						imbEmail.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								List<Email> emails = emailMgr
										.getEmailsByContactId(contacts
												.get(arg0).getId());
								CommonUtil.sendEmail(context, emails);// 发邮件
								mPopupWindow.dismiss();
							}
						});
					}
				});
			}
			return arg1;
		}
	}

	/**
	 * 初始化popupWindow
	 */
	private void check() {
		if (popupWindow == null) {
			View view = getLayoutInflater().inflate(R.layout.contact_popup_selectgroup,
					null);
			lsvGroup = (ListView) view.findViewById(R.id.lsvGroup);
			btnGroupMgr = (Button) view.findViewById(R.id.btnGroupMgr);
			popupWindow = new MyPopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, imbDownContact);
			// 下面三个设置可以使PopupWindow中的项和外面的键都可以响应点击或触摸事件
			popupWindow.setFocusable(true);
			popupWindow.setTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());

		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}
}
