package mobileMedical.Message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class OutMessage extends MessageBase{
	
//protected static byte[] outMsgBuff = new byte[2048];
	
private    ByteArrayOutputStream outMsgStream = new ByteArrayOutputStream();  
protected  DataOutputStream outDataStream = new DataOutputStream(outMsgStream);

	
	public  byte[] CreaterMessage()
	{		
		return outMsgStream.toByteArray();
	}
	
}
