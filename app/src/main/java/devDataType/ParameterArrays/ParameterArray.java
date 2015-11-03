package devDataType.ParameterArrays;
import java.util.ArrayList;

import devDataType.Parameters.IntParameter;

import android.R.integer;


public abstract class ParameterArray<T>{
	protected ArrayList<T> paraDataArray;
	
	protected int size;
	
	public void setSize(int size)
	{
		paraDataArray = new ArrayList<T>(size);
		T parameter = createParameter();
		paraDataArray.add(parameter);
		this.size = size;
	}
	
	public int getSize()
	{
		return size;
	}
	
	protected abstract T createParameter();
	
	
	

}
