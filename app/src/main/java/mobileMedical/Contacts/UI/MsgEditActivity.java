package mobileMedical.Contacts.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Manager.EmailManager;
import mobileMedical.Contacts.Manager.MsgManager;
import mobileMedical.Contacts.Manager.TelManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Model.Msg;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.Contacts.View.SendMsgService;
import mobileMedical.namespace.R;

public class MsgEditActivity extends Activity implements OnClickListener,
		OnTouchListener {
	private Context context;
	private ListView lsvItemsMsg;
	private LayoutInflater inflater;
	private ImageView imgMsgPhoto;// 联系人信息头像
	private TextView txtMsgName;// 联系人姓名,如果为陌生人则显示电话号码
	private ImageButton imbMsgCall;// 打电话按钮
	private Button btnSendMsg;// 发送消息
	private EditText edtInputMsg;// 信息编辑框
	private MsgManager msgMgr;
	private ContactManager contactMgr;
	private EmailManager emailMgr;
	private TelManager telMgr;
	private List<Msg> msgs;
	private int nowThreadId;// 用于接收主界面传过来的短信序列号
	private boolean changedFlag = false;// 标记当前页面是否有数据改变
	private PopupWindow mPopupWindow;
	private View popView;
	private boolean flag;// 接收主界面传过来的flag;
	private ViewFlipper vFlipper;
	private float touchDownX;// 左右滑动时手指按下的X坐标
	private float touchUpX;// 左右滑动时手指松开的X坐标
	private ImageView imgPhoneChoiceLeft, imgPhoneChoiceRight;
	private TextView txtCurrentTel, txtTempTel;
	private String currentNumber;// 记录当前的电话号码
	private String tempNumber;// 记录第二个号码
	private String msgContent;// 用于存储信息内容

	public static final int RESULT_MSG_CHANGED = 1;
	public static final int REQUEST_MSG_EDIT_NEW_CONTACT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity_msg_edit);

		init();// 初始化

		initUI();// 初始化界面

		imbMsgCall.setOnClickListener(this);
		btnSendMsg.setOnClickListener(this);
		registerForContextMenu(lsvItemsMsg);// 注册上下文菜单
	}

	/**
	 * Activity的初始化
	 */
	private void init() {
		this.context = this;
		lsvItemsMsg = (ListView) findViewById(R.id.lsvItemsMsg);
		imgMsgPhoto = (ImageView) findViewById(R.id.imgMsgPhoto);
		txtMsgName = (TextView) findViewById(R.id.txtMsgName);
		imbMsgCall = (ImageButton) findViewById(R.id.imbMsgCall);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		edtInputMsg = (EditText) findViewById(R.id.edtInputMsg);
		vFlipper = (ViewFlipper) findViewById(R.id.compose_title_phonenumber);
		imgPhoneChoiceLeft = (ImageView) findViewById(R.id.phone_choice_left);
		imgPhoneChoiceRight = (ImageView) findViewById(R.id.phone_choice_right);
		txtCurrentTel = (TextView) findViewById(R.id.compose_title_current_phonenumber);
		txtTempTel = (TextView) findViewById(R.id.compose_title_temp_phonenumber);
		msgMgr = new MsgManager(context);
		contactMgr = new ContactManager(context);
		emailMgr = new EmailManager(context);
		telMgr = new TelManager(context);
		inflater = LayoutInflater.from(context);
		nowThreadId = this.getIntent().getIntExtra("threadId", -1);
		flag = this.getIntent().getBooleanExtra("flag", false);
		if (flag)
			msgMgr.modifyMsgRead(1, nowThreadId);
	}

	/*
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "MsgEditActivity", "onCreateContextMenu", 'i');
		menu.add(0, 1, 0, "转发信息");
		menu.add(0, 2, 1, "删除信息");
	}

	/*
	 * 响应上下文菜单和菜单
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();// 存放当前点击项的信息
		CommonUtil.Log("menu", "ContactActivity", "onMenuItemSelected", 'i');
		final int nowPosition = info.position;
		switch (item.getItemId()) {
		case 1:// 转发信息
			Intent intent = new Intent(context, NewMsgActivity.class);
			intent.putExtra("content", msgs.get(nowPosition).getContent());
			startActivity(intent);
			finish();
			break;
		case 2:// 删除信息
			new AlertDialog.Builder(context)
					.setTitle("警告")
					.setMessage("确定删除些消息吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Msg msg = msgs.get(nowPosition);
									msgMgr.delMsgByMsgId(msg.getId());
									refresh();
									changedFlag = true;
									CommonUtil.Toast(context, "删除信息成功!");
								}
							}).setNegativeButton("取消", null).create().show();
			break;
		}
		return true;
	}

	/**
	 * 刷新界面
	 */
	public void refresh() {
		msgs = new ArrayList<Msg>();
		if (nowThreadId == -1) {
			Contact contact = contactMgr.getContactById(this.getIntent()
					.getIntExtra("contactId", -1));
			List<String> list = telMgr
					.getTelNumbersByContactId(contact.getId());
			String address;
			if (list.size() > 0) {
				address = list.get(0);
			} else {
				address = "无";
			}
			Msg msg = new Msg(nowThreadId, 0, 0, address, contact.getName(), 0,
					0, "", "");
			msgs.add(msg);
		} else {
			msgs = msgMgr.getMsgByThreadId(nowThreadId);
			MsgItemsAdapter adapter = new MsgItemsAdapter();
			lsvItemsMsg.setAdapter(adapter);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initUI() {
		refresh();// 显示信息列表
		// 显示头像和姓名
		if (!msgs.get(0).getPerson().equals("")) {
			txtMsgName.setText(msgs.get(0).getPerson());
			final List<Contact> list = contactMgr.getContactsByName(msgs.get(0)
					.getPerson());
			final Contact contact = list.get(0);// 获得当前的联系人
			byte[] image = contact.getImage();
			Bitmap bitmap = ImageTools.getBitmapFromByte(image);
			imgMsgPhoto.setImageBitmap(bitmap);
			imgMsgPhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mPopupWindow == null) {
						popView = inflater.inflate(R.layout.contact_popup_send_msgs,
								null);
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

					mPopupWindow.showAsDropDown(v, 35, -15);

					ImageButton imbCall = (ImageButton) popView
							.findViewById(R.id.imbCall);
					ImageButton imbMsg = (ImageButton) popView
							.findViewById(R.id.imbMsg);
					ImageButton imbEmail = (ImageButton) popView
							.findViewById(R.id.imbEmail);
					ImageButton imbEditContact = (ImageButton) popView
							.findViewById(R.id.imbEditContact);
					imbMsg.setVisibility(View.GONE);
					imbEditContact.setVisibility(View.VISIBLE);
					// 打电话
					imbCall.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							CommonUtil.dial(context, currentNumber);// 打电话
							mPopupWindow.dismiss();
						}
					});
					// 查看联系人详情
					imbEditContact
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									Intent intent = new Intent(context,
											NewContactActivity.class);
									intent.putExtra("contactId",
											contact.getId());
									startActivityForResult(intent,
											REQUEST_MSG_EDIT_NEW_CONTACT);
									mPopupWindow.dismiss();
								}
							});
					// 发邮件
					imbEmail.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {

							CommonUtil.sendEmail(context, emailMgr
									.getEmailsByContactId(contact.getId()));// 发邮件
							mPopupWindow.dismiss();
						}
					});
				}
			});
			// 显示电话号码
			List<String> telNumbers = telMgr.getTelNumbersByContactId(contact
					.getId());
			if (telNumbers.size() > 1) {
				imgPhoneChoiceLeft.setVisibility(View.VISIBLE);
				imgPhoneChoiceRight.setVisibility(View.VISIBLE);
				txtCurrentTel.setText(telNumbers.get(0));
				for (String str : telNumbers)
					if (!str.equals(msgs.get(0).getAddress())) {
						txtTempTel.setText(str);
						break;
					}
				currentNumber = ((TextView) vFlipper.getCurrentView())
						.getText().toString();// 记录当前显示号码
				for (String str : telNumbers)
					if (!str.equals(currentNumber)) {
						tempNumber = str;// 记录第二个号码
						break;
					}
				vFlipper.setOnTouchListener(this);
			} else {
				imgPhoneChoiceLeft.setVisibility(View.GONE);
				imgPhoneChoiceRight.setVisibility(View.GONE);
				txtCurrentTel.setText(msgs.get(0).getAddress());
				currentNumber = msgs.get(0).getAddress();// 记录当前显示号码
			}
		} else {
			txtMsgName.setText(msgs.get(0).getAddress());// 陌生人显示电话号码
			// 显示电话号码
			imgPhoneChoiceLeft.setVisibility(View.GONE);
			imgPhoneChoiceRight.setVisibility(View.GONE);
			txtCurrentTel.setText(msgs.get(0).getAddress());
			currentNumber = msgs.get(0).getAddress();// 记录当前显示号码
			// 显示头像
			imgMsgPhoto.setImageResource(R.drawable.contact_no_contact_photo);
			imgMsgPhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MsgEditActivity.this,
							NewContactActivity.class);
					intent.putExtra("tel", msgs.get(0).getAddress());
					startActivityForResult(intent, REQUEST_MSG_EDIT_NEW_CONTACT);
				}
			});
		}
	}

	/**
	 * 回调刷新界面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_MSG_EDIT_NEW_CONTACT
				&& resultCode == NewContactActivity.RESULT_BTN_SAVE) {
			initUI();
			changedFlag = true;
		}
	}

	/**
	 * onClickListener事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imbMsgCall:// 打电话
			CommonUtil.dial(context, currentNumber);
			break;
		case R.id.btnSendMsg:// 发送短信
			msgContent = edtInputMsg.getText().toString().trim();
			if (msgContent.equals("")) {
				CommonUtil.Toast(context, "信息内容不能为空!");
				return;
			}
			if (currentNumber.equals("") || currentNumber.equals("无")) {
				CommonUtil.Toast(context, "电话号码不能为空!");
				return;
			}
			Intent serviceIntent = new Intent(MsgEditActivity.this,
					SendMsgService.class);
			serviceIntent.putExtra("number", currentNumber);
			serviceIntent.putExtra("content", msgContent);
			startService(serviceIntent);// 启动发短信的服务

			CommonUtil.Toast(context, "发送中……");
			msgMgr.addMsg(currentNumber, msgContent, 2);// 将信息保存到系统数据库
			refresh();// 刷新列表
			edtInputMsg.setText("");
			changedFlag = true;
			break;
		}
	}

	/**
	 * onTouchListener事件
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 取得左右滑动时手指按下的X坐标
			touchDownX = event.getX();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// 取得左右滑动时手指松开的X坐标
			touchUpX = event.getX();
			// 从左往右，看前一个View
			if (touchUpX - touchDownX > 100) {
				// 设置View切换的动画
				vFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						android.R.anim.slide_in_left));
				vFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						android.R.anim.slide_out_right));
				// 显示下一个View
				vFlipper.showPrevious();
				// 改变当前显示号码
				String str = null;
				str = currentNumber;
				currentNumber = tempNumber;
				tempNumber = str;
				// 从右往左，看后一个View
			} else if (touchDownX - touchUpX > 100) {
				// 设置View切换的动画
				// 由于Android没有提供slide_out_left和slide_in_right，所以仿照slide_in_left和slide_out_right编写了slide_out_left和slide_in_right
				vFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.slide_in_right));
				vFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.slide_out_left));
				// 显示前一个View
				vFlipper.showNext();
				// 改变当前显示号码
				String str = null;
				str = currentNumber;
				currentNumber = tempNumber;
				tempNumber = str;
			}
			return true;
		}
		return false;
	}

	/**
	 * 自定义Adapter,显示信息列表
	 * 
	 * @author dell
	 * 
	 */
	class MsgItemsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (nowThreadId == -1)
				return 0;
			else
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
			Msg msg = msgs.get(position);
			if (msg.getMsgMark() == 1)// 接收到的信息
				convertView = inflater.inflate(R.layout.contact_vlist_receive_msgs,
						null);
			else
				convertView = inflater.inflate(R.layout.contact_vlist_send_msgs, null);
			TextView txtDate = (TextView) convertView
					.findViewById(R.id.txtDate);
			TextView txtMsgContent = (TextView) convertView
					.findViewById(R.id.txtMsgContent);
			// 设置信息的时间
			String date = CommonUtil.getNowDate();// 得到当前系统日期
			if (msg.getMsgDate().substring(0, 10).equals(date))
				txtDate.setText(msg.getMsgDate().substring(11));
			else
				txtDate.setText(msg.getMsgDate().substring(0, 10));
			// 设置信息内容
			txtMsgContent.setText(msg.getContent());
			return convertView;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && (changedFlag || flag))
			setResult(RESULT_MSG_CHANGED);
		return super.onKeyDown(keyCode, event);
	}

}
