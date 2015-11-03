package mobileMedical.namespace;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartAxis;
import com.artfulbits.aiCharts.Base.ChartAxisScale;
import com.artfulbits.aiCharts.Base.ChartPoint;
import com.artfulbits.aiCharts.Base.ChartPointCollection;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Types.ChartTypes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BloodoxStatistics extends Activity {
	private double m_start = 0;
	private static int duration = 0;// default 1 hour
	private static final boolean D = true;
	private static final String TAG = "BO Statistics";
	private static ChartViewAdv chartViewPulse = null;

	boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);
	private OnPointClickListener pointClickListener = new OnPointClickListener() {
		public void onPointClick(ChartPoint point) {
			Toast.makeText(BloodoxStatistics.this, point.toString(),
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public synchronized void onResume() {
		super.onResume();

		// 读出当前用户的每次测试的脉搏最大值最小值平均值和SPO的最小值
		Cursor cursor = boDbHelper.getAllStatistics();
		int maxPulse = 0;
		int minPulse = 0;
		int avePulse = 0;
		int minSpo = 0;
		int test_id = 0;
		String myDate;
		try {

			while (cursor.moveToNext()) {

				maxPulse = cursor.getInt(cursor.getColumnIndex("maxpulse"));
				minPulse = cursor.getInt(cursor.getColumnIndex("minpulse"));
				avePulse = cursor.getInt(cursor.getColumnIndex("avepulse"));
				minSpo = cursor.getInt(cursor.getColumnIndex("minspo"));
				test_id = cursor.getInt(cursor.getColumnIndex("testid"));
				myDate = cursor.getString(cursor.getColumnIndex("timestamp"));

				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");

				Date date = format.parse(myDate);
				if (m_start == 0) {
					m_start = date.getTime();
				}
				// 把最大值最小值平均值写入脉搏统计chart
				// 最大值
				ChartPointCollection pointsMax = chartViewPulse.getSeries()
						.get(0).getPoints();

				chartViewPulse
						.getSeries()
						.get(0)
						.setMarkerDrawable(
								getResources().getDrawable(
										R.drawable.diamonds_4));

				pointsMax.addXY(date.getTime() + 28800000, maxPulse);// 调整到中国时区,+8
				// 最小值
				ChartPointCollection pointsMin = chartViewPulse.getSeries()
						.get(1).getPoints();

				chartViewPulse
						.getSeries()
						.get(1)
						.setMarkerDrawable(
								getResources().getDrawable(
										R.drawable.sport_golf_practice));

				pointsMin.addXY(date.getTime() + 28800000, minPulse);

				// 最小值
				ChartPointCollection pointsAve = chartViewPulse.getSeries()
						.get(2).getPoints();

				chartViewPulse
						.getSeries()
						.get(2)
						.setMarkerDrawable(
								getResources()
										.getDrawable(R.drawable.stop_blue));

				pointsAve.addXY(date.getTime() + 28800000, avePulse);

				// Spo 最小值

				ChartPointCollection pointsSpo = chartViewPulse.getSeries()
						.get(3).getPoints();

				chartViewPulse
						.getSeries()
						.get(3)
						.setMarkerDrawable(
								getResources().getDrawable(
										R.drawable.sport_golf_practice));

				pointsSpo.addXY(date.getTime() + 28800000, minSpo);

			}
			cursor.close();
			// 设置时间长度
			long m_end = System.currentTimeMillis();
			switch (duration) {
			case 0:
				// set 1 hour
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setRange(m_end - 3600 * 1000, m_end);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setInterval(10.0, ChartAxisScale.IntervalType.Minutes);
				chartViewPulse.getAreas().get(0).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.resetZoom();

				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setRange(m_end - 3600 * 1000, m_end);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setInterval(10.0, ChartAxisScale.IntervalType.Minutes);
				chartViewPulse.getAreas().get(1).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.resetZoom();
				break;
			case 1:
				// set 1 day
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setRange(m_end - 24 * 3600 * 1000, m_end);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setInterval(6.0, ChartAxisScale.IntervalType.Hours);
				chartViewPulse.getAreas().get(0).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.resetZoom();

				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setRange(m_end - 24 * 3600 * 1000, m_end);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setInterval(6.0, ChartAxisScale.IntervalType.Hours);
				chartViewPulse.getAreas().get(1).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.resetZoom();
				break;
			case 2:
				// set 1 week
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setRange(m_end - 7 * 24 * 3600 * 1000, m_end);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setInterval(1.0, ChartAxisScale.IntervalType.Days);
				chartViewPulse.getAreas().get(0).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("dd HH:mm:ss"));
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.resetZoom();

				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setRange(m_end - 7 * 24 * 3600 * 1000, m_end);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setInterval(1.0, ChartAxisScale.IntervalType.Days);
				chartViewPulse.getAreas().get(1).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("dd HH:mm:ss"));
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.resetZoom();
				break;
			case 3:
				// set 1 month
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setRange(m_end - 30 * DateUtils.DAY_IN_MILLIS, m_end);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setInterval(6.0, ChartAxisScale.IntervalType.Days);
				chartViewPulse
						.getAreas()
						.get(0)
						.getDefaultXAxis()
						.setLabelFormat(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.resetZoom();

				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setRange(m_end - 30 * DateUtils.DAY_IN_MILLIS, m_end);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setInterval(6.0, ChartAxisScale.IntervalType.Days);
				chartViewPulse
						.getAreas()
						.get(1)
						.getDefaultXAxis()
						.setLabelFormat(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.resetZoom();
				break;

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

	}

	@Override
	public synchronized void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/** Called when the activity is first created. */

		super.onCreate(savedInstanceState);

		setContentView(R.layout.bldoxstatistics);
		/*
		 * MeasDataFilesOperator measdFilesOperator = new
		 * MeasDataFilesOperator(this); String[] measDataStrings =
		 * measdFilesOperator.ReadMeasDataFromFile(true); if (measDataStrings !=
		 * null) { String contentString = null; for (int i = 0; i <
		 * measDataStrings.length; i++) { contentString = measDataStrings[i]; //
		 * we can alos use the RegularExpressions
		 * if(!contentString.contains("-")) { // If it contents the "-", it is
		 * the measDataInfo
		 * 
		 * } } }
		 */

		chartViewPulse = new ChartViewAdv(this, R.xml.chart);
		ChartSeries series4 = new ChartSeries("s4", ChartTypes.Line);
		ChartArea area2 = new ChartArea("area2");
		series4.setArea("area2");
		chartViewPulse.getAreas().add(area2);
		chartViewPulse.getSeries().add(series4);
		chartViewPulse.increaseAreaNum();

		chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
				.setMargin(ChartAxisScale.MARGIN_NONE);
		chartViewPulse.getAreas().get(0).getDefaultYAxis()
				.setTitle(getString(R.string.pulse_axis_title));
		chartViewPulse.getAreas().get(0).getDefaultYAxis().getScale()
				.setRange(0, 100);

		// chartViewPulse.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
		chartViewPulse.getAreas().get(0).getDefaultXAxis()
				.setValueType(ChartAxis.ValueType.Date);
		chartViewPulse.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));

		chartViewPulse.getAreas().get(0).getDefaultXAxis().setLabelAngle(15);

		// final ChartViewAdv chartViewSpo = new ChartViewAdv(this,
		// R.xml.chartspo);

		chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
				.setMargin(ChartAxisScale.MARGIN_NONE);
		chartViewPulse.getAreas().get(1).getDefaultYAxis()
				.setTitle(getString(R.string.SPO2_axis_title));
		chartViewPulse.getAreas().get(1).getDefaultYAxis().getScale()
				.setRange(0, 100);

		// chartViewSpo.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
		chartViewPulse.getAreas().get(1).getDefaultXAxis()
				.setValueType(ChartAxis.ValueType.Date);
		// chartViewPulse.setLayoutParams(new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));

		chartViewPulse.getAreas().get(1).getDefaultXAxis().setLabelAngle(15);

		LinearLayout chartLayout = (LinearLayout) findViewById(R.id.linearLayoutBloodOxStChartPulse);

		chartLayout.addView(chartViewPulse, 0);
		// chartLayout.addView(chartViewSpo,1);

		chartViewPulse.setPanning(ChartView.PANNING_BOTH);
		chartViewPulse.setHitTestEnabled(true);
		chartViewPulse.setOnTouchListener(pointClickListener);

		// chart may contains more that one area.
		chartViewPulse.enableZooming(chartViewPulse.getAreas().get(0), true,
				false);
		chartViewPulse.enableZooming(chartViewPulse.getAreas().get(1), true,
				false);

		// chartViewSpo.setPanning(ChartView.PANNING_BOTH);
		// chartViewSpo.setHitTestEnabled(true);
		// chartViewSpo.setOnTouchListener(pointClickListener);

		// chart may contains more that one area.
		// chartViewSpo.enableZooming(chartViewSpo.getAreas().get(0), true,
		// false);

		// set listener for hour button
		Button btnBldHour = (Button) findViewById(R.id.bldHour);
		btnBldHour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get time for now
				double m_now = System.currentTimeMillis();
				// TODO Auto-generated method stub
				LinearLayout chartLayout = (LinearLayout) findViewById(R.id.linearLayoutBloodOxStChartPulse);
				ChartViewAdv chartViewPuls = (ChartViewAdv) chartLayout
						.getChildAt(0);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setRange(m_now - 3600 * 1000, m_now);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setInterval(10.0, ChartAxisScale.IntervalType.Minutes);
				chartViewPulse.getAreas().get(0).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.resetZoom();

				// ChartViewAdv chartViewSpo =
				// (ChartViewAdv)chartLayout.getChildAt(1);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setRange(m_now - 3600 * 1000, m_now);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setInterval(10.0, ChartAxisScale.IntervalType.Minutes);
				chartViewPulse.getAreas().get(1).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.resetZoom();

			}

		});

		// set listener for day button
		Button btnBldDay = (Button) findViewById(R.id.bldDay);
		btnBldDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get time for now
				double m_now = System.currentTimeMillis();
				LinearLayout chartLayout = (LinearLayout) findViewById(R.id.linearLayoutBloodOxStChartPulse);
				ChartViewAdv chartViewPuls = (ChartViewAdv) chartLayout
						.getChildAt(0);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setRange(m_now - 24 * 3600 * 1000, m_now);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setInterval(6.0, ChartAxisScale.IntervalType.Hours);
				chartViewPulse.getAreas().get(0).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.resetZoom();

				// ChartViewAdv chartViewSpo =
				// (ChartViewAdv)chartLayout.getChildAt(1);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setRange(m_now - 24 * 3600 * 1000, m_now);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setInterval(6.0, ChartAxisScale.IntervalType.Hours);
				chartViewPulse.getAreas().get(1).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.resetZoom();

			}

		});

		// set listener for week button
		Button btnBldWeek = (Button) findViewById(R.id.bldWeek);
		btnBldWeek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get time for now
				double m_now = System.currentTimeMillis();
				LinearLayout chartLayout = (LinearLayout) findViewById(R.id.linearLayoutBloodOxStChartPulse);
				ChartViewAdv chartViewPuls = (ChartViewAdv) chartLayout
						.getChildAt(0);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setRange(m_now - 7 * 24 * 3600 * 1000, m_now);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setInterval(1.0, ChartAxisScale.IntervalType.Days);
				chartViewPulse.getAreas().get(0).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("dd HH:mm:ss"));
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.resetZoom();

				// ChartViewAdv chartViewSpo =
				// (ChartViewAdv)chartLayout.getChildAt(1);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setRange(m_now - 7 * 24 * 3600 * 1000, m_now);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setInterval(1.0, ChartAxisScale.IntervalType.Days);
				chartViewPulse.getAreas().get(1).getDefaultXAxis()
						.setLabelFormat(new SimpleDateFormat("dd HH:mm:ss"));
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.resetZoom();

			}

			// set gesture listener for two chart.

		});

		// set listener for week button
		Button btnBldMonth = (Button) findViewById(R.id.bldMonth);
		btnBldMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get time for now
				long m_now = System.currentTimeMillis();
				LinearLayout chartLayout = (LinearLayout) findViewById(R.id.linearLayoutBloodOxStChartPulse);
				ChartViewAdv chartViewPuls = (ChartViewAdv) chartLayout
						.getChildAt(0);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setRange(m_now - 30 * DateUtils.DAY_IN_MILLIS, m_now);
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.setInterval(6.0, ChartAxisScale.IntervalType.Days);
				chartViewPulse
						.getAreas()
						.get(0)
						.getDefaultXAxis()
						.setLabelFormat(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				chartViewPulse.getAreas().get(0).getDefaultXAxis().getScale()
						.resetZoom();

				// ChartViewAdv chartViewSpo =
				// (ChartViewAdv)chartLayout.getChildAt(1);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setRange(m_now - 30 * DateUtils.DAY_IN_MILLIS, m_now);
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.setInterval(6.0, ChartAxisScale.IntervalType.Days);
				chartViewPulse
						.getAreas()
						.get(1)
						.getDefaultXAxis()
						.setLabelFormat(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				chartViewPulse.getAreas().get(1).getDefaultXAxis().getScale()
						.resetZoom();

			}

		});

		/*
		 * chartViewPulse.getAreas().get(0).getDefaultXAxis()
		 * .setScaleChangeListener(new ChartAxis.ScaleChangeListener() {
		 * 
		 * 
		 * @Override public void onScaleChanging(ChartEngine chart, ChartAxis
		 * axis) { }
		 * 
		 * @Override public void onScaleChanged(ChartEngine chart, ChartAxis
		 * axis) {
		 * 
		 * ChartAxisScale xScale = chartViewPulse.getAreas().get(0)
		 * .getDefaultXAxis().getScale(); ChartAxisScale targetXScale =
		 * chartViewPulse.getAreas().get(1) .getDefaultXAxis().getScale();
		 * targetXScale.setRange(xScale.getVisibleMinimum(),
		 * xScale.getVisibleMaximum()); }
		 * 
		 * });
		 * 
		 * chartViewPulse.getAreas().get(1).getDefaultXAxis()
		 * .setScaleChangeListener(new ChartAxis.ScaleChangeListener() {
		 * 
		 * @Override public void onScaleChanging(ChartEngine chart, ChartAxis
		 * axis) { }
		 * 
		 * @Override public void onScaleChanged(ChartEngine chart, ChartAxis
		 * axis) {
		 * 
		 * ChartAxisScale xScale = chartViewPulse.getAreas().get(1)
		 * .getDefaultXAxis().getScale(); ChartAxisScale targetXScale =
		 * chartViewPulse.getAreas().get(0) .getDefaultXAxis().getScale();
		 * targetXScale.setRange(xScale.getVisibleMinimum(),
		 * xScale.getVisibleMaximum()); } });
		 */
	}

}
