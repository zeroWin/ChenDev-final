package devDataType.Parameters;

import android.R.string;

public class StringParameter extends Parameter{
	private String value = "";

	private String defaultValue = "";
	private String previousValue = "";

	private StringBuilder paraData = new StringBuilder(value);
	
	public void SetValue(String value)
	{
		paraData.setLength(value.length());
		paraData.replace(0, value.length()-1 , value);
		// It is very interesting. using paraData.ToString() will get the string ended with 0
		// Using paraData.substring(0, value.length()-1) will get the string without the last character/char.
		this.value = paraData.substring(0, value.length());
		dataElement = paraData;

	}
	
	public String GetValue()
	{
		return paraData.substring(0, value.length());
	}
	public byte[] GetBytesValue()
	{				
		return paraData.substring(0, value.length()).getBytes();
	}
}
