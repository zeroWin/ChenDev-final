package mobileMedical.namespace;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.util.Utility;

public class BloodPressureStatistics extends TabActivity {
	boDb btDbHelper = new boDb(this, "bloodox.db", null, 1);
	private TabHost tabHost = null;
	private final boolean D = true;
	private final String TAG = "BloodPressureStatitics";
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
		setContentView(R.layout.blood_pressure_st);
		context=this;
		initView();
		tabHost = getTabHost();
		TabSpec BloodPressureHistory = tabHost.newTabSpec("BloodPressureHistory");
		// BloodOxSpec.setIndicator(view);
		BloodPressureHistory.setIndicator("历史数据");
		Intent BloodPressureHistoryIntent = new Intent(this, BloodPressureHistory.class);
		BloodPressureHistory.setContent(BloodPressureHistoryIntent);

		tabHost.addTab(BloodPressureHistory);

		tabHost.addTab(tabHost.newTabSpec("BloodPressureLine").setIndicator("波形图")
				.setContent(new Intent(this, BloodPressureStLine.class)));

		TabWidget tabWidget = tabHost.getTabWidget();
		int width = this.getWindowManager().getDefaultDisplay().getWidth() / 5;
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			tabWidget.getChildAt(i).getLayoutParams().height = 50;
			tabWidget.getChildAt(i).getLayoutParams().width = width;
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
		if (D)
			Log.i(TAG, "++ ON START ++");

	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		initPatientInfo();
		if (D)
			Log.i(TAG, "+ ON RESUME +");

	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.i(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.i(TAG, "-- ON STOP --");
	}
}