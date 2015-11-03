package mobileMedical.Message;

import java.io.IOException;

import android.util.Log;



public class BodyTempSensorCalRetMessage extends InMessage{

	 private int transID;
	 private int sensorNum;
	 private int reservedParm1;
	 private int sensorType = -1;
	 private String sensorID;



	 
	 private BodyTempSensorCalResult bodyTempCalRet;
	 
	 private byte[] sensorIDByteArray= new byte[MessageInfo.SENSORID_SIZE];
	 
	 private int bufferIdx = MessageInfo.BODYTEMP_CAL_RESULT_PRE_PARMS_SIZE;
	 private int bodyTempRetSize = MessageInfo.BODYTEMP_CAL_RESULT_PARMS_SIZE;
	 private static final String TAG = "BodyTempCalRetMessage";
	 private void GetBodyTempCalResult()
	 {
		 
		 bodyTempCalRet = new BodyTempSensorCalResult(inMessageBuff,bufferIdx,bodyTempRetSize);	
		 bodyTempCalRet.GetResultsFromBuff();
	 }
	 
	 @Override
	 public void Process()
	  {
		 
		 try {
		/*	msgHeader = inDataStream.readShort();
			msgBuffSize = inDataStream.readShort();
			msgType = inDataStream.readInt();*/
			transID = Integer.reverseBytes(inDataStream.readInt());
			sensorNum = Integer.reverseBytes(inDataStream.readInt());
			reservedParm1 = Integer.reverseBytes(inDataStream.readInt());
			sensorType = Integer.reverseBytes(inDataStream.readInt());
			for (int idx = 0; idx < sensorIDByteArray.length; idx++)
			{
				sensorIDByteArray[sensorIDByteArray.length - idx -1] = inDataStream.readByte();			
			}			
		
			sensorID = new String(sensorIDByteArray);
			
			GetBodyTempCalResult();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 				 
	  }
	 
	 
	 public BodyTempSensorCalResult GetBodyTempCalResults()
	 {
		 return bodyTempCalRet;
	 }
	 
	 public int GetSensorType()
	 {
		 return sensorType;
	 }
	 
	
}
