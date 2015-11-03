package mobileMedical.namespace;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import devDataType.Parameters.IntParameter;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.ParameterDataKeys;

/**
 * This fragment shows the preferences for the first header.
 */
public final class OtherPreferenceFragmentActivity extends PreferenceFragment
		implements OnPreferenceChangeListener,
		android.content.DialogInterface.OnClickListener {
	private static LoadTestPreference mLoadTestPreference;

	private PreferenceGroup mCalbrationCategory;
	private PreferenceGroup mLoadDataCategory;
	private PreferenceGroup mCleraDbCategory;
	private PreferenceGroup mSendCommandCategory;
	private PreferenceGroup mWavefromDisplayCategory;
	private ExtendDiagPreference mClearDbPreference;
	private ListPreference mSendCommandPreference;
	private Preference mBodyTempCalPreference;
	private EditTextPreference mBloodPressConfigParmPreference;
	private EditTextPreference mBloodPressDisplayFrameLenPreference;
	private EditTextPreference mBloodOxygenDisplayFrameLenPreference;
	private EditTextPreference mECGDisplayFrameLenPreference;
	private EditTextPreference mPulmonaryDisplayFrameLenPreference;
	private String mSendCommandPreferenceValueString;

	private static final String TAG = "OtherPreference";

	private static final boolean D = true;

	private static Activity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Make sure default values are applied. In a real app, you would
		// want this in a shared function that is used to retrieve the
		// SharedPreferences wherever they are needed.
		// PreferenceManager.setDefaultValues(getActivity(),
		// R.xml.advanced_preferences, false);
		mActivity = getActivity();
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.setting_others);
		final PreferenceScreen preferenceScreen = getPreferenceScreen();

		mLoadDataCategory = new PreferenceCategory(mActivity);
		mLoadDataCategory.setTitle(R.string.readData);
		preferenceScreen.addPreference(mLoadDataCategory);
		mLoadDataCategory.setEnabled(true);

		mCleraDbCategory = new PreferenceCategory(mActivity);
		mCleraDbCategory.setTitle(R.string.clearDataBase);
		preferenceScreen.addPreference(mCleraDbCategory);
		mCleraDbCategory.setEnabled(true);

		mSendCommandCategory = new PreferenceCategory(mActivity);
		mSendCommandCategory.setTitle(R.string.sendCommand);
		preferenceScreen.addPreference(mSendCommandCategory);
		mSendCommandCategory.setEnabled(true);

		mCalbrationCategory = new PreferenceCategory(mActivity);
		mCalbrationCategory.setTitle(R.string.calibration);
		preferenceScreen.addPreference(mCalbrationCategory);
		mCalbrationCategory.setEnabled(true);

		mWavefromDisplayCategory = new PreferenceCategory(mActivity);
		mWavefromDisplayCategory.setTitle(R.string.waveformDisplayFrameLen);
		preferenceScreen.addPreference(mWavefromDisplayCategory);
		mWavefromDisplayCategory.setEnabled(true);
	}

	@Override
	public synchronized void onResume() {
		super.onResume();

		SharedPreferences settings = getActivity().getSharedPreferences(
				ConstDef.SHAREPRE_SETTING_INFOS, 0); // 获取一个 SharedPreferences
														// 对象

		int bloodPressConfigParm = settings.getInt(
				ConstDef.SHAREPRE_BLOODPRESS_CONFIG_PARM, 0);
		float bloodPressDisplFramLen = settings.getFloat(
				ConstDef.SHAREPRE_BLOODPRESS_DISPLAY_FRAME_LEN, 0);
		float bloodOxygenDisplFramLen = settings.getFloat(
				ConstDef.SHAREPRE_BLOODOXYGEN_DISPLAY_FRAME_LEN, 0);
		float ecgDisplFramLen = settings.getFloat(
				ConstDef.SHAREPRE_ECG_DISPLAY_FRAME_LEN, 0);
		float pulmonaryDisplFramLen = settings.getFloat(
				ConstDef.SHAREPRE_PULMONARY_DISPLAY_FRAME_LEN, 0);

		mLoadTestPreference = new LoadTestPreference(mActivity);
		mLoadTestPreference.setSelectable(false);
		mLoadTestPreference.setKey(ConstDef.OTHERSETTING_LOADTEST_KEY_STRING);

		mClearDbPreference = new ExtendDiagPreference(mActivity, null);
		mClearDbPreference.setKey(ConstDef.OTHERSETTING_CLEARDB_KEY_STRING);
		mClearDbPreference.setTitle(R.string.clearDataBase);
		mClearDbPreference.setSummary(R.string.clearDataBaseMessage);
		mClearDbPreference.setDialogTitle(R.string.clearDataBase);
		mClearDbPreference.setDialogMessage(R.string.clearDataBaseMessage);
		mClearDbPreference.setOnPreferenceChangeListener(this);
		mClearDbPreference.setOnClickListener(this);

		mSendCommandPreference = new ListPreference(mActivity);
		mSendCommandPreference
				.setKey(ConstDef.OTHERSETTING_SENDCOMM_KEY_STRING);
		mSendCommandPreference.setTitle(R.string.sendCommand);
		mSendCommandPreference.setSummary(R.string.sendCommandMessage);
		mSendCommandPreference.setDialogTitle(R.string.sendCommand);
		mSendCommandPreference.setEntries(R.array.commands);
		mSendCommandPreference.setEntryValues(R.array.commands_value);
		mSendCommandPreference.setOnPreferenceChangeListener(this);

		mBloodPressConfigParmPreference = new EditTextPreference(mActivity);
		mBloodPressConfigParmPreference
				.setKey(ConstDef.OTHERSETTING_BLOODPRESS_CONFIG_PARM_KEY_STRING);
		mBloodPressConfigParmPreference.setTitle(R.string.bloodPressConfigParm);
		mBloodPressConfigParmPreference
				.setDialogTitle(R.string.bloodPressConfigParm);
		mBloodPressConfigParmPreference.setOnPreferenceChangeListener(this);
		mBloodPressConfigParmPreference.getEditText().setInputType(
				InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		mECGDisplayFrameLenPreference = new EditTextPreference(mActivity);
		mECGDisplayFrameLenPreference
				.setKey(ConstDef.OTHERSETTING_ECG_DISPLAYFRAMELEN_KEY_STRING);
		mECGDisplayFrameLenPreference.setTitle(R.string.ECGDisplayFrameLen);
		mECGDisplayFrameLenPreference
				.setDialogTitle(R.string.ECGDisplayFrameLen);
		mECGDisplayFrameLenPreference.setOnPreferenceChangeListener(this);
		mECGDisplayFrameLenPreference.getEditText().setInputType(
				InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		mPulmonaryDisplayFrameLenPreference = new EditTextPreference(mActivity);
		mPulmonaryDisplayFrameLenPreference
				.setKey(ConstDef.OTHERSETTING_PULMONARY_DISPLAYFRAMELEN_KEY_STRING);
		mPulmonaryDisplayFrameLenPreference
				.setTitle(R.string.pulmonaryDisplayFrameLen);
		mPulmonaryDisplayFrameLenPreference
				.setDialogTitle(R.string.pulmonaryDisplayFrameLen);
		mPulmonaryDisplayFrameLenPreference.setOnPreferenceChangeListener(this);
		mPulmonaryDisplayFrameLenPreference.getEditText().setInputType(
				InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		mBloodOxygenDisplayFrameLenPreference = new EditTextPreference(
				mActivity);
		mBloodOxygenDisplayFrameLenPreference
				.setKey(ConstDef.OTHERSETTING_BLOODOXYGEN_DISPLAYFRAMELEN_KEY_STRING);
		mBloodOxygenDisplayFrameLenPreference
				.setTitle(R.string.bloodOxygenDisplayFrameLen);
		mBloodOxygenDisplayFrameLenPreference
				.setDialogTitle(R.string.bloodOxygenDisplayFrameLen);
		mBloodOxygenDisplayFrameLenPreference
				.setOnPreferenceChangeListener(this);
		mBloodOxygenDisplayFrameLenPreference.getEditText().setInputType(
				InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		mBloodPressDisplayFrameLenPreference = new EditTextPreference(mActivity);
		mBloodPressDisplayFrameLenPreference
				.setKey(ConstDef.OTHERSETTING_BLOODPRESS_DISPLAYFRAMELEN_KEY_STRING);
		mBloodPressDisplayFrameLenPreference
				.setTitle(R.string.bloodPressDisplayFrameLen);
		mBloodPressDisplayFrameLenPreference
				.setDialogTitle(R.string.bloodPressDisplayFrameLen);
		mBloodPressDisplayFrameLenPreference
				.setOnPreferenceChangeListener(this);
		mBloodPressDisplayFrameLenPreference.getEditText().setInputType(
				InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		mBloodPressConfigParmPreference.setSummary(getResources().getString(
				R.string.bloodPressConfigParmMessage)
				+ ":" + String.valueOf(bloodPressConfigParm));
		mECGDisplayFrameLenPreference.setSummary(getResources().getString(
				R.string.ECGDisplayFrameLenMessage)
				+ ":" + String.valueOf(ecgDisplFramLen));
		mBloodOxygenDisplayFrameLenPreference.setSummary(getResources()
				.getString(R.string.bloodOxygenDisplayFrameLenMessage)
				+ ":"
				+ String.valueOf(bloodOxygenDisplFramLen));
		mPulmonaryDisplayFrameLenPreference.setSummary(getResources()
				.getString(R.string.pulmonaryDisplayFrameLenMessage)
				+ ":"
				+ String.valueOf(pulmonaryDisplFramLen));
		mBloodPressDisplayFrameLenPreference.setSummary(getResources()
				.getString(R.string.bloodPressDisplayFrameLenMessage)
				+ ":"
				+ String.valueOf(bloodPressDisplFramLen));

		/*
		 * if(mSendCommandPreference.getValue() == null) {
		 * mSendCommandPreference.setValueIndex(0); }
		 */

		mBodyTempCalPreference = new Preference(mActivity);
		mBodyTempCalPreference.setTitle(R.string.bodyTempCal_title);
		mBodyTempCalPreference.setSummary(R.string.bodyTempCalMessage);
		mBodyTempCalPreference.setSelectable(true);
		mBodyTempCalPreference
				.setKey(ConstDef.OTHERSETTING_BODYTEMP_CAL_KEY_STRING);
		// mBodyTempCalPreference.setOnPreferenceClickListener(this);
		mBodyTempCalPreference.setOnPreferenceChangeListener(this);

		/*
		 * mBodyTempCalPreference.setOnPreferenceClickListener(
		 * onPreferenceClickListener);
		 */
		// 取出保存的NAME，取出改字段名的值，不存在则创建默认为空
		boolean GW_Connected = settings.getBoolean(ConstDef.SHAREPRE_ROUTER,
				false); // 取出保存的 NAME

		if (GW_Connected) {
			mSendCommandPreference.setEnabled(true);
		} else {
			mSendCommandPreference.setEnabled(false);
		}

		mLoadDataCategory.removeAll();
		mLoadDataCategory.addPreference(mLoadTestPreference);

		mCleraDbCategory.removeAll();
		mCleraDbCategory.addPreference(mClearDbPreference);

		mSendCommandCategory.removeAll();
		mSendCommandCategory.addPreference(mBloodPressConfigParmPreference);
		mSendCommandCategory.addPreference(mSendCommandPreference);

		mCalbrationCategory.removeAll();
		mCalbrationCategory.addPreference(mBodyTempCalPreference);

		mWavefromDisplayCategory.removeAll();
		mWavefromDisplayCategory
				.addPreference(mBloodOxygenDisplayFrameLenPreference);
		mWavefromDisplayCategory
				.addPreference(mBloodPressDisplayFrameLenPreference);
		mWavefromDisplayCategory.addPreference(mECGDisplayFrameLenPreference);
		mWavefromDisplayCategory
				.addPreference(mPulmonaryDisplayFrameLenPreference);

	}

	public void onClick(DialogInterface dialog, int which) {
		if (dialog.equals(mClearDbPreference.getDialog())) {

			if (which == DialogInterface.BUTTON_POSITIVE) {
//				mActivity.deleteDatabase("bloodox.db");
				boDb boDbHelper = new boDb(mActivity, "bloodox.db", null, 1);
				boDbHelper.deleteData();
				
				mLoadTestPreference.resetProgress();
				((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.TRANSID)).SetValue(0);
				boDbHelper.UpdateTransIDParm(0);
				// if (result) {
				// Toast.makeText(mActivity, "清除数据库数据成功", Toast.LENGTH_LONG)
				// .show();
				// } else {
				// Toast.makeText(mActivity, "数据库数据已清除", Toast.LENGTH_LONG)
				// .show();
				// }
				// delete table
				// boDb boDbHelper = new boDb(mActivity, "bloodox.db", null, 1);
				// boDbHelper.getWritableDatabase().delete("bloodox.db", null,
				// null);
			}
		}
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {

		if (preference == mSendCommandPreference) {
			mSendCommandPreferenceValueString = newValue.toString();

			mSendCommandPreference.setValueIndex(Integer
					.parseInt(mSendCommandPreferenceValueString));

			String[] commandValues = mActivity.getResources().getStringArray(
					R.array.commands_value);
			if (Integer.parseInt(mSendCommandPreferenceValueString) == Integer
					.parseInt(commandValues[0])) {

				// StateQuery
				Intent gwQueryIntent = new Intent();
				gwQueryIntent
						.setAction(ConstDef.GW_STATE_QUERY_REQ_BROADCAST_MESSAGE);
				mActivity.sendBroadcast(gwQueryIntent);
			} else if (Integer.parseInt(mSendCommandPreferenceValueString) == Integer
					.parseInt(commandValues[1])) {
				// SNConfig
				Intent snConfigIntent = new Intent();
				snConfigIntent
						.setAction(ConstDef.SN_CONFIG_REQ_BROADCAST_MESSAGE);
				mActivity.sendBroadcast(snConfigIntent);
			}

		} else if (preference == mBloodOxygenDisplayFrameLenPreference) {
			float displayFrameLen = Float.parseFloat(newValue.toString());
			if (displayFrameLen >= ConstDef.DISPLAY_FRAME_LEN_MIN
					&& displayFrameLen <= ConstDef.DISPLAY_FRAME_LEN_MAX) {
				SharedPreferences settings = getActivity()
						.getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS,
								0); // 首先获取一个 SharedPreferences 对象
				Editor edit = settings.edit();
				mBloodOxygenDisplayFrameLenPreference.setSummary(getResources()
						.getString(R.string.bloodOxygenDisplayFrameLenMessage)
						+ ":" + newValue.toString());
				edit.putFloat(ConstDef.SHAREPRE_BLOODOXYGEN_DISPLAY_FRAME_LEN,
						displayFrameLen);
				edit.commit();
			} else {
				Toast.makeText(mActivity,
						R.string.bloodOxygenDisplayFrameLenMessage,
						Toast.LENGTH_LONG).show();
			}
		} else if (preference == mBloodPressDisplayFrameLenPreference) {
			float displayFrameLen = Float.parseFloat(newValue.toString());
			if (displayFrameLen >= ConstDef.DISPLAY_FRAME_LEN_MIN
					&& displayFrameLen <= ConstDef.DISPLAY_FRAME_LEN_MAX) {

				SharedPreferences settings = getActivity()
						.getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS,
								0); // 首先获取一个 SharedPreferences 对象
				Editor edit = settings.edit();
				mBloodPressDisplayFrameLenPreference.setSummary(getResources()
						.getString(R.string.bloodPressDisplayFrameLenMessage)
						+ ":" + newValue.toString());
				edit.putFloat(ConstDef.SHAREPRE_BLOODPRESS_DISPLAY_FRAME_LEN,
						displayFrameLen);
				edit.commit();
			}
		} else if (preference == mECGDisplayFrameLenPreference) {
			float displayFrameLen = Float.parseFloat(newValue.toString());
			if (displayFrameLen >= ConstDef.DISPLAY_FRAME_LEN_MIN
					&& displayFrameLen <= ConstDef.DISPLAY_FRAME_LEN_MAX) {

				SharedPreferences settings = getActivity()
						.getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS,
								0); // 首先获取一个 SharedPreferences 对象
				Editor edit = settings.edit();
				mECGDisplayFrameLenPreference.setSummary(getResources()
						.getString(R.string.ECGDisplayFrameLenMessage)
						+ ":"
						+ newValue.toString());
				edit.putFloat(ConstDef.SHAREPRE_ECG_DISPLAY_FRAME_LEN,
						displayFrameLen);
				edit.commit();
			}
		} else if (preference == mPulmonaryDisplayFrameLenPreference) {
			float displayFrameLen = Float.parseFloat(newValue.toString());

			if (displayFrameLen >= ConstDef.DISPLAY_FRAME_LEN_MIN
					&& displayFrameLen <= ConstDef.DISPLAY_FRAME_LEN_MAX) {

				SharedPreferences settings = getActivity()
						.getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS,
								0); // 首先获取一个 SharedPreferences 对象
				Editor edit = settings.edit();
				mPulmonaryDisplayFrameLenPreference.setSummary(getResources()
						.getString(R.string.pulmonaryDisplayFrameLenMessage)
						+ ":" + newValue.toString());
				edit.putFloat(ConstDef.SHAREPRE_PULMONARY_DISPLAY_FRAME_LEN,
						displayFrameLen);
				edit.commit();
			}
		} else if (preference == mBloodPressConfigParmPreference) {
			int configParm = Integer.parseInt(newValue.toString());

			if (configParm >= ConstDef.BLOOD_PRESS_CONFIG_PARMA_MIN
					&& configParm <= ConstDef.BLOOD_PRESS_CONFIG_PARMA_MAX) {

				SharedPreferences settings = getActivity()
						.getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS,
								0); // 首先获取一个 SharedPreferences 对象
				Editor edit = settings.edit();
				mBloodPressConfigParmPreference.setSummary(getResources()
						.getString(R.string.bloodPressConfigParmMessage)
						+ ":"
						+ newValue.toString());
				edit.putInt(ConstDef.SHAREPRE_BLOODPRESS_CONFIG_PARM,
						configParm);
				edit.commit();
			}
		} else if (preference == mClearDbPreference) {

		}

		return true;
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference == mClearDbPreference) {

		} else if (preference == mSendCommandPreference) {
			/*
			 * mSendCommandPreference.setValueIndex(Integer.parseInt(
			 * mSendCommandPreferenceValueString));
			 * 
			 * 
			 * String[] commandValues =
			 * mActivity.getResources().getStringArray(R.array.commands_value);
			 * if (Integer.parseInt(mSendCommandPreferenceValueString) ==
			 * Integer.parseInt(commandValues[0])) {
			 * 
			 * // StateQuery Intent gwQueryIntent = new Intent();
			 * gwQueryIntent.setAction
			 * (ConstDef.GW_STATE_QUERY_REQ_BROADCAST_MESSAGE);
			 * mActivity.sendBroadcast(gwQueryIntent); } else if
			 * (Integer.parseInt(mSendCommandPreferenceValueString) ==
			 * Integer.parseInt(commandValues[1])) { // SNConfig Intent
			 * snConfigIntent = new Intent();
			 * snConfigIntent.setAction(ConstDef.SN_CONFIG_REQ_BROADCAST_MESSAGE
			 * ); mActivity.sendBroadcast(snConfigIntent); }
			 */
		} else if (preference == mBodyTempCalPreference) {
			// very interesting, if we put it to the OnPreferenceClickListen.
			// GetActivity will be null and using mActivity will result in
			// fragment not attached to activity error.
			((PreferenceActivity) mActivity).startPreferencePanel(
					BodyTempCalPreferenceFragmentActivity.class.getName(),
					null, 0,
					getResources().getString(R.string.bodyTempCal_title), null,
					0);
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished

}
