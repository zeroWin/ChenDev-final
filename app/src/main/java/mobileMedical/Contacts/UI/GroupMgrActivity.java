package mobileMedical.Contacts.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Manager.GroupManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Model.Group;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.namespace.R;

public class GroupMgrActivity extends Activity implements OnClickListener {
	private ImageButton imbAddGroup, imbBack;// 添加分组、返回按钮
	private ListView lsvGroupMgr;// 分组的ListView
	private Context context;// 当前上下文对象
	private GroupManager groupMgr;
	private ContactManager contactMgr;
	private List<Contact> contacts;// 接收当前指定分组的联系人
	private List<Group> groups;// 接收所有分组
	private AlertDialog.Builder builder;// Dialog创建器
	private LayoutInflater inflater;// XML反射
	private AlertDialog dialog;// Dialog
	private int nowGroupId;// 记录现在操作的分组

	public static final int REQUEST_TXTADDCONTACTS = 1;
	public static final int REQUEST_TXTREMOVECONTACTS = 2;
	public static final int RESULT_GROUPMGRACTIVITY_BACK = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity_group_mgr);

		init();// 初始化

		MyAdapter adapter = new MyAdapter(context);
		lsvGroupMgr.setAdapter(adapter);

		imbAddGroup.setOnClickListener(this);// 添加一个新的分组
		imbBack.setOnClickListener(this);// 返回
		// GroupItem的点击监听事件
		lsvGroupMgr
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						nowGroupId = (int) id;
						View dialogViewItem = inflater.inflate(
								R.layout.contact_dialog_group_item_click, null);
						TextView txtDialogTitle = (TextView) dialogViewItem
								.findViewById(R.id.txtDialogTitle);
						TextView txtAddContacts = (TextView) dialogViewItem
								.findViewById(R.id.txtAddContacts);
						TextView txtRemoveContacts = (TextView) dialogViewItem
								.findViewById(R.id.txtRemoveContacts);
						TextView txtEditGroupName = (TextView) dialogViewItem
								.findViewById(R.id.txtEditGroupName);
						CommonUtil.Log("sqy",
								"lsvGroupMgr.setOnItemClickListener",
								groups.get(position) + ",id=" + id, 'i');
						txtDialogTitle.setText(groupMgr
								.getGroupById(nowGroupId).getGroupName());
						builder = new AlertDialog.Builder(context);
						dialog = builder.setView(dialogViewItem).create();
						dialog.show();
						txtAddContacts
								.setOnClickListener(GroupMgrActivity.this);
						txtRemoveContacts
								.setOnClickListener(GroupMgrActivity.this);
						txtEditGroupName
								.setOnClickListener(GroupMgrActivity.this);
					}
				});

	}

	/**
	 * 初始化
	 */
	private void init() {
		this.context = this;
		groupMgr = new GroupManager(context);
		contactMgr = new ContactManager(context);
		groups = new ArrayList<Group>();
		groups = groupMgr.getAllGroups();
		imbAddGroup = (ImageButton) findViewById(R.id.imbAddGroup);
		imbBack = (ImageButton) findViewById(R.id.imb_groupmgr_back);
		lsvGroupMgr = (ListView) findViewById(R.id.lsvGroupMgr);
		builder = new AlertDialog.Builder(context);
		inflater = LayoutInflater.from(context);
	}

	/**
	 * 刷新ListView
	 */
	public void refleshLsvGroupMgr() {
		groups = groupMgr.getAllGroups();
		MyAdapter adapter = new MyAdapter(context);
		lsvGroupMgr.setAdapter(adapter);
	}

	/**
	 * OnClickListener接口的方法
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imbAddGroup:// 添加分组
			showMyDialog(R.id.imbAddGroup, null);
			break;

		case R.id.imb_groupmgr_back:// 返回
			setResult(RESULT_GROUPMGRACTIVITY_BACK);
			finish();
			break;
		case R.id.txtAddContacts:// 向分组添加成员
			Intent intent1 = new Intent(context,
					AddRemoveContactToGroupActivity.class);
			intent1.putExtra("groupId", nowGroupId);
			intent1.putExtra("isAdd", true);
			startActivityForResult(intent1, REQUEST_TXTADDCONTACTS);
			dialog.dismiss();
			break;
		case R.id.txtRemoveContacts:// 移除分组成员
			Intent intent2 = new Intent(context,
					AddRemoveContactToGroupActivity.class);
			intent2.putExtra("groupId", nowGroupId);
			intent2.putExtra("isAdd", false);
			startActivityForResult(intent2, REQUEST_TXTREMOVECONTACTS);
			dialog.dismiss();
			break;
		case R.id.txtEditGroupName:// 分组重命名
			AlertDialog dialog1 = dialog;
			showMyDialog(R.id.txtEditGroupName,
					groupMgr.getGroupById(nowGroupId));
			CommonUtil.Log("sqy", "修改分组名称=>nowGroupId:", nowGroupId + "", 'i');
			dialog1.dismiss();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		refleshLsvGroupMgr();
	}

	/**
	 * 显示一个对话框
	 * 
	 * @param viewId
	 *            点击的控件ID号
	 */
	public void showMyDialog(int viewId, final Group group) {

		View dialog_view = inflater.inflate(R.layout.contact_dialog_add_group, null);
		final EditText edtNewGroupName = (EditText) dialog_view
				.findViewById(R.id.edtNewGroupName);
		TextView txtDialogTitleName = (TextView) dialog_view
				.findViewById(R.id.txtNewGroupTitle);
		TextView txtMsgAlert = (TextView) dialog_view
				.findViewById(R.id.txtMsgAlert);
		Button btnSure = (Button) dialog_view.findViewById(R.id.btnAddNewGroup);
		Button btnCancel = (Button) dialog_view
				.findViewById(R.id.btnGroupCancel);

		switch (viewId) {
		// 删除一个分组时显示的对话框
		case R.id.imbDelGroup:
			txtDialogTitleName.setText("温馨提示");
			txtMsgAlert.setVisibility(View.VISIBLE);
			txtMsgAlert.setText("确认解散分组'" + group.getGroupName() + "'?");
			edtNewGroupName.setVisibility(View.GONE);
			builder = new AlertDialog.Builder(context);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					contacts.clear();
					contacts = contactMgr.getContactsByGroupId(group
							.getGroupId());
					// 先把联系人分组ID设为-1(未分组)
					for (Contact contact : contacts) {
						contact.setGroupId(0);
						contactMgr.modifyContact(contact);
					}
					// 然后再删除分组
					groupMgr.delGroupById(group.getGroupId());
					CommonUtil.Log("sqy", "删除的groupId = ", group.getGroupId()
							+ "", 'i');
					refleshLsvGroupMgr();
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

		// 添加一个分组时显示的对话框
		case R.id.imbAddGroup:
			txtDialogTitleName.setText("新建分组");
			txtMsgAlert.setVisibility(View.GONE);
			edtNewGroupName.setVisibility(View.VISIBLE);
			edtNewGroupName.setHint("请输入分组名称：");
			builder = new AlertDialog.Builder(context);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtil.Log("sqy", "GroupMgrActivity:R.id.imbAddGroup",
							"btnSure", 'i');
					String newGroupName = edtNewGroupName.getText().toString();
					if (newGroupName.equals("")) {
						CommonUtil.Toast(context, "创建失败,分组名不能为空!");
						dialog.dismiss();
					} else if (newGroupName.equals("全部联系人")
							|| newGroupName.equals("未分组")) {
						CommonUtil.Toast(context, "创建失败,该分组名为系统分组!");
						dialog.dismiss();
					} else {
						List<String> groupNames = new ArrayList<String>();
						groupNames = groupMgr.getAllGroupName();
						for (String groupName : groupNames) {
							if (groupName.equals(newGroupName)) {
								CommonUtil.Toast(context, "创建失败,该分组已经存在!");
								dialog.dismiss();
								return;
							}
						}
						groupMgr.addGroup(newGroupName);// 向数据库插入新建的分组
						CommonUtil.Toast(context, "添加分组成功!");
						// 获取新建分组的ID号
						nowGroupId = groupMgr.getGroupsByName(newGroupName)
								.getGroupId();
						Intent intent3 = new Intent(context,
								AddRemoveContactToGroupActivity.class);
						intent3.putExtra("groupId", nowGroupId);
						intent3.putExtra("isAdd", true);
						startActivityForResult(intent3, REQUEST_TXTADDCONTACTS);
						dialog.dismiss();
						//refleshLsvGroupMgr();
					}
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtil.Log("sqy", "GroupMgrActivity", "btnCancel", 'i');
					dialog.dismiss();
				}
			});
			break;
		case R.id.txtEditGroupName:// 分组重命名
			txtDialogTitleName.setText("编辑名称");
			txtMsgAlert.setVisibility(View.GONE);
			edtNewGroupName.setVisibility(View.VISIBLE);
			edtNewGroupName.setHint("请输入新的分组名称：");
			edtNewGroupName.setText(group.getGroupName());
			builder = new AlertDialog.Builder(context);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtil.Log("sqy",
							"GroupMgrActivity:R.id.txtEditGroupName",
							"btnSure", 'i');
					String newGroupName = edtNewGroupName.getText().toString();
					if (newGroupName.equals("")) {
						CommonUtil.Toast(context, "修改失败,分组名不能为空!");
						dialog.dismiss();
						return;
					} else {
						List<String> groupNames = new ArrayList<String>();
						groupNames = groupMgr.getAllGroupName();
						for (String groupName : groupNames) {
							if (groupName.equals(newGroupName)) {
								CommonUtil.Toast(context, "修改失败,该分组已经存在!");
								dialog.dismiss();
								return;
							}
						}
						group.setGroupName(newGroupName);
						groupMgr.modifyGroup(group);
						CommonUtil.Toast(context, "修改分组成功!");
						dialog.dismiss();
						refleshLsvGroupMgr();
						CommonUtil.Log("sqy", "修改分组名称=>nowGroupName:",
								group.getGroupName(), 'i');
					}
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtil.Log("sqy", "GroupMgrActivity", "btnCancel", 'i');
					dialog.dismiss();
				}
			});
			break;
		}
	}

	/**
	 * 拦截系统的返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_GROUPMGRACTIVITY_BACK);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 自定义Adapter
	 * 
	 * @author dell
	 * 
	 */
	public class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return groups.size();
		}

		@Override
		public Object getItem(int position) {
			return groups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return groups.get(position).getGroupId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.contact_vlist_group_mgr, null);
			TextView txtGroupName = (TextView) convertView
					.findViewById(R.id.txtGroupName);
			TextView txtGroupCount = (TextView) convertView
					.findViewById(R.id.txtContactCount);
			ImageButton imbDelGroup = (ImageButton) convertView
					.findViewById(R.id.imbDelGroup);

			final Group group = groups.get(position);
			contacts = contactMgr.getContactsByGroupId(group.getGroupId());
			CommonUtil.Log("sqy", "GroupMgrActivity:groupId = ",
					group.getGroupId() + "", 'i');

			txtGroupName.setText(group.getGroupName());
			txtGroupCount.setText(contacts.size() + "位联系人");
			imbDelGroup.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showMyDialog(R.id.imbDelGroup, group);
				}
			});

			return convertView;
		}
	}

}
