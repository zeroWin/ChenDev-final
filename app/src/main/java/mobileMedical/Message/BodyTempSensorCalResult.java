
	package mobileMedical.Message;

	import java.io.ByteArrayInputStream;
	import java.io.DataInputStream;
	import java.io.IOException;


	import android.os.Parcel;
import android.os.Parcelable;

	public class BodyTempSensorCalResult implements Parcelable {

		private   byte[]  resultBuffer;


		 private float  coldTDegree;
		 private float  hotDegree;
		 private int reservedParm1;
		 private int reservedParm2;
		 private int reservedParm3;

		
		private     ByteArrayInputStream inMesgbuf;  
		protected   DataInputStream inDataStream;


		 
		public BodyTempSensorCalResult(byte[] buff, int index, int size)
		{
			resultBuffer = new byte[size];
			inMesgbuf = new ByteArrayInputStream(resultBuffer);  
			inDataStream = new DataInputStream(inMesgbuf);

			System.arraycopy(buff, index, resultBuffer, 0, size);				
		}
		
		public void GetResultsFromBuff()
		{
	        
			 try {
				 coldTDegree = MessageBase.FloatInvBytes(inDataStream.readFloat());
				 hotDegree = MessageBase.FloatInvBytes(inDataStream.readFloat());
				 reservedParm1 = Integer.reverseBytes(inDataStream.readInt());
				 reservedParm2 = Integer.reverseBytes(inDataStream.readInt());
				 reservedParm3 = Integer.reverseBytes(inDataStream.readInt());
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        
		        
		        
		}
		
		public float GetColdTDegree()
		{
			return coldTDegree;
		}
		
		public float GetHotDegree()
		{
			return hotDegree;
		}
		
		 public BodyTempSensorCalResult(Parcel  source) {  
			 coldTDegree =  source.readFloat();
			 hotDegree =  source.readFloat();
			 reservedParm1 =  source.readInt();
			 reservedParm2 =  source.readInt();
			 reservedParm3 =  source.readInt();
				
		    }  
		 
		 @Override  
		    public int describeContents() {  
		        return 0;  
		    }  
		 
		 @Override  
		    public void writeToParcel(Parcel dest, int flags) {   

				
				dest.writeFloat(coldTDegree);
				dest.writeFloat(hotDegree);
				dest.writeInt(reservedParm1);
				dest.writeInt(reservedParm2);
				dest.writeInt(reservedParm3);
				
		    }  
		  
		    
		    
		  
		    public static final Parcelable.Creator<BodyTempSensorCalResult> CREATOR = new Creator<BodyTempSensorCalResult>() {  
		          
		        @Override  
		        public BodyTempSensorCalResult[] newArray(int size) {  
		            return new BodyTempSensorCalResult[size];  
		        }  
		          

		        @Override  
		        public BodyTempSensorCalResult createFromParcel(Parcel source) {  
		        	BodyTempSensorCalResult bodyTempCalItemResultsParcel = new BodyTempSensorCalResult(source);	        	
		            return bodyTempCalItemResultsParcel;  
		        }  
		    };  

	}
