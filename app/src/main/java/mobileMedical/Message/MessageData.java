package mobileMedical.Message;
import java.util.HashMap;
import java.util.Hashtable;
import devDataType.Parameters.*;
import devDataType.ParameterArrays.*;

public class MessageData {
	public static HashMap<String, Object> parmsDataHashMap = new HashMap<String, Object>() ;
	
	// Define the Parameters
	 ShortParameter msgHeader;
	 ShortParameter msgEnd;
	 ShortParameter msgBufferSize;
	 
	 IntParameter msgType;
	 
	 IntParameter transID;
	 IntParameter sensorNum;
	 IntParameter reservedParm1;
	 IntParameter       sensorType;
	  StringParameter sensorID;
	  FloatParameter      timeout;
	  IntParameter       reservedParm2;
	 
	  IntParameter  measMode;     // 0 -- single group meas, 1 -- continuous group meas
	  IntParameter  measItemNum;  // <= 32
	  IntParameter  measItem;     // bit map to indicate meas item, maximum 32 of meas item.
	  IntParameter  measCount;    // each meas req message only require 32768 meas results for each group meas.
	  IntParameter measInterval; // measurement duration.
	  IntParameter  doctorID;
	  IntParameter  patientID;
	  IntParameter  reservedParm3;
	  
	  // SN Config
	  StringParameter mobMedGWID;
	  ShortParameter currentTimeYear;
	  ShortParameter currentTimeMonth;
	  ShortParameter currentTimeDay;
	  FloatParameter currentTimeSecOfDay;
	  IntParameter  reservedParm4;
	  
	  // BodyTempCalCoeff
	  FloatParameter  bodyTempCalCoeffA;
	  FloatParameter  bodyTempCalCoeffB;
	  FloatParameter  bodyTempCalCoeffC;
	  
	  // Alarm Time
	  ShortParameter alarmTimeYear;
	  ShortParameter alarmTimeMonth;
	  ShortParameter alarmTimeDay;
	  FloatParameter alarmTimeSecOfDay;
	  IntParameter  alarmTimeBeep;
	  IntParameter  alarmTimeBeepSeqLength;
	  IntParameter  alarmTimeBeepSeqGapMinutes;

	  
	  public MessageData()
	  {
		  msgHeader = new ShortParameter();
		  msgEnd = new ShortParameter();
		  msgBufferSize = new ShortParameter();
		  
		      msgType = new IntParameter();			 
			  transID = new IntParameter();
			  sensorNum = new IntParameter();
			  reservedParm1 = new IntParameter();
			  sensorType = new IntParameter();
			  sensorID = new StringParameter();
			  timeout = new FloatParameter();
			  reservedParm2 = new IntParameter();
			 
			  measMode = new IntParameter();    
			  measItemNum = new IntParameter();  
			  measItem = new IntParameter();   
			  measCount = new IntParameter();   
			  measInterval = new IntParameter(); 
			  doctorID = new IntParameter();
			  patientID = new IntParameter();
			  reservedParm3 = new IntParameter();
			  
			  
			  // SN Config
			  mobMedGWID =  new StringParameter();
			  currentTimeYear = new ShortParameter();
			  currentTimeMonth = new ShortParameter();
			  currentTimeDay = new ShortParameter();
			  currentTimeSecOfDay = new FloatParameter();
			  reservedParm4 = new IntParameter();
			  
			  // BodyTempCalCoeff
			  bodyTempCalCoeffA = new FloatParameter();
			  bodyTempCalCoeffB = new FloatParameter();
			  bodyTempCalCoeffC = new FloatParameter();
			  
			  // Alarm Time
			  alarmTimeYear = new ShortParameter();
			  alarmTimeMonth = new ShortParameter();
			  alarmTimeDay = new ShortParameter();
			  alarmTimeSecOfDay = new FloatParameter();
			  alarmTimeBeep = new IntParameter();
			  alarmTimeBeepSeqLength = new IntParameter();
			  alarmTimeBeepSeqGapMinutes = new IntParameter();

			  
			  // Add Parameters to HasMap
			  parmsDataHashMap.put(ParameterDataKeys.MSGHEADER, msgHeader);
			  parmsDataHashMap.put(ParameterDataKeys.MSGEND, msgEnd);
			  parmsDataHashMap.put(ParameterDataKeys.MSGBUFFERSIZE, msgBufferSize);
			  parmsDataHashMap.put(ParameterDataKeys.MSGTYPE, msgType);
			  parmsDataHashMap.put(ParameterDataKeys.TRANSID, transID);
			  parmsDataHashMap.put(ParameterDataKeys.SENSORNUM, sensorNum);
			  parmsDataHashMap.put(ParameterDataKeys.RESERVEDPARM1, reservedParm1);
			  parmsDataHashMap.put(ParameterDataKeys.SENSORTYPE, sensorType);
			  parmsDataHashMap.put(ParameterDataKeys.SENSORID, sensorID);
			  parmsDataHashMap.put(ParameterDataKeys.TIMEOUT, timeout);
			  parmsDataHashMap.put(ParameterDataKeys.RESERVEDPARM2, reservedParm2);
			  parmsDataHashMap.put(ParameterDataKeys.MEASMODE, measMode);
			  parmsDataHashMap.put(ParameterDataKeys.MEASITEMNUM, measItemNum);
			  parmsDataHashMap.put(ParameterDataKeys.MEASITEM, measItem);
			  parmsDataHashMap.put(ParameterDataKeys.MEASCOUNT, measCount);
			  parmsDataHashMap.put(ParameterDataKeys.MEASINTERVAL, measInterval);
			  parmsDataHashMap.put(ParameterDataKeys.DOCTORID, doctorID);
			  parmsDataHashMap.put(ParameterDataKeys.PATIENTID, patientID);
			  parmsDataHashMap.put(ParameterDataKeys.RESERVEDPARM3, reservedParm3);
			  
			  // SN Config
			  parmsDataHashMap.put(ParameterDataKeys.MOBMEDGWID, mobMedGWID);
			  parmsDataHashMap.put(ParameterDataKeys.CURRENTTIMEYEAR, currentTimeYear);
			  parmsDataHashMap.put(ParameterDataKeys.CURRENTTIMEMONTH, currentTimeMonth);
			  parmsDataHashMap.put(ParameterDataKeys.CURRENTTIMEDAY, currentTimeDay);
			  parmsDataHashMap.put(ParameterDataKeys.CURRENTTIMESECOFDAY, currentTimeSecOfDay);
			  parmsDataHashMap.put(ParameterDataKeys.RESERVEDPARM4, reservedParm4);
			  
			  // BodyTempCalCoeff
			  parmsDataHashMap.put(ParameterDataKeys.BodyTempCalCoeffA, bodyTempCalCoeffA);
			  parmsDataHashMap.put(ParameterDataKeys.BodyTempCalCoeffB, bodyTempCalCoeffB);
			  parmsDataHashMap.put(ParameterDataKeys.BodyTempCalCoeffC, bodyTempCalCoeffC);
			  
			  // Alarm Time
			  parmsDataHashMap.put(ParameterDataKeys.ALARMTIMEYEAR, alarmTimeYear);
			  parmsDataHashMap.put(ParameterDataKeys.ALARMTIMEMONTH, alarmTimeMonth);
			  parmsDataHashMap.put(ParameterDataKeys.ALARMTIMEDAY, alarmTimeDay);
			  parmsDataHashMap.put(ParameterDataKeys.ALARMTIMESECOFDAY, alarmTimeSecOfDay);
			  parmsDataHashMap.put(ParameterDataKeys.ALARMTIMEBEEP, alarmTimeBeep);
			  parmsDataHashMap.put(ParameterDataKeys.ALARMTIMEBEEPSEQLENGTH, alarmTimeBeepSeqLength);
			  parmsDataHashMap.put(ParameterDataKeys.ALARMTIMEBEEPSEQGAPINMINUTES, alarmTimeBeepSeqGapMinutes);
	  }

}
