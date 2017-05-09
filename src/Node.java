import java.util.ArrayList;

public class Node {

	private ArrayList<String> attributes = new ArrayList<String>();
	private ArrayList<Instance> data = new ArrayList<Instance>();
	private String classificationName = Driver.classificationName;
	private Node parent;
	private String attribute;
	private String label = new String();
	public Node left_no;
	public Node right_yes;
	
	
	public Node(Node parent, ArrayList<String> attributes, ArrayList<Instance> data)
	{
		this.attributes = new ArrayList<String>(attributes);
		this.data = new ArrayList<Instance>(data);
		this.parent = parent;
	}
	
	public Node(String attribute, boolean negL, boolean negR)
	{
		this.attribute = attribute;
		if(negR)
		{
			this.label = " - False";
		}
		else if (negL)
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
	
	//Creates a copy of data and removes the columns and rows based on what was selected and if the
	//branch of the tree is done or not
	public ArrayList<Instance> updateData(int column, boolean left, boolean right)
	{
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

		return temp;
	}
	
	//Calculates the I value
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
		
		double pValue, nValue, pFraction, nFraction;
		
		if (positives == 0)
		{
			pFraction = 0;
			pValue = 0;
		}
		else
		{
			pFraction = (double) positives / total;
			pValue = Math.log(pFraction) / Math.log(2);
		}
		
		if (negatives == 0)
		{
			nFraction = 0;
			nValue = 0;
		}
		else
		{
			nFraction = (double) negatives / total;
			nValue = Math.log(nFraction) / Math.log(2);
		}
		
		double value = -(pFraction * pValue) - (nFraction * nValue);
		if (value == -0.0)
		{
			value = Math.abs(value);
		}
		
		calculateIa(value);
	}
	
	//Calculates Ia
	public void calculateIa(double iValue)
	{
		// Calculates the Ia value for an attribute
		int column = 0, gainColumn = 0;
		double iaValue, temp, gain = 0.0;
		boolean leftDone = true, rightDone = false, negL = false, negR = true;
		
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
			
			iaValue = calculateInformationContent(allPositive, allNegative, posNeg, negPos, total);
			temp = calculateGain(iValue, iaValue);
						
			if (temp > gain || column == 0)
			{
				gain = temp;
				gainColumn = column;
				leftDone = true;
				rightDone = false;
				
				if(posNeg == 0 || allPositive == 0)
				{
					leftDone = true;
					rightDone = false;
					negL = true;
					negR = false;
				}

				if (negPos == 0 || allNegative == 0)
				{
					rightDone = true;
					leftDone = false;
					negL = false;
					negR = true;
				}
				
				if((negPos == 0 || allNegative == 0) && (posNeg == 0 || allPositive == 0))
				{
					rightDone = true;
					leftDone = true;

					if (allNegative == 0)
					{
						negR = true;
						negL = false;
					}
					else if (negPos == 0)
					{
						negR = false;
						negL = true;
					}
				}
			}
			
			column++;
		}

		this.attribute = attributes.get(gainColumn);
		
		ArrayList<String> newAttributes = new ArrayList<String>(attributes);
		newAttributes.remove(gainColumn);
		attributes.remove(gainColumn);
		
		ArrayList<Instance> temp1 = new ArrayList<Instance>();
		temp1 = updateData(gainColumn, leftDone, rightDone);
		
		
		for(Instance i : temp1)
		{
			i.removeColumn(gainColumn);
		}
		ArrayList<Instance> temp2 = new ArrayList<Instance>(temp1);
	
		if (!attributes.isEmpty())
		{
			if (!leftDone)
			{//We may have gotten the Right and left confused during variable naming, but they are consistent so it works
				Node child = new Node(this, newAttributes, temp1);
				child.calculateI();
				left_no = child;
			}
			else if (leftDone)
			{
				Node child = new Node(classificationName, negL, negR);
				left_no = child;
			}
			
			if (!rightDone)
			{
				Node child = new Node(this, newAttributes, temp2);
				child.calculateI();
				right_yes = child;
			}
			else if (rightDone)
			{
				Node child = new Node(classificationName, negL, negR);
				right_yes = child;	
			}
			
			if(rightDone && leftDone)
			{
				Node child = new Node(classificationName, negR, negL);
				Node child2 = new Node(classificationName, negL, negR);
				right_yes = child;	
				left_no = child2;
			}
		}		
	}

	public double calculateInformationContent(int allPositive, int allNegative, int posNeg, int negPos, int total)
	{
		double positives, negatives, posFraction = 0, negFraction = 0, negPosFraction = 0, posNegFraction = 0;
		double posLog, negPosLog, negLog, posNegLog;
		positives = allPositive + posNeg;
		negatives = allNegative + negPos;
		posFraction = allPositive / positives;
		negFraction = allNegative / negatives;
		negPosFraction = negPos / negatives;
		posNegFraction = posNeg / positives;
		
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
		
		double positiveSide = (positives/total) * (posLog + posNegLog);
		double negativeSide = (negatives/total) * (negLog + negPosLog);

		double value = positiveSide + negativeSide;

		return value;
	}
	
	public int majority(int column)
	{
		int no = 0;
		int yes = 0;
		
		for(Instance i : data)
		{
			if (i.getColumn(column) == 0)
			{
				no++;
			}
			else
			{
				yes++;
			}
		}
		
		if(no >= yes)
		{
			return 2;
		}
		return 1;
	}
	
	public double calculateGain(double iValue, double iaValue)
	{
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
