package mobileMedical.Message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GWInfoResult  implements Parcelable {
	private   byte[]  resultBuffer;
	private     ByteArrayInputStream inMesgbuf;  
	protected   DataInputStream inDataStream;
	private byte[] mobMedGWIDByteArray= new byte[MessageInfo.SENSORID_SIZE];
	private byte[] macByteArray= new byte[MessageInfo.MAC_SIZE];
	 
	 private int transID;
	 private String mobMedGWID;
	 private String systemVersion;
	 private int memorySize;
	 private int sdCapacity;
	 private float remainingPower;
	 private float systemload;
	 private int mobMedGWState;
	 private int wirelessDevice;
	 private String wirelessDeviceMAC;
	 private String wirelessDeviceProtVers;
	 private int maximumSensorNumber;
	 private int connectedSensorNumber;
	 
	public GWInfoResult(byte[] buff, int index, int size)
	{
		resultBuffer = new byte[size];
		inMesgbuf = new ByteArrayInputStream(resultBuffer);  
		inDataStream = new DataInputStream(inMesgbuf);

		System.arraycopy(buff, index, resultBuffer, 0, size);				
	}

	public void GetResultsFromBuff()
	{
		 try {
			 
				transID = Integer.reverseBytes(inDataStream.readInt());
				for (int idx = 0; idx < mobMedGWIDByteArray.length; idx++)
				{
					mobMedGWIDByteArray[idx] = inDataStream.readByte();			
				}	
				mobMedGWID = new String(mobMedGWIDByteArray);
				
				for (int idx = 0; idx < mobMedGWIDByteArray.length; idx++)
				{
					mobMedGWIDByteArray[idx] = inDataStream.readByte();			
				}	
				systemVersion = new String(mobMedGWIDByteArray);

				
				
				memorySize = Integer.reverseBytes(inDataStream.readInt());
				sdCapacity = Integer.reverseBytes(inDataStream.readInt());
				remainingPower = MessageBase.FloatInvBytes(inDataStream.readFloat());
				systemload = MessageBase.FloatInvBytes(inDataStream.readFloat());
				mobMedGWState = Integer.reverseBytes(inDataStream.readInt());
				wirelessDevice = Integer.reverseBytes(inDataStream.readInt());
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
				
				maximumSensorNumber = Integer.reverseBytes(inDataStream.readInt());
				connectedSensorNumber = Integer.reverseBytes(inDataStream.readInt());
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
						
				
	}
	
	 public String GetMobMedGWID()
	 {
		 return mobMedGWID;
	 }
	 public String GetSystemVersion()
	 {
		 return systemVersion;
	 }
	 public int GetMemorySize()
	 {
		 return memorySize;
	 }
	 public int GetSDCapacity()
	 {
		 return sdCapacity;
	 }
	 public float GetRemainingPower()
	 {
		 return remainingPower;
	 }
	 public float GetSystemload()
	 {
		 return systemload;
	 }
	 public int GetMobMedGWState()
	 {
		 return mobMedGWState;
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
	 public int GetMaximumSensorNumber()
	 {
		 return maximumSensorNumber;
	 }
	 public int GetConnectedSensorNumber()
	 {
		 return connectedSensorNumber;
	 }
	 
	 
	 public GWInfoResult(Parcel  source) {  
			
			transID =  source.readInt();
			mobMedGWID = source.readString();
			systemVersion = source.readString();
			memorySize =  source.readInt();
			sdCapacity = source.readInt();
			remainingPower  = source.readFloat();
			systemload = source.readFloat(); 
			mobMedGWState = source.readInt();
			wirelessDevice = source.readInt();
			wirelessDeviceMAC = source.readString();
			wirelessDeviceProtVers = source.readString();
			maximumSensorNumber  = source.readInt();
			connectedSensorNumber = source.readInt();
	    }  
	 
	 @Override  
	    public int describeContents() {  
	        return 0;  
	    }  
	 
	 @Override  
	    public void writeToParcel(Parcel dest, int flags) {   
		    dest.writeInt(transID);
		    dest.writeString(mobMedGWID);
		    dest.writeString(systemVersion);
			dest.writeInt(memorySize);
			dest.writeInt(sdCapacity);
			dest.writeFloat(remainingPower);
			dest.writeFloat(systemload); 
			dest.writeInt(mobMedGWState);
			dest.writeInt(wirelessDevice);
			dest.writeString(wirelessDeviceMAC);
			dest.writeString(wirelessDeviceProtVers);
			dest.writeInt(maximumSensorNumber);
			dest.writeInt(connectedSensorNumber);								
	    }  
	  
	    
	    
	  
	    public static final Parcelable.Creator<GWInfoResult> CREATOR = new Creator<GWInfoResult>() {  
	          
	        @Override  
	        public GWInfoResult[] newArray(int size) {  
	            return new GWInfoResult[size];  
	        }  
	          

	        @Override  
	        public GWInfoResult createFromParcel(Parcel source) {  
	        	GWInfoResult gwInfoResultsParcel = new GWInfoResult(source);	        	
	            return gwInfoResultsParcel;  
	        }  
	    };  
}
