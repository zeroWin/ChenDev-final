package mobileMedical.Message;

import java.io.ByteArrayInputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.R.bool;
import android.R.integer;

public class InMessage extends MessageBase{

	protected  byte[] inMessageBuff = new byte[2048];
	/*protected  int[] intRetsArray = new int[2048];
	protected  short[] shortRetsArray = new short[2048];
	protected  float[] floatRetsArray = new float[2048];
	
	protected int retIndex = 0;*/
 	protected  int inMessageSize = 0;
	protected  int inMessageIndex = 0;
	protected  boolean  messageError = false;
	private    ByteArrayInputStream inMesgbuf = new ByteArrayInputStream(inMessageBuff);  
	protected  DataInputStream inDataStream = new DataInputStream(inMesgbuf);
	
	
	public void GetInMsg(byte[] inMsg, int inMsgIndex, int inMsgSize)
	{
		inMessageSize = inMsgSize;
		System.arraycopy(inMsg, inMsgIndex, inMessageBuff, 0, inMsgSize);
		
	}
	
	  public void Process()
	  {}
  
	  public boolean GetMessageError()
	  {
		  return messageError;
	  }
	 
	
}
