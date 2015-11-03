package mobileMedical.Message;

import android.R.string;

public abstract class MessageBase {
	
	public int messageBuffSize;
	protected byte[] messageBuff;
	//public abstract byte[] createrMessage();
	
	public void ClearMessageBuff()
	{
		if (messageBuff != null)
		{
		for(int idx = 0; idx <messageBuffSize; idx++)
		{
			messageBuff[idx] = 0x0;
		}
		}
	}
	
	public static void ReverseArray(byte[] a, int left, int right) {  
	    if (left >= right)  
	      return;  
	    byte temp;  
	    temp = a[left];  
	    a[left] = a[right];  
	    a[right] = temp;  
	    ReverseArray(a, ++left, --right);  
	  }  
	
	public static void StringToInvBytes(byte[] abyte,  int index, String i)
	{
		abyte = i.getBytes();
		ReverseArray(abyte, 0, abyte.length -1);
	}
	
	
	public static void ShortToInvBytes(byte[] abyte,  int index, short i) {
		// The bytes is inverted. 
	    abyte[index] = (byte) (0xff & i);
	    abyte[index+1] = (byte) ((0xff00 & i) >> 8);
	}
	public static void IntToInvBytes(byte[] abyte,  int index, int i) {
		// The bytes is inverted. 
	    abyte[index] = (byte) (0xff & i);
	    abyte[index+1] = (byte) ((0xff00 & i) >> 8);
	    abyte[index+2] = (byte) ((0xff0000 & i) >> 16);
	    abyte[index+3] = (byte) ((0xff000000 & i) >> 24);
	}

	public static void FloatToInvBytes(byte[] abyte,  int index, float f) {
		int i = Float.floatToIntBits(f);
	    abyte[index] = (byte) (0xff & i);
	    abyte[index+1] = (byte) ((0xff00 & i) >> 8);
	    abyte[index+2] = (byte) ((0xff0000 & i) >> 16);
	    abyte[index+3] = (byte) ((0xff000000 & i) >> 24);  
	}

	public static short InvBytesToShort(byte[] bytes) {
	    short addr = (short) (bytes[0] & 0xFF);
	    addr |= ((bytes[1] << 8) & 0xFF00);
	    return addr;
	}
	public static short BytesToShort(byte[] bytes) {
	    short addr = (short) (bytes[1] & 0xFF);
	    addr |= ((bytes[0] << 8) & 0xFF00);
	    return addr;
	}
	
	public static int InvBytesToInt(byte[] bytes) {
	    int addr = bytes[0] & 0xFF;
	    addr |= ((bytes[1] << 8) & 0xFF00);
	    addr |= ((bytes[2] << 16) & 0xFF0000);
	    addr |= ((bytes[3] << 24) & 0xFF000000);
	    return addr;
	}
	public static int BytesToInt(byte[] bytes) {
	    int addr = bytes[3] & 0xFF;
	    addr |= ((bytes[2] << 8) & 0xFF00);
	    addr |= ((bytes[1] << 16) & 0xFF0000);
	    addr |= ((bytes[0] << 24) & 0xFF000000);
	    return addr;
	}
	public  static float InvBytesToFloat(byte[] bytes) {
	    int addr = bytes[0] & 0xFF;
	    addr |= ((bytes[1] << 8) & 0xFF00);
	    addr |= ((bytes[2] << 16) & 0xFF0000);
	    addr |= ((bytes[3] << 24) & 0xFF000000);
	    //return Float.intBitsToFloat(addr); 
	    return (float)addr;
	}
	
	public  static float BytesToFloat(byte[] bytes) {
	    int addr = bytes[3] & 0xFF;
	    addr |= ((bytes[2] << 8) & 0xFF00);
	    addr |= ((bytes[1] << 16) & 0xFF0000);
	    addr |= ((bytes[0] << 24) & 0xFF000000);
	    return Float.intBitsToFloat(addr); 
	    //return (float)addr;
	}
	
	public static String InvBytesToString(byte[] bytes)
	{
		byte[] tmpBytes = new byte[bytes.length];
		System.arraycopy(bytes, 0, tmpBytes, 0, bytes.length);
		ReverseArray(tmpBytes, 0, tmpBytes.length -1);
		String addr = new String(tmpBytes);
		return addr;
	}
	
	public static String BytesToString(byte[] bytes)
	{
		byte[] tmpBytes = new byte[bytes.length];
		System.arraycopy(bytes, 0, tmpBytes, 0, bytes.length);
		String addr = new String(tmpBytes);
		return addr;
	}
	
	public static String StringInvBytes(String i)
	{
		byte[] abyte = i.getBytes();
		//ReverseArray(abyte, 0, abyte.length -1);
		String addr = InvBytesToString(abyte);
		return addr;
		
	}
	public static float FloatInvBytes(float i)
	{
		int addr = Float.floatToIntBits(i);
		addr = Integer.reverseBytes(addr);
		return Float.intBitsToFloat(addr); 
		// return (float)addr;
	}
	
	public static char CharInvBytes(char i)
	{
		short addr = (short)i;
		addr = Short.reverseBytes(addr);
		return (char) addr; 
		// return (float)addr;
	}
	

}
