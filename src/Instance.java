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
		
//		printData();
	}
	
	public int getColumn(int column)
	{
		return data.get(column);
	}
	
	public int getColumns(int column)
	{
		if ((data.get(column) == 1) && (data.get(data.size() - 1) == 1))
		{
//			System.out.println("Both True");
			return 1;
		}
		else if ((data.get(column) == 0) && (data.get(data.size() - 1) == 1))
		{
//			System.out.println("Neg Pos");
			return 2;
		}
		else if ((data.get(column) == 1) && (data.get(data.size() - 1) == 0))
		{
//			System.out.println("Pos Neg");
			return 3;
		}
		else if((data.get(column) == 0) && (data.get(data.size() - 1) == 0))
		{
//			System.out.println("Both Neg");
			return 4;
		}
		
		return 0;
	}
	
	public void removeColumn(int i)
	{
		data.remove(i);
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
