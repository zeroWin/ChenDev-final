package mobileMedical.Message;


import java.io.IOException;


import devDataType.Parameters.*;



public class AlarmTimeConfigMessage extends OutMessage{
	
	
	 private short msgHeader;
	 private short msgEnd;
	 private short msgBuffSize;
	 private int msgType;
	 private int transID;
	 private int sensorNum;
	 private int reservedParm1;
	 private int sensorType;
	 private String sensorID;
	 
	 private int  alarmBeep;
	 private short alarmTimeYear;
	 private short alarmTimeMonth;
	 private short alarmTimeDay;
	 private float alarmTimeSecOfDay;
	 private int alarmTimeBeepSeqLength;
	 private int alarmTimeBeepSeqGapInMinutes;

	 private int  reservedParm2;

	  
	  public AlarmTimeConfigMessage()
	  {		
		msgHeader  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGHEADER)).GetValue();
		msgEnd = 	  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGEND)).GetValue();
		msgBuffSize = 	  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGBUFFERSIZE)).GetValue();
	    msgType   =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGTYPE)).GetValue();
	    transID=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.TRANSID)).GetValue();
	    sensorNum=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.SENSORNUM)).GetValue();
		reservedParm1=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM1)).GetValue();
		sensorType=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.SENSORTYPE)).GetValue();
		sensorID=  ((StringParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.SENSORID)).GetValue();
		alarmBeep =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.ALARMTIMEBEEP)).GetValue();
		alarmTimeYear  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.ALARMTIMEYEAR)).GetValue();
		alarmTimeMonth  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.ALARMTIMEMONTH)).GetValue();
		alarmTimeDay  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.ALARMTIMEDAY)).GetValue();
		alarmTimeSecOfDay = ((FloatParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.ALARMTIMESECOFDAY)).GetValue();
		alarmTimeBeepSeqLength = ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.ALARMTIMEBEEPSEQLENGTH)).GetValue();
		alarmTimeBeepSeqGapInMinutes = ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.ALARMTIMEBEEPSEQGAPINMINUTES)).GetValue();
		reservedParm2 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM2)).GetValue();
		
		
	
		
		InvertBytes();
		AddParmsToOutMsgBuff();
		
	  }
	  
	  private void  InvertBytes()
	  {
		 msgHeader  = Short.reverseBytes(msgHeader);
		 msgEnd = 	 Short.reverseBytes(msgEnd); 
	     msgBuffSize =  Short.reverseBytes(msgBuffSize); 	
		 msgType   =  Integer.reverseBytes(msgType); 	
		 transID = Integer.reverseBytes(transID); 
		 sensorNum = Integer.reverseBytes(sensorNum); 
		 reservedParm1 =  Integer.reverseBytes(reservedParm1); 	
		 sensorType = Integer.reverseBytes(sensorType); 	 
		 sensorID =  StringInvBytes(sensorID);
		 alarmBeep =  Integer.reverseBytes(alarmBeep); 	
		 alarmTimeYear = Short.reverseBytes(alarmTimeYear);
		 alarmTimeMonth = Short.reverseBytes(alarmTimeMonth);
		 alarmTimeDay = Short.reverseBytes(alarmTimeDay);
		 alarmTimeSecOfDay = FloatInvBytes(alarmTimeSecOfDay);
		 alarmTimeBeepSeqLength = Integer.reverseBytes(alarmTimeBeepSeqLength);  
		 alarmTimeBeepSeqGapInMinutes = Integer.reverseBytes(alarmTimeBeepSeqGapInMinutes);  
		 reservedParm2 = Integer.reverseBytes(reservedParm2); 	
	 

		  
	  }
	  
	  private void AddParmsToOutMsgBuff()
	  {
		  try {
			outDataStream.writeShort(msgHeader);
			outDataStream.writeShort(msgBuffSize);
			outDataStream.writeInt(msgType);
			outDataStream.writeInt(transID);
			outDataStream.writeInt(sensorNum);
			outDataStream.writeInt(reservedParm1);
			outDataStream.writeInt(sensorType);
			outDataStream.writeBytes(sensorID);
			outDataStream.writeInt(alarmBeep);
			outDataStream.writeShort(alarmTimeYear);
			outDataStream.writeShort(alarmTimeMonth);
			outDataStream.writeShort(alarmTimeDay);
			outDataStream.writeFloat(alarmTimeSecOfDay);
			outDataStream.writeInt(alarmTimeBeepSeqLength);
			outDataStream.writeInt(alarmTimeBeepSeqGapInMinutes);
			outDataStream.writeInt(reservedParm2);

			outDataStream.writeShort(msgEnd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
	  
	
		   
		                            
}
