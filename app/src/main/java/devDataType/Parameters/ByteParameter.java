package devDataType.Parameters;

import devDataType.Parameters.NumericParameter.DataError;

public class ByteParameter extends NumericParameter{

	private byte value = 0;
	private byte maxValue = Byte.MAX_VALUE;
	private byte minValue = Byte.MIN_VALUE;
	private byte defaultValue = 0;
	private byte previousValue = 0;

	private Byte paraData = new Byte(value);
	
	public void SetValue(byte value)
	{
		paraData = Byte.valueOf(value);
		this.value = paraData.byteValue(); 
		dataElement = paraData;

	}
	
	public int GetValue()
	{
		return paraData.byteValue();
	}
	
	public void SetDefaultValue(byte value)
	{
		defaultValue = value; 
	}
	
	public int GetDefaultValue()
	{
		return defaultValue;
	}
	
	public void SetMaxValue(byte maxValue)
	{
		this.maxValue = maxValue;
	}
	
	public int GetMaxValue()
	{
		return maxValue;
	}
	
	public void SetMinValue(byte minValue)
	{
		this.minValue = minValue;
	}
	
	public int GetMinValue()
	{
		return minValue;
	}
	
	private boolean Constrain()
	{
		if (value > maxValue)
		{
			error = DataError.OVER_MAXIMUM;
		}
		else if (value < minValue)
		{
			error = DataError.LESS_MINIMUM;
		}
		else
		{
			error = DataError.NO_ERROR;
		}
		
		if(error != DataError.NO_ERROR)
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
		

}
