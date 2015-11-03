package devDataType.Parameters;

import java.lang.reflect.Type;

import android.R.integer;



public class IntParameter extends  NumericParameter{
	
   
	

	private int value = 0;
	private int maxValue = Integer.MAX_VALUE;
	private int minValue = Integer.MIN_VALUE;
	private int defaultValue = 0;
	private int previousValue = 0;

	private Integer paraData = new Integer(value);  ;
	
	public void SetValue(int value)
	{
		paraData = Integer.valueOf(value);
		this.value = paraData.intValue(); 
		dataElement = paraData;
	

	}
	
	public int GetValue()
	{
		return paraData.intValue();
	}
	
	public void SetDefaultValue(int value)
	{
		defaultValue = value; 
	}
	
	public int GetDefaultValue()
	{
		return defaultValue;
	}
	
	public void SetMaxValue(int maxValue)
	{
		this.maxValue = maxValue;
	}
	
	public int GetMaxValue()
	{
		return maxValue;
	}
	
	public void SetMinValue(int minValue)
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
