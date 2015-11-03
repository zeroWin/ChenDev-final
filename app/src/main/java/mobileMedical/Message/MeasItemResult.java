package mobileMedical.Message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import mobileMedical.namespace.BuildConfig;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;



public class MeasItemResult implements Parcelable {

		private int measItemID;
		private float resultIntervalSec;
		private int resultType;
		private int resultNum;
		//public   byte[]  resultBuffer= new byte[1024];
		private   byte[]  resultBuffer;

		private int  buffsize;
		
		
		private char[] charRets;
		private int[] intRets;
		private float[] floatRets;
		private byte[] byteRets;
		
		//private     ByteArrayInputStream inMesgbuf = new ByteArrayInputStream(resultBuffer);  
		//protected   DataInputStream inDataStream = new DataInputStream(inMesgbuf);
		
		private     ByteArrayInputStream inMesgbuf;  
		protected   DataInputStream inDataStream;

		 private static final String TAG = "MeasItemResult";
	/*public MeasItemResult()
		{
			measItemID = 10;
			resultIntervalSec =   11;
			resultType =  110;        
			resultNum =  111;  
			floatRets = new float[1];
			floatRets[0] = 108.9f;
		}*/
		public MeasItemResult(byte[] buff, int index, int size)
		{
			buffsize = size;
			resultBuffer = new byte[size];
			inMesgbuf = new ByteArrayInputStream(resultBuffer);  
			inDataStream = new DataInputStream(inMesgbuf);

			System.arraycopy(buff, index, resultBuffer, 0, size);				
		}
		
		public boolean GetResultsFromBuff()
		{
            boolean error = false;
			 try {
				measItemID = Integer.reverseBytes(inDataStream.readInt());
				// Commented Acai 2014-03-07, Gateway does not support it now. 
				// So its measItemID is wrong.
				/*switch (measItemID)
				{
				case MessageInfo.MM_MI_BODY_TEMPERATURE:
				case MessageInfo.MM_MI_ELECTRO_CARDIOGRAM:
				case MessageInfo.MM_MI_HEART_RATE:
				case MessageInfo.MM_MI_BLOOD_PRESSURE:
				case MessageInfo.MM_MI_BLOOD_SUGAR:
				case MessageInfo.MM_MI_BLOOD_OXYGEN:
				case MessageInfo.MM_MI_BODY_BASE_TEMPERATURE:
				case MessageInfo.MM_MI_PULMONARY_VENTILATION:
					break;
				default:
					error = true;
					Log.e(TAG, "MeasItemResults RECEIVED:" + String.valueOf(measItemID));
					return error;
				}*/
				resultIntervalSec =   MessageBase.FloatInvBytes(inDataStream.readFloat());
				resultType =  Integer.reverseBytes(inDataStream.readInt());        
				resultNum =  Integer.reverseBytes(inDataStream.readInt());
				if(resultNum <=0)
				{
					error = true;
					Log.e(TAG, "MeasItemResults RECEIVED:" + String.valueOf(resultNum)); 
					return error;   
				}
				switch (resultType)
				{
				case MessageInfo.DATATYPE_INT8:
					byteRets = new byte[resultNum];
					for(int idx = 0; idx < resultNum; idx++ )
					{
						byteRets[idx] = inDataStream.readByte();
					}
					break;
				case MessageInfo.DATATYPE_CHAR_UINT16:
					charRets = new char[resultNum];
					for(int idx = 0; idx < resultNum; idx++ )
					{
						charRets[idx] = MessageBase.CharInvBytes(inDataStream.readChar());
					}
					break;
				case MessageInfo.DATATYPE_INT32:
					intRets = new int[resultNum];
					for(int idx = 0; idx < resultNum; idx++ )
					{
						intRets[idx] = Integer.reverseBytes(inDataStream.readInt());
					}
					break;
				case MessageInfo.DATATYPE_REAL32:
					floatRets = new float[resultNum];
					for(int idx = 0; idx < resultNum; idx++ )
					{
						floatRets[idx] = MessageBase.FloatInvBytes(inDataStream.readFloat());
					}
					break;
				default:
					error = true;
					Log.e(TAG, "MeasItemResults RECEIVED:" + String.valueOf(resultType)); 
						break;
						
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       
			if (BuildConfig.DEBUG) {
			Log.i(TAG, "MeasItemResults RECEIVED:" + String.valueOf(resultType) + "," + String.valueOf(resultNum));       
			}
			return error;    

		}
		
		public float[] GetFloatResults()
		{
			return floatRets;
		}
		public int[] GetIntResults()
		{
			return intRets;
		}
		public char[] GetCharResults()
		{
			return charRets;
		}
		public byte[] GetByteResults()
		{
			return byteRets;
		}
		public int GetResultsSize()
		{
			return resultNum;
		}
		public int GetMeasItemID()
		{
			return measItemID;
		}
		
		public int GetResultType()
		{
			return resultType;
		}
		
		 public MeasItemResult(Parcel  source) {  
			 measItemID = source.readInt();
			 resultIntervalSec = source.readFloat();
			 resultType = source.readInt();
			 resultNum = source.readInt();
			 // The source must has one non-null float array, and the float array length is wrote 
			 // to Parcel in writeToParcel
			 byteRets = source.createByteArray();
			 charRets = source.createCharArray();
			 intRets = source.createIntArray();
			 floatRets = source.createFloatArray();
		    }  
		 
		 @Override  
		    public int describeContents() {  
		        return 0;  
		    }  
		 
		 @Override  
		    public void writeToParcel(Parcel dest, int flags) {   

				dest.writeInt(measItemID);
				dest.writeFloat(resultIntervalSec);
				dest.writeInt(resultType);
				dest.writeInt(resultNum);
				dest.writeByteArray(byteRets);
				dest.writeCharArray(charRets);
				// We only use the floatRets, and it must not be null
				dest.writeIntArray(intRets);
				dest.writeFloatArray(floatRets);
				
		    }  
		  
		    
		    
		  
		    public static final Parcelable.Creator<MeasItemResult> CREATOR = new Creator<MeasItemResult>() {  
		          
		        @Override  
		        public MeasItemResult[] newArray(int size) {  
		            return new MeasItemResult[size];  
		        }  
		          

		        @Override  
		        public MeasItemResult createFromParcel(Parcel source) {  
		        	MeasItemResult measItemResultsParcel = new MeasItemResult(source);	        	
		            return measItemResultsParcel;  
		        }  
		    };  


}
