package mobileMedical.namespace;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartAxisScale;
import com.artfulbits.aiCharts.Base.ChartPointCollection;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Types.ChartTypes;

import java.util.ArrayList;

import devDataType.Parameters.IntParameter;
import mobileMedical.Message.MeasItemResult;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.MessageInfo;
import mobileMedical.Message.ParameterDataKeys;

public class PulmonaryVentilationActivity extends Activity {

	boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);

	private final int MaxPoints = 200;

	private final float mSampleRate = 200.0f;
	private int m_ResultsPackageIndex = 0;
	private char[] mResults = null;
	private char[] mMeasItemCharResults = null;
	private final int mResultsParameters = 3;
	private int mResultsItemNum = 0;
	private boolean m_NewResults = true;
	private ChartPointCollection m_TargetCollection;
	private ChartView m_ChartView;
	private ChartSeries m_ChartSeries;
	private ChartArea m_ChartArea;
	private final int GET_RESULTS = 1;
	private static final String TAG = "PulmonaryVentilationActivity";

	private int m_PointsCounter = 0;
	public static int revtransId = 0;
	private int m_MeasRetPackageIndex = 2;
	private int m_ViewMaxPoints = 100000;
	private int m_ViewMaxPointsRepeat = 0;

	private MeasItemResult[] m_MeasItemResults;
	private TextView m_PulVent;
	private ArrayList<MeasItemResult> m_MeasItemResultsList;
	private int m_ListSize;
	private int m_ModeTimes;
	private int m_MultipleTimes;

	private int m_MesaItemsResultLen;
	private int m_Idx;
	private int m_MeasItemIdex;
	private int m_PulVentWaveResult;
	private int m_PulVentResult;
	private int m_MaxPoints = 200;
	private int m_YAxisMax = 6000;
	private int m_YAxisMin = 0;
	private int m_XAxisMin = 0;

	private int m_TransID;
	private int m_SensorType;
	private int m_MeasItem;
	private int m_DoctorID;
	private int m_PatientID;

	private SQLiteDatabase m_DataBaseSQL;
	private StringBuilder m_TotalDataPackage;
	private ContentValues m_ContentValues;

	// #region Public methods
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pulmonaryventilation);

		m_PulVent = (TextView) findViewById(R.id.textViewPulmonaryVentilationValue);

		m_MeasItemResultsList = new ArrayList<MeasItemResult>();

		m_ChartView = (ChartView) findViewById(R.id.chartViewPulVent);

		m_DataBaseSQL = boDbHelper.getWritableDb();
		m_TotalDataPackage = new StringBuilder();
		m_ContentValues = new ContentValues();

		m_ChartSeries = new ChartSeries("FastLineSeries", ChartTypes.FastLine);
		m_ChartView.setPanning(ChartView.PANNING_BOTH);
		m_ChartView.getSeries().add(m_ChartSeries);
		m_ChartArea = new ChartArea("pulvent");
		m_ChartArea.getDefaultXAxis().getScale()
				.setMargin(ChartAxisScale.MARGIN_NONE);
		m_ChartArea.getDefaultXAxis().setShowLabels(true);
		m_ChartArea.getDefaultYAxis().setTitle(
				getString(R.string.pulmonaryventilation));

		m_ChartView.getAreas().add(m_ChartArea);

		SharedPreferences settings = getSharedPreferences(
				ConstDef.SHAREPRE_SETTING_INFOS, 0); // 首先获取一个 SharedPreferences
														// 对象
		float pulmonaryDisplFramLen = settings.getFloat(
				ConstDef.SHAREPRE_PULMONARY_DISPLAY_FRAME_LEN, 0);
		m_MaxPoints = Math.round(pulmonaryDisplFramLen * mSampleRate
				/ MessageInfo.PULVENT_WAVEFORM_SAMPLES_NUM_PER_PACKET)
				* MessageInfo.PULVENT_WAVEFORM_SAMPLES_NUM_PER_PACKET;
		;
		if (m_MaxPoints < MaxPoints) {
			m_MaxPoints = MaxPoints;
		}

		m_TargetCollection = m_ChartSeries.getPoints();

		String time = this.getIntent().getStringExtra("time");
		if (time == null) {
			time = "empty";
		}
		// show the history diagram
		Cursor cursor = null;
		// if time equal -1, it means to get the latest info
		if (time.equalsIgnoreCase("lastest")) {
			cursor = boDbHelper
					.getLatestInfoList(MessageInfo.SENSORTYPE_PULMONARYVENTILATION);
		} else {
			cursor = boDbHelper.getInfoListByTime(
					MessageInfo.SENSORTYPE_PULMONARYVENTILATION, time);
		}

		if (cursor.getCount() > 0) {
			m_PointsCounter = 0;

			while (cursor.moveToNext()) {

				String value = cursor.getString(cursor
						.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS));
				int length = value.split(" ").length;
				int pulVentResult = Integer.valueOf(value.split(" ")[0]);
				int data[] = new int[length - 1];
				for (int i = 0; i < length - 1; i++) {
					data[i] = Integer.valueOf(value.split(" ")[i + 1]);
				}
				m_PulVent.setText(String.valueOf(pulVentResult));

				m_ModeTimes = m_PointsCounter % m_MaxPoints;
				m_MultipleTimes = m_PointsCounter / m_MaxPoints;
				if (m_PointsCounter > m_ViewMaxPoints) {
					m_TargetCollection.clear();
					m_PointsCounter = 0;
					m_ViewMaxPointsRepeat++;
					if (BuildConfig.DEBUG) {
						Log.i("m_ViewMaxPoints",
								String.valueOf(m_ViewMaxPointsRepeat));
					}
				} else if (m_ModeTimes == 0 && m_PointsCounter != 0) {
					// m_TargetCollection.clear();
					m_ChartArea
							.getDefaultXAxis()
							.getScale()
							.setRange(
									m_XAxisMin,
									(m_MultipleTimes + 1) * m_MaxPoints
											/ mSampleRate);

					m_ChartArea
							.getDefaultXAxis()
							.getScale()
							.setZoom(
									m_MultipleTimes * m_MaxPoints / mSampleRate,
									m_MaxPoints / mSampleRate);
				} else if (m_PointsCounter == 0) {
					m_ChartArea.getDefaultYAxis().getScale()
							.setRange(m_YAxisMin, m_YAxisMax);

					m_ChartArea.getDefaultXAxis().getScale()
							.setRange(m_XAxisMin, m_MaxPoints / mSampleRate);

				}

				for (int index = 0; index < data.length; index++) {
					m_PulVentWaveResult = data[index];
					m_TargetCollection.addXY(m_PointsCounter / mSampleRate,
							m_PulVentWaveResult);
					m_PointsCounter++;
				}
			}
		} else {

			/*
			 * area.getDefaultXAxis().getScale().setInterval(10.0,ChartAxisScale.
			 * IntervalType.Milliseconds);
			 * 
			 * area.getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
			 * 
			 * area.getDefaultXAxis().setLabelFormat(new
			 * SimpleDateFormat("mm:ss"));
			 * area.getDefaultXAxis().setLabelAngle(15);
			 */

			// set default x axis
			/*
			 * double x = System.currentTimeMillis(); double init_start = x;
			 * double init_end = x + 2000;
			 * 
			 * area.getDefaultXAxis().getScale().setRange(init_start, init_end);
			 * area.getDefaultXAxis().getScale().setZoom(init_start, 2000);
			 */

			m_ChartArea.getDefaultYAxis().getScale()
					.setRange(m_YAxisMin, m_YAxisMax);

			m_ChartArea.getDefaultXAxis().getScale()
					.setRange(m_XAxisMin, m_MaxPoints / mSampleRate);

		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		SharedPreferences settings = getSharedPreferences(
				ConstDef.SHAREPRE_SETTING_INFOS, 0); // 首先获取一个 SharedPreferences
														// 对象
		float pulmonaryDisplFramLen = settings.getFloat(
				ConstDef.SHAREPRE_PULMONARY_DISPLAY_FRAME_LEN, 0);
		m_MaxPoints = Math.round(pulmonaryDisplFramLen * mSampleRate
				/ MessageInfo.PULVENT_WAVEFORM_SAMPLES_NUM_PER_PACKET)
				* MessageInfo.PULVENT_WAVEFORM_SAMPLES_NUM_PER_PACKET;
		;
		if (m_MaxPoints < MaxPoints) {
			m_MaxPoints = MaxPoints;
		}
		try {
			this.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			// TODO: handle exception
		}
		IntentFilter filter = new IntentFilter(
				ConstDef.PULVENT_MEAS_RESULTS_BROADCAST_MESSAGE);
		this.registerReceiver(mReceiver, filter);
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

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		boolean initPhase = true;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConstDef.MEAS_INIT_BROADCAST_MESSAGE)) {
				Bundle bundle = intent.getExtras();
				int measType = bundle.getInt(ConstDef.MeasType);
				if (measType != ConstDef.PulVentTabIndex) {
					return;
				}

				m_PulVent.setText(R.string.nullValue);

				// Clear the pulse waveform trace

				m_TargetCollection.clear();

				// Reset the m_PointsCounter
				m_PointsCounter = 0;

				m_NewResults = true;

				m_ResultsPackageIndex = 0;

			} else if (action
					.equals(ConstDef.PULVENT_MEAS_RESULTS_BROADCAST_MESSAGE)) {

				if (m_NewResults) {
					// It means that it is initiated measure by node.

					m_NewResults = false;
					m_TransID = ((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.TRANSID)).GetValue();
					// Should increase TransId by 1 to keep it is monotonic
					// increasing.
					m_TransID += 1;
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.TRANSID))
							.SetValue(m_TransID);
					m_SensorType = MessageInfo.SENSORTYPE_PULMONARYVENTILATION;
					m_MeasItem = MessageInfo.MM_MI_PULMONARY_VENTILATION;
					m_DoctorID = ((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.DOCTORID)).GetValue();
					m_PatientID = ((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.PATIENTID)).GetValue();

					m_ResultsPackageIndex = 0;
					m_PointsCounter = 0;
				}

				m_MeasItemResultsList = intent
						.getParcelableArrayListExtra(ConstDef.RESULTS);

				if (m_MeasItemResultsList != null
						&& !m_MeasItemResultsList.isEmpty()) {
					// Object[] tmpObjects = (m_MeasItemResultsList.toArray());
					m_ListSize = m_MeasItemResultsList.size();
					m_MeasItemResults = (MeasItemResult[]) m_MeasItemResultsList
							.toArray(new MeasItemResult[m_ListSize]);
				}

				if (m_MeasItemResults != null) {
					mResultsItemNum = m_MeasItemResults.length;
				}

				m_ModeTimes = m_PointsCounter % m_MaxPoints;
				m_MultipleTimes = m_PointsCounter / m_MaxPoints;
				if (m_PointsCounter > m_ViewMaxPoints) {
					m_TargetCollection.clear();
					m_PointsCounter = 0;
					m_ViewMaxPointsRepeat++;
					if (BuildConfig.DEBUG) {
						Log.i("m_ViewMaxPoints",
								String.valueOf(m_ViewMaxPointsRepeat));
					}
				} else if (m_ModeTimes == 0 && m_PointsCounter != 0) {
					// m_TargetCollection.clear();
					m_ChartArea
							.getDefaultXAxis()
							.getScale()
							.setRange(
									m_XAxisMin,
									(m_MultipleTimes + 1) * m_MaxPoints
											/ mSampleRate);

					m_ChartArea
							.getDefaultXAxis()
							.getScale()
							.setZoom(
									m_MultipleTimes * m_MaxPoints / mSampleRate,
									m_MaxPoints / mSampleRate);
				}

				m_MesaItemsResultLen = mResultsItemNum
						* MessageInfo.PULVENT_HYBRID_MEASITEM_RESULT_LEN;
				m_Idx = 0;
				mResults = new char[m_MesaItemsResultLen];
				m_MeasItemIdex = 0;
				m_PulVentWaveResult = 0;
				m_PulVentResult = 0;

				// Clear the string builder
				// m_TotalDataPackage = new StringBuilder();
				// m_DataBaseSQL = boDbHelper.getWritableDb();

				if (m_TotalDataPackage.length() > 0) {
					m_TotalDataPackage.delete(0,
							m_TotalDataPackage.length() - 1);
				}

				// m_TargetCollection.beginUpdate();
				do {

					mResultsItemNum -= 1;
					mMeasItemCharResults = m_MeasItemResults[m_MeasItemIdex]
							.GetCharResults();
					System.arraycopy(mMeasItemCharResults, 0, mResults, m_Idx,
							MessageInfo.PULVENT_HYBRID_MEASITEM_RESULT_LEN);
					m_Idx += MessageInfo.PULVENT_HYBRID_MEASITEM_RESULT_LEN;
					/*
					 * Message m = updateHandler .obtainMessage(1, Spo2, hR,
					 * pulseWave);
					 * 
					 * Bundle bundle = new Bundle(); bundle.putFloat("value",
					 * obj);
					 * 
					 * m.setData(bundle); updateHandler .sendMessage(m);
					 */
					m_PulVentResult = mMeasItemCharResults[MessageInfo.PULVENTVAL_RESULT_IDX];

					m_PulVent.setText(String.valueOf(m_PulVentResult));

					m_TotalDataPackage.append(m_PulVentResult + " ");
					for (int index = 0; index < MessageInfo.PULVENT_WAVEFORM_SAMPLES_NUM_PER_PACKET; index++) {
						m_PulVentWaveResult = mResults[MessageInfo.PULVENT_WAVEFORM_RESULT_STARTIDX
								+ index];
						// float normPulseWaveResult = (float) pulseWaveResult /
						// 65535;
						// pulseWaveResult = Math.round(normPulseWaveResult *
						// 200);
						m_TotalDataPackage.append(m_PulVentWaveResult + " ");
						// m_TargetCollection.addXY(m_PointsCounter,m_PulVentWaveResult);

						m_TargetCollection.addXY(m_PointsCounter / mSampleRate,
								m_PulVentWaveResult);
						m_PointsCounter++;
					}
				} while (mResultsItemNum > 0);
				// m_TargetCollection.endUpdate();

				m_DataBaseSQL.beginTransaction();

				m_ContentValues.clear();
				m_ContentValues.put(ConstDef.DATABASE_FIELD_TRANSID, m_TransID);
				m_ContentValues.put(ConstDef.DATABASE_FIELD_SENSORTYPE,
						m_SensorType);
				m_ContentValues.put(ConstDef.DATABASE_FIELD_MEASITEM,
						m_MeasItem);
				m_ContentValues.put(ConstDef.DATABASE_FIELD_DOCTORID,
						m_DoctorID);
				m_ContentValues.put(ConstDef.DATABASE_FIELD_PATIENTID,
						m_PatientID);
				m_ContentValues.put(ConstDef.DATABASE_FIELD_MEASRESULTS,
						m_TotalDataPackage.toString().trim());
				m_ContentValues.put(ConstDef.DATABASE_FIELD_MEASRESULTSIDX,
						m_ResultsPackageIndex);
				if (BuildConfig.DEBUG) {
					Log.d("id", m_TransID + " index " + m_ResultsPackageIndex
							+ " value " + m_TotalDataPackage.toString().trim());
				}
				m_DataBaseSQL.insert(ConstDef.DATABASE_TABLE_NAME_DATA, null,
						m_ContentValues);

				m_ResultsPackageIndex++;

				m_DataBaseSQL.setTransactionSuccessful();
				// 在setTransactionSuccessful和endTransaction之间不进行任何数据库操作

				// 当所有操作执行完成后结束一个事务
				m_DataBaseSQL.endTransaction();
				// m_DataBaseSQL.close();

				m_Idx = 0;

				// Fist Time, do not send it. Maybe we can also do it like the
				// MessageProcessThread.
				// To start this thread only when received the broadcast from
				// MobileMedical Activity.
				Intent resultDislayedInformIntent = new Intent();
				resultDislayedInformIntent.putExtra(ConstDef.CMD,
						ConstDef.MEAS_RETS_DISPLAY_STATE);
				resultDislayedInformIntent.putExtra(ConstDef.RESULTS_DISPLAYED,
						true);
				resultDislayedInformIntent
						.setAction(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE);
				sendBroadcast(resultDislayedInformIntent);

			}

			// Can add some other code here to trigger the new statistical view.
		}
	};

}
