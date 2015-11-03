package mobileMedical.Message;

import java.io.IOException;

import mobileMedical.namespace.BuildConfig;

import android.util.Log;



public class SingleSensorMeasRetMessage extends InMessage{
/*	 private short msgHeader;
	 private short msgEnd;
	 private short msgBuffSize;
	 private int msgType;*/
	 private int transID;
	 private int sensorNum;
	 private int reservedParm1;
	 private int sensorType = -1;
	 private String sensorID;
	 private int  doctorID;
	 private int  patientID;
	 private int measItemNum;
	 private int itemRltSizeInByte;

	 private short measStartTimeYear;
	 private short measStartTimeMonth;
	 private short measStartTimeday;
	 private float measStartTimeSec;
	 
	 private MeasItemResult[] measItems;
	 
	 private byte[] sensorIDByteArray= new byte[MessageInfo.SENSORID_SIZE];
	 
	 private int bufferIdx = MessageInfo.SINGLE_MEAS_RESULT_PARMS_SIZE;
	 private static final String TAG = "SingleSensorMeasRetMessage";
	 private void GetMeasItemResult()
	 {
		 measItems = new MeasItemResult[measItemNum]; 
		 int measItemBufferIdx = 0;
		 if (BuildConfig.DEBUG) {
		 Log.i(TAG, String.valueOf(measItemNum));
		 }
		 for (int idx = 0; idx < measItemNum; idx++)
		 {
			 
			 measItemBufferIdx = bufferIdx + itemRltSizeInByte * idx;
			// MeasItemResult temp = new MeasItemResult(inMessageBuff,measItemBufferIdx, itemRltSizeInByte );
			// temp.GetResultsFromBuff();
			 measItems[idx] = new MeasItemResult(inMessageBuff,measItemBufferIdx, itemRltSizeInByte );
			 messageError = measItems[idx].GetResultsFromBuff();
			 if (!messageError)
			 {
				 break;
			 }
		 }
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
			doctorID = Integer.reverseBytes(inDataStream.readInt());
			patientID = Integer.reverseBytes(inDataStream.readInt());
			measItemNum = Integer.reverseBytes(inDataStream.readInt());
			itemRltSizeInByte = Integer.reverseBytes(inDataStream.readInt());
			measStartTimeYear = Short.reverseBytes(inDataStream.readShort());
			measStartTimeMonth = Short.reverseBytes(inDataStream.readShort());
			measStartTimeday = Short.reverseBytes(inDataStream.readShort());
			measStartTimeSec = FloatInvBytes(inDataStream.readFloat());
		
			// We may need it 
			// sensorID = new String(sensorIDByteArray, "UTF-8");
			sensorID = new String(sensorIDByteArray);
			
			GetMeasItemResult();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 				 
	  }
	 
	 
	 public MeasItemResult[] GetMeasItemResults()
	 {
		 return measItems;
	 }
	 
	 public int GetSensorType()
	 {
		 return sensorType;
	 }
	 
	 
}
