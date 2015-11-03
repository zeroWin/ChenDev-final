package mobileMedical.Message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;


import android.os.Parcel;
import android.os.Parcelable;

public class SensorInfoResult implements Parcelable {

	private   byte[]  resultBuffer;


	private int sensorTyp;
	private String sensorID;
	private String systemVersion;
	private float remainingPower;
	private float systemload;
	private int sensorState;
	private int wirelessDevice;
	private String wirelessDeviceMAC;
	private String wirelessDeviceProtVers;

	
	private     ByteArrayInputStream inMesgbuf;  
	protected   DataInputStream inDataStream;

	 private byte[] sensorIDByteArray= new byte[MessageInfo.SENSORID_SIZE];
	 private byte[] macByteArray= new byte[MessageInfo.MAC_SIZE];
	 
	public SensorInfoResult(byte[] buff, int index, int size)
	{
		resultBuffer = new byte[size];
		inMesgbuf = new ByteArrayInputStream(resultBuffer);  
		inDataStream = new DataInputStream(inMesgbuf);

		System.arraycopy(buff, index, resultBuffer, 0, size);				
	}
	
	public void GetResultsFromBuff()
	{
        
		 try {
			 sensorTyp = Integer.reverseBytes(inDataStream.readInt());
			 for (int idx = 0; idx < sensorIDByteArray.length; idx++)
				{
					sensorIDByteArray[idx] = inDataStream.readByte();			
				}	
			 sensorID = new String(sensorIDByteArray);
			 
			 for (int idx = 0; idx < sensorIDByteArray.length; idx++)
				{
					sensorIDByteArray[idx] = inDataStream.readByte();			
				}	
			 systemVersion = new String(sensorIDByteArray);
			 
			 remainingPower =   MessageBase.FloatInvBytes(inDataStream.readFloat());
			 systemload =   MessageBase.FloatInvBytes(inDataStream.readFloat());
			 sensorState =  Integer.reverseBytes(inDataStream.readInt());        
			 wirelessDevice =  Integer.reverseBytes(inDataStream.readInt());  
			 
			 for (int idx = 0; idx < macByteArray.length; idx++)
				{
				 macByteArray[idx] = inDataStream.readByte();			
				}	
			 wirelessDeviceMAC = new String(macByteArray);
			 
			 for (int idx = 0; idx < macByteArray.length; idx++)
				{
				 macByteArray[idx] = inDataStream.readByte();			
				}	
			 wirelessDeviceProtVers = new String(macByteArray);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
	        
	        
	}
	
	public int GetSensorType()
	{
		return sensorTyp;
	}
	public String GetSensorID()
	{
		return sensorID;
	}
	
	
	public String GetSystemVersion()
	{
		return systemVersion;
	}
	
	public float GetRemainingPower()
	{
		return remainingPower;
	}
	public float GetSystemload()
	{
		return systemload;
	}
	
	public int GetSensorState()
	{
		return sensorState;
	}
	public int GetWirelessDevice()
	{
		return wirelessDevice;
	}
	public String GetWirelessDeviceMAC()
	{
		return wirelessDeviceMAC;
	}
	public String GetWirelessDeviceProtVers()
	{
		return wirelessDeviceProtVers;
	}
	 public SensorInfoResult(Parcel  source) {  
			sensorTyp =  source.readInt();
			sensorID = source.readString();
			systemVersion = source.readString();
			remainingPower  = source.readFloat();
			systemload = source.readFloat(); 
			sensorState = source.readInt();
			wirelessDevice = source.readInt();
			wirelessDeviceMAC = source.readString();
			wirelessDeviceProtVers = source.readString();
	    }  
	 
	 @Override  
	    public int describeContents() {  
	        return 0;  
	    }  
	 
	 @Override  
	    public void writeToParcel(Parcel dest, int flags) {   

			
			dest.writeInt(sensorTyp);
			dest.writeString(sensorID);
			dest.writeString(systemVersion);
			dest.writeFloat(remainingPower);
			dest.writeFloat(systemload);
			dest.writeInt(sensorState);
			dest.writeInt(wirelessDevice);
			dest.writeString(wirelessDeviceMAC);
			dest.writeString(wirelessDeviceProtVers);
			
	    }  
	  
	    
	    
	  
	    public static final Parcelable.Creator<SensorInfoResult> CREATOR = new Creator<SensorInfoResult>() {  
	          
	        @Override  
	        public SensorInfoResult[] newArray(int size) {  
	            return new SensorInfoResult[size];  
	        }  
	          

	        @Override  
	        public SensorInfoResult createFromParcel(Parcel source) {  
	        	SensorInfoResult sensorInfoItemResultsParcel = new SensorInfoResult(source);	        	
	            return sensorInfoItemResultsParcel;  
	        }  
	    };  

}
