package mobileMedical.namespace;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.Contacts.Tool.ImageTools;
import mobileMedical.util.Utility;

public class MobileMedicalActivity extends TabActivity {
	// boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);
	private TabHost tabHost = null;
	private int mCurrentTabIndex = -1;

	private final int BodyTempTabIndex = 0;
	private final int BloodPresureTabIndex = 1;
	private final int ElectrocardiogramTabIndex = 2;
	private final int BloodOxygenTabIndex = 3;
	private final int PulVentTabIndex = 4;

	private static final boolean D = true;

	private static final String TAG = "MobileMedicalActivity";

	private TextView patientName;
	private TextView textViewResult;
	private TextView textViewHospital;
	private TextView textViewYibao;
	private TextView textViewFileNum;
	private TextView textViewMaleFemale;
	private TextView textViewAgeNum;
	private ImageButton memberImage;

	private ContactManager contactMgr;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);

		initView();
		initPatientInfo();
		tabHost = getTabHost();

		TabSpec BloodOxSpec = tabHost.newTabSpec("BloodOx");
		BloodOxSpec.setIndicator(getString(R.string.temperature));
		Intent BloodOxIntent = new Intent(this, BodyTmpActivity.class);
		BloodOxSpec.setContent(BloodOxIntent);

		tabHost.addTab(BloodOxSpec);
		tabHost.addTab(tabHost.newTabSpec(getString(R.string.bloodPresure))
				.setIndicator(getString(R.string.bloodPresure))
				.setContent(new Intent(this, BloodPressureActivity.class)));

		tabHost.addTab(tabHost
				.newTabSpec(getString(R.string.electrocardiogrammeter))
				.setIndicator(getString(R.string.electrocardiogrammeter))
				.setContent(new Intent(this, ECGActivity.class)));

		tabHost.addTab(tabHost.newTabSpec(getString(R.string.bloodOx))
				.setIndicator(getString(R.string.bloodOx))
				.setContent(new Intent(this, BloodOxActivity.class)));

		Intent intent = new Intent();
		intent.setClass(this, PulmonaryVentilationActivity.class);
		intent.putExtra("time", this.getIntent().getStringExtra("time"));

		tabHost.addTab(tabHost
				.newTabSpec(getString(R.string.pulmonaryventilation))
				.setIndicator(getString(R.string.pulmonaryventilation))
				.setContent(intent));

		TabWidget tabWidget = tabHost.getTabWidget();
		int width = this.getWindowManager().getDefaultDisplay().getWidth() / 8;

		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			tabWidget.getChildAt(i).getLayoutParams().height = LayoutParams.FILL_PARENT;
			tabWidget.getChildAt(i).getLayoutParams().width = width;
			TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(
					android.R.id.title);
			tv.setGravity(Gravity.CENTER);// bottom
		}

		tabHost.setCurrentTab(this.getIntent().getIntExtra("tabindex", 0));

		setTitle(getString(R.string.patientslist));

		final Context currentContext = this;
		// Initialize the button to perform sensor connection
		// 初始化网关按钮
		Button sensorConnect = (Button) findViewById(R.id.buttonRouter);
		sensorConnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(
						ConstDef.SHAREPRE_SETTING_INFOS, 0); // 获取一个
																// SharedPreferences
																// 对象
				String sensorTypeString = "";
				mCurrentTabIndex = tabHost.getCurrentTab();
				switch (mCurrentTabIndex) {
				case ConstDef.BodyTempTabIndex: // BodyTemp
					sensorTypeString = getString(R.string.temperature);
					break;
				case ConstDef.BloodPresureTabIndex: // BloodPressure
					sensorTypeString = getString(R.string.bloodPresure);
					break;
				case ConstDef.ElectrocardiogramTabIndex: // Electrocardiogram
					sensorTypeString = getString(R.string.electrocardiogrammeter);
					break;
				case ConstDef.BloodOxygenTabIndex: // BloodOxygen
					sensorTypeString = getString(R.string.bloodOx);
					break;
				case ConstDef.BloodSugarTabIndex: // BloodSugar
					sensorTypeString = getString(R.string.bloodSuger);
					break;
				case ConstDef.StethoscopeTabIndex: // Stethoscope
					sensorTypeString = getString(R.string.stethoscope);
					break;
				case ConstDef.PulVentTabIndex: // Stethoscope
					sensorTypeString = getString(R.string.pulmonaryventilation);
					break;

				}
				Editor settingEdit = settings.edit();

				settingEdit.putString(ConstDef.SHAREPRE_MEAS_SENSOR,
						sensorTypeString);

				settingEdit.commit();

				Intent serverIntent = new Intent(currentContext,
						SettingPreferenceActivity.class);
				startActivity(serverIntent);
			}
		});

		// Initialize the button to perform Ox measure
		//初始化开始按钮
		Button vitalSigMeasStart = (Button) findViewById(R.id.buttonStart);
		vitalSigMeasStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// v.setClickable(false);

				// Send the create the meas command and init the meas
				Intent measIntent = new Intent();
				Bundle bundle = new Bundle();
				mCurrentTabIndex = tabHost.getCurrentTab();
				bundle.putInt(ConstDef.MeasType, mCurrentTabIndex);
				measIntent.putExtras(bundle);
				measIntent.setAction(ConstDef.MEAS_REQ_BROADCAST_MESSAGE);
				sendBroadcast(measIntent);

				// Send the meas init message to reset the meas display
				Intent startIntent = new Intent();
				startIntent.putExtras(bundle);
				startIntent.setAction(ConstDef.MEAS_INIT_BROADCAST_MESSAGE);
				sendBroadcast(startIntent);
			}
		});

		// Initialize the button to stop Ox measure
		//初始化停止按钮
		Button vitalSigMeasStop = (Button) findViewById(R.id.buttonStop);
		vitalSigMeasStop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// v.setClickable(false);

				Intent stopMeasIntent = new Intent();
				stopMeasIntent
						.setAction(ConstDef.MEAS_STOP_REQ_BROADCAST_MESSAGE);
				sendBroadcast(stopMeasIntent);

				Intent intent = new Intent();// 创建Intent对象
				intent.setAction(ConstDef.CMD_BROADCAST_MESSAGE);
				intent.putExtra(ConstDef.CMD, ConstDef.CMD_STOP_SERVICE);

				currentContext.sendBroadcast(intent);

			}
		});

		// Initialize the button to stop Ox measure
		//初始化数据按钮
		Button buttonStatistics = (Button) findViewById(R.id.buttonStatistics);
		buttonStatistics.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// v.setClickable(false);
				mCurrentTabIndex = tabHost.getCurrentTab();
				Intent statisticsIntent = new Intent();
				switch (mCurrentTabIndex) {
				case BodyTempTabIndex:
					statisticsIntent.setClass(MobileMedicalActivity.this,
							BodyTempStatistics.class);
					finish();
					break;
				case BloodPresureTabIndex:
					statisticsIntent.setClass(MobileMedicalActivity.this,
							BloodPressureStatistics.class);
					finish();
					break;
				case ElectrocardiogramTabIndex:
					statisticsIntent.setClass(MobileMedicalActivity.this,
							ElectrocardiogramStatistics.class);
					finish();
					break;
				case BloodOxygenTabIndex:
					statisticsIntent.setClass(MobileMedicalActivity.this,
							BloodOxygenStatistics.class);
					finish();
					break;
				case PulVentTabIndex:
					statisticsIntent.setClass(MobileMedicalActivity.this,
							PulVentStatistics.class);
					finish();
					break;
				default:
					break;
				}
				startActivityForResult(statisticsIntent, 0);
			}
		});
		//监听选项卡切换操作
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				mCurrentTabIndex = tabHost.getCurrentTab();
				// Intent statisticsIntent = new Intent();
				switch (mCurrentTabIndex) {
				case 0:

					break;
				case 2:

					break;
				default:
					break;

				}
			}
		});

	}

	/**
	 * 获取病人信息
	 */
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

	/**
	 * 初始化关联
	 */
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
		contactMgr = new ContactManager(MobileMedicalActivity.this);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public synchronized void onResume() {
		super.onResume();

	}

	/*
	 * @Override public boolean dispatchKeyEvent(KeyEvent event) { Log.i(TAG,
	 * "dispatchKeyEvent"); if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
	 * &&event.getAction() == KeyEvent.ACTION_UP){ new AlertDialog.Builder(this)
	 * .setIcon(R.drawable.diamonds_4) .setTitle(R.string.app_name)
	 * .setMessage("want to quit?") .setNegativeButton("Cancle", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { } })
	 * .setPositiveButton("OK", new DialogInterface.OnClickListener() { public
	 * void onClick(DialogInterface dialog, int whichButton) { finish(); }
	 * }).show(); return true; } return super.dispatchKeyEvent(event); }
	 */
	/*
	 * @Override public void onBackPressed() { showExitAppAlert(); return; }
	 * public boolean onKeyDown(int kCode, KeyEvent kEvent) { switch (kCode) {
	 * 
	 * } if(kCode == KeyEvent.KEYCODE_BACK || kCode == KeyEvent.KEYCODE_HOME){
	 * showExitAppAlert(); }
	 * 
	 * return super.onKeyDown(kCode, kEvent); }
	 * 
	 * private void showExitAppAlert() {
	 * 
	 * final AlertDialog dlg = new AlertDialog.Builder(this).create();
	 * 
	 * dlg.show(); Window window = dlg.getWindow();
	 * window.setContentView(R.layout.exit_dialog);
	 * 
	 * ImageButton ok = (ImageButton) window.findViewById(R.id.button_ok);
	 * ok.setOnClickListener(new View.OnClickListener() { public void
	 * onClick(View v) { System.exit(0); //exitApp } });
	 * 
	 * 
	 * 
	 * ImageButton cancel = (ImageButton)
	 * window.findViewById(R.id.button_cancle); cancel.setOnClickListener(new
	 * View.OnClickListener() { public void onClick(View v) { dlg.cancel();//取消
	 * } }); }
	 */
	/*
	 * private void setupSendDirectMessage() { Log.d(TAG,
	 * "setupSendDirectMessage()");
	 * 
	 * // Initialize the array adapter for the conversation thread
	 * mConversationArrayAdapter = new ArrayAdapter<String>(this,
	 * R.layout.message); mConversationView = (ListView) findViewById(R.id.in);
	 * mConversationView.setAdapter(mConversationArrayAdapter);
	 * 
	 * // Initialize the compose field with a listener for the return key
	 * mOutEditText = (EditText) findViewById(R.id.edit_text_out);
	 * mOutEditText.setOnEditorActionListener(mWriteListener);
	 * 
	 * // Initialize the send button with a listener that for click events
	 * mSendButton = (Button) findViewById(R.id.button_send);
	 * mSendButton.setOnClickListener(new OnClickListener() { public void
	 * onClick(View v) { // Send a message using content of the edit text widget
	 * TextView view = (TextView) findViewById(R.id.edit_text_out); String
	 * message = view.getText().toString(); sendMessage(message); } });
	 * 
	 * // Initialize the BluetoothServer to perform bluetooth connections
	 * mBluetoothService = new BluetoothServer(this, mHandler);
	 * 
	 * // Initialize the buffer for outgoing messages mOutStringBuffer = new
	 * StringBuffer(""); }
	 */
	@Override
	public synchronized void onPause() {
		super.onPause();

	}

	@Override
	public void onStop() {
		super.onStop();

	}

	/*
	 * @Override public void onDestroy() { super.onDestroy(); //stop bt if
	 * connected if(!mConnectedDeviceStr.equals(mNoConnection)) { Intent
	 * stopBTintent = new Intent();
	 * stopBTintent.setAction(ConstDef.CMD_BROADCAST_MESSAGE);
	 * stopBTintent.putExtra(ConstDef.CMD, ConstDef.CMD_STOP_SERVICE);
	 * 
	 * sendBroadcast(stopBTintent); } // Unregister broadcast listeners
	 * //this.unregisterReceiver(mReceiver);
	 * 
	 * if(D) Log.i(TAG, "--- ON DESTROY ---"); }
	 */

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) {
	 * 
	 * switch (requestCode) { case RESULT_OK:
	 * 
	 * Bundle bunde = data.getExtras(); mConnectedDeviceStr =
	 * bunde.getString(ConstDef.BT_DEVICE); Log.d("MMA on Activity Result",
	 * mConnectedDeviceStr); break; default: break;
	 * 
	 * } }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainoptionmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exit:

			// Launch the DeviceListActivity to see devices and do scan
			// serverIntent = new Intent(this, DeviceListActivity.class);
			// startActivityForResult(serverIntent,
			// REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.about:
			// Launch the DeviceListActivity to see devices and do scan
			// serverIntent = new Intent(this, DeviceListActivity.class);
			// startActivityForResult(serverIntent,
			// REQUEST_CONNECT_DEVICE_INSECURE);

			return true;

		}
		return false;
	}

}