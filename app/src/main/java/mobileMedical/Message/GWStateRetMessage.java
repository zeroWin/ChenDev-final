package mobileMedical.Message;

import java.io.IOException;

public class GWStateRetMessage extends InMessage{
	

	 private SensorInfoResult[] sensorInfoItems;
	 private GWInfoResult gwInfo;

     private int connectedSensorNumber;

	 
	 
	 
	 private int bufferIdx = MessageInfo.GW_STATE_QUERY_RESULT_PARMS_SIZE;
	 private final int itemRltSizeInByte = MessageInfo.SENSORINFO_RESULT_SIZE;
	 private void GetSensorInfoResult()
	 {
		 connectedSensorNumber = gwInfo.GetConnectedSensorNumber();
		 sensorInfoItems = new SensorInfoResult[connectedSensorNumber]; 
		 int sensorInfoItemBufferIdx = 0;
		 for (int idx = 0; idx < connectedSensorNumber; idx++)
		 {
			 
			 sensorInfoItemBufferIdx = bufferIdx + itemRltSizeInByte * idx;

			 sensorInfoItems[idx] = new SensorInfoResult(inMessageBuff,sensorInfoItemBufferIdx, itemRltSizeInByte );
			 sensorInfoItems[idx].GetResultsFromBuff();			 
		 }
	 }
	 private void GetGWInfoResult()
	 {
		 gwInfo = new GWInfoResult(inMessageBuff,0,bufferIdx);	
		 gwInfo.GetResultsFromBuff();
	 }
	 @Override
	 public void Process()
	  {		 
		    GetGWInfoResult();		    
			GetSensorInfoResult();						 				 
	  }
	 
	 
	 public SensorInfoResult[] GetSensorInfoResults()
	 {
		 return sensorInfoItems;
	 }
	 
	 public GWInfoResult GetGWInfoResults()
	 {
		 return gwInfo;
	 }
	 
	 public Object GetQueryResults()
	 {
		 return this;
	 }
}
