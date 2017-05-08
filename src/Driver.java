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
	public static ArrayList<Node> tree = new ArrayList<Node>();
	public static String classificationName = new String();
	public static Node root;
	
	public void addNode(Node n)
	{
		tree.add(n);
	}
	
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
	
	public static void main(String[] args)
	{
		parseFile();
		root = new Node(null, attributes, data);
		root.calculateI();
		print();
	}
	
	public static void print()
	{
		printAux(root, "");
	}
	
	public static void printAux(Node node, String padding)
	{
		System.out.println(padding + node);
		if (node.left_no != null)
		{
			System.out.println(padding + "  Yes: ");
			printAux(node.left_no, (padding + "   "));
		}
		if(node.right_yes != null)
		{
			System.out.println(padding + "  No: ");
			printAux(node.right_yes, (padding + "      "));
		}
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
