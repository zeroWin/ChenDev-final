package mobileMedical.namespace;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mobileMedical.Message.GWInfoResult;
import mobileMedical.Message.MessageInfo;
import mobileMedical.Message.SensorInfoResult;

/**
 * This fragment shows the preferences for the first header.
 */
public final class RouterPreferenceFragmentActivity extends PreferenceFragment {
	BluetoothDevicePreference mRouterDevicePreference;
	BluetoothDevicePreference mNoteDevicePreference;
	private PreferenceGroup mConnectedDevicesCategory;
	private PreferenceGroup mNoteDevicesCategory;

	private PreferenceGroup mAvailableDevicesCategory;
	private boolean mAvailableDevicesCategoryIsPresent;

	private static final String TAG = "RouterPreference";
	private static final boolean D = true;

	// Message types sent from the BluetoothServer Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothServer Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String DEVICE_MAC_ADDRESS = "device_mac_address";
	public static final String TOAST = "toast";

	public static String Connected_GW_Name = "";
	public static String Found_GW_NAME_MAC = "";
	public static String Connected_SENSOR_Name = "";

	private static final int MENU_ID_SCAN = Menu.FIRST;
	private static final int MENU_ID_STOP = Menu.FIRST + 1;
	private static Activity mActivity;

	private boolean GW_Connected = false;

	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;

	List<String> lstDevices = new ArrayList<String>();
	List<String> lstConnectedDevices = new ArrayList<String>();

	private static SensorInfoResult[] sensorInfoItemResults = null;
	private static GWInfoResult gwInfoResult = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Make sure default values are applied. In a real app, you would
		// want this in a shared function that is used to retrieve the
		// SharedPreferences wherever they are needed.
		// PreferenceManager.setDefaultValues(mActivity,
		// R.xml.advanced_preferences, false);

		mActivity = getActivity();

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.router_preference);		//？？？？？
		final PreferenceScreen preferenceScreen = getPreferenceScreen();
		SharedPreferences settings = mActivity.getSharedPreferences(
				ConstDef.SHAREPRE_SETTING_INFOS, 0); // 获取一个 SharedPreferences
														// 对象
		// 取出保存的NAME，取出改字段名的值，不存在则创建默认为空
		GW_Connected = settings.getBoolean(ConstDef.SHAREPRE_ROUTER, false); // 取出保存的
																				// NAME
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Connected:" + GW_Connected + "");
		}
		//为PreferenceScreen添加选项
		mConnectedDevicesCategory = new PreferenceCategory(mActivity);
		mConnectedDevicesCategory.setTitle("已连接的网关");
		preferenceScreen.addPreference(mConnectedDevicesCategory);
		mConnectedDevicesCategory.setEnabled(true);		//使其可点击

		mNoteDevicesCategory = new PreferenceCategory(mActivity);
		mNoteDevicesCategory.setTitle("已连接的节点");
		preferenceScreen.addPreference(mNoteDevicesCategory);
		mNoteDevicesCategory.setEnabled(true);

		mAvailableDevicesCategory = new PreferenceCategory(mActivity);
		mAvailableDevicesCategory.setTitle("可用的设备");
		preferenceScreen.addPreference(mAvailableDevicesCategory);
		mAvailableDevicesCategory.setEnabled(true);
		//添加蓝牙搜索页面
		View mCustomView = mActivity.getLayoutInflater().inflate(
				R.layout.bluetooth_scan, null);
		//添加动作栏
		mActivity.getActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
		mActivity.getActionBar().setCustomView(
				mCustomView,
				new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
						ActionBar.LayoutParams.WRAP_CONTENT,
						Gravity.CENTER_VERTICAL | Gravity.BOTTOM));
		setHasOptionsMenu(true);

		// Register for broadcasts when a device is discovered
		// 发现新蓝牙设备后注册一个intentfliter
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mActivity.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		// 蓝牙设备发现完成后注册一个intentfilter
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mActivity.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		// 低水平断开蓝牙后注册一个intentfilter
		filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		mActivity.registerReceiver(mReceiver, filter);
		// android.intent.action.btconnect注册一个intentfilter
		filter = new IntentFilter(ConstDef.BT_CONNECT_BROADCAST_MESSAGE);
		mActivity.registerReceiver(mReceiver, filter);
		//android.intent.action.gwStateQueryResults注册一个if
		filter = new IntentFilter(
				ConstDef.GW_STATE_QUERY_RESULTS_BROADCAST_MESSAGE);
		mActivity.registerReceiver(mReceiver, filter);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(mActivity, "蓝牙不可用",
					Toast.LENGTH_LONG).show();
			// mActivity.finish();
			// return;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Unregister broadcast listeners
		mActivity.unregisterReceiver(mReceiver);
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		SharedPreferences settings = mActivity.getSharedPreferences(
				ConstDef.SHAREPRE_SETTING_INFOS, 0); // 获取一个 SharedPreferences
														// 对象
		// 取出保存的NAME，取出改字段名的值，不存在则创建默认为空
		GW_Connected = settings.getBoolean(ConstDef.SHAREPRE_ROUTER, false); // 取出保存的
																				// NAME
		mConnectedDevicesCategory.removeAll();		//清空已连接的蓝牙设备列表
		mNoteDevicesCategory.removeAll();			//清空已连接的节点列表

		if (GW_Connected) {

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(mActivity);
			String name = prefs.getString(
					ConstDef.PREFERENCE_GW_KEY_NAME_STRING, "<unset>");
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "++ Connected ++" + name);
			}

			mRouterDevicePreference = new BluetoothDevicePreference(mActivity,
					name, true, ConstDef.DEVICE_ROUTER);

			mRouterDevicePreference.setSelectable(false);
			mRouterDevicePreference
					.setKey(ConstDef.PREFERENCE_GW_KEY_NAME_STRING);

			// mRouterDevicePreference.setOnSettingsClickListener(mDeviceListener);
			mConnectedDevicesCategory.addPreference(mRouterDevicePreference);

			if (settings.getBoolean(ConstDef.SHAREPRE_SENSOR, false)) {
				String nodeName = prefs.getString(
						ConstDef.PREFERENCE_SENSOR_KEY_NAME_STRING, "<unset>");
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "++ nodeName ++" + nodeName);
				}
				mNoteDevicePreference = new BluetoothDevicePreference(
						mActivity, nodeName, true, ConstDef.DEVICE_SENSOR);

				mNoteDevicePreference.setDefaultValue(null);
				mNoteDevicePreference
						.setKey(ConstDef.PREFERENCE_NEW_SENSOR_STRING);
				mNoteDevicePreference.setSelectable(false);
				mNoteDevicesCategory.addPreference(mNoteDevicePreference);
				String measSensor = settings.getString(
						ConstDef.SHAREPRE_MEAS_SENSOR, null);
				if (measSensor != null && nodeName.contains(measSensor)) {
					Editor settingEdit = settings.edit();
					settingEdit.putString(ConstDef.SHAREPRE_MEAS_SENSOR, null);
					settingEdit.commit();

					((PreferenceActivity) mActivity).startPreferencePanel(
							BodyTempDetailsPreferenceFragmentActivity.class
									.getName(), null, 0, getResources()
									.getString(R.string.sensor_info), null, 0);
				}
			}

		} else {

			mRouterDevicePreference = new BluetoothDevicePreference(mActivity,
					"placehoder", false, ConstDef.DEVICE_ROUTER);

			mRouterDevicePreference.setDefaultValue(null);
			mRouterDevicePreference.setKey(ConstDef.PREFERENCE_NEW_BT_STRING);
			mRouterDevicePreference.setSelectable(false);
			mRouterDevicePreference.setInvisible();
			mRouterDevicePreference.setOnSettingsClickListener(mDeviceListener);
			mConnectedDevicesCategory.addPreference(mRouterDevicePreference);

			mNoteDevicePreference = new BluetoothDevicePreference(mActivity,
					"placeholder", false, ConstDef.DEVICE_SENSOR);

			mNoteDevicePreference.setDefaultValue(null);
			mNoteDevicePreference.setKey(ConstDef.PREFERENCE_NEW_SENSOR_STRING);

			mNoteDevicePreference.setSelectable(false);
			mNoteDevicePreference.setInvisible();
			mNoteDevicesCategory.addPreference(mNoteDevicePreference);

		}

	}

	private final View.OnClickListener mDeviceListener = new View.OnClickListener() {
		public void onClick(View v) {
			// User clicked on advanced options icon for a device in the list
			// Intent gatewayDetailIntent = new Intent();
			// gatewayDetailIntent.setClass(mActivity,
			// RouterDetailsPreferenceFragmentActivity.class);

			// startActivity(gatewayDetailIntent);
			((PreferenceActivity) mActivity).startPreferencePanel(
					RouterDetailsPreferenceFragmentActivity.class.getName(),
					null, 0, "网关信息", null, 0);
		}
	};

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		menu.add(Menu.NONE, MENU_ID_SCAN, 0, "搜索").setEnabled(true)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(Menu.NONE, MENU_ID_STOP, 1, "断开").setEnabled(true)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_SCAN:

			startScanning();

			return true;
		case MENU_ID_STOP:

			Intent intent = new Intent();// 创建Intent对象
			intent.setAction(ConstDef.CMD_BROADCAST_MESSAGE);
			intent.putExtra(ConstDef.CMD, ConstDef.CMD_STOP_SERVICE);

			mActivity.sendBroadcast(intent);

			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void startScanning() {
		if (!mAvailableDevicesCategoryIsPresent) {
			getPreferenceScreen().addPreference(mAvailableDevicesCategory);
		}
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		mBluetoothAdapter.startDiscovery();
	}

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				// if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

				Found_GW_NAME_MAC = device.getName() + "\n"
						+ device.getAddress();
				if (lstDevices.indexOf(Found_GW_NAME_MAC) == -1)// 防止重复添加
				{
					BluetoothDevicePreference mAvailableDevicePreference = new BluetoothDevicePreference(
							mActivity, Found_GW_NAME_MAC, true,
							ConstDef.DEVICE_BLUETOOTH);

					mAvailableDevicePreference.setDefaultValue(null);
					mAvailableDevicePreference
							.setKey(ConstDef.PREFERENCE_NEW_BT_STRING);

					// mAvailableDevicePreference.setOnSettingsClickListener(mBluetoothDeviceListener);
					mAvailableDevicePreference.setSelectable(false);

					mAvailableDevicesCategory
							.addPreference(mAvailableDevicePreference);
					lstDevices.add(Found_GW_NAME_MAC);
				}
				// }
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {

			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

				GW_Connected = false;
				SharedPreferences settings = mActivity.getSharedPreferences(
						ConstDef.SHAREPRE_SETTING_INFOS, 0); // 首先获取一个
																// SharedPreferences
																// 对象
				Editor settingEdit = settings.edit();

				settingEdit.putBoolean(ConstDef.SHAREPRE_ROUTER, false);
				settingEdit.putBoolean(ConstDef.SHAREPRE_SENSOR, false);

				settingEdit.commit();
				mConnectedDevicesCategory.removeAll();
				mNoteDevicesCategory.removeAll();

				mRouterDevicePreference = new BluetoothDevicePreference(
						mActivity, "placehoder", false, ConstDef.DEVICE_ROUTER);

				mRouterDevicePreference.setDefaultValue(null);
				mRouterDevicePreference
						.setKey(ConstDef.PREFERENCE_NEW_BT_STRING);
				mRouterDevicePreference.setSelectable(false);
				mRouterDevicePreference.setInvisible();
				mRouterDevicePreference
						.setOnSettingsClickListener(mDeviceListener);
				mConnectedDevicesCategory
						.addPreference(mRouterDevicePreference);

				mNoteDevicePreference = new BluetoothDevicePreference(
						mActivity, "placeholder", false, ConstDef.DEVICE_SENSOR);

				mNoteDevicePreference.setDefaultValue(null);
				mNoteDevicePreference
						.setKey(ConstDef.PREFERENCE_NEW_SENSOR_STRING);

				mNoteDevicePreference.setSelectable(false);
				mNoteDevicePreference.setInvisible();
				mNoteDevicesCategory.addPreference(mNoteDevicePreference);

			} else if (action
					.equals(ConstDef.GW_STATE_QUERY_RESULTS_BROADCAST_MESSAGE)) {

				// Send message to MessageProcess to let it set its state
				Intent resultDislayedInformIntent = new Intent();
				resultDislayedInformIntent.putExtra(ConstDef.CMD,
						ConstDef.MEAS_RETS_DISPLAY_STATE);
				resultDislayedInformIntent.putExtra(ConstDef.RESULTS_DISPLAYED,
						true);
				resultDislayedInformIntent
						.setAction(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE);
				mActivity.sendBroadcast(resultDislayedInformIntent);

				gwInfoResult = intent
						.getParcelableExtra(ConstDef.GW_INFO_RESULTS);
				ArrayList<SensorInfoResult> sensorInfoItemResultsList = intent
						.getParcelableArrayListExtra(ConstDef.SENSOR_INFO_RESULTS);
				if (sensorInfoItemResultsList != null
						&& !sensorInfoItemResultsList.isEmpty()) {
					int listSize = sensorInfoItemResultsList.size();
					sensorInfoItemResults = (SensorInfoResult[]) sensorInfoItemResultsList
							.toArray(new SensorInfoResult[listSize]);
				}
				String MobMedGWState = "";
				switch (gwInfoResult.GetMobMedGWState()) {
				case MessageInfo.GW_STATE_ERRORING:
					MobMedGWState = MessageInfo.GW_STATE_ERRORING_STR;
					break;
				case MessageInfo.GW_STATE_IDLE:
					MobMedGWState = MessageInfo.GW_STATE_IDLE_STR;
					break;
				case MessageInfo.GW_STATE_SEARCHING:
					MobMedGWState = MessageInfo.GW_STATE_SEARCHING_STR;
					break;
				case MessageInfo.GW_STATE_CONNECTING:
					MobMedGWState = MessageInfo.GW_STATE_CONNECTING_STR;
					break;
				case MessageInfo.GW_STATE_MEASURING:
					MobMedGWState = MessageInfo.GW_STATE_MEASURING_STR;
					break;
				case MessageInfo.GW_STATE_MEASDATATRANSMITTING:
					MobMedGWState = MessageInfo.GW_STATE_MEASDATATRANSMITTING_STR;
					break;
				case MessageInfo.GW_STATE_SYNCING:
					MobMedGWState = MessageInfo.GW_STATE_SYNCING_STR;
					break;
				case MessageInfo.GW_STATE_ALIGNING:
					MobMedGWState = MessageInfo.GW_STATE_ALIGNING_STR;
					break;
				}

				String MobMedGWWirlessDev = "";
				switch (gwInfoResult.GetWirelessDevice()) {
				case MessageInfo.WIRELES_TYPE_WIFI:
					MobMedGWWirlessDev = MessageInfo.WIRELES_TYPE_WIFI_STR;
					break;
				case MessageInfo.WIRELES_TYPE_NFC:
					MobMedGWWirlessDev = MessageInfo.WIRELES_TYPE_NFC_STR;
					break;
				case MessageInfo.WIRELES_TYPE_ZIGBEE:
					MobMedGWWirlessDev = MessageInfo.WIRELES_TYPE_ZIGBEE_STR;
					break;
				case MessageInfo.WIRELES_TYPE_BLUETOOTH:
					MobMedGWWirlessDev = MessageInfo.WIRELES_TYPE_BLUETOOTH_STR;
					break;
				case MessageInfo.WIRELES_TYPE_2NDG:
					MobMedGWWirlessDev = MessageInfo.WIRELES_TYPE_2NDG_STR;
					break;
				case MessageInfo.WIRELES_TYPE_3RDG:
					MobMedGWWirlessDev = MessageInfo.WIRELES_TYPE_3RDG_STR;
					break;
				case MessageInfo.WIRELES_TYPE_4THG:
					MobMedGWWirlessDev = MessageInfo.WIRELES_TYPE_4THG_STR;
					break;
				}

				SharedPreferences settings = mActivity.getSharedPreferences(
						ConstDef.SHAREPRE_SETTING_INFOS, 0); // 首先获取一个
																// SharedPreferences
																// 对象
				Editor settingEdit = settings.edit();

				settingEdit.putBoolean(ConstDef.SHAREPRE_SENSOR, true);
				settingEdit.commit();

				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(mActivity);
				Editor edit = prefs.edit();

				edit.putString(ConstDef.GWINFO_MOBMEDGWID,
						gwInfoResult.GetMobMedGWID());
				edit.putString(ConstDef.GWINFO_SYSTEMVERSION,
						gwInfoResult.GetSystemVersion());
				edit.putString(ConstDef.GWINFO_MEMORYSIZE,
						Integer.toString(gwInfoResult.GetMemorySize()));
				edit.putString(ConstDef.GWINFO_SDCAPACITY,
						Integer.toString(gwInfoResult.GetSDCapacity()));
				edit.putString(ConstDef.GWINFO_REMAININGPOWER,
						Float.toString(gwInfoResult.GetRemainingPower()));
				edit.putString(ConstDef.GWINFO_SYSTEMLOAD,
						Float.toString(gwInfoResult.GetSystemload()));

				edit.putString(ConstDef.GWINFO_MOBMEDGWSTATE, MobMedGWState);
				edit.putString(ConstDef.GWINFO_WIRELESSDEVICE,
						MobMedGWWirlessDev);
				edit.putString(ConstDef.GWINFO_WIRELESSDEVICEMAC,
						gwInfoResult.GetWirelessDeviceMAC());
				edit.putString(ConstDef.GWINFO_WIRELESSDEVICEPROTVERS,
						gwInfoResult.GetWirelessDeviceProtVers());
				edit.putString(ConstDef.GWINFO_MAXIMUMSENSORNUMBER,
						Integer.toString(gwInfoResult.GetMaximumSensorNumber()));
				edit.putString(ConstDef.GWINFO_CONNECTEDSENSORNUMBER, Integer
						.toString(gwInfoResult.GetConnectedSensorNumber()));

				// body temp node info
				// Currently, only support one sensor
				SensorInfoResult SensorInfoRet = sensorInfoItemResults[0];

				String sensorStateString = "";
				switch (SensorInfoRet.GetSensorState()) {
				case MessageInfo.GW_STATE_ERRORING:
					sensorStateString = MessageInfo.GW_STATE_ERRORING_STR;
					break;
				case MessageInfo.GW_STATE_IDLE:
					sensorStateString = MessageInfo.GW_STATE_IDLE_STR;
					break;
				case MessageInfo.GW_STATE_SEARCHING:
					sensorStateString = MessageInfo.GW_STATE_SEARCHING_STR;
					break;
				case MessageInfo.GW_STATE_CONNECTING:
					sensorStateString = MessageInfo.GW_STATE_CONNECTING_STR;
					break;
				case MessageInfo.GW_STATE_MEASURING:
					sensorStateString = MessageInfo.GW_STATE_MEASURING_STR;
					break;
				case MessageInfo.GW_STATE_MEASDATATRANSMITTING:
					sensorStateString = MessageInfo.GW_STATE_MEASDATATRANSMITTING_STR;
					break;
				case MessageInfo.GW_STATE_SYNCING:
					sensorStateString = MessageInfo.GW_STATE_SYNCING_STR;
					break;
				case MessageInfo.GW_STATE_ALIGNING:
					sensorStateString = MessageInfo.GW_STATE_ALIGNING_STR;
					break;
				}

				String SensorWirlessDev = "";
				switch (SensorInfoRet.GetWirelessDevice()) {
				case MessageInfo.WIRELES_TYPE_WIFI:
					SensorWirlessDev = MessageInfo.WIRELES_TYPE_WIFI_STR;
					break;
				case MessageInfo.WIRELES_TYPE_NFC:
					SensorWirlessDev = MessageInfo.WIRELES_TYPE_NFC_STR;
					break;
				case MessageInfo.WIRELES_TYPE_ZIGBEE:
					SensorWirlessDev = MessageInfo.WIRELES_TYPE_ZIGBEE_STR;
					break;
				case MessageInfo.WIRELES_TYPE_BLUETOOTH:
					SensorWirlessDev = MessageInfo.WIRELES_TYPE_BLUETOOTH_STR;
					break;
				case MessageInfo.WIRELES_TYPE_2NDG:
					SensorWirlessDev = MessageInfo.WIRELES_TYPE_2NDG_STR;
					break;
				case MessageInfo.WIRELES_TYPE_3RDG:
					SensorWirlessDev = MessageInfo.WIRELES_TYPE_3RDG_STR;
					break;
				case MessageInfo.WIRELES_TYPE_4THG:
					SensorWirlessDev = MessageInfo.WIRELES_TYPE_4THG_STR;
					break;
				}

				String sensorTypeString = "";
				switch (SensorInfoRet.GetSensorType()) {
				case MessageInfo.SENSORTYPE_BLOODOXYGENMETER:
					sensorTypeString = mActivity.getResources().getString(
							R.string.bloodOx);
					break;
				case MessageInfo.SENSORTYPE_BLOODPRESSUREMETER:
					sensorTypeString = mActivity.getResources().getString(
							R.string.bloodPresure);
					break;
				case MessageInfo.SENSORTYPE_BLOODSUGARMETER:
					sensorTypeString = mActivity.getResources().getString(
							R.string.bloodSuger);
					break;
				case MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER:
					sensorTypeString = mActivity.getResources().getString(
							R.string.electrocardiogrammeter);
					break;
				case MessageInfo.SENSORTYPE_STETHOSCOPE:
					sensorTypeString = mActivity.getResources().getString(
							R.string.stethoscope);
					break;
				case MessageInfo.SENSORTYPE_THERMOMETER:
					sensorTypeString = mActivity.getResources().getString(
							R.string.temperature);
					break;
				}

				edit.putString(ConstDef.SENSORINFO_REMAININGPOWER,
						Float.toString(SensorInfoRet.GetRemainingPower()));
				edit.putString(ConstDef.SENSORINFO_SENSORID,
						SensorInfoRet.GetSensorID());
				edit.putString(ConstDef.SENSORINFO_SENSORSTATE,
						sensorStateString);
				edit.putString(ConstDef.SENSORINFO_SENSORTYP, sensorTypeString);
				edit.putString(ConstDef.SENSORINFO_SYSTEMLOAD,
						Float.toString(SensorInfoRet.GetSystemload()));
				edit.putString(ConstDef.SENSORINFO_SYSTEMVERSION,
						SensorInfoRet.GetSystemVersion());
				edit.putString(ConstDef.SENSORINFO_WIRELESSDEVICE,
						SensorWirlessDev);
				edit.putString(ConstDef.SENSORINFO_WIRELESSDEVICEMAC,
						SensorInfoRet.GetWirelessDeviceMAC());
				edit.putString(ConstDef.SENSORINFO_WIRELESSDEVICEPROTVERS,
						SensorInfoRet.GetWirelessDeviceProtVers());

				// Save the GW_NAME_MAC and SENROR_NAME_MAC
				// Connected_SENSOR_Name = SensorInfoRet.GetSensorID() + "\n" +
				// sensorTypeString;
				Connected_SENSOR_Name = sensorTypeString;

				edit.putString(ConstDef.PREFERENCE_GW_KEY_NAME_STRING,
						Connected_GW_Name);
				edit.putString(ConstDef.PREFERENCE_SENSOR_KEY_NAME_STRING,
						Connected_SENSOR_Name);

				edit.commit();

				mRouterDevicePreference.setName(Connected_GW_Name);

				mNoteDevicePreference.setName(Connected_SENSOR_Name);

				if (BuildConfig.DEBUG) {
					Log.i(TAG,
							"Connected Router:"
									+ prefs.getString(
											ConstDef.PREFERENCE_GW_KEY_NAME_STRING,
											"null"));
				}

			} else if (action.equals(ConstDef.BT_CONNECT_BROADCAST_MESSAGE)) {
				Bundle bundle = intent.getExtras();
				int cmd = bundle.getInt(ConstDef.CMD);

				if (cmd == ConstDef.CMD_SHOW_TOAST) {
					String str = bundle.getString(ConstDef.STRING_INFO);
					// Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
					Toast.makeText(mActivity.getApplicationContext(), str,
							Toast.LENGTH_SHORT).show();
				} else if (cmd == ConstDef.CMD_SHOW_TITLE) {
					String str = bundle.getString(ConstDef.STRING_INFO);
					mActivity.setTitle(str);
				} else if (cmd == ConstDef.CMD_SYSTEM_EXIT) {
					System.exit(0);
				} else if (cmd == ConstDef.CMD_SET_CONNECTED_DEVICE) {
					// Send GW Query to MobileMedicalActivity
					Intent gwQueryIntent = new Intent();
					gwQueryIntent
							.setAction(ConstDef.GW_STATE_QUERY_REQ_BROADCAST_MESSAGE);
					mActivity.sendBroadcast(gwQueryIntent);

					SharedPreferences settings = mActivity
							.getSharedPreferences(
									ConstDef.SHAREPRE_SETTING_INFOS, 0); // 首先获取一个
																			// SharedPreferences
																			// 对象
					Editor settingEdit = settings.edit();

					settingEdit.putBoolean(ConstDef.SHAREPRE_ROUTER, true);
					settingEdit.commit();

					String str = bundle.getString(ConstDef.STRING_INFO);
					Connected_GW_Name = str;

				}
			}
		}
	};
}
