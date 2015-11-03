package mobileMedical.Message;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import android.R.string;
import devDataType.Parameters.*;



public class SNConfigMessage extends OutMessage{
	
	
	 private short msgHeader;
	 private short msgEnd;
	 private short msgBuffSize;
	 private int msgType;
	 private int transID;
	 private int sensorNum;
	 private int reservedParm1;
	 private String mobMedGWID;

	 private short currentTimeYear;
	 private short currentTimeMonth;
	 private short currentTimeDay;
	 private float currentTimeSecOfDay;
	 private int  doctorID;
	 private int  patientID;
	 private int  reservedParm2;
	 private int  reservedParm3;
	 private int  reservedParm4;
	  
	  public SNConfigMessage()
	  {		
		msgHeader  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGHEADER)).GetValue();
		msgEnd = 	  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGEND)).GetValue();
		msgBuffSize = 	  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGBUFFERSIZE)).GetValue();
	    msgType   =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGTYPE)).GetValue();
	    transID=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.TRANSID)).GetValue();
	    sensorNum=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.SENSORNUM)).GetValue();
		reservedParm1=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM1)).GetValue();
		mobMedGWID = ((StringParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MOBMEDGWID)).GetValue();
		currentTimeYear  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.CURRENTTIMEYEAR)).GetValue();
		currentTimeMonth  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.CURRENTTIMEMONTH)).GetValue();
		currentTimeDay  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.CURRENTTIMEDAY)).GetValue();
		currentTimeSecOfDay = ((FloatParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.CURRENTTIMESECOFDAY)).GetValue();
		reservedParm2 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM2)).GetValue();
		
		doctorID =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.DOCTORID)).GetValue();
		patientID =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.PATIENTID)).GetValue();
		reservedParm3 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM3)).GetValue();	
		reservedParm4  =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM4)).GetValue();		
		
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
		 mobMedGWID = StringInvBytes(mobMedGWID);
		 currentTimeYear = Short.reverseBytes(currentTimeYear);
		 currentTimeMonth = Short.reverseBytes(currentTimeMonth);
		 currentTimeDay = Short.reverseBytes(currentTimeDay);
		 currentTimeSecOfDay = FloatInvBytes(currentTimeSecOfDay);
		reservedParm2 = Integer.reverseBytes(reservedParm2); 	
	 
		doctorID =  Integer.reverseBytes(doctorID); 	
		patientID = Integer.reverseBytes(patientID); 	
	   reservedParm3 = Integer.reverseBytes(reservedParm3);	
	   reservedParm4 = Integer.reverseBytes(reservedParm4);		
		  
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
			outDataStream.writeBytes(mobMedGWID);
			outDataStream.writeShort(currentTimeYear);
			outDataStream.writeShort(currentTimeMonth);
			outDataStream.writeShort(currentTimeDay);
			outDataStream.writeFloat(currentTimeSecOfDay);

			outDataStream.writeInt(doctorID);
			outDataStream.writeInt(patientID);
			outDataStream.writeInt(reservedParm2);
			outDataStream.writeInt(reservedParm3);
			outDataStream.writeInt(reservedParm4);
			outDataStream.writeShort(msgEnd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
	  
	
		   
		                            
}
