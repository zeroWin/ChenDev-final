package mobileMedical.Message;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import android.R.string;
import devDataType.Parameters.*;



public class MeasStopMessage extends OutMessage{
	
	
	 private short msgHeader;
	 private short msgEnd;
	 private short msgBuffSize;
	 private int msgType;
	 private int transID;	
	 private int reservedParm1;	
	 private int  reservedParm2;
	 private int  reservedParm3;

	  
	  public MeasStopMessage()
	  {		
		msgHeader  =  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGHEADER)).GetValue();
		msgEnd = 	  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGEND)).GetValue();
		msgBuffSize = 	  ((ShortParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGBUFFERSIZE)).GetValue();
	    msgType   =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.MSGTYPE)).GetValue();
	    transID=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.TRANSID)).GetValue();
		reservedParm1=  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM1)).GetValue();	
		reservedParm2 =  ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.RESERVEDPARM2)).GetValue();	
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
		 reservedParm1 =  Integer.reverseBytes(reservedParm1); 			
		reservedParm2 = Integer.reverseBytes(reservedParm2); 	
	   reservedParm3 = Integer.reverseBytes(reservedParm3);			
		  
	  }
	  
	  private void AddParmsToOutMsgBuff()
	  {
		  try {
			outDataStream.writeShort(msgHeader);
			outDataStream.writeShort(msgBuffSize);
			outDataStream.writeInt(msgType);
			outDataStream.writeInt(transID);		
			outDataStream.writeInt(reservedParm1);
			outDataStream.writeInt(reservedParm2);
			outDataStream.writeInt(reservedParm3);

			outDataStream.writeShort(msgEnd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
	  
	
		   
		                            
}
