package mobileMedical.Contacts.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Manager.MsgManager;
import mobileMedical.Contacts.Manager.TelManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Model.Msg;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.namespace.R;

public class NewMsgActivity extends Activity implements OnClickListener {
	private Context context;
	private MsgManager msgMgr;
	private ContactManager contactMgr;
	private TelManager telMgr;
	private LayoutInflater inflater;
	private GridView gridViewDisplayBtnContact;// 展示收信人的一个布局
	private LinearLayout llDisplayTxtContact;// 展示收信人的一个布局
	private Button btnSendMsg;// 发送信息
	private EditText edtInputMsg;// 信息编辑框
	private GridView gridViewRecentContact;// 展示最近发送信息的联系人
	private ImageButton imbAddMoreContact;// 添加更多的联系人
	private List<Msg> recentMsgs;
	private List<String> recieverTel;// 收件人电话
	private List<String> recieverName;// 收件 人姓名
	private TextView txtDisplayReceivers, txtDisplayRecerversCount;
	private AlertDialog dialog;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			CommonUtil.Toast(context, "发送成功!");
			edtInputMsg.setText("");
			// 跳转到信息主界面
			setResult(RESULT_SEND_MESG_SUCCEED);
			finish();
		}
	};

	public static final int REQUEST_SELECT_SYS_CONTACT = 1;
	public static final int RESULT_SEND_MESG_SUCCEED = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity_new_message);

		init();// 初始化
		// 展示最近联系人姓名
		gridViewRecentContact.setAdapter(new ArrayAdapter<String>(context,
				R.layout.contact_item_new_recent_contact, getRecentContactName()));

		// 最近联系人列表的点击监听
		gridViewRecentContact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				gridViewDisplayBtnContact.setVisibility(View.VISIBLE);
				llDisplayTxtContact.setVisibility(View.GONE);
				String address = recentMsgs.get(position).getAddress();
				String person = recentMsgs.get(position).getPerson();
				for (int i = 0; i < recieverTel.size(); i++) {
					if (address.equals(recieverTel.get(i))
							&& person.equals(recieverName.get(i)))
						return;
				}
				recieverTel.add(address);
				recieverName.add(person);
				refreshReceivers();
			}
		});
		// 收件栏每一项点击的监听
		gridViewDisplayBtnContact
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						recieverName.remove(position);
						recieverTel.remove(position);
						refreshReceivers();
					}
				});

		// 编辑框是否聚焦的监听
		edtInputMsg.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					gridViewDisplayBtnContact.setVisibility(View.GONE);
					llDisplayTxtContact.setVisibility(View.VISIBLE);
					String str = "";
					for (String s : recieverName)
						str = str + s + ",";
					// str.substring(0, str.length() - 2);
					txtDisplayReceivers.setText(str);
					txtDisplayRecerversCount.setText("共" + recieverName.size()
							+ "人");
				} else {
					gridViewDisplayBtnContact.setVisibility(View.VISIBLE);
					llDisplayTxtContact.setVisibility(View.GONE);
				}
			}
		});
		// 点击编辑框的时候,收件栏的状态变化
		edtInputMsg.setOnClickListener(this);
		// 点击txt收件栏的时候,收件栏的状态变化
		llDisplayTxtContact.setOnClickListener(this);
		// 添加更多联系人
		imbAddMoreContact.setOnClickListener(this);
		// 发送信息
		btnSendMsg.setOnClickListener(this);
	}

	/**
	 * Activity的初始化
	 */
	private void init() {
		this.context = this;
		msgMgr = new MsgManager(context);
		contactMgr = new ContactManager(context);
		telMgr = new TelManager(context);
		inflater = LayoutInflater.from(context);
		llDisplayTxtContact = (LinearLayout) findViewById(R.id.ll_display_textview_contact);
		gridViewDisplayBtnContact = (GridView) findViewById(R.id.gridViewDisplayBtnContact);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		edtInputMsg = (EditText) findViewById(R.id.edtInputMsg);
		gridViewRecentContact = (GridView) findViewById(R.id.gridViewRecentContact);
		imbAddMoreContact = (ImageButton) findViewById(R.id.imbAddMoreContact);
		txtDisplayReceivers = (TextView) findViewById(R.id.display_style_receiver_list);
		txtDisplayRecerversCount = (TextView) findViewById(R.id.display_stylereceiver_count);
		recentMsgs = new ArrayList<Msg>();
		recentMsgs = msgMgr.getMyEveryContactNo1MsgNoCount();
		recieverTel = new ArrayList<String>();
		recieverName = new ArrayList<String>();
		String content = this.getIntent().getStringExtra("content");
		if (content != null && !content.equals(""))// 表明是信息转发
			edtInputMsg.setText(content);
	}

	/**
	 * 获取最近联系人的姓名
	 * 
	 * @return
	 */
	private List<String> getRecentContactName() {
		List<String> list = new ArrayList<String>();
		for (Msg msg : recentMsgs)
			list.add(msg.getPerson());
		return list;
	}

	/**
	 * 刷新收件栏
	 */
	public void refreshReceivers() {
		gridViewDisplayBtnContact.setAdapter(new ArrayAdapter<String>(context,
				R.layout.contact_item_new_msg_contact, recieverName));
	}

	/**
	 * 回调刷新界面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SELECT_SYS_CONTACT
				&& resultCode == AddRemoveContactToGroupActivity.RESULT_BTN_SURE) {
			String[] ids = data.getStringArrayExtra("ids");
			if (ids != null) {
				for (int i = 0; i < ids.length; i++) {
					Contact contact = contactMgr.getContactById(Integer
							.valueOf(ids[i]));
					List<String> list = telMgr.getTelNumbersByContactId(contact
							.getId());
					if (list.size() > 0) {
						recieverName.add(contact.getName());
						recieverTel.add(list.get(0));
					}
				}
				gridViewDisplayBtnContact.setVisibility(View.VISIBLE);
				llDisplayTxtContact.setVisibility(View.GONE);
				refreshReceivers();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 添加更多联系人
		case R.id.imbAddMoreContact:
			View view = inflater.inflate(
					R.layout.contact_dialog_add_more_contact_click, null);
			TextView txtAddSysContact = (TextView) view
					.findViewById(R.id.txtAddSysContact);
			TextView txtAddUnknowContact = (TextView) view
					.findViewById(R.id.txtAddUnknowContact);
			dialog = new AlertDialog.Builder(context).setView(view).create();
			dialog.show();
			txtAddSysContact.setOnClickListener(this);// 添加系统联系人
			txtAddUnknowContact.setOnClickListener(this);// 添加陌生人号码
			break;
		// 添加系统联系人
		case R.id.txtAddSysContact:
			dialog.dismiss();
			Intent intent = new Intent(NewMsgActivity.this,
					AddRemoveContactToGroupActivity.class);
			intent.putExtra("isNewMsg", true);
			startActivityForResult(intent, REQUEST_SELECT_SYS_CONTACT);
			break;
		// 添加陌生人号码
		case R.id.txtAddUnknowContact:
			dialog.dismiss();// 关闭上一次的Dialog
			View view1 = inflater.inflate(R.layout.contact_dialog_add_group, null);
			TextView txtTitle = (TextView) view1
					.findViewById(R.id.txtNewGroupTitle);
			final EditText edtInput = (EditText) view1
					.findViewById(R.id.edtNewGroupName);
			Button btnSure = (Button) view1.findViewById(R.id.btnAddNewGroup);
			Button btnCancel = (Button) view1.findViewById(R.id.btnGroupCancel);
			txtTitle.setText("输入电话号码");
			edtInput.setHint("请输入电话号码:");
			edtInput.setInputType(InputType.TYPE_CLASS_PHONE);
			dialog = new AlertDialog.Builder(context).setView(view1).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String number = edtInput.getText().toString().trim();
					List<Integer> list = telMgr.getContactIdByTelNumber(number);
					if (list.size() > 0) {
						Contact contact = contactMgr.getContactById(list.get(0));
						recieverName.add(contact.getName());
						recieverTel.add(number);
					} else {
						recieverName.add(number);
						recieverTel.add(number);
					}
					gridViewDisplayBtnContact.setVisibility(View.VISIBLE);
					llDisplayTxtContact.setVisibility(View.GONE);
					refreshReceivers();
					dialog.dismiss();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		// 发送信息
		case R.id.btnSendMsg:
			final String msgContent = edtInputMsg.getText().toString().trim();
			if (msgContent.equals("")) {
				CommonUtil.Toast(context, "信息内容不能为空!");
				return;
			}
			if (recieverTel.size() < 1) {
				CommonUtil.Toast(context, "电话号码不能为空!");
				return;
			}
			View view2 = inflater.inflate(R.layout.contact_progress_dialog, null);
			((TextView) view2.findViewById(R.id.progress_msg))
					.setText(" 发 送 中 . . .");
			dialog = new AlertDialog.Builder(context).setView(view2).create();
			dialog.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(800);// 暂停1秒
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (String currentNumber : recieverTel) {
						sendMsg(currentNumber, msgContent);// 发送短信
						if (recieverTel.size() < 5)// 信息小于五条才保存到数据库
							msgMgr.addMsg(currentNumber, msgContent, 2);// 将信息保存到系统数据库
					}
					handler.sendMessage(new Message());
				}
			}).start();
			break;
		// 点击txt收件栏的时候,收件栏的状态变化
		case R.id.ll_display_textview_contact:
			gridViewDisplayBtnContact.setVisibility(View.VISIBLE);
			llDisplayTxtContact.setVisibility(View.GONE);
			break;
		// 点击编辑框的时候,收件栏的状态变化
		case R.id.edtInputMsg:
			gridViewDisplayBtnContact.setVisibility(View.GONE);
			llDisplayTxtContact.setVisibility(View.VISIBLE);
			String str = "";
			for (String s : recieverName)
				str = str + s + ",";
			// str.substring(0, str.length() - 2);
			txtDisplayReceivers.setText(str);
			txtDisplayRecerversCount.setText("共" + recieverName.size() + "人");
			break;
		}
	}

	/**
	 * 发送短信
	 * 
	 * @param number
	 *            电话号码
	 * @param msgContent
	 *            短信内容
	 */
	public void sendMsg(String number, String msgContent) {
		if (msgContent.length() > 70) {// 短信内容大于70时,划分短信
			List<String> list = msgMgr.divideMessage(msgContent);
			for (String content : list)
				msgMgr.sendMessage(number, content, null, null);
		} else {
			msgMgr.sendMessage(number, msgContent, null, null);
		}
	}
}
