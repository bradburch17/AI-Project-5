import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Driver class for decision trees
 * 
 * @author Brad Burch and Katherine Martin 
 */
public class Driver {

	public static ArrayList<String> attributes = new ArrayList<String>();
	public static ArrayList<Instance> data = new ArrayList<Instance>();
	public static String classificationName = new String();
	public static Node root = new Node();
	
	public static void parseFile()
	{
		String fileName = new String();
		Scanner reader = new Scanner(System.in);
		
		System.out.print("Please enter a file: ");
		fileName = reader.next();
		
		try
		{
			File file = new File(fileName);
			Scanner scanner = new Scanner(file);
			boolean isData = false;
			
			while(scanner.hasNext())
			{
				String line = scanner.nextLine();
				
				if(line.isEmpty())
				{
					isData = true;
					classificationName = attributes.get(attributes.size() - 1);
					attributes.remove(attributes.size() - 1);
				}
				
				if (!isData)
				{
					attributes.add(line);
				}
				else if (isData && !line.isEmpty())
				{
					Instance instance = new Instance(line);
					data.add(instance);
				}
			}
			
//			printAttributes();
//			printData();
//			System.out.println(classificationName);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Unable to open the file " + fileName + ".");
		}
		catch(IOException e)
		{
			System.out.println("Could not read from " + fileName + ".");
		}
	}
	
	public static void calculateI()
	{
		int total = 0, positives = 0, negatives = 0;
		for(Instance s : data)
		{
			if (s.getColumn(attributes.size()) == 1)
			{
				positives += 1;
			}
			else
			{
				negatives += 1;
			}
			
			total += 1;
		}
		
		System.out.println("Postive: " + positives + " Negative: " + negatives + " Total: " + total);
		
		double pValue, nValue, pFraction, nFraction;
		pFraction = (double) positives / total;
		nFraction = (double) negatives / total;
		pValue = Math.log(pFraction) / Math.log(2);
		nValue = Math.log(nFraction) / Math.log(2);
		
		double value = - (pFraction * pValue) - (nFraction - nValue);
		System.out.println("P Value: " + pValue + " N Value " + nValue);
		System.out.println("I Value: " + value);
	}
	
	public static void main(String[] args)
	{
		parseFile();
		calculateI();
	}
	
	public static void printAttributes()
	{
		for(String s : attributes)
		{
			System.out.println(s);
		}
	}
	
	public static void printData()
	{
		for(Instance s : data)
		{
			s.printData();
		}
	}
}
