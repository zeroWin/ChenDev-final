
package mobileMedical.Message;


import java.io.IOException;


import devDataType.Parameters.*;



public class BodyTempSensorCalMessage extends OutMessage{
	
	
	 private short msgHeader;
	 private short msgEnd;
	 private short msgBuffSize;
	 private int msgType;
	 private int transID;
	 private int sensorNum;
	 private int reservedParm1;
	 private int sensorType;
	 private String sensorID;
	 
	  
	  public BodyTempSensorCalMessage()
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
			outDataStream.writeShort(msgEnd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
	  
	
		   
		                            
}

