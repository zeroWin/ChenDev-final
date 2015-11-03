package devDataType.Parameters;

public class CharParameter extends Parameter{
	private char value = 'a';

	private char defaultValue = 'a';
	private char previousValue = 'a';

	private Character paraData = new Character(value);
	
	public void SetValue(char value)
	{
		paraData = Character.valueOf(value);
		this.value = paraData.charValue(); 
		dataElement = paraData;

	}
	
	public int GetValue()
	{
		return paraData.charValue();
	}

}
