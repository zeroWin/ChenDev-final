package mobileMedical.Contacts.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Manager.EmailManager;
import mobileMedical.Contacts.Manager.MsgManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Model.Msg;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.Contacts.Tool.ContantsUtil;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.namespace.R;

public class MessageActivity extends Activity {
	private ImageButton imbNewMsg;
	private Context context;
	private List<Msg> msgs;// 接收每个联系人最新的一条短信
	private LayoutInflater inflater;
	private ListView lsvMsg;
	// 各Manager
	private ContactManager contactMgr;
	private MsgManager msgMgr;
	private EmailManager emailMgr;
	// 信息管理
	private Menu myMenu;// 菜单
	private boolean isMessageMgr = false;// true:信息管理界面,false:信息列表界面
	private Map<Integer, Boolean> isChecked;// 标记信息的每项的CheckBox是否选中
	private int nowPosMenu;// 标记当前使用上下文菜单的项的Position
	private int ckbCount = 0;// 当前选中信息个数
	private boolean flag = false;// 标记当前点击到对话列表前是否有未读短信
	private AlertDialog dialog;

	private ComposeObserver composeObserver = null;// 系统数据库监听

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if ((Integer) msg.obj == 1) {
				CommonUtil.Toast(context, "批量删除对话成功!");
				toNotMessageMgrMenu();// 跳转到主界面
			}
			if ((Integer) msg.obj == 2) {
				refreshMsg();
				CommonUtil.Toast(context, "删除对话成功!");
			}
			dialog.dismiss();
		}

	};

	public static final int REQUEST_MSG_EDIT = 1;
	public static final int REQUEST_MSG_NEW_CONTACT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity_message);

		init();// 初始化
		registerForContextMenu(lsvMsg);
		registerSMSObserver();// 注册系统数据库监听,看是否有新信息

		// 跳转到信息编辑界面
		lsvMsg.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!isMessageMgr) {
					if (msgs.get(position).getUnreadCount() > 0)
						flag = true;
					Intent intent = new Intent(context, MsgEditActivity.class);
					intent.putExtra("threadId", msgs.get(position)
							.getThreadId());
					intent.putExtra("flag", flag);
					startActivityForResult(intent, REQUEST_MSG_EDIT);
					flag = false;
				} else {
					CheckBox ckbMsg = (CheckBox) view
							.findViewById(R.id.thread_list_item_message_checkbox);
					if (ckbMsg.isChecked()) {
						ckbMsg.setChecked(false);
						isChecked.put(msgs.get(position).getThreadId(), false);
						ckbCount--;
					} else {
						ckbMsg.setChecked(true);
						isChecked.put(msgs.get(position).getThreadId(), true);
						ckbCount++;
					}
				}
			}
		});

		imbNewMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewMsgActivity.class);
				startActivity(intent);

				isMessageMgr = false;
				if (myMenu != null) {
					myMenu.getItem(0).setTitle("批量管理");
					myMenu.getItem(1).setEnabled(false);
					myMenu.getItem(2).setEnabled(false);
					myMenu.getItem(3).setEnabled(false);
					myMenu.getItem(4).setEnabled(false);
				}
			}
		});
	}

	/**
	 * Activity的初始化
	 */
	@SuppressLint("UseSparseArrays")
	private void init() {
		context = this;
		inflater = LayoutInflater.from(context);
		imbNewMsg = (ImageButton) findViewById(R.id.imbNewMessage);
		lsvMsg = (ListView) findViewById(R.id.lsvMessage);
		contactMgr = new ContactManager(context);
		msgMgr = new MsgManager(context);
		emailMgr = new EmailManager(context);
		refreshMsg();
		isChecked = new HashMap<Integer, Boolean>();
		for (Msg msg : msgs)
			isChecked.put(msg.getThreadId(), false);
		composeObserver = new ComposeObserver(new Handler());
	}

	/**
	 * 刷新界面
	 */
	private void refreshMsg() {
		msgs = new ArrayList<Msg>();
		msgs = msgMgr.getEveryContactNo1Msg();
		MsgUiAdapter adapter = new MsgUiAdapter();
		lsvMsg.setAdapter(adapter);
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		CommonUtil.Log("menu", "MessageActivity", "onCreateOptionsMenu", 'i');
		menu.add(1, 1, 1, "批量管理").setIcon(R.drawable.contact_menu_batch_mgr);
		menu.add(1, 2, 2, "全选").setIcon(R.drawable.contact_menu_batch_all)
				.setEnabled(false);
		menu.add(1, 3, 3, "全消").setIcon(R.drawable.contact_menu_batch_cancel)
				.setEnabled(false);
		menu.add(1, 4, 4, "已读").setIcon(R.drawable.contact_menu_all_read)
				.setEnabled(false);
		menu.add(1, 5, 5, "批量删除").setIcon(R.drawable.contact_menu_batch_delete)
				.setEnabled(false);
		myMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "MessageActivity", "onCreateContextMenu", 'i');
		menu.add(0, 10, 0, "拨打电话");
		menu.add(0, 11, 1, "删除");
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
		case 1:// 批量管理
			if (isMessageMgr) {
				toNotMessageMgrMenu();
			} else {
				toIsMessageMgrMenu();
			}
			break;
		case 2:// 全选
			for (Msg msg : msgs)
				isChecked.put(msg.getThreadId(), true);
			refreshMsg();
			ckbCount = isChecked.size();
			break;
		case 3:// 全消
			for (Msg msg : msgs)
				isChecked.put(msg.getThreadId(), false);
			refreshMsg();
			ckbCount = 0;
			break;
		case 4:// 已读
			if (ckbCount > 0) {
				for (Msg msg : msgs)
					if (isChecked.get(msg.getThreadId()))
						msgMgr.modifyMsgRead(1, msg.getThreadId());
				refreshMsg();
				CommonUtil.Toast(context, "标记已读成功!");
			} else {
				CommonUtil.Toast(context, "当前无选中项!");
			}
			toNotMessageMgrMenu();
			break;
		case 5:// 批量删除
			if (ckbCount > 0) {
				View view = inflater.inflate(R.layout.contact_progress_dialog, null);
				((TextView) view.findViewById(R.id.progress_msg))
						.setText(" 删 除 中 . . .");
				dialog = new AlertDialog.Builder(context).setView(view)
						.create();
				new AlertDialog.Builder(context)
						.setTitle("警告")
						.setMessage("您确定要删除选中的对话吗?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										MessageActivity.this.dialog.show();
										new Thread(new Runnable() {

											@Override
											public void run() {
												try {
													Thread.sleep(700);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
												for (Msg msg : msgs)
													if (isChecked.get(msg
															.getThreadId())) {
														msgMgr.delMsgByThreadId(msg
																.getThreadId());
													}
												Message message = new Message();
												message.obj = 1;
												handler.sendMessage(message);
											}
										}).start();
									}
								}).setNegativeButton("取消", null).create()
						.show();
			} else
				CommonUtil.Toast(context, "当前无选中项!");
			break;
		case 10:// 打电话
			String num = msgs.get(nowPosMenu).getAddress();
			CommonUtil.dial(context, num);
			break;
		case 11:// 删除对话
			View view1 = inflater.inflate(R.layout.contact_progress_dialog, null);
			((TextView) view1.findViewById(R.id.progress_msg))
					.setText(" 删 除 中 . . .");
			dialog = new AlertDialog.Builder(context).setView(view1).create();
			new AlertDialog.Builder(context)
					.setTitle("警告")
					.setMessage("确定删除此对话??")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MessageActivity.this.dialog.show();
									new Thread(new Runnable() {

										@Override
										public void run() {
											try {
												Thread.sleep(500);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											msgMgr.delMsgByThreadId(msgs.get(
													nowPosMenu).getThreadId());
											Message message = new Message();
											message.obj = 2;
											handler.sendMessage(message);
										}
									}).start();
								}
							}).setNegativeButton("取消", null).create().show();

			break;
		}
		return true;
	}

	private void toIsMessageMgrMenu() {
		isMessageMgr = true;
		myMenu.getItem(0).setTitle("取消管理");
		refreshMsg();
		myMenu.getItem(1).setEnabled(true);
		myMenu.getItem(2).setEnabled(true);
		myMenu.getItem(3).setEnabled(true);
		myMenu.getItem(4).setEnabled(true);
		for (Msg msg : msgs)
			isChecked.put(msg.getThreadId(), false);
	}

	private void toNotMessageMgrMenu() {
		isMessageMgr = false;
		myMenu.getItem(0).setTitle("批量管理");
		refreshMsg();
		myMenu.getItem(1).setEnabled(false);
		myMenu.getItem(2).setEnabled(false);
		myMenu.getItem(3).setEnabled(false);
		myMenu.getItem(4).setEnabled(false);
	}

	/**
	 * 回调刷新界面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_MSG_NEW_CONTACT
				&& resultCode == NewContactActivity.RESULT_BTN_SAVE)
			refreshMsg();
		if (requestCode == REQUEST_MSG_EDIT
				&& resultCode == MsgEditActivity.RESULT_MSG_CHANGED)
			refreshMsg();
		if (resultCode == NewMsgActivity.RESULT_SEND_MESG_SUCCEED)
			refreshMsg();
	}

	/**
	 * 监听系统按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		CommonUtil.Log("sqy", "onKeyDown+++", "onKeyDown", 'i');
		if (keyCode == KeyEvent.KEYCODE_BACK && isMessageMgr) {
			toNotMessageMgrMenu();
			CommonUtil.Log("menu", "keyCode", 222 + "", 'i');
			return true;
		}
		CommonUtil.Log("menu", "keyCode", "111", 'i');
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 自定义Adapter,显示信息列表
	 * 
	 * @author dell
	 * 
	 */
	class MsgUiAdapter extends BaseAdapter {
		private PopupWindow mPopupWindow;
		private View popView;

		@Override
		public int getCount() {
			return msgs.size();
		}

		@Override
		public Object getItem(int position) {
			return msgs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.contact_vlist_message, null);
			QuickContactBadge qcbImage = (QuickContactBadge) convertView
					.findViewById(R.id.thread_list_item_photoview);
			TextView txtUnReadMsgCount = (TextView) convertView
					.findViewById(R.id.thread_list_item_message_count);
			TextView txtMsgName = (TextView) convertView
					.findViewById(R.id.thread_list_item_message_name);
			TextView txtMsgContent = (TextView) convertView
					.findViewById(R.id.thread_list_item_message_summary);
			TextView txtMsgTime = (TextView) convertView
					.findViewById(R.id.thread_list_item_time);
			CheckBox ckbMsg = (CheckBox) convertView
					.findViewById(R.id.thread_list_item_message_checkbox);
			final Msg msg = msgs.get(position);
			// 显示姓名和短信数量
			if (msg.getPerson().equals(""))
				txtMsgName.setText(msg.getAddress() + " (" + msg.getMsgCount()
						+ ")");
			else
				txtMsgName.setText(msg.getPerson() + " (" + msg.getMsgCount()
						+ ")");
			// 显示短信内容
			if (msg.getContent().length() > 12)
				txtMsgContent
						.setText(msg.getContent().substring(0, 12) + "...");
			else
				txtMsgContent.setText(msg.getContent());
			// 显示日期
			String date = CommonUtil.getNowDate();
			if (msg.getMsgDate().substring(0, 10).equals(date))
				txtMsgTime.setText(msg.getMsgDate().substring(11));
			else
				txtMsgTime.setText(msg.getMsgDate().substring(0, 10));
			// 设置显示未读短信条数
			if (msg.getUnreadCount() > 0) {
				txtUnReadMsgCount.setVisibility(View.VISIBLE);
				txtUnReadMsgCount.setText(msg.getUnreadCount() + "");
			} else {
				txtUnReadMsgCount.setVisibility(View.GONE);
			}
			// 设置头像
			if (!msg.getPerson().equals("")) {// 响应头像点击事件
				final List<Contact> list = contactMgr.getContactsByName(msg
						.getPerson());
				byte[] image = list.get(0).getImage();
				Bitmap bitmap = ImageTools.getBitmapFromByte(image);
				qcbImage.setImageBitmap(bitmap);
				qcbImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// CommonUtil.Toast(context, "弹出快捷菜单");
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

						mPopupWindow.showAsDropDown(v, 50, -25);

						ImageButton imbCall = (ImageButton) popView
								.findViewById(R.id.imbCall);
						ImageButton imbMsg = (ImageButton) popView
								.findViewById(R.id.imbMsg);
						ImageButton imbEmail = (ImageButton) popView
								.findViewById(R.id.imbEmail);
						ImageButton imbEditContact = (ImageButton) popView
								.findViewById(R.id.imbEditContact);
						// 打电话
						imbCall.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								CommonUtil.dial(context, msg.getAddress());// 打电话
								mPopupWindow.dismiss();
							}
						});
						// 发短信
						imbMsg.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								if (msg.getUnreadCount() > 0)
									flag = true;
								Intent intent = new Intent(context,
										MsgEditActivity.class);
								intent.putExtra("threadId", msg.getThreadId());
								intent.putExtra("flag", flag);
								startActivityForResult(intent, REQUEST_MSG_EDIT);
								flag = false;
								mPopupWindow.dismiss();
							}
						});
						// 发邮件
						imbEmail.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								CommonUtil.sendEmail(
										context,
										emailMgr.getEmailsByContactId(list.get(
												0).getId()));// 发邮件
								mPopupWindow.dismiss();
							}
						});
						// 查看联系人详情
						imbEditContact.setVisibility(View.VISIBLE);
						imbEditContact
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										Intent intent = new Intent(context,
												NewContactActivity.class);
										intent.putExtra("contactId", list
												.get(0).getId());
										startActivityForResult(intent,
												REQUEST_MSG_NEW_CONTACT);
										mPopupWindow.dismiss();
									}
								});
					}
				});
			} else {// 新建联系人
				qcbImage.setImageResource(R.drawable.contact_no_contact_photo);
				qcbImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(context)
								.setTitle("添加联系人")
								.setMessage(
										"将'" + msg.getAddress() + "'添加到联系人?")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												Intent intent = new Intent(
														MessageActivity.this,
														NewContactActivity.class);
												intent.putExtra("tel",
														msg.getAddress());
												startActivityForResult(intent,
														REQUEST_MSG_NEW_CONTACT);
											}
										}).setNegativeButton("取消", null)
								.create().show();
					}
				});
			}
			if (isMessageMgr) {
				ckbMsg.setVisibility(View.VISIBLE);
				if (isChecked.get(msg.getThreadId()))
					ckbMsg.setChecked(true);
				else
					ckbMsg.setChecked(false);
			} else {
				ckbMsg.setVisibility(View.GONE);
			}
			return convertView;
		}
	}

	/**
	 * 注册observer
	 */
	private void registerSMSObserver() {
		this.getContentResolver().registerContentObserver(
				Uri.parse(ContantsUtil.SMS_URI_ALL), true, composeObserver);

	}

	/**
	 * 数据库监听
	 * 
	 * @author Administrator
	 * 
	 */

	class ComposeObserver extends ContentObserver {
		public ComposeObserver(Handler handler) {
			super(handler);
		}

		public void onChange(boolean selfChange) {
			refreshMsg();// 刷新界面
			//CommonUtil.Log("sqy", "ComposeObserver", "====>refreshMsg()", 'i');
		}
	}

}
