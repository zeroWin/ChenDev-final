package mobileMedical.namespace;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.util.Utility;

public class BodyTempStatistics extends TabActivity {
	boDb btDbHelper = new boDb(this, "bloodox.db", null, 1);

	private TabHost tabHost = null;
	private int mCurrentTabIndex = -1;

	private final boolean D = true;

	private final String TAG = "BodyTempStatitics";

	private TextView patientName;
	private TextView textViewResult;
	private TextView textViewHospital;
	private TextView textViewYibao;
	private TextView textViewFileNum;
	private TextView textViewMaleFemale;
	private TextView textViewAgeNum;
	private ImageButton memberImage;

	private ContactManager contactMgr;
	private Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.body_temp_st);
		context = this;
		initView();
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
		/*
		 * Cursor cursor = btDbHelper.getReadDataFile(); boolean readDataFile =
		 * false;
		 * 
		 * 
		 * 
		 * if (cursor.getCount()== 0 ) { readDataFile = true;
		 * btDbHelper.insertReadDataFile(1); } else { if(cursor.moveToNext()) {
		 * int readFile = cursor.getInt(cursor.getColumnIndex("readDataFile"));
		 * if (readFile == 0) { readDataFile = true;
		 * btDbHelper.UpdateReadDataFile(1); } else { readDataFile = false; } }
		 * else { readDataFile = true; btDbHelper.insertReadDataFile(1); } }
		 * 
		 * 
		 * if (readDataFile) { // DataFile is not read to SQL;
		 * MeasDataFilesOperator measdFilesOperator = new
		 * MeasDataFilesOperator(this); String[] measDataStrings =
		 * measdFilesOperator.ReadMeasDataFromFile(true); if (measDataStrings !=
		 * null) { String contentString = null; String[] measDataInfo = null;
		 * int patientID = -1; int measItemID = -1; String measTimeString =
		 * null; Date time = null; for (int i = 0; i < measDataStrings.length;
		 * i++) { contentString = measDataStrings[i]; // we can also use the
		 * RegularExpressions if(contentString.split(",").length == 1) { // If
		 * it includes over one item", it is the measDataInfo, Otherwise, it is
		 * the MeasData double measRet = Double.parseDouble(contentString);
		 * if(measItemID == MessageInfo.MM_MI_BODY_TEMPERATURE) {
		 * SimpleDateFormat format = new
		 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss"); try { time =
		 * format.parse(measTimeString); } catch (ParseException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * btDbHelper.insertBt(patientID,(float)measRet,time); } else if
		 * (measItemID == MessageInfo.MM_MI_BODY_BASE_TEMPERATURE) {
		 * SimpleDateFormat format = new
		 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss"); try { time =
		 * format.parse(measTimeString); } catch (ParseException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * btDbHelper.insertBtBase(patientID,(float)measRet,time); }
		 * 
		 * } else if (contentString.split(",").length == 3) { // First string
		 * should be measItem measDataInfo = contentString.split(",");
		 * measItemID = Integer.parseInt(measDataInfo[0]); patientID =
		 * Integer.parseInt(measDataInfo[1]); measTimeString = measDataInfo[2];
		 * } else {
		 * 
		 * // error
		 * 
		 * }
		 * 
		 * } }
		 * 
		 * // Update the database readFile flag // i equal True
		 * 
		 * }
		 */

		tabHost = getTabHost();

		TabSpec BodyTempHistory = tabHost.newTabSpec("BodyTempHistory");
		// BloodOxSpec.setIndicator(view);
		BodyTempHistory.setIndicator("历史数据");
		Intent BodyTempHistoryIntent = new Intent(this, BodyTempHistory.class);
		BodyTempHistory.setContent(BodyTempHistoryIntent);

		tabHost.addTab(BodyTempHistory);

		tabHost.addTab(tabHost.newTabSpec("BodyTempLine")
				.setIndicator("整体体温波形图")
				.setContent(new Intent(this, BodyTempStLine.class)));

		tabHost.addTab(tabHost.newTabSpec("BodyTempBaseColum")
				.setIndicator("基础体温")
				.setContent(new Intent(this, BaseBodyTempStColum.class)));

		tabHost.addTab(tabHost.newTabSpec("BodyTempNumber")
				.setIndicator("数据统计")
				.setContent(new Intent(this, BodyTempStTable.class)));

		tabHost.addTab(tabHost.newTabSpec("BodyTempColum").setIndicator("直方图")
				.setContent(new Intent(this, BodyTempStColum.class)));

		tabHost.addTab(tabHost.newTabSpec("BodyTempTrendLine")
				.setIndicator("趋势分析")
				.setContent(new Intent(this, BodyTempStTrendLine.class)));

		TabWidget tabWidget = tabHost.getTabWidget();
		int width = this.getWindowManager().getDefaultDisplay().getWidth() / 5;
		// ???√≥√§?¨?￠???√?√∫???√?√????√?√￥???√?√????√?√?√?√ú?à????à?√??¨????¨?￥???√?√??àü√≠?¨?±???√??à?Tabhost?àü√≠?¨???¨???aà√¨???√?√?√?√≤?¨???≈í???¨?μ?àü√≠?¨?μ?à?√∏???√?√′≈í???¨?￡?¨?????√?√∫?¨??￠???√?√á?¨√ü≈í???¨?????√?√￥???√?√????√￠???à??àè???√?√′?à??à??¨??￠√?√￥?¨???à?√????√?√′???√￠√????√?√≤???√?√????√????aà√¨???√?√??à?√§???√?√￥???√?√????√?√á?aà√≠?¨??￠√?√￥?¨???à?√??¨?μ?àü√≠?aà√¨?¨?????√?√??à?√§?¨?￡?¨?????√￠???¨??￠?¨???¨??￠???√?√?√?√≤?¨???≈í?????√?√??àü√≠?¨?±???√??à????√￠???¨??￠?¨?∞?à?√?
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			// ???√?√¨√???√????√?√??àü√≠Tabhost???√?√?√???√á???√?√á?¨?????√??￥?à?√??à??àè?à?√????√?√á?¨??
			tabWidget.getChildAt(i).getLayoutParams().height = 50;
			tabWidget.getChildAt(i).getLayoutParams().width = width;
			// ???√?√¨√???√????√?√??àü√≠?aà√¨???√?√≤?¨????à??????√≥√§?à??à??à?√a?à?√á?¨??￠?à?√μ???√?√¨?¨?à?
			TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(
					android.R.id.title);
			tv.setTextSize(20);
			tv.setGravity(Gravity.FILL_VERTICAL);// bottom

		}
		// setTitle(");
		// setTitle("体温 － 统计数据");

		View actionbar_title = LayoutInflater.from(this).inflate(
				R.layout.title, null);
		TextView title = (TextView) actionbar_title.findViewById(R.id.title);
		// button_back = (Button)
		// actionbar_title.findViewById(R.id.button_back);
		// button_right = (Button)
		// actionbar_title.findViewById(R.id.button_right);

		getActionBar().setCustomView(
				actionbar_title,
				new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
						ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER));
		getActionBar().setDisplayShowCustomEnabled(true);

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

	}

	private void initView() {
		patientName = (TextView) this.findViewById(R.id.patientName);
		textViewResult = (TextView) this.findViewById(R.id.textViewResult);
		textViewHospital = (TextView) this.findViewById(R.id.textViewHospital);
		textViewYibao = (TextView) this.findViewById(R.id.textViewYibao);
		textViewFileNum = (TextView) this.findViewById(R.id.textViewFileNum);
		textViewMaleFemale = (TextView) this
				.findViewById(R.id.textViewMaleFemale);
		textViewAgeNum = (TextView) this.findViewById(R.id.textViewAgeNum);
		memberImage = (ImageButton) this.findViewById(R.id.imageButton1);
		contactMgr = new ContactManager(context);
	}

	private void initPatientInfo() {
		// TODO Auto-generated method stub

		Contact contact = contactMgr.getContactById(MemberManage.patientID);
		byte[] image = contact.getImage();

		patientName.setText(contact.getName());
		textViewResult.setText(contact.getDescribe());
		textViewHospital.setText(contact.getHospital());
		textViewYibao.setText(contact.getType());
		textViewFileNum.setText(contact.getFileNo());
		textViewMaleFemale.setText(contact.getGender());
		textViewAgeNum.setText(Utility.getAgeByBirthday(contact.getBirthday()));

		memberImage.setImageBitmap(ImageTools.getBitmapFromByte(image));
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		initPatientInfo();
	}

	@Override
	public synchronized void onPause() {
		super.onPause();

	}

	@Override
	public void onStop() {
		super.onStop();

	}

	private class TabView extends LinearLayout {
		ImageView imageView;

		public TabView(Context c, int drawable, int drawableselec) {
			super(c);
			imageView = new ImageView(c);
			StateListDrawable listDrawable = new StateListDrawable();
			listDrawable.addState(SELECTED_STATE_SET, this.getResources()
					.getDrawable(drawableselec));
			listDrawable.addState(ENABLED_STATE_SET, this.getResources()
					.getDrawable(drawable));
			imageView.setImageDrawable(listDrawable);
			imageView.setBackgroundColor(Color.TRANSPARENT);
			setGravity(Gravity.CENTER);
			addView(imageView);
		}
	}

}