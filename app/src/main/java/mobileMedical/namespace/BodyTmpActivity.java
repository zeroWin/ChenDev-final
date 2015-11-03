package mobileMedical.namespace;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mobileMedical.Message.MeasItemResult;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.MessageInfo;
import mobileMedical.Message.ParameterDataKeys;

import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartAxis;
import com.artfulbits.aiCharts.Base.ChartPoint;
import com.artfulbits.aiCharts.Base.ChartPointCollection;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.Types.ChartTypes;

import devDataType.Parameters.IntParameter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class BodyTmpActivity extends Activity {
	private ChartSeries series = null;
	private ChartSeries seriesBase = null;
	private int tempUpLimite = 38;
	private int tempLowLimite = 31;
	private ChartView chartViewBodyTemp = null;

	private ChartView chartViewBaseBodyTemp = null;
	boDb btDbHelper = new boDb(this, "bloodox.db", null, 1);
	private boolean m_NewResults = true;
	private MeasItemResult[] measItemResults;
	public static boolean baseTempBool;
	public static String transTimeTmpData = "";

	private int m_TransID;
	private int m_SensorType;
	private int m_MeasItem;
	private int m_DoctorID;
	private int m_PatientID;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.body_temp);

		CheckBox baseTemp = (CheckBox) findViewById(R.id.checkBoxBaseBodyTemp);
		baseTemp.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					baseTempBool = true;
				} else {
					baseTempBool = false;
				}
			}
		});
		chartViewBodyTemp = (ChartView) findViewById(R.id.chartViewBodyTemp);

		series = chartViewBodyTemp.getSeries().get(0);

		chartViewBodyTemp.getAreas().get(0).getDefaultYAxis().getScale()
				.setRange(20, 45);
		chartViewBodyTemp.getAreas().get(0).getDefaultYAxis().getScale()
				.setInterval(5.0);
		chartViewBodyTemp.getAreas().get(0).getDefaultYAxis()
				.setGridVisible(false);

		chartViewBodyTemp.getAreas().get(0).getDefaultXAxis().getScale()
				.setRange(0, 4);
		chartViewBodyTemp.getAreas().get(0).getDefaultXAxis()
				.setGridVisible(false);

		chartViewBodyTemp.getAreas().get(0).getDefaultXAxis()
				.setLabelsAdapter(ChartAxis.SMART_LABELS_ADAPTER);

		chartViewBaseBodyTemp = (ChartView) findViewById(R.id.chartViewBaseBodyTemp);

		seriesBase = chartViewBaseBodyTemp.getSeries().get(0);

		/*
		 * seriesBaseBodyTemp.getPoints().addXY(2,37).setAxisLabel("18:00");
		 * 
		 * seriesBaseBodyTemp.getPoints().addXY(1,0).setAxisLabel("17:00");
		 * seriesBaseBodyTemp.getPoints().addXY(3,0).setAxisLabel("19:00");
		 */
		chartViewBaseBodyTemp.getAreas().get(0).getDefaultYAxis().getScale()
				.setRange(20, 45);
		chartViewBaseBodyTemp.getAreas().get(0).getDefaultYAxis().getScale()
				.setInterval(5.0);
		chartViewBaseBodyTemp.getAreas().get(0).getDefaultYAxis()
				.setGridVisible(false);

		chartViewBaseBodyTemp.getAreas().get(0).getDefaultXAxis().getScale()
				.setRange(0, 4);
		chartViewBaseBodyTemp.getAreas().get(0).getDefaultXAxis()
				.setGridVisible(false);
		chartViewBaseBodyTemp.getAreas().get(0).getDefaultXAxis()
				.setLabelsAdapter(ChartAxis.SMART_LABELS_ADAPTER);

		// // display the last body temp
		// Cursor cursor = btDbHelper.getBodyTempAndBodyBaseTempLast();
		// display the lastest one data
		Cursor cursor = null;
		if (transTimeTmpData.equalsIgnoreCase("measure")) {
			transTimeTmpData = "";
		} else if (transTimeTmpData.equalsIgnoreCase("")) {
			cursor = btDbHelper.getLatestBodyOrBaseTem();
		} else {
			transTimeTmpData = MemberManage
					.turnCNTimeToDBTime(transTimeTmpData);
			cursor = btDbHelper.getSpecCursorByTime(transTimeTmpData);
			transTimeTmpData = "";
		}

		while (cursor != null && cursor.moveToNext()) {
			int type = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASITEM));
			String bt = cursor.getString(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			String dateString = cursor.getString(cursor
					.getColumnIndex("timestamp"));
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");

			Date date = null;
			try {
				date = format.parse(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dateString = format.format(date.getTime()
					+ DateUtils.HOUR_IN_MILLIS * 8);

			// DecimalFormat df = new DecimalFormat("###.00");
			// Temperature.setText(String.valueOf(df.format(bodyTempRet)));

			// Quanwei: we have to use the double, because addXY()function need
			// the double args. If use the float here,
			// it will result in the inconsistency between Temperature and
			// Point, for example, Temperature display 27.78,
			// but Point display 27.79 label.
			double bodyTempFormated = Double.parseDouble(bt);

			if (type == MessageInfo.MM_MI_BODY_TEMPERATURE) {
				series.getPoints().removeAll(series.getPoints());
				ChartPoint point = series.getPoints()
						.addXY(2, bodyTempFormated);
				if (bodyTempFormated > tempUpLimite) {
					point.setBackColor(0xffcc0000);
				} else if (bodyTempFormated < tempLowLimite) {
					point.setBackColor(0xff0099cc);
				} else
					point.setBackColor(0xff669900);

				series.getPoints().addXY(1, 0).setAxisLabel("");
				series.getPoints().addXY(3, 0).setAxisLabel("");

				point.setShowLabel(true);
				point.setAxisLabel(dateString);

			}
			if (type == MessageInfo.MM_MI_BODY_BASE_TEMPERATURE) {
				seriesBase.getPoints().removeAll(seriesBase.getPoints());
				ChartPoint point = seriesBase.getPoints().addXY(2,
						bodyTempFormated);
				if (bodyTempFormated > tempUpLimite) {
					point.setBackColor(0xffcc0000);
				} else if (bodyTempFormated < tempLowLimite) {
					point.setBackColor(0xff0099cc);
				} else
					point.setBackColor(0xff669900);

				seriesBase.getPoints().addXY(1, 0).setAxisLabel("");
				seriesBase.getPoints().addXY(3, 0).setAxisLabel("");

				point.setShowLabel(true);
				point.setAxisLabel(dateString);

			}
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(ConstDef.BODYTEMP_MEAS_RESULTS_BROADCAST_MESSAGE)) {

				if (m_NewResults) {
					// It means that it is initiated measure by node.

					// m_NewResults = false;
					m_TransID = ((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.TRANSID)).GetValue();
					// Should increase TransId by 1 to keep it is monotonic
					// increasing.
					m_TransID += 1;
					m_SensorType = MessageInfo.SENSORTYPE_THERMOMETER;
					m_MeasItem = MessageInfo.MM_MI_BODY_TEMPERATURE;
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.TRANSID))
							.SetValue(m_TransID);
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASITEM))
							.SetValue(m_MeasItem);
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.SENSORTYPE))
							.SetValue(m_SensorType);
					// m_DoctorID = ((IntParameter) MessageData.parmsDataHashMap
					// .get(ParameterDataKeys.DOCTORID)).GetValue();
					// m_PatientID = ((IntParameter)
					// MessageData.parmsDataHashMap
					// .get(ParameterDataKeys.PATIENTID)).GetValue();

				} else {
					m_NewResults = true;
				}

				// Bundle bundle = intent.getExtras();

				// int cmd = bundle.getInt("cmmd");

				// if(cmd == MobileMedicalActivity.BODYTMP_RESULTS){
				// measItemResults = (MeasItemResult[])
				// intent.getExtras().getParcelableArray("BodyTmpRet");
				// measItemResults = (MeasItemResult[])
				// bundle.getParcelableArray("BodyTmpRet");
				// Bundle bundle = intent.getExtras();
				// float bb = bundle.getFloat("BodyTmpRet");
				// measItemResults = new MeasItemResult[2];
				/*
				 * ArrayList<Person> measItemResultsPerson = new
				 * ArrayList<Person>(); if(bundle !=null) {
				 * measItemResultsPerson =
				 * intent.getParcelableArrayListExtra("BodyTmpRet"); }
				 */

				// display new result
				ArrayList<MeasItemResult> measItemResultsList = new ArrayList<MeasItemResult>();

				measItemResultsList = intent
						.getParcelableArrayListExtra(ConstDef.RESULTS);

				if (measItemResultsList != null
						&& !measItemResultsList.isEmpty()) {
					// Object[] tmpObjects = (measItemResultsList.toArray());
					int listSize = measItemResultsList.size();
					measItemResults = (MeasItemResult[]) measItemResultsList
							.toArray(new MeasItemResult[listSize]);
				}
				float bodyTempRet = 2;
				if (measItemResults != null && measItemResults.length == 1
						&& measItemResults[0].GetFloatResults().length == 1) {
					bodyTempRet = measItemResults[0].GetFloatResults()[0];
				}
				// MeasItemResult ee = bundle.getParcelable("BodyTmpRetParc");
				TextView Temperature = (TextView) findViewById(R.id.textViewBodyTempValue);
				DecimalFormat df = new DecimalFormat("###.00");
				// Temperature.setText(String.valueOf(df.format(bodyTempRet)));

				// Quanwei: we have to use the double, because addXY()function
				// need the double args. If use the float here,
				// it will result in the inconsistency between Temperature and
				// Point, for example, Temperature display 27.78,
				// but Point display 27.79 label.
				double bodyTempFormated = (double) (Math
						.round(bodyTempRet * 100)) / 100;

				Temperature
						.setText(String.valueOf(df.format(bodyTempFormated)));
				
				displayLastTemp();
				
				long m_time = System.currentTimeMillis();
				Date date = new Date(m_time);
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String dateString = format.format(date);

				btDbHelper.insertBt(btDbHelper, bodyTempFormated);

				Intent resultDislayedInformIntent = new Intent();
				resultDislayedInformIntent.putExtra(ConstDef.CMD,
						ConstDef.MEAS_RETS_DISPLAY_STATE);
				resultDislayedInformIntent.putExtra(ConstDef.RESULTS_DISPLAYED,
						true);
				resultDislayedInformIntent
						.setAction(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE);
				sendBroadcast(resultDislayedInformIntent);
				
				/*
				 * if (measItemResults.length == 1 &&
				 * measItemResults[0].GetResultsSize() == 1) { float temResult =
				 * measItemResults[0].GetResults()[0]; TextView Temperature =
				 * (TextView) findViewById(R.id.textViewBodyTempValue);
				 * Temperature.setText(String.valueOf(temResult)); }
				 * 
				 * // Fist Time, do not send it. Maybe we can also do it like
				 * the MessageProcessThread. // To start this thread only when
				 * received the broadcast from MobileMedical Activity. Intent
				 * newIntent = new Intent(); newIntent.putExtra("cmd",
				 * MobileMedicalActivity.MEAS_RETS_DISPLAY_STATE);
				 * newIntent.putExtra("ResultsDisplayed", true);
				 * newIntent.setAction
				 * ("android.intent.action.measResultsDisplayState");
				 * sendBroadcast(newIntent); // }
				 */
			} else if (action.equals(ConstDef.MEAS_INIT_BROADCAST_MESSAGE)) {
				m_NewResults = false;
				Bundle bundle = intent.getExtras();
				int measType = bundle.getInt(ConstDef.MeasType);
				if (measType != ConstDef.BodyTempTabIndex) {
					return;
				}
				TextView Temperature = (TextView) findViewById(R.id.textViewBodyTempValue);
				Temperature.setText(R.string.nullValue);

				displayLastTemp();
				
			}
		}

	};

	private void displayLastTemp(){
		// display the last body temp result
		Cursor cursor = btDbHelper.getBodyTempAndBodyBaseTempLast();
		while (cursor.moveToNext()) {
			int type = cursor.getInt(cursor
					.getColumnIndex(ConstDef.DATABASE_FIELD_MEASITEM));
			String bt = cursor
					.getString(cursor
							.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
			String dateString = cursor.getString(cursor
					.getColumnIndex("timestamp"));
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");

			Date date = null;
			try {
				date = format.parse(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dateString = format.format(date.getTime()
					+ DateUtils.HOUR_IN_MILLIS * 8);
			// DecimalFormat df = new DecimalFormat("###.00");
			// Temperature.setText(String.valueOf(df.format(bodyTempRet)));

			// Quanwei: we have to use the double, because
			// addXY()function need the double args. If use the float
			// here,
			// it will result in the inconsistency between Temperature
			// and Point, for example, Temperature display 27.78,
			// but Point display 27.79 label.
			double bodyTempFormated = Double.parseDouble(bt);

			if (type == MessageInfo.MM_MI_BODY_TEMPERATURE) {
				series.getPoints().removeAll(series.getPoints());
				seriesBase.getPoints()
						.removeAll(seriesBase.getPoints());
				chartViewBodyTemp.getAreas().get(0).getDefaultXAxis()
						.getScale().setRange(0, 4);

				ChartPoint point = series.getPoints().addXY(2,
						bodyTempFormated);
				if (bodyTempFormated > tempUpLimite) {
					point.setBackColor(0xffcc0000);

				} else if (bodyTempFormated < tempLowLimite) {
					point.setBackColor(0xff0099cc);

				} else {
					point.setBackColor(0xff669900);
				}

				point.setShowLabel(true);
				point.setAxisLabel(dateString);
				series.getPoints().addXY(1, 0).setAxisLabel("");
				series.getPoints().addXY(3, 0).setAxisLabel("");
			} else if (type == MessageInfo.MM_MI_BODY_BASE_TEMPERATURE) {
				series.getPoints().removeAll(series.getPoints());
				seriesBase.getPoints()
						.removeAll(seriesBase.getPoints());
				chartViewBaseBodyTemp.getAreas().get(0)
						.getDefaultXAxis().getScale().setRange(0, 4);
				ChartPoint point = seriesBase.getPoints().addXY(2,
						bodyTempFormated);
				if (bodyTempFormated > tempUpLimite) {
					point.setBackColor(0xffcc0000);

				} else if (bodyTempFormated < tempLowLimite) {
					point.setBackColor(0xff0099cc);

				} else {
					point.setBackColor(0xff669900);

				}

				seriesBase.getPoints().addXY(1, 0).setAxisLabel("");
				seriesBase.getPoints().addXY(3, 0).setAxisLabel("");

				point.setShowLabel(true);
				point.setAxisLabel(dateString);
			}
		}
		cursor.close();
	}
	
	/*
	 * //The Handler that gets information back from the BluetoothChatService
	 * private static final Handler mHandler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { switch (msg.what) { }
	 * }
	 * 
	 * };
	 * 
	 * public static Handler GetHandler() { return mHandler; }
	 */
	
	@Override
	public synchronized void onResume() {
		super.onResume();
		try {
			this.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		IntentFilter filter = new IntentFilter(
				ConstDef.BODYTEMP_MEAS_RESULTS_BROADCAST_MESSAGE);
		this.registerReceiver(mReceiver, filter);
		IntentFilter filter2 = new IntentFilter(
				ConstDef.MEAS_INIT_BROADCAST_MESSAGE);
		this.registerReceiver(mReceiver, filter2);
	}
	
	@Override
	public synchronized void onPause() {
		super.onPause();
		try {
			this.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}