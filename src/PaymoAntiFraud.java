/****************************************************
 *** INSIGHT DATA ENGINEERING - CODING CHALLENGE  ***
 *   DIGITAL-WALLET 								*
 *   Author: KARTHIK CHAGANTI						*	
 *   Date: Nov - 06 - 2016							*
 *   Using Allman style of brace-indentation		*
 ****************************************************/

/**************************************************************************************************************************************************/
/* Description: 1. To find the relations namely direct friend, friend of a friend, and 4th degree or below friends, we have to use 
 * graph representation. This graph can be represented using an adjacent list which consists of immediate or degree 1 friends of every 
 * node or user. I chose to use HashMap to represent key as user1 and value as Hashset of User objects which wraps around ID of the user
 * and a friendslist of other users with who the primary user transacted in the past. By retrieving this hashset we can infer many of the
 * features that the problem is used to solve. 
 * 
 * Feature One: Directly retrieved Adjacent list of user1 and checked it if it contains the user2
 * Feature Two: Directly retrieved Adjacent list of user1 and user2 and checked them for intersection to find the mutual friend
 * Feature Three: Ran Bi-directional BFS to find the shortest path between user1 and user2 that gives the degree of separation or connection
 * Feature Four: Calculated Median Degree of graph and compared with user2 along with its percentage by which he requested more than paid funds.
 * 
 * * * Apart from the main three features, I chose to infer one more feature which by looking at the median degree of the whole graph and
 * then comparing it with degree of connections of each transaction each user on the receiving end performed. Why I chose receiving end is 
 * because it is intuitive that a user who only requests all the time but rarely or never pays might be a stammer or scamper if his 
 * degree of connections is abnormally high. Even though this might or might not shed much of a light on fraud detection from the data 
 * given, I chose this as it is somewhat intuitive. 
 * * * One more feature that can be implemented is to look at the recivers who constantly request i.e. get paid by the people out of
 * fourth degree connections. They might be scammers. However I didn't implement this as the count at which I can possibly say that 
 * they have performed too many transactions out of fourth degree is not constant or possible to set up in the given data. However it is 
 * very useful to detect fraud and warn the users accordingly */
/**************************************************************************************************************************************************/
/* ASSUMPTION: that the stream data is only used to run on the past-data modeled graph. The graph remains constant and the new stream data 
 * is not used to update the existing graph.  */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Exception;
import java.util.Collections;

public class PaymoAntiFraud 
{	
	double median_degree; // holds median degree of the graph
	HashMap<Integer,User> map = new HashMap<Integer,User>(); 
	HashMap<Integer,Integer> fraudMap = new HashMap<Integer,Integer>();
	BiDirectionalBFS bfs = new BiDirectionalBFS(); // instantiate the class of given type
	
	public static void main(String[] args)
	{
		/* Step 1: Read from Batch File 
		 * Step 2: Read from Stream File
		 * Step 3. Print outputs */
		
		long startTime = System.currentTimeMillis(); // to calculate time taken for processing
		PaymoAntiFraud  paymo = new PaymoAntiFraud(); // Create object to access class members
		
		/* Step 1: Read from the Batch File and save the relations onto a map */
		Scanner scan_batch = null;
		Scanner scan_stream = null;

		PrintWriter printWriter_1 = null;
		PrintWriter printWriter_2 = null;
		PrintWriter printWriter_3 = null;
		PrintWriter printWriter_4 = null;
		try
		{
			/* Create Scanner object to scan the batch file */
			//scan_batch = new Scanner(new FileReader("../digital-wallet-master/paymo_input/batch_payment.txt"));
			scan_batch = new Scanner(new FileReader(args[0]));
			
			scan_batch.nextLine(); //Skip the header from being read
			
			/* Read till the end of file */
			while(scan_batch.hasNext())
			{
				// reads into string every single line till nextline is reached
				String input = scan_batch.useDelimiter("\n").next();
				
				// split the line by comma as the file is CSV
				String[] input_splitted = input.split(",");
				
				// Payer and Buyer are read into
				int payer = Integer.parseInt(input_splitted[1].trim());
				int reciever = Integer.parseInt(input_splitted[2].trim());
				
				// Create a mapping of the Payer-Buyer relation as Adjacency List
				paymo.createMap(payer,reciever);
			} // end of batch file reading
			
			// calculate median degree of the created graph represented by adjaceny list through hashMap
			paymo.median_degree = paymo.calculateMedianDegree();
			
			// Step 2: Read from Stream File and infer the fraudulent transactions from the existing batch map
			//scan_stream = new Scanner(new FileReader("../digital-wallet-master/paymo_input/stream_payment.txt"));
			scan_stream = new Scanner(new FileReader(args[1]));
			
			// Instantiate File Writing objects as well to print simultaneously while reading
			/*printWriter_1 = new PrintWriter(new BufferedWriter(new FileWriter("../digital-wallet-master/paymo_output/output1.txt")));
			printWriter_2 = new PrintWriter(new BufferedWriter(new FileWriter("../digital-wallet-master/paymo_output/output2.txt")));
			printWriter_3 = new PrintWriter(new BufferedWriter(new FileWriter("../digital-wallet-master/paymo_output/output3.txt")));
			printWriter_4 = new PrintWriter(new BufferedWriter(new FileWriter("../digital-wallet-master/paymo_output/output4.txt")));*/
			
			printWriter_1 = new PrintWriter(new BufferedWriter(new FileWriter(args[2])));
			printWriter_2 = new PrintWriter(new BufferedWriter(new FileWriter(args[3])));
			printWriter_3 = new PrintWriter(new BufferedWriter(new FileWriter(args[4])));
			printWriter_4 = new PrintWriter(new BufferedWriter(new FileWriter(args[5])));
			
			// Skip the header from being read
			scan_stream.nextLine();
			
			// Read till the end of file
			while(scan_stream.hasNext())
			{
				// reads into string every single line till nextline is reached
				String input = scan_stream.useDelimiter("\n").next();
				
				// split the line by comma as the file is CSV
				String[] input_splitted = input.split(",");
				
				// Payer and Buyer are read into
				int payer = Integer.parseInt(input_splitted[1].trim());
				int reciever = Integer.parseInt(input_splitted[2].trim());
				
				/* Check for the feature conditions*/
				if(paymo.holdsFeatureOne(payer,reciever))  // feature one : if it passes, it should pass feature two and three as well!
				{
					printWriter_1.println("trusted");
					printWriter_2.println("trusted");
					printWriter_3.println("trusted");
				}
				else
				{
					printWriter_1.println("unverified");
					if(paymo.holdsFeatureTwo(payer,reciever))  //if feature 1 fails, check for friend-of-friend condition
					{
						printWriter_2.println("trusted");  
						printWriter_3.println("trusted"); // if passed, it should satisfy third feature as well!
					}
					else
					{
						printWriter_2.println("unverified");
						if(paymo.holdsFeatureThree(payer,reciever))  // check for 3rd feature should feature 2 fail
						{
							printWriter_3.println("trusted");
						}
						else
						{
							printWriter_3.println("unverified");
						}
					}
				}
				/* Extra Feature: 
				 * Based on intuition, I feel if the degree of a user is greater than the median degree and at the same time, 
				 * if he is always on the receiver side, i.e. he rarely pays, it might indicate that he initiates too many request to unknown people
				 * and then become their first degree friend through scam or spam. */
				if(paymo.holdsFeatureFour(reciever))  
				{
					printWriter_4.println("trusted");
				}
				else
				{
					printWriter_4.println("unverified");
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace(); //catch exception
		}
		finally
		{
			/* Close the readers and writers */
			scan_batch.close();
			scan_stream.close();			 
			printWriter_1.close();
			printWriter_2.close();
			printWriter_3.close();
			printWriter_4.close();
		}
		
		long stopTime = System.currentTimeMillis();
		System.out.println(paymo.median_degree);
		System.out.println(stopTime-startTime);
	}
	
	/* checks for feature one*/
	public boolean holdsFeatureOne(int payer, int reciever)
	{
		/* Checks if both the users from stream file are part of map*/
		if(map.containsKey(payer) && map.containsKey(reciever))
		{
			/* Checks if the user is a direct friend or not by checking the adjacency list */
			boolean payer_bool = map.get(payer).getFriendsList().contains(reciever);
			if(payer_bool)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}		
	}

	/* Checks for the feature two i.e. intersection of aleast one friend among both the users */
	public boolean holdsFeatureTwo(int payer, int reciever)
	{
		if(map.containsKey(payer) && map.containsKey(reciever))
		{
			/* Checks if there is any direct mutual friend of both the users*/
			boolean intersection = Collections.disjoint(map.get(payer).getFriendsList(),map.get(reciever).getFriendsList());
			if(!intersection)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}			
	}
	
	/* Checks for the degree of separation between the users. If greater than four, mark it as unverified 
	 * Used Bi-directional BFS to find the shortest path in order to calculate the exact degree of separation/friendship */
	public boolean holdsFeatureThree(int payer, int reciever)
	{
		if(map.containsKey(payer) && map.containsKey(reciever))
		{
			long degree = bfs.biDirectionalBFS(map,payer,reciever);
			if(degree <= 4) //check if the degree is within 4
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	/* Extra Feature 4: 
	 * A small intuition that if a user who generally does only requests more than payments with huge degree than the rest of the people.
	 * Inspired from Spammers who generally have more and abnormal degree of connections than others when visualized in the graph
	 * Checking here for Degree(user) > median Degree(whole network) and at the same time where the user has more requests than payments */
	public boolean holdsFeatureFour(int reciever)
	{
		if(map.containsKey(reciever))
		{
			if(map.get(reciever).getUserDegree() > median_degree && (map.get(reciever).getCountOnRecieveEnd() / map.get(reciever).getCountOnPayerEnd() >= 2))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}
	
	/* Function to calculate the median of degrees of the graph */
	public double calculateMedianDegree()
	{
		ArrayList<Integer> degree = new ArrayList<Integer>();
		for(Map.Entry<Integer,User> entry : map.entrySet())
		{
			degree.add(entry.getValue().getUserDegree());  
		}
		double median_degree = median(degree);
		return median_degree;
	}
	
	/* Helper function to calculate the median */
	public double median(ArrayList<Integer> degree)
	{
		
		int middle = degree.size()/2;
		if(degree.size()%2 == 1)
		{
			return degree.get(middle);
		}
		else
		{
			return (degree.get(middle-1) + degree.get(middle))/2.0;
		}
	}
	
	/* Create a HashMap that stores User as key and User info object as Value.
	 * User info object consists of friendsList and other information 
	 * A graph's adjacency list is repetitive as in one list from the payer and one from the receiver
	 * If the map already contains either of them in keys, retrieve the key and update
	 * if not, create a new key and update accordingly */
	public void createMap(int payer, int reciever)
	{
		if(!map.containsKey(payer)) // if map doesn't already have this key
		{
			User user = new User(payer); //Create a new User object
			user.addToFriendsList(reciever); // add the receiver to the payer's friendlist
			user.addCountOnPayerEnd(); // increment the count on as this user is a payer
			map.put(payer, user); // append the object to the current payer into a new key 
		}
		else
		{   
			/* these get executed if the map already contains the user as the key */
			User user = map.get(payer); // retrieve the existing valueset of the key
			user.addToFriendsList(reciever);// add the receiver to the payer's friendlist
			user.addCountOnPayerEnd(); // increment the count on as this user is a payer
			map.put(payer,user); // append the object to the current payer into a new key 
		}
		
		if(!map.containsKey(reciever)) //checks if the receiver is already present in the map
		{
			User user = new User(reciever); //Create a new User object
			user.addCountOnRecieveEnd(); // increment the count as the receiver is on the same end
			user.addToFriendsList(payer);// add the payer to the reciever's friendlist
			map.put(reciever, user); // append the object to the current payer into a new key 
		}
		else
		{
			User user = map.get(reciever); // retrieve the existing valueset of the key
			user.addCountOnRecieveEnd(); // increment the count as the receiver is on the same end
			user.addToFriendsList(payer); // add the payer to the reciever's friendlist
			map.put(reciever,user); // append the object to the current payer into a new key 
		}
	}
}

/********************** END OF FILE ******************************/