import java.util.ArrayList;

public class Node {

	private String padding = new String();
	private ArrayList<String> attributes = new ArrayList<String>();
	private ArrayList<Instance> data = new ArrayList<Instance>();
//	private ArrayList<Instance> data2 = new ArrayList<Instance>();
	private String classificationName = Driver.classificationName;
	private Node parent;
	private String attribute;
	private String label;
	public Node left_yes;
	public Node right_no;
	
	
	public Node(Node parent, String padding, ArrayList<String> attributes, ArrayList<Instance> data)
	{
		System.out.println();
		if(data.size() == 0)
		{
			return;
		}
		else if (attributesSame()) //This is useful so that it doesn't say 'Smelly' at the end. Need to implement to check if the attributes are the same, meaning not worth our time
		{
			this.attribute = attributes.get(0);
		}
		else if (attributes.size() == 0)
		{
			return;
		}
		else
		{
			
		}
		/*
		 * if (there are no examples)
		 * 		return default decision
		 * else if (all examples are same classification)
		 * 		return that classification
		 * else if(attributes is empty)
		 * 		return the classification of the majority of examples
		 * else
		 * 		choose an attribute from attributes and call it best
		 * 		create a new decision tree with root test best and call it tree
		 * 		determine the classification of the majority of examples andcall it m 
		 * 		for each value vi of best do 
		 * 			examplesi is the set of elements from examples with best = vi
		 * 			subtree = DECISION-TREE-LEARNING(examplesi, attribtutes - best, m)
		 * 			add a branch to tree with level vi and subtree subtree
		 * 		return tree
		 */

		this.padding = padding;
		this.attributes = new ArrayList<String>(attributes);
		this.data = new ArrayList<Instance>(data);
		this.parent = parent;
	}
	
	public boolean attributesSame()
	{
		if (data.size() == 2)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void calculateI()
	{
		int total = 0, positives = 0, negatives = 0;
		for(Instance s : data)
		{
			if (s.getColumn(attributes.size()) == 1)
			{
				positives ++;
			}
			else
			{
				negatives ++;
			}
			
			total ++;
		}
		
		System.out.println("Postive: " + positives + " Negative: " + negatives + " Total: " + total);
		
		double pValue, nValue, pFraction, nFraction;
		pFraction = (double) positives / total;
		nFraction = (double) negatives / total;
		pValue = Math.log(pFraction) / Math.log(2);
		nValue = Math.log(nFraction) / Math.log(2);
		
		double value = -(pFraction * pValue) - (nFraction * nValue);
		System.out.println("P Value: " + pValue + " N Value " + nValue);
		System.out.println("I Value: " + value);
		
		calculateIa(value);
	}
	
	public void updateData(int column, int direction){
	// Creates a new table to look through based on the attribute previously selected
	// The instance is the row in the table - the s.getColumn(n) looks at the value in the row s at column n
		if(direction == 1){

			for(Instance s : data)
			{
				if(s.getColumn(column) == 1)
				{
//					data2.add(s);
				}
			}
		}
		else {
			for(Instance s : data)
			{
				if(s.getColumn(column) == 0)
				{
//					data2.add(s);
				}
			}
		}
//		System.out.println(data2.size());
	}
	
	public void calculateIa(double iValue)
	{
		// Calculates the Ia value for an attribute
		int column = 0, gainColumn = 0;
		double iaValue, temp, gain = 0.0;
		boolean leftDone = false, rightDone = false;
		
		while (column < attributes.size())
		{
			int allPositive = 0, allNegative = 0, posNeg = 0, negPos = 0, total = 0;

			for(Instance s : data)
			{
				switch(s.getColumns(column))
				{
					case 1: 
						allPositive++;
						break;
					case 2: 
						negPos++;
						break;
					case 3: 
						posNeg++;
						break;
					case 4: 
						allNegative++;
						break;
					default: 
						break;
				}
				
				total++;
			}
//			System.out.println("\n" + attributes.get(column));
//			System.out.println("total: " + total);
//			System.out.println("All Pos: " + allPositive + " Neg Pos: " + negPos + " Pos Neg: " + posNeg + " All Neg: " + allNegative);
			
			iaValue = calculateInformationContent(allPositive, allNegative, posNeg, negPos, total);
			temp = calculateGain(iValue, iaValue);
//			System.out.println("Gain: " + temp);
			if (column == 0)
			{
				gain = calculateGain(iValue, iaValue);
			}
			
			if (temp > gain)
			{
				gain = temp;
				gainColumn = column;
				if(posNeg == 0)
				{
					leftDone = true;
					rightDone = false;
				}
				else if (negPos == 0)
				{
					rightDone = true;
					leftDone = false;
				}
				else
				{
					leftDone = false;
					rightDone = false;
				}
			}
			
			column++;
		}
		this.attribute = attributes.get(gainColumn);
		
//		System.out.println("\nGain: " + gain + " gainColumn: " + gainColumn + " Attribute: " + attributes.get(gainColumn));
		
		attributes.remove(gainColumn);
		//Removes the attribute that was selected and updates the Data to create a new table to evaluate
		// Continues evaluation one direction until there are no more children; then moves to the other direction
		for(Instance i : data)
		{
			i.removeColumn(gainColumn);
		}
		
		System.out.println(toString());
		
		if (!attributes.isEmpty())
		{
			if (!leftDone)
			{
				System.out.println(attribute + " Left: ");
				Node child = new Node(this, "	", attributes, data);
				child.calculateI();
				left_yes = child;
			}
			else if (leftDone)
			{
				System.out.println(attribute + " Leftd: ");
				System.out.println("We are done. what now? \n");
			}
			
			if (!rightDone)
			{
				System.out.println(attribute + " Right: ");
				Node child = new Node(this, "	", attributes, data);
				child.calculateI();
				right_no = child;
			}
			else if (rightDone)
			{
				System.out.println(attribute + " Rightd: ");
				System.out.println("We are done. What now? ");
			}
		}		
	}

	public double calculateInformationContent(int allPositive, int allNegative, int posNeg, int negPos, int total)
	{
		// Creates the Information content equation
		double positives, negatives, posFraction = 0, negFraction = 0, negPosFraction = 0, posNegFraction = 0;
		positives = allPositive + posNeg;
		negatives = allNegative + negPos;
		posFraction = allPositive / positives;
		negFraction = allNegative / negatives;
		negPosFraction = negPos / positives;
		posNegFraction = posNeg / negatives;
		
		double posLog, negPosLog, negLog, posNegLog;
		
		if (posFraction == 0)
		{ 
			posLog = 0; 
		}
		else 
		{
			posLog = (-posFraction * (Math.log(posFraction) / Math.log(2)));
		}
		
		if (negPosFraction == 0) 
		{ 
			negPosLog = 0; 
		}
		else
		{
			negPosLog = (-negPosFraction * (Math.log(negPosFraction)/ Math.log(2)));
		}

		if(negFraction == 0)
		{
			negLog = 0;
		}
		else 
		{
			negLog = (-negFraction * (Math.log(negFraction) / Math.log(2)));
		}

		if(posNegFraction == 0)
		{
			posNegLog = 0;
		}
		else
		{
			posNegLog = (-posNegFraction * (Math.log(posNegFraction) / Math.log(2)));
		}

		double positiveSide = positives/total * (posLog + negPosLog);
		double negativeSide = negatives/total * (negLog + posNegLog);

		double value = positiveSide + negativeSide;
//		System.out.println(value);
		return value;
	}
	
	public double calculateGain(double iValue, double iaValue)
	{
		// Caculates the gain of the attributes
		return iValue - iaValue;
	}
	
	public void addLeftNode()
	{
		ArrayList<Instance> newData = new ArrayList<Instance>(data);
		
		for(Instance s : newData)
		{
			if (s.getColumn(1) == 0)
			{
				
			}
		}
	}
	
	public String toString()
	{
		String s =  attribute ;
		
		return s;
	}
}
