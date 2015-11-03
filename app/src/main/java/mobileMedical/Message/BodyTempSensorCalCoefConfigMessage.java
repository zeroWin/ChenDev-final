package mobileMedical.Message;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import android.R.string;
import devDataType.Parameters.*;



public class BodyTempSensorCalCoefConfigMessage extends OutMessage{
	
	
	 private short msgHeader;
	 private short msgEnd;
	 private short msgBuffSize;
	 private int msgType;
	 private int transID;
	 private int sensorNum;
	 private int reservedParm1;
	 private int sensorType;
	 private String sensorID;
	 private float calCoeffA;
	 private float calCoeffB;
	 private float calCoeffC;
	 private int reservedParm2;	 
	 private int  reservedParm3;
	 private int  reservedParm4;
	  
	  public BodyTempSensorCalCoefConfigMessage()
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
		calCoeffA = ((FloatParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.BodyTempCalCoeffA)).GetValue();
		calCoeffB = ((FloatParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.BodyTempCalCoeffB)).GetValue();
		calCoeffC = ((FloatParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.BodyTempCalCoeffC)).GetValue();
		reservedParm2 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM2)).GetValue();		  		  
		reservedParm3 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM3)).GetValue();		  		  
		reservedParm4 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM4)).GetValue();		  		  

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
		calCoeffA = FloatInvBytes(calCoeffA); 
		calCoeffB = FloatInvBytes(calCoeffB); 	
		calCoeffC = FloatInvBytes(calCoeffC); 	
		reservedParm2 = Integer.reverseBytes(reservedParm2); 	 	
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
			outDataStream.writeInt(sensorType);
			outDataStream.writeBytes(sensorID);
			outDataStream.writeFloat(calCoeffA);
			outDataStream.writeFloat(calCoeffB);
			outDataStream.writeFloat(calCoeffC);
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
