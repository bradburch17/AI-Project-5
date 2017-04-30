import java.util.ArrayList;

public class Instance {

	public String dataString;
	public ArrayList<Integer> data = new ArrayList<Integer>();
	
	public Instance(String dataString)
	{
		this.dataString = dataString;
		char[] array = dataString.toCharArray();
		
		for(int i = 0; i < array.length; i++)
		{
			if (array[i] != ',')
			{
				data.add( Integer.parseInt(String.valueOf(array[i])) );
			}
		}
	}
	
	public void calculate()
	{
		
	}
	
	public void informationGain()
	{
		
	}
	
	public int getColumn(int column)
	{
		return data.get(column);
	}
	
	public String toString()
	{
		return dataString;
	}
	
	public void printData()
	{
		for(int s : data)
		{
			System.out.print(s + " ");
		}
		System.out.println();
	}
}
