package mobileMedical.namespace;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import devDataType.Parameters.FloatParameter;
import devDataType.Parameters.IntParameter;
import devDataType.Parameters.ShortParameter;
import devDataType.Parameters.StringParameter;
import mobileMedical.Message.AlarmTimeConfigMessage;
import mobileMedical.Message.BodyTempSensorCalCoefConfigMessage;
import mobileMedical.Message.BodyTempSensorCalMessage;
import mobileMedical.Message.BodyTempSensorCalRetMessage;
import mobileMedical.Message.GWStateQueryMessage;
import mobileMedical.Message.GWStateRetMessage;
import mobileMedical.Message.InMessage;
import mobileMedical.Message.MeasStopMessage;
import mobileMedical.Message.MessageBase;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.MessageInfo;
import mobileMedical.Message.OutMessage;
import mobileMedical.Message.ParameterDataKeys;
import mobileMedical.Message.SNConfigMessage;
import mobileMedical.Message.SingleSensorMeasMessage;
import mobileMedical.Message.SingleSensorMeasRetMessage;

public class MessageProcess {

	// Receive Message Process
	public enum ReceiveMsgProcessState {
		INIT_STATE(0), FIND_MSG_HEADER_STATE(1), FIND_MSG_BUFFER_SIZE_STATE(2), FIND_MSG_MSG_TYPE_STATE(
				3), GET_PARMS_RESULTS_STATE(4), FIND_MSG_END_STATE(5), SEDN_RESPONSE_MSG_STATE(
				6), PROCESS_RESULTS_STATE(7), SEND_ERROR_MSG_STATE(8), SEND_RESULTS_STATE(
				9), FINISHED_MEAS(10);

		private int nCode;

		private ReceiveMsgProcessState(int _nCode) {
			this.nCode = _nCode;
		}

		public int Value() {
			return this.nCode;
		}
	}

	public static int TransID_Parm = 0;

	public static final int SINGLE_MEAS_COMM = 0x6D;
	public static final int BATCH_MEAS_COMM = 0x6E;

	public static final int MSG_HEADER_AND_END_SIZE = 0x0C; // size in byte
	public static final int SINGLE_MEAS_COMMAND_SIZE = 0x38; // size in byte
	public static final int BATCH_MEAS_MSG_HEADER_SIZE = 0x18; // size in byte

	public static final int SENSOR_TYPE_NUM = 8;

	public static final int SEND_MSG_BUFFER_SIZE = 1024; // size in byte
	public static final int RECEIVED_MSG_BUFFER_SIZE = 2048; // size in byte
	public static final int RESULT_BUFFER_SIZE = 1024; // size in float

	public static final int BYTE_OFFSET = 3;// due to received bytes is inverted

	// Error Message Define
	private static final String TAG = "MessageProcess";

	public enum MeasError {
		NO_ERROR(0x00), ERROR_HEDAER_NOT_FOUND(0x01), ERROR_BUFFERSIZE_PARM_NOT_FOUND(
				0x02), ERROR_BUFFERSIZE_NOT_CORRECT(0x04), ERROR_END_NOT_FOUND(
				0x08), ERROR_SENSOR_TYPE_ERROR(0x10), ERROR_RETS_ERROR(0x12);

		private int nErrorCode;

		private MeasError(int _nErrorCode) {

			this.nErrorCode = _nErrorCode;
		}

		public int Value() {
			return this.nErrorCode;
		}

		@Override
		public String toString() {

			return String.valueOf(this.nErrorCode);
		}

		public static MeasError valueOf(int value) {

			switch (value) {
			case 0x00:
				return NO_ERROR;
			case 0x01:
				return ERROR_HEDAER_NOT_FOUND;
			case 0x02:
				return ERROR_BUFFERSIZE_PARM_NOT_FOUND;
			case 0x04:
				return ERROR_BUFFERSIZE_NOT_CORRECT;
			case 0x08:
				return ERROR_END_NOT_FOUND;
			case 0x10:
				return ERROR_SENSOR_TYPE_ERROR;
			case 0x12:
				return ERROR_RETS_ERROR;
			default:
				return null;
			}
		}

	}

	private byte[] mSendMsgBuffer;
	private int mSendMsgSize = -1;

	private static byte[] mReceiveBuffer = new byte[RECEIVED_MSG_BUFFER_SIZE];
	private static int mReceiveBufferSize = -1;

	private static byte[] mReceiveRetsBuffer = new byte[RECEIVED_MSG_BUFFER_SIZE];
	private static int mReceiveRetsBufferSize = 0;
	private static int mRemainReceiveRetsBufferSize = -1;

	/*
	 * private static float[] mResultsBuffer = new float[RESULT_BUFFER_SIZE];
	 * private static int mResultsLen = -1;
	 */

	private Object mResults;

	private static ReceiveMsgProcessState mReceiveMsgProcessState = ReceiveMsgProcessState.INIT_STATE;

	private static final int SHORT_PARM_SIZE = 2; // int float parameter
	private static final int INT_FLOAT_PARM_SIZE = 4; // int float parameter
	private static final int STRING_PARM_SIZE = 8; // Byte[8] parameter

	private static byte[] mReceiveMsgParm;
	private static int mReceiveMsgParmSize;

	private static byte[] mReceiveMsgHeaderParm = new byte[SHORT_PARM_SIZE];
	private static byte[] mReceiveMsgBufferSizeParm = new byte[SHORT_PARM_SIZE];
	private static byte[] mReceiveMsgMsgTypeParm = new byte[INT_FLOAT_PARM_SIZE];
	private static byte[] mReceiveMsgEndParm = new byte[SHORT_PARM_SIZE];

	private static short mReceiveMsgHeader = -1;
	private static short mReceiveMsgBufferSize = -1;
	private static int mReceiveMsgMsgType = -1;
	private static short mReceiveMsgEnd = -1;
	private static int mReceiveMsgSensorType = -1;

	private InMessage mInMsg;
	private OutMessage mOutMsg;

	private static int mReceiveMsgRetsLen;
	private static boolean mFindReceiveMsgParmProcessOne = false;

	private static int mReceiveMsgPreParmEndIdx = -1;
	private static int mReadReceiveMsgParmIdx = -1;
	private static int mReceiveMsgParmIdx = -1;

	public static boolean ReceiveMsgProcessed = true;
	public static boolean MeasResultsReady = false;
	public static boolean MeasFinised = false;

	private int mSelectedMeasSensorBitMap;
	private int mSelectedBatch_MeasNum_Parm;

	private static int mMeasSensorNum;

	private static boolean mStopMeas = false;
	private static boolean mOnceMeas = false;
	private static boolean mSensorInfoMessage = false;
	private static MeasError mMeasError = MeasError.NO_ERROR;

	// Results Error got form results message
	private static boolean mRetsError = false;
	private MessageParserThread mMessageParserThread = null;
	private static final Object mObjcect = new Object();
	private Handler mHandler;
	private Context mContext;

	/**
	 * 获取context和handler，令TransID_Parm=TRANSID的值
	 * @param context
	 * @param handler
	 */
	public MessageProcess(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		TransID_Parm = ((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).GetValue();
	}

	//只看到这里-------------------------------------------------------------------------------------------------------------

	// Setter and Getter
	public int GetSendMsgSize() {
		return mSendMsgSize;
	}

	public byte[] GetSendMsgBuffer() {
		return mSendMsgBuffer;
	}

	/*
	 * public int GetResultsLen() { return mResultsLen; }
	 * 
	 * public float[] GetResultsBuffer() { return mResultsBuffer; }
	 */
	public void ContinueMesssageProcessing() {
		if (mMessageParserThread != null) {
			synchronized (mMessageParserThread) {
				mMessageParserThread.notify();
			}
		}
	}

	/**
	 *
	 * @param srcBuffer
	 * @param size
	 */
	public void CopyReceviedMessageBuffer(byte[] srcBuffer, int size) {
		if (mMessageParserThread == null) {

			// Set the flag to stop get the data form bluetooth ouputstream
			// to avoid the overwrite the ReceiveMessageBuffer
			ReceiveMsgProcessed = false;

			System.arraycopy(srcBuffer, 0, mReceiveBuffer, 0, size);
			mReceiveBufferSize = size;

			// mMessageParserThread.notify();

			mMessageParserThread = new MessageParserThread();
			mMessageParserThread.start();

		} else {

			synchronized (mMessageParserThread) {

				// Set the flag to stop get the data form bluetooth ouputstream
				// to avoid the overwrite the ReceiveMessageBuffer
				ReceiveMsgProcessed = false;

				System.arraycopy(srcBuffer, 0, mReceiveBuffer, 0, size);
				mReceiveBufferSize = size;
				mMessageParserThread.notify();
			}
		}
	}

	/**
	 * 根据measID创建命令信息
	 * @param measID
	 */
	public void CreateMeasureCommandMessage(int measID) {
		// Set common parameters value
		TransID_Parm = ((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).GetValue();

		TransID_Parm += 1;

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGHEADER))		//头1
				.SetValue(MessageInfo.MSG_HEADER_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGEND))			//尾2
				.SetValue(MessageInfo.MSG_TAIL_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGBUFFERSIZE))	//大小3
				.SetValue(MessageInfo.SINGLE_MEAS_REQ_PARMS_SIZE);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGTYPE))		//类型4
				.SetValue(MessageInfo.MSGTYPE_ST_MEAS_START_REQ);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).SetValue(TransID_Parm);	//transID5
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORNUM)).SetValue(1);			//传感器代号6
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(1);		//RESERVEDPARM1   7

		((FloatParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TIMEOUT)).SetValue(2);		//timeout  8
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM2)).SetValue(0);		//RESERVEDPARM2  9

		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.DOCTORID))
				.SetValue(MemberManage.doctorID);			//医生id  10
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.PATIENTID))
				.SetValue(MemberManage.patientID);			//病人id  11
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM3)).SetValue(0);		//RESERVEDPARM3  12

		switch (measID) {
		case ConstDef.BodyTempTabIndex: // BodyTemp
			mOnceMeas = true;
			if (BodyTmpActivity.baseTempBool) {
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORTYPE))
						.SetValue(MessageInfo.SENSORTYPE_THERMOMETER);		//传感器类型为体温 13
				((StringParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");		//传感器id为THERMOME 14
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASMODE))
						.SetValue(MessageInfo.SINGLE_MEAS_MODE);		//测量类型 15
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);		//meas-item-num为1  16
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEM))
						.SetValue(MessageInfo.MM_MI_BODY_BASE_TEMPERATURE);		//measitem设定为4096（基本体温的代号）  17
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);			//Measure Count设为1  18
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);		//测量间隔为5  19
			} else {
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORTYPE))
						.SetValue(MessageInfo.SENSORTYPE_THERMOMETER);
				((StringParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASMODE))
						.SetValue(MessageInfo.SINGLE_MEAS_MODE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEM))
						.SetValue(MessageInfo.MM_MI_BODY_TEMPERATURE);			//measitem设定为64（体温的代号）
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
			}

			break;
		case ConstDef.BloodPresureTabIndex: // BloodPressure
			mOnceMeas = true;

			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORTYPE))
					.SetValue(MessageInfo.SENSORTYPE_BLOODPRESSUREMETER);
			((StringParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASMODE))
					.SetValue(MessageInfo.SINGLE_MEAS_MODE);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEM))
					.SetValue(MessageInfo.MM_MI_BLOOD_PRESSURE);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
			break;
		case ConstDef.ElectrocardiogramTabIndex: // Electrocardiogram
			mOnceMeas = false;
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORTYPE))
					.SetValue(MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER);
			((StringParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORID)).SetValue("ECGTROCA");
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASMODE))
					.SetValue(MessageInfo.SINGLE_MEAS_MODE);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEM))
					.SetValue(MessageInfo.MM_MI_ELECTRO_CARDIOGRAM);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
			break;
		case ConstDef.BloodOxygenTabIndex: // BloodOxygen
			mOnceMeas = false;
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORTYPE))
					.SetValue(MessageInfo.SENSORTYPE_BLOODOXYGENMETER);
			((StringParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORID)).SetValue("BLOODOXY");
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASMODE))
					.SetValue(MessageInfo.SINGLE_MEAS_MODE);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEM))
					.SetValue(MessageInfo.MM_MI_BLOOD_OXYGEN);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);

			break;
		case ConstDef.BloodSugarTabIndex: // BloodSugar
			mOnceMeas = true;
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORTYPE))
					.SetValue(MessageInfo.SENSORTYPE_BLOODSUGARMETER);
			((StringParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASMODE))
					.SetValue(MessageInfo.SINGLE_MEAS_MODE);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEM))
					.SetValue(MessageInfo.MM_MI_BLOOD_SUGAR);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
			break;

		case ConstDef.StethoscopeTabIndex: // Stethoscope
			mOnceMeas = true;

			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORTYPE))
					.SetValue(MessageInfo.SENSORTYPE_STETHOSCOPE);
			((StringParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASMODE))
					.SetValue(MessageInfo.SINGLE_MEAS_MODE);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEM))
					.SetValue(MessageInfo.MM_MI_HEART_RATE);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
			break;
		case ConstDef.PulVentTabIndex: // pulmonaryventilation
			mOnceMeas = false;

			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORTYPE))
					.SetValue(MessageInfo.SENSORTYPE_PULMONARYVENTILATION);
			((StringParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.SENSORID)).SetValue("PULVENT");
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASMODE))
					.SetValue(MessageInfo.SINGLE_MEAS_MODE);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASITEM))
					.SetValue(MessageInfo.MM_MI_PULMONARY_VENTILATION);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
			((IntParameter) MessageData.parmsDataHashMap
					.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
			break;
		case 7: // Complex
			/*
			 * mOnceMeas = false; Meas_CommandName_Parm = BATCH_MEAS_COMM;
			 * Meas_CommandID_Parm += 1; Batch_MeasNum_Parm =
			 * mSelectedBatch_MeasNum_Parm; Meas_MessageBufferSize_Parm =
			 * BATCH_MEAS_MESSAGE_HEADER_SIZE + (SINGLE_MEAS_COMMAND_SIZE *
			 * Batch_MeasNum_Parm); if (Batch_MeasNum_Parm > 0) {
			 * CreateBatchMeasureCommandMessage(mSendMessBuffer, 0); }
			 */
			break;
		}

		mOutMsg = new SingleSensorMeasMessage();
		mSendMsgBuffer = mOutMsg.CreaterMessage();
		mSendMsgSize = mSendMsgBuffer.length;

	}


	public void CreateSyncCommandMessage(int measID){
		// Set common parameters value
		TransID_Parm = ((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).GetValue();

		TransID_Parm += 1;

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGHEADER))		//头1
				.SetValue(MessageInfo.MSG_HEADER_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGEND))			//尾2
				.SetValue(MessageInfo.MSG_TAIL_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGBUFFERSIZE))	//大小3
				.SetValue(MessageInfo.SINGLE_MEAS_REQ_PARMS_SIZE);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGTYPE))		//类型4
				.SetValue(MessageInfo.MSGTYPE_ST_MEAS_SYNC_REQ);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).SetValue(TransID_Parm);	//transID5
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORNUM)).SetValue(1);			//传感器代号6
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(1);		//RESERVEDPARM1   7

		((FloatParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TIMEOUT)).SetValue(2);		//timeout  8
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM2)).SetValue(0);		//RESERVEDPARM2  9

		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.DOCTORID))
				.SetValue(MemberManage.doctorID);			//医生id  10
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.PATIENTID))
				.SetValue(MemberManage.patientID);			//病人id  11
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM3)).SetValue(0);		//RESERVEDPARM3  12

		switch (measID) {
			case ConstDef.BodyTempTabIndex: // BodyTemp
				mOnceMeas = true;
				if (BodyTmpActivity.baseTempBool) {
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.SENSORTYPE))
							.SetValue(MessageInfo.SENSORTYPE_THERMOMETER);		//传感器类型为体温 13
					((StringParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");		//传感器id为THERMOME 14
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASMODE))
							.SetValue(MessageInfo.SINGLE_MEAS_MODE);		//测量类型 15
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);		//meas-item-num为1  16
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASITEM))
							.SetValue(MessageInfo.MM_MI_BODY_BASE_TEMPERATURE);		//measitem设定为4096（基本体温的代号）  17
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);			//Measure Count设为1  18
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);		//测量间隔为5  19
				} else {
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.SENSORTYPE))
							.SetValue(MessageInfo.SENSORTYPE_THERMOMETER);
					((StringParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASMODE))
							.SetValue(MessageInfo.SINGLE_MEAS_MODE);
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASITEM))
							.SetValue(MessageInfo.MM_MI_BODY_TEMPERATURE);			//measitem设定为64（体温的代号）
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
					((IntParameter) MessageData.parmsDataHashMap
							.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
				}

				break;
			case ConstDef.BloodPresureTabIndex: // BloodPressure
				mOnceMeas = true;

				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORTYPE))
						.SetValue(MessageInfo.SENSORTYPE_BLOODPRESSUREMETER);
				((StringParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASMODE))
						.SetValue(MessageInfo.SINGLE_MEAS_MODE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEM))
						.SetValue(MessageInfo.MM_MI_BLOOD_PRESSURE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
				break;
			case ConstDef.ElectrocardiogramTabIndex: // Electrocardiogram
				mOnceMeas = false;
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORTYPE))
						.SetValue(MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER);
				((StringParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORID)).SetValue("ECGTROCA");
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASMODE))
						.SetValue(MessageInfo.SINGLE_MEAS_MODE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEM))
						.SetValue(MessageInfo.MM_MI_ELECTRO_CARDIOGRAM);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
				break;
			case ConstDef.BloodOxygenTabIndex: // BloodOxygen
				mOnceMeas = false;
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORTYPE))
						.SetValue(MessageInfo.SENSORTYPE_BLOODOXYGENMETER);
				((StringParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORID)).SetValue("BLOODOXY");
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASMODE))
						.SetValue(MessageInfo.SINGLE_MEAS_MODE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEM))
						.SetValue(MessageInfo.MM_MI_BLOOD_OXYGEN);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);

				break;
			case ConstDef.BloodSugarTabIndex: // BloodSugar
				mOnceMeas = true;
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORTYPE))
						.SetValue(MessageInfo.SENSORTYPE_BLOODSUGARMETER);
				((StringParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASMODE))
						.SetValue(MessageInfo.SINGLE_MEAS_MODE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEM))
						.SetValue(MessageInfo.MM_MI_BLOOD_SUGAR);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
				break;

			case ConstDef.StethoscopeTabIndex: // Stethoscope
				mOnceMeas = true;

				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORTYPE))
						.SetValue(MessageInfo.SENSORTYPE_STETHOSCOPE);
				((StringParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASMODE))
						.SetValue(MessageInfo.SINGLE_MEAS_MODE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEM))
						.SetValue(MessageInfo.MM_MI_HEART_RATE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
				break;
			case ConstDef.PulVentTabIndex: // pulmonaryventilation
				mOnceMeas = false;

				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORTYPE))
						.SetValue(MessageInfo.SENSORTYPE_PULMONARYVENTILATION);
				((StringParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.SENSORID)).SetValue("PULVENT");
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASMODE))
						.SetValue(MessageInfo.SINGLE_MEAS_MODE);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEMNUM)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASITEM))
						.SetValue(MessageInfo.MM_MI_PULMONARY_VENTILATION);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASCOUNT)).SetValue(1);
				((IntParameter) MessageData.parmsDataHashMap
						.get(ParameterDataKeys.MEASINTERVAL)).SetValue(5);
				break;
			case 7: // Complex
			/*
			 * mOnceMeas = false; Meas_CommandName_Parm = BATCH_MEAS_COMM;
			 * Meas_CommandID_Parm += 1; Batch_MeasNum_Parm =
			 * mSelectedBatch_MeasNum_Parm; Meas_MessageBufferSize_Parm =
			 * BATCH_MEAS_MESSAGE_HEADER_SIZE + (SINGLE_MEAS_COMMAND_SIZE *
			 * Batch_MeasNum_Parm); if (Batch_MeasNum_Parm > 0) {
			 * CreateBatchMeasureCommandMessage(mSendMessBuffer, 0); }
			 */
				break;
		}

		mOutMsg = new SingleSensorMeasMessage();
		mSendMsgBuffer = mOutMsg.CreaterMessage();
		mSendMsgSize = mSendMsgBuffer.length;

	}

	public void CreateGWStateQueryCommandMessage(int measID) {
		// Set common parameters value
		// TransID_Parm += 1;

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGHEADER))
				.SetValue(MessageInfo.MSG_HEADER_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGEND))
				.SetValue(MessageInfo.MSG_TAIL_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGBUFFERSIZE))
				.SetValue(MessageInfo.GW_STATE_QUERY_PARMS_SIZE);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGTYPE))
				.SetValue(MessageInfo.MSGTYPE_ST_GWZG_STATE_QUERY_REQ);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).SetValue(TransID_Parm);
		// cyf
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.DOCTORID))
				.SetValue(MemberManage.doctorID);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.PATIENTID))
				.SetValue(MemberManage.patientID);
		// end cyf
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(1);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM2)).SetValue(1);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM3)).SetValue(1);

		mOutMsg = new GWStateQueryMessage();
		mSendMsgBuffer = mOutMsg.CreaterMessage();
		mSendMsgSize = mSendMsgBuffer.length;
	}

	public void CreateSNConfigCommandMessage(int measID) {
		// Set common parameters value
		// TransID_Parm += 1;

		/*
		 * Calendar now = Calendar.getInstance();
		 * 
		 * short timeYear = (short) now.YEAR; short timeMonth =
		 * (short)now.MONTH; short timeDay = (short) now.DAY_OF_MONTH;
		 * 
		 * float timeDayOfSec = (float) (now.MILLISECOND ) /1000 + now.SECOND +
		 * now.MINUTE * 60 + now.HOUR_OF_DAY * 3600;
		 */
		SharedPreferences settings = mContext.getSharedPreferences(
				ConstDef.SHAREPRE_SETTING_INFOS, 0); // 首先获取一个 SharedPreferences
														// 对象
		int reservedParm1 = settings.getInt(
				ConstDef.SHAREPRE_BLOODPRESS_CONFIG_PARM, 1);
		long timeMillSecs = System.currentTimeMillis();
		Date date = new Date(timeMillSecs);
		SimpleDateFormat parser = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.sss");
		String time = parser.format(date);
		String[] timeInfo = time.split("-| |:|[//.]");
		short timeYear = 0;
		short timeMonth = 0;
		short timeDay = 0;
		float timeDayOfSec = 0.0f;

		if (timeInfo.length == 7) {
			timeYear = Short.parseShort(timeInfo[0]);
			timeMonth = Short.parseShort(timeInfo[1]);
			timeDay = Short.parseShort(timeInfo[2]);
			timeDayOfSec = ((float) Short.parseShort(timeInfo[6]) / 1000)
					+ Short.parseShort(timeInfo[5])
					+ Short.parseShort(timeInfo[4]) * 60
					+ Short.parseShort(timeInfo[3]) * 3600;
		}

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGHEADER))
				.SetValue(MessageInfo.MSG_HEADER_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGEND))
				.SetValue(MessageInfo.MSG_TAIL_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGBUFFERSIZE))
				.SetValue(MessageInfo.ST_SENSOR_CONFIG_PARMS_SIZE);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGTYPE))
				.SetValue(MessageInfo.MSGTYPE_ST_SENSOR_CONFIG_REQ);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).SetValue(TransID_Parm);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORNUM)).SetValue(1);

		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(reservedParm1);

		((StringParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MOBMEDGWID)).SetValue("MOBCONFI");

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.CURRENTTIMEYEAR)).SetValue(timeYear);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.CURRENTTIMEMONTH)).SetValue(timeMonth);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.CURRENTTIMEDAY)).SetValue(timeDay);
		((FloatParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.CURRENTTIMESECOFDAY))
				.SetValue(timeDayOfSec);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.DOCTORID))
				.SetValue(MemberManage.doctorID);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.PATIENTID))
				.SetValue(MemberManage.patientID);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM2)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM3)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM4)).SetValue(0);
		mOutMsg = new SNConfigMessage();
		mSendMsgBuffer = mOutMsg.CreaterMessage();
		mSendMsgSize = mSendMsgBuffer.length;
	}

	public void CreateMeasureStopCommandMessage(int measID) {
		// Set common parameters value
		// TransID_Parm += 1;

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGHEADER))
				.SetValue(MessageInfo.MSG_HEADER_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGEND))
				.SetValue(MessageInfo.MSG_TAIL_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGBUFFERSIZE))
				.SetValue(MessageInfo.ST_MEAS_STOP_PARMS_SIZE);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGTYPE))
				.SetValue(MessageInfo.MSGTYPE_ST_MEAS_STOP_REQ);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).SetValue(TransID_Parm);

		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM2)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM3)).SetValue(0);

		mOutMsg = new MeasStopMessage();
		mSendMsgBuffer = mOutMsg.CreaterMessage();
		mSendMsgSize = mSendMsgBuffer.length;
	}

	public void CreateBodyTempCalCommandMessage(int measID) {
		// Set common parameters value
		// TransID_Parm += 1;

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGHEADER))
				.SetValue(MessageInfo.MSG_HEADER_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGEND))
				.SetValue(MessageInfo.MSG_TAIL_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGBUFFERSIZE))
				.SetValue(MessageInfo.ST_BODYTEMP_CAL_PARMS_SIZE);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGTYPE))
				.SetValue(MessageInfo.MSGTYPE_ST_BODYTEMP_SENSOR_CAL_REQ);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).SetValue(TransID_Parm);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORNUM)).SetValue(1);

		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORTYPE))
				.SetValue(MessageInfo.SENSORTYPE_THERMOMETER);
		((StringParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");

		mOutMsg = new BodyTempSensorCalMessage();
		mSendMsgBuffer = mOutMsg.CreaterMessage();
		mSendMsgSize = mSendMsgBuffer.length;
	}

	public void CreateAlarmTimeConfigCommandMessage(float[] coeffs) {
		// TransID_Parm += 1;

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGHEADER))
				.SetValue(MessageInfo.MSG_HEADER_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGEND))
				.SetValue(MessageInfo.MSG_TAIL_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGBUFFERSIZE))
				.SetValue(MessageInfo.ST_ALARM_TIME_CONFIG_PARMS_SIZE);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGTYPE))
				.SetValue(MessageInfo.MSGTYPE_ST_ALARM_TIME_CONFIG_REQ);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).SetValue(TransID_Parm);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORNUM)).SetValue(1);

		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORTYPE))
				.SetValue(MessageInfo.SENSORTYPE_THERMOMETER);
		((StringParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.ALARMTIMEYEAR))
				.SetValue((short) coeffs[0]);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.ALARMTIMEMONTH))
				.SetValue((short) coeffs[1]);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.ALARMTIMEDAY))
				.SetValue((short) coeffs[2]);
		((FloatParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.ALARMTIMESECOFDAY)).SetValue(coeffs[3]);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.ALARMTIMEBEEP))
				.SetValue((int) coeffs[4]);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.ALARMTIMEBEEPSEQLENGTH))
				.SetValue((int) coeffs[5]);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.ALARMTIMEBEEPSEQGAPINMINUTES))
				.SetValue((int) coeffs[6]);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM2)).SetValue(0);
		mOutMsg = new AlarmTimeConfigMessage();
		mSendMsgBuffer = mOutMsg.CreaterMessage();
		mSendMsgSize = mSendMsgBuffer.length;
	}

	public void CreateBodyTempCalCoefConfigCommandMessage(float[] coeffs) {
		// Set common parameters value
		// TransID_Parm += 1;

		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGHEADER))
				.SetValue(MessageInfo.MSG_HEADER_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGEND))
				.SetValue(MessageInfo.MSG_TAIL_SYNC_WORD);
		((ShortParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGBUFFERSIZE))
				.SetValue(MessageInfo.ST_BODYTEMP_CAL_COEFF_CONFIG_PARMS_SIZE);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.MSGTYPE))
				.SetValue(MessageInfo.MSGTYPE_ST_BODYTEMP_SENSOR_CAL_COEF_CONFIG_REQ);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.TRANSID)).SetValue(TransID_Parm);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORNUM)).SetValue(1);

		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM1)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORTYPE))
				.SetValue(MessageInfo.SENSORTYPE_THERMOMETER);
		((StringParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.SENSORID)).SetValue("THERMOME");
		((FloatParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.BodyTempCalCoeffA)).SetValue(coeffs[0]);
		((FloatParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.BodyTempCalCoeffB)).SetValue(coeffs[1]);
		((FloatParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.BodyTempCalCoeffC)).SetValue(coeffs[2]);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM2)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM3)).SetValue(0);
		((IntParameter) MessageData.parmsDataHashMap
				.get(ParameterDataKeys.RESERVEDPARM4)).SetValue(0);
		mOutMsg = new BodyTempSensorCalCoefConfigMessage();
		mSendMsgBuffer = mOutMsg.CreaterMessage();
		mSendMsgSize = mSendMsgBuffer.length;
	}

	private class MessageParserThread extends Thread {

		public MessageParserThread() {

		}

		public void run() {
			while (true) {
				synchronized (this) {
					try {

						if (ReceiveMsgProcessed) {
							mHandler.obtainMessage(ConstDef.MESSAGE_PROCESSED,
									1, -1).sendToTarget();
							wait();
						} else {
							ReceiveMessageProcess();
						}
					}

					catch (Exception e) {
						Log.e("Error", "MessageParserThread Error", e);
					}
				}
			}

		}

		private void ResetParms() {
			// Reset all parameters

			for (int idx = 0; idx < INT_FLOAT_PARM_SIZE; idx++) {
				mReceiveMsgMsgTypeParm[idx] = Byte.MAX_VALUE;
			}

			for (int idx = 0; idx < SHORT_PARM_SIZE; idx++) {
				mReceiveMsgHeaderParm[idx] = Byte.MAX_VALUE;
				mReceiveMsgBufferSizeParm[idx] = Byte.MAX_VALUE;
				mReceiveMsgEndParm[idx] = Byte.MAX_VALUE;
			}

			mReceiveMsgBufferSize = -1;
			mReceiveMsgRetsLen = -1;

			mFindReceiveMsgParmProcessOne = false;

			mReadReceiveMsgParmIdx = -1;
			mReceiveMsgParmIdx = -1;

			mRetsError = false;
			mMeasError = MeasError.NO_ERROR;

			if (mReceiveMsgPreParmEndIdx == (mReceiveBufferSize - 1)) {
				mReceiveBufferSize = -1;
				mReceiveMsgPreParmEndIdx = -1;
				mFindReceiveMsgParmProcessOne = true;

				ReceiveMsgProcessed = true;
				MeasFinised = false;
			}
		}

		public void ReceiveMessageProcess() {

			while (!ReceiveMsgProcessed && !MeasFinised) {
				switch (mReceiveMsgProcessState) {
				case INIT_STATE:		//1
					InitProcess();
				case FIND_MSG_HEADER_STATE:
					GetReceiveMsgParmsProcess();		//2
					break;
				case FIND_MSG_BUFFER_SIZE_STATE:
					GetReceiveMsgParmsProcess();		//3
					break;
				case FIND_MSG_MSG_TYPE_STATE:
					GetReceiveMsgParmsProcess();		//4
					break;
				case GET_PARMS_RESULTS_STATE:
					GetReceiveMsgRetsProcess();//此处报错		//5
					break;
				case FIND_MSG_END_STATE:
					GetReceiveMsgParmsProcess();		//6
				case SEDN_RESPONSE_MSG_STATE:
					SendRespProcess();		//7
				case PROCESS_RESULTS_STATE:
					ProcessResultsProcess();		//8
					break;
				case SEND_RESULTS_STATE:
					// Send Results to UI
					SendResultsProcess();		//9
					break;
				case SEND_ERROR_MSG_STATE:
					// Send Results to UI
					SendErrorMessageProcess();		//10
					break;
				case FINISHED_MEAS:
					// Reset Parameters
					FinishedProcess();		//11
					break;
				}
			}

		}

		private int ArraySearch(byte[] array, int startIndex, int endIndex,
				byte value) {
			if (startIndex < 0) {
				throw new IllegalArgumentException(
						"startIndex less array lowboundary：" + startIndex);
			}

			if (endIndex > (array.length - 1)) {
				throw new IllegalArgumentException(
						"endIndex over array upperboundary：" + endIndex);
			}

			int indexOfValue = -1;

			for (int idx = startIndex; idx <= endIndex; idx++) {
				if (array[idx] == value) {
					indexOfValue = idx;
					break;
				}
			}

			return indexOfValue;
		}

		private void InitProcess() {//1

			// We may need to use the ArraySearch to do the first step MsgHeader
			// searching.
			// ArraySearch(array, startIndex, endIndex, value)
			SetReceiveMessageProcessParm();
		}

		private void FinishedProcess() {		//11
			// Measurment finished, can enable the start button for next measure
			// or give some indication
			ResetParms();
			SetReceiveMessageProcessParm();
		}

		private void GetReceiveMsgRetsProcess() {		//5
			int retSizeWOMsgtype = mReceiveMsgBufferSize - INT_FLOAT_PARM_SIZE;
			mRemainReceiveRetsBufferSize = retSizeWOMsgtype
					- mReceiveRetsBufferSize;
			if ((mRemainReceiveRetsBufferSize + mReceiveMsgPreParmEndIdx) > (mReceiveBufferSize - 1)) {
				System.arraycopy(mReceiveBuffer, mReceiveMsgPreParmEndIdx + 1,
						mReceiveRetsBuffer, mReceiveRetsBufferSize,
						mReceiveBufferSize - mReceiveMsgPreParmEndIdx - 1);
				mReceiveRetsBufferSize += mReceiveBufferSize
						- mReceiveMsgPreParmEndIdx - 1;
				mReceiveMsgPreParmEndIdx = -1;
				ReceiveMsgProcessed = true;
			} else {
				System.arraycopy(mReceiveBuffer, mReceiveMsgPreParmEndIdx + 1,		//mReceiveBuffer是收到所有包的合集
						mReceiveRetsBuffer, mReceiveRetsBufferSize,					//mReceiveRetsBuffer应该是所有收到的包的合集或者单个包的内容
						mRemainReceiveRetsBufferSize);								//mReceiveRetsBuffer接收蓝牙连接命令包时去掉了头八位和包尾
				// It should equal to mReceiveRetsBufferSize;
				mReceiveRetsBufferSize += mRemainReceiveRetsBufferSize;
				mReceiveMsgPreParmEndIdx += mRemainReceiveRetsBufferSize;
				SetReceiveMessageProcessParm();
			}
		}

		private void ProcessResultsProcess() {		//8
			switch (mReceiveMsgMsgType) {		//有问题
			case MessageInfo.MSGTYPE_ST_MEAS_RESULT_IND:
				mInMsg = new SingleSensorMeasRetMessage();
				mInMsg.GetInMsg(mReceiveRetsBuffer, 0, mReceiveRetsBufferSize);			//看一看mReceiveRetsBuffer的长度，其中包含了除包头尾长度和网关指令之外的所有信息
				mInMsg.Process();														//mInMsg包含了除包头尾长度网关之外所有信息
				mRetsError = mInMsg.GetMessageError();
				if (!mRetsError) {
					mResults = ((SingleSensorMeasRetMessage) mInMsg)////////////////////////
							.GetMeasItemResults();
					mReceiveMsgSensorType = ((SingleSensorMeasRetMessage) mInMsg)
							.GetSensorType();
				}
				break;
			case MessageInfo.MSGTYPE_ST_GWZG_STATE_QUERY_IND:
				mInMsg = new GWStateRetMessage();
				mInMsg.GetInMsg(mReceiveRetsBuffer, 0, mReceiveRetsBufferSize);
				mInMsg.Process();
				mResults = ((GWStateRetMessage) mInMsg).GetQueryResults();
				break;
			case MessageInfo.MSGTYPE_ST_BODYTEMP_SENSOR_CAL_IND:
				mInMsg = new BodyTempSensorCalRetMessage();
				mInMsg.GetInMsg(mReceiveRetsBuffer, 0, mReceiveRetsBufferSize);
				mInMsg.Process();
				mResults = ((BodyTempSensorCalRetMessage) mInMsg)
						.GetBodyTempCalResults();
				break;
			default:
				break;
			}

			mReceiveRetsBufferSize = 0;
			SetReceiveMessageProcessParm();
		}

		private void SendRespProcess() {		//7
			// Currently, do not send the ResponseMessage

			SetReceiveMessageProcessParm();
		}

		private void GetReceiveMsgParmsProcess() {//2,3,4,6
			int idx;
			if (mFindReceiveMsgParmProcessOne) {
				mReceiveMsgParmIdx = mReceiveMsgPreParmEndIdx + 1;
				if (mReceiveMsgParmIdx >= mReceiveBufferSize) {
					// This message does not include the
					// this ReceiveMsgParm, it is in the next message.
					// Still in GET_PARMS_RESULTS_STATE state

					// Set the mReceiveMsgPreParmEndIdx for next message
					mReceiveMsgPreParmEndIdx = -1;

					ReceiveMsgProcessed = true;
					mFindReceiveMsgParmProcessOne = true;

				} else if (mReceiveMsgParmIdx > (mReceiveBufferSize - mReceiveMsgParmSize)) {
					// The part bytes of this ReceiveMsgParm is in the
					// message.
					// Need the next message to get the whole
					// ReceiveMessageBufferSizeParm value;

					mReadReceiveMsgParmIdx = mReceiveBufferSize
							- mReceiveMsgParmIdx - 1;
					for (idx = 0; idx <= mReadReceiveMsgParmIdx; idx++) {
						mReceiveMsgParm[idx] = mReceiveBuffer[mReceiveMsgParmIdx		//mReceiveMsgParm可能包含所有指令信息
								+ idx];
					}

					// Set the mReceiveMsgPreParmEndIdx for next message
					mReceiveMsgPreParmEndIdx = -1;
					ReceiveMsgProcessed = true;
					mFindReceiveMsgParmProcessOne = false;

				} else {
					// Parm all bytes are within this message.
					for (idx = 0; idx < mReceiveMsgParmSize; idx++) {
						mReceiveMsgParm[idx] = mReceiveBuffer[mReceiveMsgParmIdx
								+ idx];
					}

					mFindReceiveMsgParmProcessOne = true;
					mReceiveMsgPreParmEndIdx = mReceiveMsgParmIdx + idx - 1;
					SetReceiveMessageProcessParm();
				}

			} else {
				if ((mReceiveMsgParmSize - mReadReceiveMsgParmIdx - 1) > mReceiveBufferSize) {
					// The part bytes of this ReceiveMsgParm is in the
					// message.
					// Need the next message to get the whole
					// ReceiveMessageBufferSizeParm value;
					for (idx = 0; idx < mReceiveBufferSize; idx++) {
						mReceiveMsgParm[mReadReceiveMsgParmIdx + 1 + idx] = mReceiveBuffer[idx];

					}
					mReadReceiveMsgParmIdx += mReceiveBufferSize;

					// Set the mReceiveMsgPreParmEndIdx for next message
					mReceiveMsgPreParmEndIdx = -1;
					ReceiveMsgProcessed = true;
					mFindReceiveMsgParmProcessOne = false;

				} else {
					// read the remained bytes of parameter.
					for (idx = 0; idx < (mReceiveMsgParmSize
							- mReadReceiveMsgParmIdx - 1); idx++) {
						mReceiveMsgParm[mReadReceiveMsgParmIdx + 1 + idx] = mReceiveBuffer[idx];
					}

					mFindReceiveMsgParmProcessOne = true;
					mReceiveMsgPreParmEndIdx = idx - 1;
					SetReceiveMessageProcessParm();
				}
			}
		}

		private boolean IsMeasFinished() {
			if ((mReceiveMsgProcessState == ReceiveMsgProcessState.SEND_RESULTS_STATE)
					&& (mStopMeas || mOnceMeas) && !IsConfirmMsg()) {
				return true;
			} else {
				{
					return false;
				}
			}
		}

		private boolean IsConfirmMsg() {
			if (mReceiveMsgMsgType == MessageInfo.MSGTYPE_ST_MEAS_START_CFM
					|| mReceiveMsgMsgType == MessageInfo.MSGTYPE_ST_MEAS_STOP_CFM
					|| mReceiveMsgMsgType == MessageInfo.MSGTYPE_ST_MEAS_CONFIG_CFM
					|| mReceiveMsgMsgType == MessageInfo.MSGTYPE_ST_SET_REALTIME_CLOCK_CFM
					|| mReceiveMsgMsgType == MessageInfo.MSGTYPE_ST_GWZG_STATE_QUERY_CFM) {
				return true;
			} else {

				return false;
			}

		}

		private void SendErrorMessageProcess() {		//10
			ResetParms();
		}

		private void SendResultsProcess() {		//9
			MeasResultsReady = true;
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "MessageProcess SendResults");
			}
			// Send the obtained bytes to the UI Activity
			mHandler.obtainMessage(ConstDef.MESSAGE_RESULTS,
					mReceiveMsgMsgType, mReceiveMsgSensorType, mResults)
					.sendToTarget();
			try {
				wait();
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "MessageProcess Continue");
				}
				SetReceiveMessageProcessParm();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// SetReceiveMessageProcessParm();
		}

		private void SetReceiveMessageProcessParm() {

			switch (mReceiveMsgProcessState) {
			case INIT_STATE:
				mReceiveMsgParm = mReceiveMsgHeaderParm;
				mReceiveMsgParmSize = SHORT_PARM_SIZE;
				mReceiveMsgProcessState = ReceiveMsgProcessState.FIND_MSG_HEADER_STATE;

				break;
			case FIND_MSG_HEADER_STATE:
				mReceiveMsgHeader = MessageBase
						.InvBytesToShort(mReceiveMsgHeaderParm);
				if (mReceiveMsgHeader == MessageInfo.MSG_HEADER_SYNC_WORD) {
					mReceiveMsgParm = mReceiveMsgBufferSizeParm;
					mReceiveMsgParmSize = SHORT_PARM_SIZE;
					mReceiveMsgProcessState = ReceiveMsgProcessState.FIND_MSG_BUFFER_SIZE_STATE;
				} else {
					mMeasError = MeasError.ERROR_HEDAER_NOT_FOUND;
					mReceiveMsgProcessState = ReceiveMsgProcessState.SEND_ERROR_MSG_STATE;
					Log.e(TAG, "MSG_HEADER_Error");
				}

				break;
			case FIND_MSG_BUFFER_SIZE_STATE:
				mReceiveMsgBufferSize = MessageBase
						.InvBytesToShort(mReceiveMsgBufferSizeParm);

				mReceiveMsgParm = mReceiveMsgMsgTypeParm;
				mReceiveMsgParmSize = INT_FLOAT_PARM_SIZE;
				mReceiveMsgProcessState = ReceiveMsgProcessState.FIND_MSG_MSG_TYPE_STATE;
				break;

			case FIND_MSG_MSG_TYPE_STATE:
				mReceiveMsgMsgType = MessageBase			//查看类型
						.InvBytesToInt(mReceiveMsgMsgTypeParm);
				mReceiveMsgProcessState = ReceiveMsgProcessState.GET_PARMS_RESULTS_STATE;
				break;
			case GET_PARMS_RESULTS_STATE:
				mReceiveMsgParm = mReceiveMsgEndParm;
				mReceiveMsgParmSize = SHORT_PARM_SIZE;
				mReceiveMsgProcessState = ReceiveMsgProcessState.FIND_MSG_END_STATE;
				break;
			case FIND_MSG_END_STATE:
				mReceiveMsgEnd = MessageBase
						.InvBytesToShort(mReceiveMsgEndParm);
				if (mReceiveMsgEnd != MessageInfo.MSG_TAIL_SYNC_WORD) {
					mMeasError = MeasError.ERROR_END_NOT_FOUND;
					mReceiveMsgProcessState = ReceiveMsgProcessState.SEND_ERROR_MSG_STATE;
					Log.e(TAG, "MSG_END_Error");
				} else {
					mReceiveMsgProcessState = ReceiveMsgProcessState.SEDN_RESPONSE_MSG_STATE;
				}
				break;
			case SEDN_RESPONSE_MSG_STATE:
				mReceiveMsgProcessState = ReceiveMsgProcessState.PROCESS_RESULTS_STATE;
				break;
			case PROCESS_RESULTS_STATE:
				if (mRetsError) {
					mMeasError = MeasError.ERROR_RETS_ERROR;
					mReceiveMsgProcessState = ReceiveMsgProcessState.SEND_ERROR_MSG_STATE;
					Log.e(TAG, "MSG_MesRet_Error");
				} else {
					mReceiveMsgProcessState = ReceiveMsgProcessState.SEND_RESULTS_STATE;
				}
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "MessageProcess ReceiveMsgProcessState:"
							+ String.valueOf(mReceiveMsgProcessState));
				}
				break;
			case SEND_ERROR_MSG_STATE:
				mReceiveMsgProcessState = ReceiveMsgProcessState.INIT_STATE;
				break;
			case SEND_RESULTS_STATE:
				// Currently, do not use the multi-threads. So there only set
				// the
				// mReceiveMsgProcessState to next state.
				boolean measFinished = IsMeasFinished();
				if (measFinished) {
					mReceiveMsgProcessState = ReceiveMsgProcessState.FINISHED_MEAS;
				} else {
					mReceiveMsgProcessState = ReceiveMsgProcessState.INIT_STATE;
				}
				break;
			case FINISHED_MEAS:
				mReceiveMsgProcessState = ReceiveMsgProcessState.INIT_STATE;
				break;

			}
		}

		//以下为我自己写的

	}

}