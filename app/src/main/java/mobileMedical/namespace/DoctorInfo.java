package mobileMedical.namespace;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.Contacts.UI.FileSelectActivity;
import mobileMedical.adapter.ListViewAdapter;
import mobileMedical.adapter.ListViewThreeItemAdapter;
import mobileMedical.database.DBManager;
import mobileMedical.util.Utility;

public class DoctorInfo extends Activity {

	private Context context;
	private List<Map<String, Object>> doctorInfoList;
	private ListViewAdapter doctorInfoListAdapter;
	private ListView doctorInfolv;

	private List<Map<String, Object>> doctorRecordInfoList;
	private ListViewThreeItemAdapter doctorRecordInfoListAdapter;
	private ListView doctorRecordInfolv;

	private ImageButton imageButtonDoctor;
	private View popView;
	private PopupWindow popupWindow;
	private LayoutInflater inflater;// 反射
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	private File mCurrentPhotoFile;
	private AlertDialog.Builder builder;// 对话框创建器
	private AlertDialog dialog;// 对话框
	/** 系统头像资源 **/
	private Integer[] mImageIds = { R.drawable.contact_icon,
			R.drawable.contact_photo1, R.drawable.contact_photo10,
			R.drawable.contact_photo11, R.drawable.contact_photo12,
			R.drawable.contact_photo13 };
	private Gallery gallery;
	private int currentImagePosition;// 用于记录当前选中图像在图像数组中的位置，默认值为0
	public static final int REQUEST_BTN_FILE_SELECT = 2;
	private Bitmap cameraBitmap;

	private DBManager dbManager;
	boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);
	private ContactManager contactMgr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.doctor_manger);
		context = this;
		initView();
		dbManager = new DBManager(DoctorInfo.this);

		byte[] image = dbManager.getImageByDoctorId(MemberManage.doctorID + "");
		if (image != null) {
			Bitmap bitmap = ImageTools.getBitmapFromByte(image);
			imageButtonDoctor.setImageBitmap(bitmap);
		}

		initDoctorBasicInfoList();
		// set up the doctor basic info list
		doctorInfoListAdapter = new ListViewAdapter(this, doctorInfoList); // 创建适配器
		doctorInfolv.setAdapter(doctorInfoListAdapter);
		Utility.setListViewHeightBasedOnChildren(doctorInfolv);

		doctorInfolv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (doctorInfoList.get(arg2).get("title").toString().split("：")[0]
						.trim().equalsIgnoreCase(
								getString(R.string.DoctorInfoId))) {
					Toast.makeText(DoctorInfo.this,
							getString(R.string.DoctorInfoIdNotChange),
							Toast.LENGTH_SHORT).show();
					return false;
				}
				final EditText inputServer = new EditText(DoctorInfo.this);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						DoctorInfo.this);
				builder.setTitle(
						doctorInfoList.get(arg2).get("title").toString()
								.split("：")[0].trim())
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(inputServer).setNegativeButton("Cancel", null);
				builder.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dbManager.updateOneDoctorInfo(doctorInfoList
										.get(arg2).get("title").toString()
										.split("：")[0].trim(), inputServer
										.getText().toString().trim(),
										MemberManage.doctorID + "");
								doctorInfoList.get(arg2)
										.put("info",
												inputServer.getText()
														.toString().trim());
								doctorInfoListAdapter.notifyDataSetChanged();
							}
						});
				builder.show();
				return false;
			}
		});

		initDoctorRecordInfoList();
		// set up the doctor record info list
		doctorRecordInfoListAdapter = new ListViewThreeItemAdapter(this,
				doctorRecordInfoList); // 创建适配器
		doctorRecordInfolv.setAdapter(doctorRecordInfoListAdapter);
		Utility.setListViewHeightBasedOnChildren(doctorRecordInfolv);

		doctorRecordInfolv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// CommonUtil.Toast(DoctorInfo.this,"hello   arg1  "+arg1+"  arg2  "+arg2+"  arg3  "+arg3);
				String nameString = doctorRecordInfoList.get(arg2).get("title")
						.toString();
				MemberManage.patientID = contactMgr
						.getContactsByName(nameString).get(0).getId();
				finish();
			}
		});
		imageButtonDoctor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (popupWindow == null) {
					popView = inflater.inflate(
							R.layout.contact_popup_photo_select, null);
					popupWindow = new PopupWindow(popView,
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
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
				popupWindow.showAsDropDown(imageButtonDoctor, 70, -70);// 弹出选择头像的PopoupWindow
				txtAddImageFromPhoto.setOnClickListener(onClickListener1);// 监听从照相机获取头像
				txtAddImageFromSystem.setOnClickListener(onClickListener1);// 监听从系统获取头像
				txtAddImageFromFile.setOnClickListener(onClickListener1);// 监听从文件获取头像
			}
		});
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
										imageButtonDoctor
												.setImageResource(mImageIds[currentImagePosition
														% mImageIds.length]);
										saveImageToDB();
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

	private void initView() {
		contactMgr = new ContactManager(this);
		doctorInfolv = (ListView) this.findViewById(R.id.listViewDoctorInfo);
		imageButtonDoctor = (ImageButton) this
				.findViewById(R.id.imageButtonDoctor);
		doctorRecordInfolv = (ListView) this
				.findViewById(R.id.listViewDoctorRecordInfo);
		inflater = LayoutInflater.from(context);
		builder = new AlertDialog.Builder(context);
	}

	/**
	 * set up the patient basic info table
	 */
	private void initDoctorBasicInfoList() {
		// TODO Auto-generated method stub
		Cursor cursor = dbManager.queryDoctorInfo(MemberManage.doctorID);
		String username = "", id = "", work = "", room = "", level = "", tel = "", email = "";

		if (cursor.moveToNext()) {
			username = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			id = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_ID));
			work = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_WORK));
			room = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_ROOM));
			level = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_LEVEL));
			tel = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_TEL));
			email = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_EMAIL));
		}
		cursor.close();
		dbManager.closeDB();

		doctorInfoList = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", getString(R.string.DoctorInfoName) + "：");
		map.put("info", username);
		doctorInfoList.add(map);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("title", getString(R.string.DoctorInfoId) + "：");
		map2.put("info", id);
		doctorInfoList.add(map2);

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("title", getString(R.string.DoctorInfoWork) + "：");
		map3.put("info", work);
		doctorInfoList.add(map3);

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("title", getString(R.string.DoctorInfoRoom) + "：");
		map4.put("info", room);
		doctorInfoList.add(map4);

		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("title", getString(R.string.DoctorInfoLevel) + "：");
		map5.put("info", level);
		doctorInfoList.add(map5);

		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("title", getString(R.string.DoctorInfoTele) + "：");
		map6.put("info", tel);
		doctorInfoList.add(map6);

		Map<String, Object> map7 = new HashMap<String, Object>();
		map7.put("title", getString(R.string.DoctorInfoEmail) + "：");
		map7.put("info", email);
		doctorInfoList.add(map7);

	}

	private void initDoctorRecordInfoList() {
		// TODO Auto-generated method stub
		
		Cursor cursor = boDbHelper.getDoctorRecordInfo();
		String patientID = "", sensorType = "";
		String dateString = "";
		String doctor = "";
		int doctorId = 0 ;

		doctorRecordInfoList = new ArrayList<Map<String, Object>>();

		while (cursor.moveToNext()) {
			patientID = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_PATIENTID));
			sensorType = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_SENSORTYPE));
			dateString = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));

			sensorType = MemberManage.turnSensorTypeFromNum(sensorType);
			dateString = MemberManage.turnTimeToCNTime(dateString);
			
			doctorId= cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c=dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor= c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			c.close();
			dbManager.closeDB();

			Contact contact = contactMgr.getContactById(Integer
					.valueOf(patientID));

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", contact.getName());
			map.put("info", sensorType);
			map.put("time", dateString);
			map.put("doctor", doctor);
			map.put("doctorId", doctorId);
			doctorRecordInfoList.add(map);
		}
		cursor.close();
		boDbHelper.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent = new Intent(DoctorInfo.this, MemberManage.class);
			// startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

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
				imageButtonDoctor.setImageBitmap(ImageTools.createBitmapBySize(
						bitmap, 77, 77));
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
		if (requestCode == 3) {
			if (data != null) {
				cameraBitmap = data.getParcelableExtra("data");
				imageButtonDoctor.setImageBitmap(cameraBitmap);
			}
		}

		// save to db
		saveImageToDB();
	}

	private void saveImageToDB() {
		byte[] image;
		Bitmap newbitmap = ImageTools.getBitmapFromDrawable(imageButtonDoctor
				.getDrawable());
		image = ImageTools.getByteFromBitmap(newbitmap);
		dbManager.updateDoctorImage(MemberManage.doctorID + "", image);
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
	
	@Override
	public synchronized void onResume() {
		super.onResume();
		setTitle(getString(R.string.doctor));
//		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.lineborder); 
	}
}
