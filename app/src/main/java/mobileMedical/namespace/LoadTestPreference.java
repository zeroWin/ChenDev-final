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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import devDataType.Parameters.IntParameter;
import mobileMedical.FileManage.MeasDataFilesOperator;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.ParameterDataKeys;

/**
 * BluetoothDevicePreference is the preference type used to display each remote
 * Bluetooth device in the Bluetooth Settings screen.
 */
public final class LoadTestPreference extends Preference implements
		OnClickListener {
	private static final String TAG = "LoadTestPreference";

	private static int sDimAlpha = Integer.MIN_VALUE;
	private Button readButton;
	private TextView finishPercent;
	private CheckBox readFromSD;
	private ProgressBar progressBar;
	private final int updateProgess = 0x11;
	private final int updateProgessError = 0x12;
	private static int progress = 0;
	private static boolean readFromSDchecked = false;
	private boolean threadFlag = true;
	updateProgessThread mUpdateProgessThread;

	private Context contextThis;
	private static View mView = null;

	private OnClickListener mOnSettingsClickListener;

	private String mDeviceName;
	private int mDeviceID;

	public LoadTestPreference(Context context) {
		super(context);

		contextThis = context;
		setWidgetLayoutResource(R.layout.setting_others);

	}

	public void resetProgress() {
		progress = 0;
		notifyChanged();
	}

	@Override
	protected void onBindView(View view) {

		readButton = (Button) view.findViewById(R.id.buttonReadMeasData);
		progressBar = (ProgressBar) view
				.findViewById(R.id.progressBarReadMeasData);

		readButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				readButton.setEnabled(false);
				threadFlag = true;
				mUpdateProgessThread = new updateProgessThread();
				mUpdateProgessThread.start();

				/*
				 * for (int i= 0; i<100;i++) {
				 * 
				 * progressBar.setProgress(i); try { Thread.sleep(200); } catch
				 * (InterruptedException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } } Message m = new Message(); m.what =
				 * updateProgess;
				 * 
				 * m.arg2 = 80; //mHandler.sendMessage(m);
				 * //mHandler.obtainMessage().sendToTarget();
				 * mHandler.obtainMessage(m.what, 0, m.arg2).sendToTarget();
				 */
			}
		});
		readFromSD = (CheckBox) view.findViewById(R.id.checkBoxFromSDCard);
		if (readFromSD != null) {
			readFromSD
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								readFromSDchecked = true;
							} else {
								readFromSDchecked = false;
							}
						}
					});

		}
		// progressBar.setProgress(progress);
		readFromSD.setChecked(readFromSDchecked);
		// progressBar.setProgress(100);
		/*
		 * Message m = new Message(); m.what = updateProgess; progress =
		 * progress + 40; m.arg2 = progress; //mHandler.sendMessage(m);
		 * //mHandler.obtainMessage().sendToTarget();
		 * mHandler.obtainMessage(m.what, 0, m.arg2).sendToTarget();
		 */
		mView = view;
		super.onBindView(view);
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case updateProgess:
				progressBar = (ProgressBar) mView
						.findViewById(R.id.progressBarReadMeasData);
				readButton = (Button) mView
						.findViewById(R.id.buttonReadMeasData);
				finishPercent = (TextView) mView
						.findViewById(R.id.progressBarReadMeasDataPercent);
				progressBar.setProgress(msg.arg2);
				finishPercent.setText(progress + " %");
				if (progress == 100) {
					readButton.setEnabled(true);
				} else {
					readButton.setEnabled(false);
				}
				// notifyChanged();
				break;
			case updateProgessError:
				Toast.makeText(contextThis, (String) msg.obj, Toast.LENGTH_LONG)
						.show();
				readButton.setEnabled(true);
				break;
			}

		}
	};

	public class updateProgessThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			boDb boDbHelper = new boDb(contextThis, "bloodox.db", null, 1);
			while (threadFlag) {
				readButton.setEnabled(false);
				boolean readFromSDCard = false;
				MeasDataFilesOperator measdFilesOperator = new MeasDataFilesOperator(
						contextThis);
				if (readFromSDchecked) {
					readFromSDCard = true;
					if (!measdFilesOperator.HasSDCard()) {

						Message m = new Message();
						m.what = updateProgessError;
						m.obj = contextThis.getString(R.string.hint_no_sd_card);
						mHandler.sendMessage(m);
						// mHandler.obtainMessage(m.what, m.obj).sendToTarget();
						threadFlag = false;
						break;
					}
				} else {
					readFromSDCard = false;
				}
				String[] measDataStrings = null;
				String[] measDataInfoStrings = null;
				try {
					measDataStrings = measdFilesOperator.ReadMeasDataFromFile(
							"measdata.txt", readFromSDCard, 1);
					measDataInfoStrings = measdFilesOperator
							.ReadMeasDataFromFile("measdatainfo.txt",
									readFromSDCard, 1);
				} catch (Exception e) {
					// TODO: handle exception
				}

				if ((measDataStrings != null || measDataInfoStrings != null)
						&& checkIdentity(measDataInfoStrings)) {

					for (int i = 0; i < measDataInfoStrings.length; i++) {
						// we can also use the RegularExpressions
						if (measDataInfoStrings[i].split(",").length == 8) {
							int transId = Integer
									.valueOf(measDataInfoStrings[i].split(",")[0]);
							int sensorType = Integer
									.valueOf(measDataInfoStrings[i].split(",")[1]);
							int measItem = Integer
									.valueOf(measDataInfoStrings[i].split(",")[2]);
							int doctorID = Integer
									.valueOf(measDataInfoStrings[i].split(",")[3]);
							int patientID = Integer
									.valueOf(measDataInfoStrings[i].split(",")[4]);
							String timestamp = measDataInfoStrings[i]
									.split(",")[5];
							timestamp = MemberManage
									.turnCNTimeToDBTime(timestamp);
							int beginLine = Integer
									.valueOf(measDataInfoStrings[i].split(",")[6]);
							int lineNum = Integer
									.valueOf(measDataInfoStrings[i].split(",")[7]);
							String measResults = "";
							int measResultsIdx = 0;

							int count = lineNum / 26;
							if (count == 0) {
								count = 1;
							}
							for (int k = 0; k < count; k++) {
								measResults = "";
								for (int j = beginLine + k * 26; j < beginLine
										+ k * 26 + lineNum / count; j++) {
									measResults += measDataStrings[j - 1] + " ";
								}
								measResults = measResults.trim();
								measResultsIdx = k;
								boDbHelper.insertOneItem(transId, sensorType,
										measItem, doctorID, patientID,
										timestamp, measResults, measResultsIdx);
							}
							((IntParameter) MessageData.parmsDataHashMap
									.get(ParameterDataKeys.TRANSID))
									.SetValue(transId);
							// try {
							// sleep(1000);
							// } catch (InterruptedException e) {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }
						} else {

							Message m = new Message();
							m.what = updateProgessError;
							m.obj = Integer.toString(i)
									+ " line is not correct";
							mHandler.sendMessage(m);
							// mHandler.obtainMessage(m.what,
							// m.obj).sendToTarget();
							threadFlag = false;
							break;

						}
						progress = (int) (((float) (i + 1) / measDataInfoStrings.length) * 100);
						if (progress == 100) {
							threadFlag = false;
						}
						Message m = new Message();
						m.what = updateProgess;
						m.arg2 = progress;
						// mHandler.sendMessage(m);
						mHandler.obtainMessage(m.what, 0, m.arg2)
								.sendToTarget();

						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} else {
					Message m = new Message();
					m.what = updateProgessError;
					m.obj = contextThis.getString(R.string.hint_already_in_db);
					mHandler.sendMessage(m);
					// mHandler.obtainMessage(m.what, m.obj).sendToTarget();
					threadFlag = false;
					break;
				}
			}
		}
	}

	private boolean checkIdentity(String[] info) {
		// TODO Auto-generated method stub
		for (int i = 0; i < info.length / 10 + 1; i++) {
			int randomNum = new Random().nextInt(info.length);// 产生的是0-9的随机数
			String timestamp = info[randomNum].split(",")[5];
			timestamp = MemberManage.turnCNTimeToDBTime(timestamp);
			boDb boDbHelper = new boDb(getContext(), "bloodox.db", null, 1);
			int count = boDbHelper.getCountByTime(timestamp);
			boDbHelper.close();
			if (count > 0) {
				return false;
			}
		}
		return true;
	}

	public void onDestroy() {

		threadFlag = false;
	}

	public void onClick(View v) {
		// Should never be null by construction
		if (mOnSettingsClickListener != null) {
			mOnSettingsClickListener.onClick(v);
		} else {
			Bundle arg = new Bundle();
			arg.putInt("DEVICE_ID", mDeviceID);
		}
	}

	void onClicked() {

		/*
		 * if (mCachedDevice.isConnected()) { askDisconnect(); } else if
		 * (bondState == BluetoothDevice.BOND_BONDED) {
		 * mCachedDevice.connect(true); } else if (bondState ==
		 * BluetoothDevice.BOND_NONE) { pair(); }
		 */
	}

	// Show disconnect confirmation dialog for a device.

}
