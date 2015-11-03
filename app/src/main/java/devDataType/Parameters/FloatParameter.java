package devDataType.Parameters;

import devDataType.Parameters.NumericParameter.DataError;

public class FloatParameter extends NumericParameter{

	private float value = 0.0f;
	private float maxValue = Float.MAX_VALUE;
	private float minValue = Float.MIN_VALUE;
	private float defaultValue = 0.0f;
	private float previousValue = 0.0f;

	private Float paraData = new Float(value);
	
	public void SetValue(float value)
	{
		paraData = Float.valueOf(value);
		this.value = paraData.floatValue(); 
		dataElement = paraData;

	}
	
	public float GetValue()
	{
		return paraData.floatValue();
	}
	
	public void SetDefaultValue(float value)
	{
		this.defaultValue = value; 
	}
	
	public float GetDefaultValue()
	{
		return defaultValue;
	}
	
	public void SetMaxValue(float maxValue)
	{
		this.maxValue = maxValue;
	}
	
	public float GetMaxValue()
	{
		return maxValue;
	}
	
	public void SetMinValue(float minValue)
	{
		this.minValue = minValue;
	}
	
	public float GetMinValue()
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
