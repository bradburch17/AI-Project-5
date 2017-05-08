import java.util.ArrayList;

public class Node {

	private String padding = new String();
	private ArrayList<String> attributes = new ArrayList<String>();
	private ArrayList<Instance> data = new ArrayList<Instance>();
	private String classificationName = Driver.classificationName;
	private Node parent;
	private String attribute;
	private String label = new String();
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
	
	public Node(String attribute, boolean neg)
	{
		this.attribute = attribute;
		if(neg)
		{
			this.label = " - False";
		}
		else
		{
			this.label = " - True";
		}
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
		
//		System.out.println("Postive: " + positives + " Negative: " + negatives + " Total: " + total);
		
		double pValue, nValue, pFraction, nFraction;
		pFraction = (double) positives / total;
		nFraction = (double) negatives / total;
		pValue = Math.log(pFraction) / Math.log(2);
		nValue = Math.log(nFraction) / Math.log(2);
		
		double value = -(pFraction * pValue) - (nFraction * nValue);
//		System.out.println("P Value: " + pValue + " N Value " + nValue);
//		System.out.println("I Value: " + value);
		
		calculateIa(value);
	}
	
	public ArrayList<Instance> updateData(int column, boolean left, boolean right){
	// Creates a new table to look through based on the attribute previously selected
	// The instance is the row in the table - the s.getColumn(n) looks at the value in the row s at column n
		ArrayList<Instance> temp = new ArrayList<Instance>(data);
		
		for(Instance s : data)
		{
			if(left && s.getColumn(column) == 1)
			{
				temp.remove(s);
			}
			
			if(right && s.getColumn(column) == 0)
			{
				temp.remove(s);
			}
		}

		System.out.println("Temp size: " + temp.size());
		
		for(Instance q : temp)
		{
			q.printData();
		}

		return temp;
	}
	
	public void calculateIa(double iValue)
	{
		// Calculates the Ia value for an attribute
		int column = 0, gainColumn = 0;
		double iaValue, temp, gain = 0.0;
		boolean leftDone = false, rightDone = false, neg = false;
		
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

			System.out.println("\n" + attributes.get(column) + " All Pos: " + allPositive + " Neg Pos: " + negPos + " Pos Neg: " + posNeg + " All Neg: " + allNegative);
			
			iaValue = calculateInformationContent(allPositive, allNegative, posNeg, negPos, total);
			temp = calculateGain(iValue, iaValue);
			
			System.out.println("Temp: " + temp + " Gain: " + gain);
			
			if (temp > gain || column == 0)
			{
				gain = temp;
				gainColumn = column;
				if(posNeg == 0 || allPositive == 0)
				{
					leftDone = true;
					rightDone = false;
					neg = false;
				}

				if (negPos == 0 || allNegative == 0)
				{
					rightDone = true;
					leftDone = false;
					neg = true;
				}
			}
			
			column++;
		}
		this.attribute = attributes.get(gainColumn);
		
		System.out.println("\nGain: " + gain + " gainColumn: " + gainColumn + " Attribute: " + attributes.get(gainColumn));
		
		attributes.remove(gainColumn);
		//Removes the attribute that was selected and updates the Data to create a new table to evaluate
		// Continues evaluation one direction until there are no more children; then moves to the other direction
		ArrayList<Instance> temp1 = new ArrayList<Instance>();
		temp1 = updateData(gainColumn, leftDone, rightDone);
		
		for(Instance i : temp1)
		{
			i.printData();
		}
		
		for(Instance i : temp1)
		{
			i.printData();
			i.removeColumn(gainColumn);
		}
		
		printData();
		
		if (!attributes.isEmpty())
		{
			if (!leftDone)
			{
				System.out.println(attribute + " Left: ");
				Node child = new Node(this, "	", attributes, temp1);
				child.calculateI();
				left_yes = child;
			}
			else if (leftDone)
			{
				System.out.println(attribute + " Leftd: ");
				System.out.println("We are done. what now? \n");
				Node child = new Node(classificationName, neg);
				left_yes = child;
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
				Node child = new Node(classificationName, neg);
				right_no = child;
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
		negPosFraction = negPos / negatives;
		posNegFraction = posNeg / positives;
		System.out.println("negFraction: " + negFraction + " negPosLog: " + negPosFraction);
		double posLog, negPosLog, negLog, posNegLog;
		
		if (posFraction == 0 || posFraction == 1 || Double.isNaN(posFraction))
		{ 
			posLog = 0; 
		}
		else 
		{
			posLog = -posFraction * (Math.log(posFraction) / Math.log(2));
		}
		
		if (negPosFraction == 0 || negPosFraction == 1 || Double.isNaN(negPosFraction)) 
		{ 
			negPosLog = 0; 
		}
		else
		{
			negPosLog = -negPosFraction * (Math.log(negPosFraction)/ Math.log(2));
		}

		if(negFraction == 0 || negFraction == 1 || Double.isNaN(negFraction))
		{
			negLog = 0;
		}
		else 
		{
			negLog = -negFraction * (Math.log(negFraction) / Math.log(2));
		}

		if(posNegFraction == 0 || posNegFraction == 1|| Double.isNaN(posNegFraction))
		{
			posNegLog = 0;
		}
		else
		{
			posNegLog = -posNegFraction * (Math.log(posNegFraction) / Math.log(2));
		}
		System.out.println("PosLog: " + posLog + " PosNegLog: " + posNegLog);
		System.out.println("NegLog: " + negLog + " NegPosLog: " + negPosLog);
		double positiveSide = (positives/total) * (posLog + posNegLog);
		double negativeSide = (negatives/total) * (negLog + negPosLog);

		double value = positiveSide + negativeSide;
//		System.out.println(value);
		return value;
	}
	
	public double calculateGain(double iValue, double iaValue)
	{
		// Caculates the gain of the attributes
		return iValue - iaValue;
	}
	
	public void printData()
	{
		for(Instance s : data)
		{
			s.printData();
		}
	}
	
	public String toString()
	{
		String s =  attribute;
		if (!label.isEmpty())
		{
			s += label;
		}
		
		return s;
	}
}
