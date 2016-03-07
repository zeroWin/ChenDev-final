package mobileMedical.Message;

public class MessageInfo {
	// Constant Definition
	/* 
	 * 
	 * 
	 sensor type definition, from 0x0000 to 0xFFFF.
	 */
	public static final short SENSORTYPE_THERMOMETER = 0xA1;	//体温
	public static final short SENSORTYPE_ELECTROCARDIOGRAMMETER = 0xA2;		//心电图
	public static final short SENSORTYPE_BLOODPRESSUREMETER = 0xA3;		//血压
	public static final short SENSORTYPE_BLOODSUGARMETER = 0xA4;		//血糖
	public static final short SENSORTYPE_STETHOSCOPE = 0xA5;
	public static final short SENSORTYPE_BLOODOXYGENMETER = 0xA6;		//血氧
	public static final short SENSORTYPE_PULMONARYVENTILATION = 0xA7;		//肺通

	public static final int SINGLE_MEAS_MODE = 0;
	public static final int CONT_MEAS_MODE = 1;
	/* 
	 result type  definition, which is composed two type identifier, 
	 one is data type of low 16bit in uint32, another is meas result type 
	 of high 16bit in uint32. 
	 */
	public static final int  DATATYPE_CHAR_UINT16 = 0x01;
	public static final int  DATATYPE_INT8 = 0x02;
	public static final int  DATATYPE_INT16 = 0x03;
	public static final int  DATATYPE_INT32 = 0x04;
	public static final int  DATATYPE_REAL32 = 0x05;
	public static final int  DATATYPE_REAL64 = 0x06;

	public static final int  MEASRLTTYPE_BASICTEMP  = 0x010000; // high 16 bit
	public static final int  MEASRLTTYPE_NORMTEMP   = 0x020000; // high 16 bit



	/*
	 measurement item  definition
	 */
	public static final int  MM_MI_BODY_TEMPERATURE = (1 << 6); //  64;  // 
	public static final int  MM_MI_ELECTRO_CARDIOGRAM = (1 << 7); // 128; // 
	public static final int  MM_MI_HEART_RATE =    (1 << 8);  // 256;  // 
	public static final int  MM_MI_BLOOD_PRESSURE = (1 << 9);  // 512;  // 
	public static final int  MM_MI_BLOOD_SUGAR =   (1 << 10); // 1024; // 
	public static final int  MM_MI_BLOOD_OXYGEN =   (1 << 11); // 2048; // 
	public static final int  MM_MI_BODY_BASE_TEMPERATURE = (1 << 12); //  4096;  // 
	public static final int  MM_MI_PULMONARY_VENTILATION = (1 << 13); //  8192;  // 
	
	public static final short MSG_HEADER_SYNC_WORD = 0x1F2E;
	public static final short MSG_TAIL_SYNC_WORD = 0x4B3D;



	/* 
	 message type/command name micro definition, uint16.
	 */
	public static final int STMSG_REQUEST_BIT   = 0x80000000;
	public static final int STMSG_CONFIRM_BIT    = 0x90000000;
	public static final int STMSG_INDICATION_BIT =  0xA0000000;
	public static final int STMSG_RESPONSE_BIT   =  0xB0000000;

	

	public static final int MSGTYPE_ST_MEAS_START  = 0x13;
	public static final int MSGTYPE_ST_MEAS_STOP = 0x14;
	public static final int MSGTYPE_ST_SENSOR_ATTACH = 0x15;
	public static final int MSGTYPE_ST_MEAS_CONFIG = 0x16;
	public static final int MSGTYPE_ST_MEAS_RESULT = 0x17;
	public static final int MSGTYPE_ST_MEAS_INTERMEDIATE_RESULT = 0x18;
	public static final int MSGTYPE_ST_SET_REALTIME_CLOCK = 0x19;
	public static final int MSGTYPE_ST_GWZG_STATE_QUERY = 0x20;
	public static final int MSGTYPE_ST_SENSOR_CONFIG = 0x21;

	public static final int MSGTYPE_ST_MEAS_START_REQ = 0x80000013;
	public static final int MSGTYPE_ST_MEAS_STOP_REQ = 0x80000014;
	public static final int MSGTYPE_ST_SENSOR_ATTACH_REQ = 0x80000015;
	public static final int MSGTYPE_ST_MEAS_CONFIG_REQ = 0x80000016;
	public static final int MSGTYPE_ST_MEAS_RESULT_REQ = 0x80000017;
	public static final int MSGTYPE_ST_MEAS_INTERMEDIATE_RESULT_REQ = 0x80000018;
	public static final int MSGTYPE_ST_SET_REALTIME_CLOCK_REQ = 0x80000019;
	public static final int MSGTYPE_ST_GWZG_STATE_QUERY_REQ = 0x80000020; 
	public static final int MSGTYPE_ST_SENSOR_CONFIG_REQ = 0x80000021; 
	public static final int MSGTYPE_ST_BODYTEMP_SENSOR_CAL_REQ = 0x80000022;
	public static final int MSGTYPE_ST_BODYTEMP_SENSOR_CAL_COEF_CONFIG_REQ = 0x80000024;
	public static final int MSGTYPE_ST_ALARM_TIME_CONFIG_REQ = 0x80000025;
	
	public static final int MSGTYPE_ST_MEAS_START_CFM   =    0x90000013;
	public static final int MSGTYPE_ST_MEAS_STOP_CFM    =    0x90000014;
	public static final int MSGTYPE_ST_SENSOR_ATTACH_CFM  =  0x90000015;
	public static final int MSGTYPE_ST_MEAS_CONFIG_CFM  =    0x90000016;
	public static final int MSGTYPE_ST_MEAS_RESULT_CFM  =    0x90000017;
	public static final int MSGTYPE_ST_MEAS_INTERMEDIATE_RESULT_CFM  = 0x90000018;
	public static final int MSGTYPE_ST_SET_REALTIME_CLOCK_CFM   =      0x90000019;
	public static final int MSGTYPE_ST_GWZG_STATE_QUERY_CFM    =       0x90000020;
	public static final int MSGTYPE_ST_SENSOR_CONFIG_CFM = 0x90000021; 
	
	public static final int MSGTYPE_ST_MEAS_START_IND   =    0xA0000013;
	public static final int MSGTYPE_ST_MEAS_STOP_IND   =     0xA0000014;
	public static final int MSGTYPE_ST_SENSOR_ATTACH_IND =   0xA0000015;
	public static final int MSGTYPE_ST_MEAS_CONFIG_IND   =   0xA0000016;
	public static final int MSGTYPE_ST_MEAS_RESULT_IND   =   0xA0000017;
	public static final int MSGTYPE_ST_MEAS_INTERMEDIATE_RESULT_IND =  0xA0000018;
	public static final int MSGTYPE_ST_SET_REALTIME_CLOCK_IND    =     0xA0000019;
	public static final int MSGTYPE_ST_GWZG_STATE_QUERY_IND      =    0xA0000020;
	public static final int MSGTYPE_ST_SENSOR_CONFIG_IND      =    0xA0000021;
	public static final int MSGTYPE_ST_BODYTEMP_SENSOR_CAL_IND      =    0xA0000023;
	
	public static final int MSGTYPE_ST_MEAS_START_RSP  =     0xB0000013;
	public static final int MSGTYPE_ST_MEAS_STOP_RSP   =     0xB0000014;
	public static final int MSGTYPE_ST_SENSOR_ATTACH_RSP =   0xB0000015;
	public static final int MSGTYPE_ST_MEAS_CONFIG_RSP   =   0xB0000016;
	public static final int MSGTYPE_ST_MEAS_RESULT_RSP  =   0xB0000017;
	public static final int MSGTYPE_ST_MEAS_INTERMEDIATE_RESULT_RSP  = 0xB0000018;
	public static final int MSGTYPE_ST_SET_REALTIME_CLOCK_RSP   =      0xB0000019;
	public static final int MSGTYPE_ST_GWZG_STATE_QUERY_RSP    =       0xB0000020;
	public static final int MSGTYPE_ST_SENSOR_CONFIG_RSP    =       0xB0000021;
// End Constant Definition
	
	protected static  int req(int x) {return (x|STMSG_REQUEST_BIT);}
	protected static  int cfm(int x) {return (x|STMSG_CONFIRM_BIT);}
	protected static  int ind(int x) {return (x|STMSG_INDICATION_BIT);}
	protected static  int rsp(int x) {return (x|STMSG_RESPONSE_BIT);}
	
	
	// Some Data Bytes Length Constants
	
	public static final short SENSORID_SIZE = 0x08; 
	
	public static final short MAC_SIZE = 0x14; 

	// From sensorTyp,sensorID,systemVersion,remainingPower,systemload,sensorState;
	// wirelessDevice,wirelessDeviceMAC,wirelessDeviceProtVers
	public static final short SENSORINFO_RESULT_SIZE = 0x4C;
	
	// Form transID, mobMedGWID, ... to ... maximumSensorNumbe, connectedSensorNumber
	public static final short GW_STATE_QUERY_RESULT_PARMS_SIZE = 0x5c;
	
	// Form transID, sensorNum, ... to ... measStartTimeYear, measStartTimeday
	// measStartTimeMonth, measStartTimeSec
	public static final short SINGLE_MEAS_RESULT_PARMS_SIZE = 0x32;
	
	// Form transID, sensorNum, ... to ... sensorID
	public static final short BODYTEMP_CAL_RESULT_PRE_PARMS_SIZE = 0x18;
	
	// Form transID, sensorNum, ... to ... sensorID
	public static final short BODYTEMP_CAL_RESULT_PARMS_SIZE = 0x14;
	
	// The messages parms except the MessageHeader, MessageBufferSize, MessageEnd
	public static final short SINGLE_MEAS_REQ_PARMS_SIZE = 0x44;
	
	// MessageHeader, MessageBufferSize, MessageEnd
	public static final short SINGLE_MEAS_REQ_MSG_PARMS_SIZE = 0x06;
	
	// 
	public static final short SINGLE_MEAS_REQ_MSG_SIZE = SINGLE_MEAS_REQ_MSG_PARMS_SIZE + SINGLE_MEAS_REQ_PARMS_SIZE;
	
	// SN Config
	// The messages parms except the MessageHeader, MessageBufferSize, MessageEnd
	public static final short ST_SENSOR_CONFIG_PARMS_SIZE = 0x36;
	
	// GW State Query
	// The messages parms except the MessageHeader, MessageBufferSize, MessageEnd
	public static final short GW_STATE_QUERY_PARMS_SIZE = 0x14;
	
	// MeasStop Command
	// The messages parms except the MessageHeader, MessageBufferSize, MessageEnd
	public static final short ST_MEAS_STOP_PARMS_SIZE = 0x14;
	
	// BodyTempCal Command
	// The messages parms except the MessageHeader, MessageBufferSize, MessageEnd
	public static final short ST_BODYTEMP_CAL_PARMS_SIZE = 0x1C;
	
	// BodyTempCalCoeffConfig Command
	// The messages parms except the MessageHeader, MessageBufferSize, MessageEnd
	public static final short ST_BODYTEMP_CAL_COEFF_CONFIG_PARMS_SIZE = 0x34;
	
	// AlarmTimeConfig Command
	// The messages parms except the MessageHeader, MessageBufferSize, MessageEnd
	public static final short ST_ALARM_TIME_CONFIG_PARMS_SIZE = 0x36;
    
    public static final int GW_STATE_ERRORING = 1;
    public static final int GW_STATE_IDLE= 64;
    public static final int GW_STATE_SEARCHING = 128;
    public static final int GW_STATE_CONNECTING = 256;
    public static final int GW_STATE_MEASURING = 512;
    public static final int GW_STATE_MEASDATATRANSMITTING = 1024;
    public static final int GW_STATE_SYNCING = 4096;
    public static final int GW_STATE_ALIGNING = 8192;
    
    
    public static final String GW_STATE_ERRORING_STR = "ERROR";
    public static final String GW_STATE_IDLE_STR = "IDLE";
    public static final String GW_STATE_SEARCHING_STR = "SEARCHING";
    public static final String GW_STATE_CONNECTING_STR = "CONNECTING";
    public static final String GW_STATE_MEASURING_STR = "MEASURING";
    public static final String GW_STATE_MEASDATATRANSMITTING_STR = "MEASDATATRANSMITTING";
    public static final String GW_STATE_SYNCING_STR = "SYNCING";
    public static final String GW_STATE_ALIGNING_STR = "ALIGNING";

    
    
    public static final int WIRELES_TYPE_WIFI = 64;
    public static final int WIRELES_TYPE_NFC = 128;
    public static final int WIRELES_TYPE_ZIGBEE = 256;
    public static final int WIRELES_TYPE_BLUETOOTH = 1024;
    public static final int WIRELES_TYPE_2NDG =2048;
    public static final int WIRELES_TYPE_3RDG = 4096;
    public static final int WIRELES_TYPE_4THG = 8192;
    
    public static final String WIRELES_TYPE_WIFI_STR = "WIFI";
    public static final String WIRELES_TYPE_NFC_STR = "NFC";
    public static final String WIRELES_TYPE_ZIGBEE_STR = "ZigBee";
    public static final String WIRELES_TYPE_BLUETOOTH_STR = "Bluetooth";
    public static final String WIRELES_TYPE_2NDG_STR = "2ndG";
    public static final String WIRELES_TYPE_3RDG_STR = "3rdG";
    public static final String WIRELES_TYPE_4THG_STR = "4thG";
    
    
    // Hybrid SPO2
    /*typedef struct{
      	int16 pulse_beep_flag; // indicate pulse sound can be detected. 
      	int16 SpO2_below_90_flag;
    	int16 no_pulse_flag;
    	int16 pulse_strength;
      	int16 plethymogram;
      	int16 pulse_searching_flag;
      	int16 SpO2_probe_drop_flag;
      	int16 bargraph;
      	int16 PulseRate;
      	int16 SpO2_value;
      	int16 SpO2_waveform[SPO2_WAVEFORM_SAMPLER_NUM_PER_PACKET];
      	}SpO2Hybrid_SubRlt_t;*/

    public static int SPO2_WAVEFORM_SAMPLES_NUM_PER_PACKET =10;
    public static int ECG_WAVEFORM_SAMPLES_NUM_PER_PACKET =10;
	//尚未定义血压包长度

    public static int PULSERATE_RESULT_IDX = 8; // From 0
    public static int SPO2_VALUE_RESULT_IDX = 9;
	public static int SPO2_WAVEFORM_RESULT_STARTIDX = 10;
	public static int SPO2_HYBRID_MEASITEM_RESULT_LEN = 20;
	public static int PULVENTVAL_RESULT_IDX = 0; // From 0
	public static int PULVENT_WAVEFORM_SAMPLES_NUM_PER_PACKET =25;
	public static int PULVENT_WAVEFORM_RESULT_STARTIDX = 1;
	public static int PULVENT_HYBRID_MEASITEM_RESULT_LEN = 26;
	public static int ECG_HEARTRATE_RESULT_IDX = 0; // From 0
	public static int ECG_WAVEFORM_RESULT_STARTIDX = 4;
	public static int BLOODPRESS_RESULT_IDX = 0; // From 0
	public static int BLOODPRESS_HYBRID_MEASITEM_RESULT_LEN = 19;
	public static int BLOODPRESS_WAVEFORM_RESULT_STARTIDX = 3;
	public static int BLOODPRESS_WAVEFORM_SAMPLES_NUM_PER_PACKET =8;
	
	public static int ECG_HYBRID_MEASITEM_RESULT_LEN = 14;
}
