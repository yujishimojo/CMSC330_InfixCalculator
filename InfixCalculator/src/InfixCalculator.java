import java.util.LinkedList;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class InfixCalculator
{
    public static void main(String[] args)
    {
	String expression;
	String token;
	String operator;
	String operand1, operand2;
	String variable = null;
	Object value;
	StringTokenizer tokens;
	int parenthesis=0;

	//create an output postfixQueue
	LinkedList<String> postfixQueue = new LinkedList<String>();

	//create empty stack of pending string operators
	Stack operatorStack = new Stack();
	
	//create empty stack for calculating
	Stack calculateStack = new Stack();

	expression = JOptionPane.showInputDialog( null, "Enter Infix Expression", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
	tokens = new StringTokenizer(expression);

	//infix to postfix translaion
	while (tokens.hasMoreTokens())
	{  //hasn't gone thru all the "tokens"
	    token = tokens.nextToken();    //get next "token" from input string
	    if (isNumeric(token) || isAlphametic(token))  //it's an operand (a number)
	    {
	        //extract int from String token, create Integer object, add token to postfixQueue:
	        postfixQueue.addLast(token);
	    }
	    else if (isOperator(token)) //token is an operator
	    {
	    	if (token.equals("(")) //token is "("
	    	{
	    		parenthesis++; //for status of parentheses
	    		operatorStack.push(token); //push token to stack
	    	} else if (token.equals(")"))
	    	{
	    		parenthesis++; //for status of parentheses
	    		while (true)
	    		{
	    			operator = (String) operatorStack.pop(); //pop until "(" found
	    			if (operator.equals("("))
	    			{
	    				break;
	    			} else
	    			{
		    			postfixQueue.addLast(operator);
	    			}
	    		}
	    	} else if (operatorStack.isEmpty())
	    	{
	    		operatorStack.push(token); //push token to stack
	    	} else {  //if operatorStack is not empty
	    		String stackTop = (String) operatorStack.peek(); //get the stack top value
	    		//token has higher precedence than stack top
	    		if (operatorPrecedence(token) > operatorPrecedence(stackTop))
	    		{
	    			operatorStack.push(token);
	    		//token has same or lower precedence than stack top
	    		} else if (operatorPrecedence(token) == operatorPrecedence(stackTop)
	    				|| operatorPrecedence(token) < operatorPrecedence(stackTop))
	    		{
	    			if (parenthesis % 2 != 0) { //not closed parenthesis
	    				operatorStack.push(token);
	    			} else if (parenthesis == 0) { //there is no parenthesis
	    				if (operatorPrecedence(token) < operatorPrecedence(stackTop))
	    				{
			    			operator = (String) operatorStack.pop();
			    			postfixQueue.addLast(operator);
		    				int count = postfixQueue.size();
		    				if (count % 2 != 0)
		    				{
		    					operatorStack.push(token);
		    				} else {
				    			postfixQueue.addLast(token);
		    				}
		    			} else if (operatorPrecedence(token) == operatorPrecedence(stackTop))
		    			{
			    			operator = (String) operatorStack.pop();
			    			postfixQueue.addLast(operator);
		    				operatorStack.push(token);
		    			}
	    			} else { //closed parenthesis
		    			operator = (String) operatorStack.pop();
		    			postfixQueue.addLast(operator);
		    			operatorStack.push(token);
	    			}
	    		}
	    	}
	    } else if (token.equals(","))
    	{
	    	while (!token.equals(";"))
	    	{
		    	token = tokens.nextToken();
		    	if (isAlphametic(token))
		    	{
		    		variable = token;
		    	} else if (isNumeric(token))
		    	{
		    		if (postfixQueue.contains(variable))
		    		{
		    			int index = postfixQueue.indexOf(variable);
		    			postfixQueue.remove(index);
		    			postfixQueue.add(index, token);
		    		}
		    	}
	    	}
    	} else //token is neither a number nor an operator
	    {
	    	System.out.println(token + " is not verified character.");
	    }
	}
	//pop all the rest of operators off stack, and add them to postfixQueue
	while (!operatorStack.isEmpty())
	{
		operator = (String) operatorStack.pop();
		postfixQueue.addLast(operator);
	}

	//display both infix expression and postfix expression
	System.out.println("Infix Expression : " + expression);
	System.out.print("Postfix Expression : ");
	for (int i=0; i < postfixQueue.size(); i++)
	{
		System.out.print(postfixQueue.get(i) + " ");
	}
	System.out.println();
	
	//calculate
	for (int i=0; i < postfixQueue.size(); i++)
	{
		token = postfixQueue.get(i);
		if (isNumeric(token))
		{
			calculateStack.push(token);
		} else if (isOperator(token))
		{
			operand2 = calculateStack.pop().toString();
			operand1 = calculateStack.pop().toString();
			if (token.equals("+"))
			{
				calculateStack.push(plus(operand1, operand2));
			} else if (token.equals("-"))
			{
				calculateStack.push(minus(operand1, operand2));
			} else if (token.equals("*"))
			{
				calculateStack.push(times(operand1, operand2));
			} else if (token.equals("/"))
			{
				calculateStack.push(divide(operand1, operand2));
			}
		}
		if (calculateStack.capacity() == 1)
		{
			break;
		}
	}
	value = calculateStack.peek();
	System.out.println("Value = " + value);		

	System.exit(0);
    }
    
    static boolean isNumeric(String n)
    {
    	try {
    		Double.parseDouble(n);
    		return true;
    	} catch (NumberFormatException e)
    	{
    		return false;
    	}
    }

    static boolean isOperator(String t)
    {
    	if (t.equals("+") || t.equals("-") || t.equals("*") || t.equals("/") ||
    	    t.equals("%") || t.equals("(") || t.equals(")"))
    	    return true;
    	else
    	    return false;
    }

    static int operatorPrecedence(String o)
    {
    	if (o.equals("*") || o.equals("/"))
    	{
    		return 2;
    	} else
    	{
    		return 1;
    	}
    }
    
    static boolean isAlphametic(String a)
    {
    	Matcher m = Pattern.compile("[a-z|A-Z]").matcher(a);
    	if (m.find())
    	{
    		return true;
    	} else
    	{
        	return false;    		
    	}
    }
    
    static Integer plus(String operand1, String operand2)
    {
    	return Integer.valueOf(operand1) + Integer.valueOf(operand2);
    }
    
    static Integer minus(String operand1, String operand2)
    {
    	return Integer.valueOf(operand1) - Integer.valueOf(operand2);
    }
    
    static Integer times(String operand1, String operand2)
    {
    	return Integer.valueOf(operand1) * Integer.valueOf(operand2);
    }

    static Integer divide(String operand1, String operand2)
    {
    	return Integer.valueOf(operand1) / Integer.valueOf(operand2);
    }
}
