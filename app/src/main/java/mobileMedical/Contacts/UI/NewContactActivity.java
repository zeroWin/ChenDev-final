package mobileMedical.Contacts.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Manager.EmailManager;
import mobileMedical.Contacts.Manager.GroupManager;
import mobileMedical.Contacts.Manager.IMManager;
import mobileMedical.Contacts.Manager.TelManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Model.Email;
import mobileMedical.Contacts.Model.IM;
import mobileMedical.Contacts.Model.Tel;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.namespace.MemberManage;
import mobileMedical.namespace.R;

public class NewContactActivity extends Activity implements OnClickListener {
	private RelativeLayout rlTelTitle, rlEmailTitle, rlIMTitle;// 电话、邮箱、IM的标题栏
	private LinearLayout llSelectGroup;// 分组选择栏
	private RelativeLayout rlAddAddress,
			rlAddCompany,
			rlAddNickname,// 添加"其它"栏的属性
			rlAddBirthday, rlAddIdNo, rlAddDescribe, rlAddGender, rlAddType,
			rlAddFileNo, rlAddHospital, rlAddRelationName, rlAddRelation,
			rlAddRelationTele, rlAddRelationEmail, rlAddNote;
	private ImageButton imbBack;// 返回按钮
	private EditText edtName ,edt_inputidno;// 姓名栏
	private Button btnAddMore, btnSave;// 添加更多属性、保存按钮
	private int telCount = 0, emailCount = 0, imCount = 0;// 记录"电话、邮箱、IM"编辑框数量
	private Context context;// 当前上下文对象
	private LayoutInflater inflater;// 反射
	private LinearLayout llTel, llEmail, llIM;// 电话、邮箱、IM的父容器
	private AlertDialog.Builder builder;// 对话框创建器
	private AlertDialog dialog, dialog2;// 对话框
	private String[] mItemsGroupName;// 所有分组名的数组
	private int nowContactId;// 点击联系人传递过来的联系人ID号
	private String newTelNumber;// 信息界面点击新建联系人过来的电话号码
	private Contact nowContact = null;// 点击联系人过来的实体
	private TextView txtActivityTitle;// Activity标题
	private File mCurrentPhotoFile;
	private Bitmap cameraBitmap;
	private boolean flagBtnImage = false;// 标记是否改变头像
	private byte[] oldImage;// 保存点击过来的头像
	private int contactID;
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (dialog != null) {
				dialog.dismiss();
			}
			if (dialog2 != null) {
				dialog2.dismiss();
				if (nowContactId > 0)
					CommonUtil.Toast(context, "修改联系人成功");
				else
					CommonUtil.Toast(context, "联系人添加成功");
			}

			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("nowContactId", "" + contactID);
			data.putExtras(bundle);
			setResult(RESULT_BTN_SAVE, data);
			finish();
		}
	};
	// 日期
	private String setDate;
	private Calendar calendar;
	private int mYear, mMonth, mDay;
	private DatePickerDialog datePickerDialog;
	// Manger
	private ContactManager contactMgr;
	private GroupManager groupMgr;
	private TelManager telMgr;
	private EmailManager emailMgr;
	private IMManager imMgr;
	// 头像选择
	private ImageView imgPickPhoto;
	private View popView;
	private PopupWindow popupWindow;
	private Gallery gallery;
	private int currentImagePosition;// 用于记录当前选中图像在图像数组中的位置，默认值为0

	public static final int RESULT_BTN_SAVE = 0;
	public static final int REQUEST_BTN_FILE_SELECT = 2;

	/** 手机类别 **/
	public final String[] mItemsTel = { "家庭电话", "手机", "工作电话", "工作传真", "家庭传真",
			"寻呼机", "其它电话", "自定义电话", "回拨电话", "车载电话", "公司电话", "数字电话", "主要电话",
			"其他传真", "无线电话", "电报", "TTY/TDD", "工作手机", "工作寻呼", "次要电话", "彩信电话" };
	/** 邮箱类别 **/
	public final String[] mItemsEmail = { "个人邮箱", "工作邮箱", "其它邮箱", "手机邮箱",
			"自定义邮箱" };
	/** IM类别 **/
	public final String[] mItemsIM = { "QQ", "MSN", "AIM", "WINDOWS LIVE",
			"YAHOO", "SKYPE", "GTALK", "自定义" };

	/** 系统头像资源 **/
	private Integer[] mImageIds = { R.drawable.contact_icon,
			R.drawable.contact_photo1, R.drawable.contact_photo10,
			R.drawable.contact_photo11, R.drawable.contact_photo12,
			R.drawable.contact_photo13 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity_new_contacts);

		init();// 初始化

		// 添加更多联系人属性
		btnAddMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = inflater.inflate(
						R.layout.contact_dialog_newcontact_addmore, null);
				TextView txtAddTel = (TextView) view
						.findViewById(R.id.txtAddTel);
				TextView txtAddEmail = (TextView) view
						.findViewById(R.id.txtAddEmail);
//				TextView txtAddIM = (TextView) view.findViewById(R.id.txtAddIM);
				TextView txtAddAddress = (TextView) view
						.findViewById(R.id.txtAddAdress);
//				TextView txtAddCompany = (TextView) view
//						.findViewById(R.id.txtAddCompany);
//				TextView txtAddNickname = (TextView) view
//						.findViewById(R.id.txtAddNickname);
				TextView txtAddIdNo = (TextView) view
						.findViewById(R.id.txtAddIdNo);
				TextView txtAddDescribe = (TextView) view
						.findViewById(R.id.txtAddDescribe);
				TextView txtAddGender = (TextView) view
						.findViewById(R.id.txtAddGender);
				TextView txtAddType = (TextView) view
						.findViewById(R.id.txtAddType);
				TextView txtAddFileNo = (TextView) view
						.findViewById(R.id.txtAddFileNo);
				TextView txtAddHospital = (TextView) view
						.findViewById(R.id.txtAddHospital);
				TextView txtAddRalationName = (TextView) view
						.findViewById(R.id.txtAddRelationName);
				TextView txtAddRalation = (TextView) view
						.findViewById(R.id.txtAddRelation);
				TextView txtAddRalationTele = (TextView) view
						.findViewById(R.id.txtAddRelationTele);
				TextView txtAddRalationEmail = (TextView) view
						.findViewById(R.id.txtAddRelationEmail);
				TextView txtAddBirthday = (TextView) view
						.findViewById(R.id.txtAddBirthday);
				TextView txtAddNote = (TextView) view
						.findViewById(R.id.txtAddNote);
				builder = new AlertDialog.Builder(context);
				dialog = builder.setView(view).create();
				dialog.show();
				txtAddTel.setOnClickListener(NewContactActivity.this);
				txtAddEmail.setOnClickListener(NewContactActivity.this);
//				txtAddIM.setOnClickListener(NewContactActivity.this);
				txtAddAddress.setOnClickListener(NewContactActivity.this);
//				txtAddCompany.setOnClickListener(NewContactActivity.this);
//				txtAddNickname.setOnClickListener(NewContactActivity.this);
				txtAddIdNo.setOnClickListener(NewContactActivity.this);
				txtAddDescribe.setOnClickListener(NewContactActivity.this);
				txtAddGender.setOnClickListener(NewContactActivity.this);
				txtAddType.setOnClickListener(NewContactActivity.this);
				txtAddFileNo.setOnClickListener(NewContactActivity.this);
				txtAddHospital.setOnClickListener(NewContactActivity.this);
				txtAddRalationName.setOnClickListener(NewContactActivity.this);
				txtAddRalation.setOnClickListener(NewContactActivity.this);
				txtAddRalationTele.setOnClickListener(NewContactActivity.this);
				txtAddRalationEmail.setOnClickListener(NewContactActivity.this);
				txtAddBirthday.setOnClickListener(NewContactActivity.this);
				txtAddNote.setOnClickListener(NewContactActivity.this);
			}
		});
		rlAddAddress.setOnClickListener(this);// 住址栏监听
		rlAddBirthday.setOnClickListener(this);// 生日栏监听
		rlAddCompany.setOnClickListener(this);// 公司栏监听
		rlAddNickname.setOnClickListener(this);// 住址栏监听
		rlAddIdNo.setOnClickListener(this);// 住址栏监听
		rlAddGender.setOnClickListener(this);// 住址栏监听
		rlAddType.setOnClickListener(this);// 住址栏监听
		rlAddHospital.setOnClickListener(this);// 住址栏监听
		rlAddDescribe.setOnClickListener(this);// 住址栏监听
		rlAddFileNo.setOnClickListener(this);// 住址栏监听
		rlAddRelation.setOnClickListener(this);// 住址栏监听
		rlAddRelationEmail.setOnClickListener(this);// 住址栏监听
		rlAddRelationName.setOnClickListener(this);// 住址栏监听
		rlAddRelationTele.setOnClickListener(this);// 住址栏监听
		rlAddNote.setOnClickListener(this);// 住址栏监听
		llSelectGroup.setOnClickListener(this);// 分组选择栏监听
		imgPickPhoto.setOnClickListener(this);// 头像选择栏监听

		btnSave.setOnClickListener(onClickListener);// 保存按钮的监听

		// 返回按钮
		imbBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				handler.sendMessage(new Message());
				// finish();
			}
		});

		if(nowContactId>0){//modify 

			edt_inputidno.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
//					edt_inputidno.setFocusable(false);
//					edt_inputidno.setFocusableInTouchMode(false);
					edt_inputidno.setEnabled(false);
					CommonUtil.Toast(context, getString(R.string.idno_not_change));
					return false;
				}
			});
		}
	}

	/**
	 * 初始化
	 */
	private void init() {
		this.context = this;
		nowContactId = this.getIntent().getIntExtra("contactId", -1);
		contactID = nowContactId;
		newTelNumber = this.getIntent().getStringExtra("tel");
		inflater = LayoutInflater.from(context);
		builder = new AlertDialog.Builder(context);
		rlTelTitle = (RelativeLayout) findViewById(R.id.rlPhoneTitle);
		rlEmailTitle = (RelativeLayout) findViewById(R.id.rlEmailTitle);
		rlIMTitle = (RelativeLayout) findViewById(R.id.rlIMTitle);
		llSelectGroup = (LinearLayout) findViewById(R.id.llgroup);
		rlAddAddress = (RelativeLayout) findViewById(R.id.rlAddress);
		rlAddBirthday = (RelativeLayout) findViewById(R.id.rlBirthday);
		rlAddCompany = (RelativeLayout) findViewById(R.id.rlCompany);
		rlAddNickname = (RelativeLayout) findViewById(R.id.rlNickname);
		rlAddIdNo = (RelativeLayout) findViewById(R.id.rlIdNo);
		rlAddDescribe = (RelativeLayout) findViewById(R.id.rlDescribe);
		rlAddGender = (RelativeLayout) findViewById(R.id.rlGender);
		rlAddType = (RelativeLayout) findViewById(R.id.rlType);
		rlAddHospital = (RelativeLayout) findViewById(R.id.rlHospital);
		rlAddFileNo = (RelativeLayout) findViewById(R.id.rlFileNo);
		rlAddRelation = (RelativeLayout) findViewById(R.id.rlRelation);
		rlAddRelationEmail = (RelativeLayout) findViewById(R.id.rlRelationEmail);
		rlAddRelationName = (RelativeLayout) findViewById(R.id.rlRelationName);
		rlAddRelationTele = (RelativeLayout) findViewById(R.id.rlRelationTele);
		rlAddNote = (RelativeLayout) findViewById(R.id.rlNote);
		imbBack = (ImageButton) findViewById(R.id.imb_new_back);
		edtName = (EditText) findViewById(R.id.edt_name);
		edt_inputidno= (EditText) findViewById(R.id.edt_inputidno);
		imgPickPhoto = (ImageView) findViewById(R.id.contact_facepic);
		btnAddMore = (Button) findViewById(R.id.btn_addmore);
		btnSave = (Button) findViewById(R.id.btn_new_save);
		llTel = (LinearLayout) findViewById(R.id.ll_rlPhone);
		llEmail = (LinearLayout) findViewById(R.id.ll_rlEmail);
		llIM = (LinearLayout) findViewById(R.id.ll_rlIM);
		txtActivityTitle = (TextView) findViewById(R.id.txtNewContactTitle);

		contactMgr = new ContactManager(context);
		groupMgr = new GroupManager(context);
		telMgr = new TelManager(context);
		emailMgr = new EmailManager(context);
		imMgr = new IMManager(context);
		// 获取所有分组名称并转化为数组
		mItemsGroupName = new String[groupMgr.getAllGroupName().size()];
		mItemsGroupName = (groupMgr.getAllGroupName()).toArray(mItemsGroupName);

		if (nowContactId == -1) {// 点击添加联系人过来的
			addViewToTel();
			addViewToEmail();
			rlIMTitle.setVisibility(View.GONE);
			llIM.setVisibility(View.GONE);
			txtActivityTitle.setText("新建联系人");
		} else {// 点击联系人过来的
			txtActivityTitle.setText("联系人详情");
			initUi(MemberManage.chooseItem);// 显示联系人详情
		}
	}

	/**
	 * 显示联系人详情界面
	 */
	public void initUi(int item) {
		// ((EditText)
		// rlAddAddress.findViewById(R.id.edt_inputaddress)).requestFocus();
		// 展示Contact的字段
		nowContact = contactMgr.getContactById(nowContactId);
		oldImage = nowContact.getImage();
		Bitmap bitmap = ImageTools.getBitmapFromByte(oldImage);
		imgPickPhoto.setImageBitmap(bitmap);
		edtName.setText(nowContact.getName());
		if (item == 0 || true) {
			edtName.requestFocus();
		}
		if (!nowContact.getNickName().equals("")) {
			AddNicknameView();
			((EditText) rlAddNickname.findViewById(R.id.edt_inputnickname))
					.setText(nowContact.getNickName());
		}
		if (!nowContact.getAddress().equals("")) {
			AddAddressView();
			((EditText) rlAddAddress.findViewById(R.id.edt_inputaddress))
					.setText(nowContact.getAddress());
			if (item == 3) {
				((EditText) rlAddAddress.findViewById(R.id.edt_inputaddress))
						.requestFocus();
			}
		}
		if (!nowContact.getIdNo().equals("")) {
			AddIdNoView();
			((EditText) rlAddIdNo.findViewById(R.id.edt_inputidno))
					.setText(nowContact.getIdNo());
			if (item == 2) {
				((EditText) rlAddIdNo.findViewById(R.id.edt_inputidno))
						.requestFocus();
			}
		}
		if (!nowContact.getDescribe().equals("")) {
			AddDescribeView();
			((EditText) rlAddDescribe.findViewById(R.id.edt_inputdescribe))
					.setText(nowContact.getDescribe());
		}
		if (!nowContact.getGender().equals("")) {
			AddGenderView();
			((TextView) rlAddGender.findViewById(R.id.edt_inputgender))
					.setText(nowContact.getGender());
		}
		if (!nowContact.getType().equals("")) {
			AddTypeView();
			((TextView) rlAddType.findViewById(R.id.edt_inputtype))
					.setText(nowContact.getType());
		}

		if (!nowContact.getFileNo().equals("")) {
			AddFileNoView();
			((EditText) rlAddFileNo.findViewById(R.id.edt_inputfileno))
					.setText(nowContact.getFileNo());
		}
		if (!nowContact.getHospital().equals("")) {
			AddHospitalView();
			((EditText) rlAddHospital.findViewById(R.id.edt_inputhospital))
					.setText(nowContact.getHospital());
		}
		if (!nowContact.getRelation().equals("")) {
			AddRelationView();
			((EditText) rlAddRelation.findViewById(R.id.edt_inputrelation))
					.setText(nowContact.getRelation());
			if (item == 7) {
				((EditText) rlAddRelation.findViewById(R.id.edt_inputrelation))
						.requestFocus();
			}
		}
		if (!nowContact.getRelationEmail().equals("")) {
			AddRelationEmailView();
			((EditText) rlAddRelationEmail
					.findViewById(R.id.edt_inputrelationemail))
					.setText(nowContact.getRelationEmail());
			if (item == 9) {
				((EditText) rlAddRelationEmail
						.findViewById(R.id.edt_inputrelationemail))
						.requestFocus();
			}
		}
		if (!nowContact.getRelationName().equals("")) {
			AddRelationNameView();
			((EditText) rlAddRelationName
					.findViewById(R.id.edt_inputrelationname))
					.setText(nowContact.getRelationName());
			if (item == 6) {
				((EditText) rlAddRelationName
						.findViewById(R.id.edt_inputrelationname))
						.requestFocus();
			}
		}
		if (!nowContact.getRelationTele().equals("")) {
			AddRelationTeleView();
			((EditText) rlAddRelationTele
					.findViewById(R.id.edt_inputrelationtele))
					.setText(nowContact.getRelationTele());
			if (item == 8) {
				((EditText) rlAddRelationTele
						.findViewById(R.id.edt_inputrelationtele))
						.requestFocus();
			}
		}
		if (!nowContact.getCompany().equals("")) {
			AddCompanyView();
			((EditText) rlAddCompany.findViewById(R.id.edt_inputcompany))
					.setText(nowContact.getCompany());
		}
		if (!nowContact.getBirthday().equals("")) {
			AddBirthdayView();
			((TextView) rlAddBirthday.findViewById(R.id.edit_inputbirthday))
					.setText(nowContact.getBirthday());
		}
		if (!nowContact.getNote().equals("")) {
			AddNoteView();
			((EditText) rlAddNote.findViewById(R.id.edt_inputnote))
					.setText(nowContact.getNote());
		}
		if (nowContact.getGroupId() == 0) {
			((TextView) llSelectGroup.findViewById(R.id.txt_groupname))
					.setText("无");
		} else {
			((TextView) llSelectGroup.findViewById(R.id.txt_groupname))
					.setText(groupMgr.getGroupById(nowContact.getGroupId())
							.getGroupName());
		}

		// 展示Tel的字段
		List<Tel> tels = telMgr.getTelsByContactId(nowContactId);
		if (tels.size() > 0) {
			rlTelTitle.setVisibility(View.VISIBLE);
			for (int i = 0; i < tels.size(); i++) {
				addViewToTel();
				View view = llTel.getChildAt(i);
				((EditText) view.findViewById(R.id.edt_inputphone))
						.setText(tels.get(i).getTelNumber());
				((TextView) view.findViewById(R.id.txt_phone)).setText(tels
						.get(i).getTelName());
				if (item == 4) {
					((EditText) view.findViewById(R.id.edt_inputphone))
							.requestFocus();
				}
			}
		} else
			rlTelTitle.setVisibility(View.GONE);

		// 展示Email的字段
		List<Email> emails = emailMgr.getEmailsByContactId(nowContactId);
		if (emails.size() > 0) {
			rlEmailTitle.setVisibility(View.VISIBLE);
			for (int i = 0; i < emails.size(); i++) {
				addViewToEmail();
				View view = llEmail.getChildAt(i);
				((EditText) view.findViewById(R.id.edt_inputemail))
						.setText(emails.get(i).getEmailAcount());
				((TextView) view.findViewById(R.id.txt_email)).setText(emails
						.get(i).getEmailName());
				if (item == 5) {
					((EditText) view.findViewById(R.id.edt_inputemail))
							.requestFocus();
				}
			}
		} else
			rlEmailTitle.setVisibility(View.GONE);

		// 展示IM的字段
		List<IM> ims = imMgr.getIMsByContactId(nowContactId);
		if (ims.size() > 0) {
			rlIMTitle.setVisibility(View.VISIBLE);
			for (int i = 0; i < ims.size(); i++) {
				addViewToIM();
				View view = llIM.getChildAt(i);
				((EditText) view.findViewById(R.id.edt_inputIm)).setText(ims
						.get(i).getImAcount());
				((TextView) view.findViewById(R.id.txt_im)).setText(ims.get(i)
						.getImName());
			}
		} else
			rlIMTitle.setVisibility(View.GONE);
	}

	/**
	 * 点击事件的响应
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击"添加更多属性"后的点击事件
		case R.id.txtAddTel:
			addViewToTel();// 添加一个电话栏
			dialog.dismiss();
			break;
		case R.id.txtAddEmail:// 添加一个邮箱栏
			addViewToEmail();
			dialog.dismiss();
			break;
//		case R.id.txtAddIM:// 添加一个IM栏
//			addViewToIM();
//			dialog.dismiss();
//			break;
		case R.id.txtAddAdress:// 添加地址栏
			AddAddressView();
			dialog.dismiss();
			break;
//		case R.id.txtAddCompany:// 添加公司栏
//			AddCompanyView();
//			dialog.dismiss();
//			break;
//		case R.id.txtAddNickname:// 添加昵称栏
//			AddNicknameView();
//			dialog.dismiss();
//			break;
		case R.id.txtAddIdNo:
			AddIdNoView();
			dialog.dismiss();
			break;
		case R.id.txtAddDescribe:
			AddDescribeView();
			dialog.dismiss();
			break;
		case R.id.txtAddGender:
			showMenuDialog(1);
			AddGenderView();
			dialog.dismiss();
			break;
		case R.id.txtAddType:
			showMenuDialog(2);
			AddTypeView();
			dialog.dismiss();
			break;
		case R.id.txtAddFileNo:
			AddFileNoView();
			dialog.dismiss();
			break;
		case R.id.txtAddHospital:
			AddHospitalView();
			dialog.dismiss();
			break;
		case R.id.txtAddRelation:
			AddRelationView();
			dialog.dismiss();
			break;
		case R.id.txtAddRelationEmail:
			AddRelationEmailView();
			dialog.dismiss();
			break;
		case R.id.txtAddRelationName:
			AddRelationNameView();
			dialog.dismiss();
			break;
		case R.id.txtAddRelationTele:
			AddRelationTeleView();
			dialog.dismiss();
			break;
		case R.id.txtAddBirthday:// 添加生日栏
			showDatePickerDialog();// 显示日期设置对话框
			AddBirthdayView();
			dialog.dismiss();
			break;
		case R.id.txtAddNote:// 添加备注栏
			AddNoteView();
			dialog.dismiss();
			break;
		// 点击"其它"的点击事件
		case R.id.llgroup:
			if (mItemsGroupName.length > 0) {
				builder = new AlertDialog.Builder(context);
				builder.setTitle("选择分组")
						.setItems(mItemsGroupName,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										((TextView) llSelectGroup
												.findViewById(R.id.txt_groupname))
												.setText(mItemsGroupName[which]);
									}
								}).create().show();
			} else
				CommonUtil.Toast(context, "当前无分组可选择!");
			break;
		// 头像选择
		case R.id.contact_facepic:
			if (popupWindow == null) {
				popView = inflater.inflate(R.layout.contact_popup_photo_select,
						null);
				popupWindow = new PopupWindow(popView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				// 下面三个设置可以使PopupWindow中的项和外面的键都可以响应点击或触摸事件
				popupWindow.setFocusable(true);
				popupWindow.setTouchable(true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
			}
			TextView txtAddImageFromPhoto = (TextView) popView
					.findViewById(R.id.txtAddImageFromPhoto);
			TextView txtAddImageFromSystem = (TextView) popView
					.findViewById(R.id.txtAddImageFromSystem);
			TextView txtAddImageFromFile = (TextView) popView
					.findViewById(R.id.txtAddImageFromFile);
			popupWindow.showAsDropDown(imgPickPhoto, 70, -70);// 弹出选择头像的PopoupWindow
			txtAddImageFromPhoto.setOnClickListener(onClickListener1);// 监听从照相机获取头像
			txtAddImageFromSystem.setOnClickListener(onClickListener1);// 监听从系统获取头像
			txtAddImageFromFile.setOnClickListener(onClickListener1);// 监听从文件获取头像
			break;
		}
	}

	// 头像选择的监听事件
	private OnClickListener onClickListener1 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 从照相机获取头像
			case R.id.txtAddImageFromPhoto:
				try {
					// Launch camera to take photo for selected contact
					PHOTO_DIR.mkdirs();
					mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE,
							null);
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(mCurrentPhotoFile));
					startActivityForResult(intent, Activity.DEFAULT_KEYS_DIALER);
				} catch (ActivityNotFoundException e) {
					CommonUtil.Toast(context, "not find photo");
				}
				popupWindow.dismiss();
				break;
			// 从系统获取头像
			case R.id.txtAddImageFromSystem:
				CommonUtil.Log("sqy", "onClickListener1",
						"txtAddImageFromSystem", 'i');
				View view = inflater.inflate(R.layout.contact_gallery, null);
				builder = new AlertDialog.Builder(context);
				dialog = builder
						.setView(view)
						.setTitle("请选择头像")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										imgPickPhoto
												.setImageResource(mImageIds[currentImagePosition
														% mImageIds.length]);
										flagBtnImage = true;
									}
								}).setNegativeButton("取消", null).create();
				gallery = (Gallery) view.findViewById(R.id.img_gallery);
				gallery.setAdapter(new ImageAdapter(context));
				gallery.setSelection(10000);
				gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// 当前的头像位置为选中的位置,方便以后用到
						currentImagePosition = arg2;
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
				dialog.show();
				popupWindow.dismiss();
				break;
			// 从文件获取头像
			case R.id.txtAddImageFromFile:
				CommonUtil.Log("sqy", "onClickListener1",
						"txtAddImageFromFile", 'i');
				Intent intent1 = new Intent(context, FileSelectActivity.class);
				startActivityForResult(intent1, REQUEST_BTN_FILE_SELECT);
				popupWindow.dismiss();
				break;
			}
		}
	};

	/**
	 * 创建图片名称
	 * 
	 * @return
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";

	}

	/***
	 * Constructs an intent for image cropping.
	 */
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/**");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 77);
		intent.putExtra("outputY", 77);
		intent.putExtra("return-data", true);
		return intent;
	}

	// 点击保存按钮的监听事件
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (edtName.getText().toString().equals("")) {
				CommonUtil.Toast(context, "姓名不能为空,保存联系人失败!");
				return;
			}
			if (edt_inputidno.getText().toString().equals("")) {
				CommonUtil.Toast(context, "身份证号不能为空,保存联系人失败!");
				return;
			}
			if(nowContactId<=0){//modify 
				if(contactMgr.getContactByIdNo(edt_inputidno.getText().toString())!=null){
					CommonUtil.Toast(context, "身份证号已经存在,保存联系人失败!");
					return;
				}
			}
			
			for (int i = 0; i < llTel.getChildCount(); i++) {
				View view = llTel.getChildAt(i);
				EditText edtTel = (EditText) view
						.findViewById(R.id.edt_inputphone);
				if (!edtTel.getText().toString().equals("") && edtTel.getText().toString().length()>11) {
					CommonUtil.Toast(context, "请填写正确的电话号码,保存联系人失败!");
					return;
				}
			}
			
			View view = inflater
					.inflate(R.layout.contact_progress_dialog, null);
			builder = new AlertDialog.Builder(context);
			dialog2 = builder.setView(view).create();
			dialog2.show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// 获取Contact的字段值,并插入数据库
					String name = edtName.getText().toString();
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
						string = PinyinHelper.toHanyuPinyinString(name, output,
								"-");
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String[] strs = string.split("-");
					for (String str : strs)
						namePinyin = namePinyin + str.substring(0, 1);
					CommonUtil.Log("sqy", "toHanyuPinyinString", namePinyin,
							'i');

					String nickName = ((EditText) rlAddNickname
							.findViewById(R.id.edt_inputnickname)).getText()
							.toString();
					String address = ((EditText) rlAddAddress
							.findViewById(R.id.edt_inputaddress)).getText()
							.toString();
					String company = ((EditText) rlAddCompany
							.findViewById(R.id.edt_inputcompany)).getText()
							.toString();
					String birthday = ((TextView) rlAddBirthday
							.findViewById(R.id.edit_inputbirthday)).getText()
							.toString();
					String idNo = ((EditText) rlAddIdNo
							.findViewById(R.id.edt_inputidno)).getText()
							.toString();
					String describe = ((EditText) rlAddDescribe
							.findViewById(R.id.edt_inputdescribe)).getText()
							.toString();
					String gender = ((TextView) rlAddGender
							.findViewById(R.id.edt_inputgender)).getText()
							.toString();
					String type = ((TextView) rlAddType
							.findViewById(R.id.edt_inputtype)).getText()
							.toString();
					String fileNo = ((EditText) rlAddFileNo
							.findViewById(R.id.edt_inputfileno)).getText()
							.toString();
					String hospital = ((EditText) rlAddHospital
							.findViewById(R.id.edt_inputhospital)).getText()
							.toString();
					String relationName = ((EditText) rlAddRelationName
							.findViewById(R.id.edt_inputrelationname))
							.getText().toString();
					String relation = ((EditText) rlAddRelation
							.findViewById(R.id.edt_inputrelation)).getText()
							.toString();
					String relationTele = ((EditText) rlAddRelationTele
							.findViewById(R.id.edt_inputrelationtele))
							.getText().toString();
					String relationEmail = ((EditText) rlAddRelationEmail
							.findViewById(R.id.edt_inputrelationemail))
							.getText().toString();
					if (birthday.equals("无"))
						birthday = "";
					String note = ((EditText) rlAddNote
							.findViewById(R.id.edt_inputnote)).getText()
							.toString();
					int groupId = 0;
					String groupName = ((TextView) llSelectGroup
							.findViewById(R.id.txt_groupname)).getText()
							.toString();
					if (groupName.equals("无"))
						groupId = 0;
					else
						groupId = groupMgr.getGroupsByName(groupName)
								.getGroupId();
					byte[] image;
					if (flagBtnImage || nowContactId == -1) {// 头像改变了
						Bitmap bitmap = ImageTools
								.getBitmapFromDrawable(imgPickPhoto
										.getDrawable());
						image = ImageTools.getByteFromBitmap(bitmap);
					} else {
						image = oldImage;
					}

					int id = 0;
					if (nowContactId == -1) {
						contactMgr.addContact(name, namePinyin, nickName,
								address, company, birthday, note, image,
								groupId, idNo, describe, hospital, type,
								fileNo, gender, relationName, relation,
								relationTele, relationEmail,MemberManage.doctorID);// 将联系人存入数据库
						// 获得插入联系人的ID号
						List<Contact> list = contactMgr.getContactsByName(name);
						if (list == null) {
							CommonUtil.Toast(context, "添加联系人失败!");
							return;
						}
						id = list.get((list.size() - 1)).getId();
					} else {
						contactMgr.modifyContact(nowContactId, name,
								namePinyin, nickName, address, company,
								birthday, note, image, groupId, idNo, describe,
								hospital, type, fileNo, gender, relationName,
								relation, relationTele, relationEmail,MemberManage.doctorID);// 修改联系人
					}
					if (nowContactId == -1) {
						contactID = id;
					} else {
						contactID = nowContactId;
					}

					// 获取Tel的字段值,并插入数据库
					if (nowContactId > 0)// 如果是修改联系人，则先删除所有电话号码
						telMgr.delTelByContactId(nowContactId);
					for (int i = 0; i < llTel.getChildCount(); i++) {
						View view = llTel.getChildAt(i);
						EditText edtTel = (EditText) view
								.findViewById(R.id.edt_inputphone);
						if (!edtTel.getText().toString().equals("")) {
							String telNumber = edtTel.getText().toString();
							String telName = ((TextView) view
									.findViewById(R.id.txt_phone)).getText()
									.toString();
							telMgr.addTel(contactID, telName, telNumber);// 将Tel添加到数据库
						}
					}

					// 获取Email的字段值,并插入数据库
					if (nowContactId > 0)// 如果是修改联系人，则先删除所有电话号码
						emailMgr.delEmailByContactId(nowContactId);
					for (int i = 0; i < llEmail.getChildCount(); i++) {
						View view = llEmail.getChildAt(i);
						EditText edtEmail = (EditText) view
								.findViewById(R.id.edt_inputemail);
						if (!edtEmail.getText().toString().equals("")) {
							String emailAcount = edtEmail.getText().toString();
							String emailName = ((TextView) view
									.findViewById(R.id.txt_email)).getText()
									.toString();
							emailMgr.addEmail(contactID, emailName, emailAcount);// 将Email添加到数据库
						}
					}

					// 获取IM的字段值,并插入数据库
					if (nowContactId > 0)
						imMgr.delIMByContactId(nowContactId);
					for (int i = 0; i < llIM.getChildCount(); i++) {
						View view = llIM.getChildAt(i);
						EditText edtIM = (EditText) view
								.findViewById(R.id.edt_inputIm);
						if (!edtIM.getText().toString().equals("")) {
							String imAcount = edtIM.getText().toString();
							String imName = ((TextView) view
									.findViewById(R.id.txt_im)).getText()
									.toString();
							imMgr.addIM(contactID, imName, imAcount);// 将Email添加到数据库
						}
					}
					handler.sendMessage(new Message());
				}
			}).start();
		}
	};

	/**
	 * Activity回调函数
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap bitmap = null;
		// 文件返回的
		if (requestCode == REQUEST_BTN_FILE_SELECT
				&& resultCode == FileSelectActivity.RESULT_FILE_SELECTED) {
			String imagePath = data.getStringExtra("path");
			File file = new File(imagePath);
			CommonUtil.Log("sqy", "file.length()", file.length() + "", 'i');
			try {
				if (file.length() > 800000)// 压缩后转换为Bitmap
					bitmap = ImageTools.saveBefore(imagePath);
				else
					bitmap = ImageTools.getBitemapFromFile(file);
				CommonUtil.Log("sqy", "onActivityResult", imagePath, 'i');
				// 将图片转换为指定大小的图片,并显示在头像上
				imgPickPhoto.setImageBitmap(ImageTools.createBitmapBySize(
						bitmap, 77, 77));
				flagBtnImage = true;
			} catch (Exception ex) {
				CommonUtil.Toast(context, "图片过大，无法显示");
			}
		}
		// 照相机返回的
		if (requestCode == Activity.DEFAULT_KEYS_DIALER) {
			try {
				// Add the image to the media store
				Intent intentScan = new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				sendBroadcast(intentScan);
				// Launch gallery to crop the photo
				final Intent intent = getCropImageIntent(Uri
						.fromFile(mCurrentPhotoFile));
				startActivityForResult(intent, 3);
			} catch (Exception e) {
				CommonUtil.Log("sqy", "Devdiv", "Cannot crop image", 'i');
				CommonUtil.Toast(context, "not find photo ");
			}
		}
		// 得到的裁剪后的photo的bitmap对象
		if (requestCode == 3)
			if (data != null) {
				cameraBitmap = data.getParcelableExtra("data");
				imgPickPhoto.setImageBitmap(cameraBitmap);
				flagBtnImage = true;
			}
	}

	/**
	 * 显示DatePickerDialog,用以选择联系人生日
	 */
	public void showDatePickerDialog() {
		calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		datePickerDialog = new DatePickerDialog(context,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						setDate = String.valueOf(year) + "-"
								+ String.valueOf(monthOfYear + 1) + "-"
								+ String.valueOf(dayOfMonth);
						((TextView) rlAddBirthday
								.findViewById(R.id.edit_inputbirthday))
								.setText(setDate);// 显示当前设置的时间
					}
				}, mYear, mMonth, mDay);
		datePickerDialog.show();
	}

	/**
	 * 显示选择菜单
	 */
	public void showMenuDialog(int flag) {
		LayoutInflater inflater = LayoutInflater.from(NewContactActivity.this);
		// 引入窗口配置文件
		if (flag == 1) {
			View view = inflater.inflate(R.layout.choose_dialog_gender, null);
			WindowManager wm = (WindowManager) this
					.getSystemService(Context.WINDOW_SERVICE);
			int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
			// 创建PopupWindow对象
			final PopupWindow pop = new PopupWindow(view, width / 3,
					LayoutParams.WRAP_CONTENT, true);
			if (pop.isShowing()) {
				// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
				pop.dismiss();
			} else {
				// 显示窗口
				pop.showAtLocation(view, Gravity.CENTER, 0, 0);
			}
			RadioButton btn1 = (RadioButton) view.findViewById(R.id.male);
			RadioButton btn2 = (RadioButton) view.findViewById(R.id.female);

			btn1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// Toast.makeText(context, "nan", Toast.LENGTH_LONG).show();
					((TextView) rlAddGender.findViewById(R.id.edt_inputgender))
							.setText("男");
					pop.dismiss();
				}
			});
			btn2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// Toast.makeText(context, "nv", Toast.LENGTH_LONG).show();
					((TextView) rlAddGender.findViewById(R.id.edt_inputgender))
							.setText("女");
					pop.dismiss();
				}
			});
		} else if (flag == 2) {
			View view = inflater.inflate(R.layout.choose_dialog_type, null);
			WindowManager wm = (WindowManager) this
					.getSystemService(Context.WINDOW_SERVICE);
			int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
			// 创建PopupWindow对象
			final PopupWindow pop = new PopupWindow(view, width / 3,
					LayoutParams.WRAP_CONTENT, true);
			if (pop.isShowing()) {
				// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
				pop.dismiss();
			} else {
				// 显示窗口
				pop.showAtLocation(view, Gravity.CENTER, 0, 0);
			}
			RadioButton btn1 = (RadioButton) view.findViewById(R.id.typeA);
			RadioButton btn2 = (RadioButton) view.findViewById(R.id.typeB);

			btn1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// Toast.makeText(context, "nan", Toast.LENGTH_LONG).show();
					((TextView) rlAddType.findViewById(R.id.edt_inputtype))
							.setText("医保A");
					pop.dismiss();
				}
			});
			btn2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// Toast.makeText(context, "nv", Toast.LENGTH_LONG).show();
					((TextView) rlAddType.findViewById(R.id.edt_inputtype))
							.setText("医保B");
					pop.dismiss();
				}
			});
		}

	}

	/**
	 * 添加地址栏
	 */
	public void AddAddressView() {
		rlAddAddress.setVisibility(View.VISIBLE);
		((EditText) rlAddAddress.findViewById(R.id.edt_inputaddress))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddAddress.findViewById(R.id.imb_del_address).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddAddress.setVisibility(View.GONE);
						((EditText) rlAddAddress
								.findViewById(R.id.edt_inputaddress))
								.setText("");
					}
				});
	}

	/**
	 * 添加公司栏
	 */
	public void AddCompanyView() {
		rlAddCompany.setVisibility(View.VISIBLE);
		((EditText) rlAddCompany.findViewById(R.id.edt_inputcompany))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddCompany.findViewById(R.id.imb_del_company).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddCompany.setVisibility(View.GONE);
						((EditText) rlAddCompany
								.findViewById(R.id.edt_inputcompany))
								.setText("");
					}
				});
	}

	/**
	 * 添加昵称栏
	 */
	public void AddNicknameView() {
		rlAddNickname.setVisibility(View.VISIBLE);
		((EditText) rlAddNickname.findViewById(R.id.edt_inputnickname))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddNickname.findViewById(R.id.imb_del_nickname).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddNickname.setVisibility(View.GONE);
						((EditText) rlAddNickname
								.findViewById(R.id.edt_inputnickname))
								.setText("");
					}
				});
	}

	/**
	 * 添加身份证号
	 */
	public void AddIdNoView() {
		rlAddIdNo.setVisibility(View.VISIBLE);
		((EditText) rlAddIdNo.findViewById(R.id.edt_inputidno))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddIdNo.findViewById(R.id.imb_del_idno).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddIdNo.setVisibility(View.GONE);
						((EditText) rlAddIdNo.findViewById(R.id.edt_inputidno))
								.setText("");
					}
				});
	}

	/**
	 * 添加身份证号
	 */
	public void AddDescribeView() {
		rlAddDescribe.setVisibility(View.VISIBLE);
		((EditText) rlAddDescribe.findViewById(R.id.edt_inputdescribe))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddDescribe.findViewById(R.id.imb_del_describe).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddDescribe.setVisibility(View.GONE);
						((EditText) rlAddDescribe
								.findViewById(R.id.edt_inputdescribe))
								.setText("");
					}
				});
	}

	/**
	 * 添加身份证号
	 */
	public void AddGenderView() {
		rlAddGender.setVisibility(View.VISIBLE);
		// 点击删除按钮监听事件
		rlAddGender.findViewById(R.id.imb_del_gender).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddGender.setVisibility(View.GONE);
						((TextView) rlAddGender
								.findViewById(R.id.edt_inputgender))
								.setText("");
					}
				});
		// show the choose items
		rlAddGender.findViewById(R.id.edt_inputgender).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showMenuDialog(1);
					}
				});
	}

	/**
	 * 添加身份证号
	 */
	public void AddTypeView() {
		rlAddType.setVisibility(View.VISIBLE);
		// 点击删除按钮监听事件
		rlAddType.findViewById(R.id.imb_del_type).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddType.setVisibility(View.GONE);
						((TextView) rlAddType.findViewById(R.id.edt_inputtype))
								.setText("");
					}
				});
		// show the choose items
		rlAddType.findViewById(R.id.edt_inputtype).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						showMenuDialog(2);
					}
				});
	}

	/**
	 * 添加身份证号
	 */
	public void AddFileNoView() {
		rlAddFileNo.setVisibility(View.VISIBLE);
		((EditText) rlAddFileNo.findViewById(R.id.edt_inputfileno))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddFileNo.findViewById(R.id.imb_del_fileno).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddFileNo.setVisibility(View.GONE);
						((EditText) rlAddFileNo
								.findViewById(R.id.edt_inputfileno))
								.setText("");
					}
				});
	}

	/**
	 * 添加身份证号
	 */
	public void AddHospitalView() {
		rlAddHospital.setVisibility(View.VISIBLE);
		((EditText) rlAddHospital.findViewById(R.id.edt_inputhospital))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddHospital.findViewById(R.id.imb_del_hospital).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddHospital.setVisibility(View.GONE);
						((EditText) rlAddHospital
								.findViewById(R.id.edt_inputhospital))
								.setText("");
					}
				});
	}

	/**
	 * 添加联系人姓名
	 */
	public void AddRelationNameView() {
		rlAddRelationName.setVisibility(View.VISIBLE);
		((EditText) rlAddRelationName.findViewById(R.id.edt_inputrelationname))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddRelationName.findViewById(R.id.imb_del_relationname)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddRelationName.setVisibility(View.GONE);
						((EditText) rlAddRelationName
								.findViewById(R.id.edt_inputrelationname))
								.setText("");
					}
				});
	}

	/**
	 * 添加联系人关系
	 */
	public void AddRelationView() {
		rlAddRelation.setVisibility(View.VISIBLE);
		((EditText) rlAddRelation.findViewById(R.id.edt_inputrelation))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddRelation.findViewById(R.id.imb_del_relation).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddRelation.setVisibility(View.GONE);
						((EditText) rlAddRelation
								.findViewById(R.id.edt_inputrelation))
								.setText("");
					}
				});
	}

	/**
	 * 添加联系人电话栏
	 */
	public void AddRelationTeleView() {
		rlAddRelationTele.setVisibility(View.VISIBLE);
		((EditText) rlAddRelationTele.findViewById(R.id.edt_inputrelationtele))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddRelationTele.findViewById(R.id.imb_del_relationtele)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddRelationTele.setVisibility(View.GONE);
						((EditText) rlAddRelationTele
								.findViewById(R.id.edt_inputrelationtele))
								.setText("");
					}
				});
		rlAddRelationTele.findViewById(R.id.imb_dial_relationtele)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String teleString = ((EditText) rlAddRelationTele
								.findViewById(R.id.edt_inputrelationtele))
								.getText().toString();
						try {
							Intent intent = new Intent(Intent.ACTION_DIAL, Uri
									.parse("tel:" + teleString));
							startActivity(intent);
						} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(context, "此平板不支持此项功能",
									Toast.LENGTH_LONG).show();
						}
					}
				});
		rlAddRelationTele.findViewById(R.id.imb_send_relationtele)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String teleString = ((EditText) rlAddRelationTele
								.findViewById(R.id.edt_inputrelationtele))
								.getText().toString();
						try {
							Uri smsToUri = Uri.parse("smsto://" + teleString);
							Intent mIntent = new Intent(
									android.content.Intent.ACTION_SENDTO,
									smsToUri);
							// mIntent.putExtra("sms_body","the sms text");
							startActivity(mIntent);
						} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(context, "此平板不支持此项功能",
									Toast.LENGTH_LONG).show();
						}

					}
				});
	}

	/**
	 * 添加联系人邮箱栏
	 */
	public void AddRelationEmailView() {
		rlAddRelationEmail.setVisibility(View.VISIBLE);
		((EditText) rlAddRelationEmail.findViewById(R.id.edt_inputrelationemail))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddRelationEmail.findViewById(R.id.imb_del_relationemail)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddRelationEmail.setVisibility(View.GONE);
						((EditText) rlAddRelationEmail
								.findViewById(R.id.edt_inputrelationemail))
								.setText("");
					}
				});
		rlAddRelationEmail.findViewById(R.id.imb_send_relationemail)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						EditText emailEditText = (EditText) rlAddRelationEmail
								.findViewById(R.id.edt_inputrelationemail);
						try {
							Uri uri = Uri.parse("mailto:"
									+ emailEditText.getText().toString());
							Intent intent = new Intent(Intent.ACTION_SENDTO,
									uri);
							startActivity(intent);
						} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(context, "此平板不支持此项功能",
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	/**
	 * 添加生日栏
	 */
	public void AddBirthdayView() {
		rlAddBirthday.setVisibility(View.VISIBLE);
		// 点击删除按钮监听事件
		rlAddBirthday.findViewById(R.id.imb_del_birthday).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddBirthday.setVisibility(View.GONE);
						((TextView) rlAddBirthday
								.findViewById(R.id.edit_inputbirthday))
								.setText("无");
					}
				});
		// 点击当前日期弹出对话框重新选择日期
		rlAddBirthday.findViewById(R.id.edit_inputbirthday).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showDatePickerDialog();// 显示日期设置对话框
					}
				});
	}

	/**
	 * 添加备注栏
	 */
	public void AddNoteView() {
		rlAddNote.setVisibility(View.VISIBLE);
		((EditText) rlAddNote.findViewById(R.id.edt_inputnote))
		.requestFocus();
		// 点击删除按钮监听事件
		rlAddNote.findViewById(R.id.imb_del_note).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						rlAddNote.setVisibility(View.GONE);
						((EditText) rlAddNote.findViewById(R.id.edt_inputnote))
								.setText("");
					}
				});
	}

	/**
	 * 添加一个Tel编辑框
	 */
	public void addViewToTel() {
		telCount++;
		if (telCount == 1) {
			rlTelTitle.setVisibility(View.VISIBLE);
			llTel.setVisibility(View.VISIBLE);
		}
		final View view = inflater.inflate(R.layout.contact_vlist_phone, null);
		llTel.addView(view);
		((EditText) view.findViewById(R.id.edt_inputphone)).requestFocus();
		if (newTelNumber != null && !newTelNumber.equals("")) {
			EditText edtTel = (EditText) view.findViewById(R.id.edt_inputphone);
			edtTel.setText(newTelNumber);
			newTelNumber = "";
		}
		ImageButton imbDel = (ImageButton) view
				.findViewById(R.id.imb_del_phone);
		ImageButton imbDial = (ImageButton) view
				.findViewById(R.id.imb_dial_tele);
		ImageButton imbSend = (ImageButton) view
				.findViewById(R.id.imb_send_tele);
		RelativeLayout rlSelectTel = (RelativeLayout) view
				.findViewById(R.id.phoneTitle);
		final TextView txtTel = (TextView) view.findViewById(R.id.txt_phone);
		imbDel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				llTel.removeView(view);
				telCount--;
				if (telCount == 0) {
					rlTelTitle.setVisibility(View.GONE);
					llTel.setVisibility(View.GONE);
				}
			}
		});
		imbDial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText edtTel = (EditText) view
						.findViewById(R.id.edt_inputphone);
				try {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + edtTel.getText().toString()));
					startActivity(intent);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(context, "此平板不支持此项功能", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		imbSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText edtTel = (EditText) view
						.findViewById(R.id.edt_inputphone);
				try {
					Uri smsToUri = Uri.parse("smsto://"
							+ edtTel.getText().toString());
					Intent mIntent = new Intent(
							android.content.Intent.ACTION_SENDTO, smsToUri);
					// mIntent.putExtra("sms_body","the sms text");
					startActivity(mIntent);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(context, "此平板不支持此项功能", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		rlSelectTel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder = new AlertDialog.Builder(context);
				builder.setTitle("请选择标签")
						.setItems(mItemsTel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										txtTel.setText(mItemsTel[which]);
									}
								}).create().show();
			}
		});
	}

	/**
	 * 添加一个Email编辑框
	 */
	public void addViewToEmail() {
		emailCount++;
		if (emailCount == 1) {
			rlEmailTitle.setVisibility(View.VISIBLE);
			llEmail.setVisibility(View.VISIBLE);
		}
		final View view = inflater.inflate(R.layout.contact_vlist_email, null);
		llEmail.addView(view);
		((EditText) view.findViewById(R.id.edt_inputemail)).requestFocus();
		ImageButton imbDel = (ImageButton) view
				.findViewById(R.id.imb_del_email);
		ImageButton imbSend = (ImageButton) view
				.findViewById(R.id.imb_send_email);
		RelativeLayout rlSelectEmail = (RelativeLayout) view
				.findViewById(R.id.emailTitle);

		final TextView txtEmail = (TextView) view.findViewById(R.id.txt_email);
		imbDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				llEmail.removeView(view);
				emailCount--;
				if (emailCount == 0) {
					rlEmailTitle.setVisibility(View.GONE);
					llEmail.setVisibility(View.GONE);
				}
			}
		});
		imbSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText edtemailEditText = (EditText) view
						.findViewById(R.id.edt_inputemail);
				try {
					Uri uri = Uri.parse("mailto:"
							+ edtemailEditText.getText().toString() + "");
					Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
					startActivity(intent);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(context, "此平板不支持此项功能", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		rlSelectEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder = new AlertDialog.Builder(context);
				builder.setTitle("请选择标签")
						.setItems(mItemsEmail,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										txtEmail.setText(mItemsEmail[which]);
									}
								}).create().show();
			}
		});
	}

	/**
	 * 添加一个IM编辑框
	 */
	public void addViewToIM() {
		imCount++;
		if (imCount == 1) {
			rlIMTitle.setVisibility(View.VISIBLE);
			llIM.setVisibility(View.VISIBLE);
		}
		final View view = inflater.inflate(R.layout.contact_vlist_im, null);
		llIM.addView(view);

		CommonUtil.Log("sqy", "addViewToIM", "view的个数：" + llIM.getChildCount(),
				'i');

		ImageButton imbDel = (ImageButton) view.findViewById(R.id.imb_del_im);
		RelativeLayout rlSelectIM = (RelativeLayout) view
				.findViewById(R.id.imTitle);
		final TextView txtIM = (TextView) view.findViewById(R.id.txt_im);
		imbDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				llIM.removeView(view);
				imCount--;
				if (imCount == 0) {
					rlIMTitle.setVisibility(View.GONE);
					llIM.setVisibility(View.GONE);
				}
			}
		});
		rlSelectIM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder = new AlertDialog.Builder(context);
				builder.setTitle("请选择标签")
						.setItems(mItemsIM,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										txtIM.setText(mItemsIM[which]);
									}
								}).create().show();
			}
		});
	}

	/**
	 * 监听系统按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			handler.sendMessage(new Message());
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * image的布局adapter
	 */
	class ImageAdapter extends BaseAdapter {
		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		public int getCount() {
			// return mImageIds.length;
			return 100000;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(context);
			iv.setImageResource(mImageIds[position % mImageIds.length]);
			iv.setAdjustViewBounds(true);
			iv.setLayoutParams(new Gallery.LayoutParams(100, 100));// 设置Gallery中图片的大小
			iv.setPadding(15, 10, 15, 10);
			return iv;
		}

	}
}