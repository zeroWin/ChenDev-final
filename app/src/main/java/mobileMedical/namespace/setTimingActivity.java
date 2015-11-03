package mobileMedical.namespace;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class setTimingActivity extends Activity {

	boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);

	private TimePicker timePickerMorningFrom;
	private TimePicker timePickerMorningTo;
	private TimePicker timePickerNoonFrom;
	private TimePicker timePickerNoonTo;
	private TimePicker timePickerEveningFrom;
	private TimePicker timePickerEveningTo;
	private TimePicker timePickerNightFrom;
	private TimePicker timePickerNightTo;
	private TimePicker timePickerAnalysisFrom;
	private TimePicker timePickerAnalysisTo;
	private Calendar c;
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

	// #region Public methods
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_time);

		c = Calendar.getInstance();

		Cursor cursor = boDbHelper.getTimeSetting();
		if (!cursor.moveToNext()) {
			cursor.close();
			boDbHelper.initTimeSetting();
		}
		cursor = boDbHelper.getTimeSetting();
		cursor.moveToNext();
		morningFromHour = cursor.getInt(cursor
				.getColumnIndex("morningFromHour"));
		morningFromMin = cursor.getInt(cursor.getColumnIndex("morningFromMin"));
		morningToHour = cursor.getInt(cursor.getColumnIndex("morningToHour"));
		morningToMin = cursor.getInt(cursor.getColumnIndex("morningToMin"));

		noonFromHour = cursor.getInt(cursor.getColumnIndex("noonFromHour"));
		noonFromMin = cursor.getInt(cursor.getColumnIndex("noonFromMin"));
		noonToHour = cursor.getInt(cursor.getColumnIndex("noonToHour"));
		noonToMin = cursor.getInt(cursor.getColumnIndex("noonToMin"));

		eveningFromHour = cursor.getInt(cursor
				.getColumnIndex("eveningFromHour"));
		eveningFromMin = cursor.getInt(cursor.getColumnIndex("eveningFromMin"));
		eveningToHour = cursor.getInt(cursor.getColumnIndex("eveningToHour"));
		eveningToMin = cursor.getInt(cursor.getColumnIndex("eveningToMin"));

		nightFromHour = cursor.getInt(cursor.getColumnIndex("nightFromHour"));
		nightFromMin = cursor.getInt(cursor.getColumnIndex("nightFromMin"));
		nightToHour = cursor.getInt(cursor.getColumnIndex("nightToHour"));
		nightToMin = cursor.getInt(cursor.getColumnIndex("nightToMin"));

		analysisFromHour = cursor
				.getInt(cursor.getColumnIndex("analyFromHour"));
		analysisFromMin = cursor.getInt(cursor.getColumnIndex("analyFromMin"));
		analysisToHour = cursor.getInt(cursor.getColumnIndex("analyToHour"));
		analysisToMin = cursor.getInt(cursor.getColumnIndex("analyToMin"));

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		c.set(Calendar.HOUR_OF_DAY, morningFromHour);
		c.set(Calendar.MINUTE, morningFromMin);
		Date date = c.getTime();
		String time = df.format(date);

		TextView timeTextMorningFrom = (TextView) findViewById(R.id.textViewMorningFromValue);
		timeTextMorningFrom.setText(time);

		timeTextMorningFrom.setOnClickListener(new TextView.OnClickListener() {
			public void onClick(View view) {
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = morningFromHour;
				int minute = morningFromMin;
				new TimePickerDialog(setTimingActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0); // 设为 0
								c.set(Calendar.MILLISECOND, 0); // 设为 0
								SimpleDateFormat df = new SimpleDateFormat(
										"HH:mm");

								Date date = c.getTime();
								String time = df.format(date);
								TextView timeText = (TextView) findViewById(R.id.textViewMorningFromValue);
								timeText.setText(time);
								boDbHelper.setTime(1, hourOfDay, minute);
								morningFromHour = hourOfDay;
								morningFromMin = minute;
							}
						}, hour, minute, true).show();

			}
		});

		c.set(Calendar.HOUR_OF_DAY, morningToHour);
		c.set(Calendar.MINUTE, morningToMin);

		date = c.getTime();
		time = df.format(date);
		TextView timeTextMorningTo = (TextView) findViewById(R.id.textViewMorningToValue);
		timeTextMorningTo.setText(time);
		timeTextMorningTo.setOnClickListener(new TextView.OnClickListener() {
			public void onClick(View view) {
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = morningToHour;
				int minute = morningToMin;
				new TimePickerDialog(setTimingActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0); // 设为 0
								c.set(Calendar.MILLISECOND, 0); // 设为 0
								SimpleDateFormat df = new SimpleDateFormat(
										"HH:mm");

								Date date = c.getTime();
								String time = df.format(date);
								if (BuildConfig.DEBUG) {
									Log.i("time set ", time);
								}
								TextView timeText = (TextView) findViewById(R.id.textViewMorningToValue);
								timeText.setText(time);
								boDbHelper.setTime(2, hourOfDay, minute);
								morningToHour = hourOfDay;
								morningToMin = minute;
							}
						}, hour, minute, true).show();

			}
		});

		c.set(Calendar.HOUR_OF_DAY, noonFromHour);
		c.set(Calendar.MINUTE, noonFromMin);
		date = c.getTime();
		time = df.format(date);

		TextView timeTextNoonFrom = (TextView) findViewById(R.id.textViewNoonFromValue);
		timeTextNoonFrom.setText(time);

		timeTextNoonFrom.setOnClickListener(new TextView.OnClickListener() {
			public void onClick(View view) {
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = noonFromHour;
				int minute = noonFromMin;
				new TimePickerDialog(setTimingActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0); // 设为 0
								c.set(Calendar.MILLISECOND, 0); // 设为 0
								SimpleDateFormat df = new SimpleDateFormat(
										"HH:mm");

								Date date = c.getTime();
								String time = df.format(date);
								TextView timeText = (TextView) findViewById(R.id.textViewNoonFromValue);
								timeText.setText(time);
								boDbHelper.setTime(3, hourOfDay, minute);
								noonFromHour = hourOfDay;
								noonFromMin = minute;
							}
						}, hour, minute, true).show();

			}
		});

		c.set(Calendar.HOUR_OF_DAY, noonToHour);
		c.set(Calendar.MINUTE, noonToMin);
		date = c.getTime();
		time = df.format(date);

		TextView timeTextNoonTo = (TextView) findViewById(R.id.textViewNoonToValue);
		timeTextNoonTo.setText(time);

		timeTextNoonTo.setOnClickListener(new TextView.OnClickListener() {
			public void onClick(View view) {
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = noonToHour;
				int minute = noonToMin;
				new TimePickerDialog(setTimingActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0); // 设为 0
								c.set(Calendar.MILLISECOND, 0); // 设为 0
								SimpleDateFormat df = new SimpleDateFormat(
										"HH:mm");

								Date date = c.getTime();
								String time = df.format(date);
								TextView timeText = (TextView) findViewById(R.id.textViewNoonToValue);
								timeText.setText(time);
								boDbHelper.setTime(4, hourOfDay, minute);
								noonToHour = hourOfDay;
								noonToMin = minute;
							}
						}, hour, minute, true).show();

			}
		});

		c.set(Calendar.HOUR_OF_DAY, eveningFromHour);
		c.set(Calendar.MINUTE, eveningFromMin);
		date = c.getTime();
		time = df.format(date);

		TextView timeTextEveningFrom = (TextView) findViewById(R.id.textViewEveningFromValue);
		timeTextEveningFrom.setText(time);

		timeTextEveningFrom.setOnClickListener(new TextView.OnClickListener() {
			public void onClick(View view) {
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = eveningFromHour;
				int minute = eveningFromMin;
				new TimePickerDialog(setTimingActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0); // 设为 0
								c.set(Calendar.MILLISECOND, 0); // 设为 0
								SimpleDateFormat df = new SimpleDateFormat(
										"HH:mm");

								Date date = c.getTime();
								String time = df.format(date);
								TextView timeText = (TextView) findViewById(R.id.textViewEveningFromValue);
								timeText.setText(time);
								boDbHelper.setTime(5, hourOfDay, minute);
								eveningFromHour = hourOfDay;
								eveningFromMin = minute;
							}
						}, hour, minute, true).show();

			}
		});

		c.set(Calendar.HOUR_OF_DAY, eveningToHour);
		c.set(Calendar.MINUTE, eveningToMin);
		date = c.getTime();
		time = df.format(date);

		TextView timeTextEveningTo = (TextView) findViewById(R.id.textViewEveningToValue);
		timeTextEveningTo.setText(time);

		timeTextEveningTo.setOnClickListener(new TextView.OnClickListener() {
			public void onClick(View view) {
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = eveningToHour;
				int minute = eveningToMin;
				new TimePickerDialog(setTimingActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0); // 设为 0
								c.set(Calendar.MILLISECOND, 0); // 设为 0
								SimpleDateFormat df = new SimpleDateFormat(
										"HH:mm");

								Date date = c.getTime();
								String time = df.format(date);
								TextView timeText = (TextView) findViewById(R.id.textViewEveningToValue);
								timeText.setText(time);
								boDbHelper.setTime(6, hourOfDay, minute);
								eveningToHour = hourOfDay;
								eveningToMin = minute;
							}
						}, hour, minute, true).show();

			}
		});

		c.set(Calendar.HOUR_OF_DAY, nightFromHour);
		c.set(Calendar.MINUTE, nightFromMin);
		date = c.getTime();
		time = df.format(date);

		TextView timeTextNightFrom = (TextView) findViewById(R.id.textViewNightFromValue);
		timeTextNightFrom.setText(time);

		timeTextNightFrom.setOnClickListener(new TextView.OnClickListener() {
			public void onClick(View view) {
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = nightFromHour;
				int minute = nightFromMin;
				new TimePickerDialog(setTimingActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0); // 设为 0
								c.set(Calendar.MILLISECOND, 0); // 设为 0
								SimpleDateFormat df = new SimpleDateFormat(
										"HH:mm");

								Date date = c.getTime();
								String time = df.format(date);
								TextView timeText = (TextView) findViewById(R.id.textViewNightFromValue);
								timeText.setText(time);
								boDbHelper.setTime(7, hourOfDay, minute);
								nightFromHour = hourOfDay;
								nightFromMin = minute;
							}
						}, hour, minute, true).show();

			}
		});

		c.set(Calendar.HOUR_OF_DAY, nightToHour);
		c.set(Calendar.MINUTE, nightToMin);
		date = c.getTime();
		time = df.format(date);

		TextView timeTextNightTo = (TextView) findViewById(R.id.textViewNightToValue);
		timeTextNightTo.setText(time);

		timeTextNightTo.setOnClickListener(new TextView.OnClickListener() {
			public void onClick(View view) {
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = nightToHour;
				int minute = nightToMin;
				new TimePickerDialog(setTimingActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0); // 设为 0
								c.set(Calendar.MILLISECOND, 0); // 设为 0
								SimpleDateFormat df = new SimpleDateFormat(
										"HH:mm");

								Date date = c.getTime();
								String time = df.format(date);
								TextView timeText = (TextView) findViewById(R.id.textViewNightToValue);
								timeText.setText(time);
								boDbHelper.setTime(8, hourOfDay, minute);
								nightToHour = hourOfDay;
								nightToMin = minute;
							}
						}, hour, minute, true).show();

			}
		});

		c.set(Calendar.HOUR_OF_DAY, analysisFromHour);
		c.set(Calendar.MINUTE, analysisFromMin);
		date = c.getTime();
		time = df.format(date);

		TextView timeTextAnalysisTimeFrom = (TextView) findViewById(R.id.textViewAnalysisTimeFromValue);
		timeTextAnalysisTimeFrom.setText(time);

		timeTextAnalysisTimeFrom
				.setOnClickListener(new TextView.OnClickListener() {
					public void onClick(View view) {
						c.setTimeInMillis(System.currentTimeMillis());
						int hour = analysisFromHour;
						int minute = analysisFromMin;
						new TimePickerDialog(setTimingActivity.this,
								new TimePickerDialog.OnTimeSetListener() {

									@Override
									public void onTimeSet(TimePicker view,
											int hourOfDay, int minute) {
										c.setTimeInMillis(System
												.currentTimeMillis());
										c.set(Calendar.HOUR_OF_DAY, hourOfDay);
										c.set(Calendar.MINUTE, minute);
										c.set(Calendar.SECOND, 0); // 设为 0
										c.set(Calendar.MILLISECOND, 0); // 设为 0
										SimpleDateFormat df = new SimpleDateFormat(
												"HH:mm");

										Date date = c.getTime();
										String time = df.format(date);
										TextView timeText = (TextView) findViewById(R.id.textViewAnalysisTimeFromValue);
										timeText.setText(time);
										boDbHelper
												.setTime(9, hourOfDay, minute);
										analysisFromHour = hourOfDay;
										analysisFromMin = minute;
									}
								}, hour, minute, true).show();

					}
				});

		c.set(Calendar.HOUR_OF_DAY, analysisToHour);
		c.set(Calendar.MINUTE, analysisToMin);
		date = c.getTime();
		time = df.format(date);

		TextView timeTextAnalysisTimeTo = (TextView) findViewById(R.id.textViewAnalysisTimeToValue);
		timeTextAnalysisTimeTo.setText(time);

		timeTextAnalysisTimeTo
				.setOnClickListener(new TextView.OnClickListener() {
					public void onClick(View view) {
						c.setTimeInMillis(System.currentTimeMillis());
						int hour = analysisToHour;
						int minute = analysisToMin;
						new TimePickerDialog(setTimingActivity.this,
								new TimePickerDialog.OnTimeSetListener() {

									@Override
									public void onTimeSet(TimePicker view,
											int hourOfDay, int minute) {
										c.setTimeInMillis(System
												.currentTimeMillis());
										c.set(Calendar.HOUR_OF_DAY, hourOfDay);
										c.set(Calendar.MINUTE, minute);
										c.set(Calendar.SECOND, 0); // 设为 0
										c.set(Calendar.MILLISECOND, 0); // 设为 0
										SimpleDateFormat df = new SimpleDateFormat(
												"HH:mm");

										Date date = c.getTime();
										String time = df.format(date);
										TextView timeText = (TextView) findViewById(R.id.textViewAnalysisTimeToValue);
										timeText.setText(time);
										boDbHelper.setTime(10, hourOfDay,
												minute);
										analysisToHour = hourOfDay;
										analysisToMin = minute;
									}
								}, hour, minute, true).show();

					}
				});

	}

}
