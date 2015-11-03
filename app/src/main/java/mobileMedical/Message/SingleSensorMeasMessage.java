package mobileMedical.Message;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import android.R.string;
import devDataType.Parameters.*;



public class SingleSensorMeasMessage extends OutMessage{
	
	
	 private short msgHeader;
	 private short msgEnd;
	 private short msgBuffSize;
	 private int msgType;
	 private int transID;
	 private int sensorNum;
	 private int reservedParm1;
	 private int sensorType;
	 private String sensorID;
	 private float      timeout;
	 private int       reservedParm2;
	 
	 private int  measMode;     
	 private int  measItemNum;  
	 private int  measItem;     
	 private int  measCount;   
	 private int measInterval; 
	 private int  doctorID;
	 private int  patientID;
	 private int  reservedParm3;
	  
	  public SingleSensorMeasMessage()
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
		timeout =  ((FloatParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.TIMEOUT)).GetValue();
		reservedParm2 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM2)).GetValue();
		measMode =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MEASMODE)).GetValue();
		measItemNum =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MEASITEMNUM)).GetValue();
		measItem = ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MEASITEM)).GetValue();
		measCount =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MEASCOUNT)).GetValue();
		measInterval =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MEASINTERVAL)).GetValue();
		doctorID =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.DOCTORID)).GetValue();
		patientID =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.PATIENTID)).GetValue();
		reservedParm3 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM3)).GetValue();		  		  
		
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
		timeout = FloatInvBytes(timeout);
		reservedParm2 = Integer.reverseBytes(reservedParm2); 	
		measMode =  Integer.reverseBytes(measMode); 	
		measItemNum =   Integer.reverseBytes(measItemNum); 	
		measItem = Integer.reverseBytes(measItem); 	
		measCount =  Integer.reverseBytes(measCount); 	
		measInterval = Integer.reverseBytes(measInterval); 	 
		doctorID =  Integer.reverseBytes(doctorID); 	
		patientID = Integer.reverseBytes(patientID); 	
	   reservedParm3 = Integer.reverseBytes(reservedParm3);					                 			                  
		  
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
			outDataStream.writeFloat(timeout);
			outDataStream.writeInt(reservedParm2);
			outDataStream.writeInt(measMode);
			outDataStream.writeInt(measItemNum);
			outDataStream.writeInt(measItem);
			outDataStream.writeInt(measCount);
			outDataStream.writeInt(measInterval);
//			outDataStream.writeBytes(doctorID);
			outDataStream.writeInt(doctorID);
			outDataStream.writeInt(patientID);
			outDataStream.writeInt(reservedParm3);
			outDataStream.writeShort(msgEnd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
	  
	
		   
		                            
}
