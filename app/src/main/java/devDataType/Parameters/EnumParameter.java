package devDataType.Parameters;

import java.util.EnumMap;

public class EnumParameter<T> extends Parameter{

	private T value;

	private T defaultValue;
	private T previousValue;

	public void SetValue(T value)
	{
		this.value = value;
	
	}
	
    public T GetValue()
    {
    	return this.value;
    }
}
