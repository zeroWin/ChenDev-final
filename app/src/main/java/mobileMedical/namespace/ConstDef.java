package mobileMedical.namespace;

public class ConstDef {
	// Const BroadCase Message
	public static final String CMD_BROADCAST_MESSAGE = "android.intent.action.cmd";
	public static final String BT_CONNECT_BROADCAST_MESSAGE = "android.intent.action.btconnect";
	public static final String BT_RECEIVED_DATA_BROADCAST_MESSAGE =  "android.intent.action.received";
	
	public static final String MEAS_REQ_BROADCAST_MESSAGE = "android.intent.action.measreq";
	public static final String GW_STATE_QUERY_REQ_BROADCAST_MESSAGE = "android.intent.action.gwStateQuery";
	public static final String BODYTEMP_CAL_REQ_BROADCAST_MESSAGE = "android.intent.action.bodyTempCal";
	public static final String BODYTEMP_CAL_COEFF_CONFIG_REQ_BROADCAST_MESSAGE = "android.intent.action.bodyTempCalCoeffConfig";
	
	public static final String SN_CONFIG_REQ_BROADCAST_MESSAGE = "android.intent.action.snConfig";
	public static final String MEAS_STOP_REQ_BROADCAST_MESSAGE = "android.intent.action.measstopreq";
	public static final String ALARM_TIME_REQ_BROADCAST_MESSAGE = "android.intent.action.alarmTime";
	
	public static final String BLOODOX_MEAS_RESULTS_BROADCAST_MESSAGE = "android.intent.action.bloodox";
	public static final String BODYTEMP_MEAS_RESULTS_BROADCAST_MESSAGE = "android.intent.action.bodytemp";
	public static final String BLOODPRESS_MEAS_RESULTS_BROADCAST_MESSAGE = "android.intent.action.bloodpress";
	public static final String PULVENT_MEAS_RESULTS_BROADCAST_MESSAGE = "android.intent.action.pulmonaryVent";
	public static final String ECG_MEAS_RESULTS_BROADCAST_MESSAGE = "android.intent.action.ecg";
	
	public static final String GW_STATE_QUERY_RESULTS_BROADCAST_MESSAGE  = "android.intent.action.gwStateQueryResults";
	public static final String BODYTEMP_CAL_RESULTS_BROADCAST_MESSAGE  = "android.intent.action.bodyTempCalResults";

	public static final String MEAS_INIT_BROADCAST_MESSAGE = "android.intent.action.initMeas";
	public static final String MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE =  "android.intent.action.measResultsDisplayState";
	
	public static final String MeasType = "MeasType";
	public static final String CMD = "CMD";
	
	public static final String BODYTEMP_CAL_COEFFS = "BodyTempCalCoeffs";
	public static final String ALARM_TIME_COEFFS = "AlarmTimeCoeffs";
	
	public static final String RESULTS_DISPLAYED = "ResultsDisplayed";
	public static final String RESULTS = "Results";
	public static final String RESULTS_SIZE = "ResultsSize";
	public static final String NEW_RESUTS_INDICATOR = "NewResultsIndicator";
	public static final String GW_INFO_RESULTS = "GWInfoResults";
	public static final String BODYTEMP_CAL_TEMP_RESULTS = "BodyTempCalTempResults";
	public static final String SENSOR_INFO_RESULTS = "SensorInfoResults";
	public static final String SINGLE_SENSOR_INFO_RESULTS = "SingleSensorInfoResults";
	public static final String STRING_INFO = "StringInfo";
	public static final String BT_DEVICE = "btDevice";
	
	
	
	public static final int MESSAGE_RESULTS = 1;
	public static final int MESSAGE_PROCESSED = 2;
	public static final int BLOODOX_RESULTS = 3;
	public static final int BLOODOX_STATE = 4;
	public static final int BODYTMP_RESULTS = 5;
	public static final int BODYTMP_STATE = 6;
	
	public static final int GW_STATE_QUERY_RESULTS = 26;
	public static final int BODYTEMP_CAL_RESULTS = 27;
	
	public static final int MEAS_RETS_DISPLAY_STATE = 0;
	public static final int MEAS_START = 7;
	
	
	
	// CMD 
	public  static final int CMD_STOP_SERVICE = 0x01;  
	public static final int CMD_SEND_DATA = 0x02;
	public static final int CMD_RECEIVED_DATA = 0x03;  
	public static final int CMD_SYSTEM_EXIT =0x04;  
	public static final int CMD_SHOW_TOAST =0x05;
	public static final int CMD_SHOW_TITLE =0x06; 
	public static final int CMD_SET_READBYTE_FLAG =0x07; 
	public static final int CMD_SET_CONNECTED_DEVICE = 0x08;
	public static final int CMD_INFORM_CONNECTED_DEVICE = 0x09;
	
	// GW Info
	public  static String GWINFO_MOBMEDGWID = "GWINFO_MOBMEDGWID";
	public  static String GWINFO_SYSTEMVERSION = "GWINFO_SYSTEMVERSION";
	public  static String GWINFO_MEMORYSIZE = "GWINFO_MEMORYSIZE";
	public  static String GWINFO_SDCAPACITY = "GWINFO_SDCAPACITY";
	public  static String GWINFO_REMAININGPOWER = "GWINFO_REMAININGPOWER";
	public  static String GWINFO_SYSTEMLOAD = "GWINFO_SYSTEMLOAD";
	public  static String GWINFO_MOBMEDGWSTATE = "GWINFO_MOBMEDGWSTATE";
	public  static String GWINFO_WIRELESSDEVICE = "GWINFO_WIRELESSDEVICE";
	public  static String GWINFO_WIRELESSDEVICEMAC = "GWINFO_WIRELESSDEVICEMAC";
	public  static String GWINFO_WIRELESSDEVICEPROTVERS = "GWINFO_WIRELESSDEVICEPROTVERS";
	public  static String GWINFO_MAXIMUMSENSORNUMBER = "GWINFO_MAXIMUMSENSORNUMBER";
	public  static String GWINFO_CONNECTEDSENSORNUMBER = "GWINFO_CONNECTEDSENSORNUMBER";
	
	// Sensor Info
	public  static String  SENSORINFO_SENSORTYP = "SENSORINFO_SENSORTYP";
	public  static String  SENSORINFO_SENSORID = "SENSORINFO_SENSORID";
	public  static String  SENSORINFO_SYSTEMVERSION = "SENSORINFO_SYSTEMVERSION";
	public  static String  SENSORINFO_REMAININGPOWER = "SENSORINFO_REMAININGPOWER";
	public  static String  SENSORINFO_SYSTEMLOAD = "SENSORINFO_SYSTEMLOAD";
	public  static String  SENSORINFO_SENSORSTATE = "SENSORINFO_SENSORSTATE";
	public  static String  SENSORINFO_WIRELESSDEVICE = "SENSORINFO_WIRELESSDEVICE";
	public  static String  SENSORINFO_WIRELESSDEVICEMAC = "SENSORINFO_WIRELESSDEVICEMAC";
	public  static String  SENSORINFO_WIRELESSDEVICEPROTVERS = "SENSORINFO_WIRELESSDEVICEPROTVERS";
	
	
	// SharePerference Strings
	public static final String SHAREPRE_SETTING_INFOS = "SETTINGInfos";  
	public static final String SHAREPRE_ROUTER = "ROUTERCONNECTED"; 
	public static final String SHAREPRE_BODYTEMP = "BODYTEMP";
	public static final String SHAREPRE_BLOODOX = "BLOODOX";
	public static final String SHAREPRE_BLOODPRESURE = "BLOODPRESURE";
	public static final String SHAREPRE_BLOODSUGAR = "BLOODSUGAR";
	public static final String SHAREPRE_SENSOR  = "SENSORCONNECTED";
	
	public static final String SHAREPRE_MEAS_SENSOR = "MEASSENSOR";
	
	public static final String SHAREPRE_TIME_BEEP = "TimeBeep";
	public static final String SHAREPRE_TIME_YEAR = "TimeYear";
	public static final String SHAREPRE_TIME_Month = "TimeMonth";
	public static final String SHAREPRE_TIME_Date = "TimeDate";
	public static final String SHAREPRE_TIME_Hour = "TimeHour";
	public static final String SHAREPRE_TIME_Minute = "TimeMin";
	public static final String SHAREPRE_TIME_Second = "TimeSec";

	public static final String SHAREPRE_BLOODPRESS_DISPLAY_FRAME_LEN  = "sharepre_bloodpress_display_frame_len";
	public static final String SHAREPRE_BLOODOXYGEN_DISPLAY_FRAME_LEN  = "sharepre_bloodoxygen_display_frame_len";
	public static final String SHAREPRE_ECG_DISPLAY_FRAME_LEN  = "sharepre_ecg_display_frame_len";
	public static final String SHAREPRE_PULMONARY_DISPLAY_FRAME_LEN  = "sharepre_pulmonary_display_frame_len";
	public static final String SHAREPRE_BLOODPRESS_CONFIG_PARM  = "sharepre_bloodpress_config_parm";

	
	
	public static final String  PREFERENCE_GW_KEY_NAME_STRING = "KEY_CONNECTE_SEARCHED_ROUTER";
	public static final String  PREFERENCE_SENSOR_KEY_NAME_STRING = "KEY_CONNECTE_SENSOR";
	public static final String  PREFERENCE_NEW_BT_STRING = "KEY_NEW_BT";
	public static final String  PREFERENCE_NEW_SENSOR_STRING = "KEY_NEW_SENSOR";
	
	public static final int DEVICE_ROUTER = 0;
	public static final int DEVICE_SENSOR = 1;
	public static final int DEVICE_BLUETOOTH = 10;
	
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_MAC_ADDRESS = "device_mac_address";
    
    public static final int BodyTempTabIndex = 0;
    public static final int BloodPresureTabIndex = 1;
    public static final int ElectrocardiogramTabIndex = 2;
    public static final int BloodOxygenTabIndex = 3;
    public static final int BloodSugarTabIndex = 4;
    public static final int StethoscopeTabIndex = 5;
    public static final int PulVentTabIndex = 6;
    
    public static final String OTHERSETTING_LOADTEST_KEY_STRING = "othersetting_loadtest_key_string";
    public static final String OTHERSETTING_CLEARDB_KEY_STRING = "othersetting_cleardb_key_string";
    public static final String OTHERSETTING_SENDCOMM_KEY_STRING = "othersetting_sendcomm_key_string";
    public static final String OTHERSETTING_BODYTEMP_CAL_KEY_STRING = "othersetting_bodytempcal_key_string";
    public static final String OTHERSETTING_BLOODPRESS_CONFIG_PARM_KEY_STRING = "othersetting_bloodpressconfigparm_key_string";
    public static final String OTHERSETTING_BLOODOXYGEN_DISPLAYFRAMELEN_KEY_STRING = "othersetting_bloodoxygen_displayframelen_key_string";
    public static final String OTHERSETTING_ECG_DISPLAYFRAMELEN_KEY_STRING = "othersetting_ecg_displayframelen_key_string";
    public static final String OTHERSETTING_PULMONARY_DISPLAYFRAMELEN_KEY_STRING = "othersetting_pulmonary_displayframelen_key_string";
    public static final String OTHERSETTING_BLOODPRESS_DISPLAYFRAMELEN_KEY_STRING = "othersetting_bloodpress_displayframelen_key_string";

    
    public static final String BODYTEMPCAL_CalCoeff_KEY_STRING = "bodytempcal_calcoeff_key_string";
    public static final String BODYTEMPCAL_CalcCalCoeff_KEY_STRING = "bodytempcal_calccalcoeff_key_string";
    public static final String BODYTEMPCAL_SendCalCoeff_KEY_STRING = "bodytempcal_sendcalcoeff_key_string";
    public static final String BODYTEMPCAL_CalDataName_KEY_STRING = "bodytempcal_caldataname_key_string";
    public static final String BODYTEMPCAL_ClearAllCalDat_KEY_STRING = "bodytempcal_clearallcaldat_key_string";
    public static final String BODYTEMPCAL_CalData_KEY_STRING = "bodytempcal_caldata_key_string";

    public static final String SEND_TIME_SET_MESSAGE_STRING = "send_time_set_message_string";
    public static final String TIME_SET_PERIOD_STRING = "time_set_period_string";
    public static final String TIME_SET_DATE_STRING = "time_set_date_string";
    public static final String TIME_SET_TIME_STRING = "time_set_time_string";
    public static final String TIME_SET_SECOND_STRING = "time_set_second_string";
    public static final String BEEP_SET_SEQ_LEN_STRING = "beep_set_seq_len_string";
    public static final String BEEP_SET_SEQ_GAP_MINUTES_STRING = "beep_set_seq_gap_minutes_string";

    
    public static final String ALARM_TIME_CONFIG = "alarm_time_config";
    public static final String ALARM_TIME_BEEP = "alarm_time_beep";
	public static final String ALARM_TIME_YEAR = "alarm_time_year";
	public static final String ALARM_TIME_MONTH = "alarm_time_month";
	public static final String ALARM_TIME_DATE = "alarm_time_date";
	public static final String ALARM_TIME_HOUR = "alarm_time_hour";
	public static final String ALARM_TIME_MINUTE = "alarm_time_minute";
	public static final String ALARM_TIME_SECONDS = "alarm_time_seconds";
	public static final String ALARM_TIME_BEEP_SEQ_LENGTH = "alarm_time_beep_seq_length";
	public static final String ALARM_TIME_BEEP_SEQ_GAP_MINUTES = "alarm_time_beep_seq_gap_minutes";
	public static final String ALARM_TIME_PERIOD = "alarm_time_period";

	public static final String DATABASE_FIELD_TRANSID="id";
	public static final String DATABASE_FIELD_SENSORTYPE="sensorType";
	public static final String DATABASE_FIELD_MEASITEM="measItem";
	public static final String DATABASE_FIELD_DOCTORID="doctorID";
	public static final String DATABASE_FIELD_PATIENTID="patientID";
	public static final String DATABASE_FIELD_MEASRESULTS="measResults";
	public static final String DATABASE_FIELD_MEASRESULTSIDX="measResultsIdx";
	public static final String DATABASE_FIELD_TIMESTAMP="timestamp";

	
	public static final String DATABASE_FIELD_USERNAME="username";
	public static final String DATABASE_FIELD_PASSWORD="password";
	public static final String DATABASE_FIELD_ID="id";
	public static final String DATABASE_FIELD_WORK="work";
	public static final String DATABASE_FIELD_ROOM="room";
	public static final String DATABASE_FIELD_LEVEL="level";
	public static final String DATABASE_FIELD_TEL="tel";
	public static final String DATABASE_FIELD_EMAIL="email";
	public static final String DATABASE_FIELD_IMAGE="image";
	
	public static final String DATABASE_TABLE_NAME_DATA="data";
	public static final String DATABASE_TABLE_NAME_DOCTORINFO="doctorInfo";
	public static final String DATABASE_TABLE_NAME_DOCTORSETTING="doctorSetting";
    
	
	// Display Frame Length Constant in Seconds
	public static final float DISPLAY_FRAME_LEN_MIN = 1;
	public static final float DISPLAY_FRAME_LEN_MAX = 50;

	public static final int BLOOD_PRESS_CONFIG_PARMA_MIN = 1;
	public static final int BLOOD_PRESS_CONFIG_PARMA_MAX = 255;
}
