package devDataType.Parameters;



public class ShortParameter extends NumericParameter{

	private Short paraData;
	
	private short value = 0;
	private short maxValue = Short.MAX_VALUE;
	private short minValue = Short.MIN_VALUE;

	
	public void SetValue(short value)
	{
		paraData = new Short(value);
		value = paraData.shortValue(); 
		dataElement = paraData;
	}
	
	public short GetValue()
	{
		return paraData.shortValue();
	}
	
	public void SetMaxValue(short maxValue)
	{
		this.maxValue = maxValue;
	}
	
	public short GetMaxValue()
	{
		return maxValue;
	}
	
	public void SetMinValue(short minValue)
	{
		this.minValue = minValue;
	}
	
	public short GetMinValue()
	{
		return minValue;
	}
	
	private void Constrain()
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
		
		
	}

}
