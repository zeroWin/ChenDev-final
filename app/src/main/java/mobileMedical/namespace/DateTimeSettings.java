/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobileMedical.namespace;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeSettings extends SettingsPreferenceFragment implements
		OnSharedPreferenceChangeListener, OnPreferenceChangeListener,
		TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener,
		android.content.DialogInterface.OnClickListener {

	private static final String HOURS_12 = "12";
	private static final String HOURS_24 = "24";

	// Used for showing the current date format, which looks like "12/31/2010",
	// "2010/12/13", etc.
	// The date value is dummy (independent of actual date).
	private Calendar mDummyDate;

	public static final String SETTING_INFOS = "SETTINGInfos";

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

	// have we been launched from the setup wizard?
	protected static final String EXTRA_IS_FIRST_RUN = "firstRun";

	private static final String TAG = "DataTimePreference";

	private static final boolean D = true;

	private Preference mMorningTimeFromPref;
	private Preference mMorningTimeToPref;
	private Preference mNoonTimeFromPref;
	private Preference mNoonTimeToPref;
	private Preference mEveningTimeFromPref;
	private Preference mEveningTimeToPref;
	private Preference mNightTimeFromPref;
	private Preference mNightTimeToPref;
	private Preference mAnalysisTimeFromPref;
	private Preference mAnalysisTimeToPref;
	private Preference mStatisticTimeYearPref;
	private Preference mStatisticTimeHourPref;
	private CheckBoxPreference currentTimeCheckBox;
	private ExtendDiagPreference mSendAlarmDiagPreference;
	private CheckBoxPreference mAlarmTimeCheckBox;
	private Preference mAlarmDatePref;
	private Preference mAlarmTimePref;
	private ListPreference mAlarmTimePeroidPref;
	private NumberPickerDialogPreference mAlarmSecondPref;
	private NumberPickerDialogPreference mAlarmBeepSeqLengthPref;
	private NumberPickerDialogPreference mAlarmBeepSeqGapMinutesPref;

	private static final int DIALOG_TIMEPICKER_MORNING_FROM = 0;
	private static final int DIALOG_TIMEPICKER_MORNING_TO = 1;

	private static final int DIALOG_TIMEPICKER_NOON_FROM = 2;
	private static final int DIALOG_TIMEPICKER_NOON_TO = 3;

	private static final int DIALOG_TIMEPICKER_EVENING_FROM = 4;
	private static final int DIALOG_TIMEPICKER_EVENING_TO = 5;

	private static final int DIALOG_TIMEPICKER_NIGHT_FROM = 6;
	private static final int DIALOG_TIMEPICKER_NIGHT_TO = 7;

	private static final int DIALOG_TIMEPICKER_ANALYSIS_FROM = 8;
	private static final int DIALOG_TIMEPICKER_ANALYSIS_TO = 9;

	private static final int DIALOG_DATEPICKER_STATISTIC = 10;
	private static final int DIALOG_TIMEPICKER_STATISTIC = 11;
	private static final int DIALOG_ALARM_DATEPICKER = 12;
	private static final int DIALOG_ALARM_TIMEPICKER = 13;

	private PreferenceGroup mMorningCategory;
	private PreferenceGroup mNoonCategory;

	private PreferenceGroup mEveningCategory;
	private PreferenceGroup mNightCategory;
	private PreferenceGroup mAnalysisCategory;
	private PreferenceGroup mStatisticCategory;
	private PreferenceGroup mAlarmCategory;

	private int morningFromHour;
	private int morningFromMin;
	private int morningToHour;
	private int morningToMin;
	private int noonFromHour;
	private int noonFromMin;
	private int noonToHour;
	private int noonToMin;
	private int eveningFromHour;
	private int eveningFromMin;
	private int eveningToHour;
	private int eveningToMin;
	private int nightFromHour;
	private int nightFromMin;
	private int nightToHour;
	private int nightToMin;
	private int analysisFromHour;
	private int analysisFromMin;
	private int analysisToHour;
	private int analysisToMin;
	private boolean currentTime;
	private int statisticYear;
	private int statisticMonth;
	private int statisticDate;
	private int statisticHour;

	private int alarmYear;
	private int alarmMonth;
	private int alarmDate;
	private int alarmHour;
	private int alarmMin;
	private int alarmSec;
	private int alarmPeriod;
	private int alarmBeepSeqLen;
	private int alarmBeepSeqGapMinutes;
	private boolean alarmBeepChecked;

	private boDb boDbHelper2;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		addPreferencesFromResource(R.xml.date_time_prefs);
		boDbHelper2 = new boDb(getActivity(), "bloodox.db", null, 1);

		initUI();
	}

	private void initUI() {

		Intent intent = getActivity().getIntent();
		boolean isFirstRun = intent.getBooleanExtra(EXTRA_IS_FIRST_RUN, false);

		addPreferencesFromResource(R.xml.router_preference);
		final PreferenceScreen preferenceScreen = getPreferenceScreen();

		mMorningCategory = new PreferenceCategory(getActivity());
		mMorningCategory.setTitle("早晨时间段");
		preferenceScreen.addPreference(mMorningCategory);
		mMorningCategory.setEnabled(true);

		mNoonCategory = new PreferenceCategory(getActivity());
		mNoonCategory.setTitle("中午时间段");
		preferenceScreen.addPreference(mNoonCategory);
		mNoonCategory.setEnabled(true);

		mEveningCategory = new PreferenceCategory(getActivity());
		mEveningCategory.setTitle("晚上时间段");
		preferenceScreen.addPreference(mEveningCategory);
		mEveningCategory.setEnabled(true);

		mNightCategory = new PreferenceCategory(getActivity());
		mNightCategory.setTitle("夜晚时间段");
		preferenceScreen.addPreference(mNightCategory);
		mNightCategory.setEnabled(true);

		mAnalysisCategory = new PreferenceCategory(getActivity());
		mAnalysisCategory.setTitle("分析时间段");
		preferenceScreen.addPreference(mAnalysisCategory);
		mAnalysisCategory.setEnabled(true);

		mStatisticCategory = new PreferenceCategory(getActivity());
		mStatisticCategory.setTitle("统计截止时间");
		preferenceScreen.addPreference(mStatisticCategory);
		mStatisticCategory.setEnabled(true);

		mAlarmCategory = new PreferenceCategory(getActivity());
		mAlarmCategory.setTitle(R.string.TimeSet);
		preferenceScreen.addPreference(mAlarmCategory);
		mAlarmCategory.setEnabled(true);

	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences settings = getActivity().getSharedPreferences(
				SETTING_INFOS, 0); // 获取一个 SharedPreferences 对象

		morningFromHour = settings.getInt(MORNING_HOUR_FROM, 6);
		morningFromMin = settings.getInt(MORNING_MIN_FROM, 0);
		morningToHour = settings.getInt(MORNING_HOUR_TO, 11);
		morningToMin = settings.getInt(MORNING_MIN_TO, 0);
		noonFromHour = settings.getInt(NOON_HOUR_FROM, 11);
		noonFromMin = settings.getInt(NOON_MIN_FROM, 0);
		noonToHour = settings.getInt(NOON_HOUR_TO, 18);
		noonToMin = settings.getInt(NOON_MIN_TO, 0);
		eveningFromHour = settings.getInt(EVENING_HOUR_FROM, 18);
		eveningFromMin = settings.getInt(EVENING_MIN_FROM, 0);
		eveningToHour = settings.getInt(EVENING_HOUR_TO, 20);
		eveningToMin = settings.getInt(EVENING_MIN_TO, 0);
		nightFromHour = settings.getInt(NIGHT_HOUR_FROM, 20);
		nightFromMin = settings.getInt(NIGHT_MIN_FROM, 0);
		nightToHour = settings.getInt(NIGHT_HOUR_TO, 24);
		nightToMin = settings.getInt(NIGHT_MIN_TO, 0);
		analysisFromHour = settings.getInt(ANALYSIS_HOUR_FROM, 11);
		analysisFromMin = settings.getInt(ANALYSIS_MIN_FROM, 0);
		analysisToHour = settings.getInt(ANALYSIS_MIN_FROM, 18);
		analysisToMin = settings.getInt(ANALYSIS_MIN_TO, 0);

		// statistic
		currentTime = settings.getBoolean(STATISTIC_TIME_CHECK_BOX, true);
		statisticYear = settings.getInt(STATISTIC_TIME_YEAR, 2013);
		statisticMonth = settings.getInt(STATISTIC_TIME_MONTH, 1);
		statisticDate = settings.getInt(STATISTIC_TIME_DATE, 1);
		statisticHour = settings.getInt(STATISTIC_TIME_HOUR, 1);

		// Alarm time
		alarmBeepChecked = settings.getBoolean(ConstDef.ALARM_TIME_BEEP, true);
		alarmYear = settings.getInt(ConstDef.ALARM_TIME_YEAR, 2013);
		alarmMonth = settings.getInt(ConstDef.ALARM_TIME_MONTH, 1);
		alarmDate = settings.getInt(ConstDef.ALARM_TIME_DATE, 1);
		alarmHour = settings.getInt(ConstDef.ALARM_TIME_HOUR, 1);
		alarmMin = settings.getInt(ConstDef.ALARM_TIME_MINUTE, 1);
		alarmSec = settings.getInt(ConstDef.ALARM_TIME_SECONDS, 1);
		alarmBeepSeqLen = settings.getInt(ConstDef.ALARM_TIME_BEEP_SEQ_LENGTH,
				1);
		alarmBeepSeqGapMinutes = settings.getInt(
				ConstDef.ALARM_TIME_BEEP_SEQ_GAP_MINUTES, 1);
		alarmPeriod = settings.getInt(ConstDef.ALARM_TIME_PERIOD, 0);

		mMorningTimeFromPref = new Preference(getActivity());
		mMorningTimeFromPref.setTitle("起始时间");
		mMorningTimeFromPref.setKey("KEY_MORNING_FROM");

		String hour;
		String min;
		String sec;
		String beepSeqLen;
		String beepSeqGapMin;
		if (morningFromHour >= 10) {
			hour = String.valueOf(morningFromHour);
		} else {
			hour = "0" + String.valueOf(morningFromHour);
		}

		if (morningFromMin >= 10) {
			min = String.valueOf(morningFromMin);
		} else {
			min = "0" + String.valueOf(morningFromMin);
		}
		mMorningTimeFromPref.setSummary(hour + ":" + min);

		mMorningCategory.addPreference(mMorningTimeFromPref);

		mMorningTimeToPref = new Preference(getActivity());
		mMorningTimeToPref.setKey("KEY_MORNING_TO");
		mMorningTimeToPref.setTitle("截止时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(morningToHour) + ":"
							+ String.valueOf(morningFromMin));
		}
		if (morningToHour >= 10) {
			hour = String.valueOf(morningToHour);
		} else {
			hour = "0" + String.valueOf(morningToHour);
		}

		if (morningToMin >= 10) {
			min = String.valueOf(morningToMin);
		} else {
			min = "0" + String.valueOf(morningToMin);
		}
		mMorningTimeToPref.setSummary(hour + ":" + min);

		mMorningCategory.addPreference(mMorningTimeToPref);

		// noon from
		mNoonTimeFromPref = new Preference(getActivity());
		mNoonTimeFromPref.setKey("KEY_NOON_TO");
		mNoonTimeFromPref.setTitle("起始时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(noonFromHour) + ":"
							+ String.valueOf(noonFromMin));
		}
		if (noonFromHour >= 10) {
			hour = String.valueOf(noonFromHour);
		} else {
			hour = "0" + String.valueOf(noonFromHour);
		}

		if (noonFromMin >= 10) {
			min = String.valueOf(noonFromMin);
		} else {
			min = "0" + String.valueOf(noonFromMin);
		}
		mNoonTimeFromPref.setSummary(hour + ":" + min);

		mNoonCategory.addPreference(mNoonTimeFromPref);

		// noon to
		mNoonTimeToPref = new Preference(getActivity());
		mNoonTimeToPref.setKey("KEY_MORNING_TO");
		mNoonTimeToPref.setTitle("截止时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(noonToHour) + ":"
							+ String.valueOf(noonToHour));
		}
		if (noonToHour >= 10) {
			hour = String.valueOf(noonToHour);
		} else {
			hour = "0" + String.valueOf(noonToHour);
		}

		if (noonToMin >= 10) {
			min = String.valueOf(noonToMin);
		} else {
			min = "0" + String.valueOf(noonToMin);
		}
		mNoonTimeToPref.setSummary(hour + ":" + min);

		mNoonCategory.addPreference(mNoonTimeToPref);

		// Evening from
		mEveningTimeFromPref = new Preference(getActivity());
		mEveningTimeFromPref.setKey("KEY_NOON_TO");
		mEveningTimeFromPref.setTitle("起始时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(eveningFromHour) + ":"
							+ String.valueOf(eveningFromMin));
		}
		if (eveningFromHour >= 10) {
			hour = String.valueOf(eveningFromHour);
		} else {
			hour = "0" + String.valueOf(eveningFromHour);
		}

		if (eveningFromMin >= 10) {
			min = String.valueOf(eveningFromMin);
		} else {
			min = "0" + String.valueOf(eveningFromMin);
		}
		mEveningTimeFromPref.setSummary(hour + ":" + min);

		mEveningCategory.addPreference(mEveningTimeFromPref);

		// evening to
		mEveningTimeToPref = new Preference(getActivity());
		mEveningTimeToPref.setKey("KEY_MORNING_TO");
		mEveningTimeToPref.setTitle("截止时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(eveningToHour) + ":"
							+ String.valueOf(eveningToHour));
		}
		if (eveningToHour >= 10) {
			hour = String.valueOf(eveningToHour);
		} else {
			hour = "0" + String.valueOf(eveningToHour);
		}

		if (eveningToMin >= 10) {
			min = String.valueOf(eveningToMin);
		} else {
			min = "0" + String.valueOf(eveningToMin);
		}
		mEveningTimeToPref.setSummary(hour + ":" + min);

		mEveningCategory.addPreference(mEveningTimeToPref);

		// Night
		mNightTimeFromPref = new Preference(getActivity());
		mNightTimeFromPref.setKey("KEY_NOON_TO");
		mNightTimeFromPref.setTitle("起始时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(nightFromHour) + ":"
							+ String.valueOf(nightFromMin));
		}
		if (nightFromHour >= 10) {
			hour = String.valueOf(nightFromHour);
		} else {
			hour = "0" + String.valueOf(nightFromHour);
		}

		if (nightFromMin >= 10) {
			min = String.valueOf(nightFromMin);
		} else {
			min = "0" + String.valueOf(nightFromMin);
		}
		mNightTimeFromPref.setSummary(hour + ":" + min);

		mNightCategory.addPreference(mNightTimeFromPref);

		// night to
		mNightTimeToPref = new Preference(getActivity());
		mNightTimeToPref.setKey("KEY_MORNING_TO");
		mNightTimeToPref.setTitle("截止时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(nightToHour) + ":"
							+ String.valueOf(nightToHour));
		}
		if (nightToHour >= 10) {
			hour = String.valueOf(nightToHour);
		} else {
			hour = "0" + String.valueOf(nightToHour);
		}

		if (nightToMin >= 10) {
			min = String.valueOf(nightToMin);
		} else {
			min = "0" + String.valueOf(nightToMin);
		}
		mNightTimeToPref.setSummary(hour + ":" + min);

		mNightCategory.addPreference(mNightTimeToPref);

		// analysis
		mAnalysisTimeFromPref = new Preference(getActivity());
		mAnalysisTimeFromPref.setKey("KEY_NOON_TO");
		mAnalysisTimeFromPref.setTitle("起始时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(analysisFromHour) + ":"
							+ String.valueOf(analysisFromMin));
		}
		if (analysisFromHour >= 10) {
			hour = String.valueOf(analysisFromHour);
		} else {
			hour = "0" + String.valueOf(analysisFromHour);
		}

		if (analysisFromMin >= 10) {
			min = String.valueOf(analysisFromMin);
		} else {
			min = "0" + String.valueOf(analysisFromMin);
		}
		mAnalysisTimeFromPref.setSummary(hour + ":" + min);

		mAnalysisCategory.addPreference(mAnalysisTimeFromPref);

		// analysis to
		mAnalysisTimeToPref = new Preference(getActivity());
		mAnalysisTimeToPref.setKey("KEY_MORNING_TO");
		mAnalysisTimeToPref.setTitle("截止时间");

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					String.valueOf(analysisToHour) + ":"
							+ String.valueOf(analysisToHour));
		}
		if (analysisToHour >= 10) {
			hour = String.valueOf(analysisToHour);
		} else {
			hour = "0" + String.valueOf(analysisToHour);
		}

		if (analysisToMin >= 10) {
			min = String.valueOf(analysisToMin);
		} else {
			min = "0" + String.valueOf(analysisToMin);
		}
		mAnalysisTimeToPref.setSummary(hour + ":" + min);

		mAnalysisCategory.addPreference(mAnalysisTimeToPref);

		currentTimeCheckBox = new CheckBoxPreference(getActivity());
		currentTimeCheckBox.setTitle("截止到当前时间");
		currentTimeCheckBox.setOnPreferenceChangeListener(this);

		mStatisticTimeYearPref = new Preference(getActivity());
		mStatisticTimeYearPref.setKey("KEY_STATISTIC_YEAR");
		mStatisticTimeYearPref.setTitle("设置日期");

		mStatisticTimeHourPref = new Preference(getActivity());
		mStatisticTimeHourPref.setKey("KEY_STATISTIC_HOUR");
		mStatisticTimeHourPref.setTitle("设置时间");

		if (currentTime == true) {
			currentTimeCheckBox.setChecked(true);
			mStatisticTimeYearPref.setSummary(String.valueOf(statisticYear)
					+ "-" + String.valueOf(statisticMonth) + "-"
					+ String.valueOf(statisticDate));
			mStatisticTimeYearPref.setEnabled(false);
			if (statisticHour >= 10) {
				hour = String.valueOf(statisticHour);
			} else {
				hour = "0" + String.valueOf(statisticHour);
			}
			mStatisticTimeHourPref.setSummary(hour + ":" + "00");
			mStatisticTimeHourPref.setEnabled(false);
		} else {
			currentTimeCheckBox.setChecked(false);
			mStatisticTimeYearPref.setSummary(String.valueOf(statisticYear)
					+ "-" + String.valueOf(statisticMonth) + "-"
					+ String.valueOf(statisticDate));
			mStatisticTimeYearPref.setEnabled(true);
			if (statisticHour >= 10) {
				hour = String.valueOf(statisticHour);
			} else {
				hour = "0" + String.valueOf(statisticHour);
			}
			mStatisticTimeHourPref.setSummary(hour + ":" + "00");
			mStatisticTimeHourPref.setEnabled(true);
		}
		mStatisticCategory.addPreference(currentTimeCheckBox);
		mStatisticCategory.addPreference(mStatisticTimeYearPref);
		mStatisticCategory.addPreference(mStatisticTimeHourPref);
		updateTimeAndDateDisplay(getActivity());

		mSendAlarmDiagPreference = new ExtendDiagPreference(getActivity(), null);
		mSendAlarmDiagPreference.setKey(ConstDef.SEND_TIME_SET_MESSAGE_STRING);
		mSendAlarmDiagPreference.setTitle(R.string.SendTimingTitle);
		mSendAlarmDiagPreference.setSummary(R.string.SendTimingMessage);
		mSendAlarmDiagPreference.setDialogTitle(R.string.SendTimingTitle);
		mSendAlarmDiagPreference.setDialogMessage(R.string.SendTimingMessage);
		mSendAlarmDiagPreference.setOnPreferenceChangeListener(this);
		mSendAlarmDiagPreference.setOnClickListener(this);

		mAlarmTimeCheckBox = new CheckBoxPreference(getActivity());
		mAlarmTimeCheckBox.setTitle(R.string.TimeBeep);
		mAlarmTimeCheckBox.setOnPreferenceChangeListener(this);
		mAlarmTimeCheckBox.setChecked(alarmBeepChecked);

		mAlarmTimePeroidPref = new ListPreference(getActivity());
		mAlarmTimePeroidPref.setKey(ConstDef.TIME_SET_PERIOD_STRING);
		mAlarmTimePeroidPref.setTitle(R.string.TimePeriod);
		mAlarmTimePeroidPref.setDialogTitle(R.string.TimePeriod);
		mAlarmTimePeroidPref.setEntries(R.array.timerperiod);
		mAlarmTimePeroidPref.setEntryValues(R.array.timerperiod_value);
		mAlarmTimePeroidPref.setOnPreferenceChangeListener(this);
		mAlarmTimePeroidPref.setValueIndex(alarmPeriod);
		String[] alarmTimePeriodValues = getActivity().getResources()
				.getStringArray(R.array.timerperiod);
		mAlarmTimePeroidPref.setSummary(alarmTimePeriodValues[alarmPeriod]);

		mAlarmDatePref = new Preference(getActivity());
		mAlarmDatePref.setKey(ConstDef.TIME_SET_DATE_STRING);
		mAlarmDatePref.setTitle(R.string.SetDate);
		mAlarmDatePref.setSummary(String.valueOf(alarmYear) + "-"
				+ String.valueOf(alarmMonth + 1) + "-"
				+ String.valueOf(alarmDate));

		mAlarmTimePref = new Preference(getActivity());
		mAlarmTimePref.setKey(ConstDef.TIME_SET_TIME_STRING);
		mAlarmTimePref.setTitle(R.string.SetTime);

		if (alarmHour >= 10) {
			hour = String.valueOf(alarmHour);
		} else {
			hour = "0" + String.valueOf(alarmHour);
		}
		if (alarmMin >= 10) {
			min = String.valueOf(alarmMin);
		} else {
			min = "0" + String.valueOf(alarmMin);
		}
		mAlarmTimePref.setSummary(hour + ":" + min);

		mAlarmSecondPref = new NumberPickerDialogPreference(getActivity());
		mAlarmSecondPref.setKey(ConstDef.TIME_SET_SECOND_STRING);
		mAlarmSecondPref.setTitle(R.string.SetSecond);
		mAlarmSecondPref.setDialogTitle(R.string.SetSecond);
		mAlarmSecondPref.setValue(alarmSec);
		mAlarmSecondPref.setMaxValue(60);
		mAlarmSecondPref.setMinValue(0);
		mAlarmSecondPref.setOnPreferenceChangeListener(this);
		if (alarmSec >= 10) {
			sec = String.valueOf(alarmSec);
		} else {
			sec = "0" + String.valueOf(alarmSec);
		}
		mAlarmSecondPref.setSummary(sec);

		mAlarmBeepSeqLengthPref = new NumberPickerDialogPreference(
				getActivity());
		mAlarmBeepSeqLengthPref.setKey(ConstDef.BEEP_SET_SEQ_LEN_STRING);
		mAlarmBeepSeqLengthPref.setTitle(R.string.SetBeepSeqLen);
		mAlarmBeepSeqLengthPref.setDialogTitle(R.string.SetBeepSeqLen);
		mAlarmBeepSeqLengthPref.setValue(alarmBeepSeqLen);
		mAlarmBeepSeqLengthPref.setMaxValue(100);
		mAlarmBeepSeqLengthPref.setMinValue(0);
		mAlarmBeepSeqLengthPref.setOnPreferenceChangeListener(this);
		if (alarmBeepSeqLen >= 10) {
			beepSeqLen = String.valueOf(alarmBeepSeqLen);
		} else {
			beepSeqLen = "0" + String.valueOf(alarmBeepSeqLen);
		}
		mAlarmBeepSeqLengthPref.setSummary(beepSeqLen);

		mAlarmBeepSeqGapMinutesPref = new NumberPickerDialogPreference(
				getActivity());
		mAlarmBeepSeqGapMinutesPref
				.setKey(ConstDef.BEEP_SET_SEQ_GAP_MINUTES_STRING);
		mAlarmBeepSeqGapMinutesPref.setTitle(R.string.SetBeepSeqGapMinutes);
		mAlarmBeepSeqGapMinutesPref
				.setDialogTitle(R.string.SetBeepSeqGapMinutes);
		mAlarmBeepSeqGapMinutesPref.setValue(alarmBeepSeqGapMinutes);
		mAlarmBeepSeqGapMinutesPref.setMaxValue(60);
		mAlarmBeepSeqGapMinutesPref.setMinValue(0);
		mAlarmBeepSeqGapMinutesPref.setOnPreferenceChangeListener(this);
		if (alarmBeepSeqGapMinutes >= 10) {
			beepSeqGapMin = String.valueOf(alarmBeepSeqGapMinutes);
		} else {
			beepSeqGapMin = "0" + String.valueOf(alarmBeepSeqGapMinutes);
		}
		mAlarmBeepSeqGapMinutesPref.setSummary(beepSeqGapMin);

		mAlarmCategory.addPreference(mSendAlarmDiagPreference);
		mAlarmCategory.addPreference(mAlarmTimeCheckBox);
		mAlarmCategory.addPreference(mAlarmTimePeroidPref);
		mAlarmCategory.addPreference(mAlarmDatePref);
		mAlarmCategory.addPreference(mAlarmTimePref);
		mAlarmCategory.addPreference(mAlarmSecondPref);
		mAlarmCategory.addPreference(mAlarmBeepSeqLengthPref);
		mAlarmCategory.addPreference(mAlarmBeepSeqGapMinutesPref);
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	public void updateTimeAndDateDisplay(Context context) {
		java.text.DateFormat shortDateFormat = DateFormat
				.getDateFormat(context);
		final Calendar now = Calendar.getInstance();

		// mMorningTimeFromPref.setSummary(DateFormat.getTimeFormat(getActivity()).format(now.getTime()));

	}

	public void onClick(DialogInterface dialog, int which) {
		if (dialog.equals(mSendAlarmDiagPreference.getDialog())) {

			if (which == DialogInterface.BUTTON_POSITIVE) {
				Intent alarmTimeIntent = new Intent();
				float[] coeffs = new float[7];
				coeffs[0] = alarmYear;
				coeffs[1] = alarmMonth + 1;
				coeffs[2] = alarmDate;
				coeffs[3] = alarmHour * 60 * 60 + alarmMin * 60 + alarmSec;
				coeffs[4] = alarmBeepChecked ? 1 : 0;
				coeffs[5] = alarmBeepSeqLen;
				coeffs[6] = alarmBeepSeqGapMinutes;
				switch (alarmPeriod) {
				case 0: {
					coeffs[0] = -1;
					break;
				}
				case 1: {
					coeffs[0] = -1;
					coeffs[1] = -1;
					break;
				}
				case 2: {
					coeffs[0] = -1;
					coeffs[1] = -1;
					coeffs[2] = -1;
					break;
				}

				}
				alarmTimeIntent
						.setAction(ConstDef.ALARM_TIME_REQ_BROADCAST_MESSAGE);
				alarmTimeIntent.putExtra(ConstDef.ALARM_TIME_COEFFS, coeffs);
				getActivity().sendBroadcast(alarmTimeIntent);
			}
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		final Activity activity = getActivity();
		if (activity != null) {
			setDate(activity, year, month, day);
			updateTimeAndDateDisplay(activity);
		}
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		final Activity activity = getActivity();

		if (activity != null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "onTimeSet");
			}
			mMorningTimeFromPref.setSummary(String.valueOf(hourOfDay) + ":"
					+ String.valueOf(minute));
			updateTimeAndDateDisplay(activity);
		}

		// We don't need to call timeUpdated() here because the TIME_CHANGED
		// broadcast is sent by the AlarmManager as a side effect of setting the
		// SystemClock time.
	}

	public void onTimeSetMorningFrom(TimePicker view, int hourOfDay, int minute) {
		final Activity activity = getActivity();

		if (activity != null) {

			mMorningTimeFromPref.setSummary(String.valueOf(hourOfDay) + ":"
					+ String.valueOf(minute));
			updateTimeAndDateDisplay(activity);
		}

		// We don't need to call timeUpdated() here because the TIME_CHANGED
		// broadcast is sent by the AlarmManager as a side effect of setting the
		// SystemClock time.
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences,
			String key) {

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "CheckBox changed");
		}
		if (preference == mAlarmTimeCheckBox) {
			SharedPreferences settings = getActivity().getSharedPreferences(
					SETTING_INFOS, 0); // 首先获取一个 SharedPreferences 对象
			Editor edit = settings.edit();

			if (newValue.toString().equals("true")) {
				alarmBeepChecked = true;

			} else {
				alarmBeepChecked = false;
			}

			mAlarmTimeCheckBox.setChecked(alarmBeepChecked);
			edit.putBoolean(ConstDef.ALARM_TIME_BEEP, alarmBeepChecked);
			edit.commit();
		} else if (preference == currentTimeCheckBox)

		{
			SharedPreferences settings = getActivity().getSharedPreferences(
					SETTING_INFOS, 0); // 首先获取一个 SharedPreferences 对象
			Editor edit = settings.edit();
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "CheckBox changed");
			}

			if (newValue.toString().equals("true")) {
				currentTimeCheckBox.setChecked(true);
				mStatisticTimeYearPref.setEnabled(false);

				mStatisticTimeHourPref.setEnabled(false);
				edit.putBoolean(STATISTIC_TIME_CHECK_BOX, true);
				edit.commit();
			} else {
				currentTimeCheckBox.setChecked(false);
				mStatisticTimeYearPref.setEnabled(true);

				mStatisticTimeHourPref.setEnabled(true);
				edit.putBoolean(STATISTIC_TIME_CHECK_BOX, false);
				edit.commit();
			}
		} else if (preference == mAlarmSecondPref) {
			alarmSec = Integer.parseInt(newValue.toString());
			SharedPreferences settings = getActivity().getSharedPreferences(
					SETTING_INFOS, 0); // 首先获取一个 SharedPreferences 对象
			Editor edit = settings.edit();
			// Send the Alarm Timer Message
			mAlarmSecondPref.setValue(alarmSec);
			mAlarmSecondPref.setSummary(newValue.toString());
			edit.putInt(ConstDef.ALARM_TIME_SECONDS, alarmSec);

			edit.commit();
		} else if (preference == mAlarmBeepSeqLengthPref) {
			alarmBeepSeqLen = Integer.parseInt(newValue.toString());
			SharedPreferences settings = getActivity().getSharedPreferences(
					SETTING_INFOS, 0); // 首先获取一个 SharedPreferences 对象
			Editor edit = settings.edit();
			// Send the Alarm Timer Message
			mAlarmBeepSeqLengthPref.setValue(alarmBeepSeqLen);
			mAlarmBeepSeqLengthPref.setSummary(newValue.toString());
			edit.putInt(ConstDef.ALARM_TIME_BEEP_SEQ_LENGTH, alarmBeepSeqLen);

			edit.commit();
		} else if (preference == mAlarmBeepSeqGapMinutesPref) {
			alarmBeepSeqGapMinutes = Integer.parseInt(newValue.toString());
			SharedPreferences settings = getActivity().getSharedPreferences(
					SETTING_INFOS, 0); // 首先获取一个 SharedPreferences 对象
			Editor edit = settings.edit();
			// Send the Alarm Timer Message
			mAlarmBeepSeqGapMinutesPref.setValue(alarmBeepSeqGapMinutes);
			mAlarmBeepSeqGapMinutesPref.setSummary(newValue.toString());
			edit.putInt(ConstDef.ALARM_TIME_BEEP_SEQ_GAP_MINUTES,
					alarmBeepSeqGapMinutes);

			edit.commit();
		} else if (preference == mAlarmTimePeroidPref) {
			alarmPeriod = Integer.parseInt(newValue.toString());
			String[] alarmTimePeriodValues = getActivity().getResources()
					.getStringArray(R.array.timerperiod);
			SharedPreferences settings = getActivity().getSharedPreferences(
					SETTING_INFOS, 0); // 首先获取一个 SharedPreferences 对象
			Editor edit = settings.edit();

			mAlarmTimePeroidPref.setValueIndex(alarmPeriod);
			mAlarmTimePeroidPref.setSummary(alarmTimePeriodValues[alarmPeriod]);
			edit.putInt(ConstDef.ALARM_TIME_PERIOD, alarmPeriod);

			edit.commit();
		}
		return false;
	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog d;

		switch (id) {

		case DIALOG_TIMEPICKER_MORNING_FROM: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {
								if (BuildConfig.DEBUG) {
									Log.i(TAG, "cyf+onTimeSetListener");
								}
								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mMorningTimeFromPref.setSummary(hour + ":"
										+ min);
								if (BuildConfig.DEBUG) {
									Log.i(TAG, String.valueOf(hourOfDay) + ":"
											+ String.valueOf(minute));
								}
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								// boDbHelper.setTime(1, hourOfDay, minute);
								settings.edit()
										.putInt(MORNING_HOUR_FROM, hourOfDay)
										.commit();
								settings.edit()
										.putInt(MORNING_MIN_FROM, minute)
										.commit();
								boDbHelper2.setTime(1, hourOfDay, minute);

							}
						}

					}, morningFromHour, morningFromMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_MORNING_TO: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {

								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mMorningTimeToPref.setSummary(hour + ":" + min);
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								settings.edit()
										.putInt(MORNING_HOUR_TO, hourOfDay)
										.commit();
								settings.edit().putInt(MORNING_MIN_TO, minute)
										.commit();
								boDbHelper2.setTime(2, hourOfDay, minute);
							}
						}

					}, morningToHour, morningToMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_NOON_FROM: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {
								if (BuildConfig.DEBUG) {
									Log.i(TAG, "onTimeSetListener:noon from");
								}
								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mNoonTimeFromPref.setSummary(hour + ":" + min);
								if (BuildConfig.DEBUG) {
									Log.i(TAG, String.valueOf(hourOfDay) + ":"
											+ String.valueOf(minute));
								}
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								boDbHelper2.setTime(3, hourOfDay, minute);
								settings.edit()
										.putInt(NOON_HOUR_FROM, hourOfDay)
										.commit();
								settings.edit().putInt(NOON_MIN_FROM, minute)
										.commit();
							}
						}

					}, noonFromHour, noonFromMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_NOON_TO: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {

								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mNoonTimeToPref.setSummary(hour + ":" + min);
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								boDbHelper2.setTime(4, hourOfDay, minute);
								settings.edit().putInt(NOON_HOUR_TO, hourOfDay)
										.commit();
								settings.edit().putInt(NOON_MIN_TO, minute)
										.commit();
							}
						}

					}, noonToHour, noonToMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_EVENING_FROM: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {
								if (BuildConfig.DEBUG) {
									Log.i(TAG, "onTimeSetListener:evening from");
								}
								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mEveningTimeFromPref.setSummary(hour + ":"
										+ min);
								if (BuildConfig.DEBUG) {
									Log.i(TAG, String.valueOf(hourOfDay) + ":"
											+ String.valueOf(minute));
								}
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								boDbHelper2.setTime(5, hourOfDay, minute);
								settings.edit()
										.putInt(EVENING_HOUR_FROM, hourOfDay)
										.commit();
								settings.edit()
										.putInt(EVENING_MIN_FROM, minute)
										.commit();
							}
						}

					}, eveningFromHour, eveningFromMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_EVENING_TO: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {

								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mEveningTimeToPref.setSummary(hour + ":" + min);
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								boDbHelper2.setTime(6, hourOfDay, minute);
								settings.edit()
										.putInt(EVENING_HOUR_TO, hourOfDay)
										.commit();
								settings.edit().putInt(EVENING_MIN_TO, minute)
										.commit();
							}
						}

					}, eveningToHour, eveningToMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_NIGHT_FROM: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {
								if (BuildConfig.DEBUG) {
									Log.i(TAG, "onTimeSetListener:night from");
								}
								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mNightTimeFromPref.setSummary(hour + ":" + min);
								if (BuildConfig.DEBUG) {
									Log.i(TAG, String.valueOf(hourOfDay) + ":"
											+ String.valueOf(minute));
								}
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								boDbHelper2.setTime(7, hourOfDay, minute);
								settings.edit()
										.putInt(NIGHT_HOUR_FROM, hourOfDay)
										.commit();
								settings.edit().putInt(NIGHT_MIN_FROM, minute)
										.commit();
							}
						}

					}, nightFromHour, nightFromMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_NIGHT_TO: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {

								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mNightTimeToPref.setSummary(hour + ":" + min);
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								boDbHelper2.setTime(8, hourOfDay, minute);
								settings.edit()
										.putInt(NIGHT_HOUR_TO, hourOfDay)
										.commit();
								settings.edit().putInt(NIGHT_MIN_TO, minute)
										.commit();
							}
						}

					}, nightToHour, nightToMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_ANALYSIS_FROM: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {
								if (BuildConfig.DEBUG) {
									Log.i(TAG,
											"onTimeSetListener:analysis from");
								}
								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mAnalysisTimeFromPref.setSummary(hour + ":"
										+ min);
								if (BuildConfig.DEBUG) {
									Log.i(TAG, String.valueOf(hourOfDay) + ":"
											+ String.valueOf(minute));
								}
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								boDbHelper2.setTime(9, hourOfDay, minute);
								settings.edit()
										.putInt(ANALYSIS_HOUR_FROM, hourOfDay)
										.commit();
								settings.edit()
										.putInt(ANALYSIS_MIN_FROM, minute)
										.commit();
							}
						}

					}, analysisFromHour, analysisFromMin, true);
			break;
		}
		case DIALOG_TIMEPICKER_ANALYSIS_TO: {
			final Calendar calendar = Calendar.getInstance();
			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {

								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mAnalysisTimeToPref
										.setSummary(hour + ":" + min);
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								boDbHelper2.setTime(10, hourOfDay, minute);
								settings.edit()
										.putInt(ANALYSIS_HOUR_TO, hourOfDay)
										.commit();
								settings.edit().putInt(ANALYSIS_MIN_TO, minute)
										.commit();
							}
						}

					}, analysisToHour, analysisToMin, true);
			break;
		}

		case DIALOG_DATEPICKER_STATISTIC: {
			;
			d = new DatePickerDialog(getActivity(),
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int month, int day) {
							final Activity activity = getActivity();

							if (activity != null) {

								statisticYear = year;
								statisticMonth = month;
								statisticDate = day;
								mStatisticTimeYearPref.setSummary(String
										.valueOf(statisticYear)
										+ "-"
										+ String.valueOf(statisticMonth + 1)
										+ "-" + String.valueOf(statisticDate));
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								// boDbHelper.setTime(1, hourOfDay, minute);
								settings.edit()
										.putInt(STATISTIC_TIME_YEAR,
												statisticYear).commit();
								settings.edit()
										.putInt(STATISTIC_TIME_MONTH,
												statisticMonth + 1).commit();
								settings.edit()
										.putInt(STATISTIC_TIME_DATE,
												statisticDate).commit();
							}
						}

					}, statisticYear, statisticMonth, statisticDate);
			break;
		}

		case DIALOG_TIMEPICKER_STATISTIC: {

			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {

								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mStatisticTimeHourPref.setSummary(hour + ":"
										+ min);
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								statisticHour = hourOfDay;
								settings.edit()
										.putInt(STATISTIC_TIME_HOUR, hourOfDay)
										.commit();
								// settings.edit().putInt(ANALYSIS_MIN_TO,
								// minute).commit();
							}
						}

					}, statisticHour, 0, true);
			break;
		}
		case DIALOG_ALARM_DATEPICKER: {
			;
			d = new DatePickerDialog(getActivity(),
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int month, int day) {
							final Activity activity = getActivity();

							if (activity != null) {

								alarmYear = year;
								alarmMonth = month;
								alarmDate = day;
								mAlarmDatePref.setSummary(String
										.valueOf(alarmYear)
										+ "-"
										+ String.valueOf(alarmMonth + 1)
										+ "-"
										+ String.valueOf(alarmDate));
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								// boDbHelper.setTime(1, hourOfDay, minute);
								settings.edit()
										.putInt(ConstDef.ALARM_TIME_YEAR,
												alarmYear).commit();
								settings.edit()
										.putInt(ConstDef.ALARM_TIME_MONTH,
												alarmMonth).commit();
								settings.edit()
										.putInt(ConstDef.ALARM_TIME_DATE,
												alarmDate).commit();
							}
						}

					}, alarmYear, alarmMonth, alarmDate);
			break;
		}
		case DIALOG_ALARM_TIMEPICKER: {

			d = new TimePickerDialog(getActivity(),
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							final Activity activity = getActivity();

							if (activity != null) {

								String hour;
								String min;
								if (hourOfDay >= 10) {
									hour = String.valueOf(hourOfDay);
								} else {
									hour = "0" + String.valueOf(hourOfDay);
								}

								if (minute >= 10) {
									min = String.valueOf(minute);
								} else {
									min = "0" + String.valueOf(minute);
								}
								mAlarmTimePref.setSummary(hour + ":" + min);
								SharedPreferences settings = getActivity()
										.getSharedPreferences(SETTING_INFOS, 0);
								alarmHour = hourOfDay;
								alarmMin = minute;
								settings.edit()
										.putInt(ConstDef.ALARM_TIME_HOUR,
												alarmHour).commit();
								settings.edit()
										.putInt(ConstDef.ALARM_TIME_MINUTE,
												alarmMin).commit();
							}
						}

					}, alarmHour, alarmMin, true);
			break;
		}
		default:
			d = null;
			break;
		}

		return d;
	}

	/*
	 * @Override public void onPrepareDialog(int id, Dialog d) { switch (id) {
	 * case DIALOG_DATEPICKER: { DatePickerDialog datePicker =
	 * (DatePickerDialog)d; final Calendar calendar = Calendar.getInstance();
	 * datePicker.updateDate( calendar.get(Calendar.YEAR),
	 * calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	 * break; } case DIALOG_TIMEPICKER: { TimePickerDialog timePicker =
	 * (TimePickerDialog)d; final Calendar calendar = Calendar.getInstance();
	 * timePicker.updateTime( calendar.get(Calendar.HOUR_OF_DAY),
	 * calendar.get(Calendar.MINUTE)); break; } default: break; } }
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		if (preference == mMorningTimeFromPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_MORNING_FROM);
			showDialog(DIALOG_TIMEPICKER_MORNING_FROM);

		} else if (preference == mMorningTimeToPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_MORNING_TO);
			showDialog(DIALOG_TIMEPICKER_MORNING_TO);

		} else if (preference == mNoonTimeFromPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_NOON_FROM);
			showDialog(DIALOG_TIMEPICKER_NOON_FROM);

		} else if (preference == mNoonTimeToPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_NOON_TO);
			showDialog(DIALOG_TIMEPICKER_NOON_TO);

		} else if (preference == mEveningTimeFromPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_EVENING_FROM);
			showDialog(DIALOG_TIMEPICKER_EVENING_FROM);

		} else if (preference == mEveningTimeToPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_EVENING_TO);
			showDialog(DIALOG_TIMEPICKER_EVENING_TO);

		} else if (preference == mNightTimeFromPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_NIGHT_FROM);
			showDialog(DIALOG_TIMEPICKER_NIGHT_FROM);

		} else if (preference == mNightTimeToPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_NIGHT_TO);
			showDialog(DIALOG_TIMEPICKER_NIGHT_TO);

		} else if (preference == mAnalysisTimeFromPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_ANALYSIS_FROM);
			showDialog(DIALOG_TIMEPICKER_ANALYSIS_FROM);

		} else if (preference == mAnalysisTimeToPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_ANALYSIS_TO);
			showDialog(DIALOG_TIMEPICKER_ANALYSIS_TO);

		} else if (preference == mStatisticTimeYearPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_DATEPICKER_STATISTIC);
			showDialog(DIALOG_DATEPICKER_STATISTIC);

		} else if (preference == mStatisticTimeHourPref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_TIMEPICKER_STATISTIC);
			showDialog(DIALOG_TIMEPICKER_STATISTIC);

		} else if (preference == mAlarmDatePref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_ALARM_DATEPICKER);
			showDialog(DIALOG_ALARM_DATEPICKER);

		} else if (preference == mAlarmTimePref) {
			// The 24-hour mode may have changed, so recreate the dialog
			removeDialog(DIALOG_ALARM_TIMEPICKER);
			showDialog(DIALOG_ALARM_TIMEPICKER);

		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		updateTimeAndDateDisplay(getActivity());
	}

	private void timeUpdated() {
		Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
		getActivity().sendBroadcast(timeChanged);
	}

	/* Get & Set values from the system settings */

	private boolean is24Hour() {
		return DateFormat.is24HourFormat(getActivity());
	}

	private void set24Hour(boolean is24Hour) {
		Settings.System.putString(getContentResolver(),
				Settings.System.TIME_12_24, is24Hour ? HOURS_24 : HOURS_12);
	}

	private String getDateFormat() {
		return Settings.System.getString(getContentResolver(),
				Settings.System.DATE_FORMAT);
	}

	/* package */static void setDate(Context context, int year, int month,
			int day) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
					.setTime(when);
		}
	}

	/* package */static void setTime(Context context, int hourOfDay, int minute) {

	}

	/* Helper routines to format timezone */

	/* package */static String getTimeZoneText(TimeZone tz) {
		// Similar to new SimpleDateFormat("'GMT'Z, zzzz").format(new Date()),
		// but
		// we want "GMT-03:00" rather than "GMT-0300".
		Date now = new Date();
		return formatOffset(new StringBuilder(), tz, now)
				.append(", ")
				.append(tz.getDisplayName(tz.inDaylightTime(now), TimeZone.LONG))
				.toString();
	}

	private static StringBuilder formatOffset(StringBuilder sb, TimeZone tz,
			Date d) {
		int off = tz.getOffset(d.getTime()) / 1000 / 60;

		sb.append("GMT");
		if (off < 0) {
			sb.append('-');
			off = -off;
		} else {
			sb.append('+');
		}

		int hours = off / 60;
		int minutes = off % 60;

		sb.append((char) ('0' + hours / 10));
		sb.append((char) ('0' + hours % 10));

		sb.append(':');

		sb.append((char) ('0' + minutes / 10));
		sb.append((char) ('0' + minutes % 10));

		return sb;
	}

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final Activity activity = getActivity();
			if (activity != null) {
				updateTimeAndDateDisplay(activity);
			}
		}
	};
}
