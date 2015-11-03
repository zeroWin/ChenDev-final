package mobileMedical.namespace;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobileMedical.Message.MessageInfo;
import mobileMedical.adapter.ListViewThreeItemAdapter;
import mobileMedical.database.DBManager;
import mobileMedical.util.Utility;

public class ElectrocardiogramHistory extends Activity {
	public static final String SETTING_INFOS = "SETTINGInfos";
	public static final String STATISTIC_TIME_CHECK_BOX = "STATISTICTIMECHECKBOX";
	public static final String STATISTIC_TIME_YEAR = "STATISTICTIMEYEAR";
	public static final String STATISTIC_TIME_MONTH = "STATISTICTIMEMONTH";
	public static final String STATISTIC_TIME_DATE = "STATISTICTIMEDATE";
	public static final String STATISTIC_TIME_HOUR = "STATISTICTIMEHOUR";
	private List<Map<String, Object>> patientHistoryInfoList;
	private ListViewThreeItemAdapter patientHistoryInfotableAdapter;
	private ListView patientHistoryInfolv;
	private Button btnBldHour, btnBldDay, btnBldWeek, btnBldMonth;
	private boolean currentTime;
	private int statisticYear;
	private int statisticMonth;
	private int statisticDate;
	private int statisticHour;
	private DBManager dbManager;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private long m_end,dateMs;
	boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.electrocardiogram_st_history);
		dbManager = new DBManager(ElectrocardiogramHistory.this);
		initView();
		initPatientHistoryInfoTable();

		SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);
		currentTime = settings.getBoolean(STATISTIC_TIME_CHECK_BOX, true);
		statisticYear = settings.getInt(STATISTIC_TIME_YEAR, 2013);
		statisticMonth = settings.getInt(STATISTIC_TIME_MONTH, 1);
		statisticDate = settings.getInt(STATISTIC_TIME_DATE, 1);
		statisticHour = settings.getInt(STATISTIC_TIME_HOUR, 1);
		String Year;
		String Month;
		String Day;
		String Hour;
		
		if (currentTime == true) {
			m_end = System.currentTimeMillis();
		} else {
			Year = String.valueOf(statisticYear);
			if (statisticMonth > 10) {
				Month = String.valueOf(statisticMonth);
			} else {
				Month = "0" + String.valueOf(statisticMonth);
			}

			if (statisticDate > 10) {
				Day = String.valueOf(statisticDate);
			} else {
				Day = "0" + String.valueOf(statisticDate);
			}
			if (statisticHour > 10) {
				Hour = String.valueOf(statisticHour);
			} else {
				Hour = "0" + String.valueOf(statisticHour);
			}
			Date dateEnd = null;
			try {
				dateEnd = format.parse(Year + "-" + Month + "-" + Day + " "
						+ Hour + ":" + "00");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_end = dateEnd.getTime();
		}

		// set up the history info list
		patientHistoryInfotableAdapter = new ListViewThreeItemAdapter(this,
				patientHistoryInfoList); // 创建适配器
		patientHistoryInfolv.setAdapter(patientHistoryInfotableAdapter);
		Utility.setListViewHeightBasedOnChildren(patientHistoryInfolv);

		patientHistoryInfolv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				BodyTmpActivity.transTimeTmpData=patientHistoryInfoList.get(arg2).get("time").toString();
//				Intent startMeasIntent = new Intent();
//				startMeasIntent.setClass(BloodPressureHistory.this, MobileMedicalActivity.class);
//				startMeasIntent.putExtra("tabindex", 0);
//				startActivity(startMeasIntent);
			}
		});
		
		btnBldHour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				refreshPatientHistoryInfoTable(DateUtils.HOUR_IN_MILLIS);
				patientHistoryInfotableAdapter.notifyDataSetChanged();
			}
		});
		btnBldDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				refreshPatientHistoryInfoTable(DateUtils.DAY_IN_MILLIS);
				patientHistoryInfotableAdapter.notifyDataSetChanged();
			}
		});
		btnBldWeek.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				refreshPatientHistoryInfoTable(DateUtils.WEEK_IN_MILLIS);
				patientHistoryInfotableAdapter.notifyDataSetChanged();
			}
		});
		btnBldMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				refreshPatientHistoryInfoTable(DateUtils.DAY_IN_MILLIS*30);
				patientHistoryInfotableAdapter.notifyDataSetChanged();
			}
		});

	}

	private void initView() {
		patientHistoryInfolv = (ListView) this
				.findViewById(R.id.ElectrocardiogramHistory);
		btnBldHour = (Button) findViewById(R.id.bldHour);
		btnBldDay = (Button) findViewById(R.id.bldDay);
		btnBldWeek = (Button) findViewById(R.id.bldWeek);
		btnBldMonth = (Button) findViewById(R.id.bldMonth);
	}

	private void initPatientHistoryInfoTable() {
		// Get the latest BodyTempValue display the last body temp
		String item = "";
		String value = "";
		String dateString = "";
		String doctor = "";
		int doctorId = 0 ;

		patientHistoryInfoList = new ArrayList<Map<String, Object>>();

		Cursor cursor = boDbHelper.getOneSpecItem(MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			item = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_SENSORTYPE));
			item = MemberManage.turnSensorTypeFromNum(item);
			value = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			value = value.split(" ")[0];
			dateString = cursor.getString(cursor.getColumnIndex("timestamp"));
			dateString = MemberManage.turnTimeToCNTime(dateString);
			doctorId= cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c=dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor= c.getString(c
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
	}

	private void refreshPatientHistoryInfoTable(long period) {
		// Get the latest BodyTempValue display the last body temp
		String item = "";
		String value = "";
		String dateString = "";
		String doctor = "";
		int doctorId = 0 ;

		patientHistoryInfoList.clear();

		Cursor cursor = boDbHelper.getOneSpecItem(MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			item = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_SENSORTYPE));
			item = MemberManage.turnSensorTypeFromNum(item);
			value = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			value = value.split(" ")[0];
			dateString = cursor.getString(cursor.getColumnIndex("timestamp"));
			dateString = MemberManage.turnTimeToCNTime(dateString);
			doctorId= cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_DOCTORID));
			Cursor c=dbManager.queryDoctorInfo(doctorId);
			c.moveToNext();
			doctor= c.getString(c
					.getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
			c.close();
			dbManager.closeDB();

			try {
				Date date = format.parse(dateString);
				 dateMs = date.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(dateMs > (m_end - period)&& m_end >= dateMs){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", item);
				map.put("info", value);
				map.put("time", dateString);
				map.put("doctor", doctor);
				map.put("doctorId", doctorId);
				patientHistoryInfoList.add(map);
				dateMs=0;
			}
			
			
			// }
		}
		cursor.close();
	}
}
