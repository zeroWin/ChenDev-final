package mobileMedical.namespace;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devDataType.Parameters.IntParameter;
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
import mobileMedical.Contacts.UI.GroupMgrActivity;
import mobileMedical.Contacts.UI.MsgEditActivity;
import mobileMedical.Contacts.UI.NewContactActivity;
import mobileMedical.Contacts.View.MyPopupWindow;
import mobileMedical.FileManage.MeasDataFilesOperator;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.MessageInfo;
import mobileMedical.Message.ParameterDataKeys;
import mobileMedical.adapter.ListViewAdapter;
import mobileMedical.adapter.ListViewThreeItemAdapter;
import mobileMedical.database.DBManager;
import mobileMedical.util.Utility;

//屏蔽java编译中的一些警告信息。
// unused这个参数是屏蔽：定义的变量在代码中并未使用且无法访问。
// java在编译的时候会出现这样的警告，加上这个注解之后就是告诉编译器，忽略这些警告，编译的过程中将不会出现这种类型的警告
@SuppressWarnings("unused")
public class MemberManage extends Activity {

	private static MessageData mMessageData = new MessageData();

	public static Context context;// 当前上下文对象
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
	private ImageView showlistIcon;
	private Button saveData;//存储数据按钮
	private Button fileTrans; //跳转到上传页面按钮
	private int nowGroupPosition = 0;// 当前分组位置
	// 删除联系人的相关字段
	private boolean isContactMgr = false;// true:联系人管理界面,false:联系人列表界面
	private Map<Integer, Boolean> isChecked;// 标记联系人的CheckBox是否选中
	private Menu myMenu;// 菜单
	private int ckbCount = 0;// 当前选中联系人个数
	private int nowPosMenu;// 标记当前使用上下文菜单的项的Position
	private AlertDialog dialog;
	// public static int transID = 0;
	public static int chooseItem = 0;
	public static int patientID = 0;
	public static int doctorID = 12345678;
	private LayoutInflater inflater;// 反射
	ProgressDialog waitingDialog;
	int xh_count = 0;
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

	boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);
	private DBManager dbManager;

	private List<Map<String, Object>> patientInfoList;
	private ListViewAdapter patientInfoListAdapter;
	private ListView patientInfolv;

	private List<Map<String, Object>> patientContactInfoList;
	private ListViewAdapter patientContactInfoListAdapter;
	private ListView patientContactInfolv;

	private List<Map<String, Object>> patientSignsMeasInfoList;
	private List<Map<String, Object>> patientHistoryInfoList;
	private ListViewThreeItemAdapter patientSignsMeasInfotableAdapter;
	private ListViewThreeItemAdapter patientHistoryInfotableAdapter;
	private ListView patientSignsMeasInfolv;		//患者信息listview
	private ListView patientHistoryInfolv;

	private static final boolean D = true;
	private static final String TAG = "MemberManage";
	public static final String SETTING_INFOS = "SETTINGInfos";
	public static final String ROUTER = "ROUTERCONNECTED";
	public static final String BODYTEMP = "BODYTEMP";
	public static final String BLOODOX = "BLOODOX";
	public static final String BLOODPRESURE = "BLOODPRESURE";
	public static final String BLOODSUGAR = "BLOODSUGAR";

	public static final String MORNING_HOUR_FROM = "MORNINGHOURFROM";
	public static final String MORNING_MIN_FROM = "MORNINGMINFROM";
	public static final String MORNING_HOUR_TO = "MORNINGHOURTO";
	public static final String MORNING_MIN_TO = "MORNINGMINTO";

	public static final String NOON_HOUR_FROM = "NOONHOURFROM";
	public static final String NOON_MIN_FROM = "NOONMINFROM";
	public static final String NOON_HOUR_TO = "NOONHOURTO";
	public static final String NOON_MIN_TO = "NOONMINTO";

	public static final String EVENING_HOUR_FROM = "EVENINGHOURFROM";
	public static final String EVENING_MIN_FROM = "EVENINGMINFROM";
	public static final String EVENING_HOUR_TO = "EVENINGHOURTO";
	public static final String EVENING_MIN_TO = "EVENINGMINTO";

	public static final String NIGHT_HOUR_FROM = "NIGHTHOURFROM";
	public static final String NIGHT_MIN_FROM = "NIGHTMINFROM";
	public static final String NIGHT_HOUR_TO = "NIGHTHOURTO";
	public static final String NIGHT_MIN_TO = "NIGHTMINTO";

	public static final String ANALYSIS_HOUR_FROM = "ANALYSISHOURFROM";
	public static final String ANALYSIS_MIN_FROM = "ANALYSISMINFROM";
	public static final String ANALYSIS_HOUR_TO = "ANALYSISHOURTO";
	public static final String ANALYSIS_MIN_TO = "ANALYSISMINTO";

	public static final String STATISTIC_TIME_CHECK_BOX = "STATISTICTIMECHECKBOX";
	public static final String STATISTIC_TIME_YEAR = "STATISTICTIMEYEAR";
	public static final String STATISTIC_TIME_MONTH = "STATISTICTIMEMONTH";
	public static final String STATISTIC_TIME_DATE = "STATISTICTIMEDATE";
	public static final String STATISTIC_TIME_HOUR = "STATISTICTIMEHOUR";

	private TextView patientName;
	private TextView textViewResult;
	private TextView textViewHospital;
	private TextView textViewYibao;
	private TextView textViewFileNum;
	private TextView textViewMaleFemale;
	private TextView textViewAgeNum;

	private ImageButton memberImage;
	private int contactId = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.member_manger);
		// Contacts
		dbManager = new DBManager(MemberManage.this);
		init();// 初始化数据
		initDefaultPerson();

		patientSignsMeasInfolv.setOnItemClickListener(new ItemClickEvent());	//体征测量点击事件监听

		// initDataFile();
		initTransIDParm();

		MeasDataFilesOperator filesOperator = new MeasDataFilesOperator(
				MemberManage.this);
		filesOperator.WriteMeasDataToFile("measdata.txt", false, "");
		filesOperator.WriteMeasDataToFile("measdatainfo.txt", false, "");

		memberImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//？？干啥的？？？？？？？？？？？？？？？，对应代码没有执行什么操作
				BodyTmpActivity.transTimeTmpData = "measure";
				Intent startMeasIntent = new Intent();
				startMeasIntent.setClass(MemberManage.this,
						MobileMedicalActivity.class);
				startActivity(startMeasIntent);

			}
		});
		//储存数据
		saveData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				querySaveToFile(MemberManage.this,
						new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated
								// method stub
								stopService(new Intent(
										getApplication(),
										BluetoothServer.class));
								// Save the TransID_Parma;
								int transID = ((IntParameter) MessageData.parmsDataHashMap
										.get(ParameterDataKeys.TRANSID))
										.GetValue();
								if (BuildConfig.DEBUG) {
									Log.d("initTransIDParm",
											"Exit transid is  "
													+ transID
													+ "");
								}
								boDbHelper.close();
								boDbHelper
										.UpdateTransIDParm(transID);
								boDbHelper.close();
								Toast.makeText(MemberManage.this,"存储完成",Toast.LENGTH_LONG).show();
							}
						});

			}
		});
		//跳转到上传数据页面
		fileTrans.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(MemberManage.this,fileTransmission.class);
				startActivity(intent);
			}
		});
		initTimeSetting();
		initSetting();

		registerForContextMenu(lsvContact);	//在lsvContact中注册一个菜单

		// 选择分组
		rlContactTool.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				check();// 初始化PopupWindow
				refleshLsvGroup();//不知道有没有作用，加个断点看看

				imbDownContact
						.setBackgroundResource(R.drawable.contact_spinner_down);
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
						contactId = (int) id;
						// 显示联系人详细信息并可修改联系人信息
						if (!isContactMgr) {
							refreshPatientInfo(contactId);
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
						// contacts = contactMgr.getContactsByNamePinyin(str);
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
	 * 初始化默认人物信息
	 */
	private void initDefaultPerson() {
		byte[] image;
		//设置头像
		Bitmap bitmap = ImageTools.getBitmapFromDrawable(getResources()
				.getDrawable(R.drawable.contact_bg_photo_default));
		image = ImageTools.getByteFromBitmap(bitmap);
		//如果联系人中没有信息，则添加默认联系人信息
		if (contactMgr.getAllContacts().isEmpty()) {
			contactMgr.addContact("小明", "x", "", "北京市朝阳区三号楼305", "",
					"2000-1-1", "", image, 0, "6328279485789", "心律不齐", "海淀医院",
					"医保", "17-8-4", "男", "王大军", "父子", "1323456532",
					"wangdajun@sina.com", doctorID);// 将联系人存入数据库
			// // 获得插入联系人的ID号
			// List<Contact> list = contactMgr.getContactsByName("小明");
			// if (list == null) {
			// CommonUtil.Toast(context, "添加联系人失败!");
			// return;
			// }
			//添加电话
			telMgr.addTel(contactMgr.getContactsByName("小明").get(0).getId(),
					"", "13661111111");
			//添加email
			emailMgr.addEmail(
					contactMgr.getContactsByName("小明").get(0).getId(), "",
					"xiaoming@163.com");
		}
		Contact contact = null;
		if (patientID == 0) {
			contact = contactMgr.getAllContacts().get(0);
			contactId = contact.getId();
			patientID = contactId;
		} else {
			contact = contactMgr.getContactById(patientID);
			contactId = patientID;
		}

		initPatientBasicInfoList(contact, telMgr, emailMgr);
		initPatientContactInfoList(contact);
		initPatientMeasInfoTable();
		initPatientHistoryInfoTable();

		// set up the patient basic info list
		// 建立患者信息页面部分
		patientInfoListAdapter = new ListViewAdapter(this, patientInfoList); // 创建适配器
		patientInfolv.setAdapter(patientInfoListAdapter);
		Utility.setListViewHeightBasedOnChildren(patientInfolv);

		// set up the patient contact info list
		// 建立联系人信息页面部分
		patientContactInfoListAdapter = new ListViewAdapter(this,
				patientContactInfoList); // 创建适配器
		patientContactInfolv.setAdapter(patientContactInfoListAdapter);
		Utility.setListViewHeightBasedOnChildren(patientContactInfolv);

		// set up the measure info list
		// 建立体征测量页面部分
		patientSignsMeasInfotableAdapter = new ListViewThreeItemAdapter(this,
				patientSignsMeasInfoList); // 创建适配器
		patientSignsMeasInfolv.setAdapter(patientSignsMeasInfotableAdapter);
		Utility.setListViewHeightBasedOnChildren(patientSignsMeasInfolv);

		// set up the history info list
		// 建立历史测量部分
		patientHistoryInfotableAdapter = new ListViewThreeItemAdapter(this,
				patientHistoryInfoList); // 创建适配器
		patientHistoryInfolv.setAdapter(patientHistoryInfotableAdapter);
		Utility.setListViewHeightBasedOnChildren(patientHistoryInfolv);

		refleshLsvContact();
		// refleshLsvGroup();
		// 收起历史测量数据的按钮
		showlistIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (patientHistoryInfolv.getVisibility() == View.VISIBLE) {
					patientHistoryInfolv.setVisibility(View.GONE);
				} else {
					patientHistoryInfolv.setVisibility(View.VISIBLE);
				}
			}
		});

		patientInfolv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// CommonUtil.Toast(context,
				// "hello   arg1  "+arg1+"  arg2  "+arg2+"  arg3  "+arg3);
				if (contactId == -1) {
					CommonUtil.Toast(context, "请选择联系人");
				} else {
					chooseItem = arg2;
					Intent intent = new Intent(context,
							NewContactActivity.class);
					intent.putExtra("contactId", contactId);
					startActivityForResult(intent, REQUEST_CONTACT_ITEM_CLICK);
				}
			}
		});
		patientContactInfolv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (contactId == -1) {
					CommonUtil.Toast(context, "请选择联系人");
				} else {
					chooseItem = arg2 + 6;
					Intent intent = new Intent(context,
							NewContactActivity.class);
					intent.putExtra("contactId", contactId);
					startActivityForResult(intent, REQUEST_CONTACT_ITEM_CLICK);
				}
			}
		});

		patientID = contactId;
	}

	/**
	 * clear information
	 * 清空MemberManage右半部分的内容
	 */
	private void clearListView() {
		patientInfoList.get(0).put("info", "");
		patientInfoList.get(1).put("info", "");
		patientInfoList.get(2).put("info", "");
		patientInfoList.get(3).put("info", "");
		patientInfoList.get(4).put("info", "");
		patientInfoList.get(5).put("info", "");
		patientInfoListAdapter.notifyDataSetChanged();
		patientContactInfoList.get(0).put("info", "");
		patientContactInfoList.get(1).put("info", "");
		patientContactInfoList.get(2).put("info", "");
		patientContactInfoList.get(3).put("info", "");
		patientContactInfoListAdapter.notifyDataSetChanged();

		patientSignsMeasInfoList.get(0).put("info", "");
		patientSignsMeasInfoList.get(0).put("time", "");
		patientSignsMeasInfoList.get(0).put("doctor", "");
		patientSignsMeasInfoList.get(0).put("doctorId", "");
		patientSignsMeasInfoList.get(1).put("info", "");
		patientSignsMeasInfoList.get(1).put("time", "");
		patientSignsMeasInfoList.get(1).put("doctor", "");
		patientSignsMeasInfoList.get(1).put("doctorId", "");
		patientSignsMeasInfoList.get(2).put("info", "");
		patientSignsMeasInfoList.get(2).put("time", "");
		patientSignsMeasInfoList.get(2).put("doctor", "");
		patientSignsMeasInfoList.get(2).put("doctorId", "");
		patientSignsMeasInfoList.get(3).put("info", "");
		patientSignsMeasInfoList.get(3).put("time", "");
		patientSignsMeasInfoList.get(3).put("doctor", "");
		patientSignsMeasInfoList.get(3).put("doctorId", "");
		patientSignsMeasInfoList.get(4).put("info", "");
		patientSignsMeasInfoList.get(4).put("time", "");
		patientSignsMeasInfoList.get(4).put("doctor", "");
		patientSignsMeasInfoList.get(4).put("doctorId", "");
		patientSignsMeasInfotableAdapter.notifyDataSetChanged();

		patientHistoryInfoList.clear();
		patientHistoryInfotableAdapter.notifyDataSetChanged();

		patientName.setText("");
		textViewResult.setText("");
		textViewHospital.setText("");
		textViewYibao.setText("");
		textViewFileNum.setText("");
		textViewMaleFemale.setText("");
		textViewAgeNum.setText("");

		memberImage.setImageBitmap(null);
		patientID = 0;
	}

	/**
	 * refresh patients' info
	 * 刷新病人的信息
	 */
	private void refreshPatientInfo(int id) {
		patientID = id;
		contactId = id;
		Contact contact = contactMgr.getContactById(id);
		String name = contact.getName();
		String birthday = contact.getBirthday();
		String address = contact.getAddress();
		String idNo = contact.getIdNo();
		String relationName = contact.getRelationName();
		String relation = contact.getRelation();
		String relationTele = contact.getRelationTele();
		String relationEmail = contact.getRelationEmail();
		String firstTelNumbertel = "";
		String firstEmailName = "";
		byte[] image = contact.getImage();

		if (!telMgr.getTelNumbersByContactId(id).isEmpty()) {
			firstTelNumbertel = telMgr.getTelNumbersByContactId(id).get(0);
		}
		if (!emailMgr.getEmailsByContactId(id).isEmpty()) {
			firstEmailName = emailMgr.getEmailsByContactId(id).get(0)
					.getEmailAcount();
		}
		patientInfoList.get(0).put("info", name);
		patientInfoList.get(1).put("info", birthday);
		patientInfoList.get(2).put("info", idNo);
		patientInfoList.get(3).put("info", address);
		patientInfoList.get(4).put("info", firstTelNumbertel);
		patientInfoList.get(5).put("info", firstEmailName);
		patientInfoListAdapter.notifyDataSetChanged();

		patientContactInfoList.get(0).put("info", relationName);
		patientContactInfoList.get(1).put("info", relation);
		patientContactInfoList.get(2).put("info", relationTele);
		patientContactInfoList.get(3).put("info", relationEmail);
		patientContactInfoListAdapter.notifyDataSetChanged();

		initPatientMeasInfoTable();
		patientSignsMeasInfotableAdapter = new ListViewThreeItemAdapter(this,
				patientSignsMeasInfoList); // 创建适配器
		patientSignsMeasInfolv.setAdapter(patientSignsMeasInfotableAdapter);
		Utility.setListViewHeightBasedOnChildren(patientSignsMeasInfolv);

		patientHistoryInfoList.clear();
		//向patientHistoryInfoList中填充信息
		Cursor cursor = boDbHelper.getAllItems();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			// if (cursor
			// .getString(
			// cursor.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTSIDX))
			// .equalsIgnoreCase("0")) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", turnSensorTypeFromNum(cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_SENSORTYPE))));
			map.put("info",
					cursor.getString(
							cursor.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS))
							.split(" ")[0]);
			map.put("time", turnTimeToCNTime(cursor.getString(cursor
					.getColumnIndex("timestamp"))));

			int doctorId = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			map.put("doctorId", doctorId);
			Cursor c = dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			map.put("doctor", c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME)));
			c.close();
			dbManager.closeDB();
			patientHistoryInfoList.add(map);
			// }
		}
		cursor.close();
		boDbHelper.close();
		patientHistoryInfotableAdapter = new ListViewThreeItemAdapter(this,
				patientHistoryInfoList); // 创建适配器
		patientHistoryInfolv.setAdapter(patientHistoryInfotableAdapter);
		Utility.setListViewHeightBasedOnChildren(patientHistoryInfolv);

		patientName.setText(name);
		textViewResult.setText(contact.getDescribe());
		textViewHospital.setText(contact.getHospital());
		textViewYibao.setText(contact.getType());
		textViewFileNum.setText(contact.getFileNo());
		textViewMaleFemale.setText(contact.getGender());
		textViewAgeNum.setText(Utility.getAgeByBirthday(contact.getBirthday()));

		memberImage.setImageBitmap(ImageTools.getBitmapFromByte(image));
	}

	/**
	 * initial the time setting
	 * 初始化时间设置
	 */
	private void initTimeSetting() {
		Cursor timeSettingcursor = boDbHelper.getTimeSetting();
		if (!timeSettingcursor.moveToNext()) {
			timeSettingcursor.close();
			boDbHelper.initTimeSetting();
			if (BuildConfig.DEBUG) {
				Log.i("init time", "0");
			}
		}
		// boDbHelper.initTimeSetting();
	}

	/**
	 * 初始化TransID，如果db里没有，设定为0，否则从TransIDParm表中得到
	 */
	private void initTransIDParm() {
		Cursor cursor = boDbHelper.getTransIDParm();
		int transID = 0;
		if (cursor.getCount() == 0) {

			boDbHelper.insertTransIDParm(transID);
		} else {
			if (cursor.moveToNext()) {
				transID = cursor.getInt(cursor.getColumnIndex("TransID"));
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.TRANSID)).SetValue(transID);
			} else {
				boDbHelper.insertTransIDParm(transID);
			}
		}
		if (BuildConfig.DEBUG) {
			Log.i("initTransIDParm", String.valueOf(transID));
		}
	}

	/**
	 * deal with the data file Copy the MeasData.file to SDCard and the
	 * application data files
	 * 将数据文件复制到机身存储和SD卡中
	 */
	private void initDataFile() {
		Cursor cursor = boDbHelper.getCopyDataFile();
		boolean copyDataFile = false;

		if (cursor.getCount() == 0) {
			copyDataFile = true;
			boDbHelper.insertCopyDataFile(1);
		} else {
			if (cursor.moveToNext()) {
				int copyFile = cursor.getInt(cursor
						.getColumnIndex("copyDataFile"));
				if (copyFile == 0) {
					copyDataFile = true;
					boDbHelper.UpdateCopyDataFile(1);
				} else {
					copyDataFile = false;
				}
			} else {
				copyDataFile = true;
				boDbHelper.insertReadDataFile(1);
			}
		}
		if (copyDataFile) {
			MeasDataFilesOperator measdFilesOperator = new MeasDataFilesOperator(
					this);
			measdFilesOperator.CopyFilesToSDCardOrApp(false);
			measdFilesOperator.CopyFilesToSDCardOrApp(true);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onRestart() {
		super.onRestart();
		initDefaultPerson();
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		Cursor cursor = dbManager.queryDoctorInfo(MemberManage.doctorID);
		cursor.moveToNext();
		setTitle(cursor.getString(cursor
				.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME)) + "医生");
		cursor.close();
		dbManager.closeDB();
	}

	/**
	 * 获取检测位置并返回相应的选项卡值
	 */
	class ItemClickEvent implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int lineIdx = arg2;
			Intent startMeasIntent = new Intent();
			startMeasIntent.setClass(MemberManage.this,
					MobileMedicalActivity.class);
			switch (arg2) {
			case 0:
				startMeasIntent.putExtra("tabindex", arg2);
				startMeasIntent.putExtra("time", "lastest");
				startActivity(startMeasIntent);
				break;
			case 1:
				startMeasIntent.putExtra("tabindex", arg2);
				startMeasIntent.putExtra("time", "lastest");
				startActivity(startMeasIntent);
				break;
			case 2:
				startMeasIntent.putExtra("tabindex", arg2);
				startMeasIntent.putExtra("time", "lastest");
				startActivity(startMeasIntent);
				break;
			case 3:
				startMeasIntent.putExtra("tabindex", arg2);
				startMeasIntent.putExtra("time", "lastest");
				startActivity(startMeasIntent);
				break;
			case 4:
				startMeasIntent.putExtra("tabindex", arg2);
				startMeasIntent.putExtra("time", "lastest");
				startActivity(startMeasIntent);
				break;
			default:
				break;
			}

		}
	}

	/**
	 * initialize the sharedpreference for setting
	 * 初始化一个名为SETTING_INFOS的SharedPreferences格式的数据库
	 */
	private void initSetting() {
		SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0); // 首先获取一个SharedPreferences对象
		Editor edit = settings.edit();

		if (!settings.contains(MORNING_HOUR_FROM)) {
			edit.putInt(MORNING_HOUR_FROM, 6);
			edit.putInt(MORNING_MIN_FROM, 0);
			edit.putInt(MORNING_HOUR_TO, 11);
			edit.putInt(MORNING_MIN_TO, 0);

			edit.putInt(NOON_HOUR_FROM, 11);
			edit.putInt(NOON_MIN_FROM, 0);
			edit.putInt(NOON_HOUR_TO, 18);
			edit.putInt(NOON_MIN_TO, 0);

			edit.putInt(EVENING_HOUR_FROM, 18);
			edit.putInt(EVENING_MIN_FROM, 0);
			edit.putInt(EVENING_HOUR_TO, 20);
			edit.putInt(EVENING_MIN_TO, 0);

			edit.putInt(NIGHT_HOUR_FROM, 20);
			edit.putInt(NIGHT_MIN_FROM, 0);
			edit.putInt(NIGHT_HOUR_TO, 24);
			edit.putInt(NIGHT_MIN_TO, 0);

			edit.putInt(ANALYSIS_HOUR_FROM, 11);
			edit.putInt(ANALYSIS_MIN_FROM, 0);
			edit.putInt(ANALYSIS_HOUR_TO, 18);
			edit.putInt(ANALYSIS_MIN_TO, 0);

		}
		if (!settings.contains(STATISTIC_TIME_CHECK_BOX)) {
			edit.putBoolean(STATISTIC_TIME_CHECK_BOX, true);
			edit.putInt(STATISTIC_TIME_YEAR, 2013);
			edit.putInt(STATISTIC_TIME_MONTH, 1);
			edit.putInt(STATISTIC_TIME_DATE, 1);
			edit.putInt(STATISTIC_TIME_HOUR, 1);

		}
		edit.putBoolean(ROUTER, false);
		edit.putBoolean(BODYTEMP, false);
		edit.putBoolean(BLOODPRESURE, false);
		edit.putBoolean(BLOODSUGAR, false);
		edit.putBoolean(BLOODOX, false);
		edit.commit();
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.member_manage, menu);
		return true;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// Intent serverIntent = null;
	// switch (item.getItemId()) {
	// case R.id.refresh:
	//
	// // Launch the DeviceListActivity to see devices and do scan
	//
	// return true;
	// case R.id.setting:
	// // Launch the settingActivity
	// // serverIntent = new Intent(this, SettingPreferenceActivity.class);
	// // startActivity(serverIntent);
	// Toast.makeText(this, "sss", Toast.LENGTH_LONG).show();
	//
	// return true;
	//
	// }
	// return false;
	// }

	/**
	 * set up the patient basic info table
	 * 为建立患者信息和上面的相关信息页面填充信息
	 */
	private void initPatientBasicInfoList(Contact contact,
			TelManager telManager, EmailManager emailMgr) {
		// TODO Auto-generated method stub
		patientInfolv = (ListView) this.findViewById(R.id.listViewPatientInfo);

		patientInfoList = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "姓名：");
		map.put("info", contact.getName());
		patientInfoList.add(map);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("title", "出生日期：");
		map2.put("info", contact.getBirthday());
		patientInfoList.add(map2);

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("title", "身份证号：");
		map3.put("info", contact.getIdNo());
		patientInfoList.add(map3);

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("title", "住址：");
		map4.put("info", contact.getAddress());
		patientInfoList.add(map4);

		String telenumber = "";
		String emailAddress = "";
		try {
			telenumber = telManager.getTelNumbersByContactId(contact.getId())
					.get(0);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			emailAddress = emailMgr.getEmailsByContactId(contact.getId())
					.get(0).getEmailAcount();
		} catch (Exception e) {
			// TODO: handle exception
		}

		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("title", "联系电话：");
		map5.put("info", telenumber);
		patientInfoList.add(map5);

		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("title", "电子邮件：");
		map6.put("info", emailAddress);
		patientInfoList.add(map6);

		// int width1 = this.getWindowManager().getDefaultDisplay().getWidth()
		// / (nameInfo.length + 10);
		// int width2 = this.getWindowManager().getDefaultDisplay().getWidth()
		// / (nameInfo.length);
		// int height = this.getWindowManager().getDefaultDisplay().getHeight()
		// / 20;// the
		// // chart
		// // occupy
		// // 3/4
		// // of
		// // the
		// // screen
		// ViewGroup.LayoutParams params = patientInfolv.getLayoutParams();
		// params.width = width1 + width2 + 6;
		// params.height = height * 7;
		// patientInfolv.setLayoutParams(params);
		patientName.setText(contact.getName());
		textViewResult.setText(contact.getDescribe());
		textViewHospital.setText(contact.getHospital());
		textViewYibao.setText(contact.getType());
		textViewFileNum.setText(contact.getFileNo());
		textViewMaleFemale.setText(contact.getGender());
		textViewAgeNum.setText(Utility.getAgeByBirthday(contact.getBirthday()));

		memberImage.setImageBitmap(ImageTools.getBitmapFromByte(contact
				.getImage()));
	}

	/**
	 * set up the patient contact info list
	 * 为建立联系人信息页面填充信息
	 */
	private void initPatientContactInfoList(Contact contact) {
		patientContactInfolv = (ListView) this
				.findViewById(R.id.listViewPatientContactInfo);

		patientContactInfoList = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "姓名：");
		map.put("info", contact.getRelationName());
		patientContactInfoList.add(map);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("title", "关系：");
		map2.put("info", contact.getRelation());
		patientContactInfoList.add(map2);

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("title", "联系电话：");
		map3.put("info", contact.getRelationTele());
		patientContactInfoList.add(map3);

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("title", "电子邮件：");
		map4.put("info", contact.getRelationEmail());
		patientContactInfoList.add(map4);
	}

	/**
	 * set up the patient measure info table
	 * 为建立体征测量页面填充信息
	 */
	private void initPatientMeasInfoTable() {
		// Get the latest BodyTempValue display the last body temp
		String value = "";
		String dateString = "";
		String doctor = "";
		int doctorId = 0;

		boDbHelper.close();
		Cursor cursor = boDbHelper.getLatestBodyOrBaseTem();
		if (cursor.moveToNext()) {
			value = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			value = value.split(" ")[0];
			dateString = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));
			dateString = turnTimeToCNTime(dateString);
			doctorId = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c = dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor = c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			c.close();
			dbManager.closeDB();
		}
		boDbHelper.close();

		patientSignsMeasInfolv = (ListView) this
				.findViewById(R.id.listViewSignsMeas);
		patientSignsMeasInfoList = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "体温");
		map.put("info", value);
		map.put("time", dateString);
		map.put("doctor", doctor);
		map.put("doctorId", doctorId);
		patientSignsMeasInfoList.add(map);

		value = "";
		dateString = "";
		doctor = "";
		doctorId = 0;

		cursor = boDbHelper
				.getLatestInfo(MessageInfo.SENSORTYPE_BLOODPRESSUREMETER);
		if (cursor.moveToNext()) {
			value = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			value = value.split(" ")[0];
			dateString = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));
			dateString = turnTimeToCNTime(dateString);
			doctorId = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c = dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor = c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			c.close();
			dbManager.closeDB();
		}
		cursor.close();
		boDbHelper.close();

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("title", "血压");
		map2.put("info", value);
		map2.put("time", dateString);
		map2.put("doctor", doctor);
		map2.put("doctorId", doctorId);
		patientSignsMeasInfoList.add(map2);

		value = "";
		dateString = "";
		doctor = "";
		doctorId = 0;
		
		cursor = boDbHelper
				.getLatestInfo(MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER);
		if (cursor.moveToNext()) {
			value = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			value = value.split(" ")[0];
			dateString = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));
			dateString = turnTimeToCNTime(dateString);
			doctorId = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c = dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor = c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			c.close();
			dbManager.closeDB();
		}
		cursor.close();
		boDbHelper.close();

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("title", "心电");
		map3.put("info", value);
		map3.put("time", dateString);
		map3.put("doctor", doctor);
		map3.put("doctorId", doctorId);
		patientSignsMeasInfoList.add(map3);

		value = "";
		dateString = "";
		doctor = "";
		doctorId = 0;

		cursor = boDbHelper
				.getLatestInfo(MessageInfo.SENSORTYPE_BLOODOXYGENMETER);
		if (cursor.moveToNext()) {
			value = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			value = value.split(" ")[0];
			dateString = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));
			dateString = turnTimeToCNTime(dateString);
			doctorId = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c = dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor = c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			c.close();
			dbManager.closeDB();
		}
		cursor.close();
		boDbHelper.close();

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("title", "血氧");
		map4.put("info", value);
		map4.put("time", dateString);
		map4.put("doctor", doctor);
		map4.put("doctorId", doctorId);
		patientSignsMeasInfoList.add(map4);

		value = "";
		dateString = "";
		doctor = "";
		doctorId = 0;

		//delete this code to hide pulmonaryventilation
/*		cursor = boDbHelper
				.getLatestInfo(MessageInfo.SENSORTYPE_PULMONARYVENTILATION);
		if (cursor.moveToNext()) {
			value = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			value = value.split(" ")[0];
			dateString = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));
			dateString = turnTimeToCNTime(dateString);
			doctorId = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c = dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor = c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			c.close();
			dbManager.closeDB();
		}
		cursor.close();
		boDbHelper.close();

		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("title", "肺通");
		map5.put("info", value);
		map5.put("time", dateString);
		map5.put("doctor", doctor);
		map5.put("doctorId", doctorId);
		patientSignsMeasInfoList.add(map5);*/
	}

	/**
	 * set up the patient measure info table
	 * 为建立历史测量页面填充信息
	 */
	private void initPatientHistoryInfoTable() {
		// Get the latest BodyTempValue display the last body temp
		String item = "";
		String value = "";
		String dateString = "";
		String doctor = "";
		int doctorId = 0;

		patientHistoryInfolv = (ListView) this
				.findViewById(R.id.listViewHistory);
		patientHistoryInfoList = new ArrayList<Map<String, Object>>();

		Cursor cursor = boDbHelper.getAllItems();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			// if (cursor
			// .getString(
			// cursor.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTSIDX))
			// .equalsIgnoreCase("0")) {
			item = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_SENSORTYPE));
			item = turnSensorTypeFromNum(item);
			value = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			value = value.split(" ")[0];
			dateString = cursor.getString(cursor.getColumnIndex("timestamp"));
			dateString = turnTimeToCNTime(dateString);
			doctorId = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c = dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor = c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			c.close();
			dbManager.closeDB();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", item);
			map.put("info", value);
			map.put("time", dateString);
			map.put("doctor", doctor);
			map.put("doctorId", doctorId);
			patientHistoryInfoList.add(map);
			// }
		}
		cursor.close();
		boDbHelper.close();
	}

	/**
	 * 将日期改成"yyyy-MM-dd HH:mm:ss"格式，加上了xxx
	 * @param dateString
	 * @return
	 */
	public static String turnTimeToCNTime(String dateString) {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dateString = format.format(date.getTime() + DateUtils.HOUR_IN_MILLIS
				* 8);
		return dateString;
	}

	/**
	 * 将日期改成"yyyy-MM-dd HH:mm:ss"格式，减去了xxx
	 * @param dateString
	 * @return
	 */
	public static String turnCNTimeToDBTime(String dateString) {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dateString = format.format(date.getTime() - DateUtils.HOUR_IN_MILLIS
				* 8);
		return dateString;
	}

	/**
	 * 将sensorType转化为对应的检测类型汉字
	 * @param sensorType
	 * @return
	 */
	public static String turnSensorTypeFromNum(String sensorType) {

		if (sensorType
				.equalsIgnoreCase(MessageInfo.SENSORTYPE_THERMOMETER + "")) {
			return context.getString(R.string.temperature);
		} else if (sensorType
				.equalsIgnoreCase(MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER
						+ "")) {
			return context.getString(R.string.electrocardiogrammeter);
		} else if (sensorType
				.equalsIgnoreCase(MessageInfo.SENSORTYPE_STETHOSCOPE + "")) {
			return context.getString(R.string.heartBeat);
		} else if (sensorType.equalsIgnoreCase(MessageInfo.MM_MI_BLOOD_PRESSURE
				+ "")) {
			return context.getString(R.string.bloodPresure);
		} else if (sensorType
				.equalsIgnoreCase(MessageInfo.SENSORTYPE_BLOODSUGARMETER + "")) {
			return context.getString(R.string.bloodSuger);
		} else if (sensorType
				.equalsIgnoreCase(MessageInfo.SENSORTYPE_BLOODOXYGENMETER + "")) {
			return context.getString(R.string.bloodOx);
		} else if (sensorType
				.equalsIgnoreCase(MessageInfo.SENSORTYPE_PULMONARYVENTILATION
						+ "")) {
			return context.getString(R.string.pulmonaryventilation);
		}
		return context.getString(R.string.no_type);

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
		showlistIcon = (ImageView) findViewById(R.id.showlist);
		saveData = (Button)findViewById(R.id.saveData);
		fileTrans = (Button)findViewById(R.id.fileTrans);
		isChecked = new HashMap<Integer, Boolean>();

		MyAdapter adapter = new MyAdapter(context);
		lsvContact.setAdapter(adapter);

		patientName = (TextView) this.findViewById(R.id.patientName);
		textViewResult = (TextView) this.findViewById(R.id.textViewResult);
		textViewHospital = (TextView) this.findViewById(R.id.textViewHospital);
		textViewYibao = (TextView) this.findViewById(R.id.textViewYibao);
		textViewFileNum = (TextView) this.findViewById(R.id.textViewFileNum);
		textViewMaleFemale = (TextView) this
				.findViewById(R.id.textViewMaleFemale);
		textViewAgeNum = (TextView) this.findViewById(R.id.textViewAgeNum);
		memberImage = (ImageButton) this.findViewById(R.id.imageButtonMember);
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
		// nowGroupPosition=0;
		// groupPosition=0;
		System.out.println("===>" + groupNames.size());		//然而并没有找到相关输出
		System.out.println("===>" + groupPosition);
		System.out.println("===>" + nowGroupPosition);
		txtContactTool.setText(mItems[nowGroupPosition]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.contact_vlist_group, mItems);
		lsvGroup.setAdapter(adapter);
	}
	/**
	 * 不知道啥玩意
	 * @param menu
	 * @param v
	 * @param menuInfo
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
		Intent serverIntent = null;
		CommonUtil.Log("menu", "ContactActivity", "onMenuItemSelected", 'i');
		// 表明是上下文菜单
		if (featureId == 6) {
			nowPosMenu = info.position;
		}
		Cursor cursor = dbManager.queryDoctorInfo(MemberManage.doctorID);
		cursor.moveToNext();
		String doctorNameString = cursor.getString(cursor
				.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
		cursor.close();
		dbManager.closeDB();

		if (item.getTitle() != null
				&& item.getTitle().equals(doctorNameString + "医生")) {
			Intent doctorInfoIntent = new Intent(this, DoctorInfo.class);
			startActivity(doctorInfoIntent);
			// finish();
			item.getIcon();
		}
		switch (item.getItemId()) {
		case R.id.refresh:
			// Launch the DeviceListActivity to see devices and do scan
			return true;
		case R.id.setting:
			// Launch the settingActivity
			serverIntent = new Intent(this, SettingPreferenceActivity.class);
			startActivity(serverIntent);
			return true;
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
									if (contactMgr.getAllContacts().isEmpty()) {
										clearListView();
										contactId = -1;
									} else {
										contactId = contactMgr.getAllContacts()
												.get(0).getId();
										refreshPatientInfo(contactMgr
												.getAllContacts().get(0)
												.getId());
									}

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
				View view = inflater.inflate(R.layout.contact_progress_dialog,
						null);
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
										MemberManage.this.dialog.show();
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
			if (contactMgr.getAllContacts().isEmpty()) {
				clearListView();
			} else {
				if (Integer.valueOf(data.getExtras().getString("nowContactId")) == -1) {
					refreshPatientInfo(contactMgr.getAllContacts().get(0)
							.getId());
				} else {
					refreshPatientInfo(Integer.valueOf(data.getExtras()
							.getString("nowContactId")));
				}
			}
		}
		if (requestCode == REQUEST_CONTACT_ITEM_CLICK
				&& resultCode == NewContactActivity.RESULT_BTN_SAVE) {
			edtSearchContact.setText("");
			refleshLsvContact();
			if (contactMgr.getAllContacts().isEmpty()) {
				clearListView();
			} else {

				if (Integer.valueOf(data.getExtras().getString("nowContactId")) == -1) {
					refreshPatientInfo(contactMgr.getAllContacts().get(0)
							.getId());
				} else {
					refreshPatientInfo(Integer.valueOf(data.getExtras()
							.getString("nowContactId")));
				}

			}
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
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.notice)
					.setTitle(R.string.app_name)
					.setMessage(R.string.hint_quit)
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
													int which) {
								}
							})
					.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int whichButton) {
//									showWaitingDialog();
//									querySaveToFile(MemberManage.this,
//											new Runnable() {
//												@Override
//												public void run() {
//													// TODO Auto-generated
//													// method stub
//													stopService(new Intent(
//															getApplication(),
//															BluetoothServer.class));
//													// Save the TransID_Parma;
//													int transID = ((IntParameter) MessageData.parmsDataHashMap
//															.get(ParameterDataKeys.TRANSID))
//															.GetValue();
//													if (BuildConfig.DEBUG) {
//														Log.d("initTransIDParm",
//																"Exit transid is  "
//																		+ transID
//																		+ "");
//													}
//													boDbHelper.close();
//													boDbHelper
//															.UpdateTransIDParm(transID);
//													boDbHelper.close();
													System.exit(0); // exitApp
//												}
//											});
								}
							}).show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void querySaveToFile(final Context context, final Runnable runnable) {
		new Thread() {
			public void run() {
				saveToFile();
				if (context != null && runnable != null) {
					((Activity) context).runOnUiThread(runnable);
				}
			}
		}.start();
	}

	/**
	 * 将数据存储
	 */
	private void saveToFile() {
		String splitString = System.getProperty("line.separator");
		String splitNoteString = ",";
		String[] fileContents = null;
		String totalResultString = "", dateString = "";
		int maxTransId = 0, transId = 0, transIdinFile = 0,

		alreadyLinesInMeasDataFile = 0;
		int linesNum = 0, sensorType = 0, measItem = 0, doctorID = 0, patientID = 0;
		MeasDataFilesOperator filesOperator = new MeasDataFilesOperator(
				MemberManage.this);

		try {
			fileContents = filesOperator.ReadMeasDataFromFile(
					"measdatainfo.txt", false, 1);
			transIdinFile = Integer
					.parseInt(fileContents[fileContents.length - 1]
							.split(splitNoteString)[0]);
			alreadyLinesInMeasDataFile = filesOperator.ReadMeasDataFromFile(
					"measdata.txt", false, 1).length;
		} catch (Exception e) {
			// TODO: handle exception
			transIdinFile = 0;
			alreadyLinesInMeasDataFile = 0;
		}

		maxTransId = boDbHelper.getMaxTransId();

		if (maxTransId > transIdinFile) {
			totalResultString = "";
			int tmpLineNum = 0;
			int tmpTransId = 0;
			int tmpAlreadyLinesInMeasDataFile = 0;
			int count = 0;
			int num = 0;
			Cursor dataCursor = boDbHelper.getLatestData(transIdinFile);
			while (dataCursor.moveToNext()) {
				alreadyLinesInMeasDataFile += linesNum;
				linesNum = 0;

				transId = dataCursor.getInt(dataCursor
						.getColumnIndex(ConstDef.DATABASE_FIELD_TRANSID));
				sensorType = dataCursor.getInt(dataCursor
						.getColumnIndex(ConstDef.DATABASE_FIELD_SENSORTYPE));
				measItem = dataCursor.getInt(dataCursor
						.getColumnIndex(ConstDef.DATABASE_FIELD_MEASITEM));
				doctorID = dataCursor.getInt(dataCursor
						.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
				patientID = dataCursor.getInt(dataCursor
						.getColumnIndex(ConstDef.DATABASE_FIELD_PATIENTID));
				dateString = dataCursor.getString(dataCursor
						.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));
				dateString = turnTimeToCNTime(dateString);
				String measResults = dataCursor.getString(dataCursor
						.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
				String[] results = measResults.split(" ");
				// measResultsIdx = dataCursor.getInt(dataCursor
				// .getColumnIndex("measResultsIdx"));

				for (int j = 0; j < results.length; j++) {
					totalResultString += results[j] + splitString;
				}
				linesNum += results.length;
				filesOperator.WriteMeasDataToFile("measdata.txt", false,
						totalResultString);
				filesOperator.WriteMeasDataToFile("measdata.txt", true,
						totalResultString);
				totalResultString = "";

				if (count == 0) {
					num = boDbHelper.getNumberViaTransId(transId);
					boDbHelper.close();
				}

				if (num > 1) {
					if (count == 0) {
						tmpAlreadyLinesInMeasDataFile =

						alreadyLinesInMeasDataFile;
						tmpTransId = transId;
					}
					if (tmpTransId == transId) {
						tmpLineNum += linesNum;
					}
					count++;
					if (count == num) {
						filesOperator.WriteMeasDataToFile("measdatainfo.txt",
								false, transId + splitNoteString + sensorType
										+ splitNoteString + measItem
										+ splitNoteString + doctorID
										+ splitNoteString + patientID
										+ splitNoteString + dateString
										+ splitNoteString
										+ (tmpAlreadyLinesInMeasDataFile + 1)
										+ splitNoteString + tmpLineNum
										+ splitString);
						filesOperator.WriteMeasDataToFile("measdatainfo.txt",
								true, transId + splitNoteString + sensorType
										+ splitNoteString + measItem
										+ splitNoteString + doctorID
										+ splitNoteString + patientID
										+ splitNoteString + dateString
										+ splitNoteString
										+ (tmpAlreadyLinesInMeasDataFile + 1)
										+ splitNoteString + tmpLineNum
										+ splitString);
						count = 0;
						tmpAlreadyLinesInMeasDataFile = 0;
						tmpLineNum = 0;
						tmpTransId = 0;
					}
				} else {
					filesOperator.WriteMeasDataToFile("measdatainfo.txt",
							false, transId + splitNoteString + sensorType
									+ splitNoteString + measItem
									+ splitNoteString + doctorID
									+ splitNoteString + patientID
									+ splitNoteString + dateString
									+ splitNoteString
									+ (alreadyLinesInMeasDataFile + 1)
									+ splitNoteString + linesNum + splitString);
					filesOperator.WriteMeasDataToFile("measdatainfo.txt", true,
							transId + splitNoteString + sensorType
									+ splitNoteString + measItem
									+ splitNoteString + doctorID
									+ splitNoteString + patientID
									+ splitNoteString + dateString
									+ splitNoteString
									+ (alreadyLinesInMeasDataFile + 1)
									+ splitNoteString + linesNum + splitString);
				}
//				waitingDialog.setProgress((xh_count++) * 100
//						/ dataCursor.getCount());
			}
			dataCursor.close();
//			waitingDialog.setProgress(100);
			// try {								//这些双斜杠靠里的不是我注释掉的
			// Thread.sleep(1500);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
//			if (waitingDialog != null && waitingDialog.isShowing())
//				waitingDialog.cancel();
		}
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
			TextView tvLetter = (TextView) arg1.findViewById(R.id.catalog);

			final Contact contact = contacts.get(arg0);
			CommonUtil.Log("sqy", "getView", arg0 + "," + contacts.size(), 'i');
			String upperPinYinString = Utility.getPinYinHeadChar(
					contact.getName()).substring(0, 1);
			int section = upperPinYinString.charAt(0);
			if (arg0 == getPositionForSection(section)) {
				tvLetter.setVisibility(View.VISIBLE);
				tvLetter.setText(upperPinYinString);
			} else {
				tvLetter.setVisibility(View.GONE);
			}
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
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < contacts.size(); i++) {
			String upperPinYinString = Utility.getPinYinHeadChar(
					contacts.get(i).getName()).substring(0, 1);
			char firstChar = upperPinYinString.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 初始化popupWindow
	 */
	private void check() {
		if (popupWindow == null) {
			View view = getLayoutInflater().inflate(
					R.layout.contact_popup_selectgroup, null);
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

	/**
	 * show waiting dialog
	 */
	private void showWaitingDialog() {
		// TODO Auto-generated method stub
		waitingDialog = new ProgressDialog(MemberManage.this);
		waitingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// // set ProgressDialog title
		// xh_pDialog.setTitle("hint");
		// set ProgressDialog message
		waitingDialog.setMessage(getResources().getString(R.string.saving));
		// // set ProgressDialog icon
		// xh_pDialog.setIcon(R.drawable.ic_launcher);
		// set ProgressDialog if process clear enough
		waitingDialog.setIndeterminate(false);
		// set ProgressDialog process
		waitingDialog.setProgress(100);
		// set ProgressDialog cancelable
		waitingDialog.setCancelable(false);
		// 让ProgressDialog显示
		waitingDialog.show();
	}

}