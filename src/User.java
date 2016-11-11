import java.util.LinkedHashSet;

// Create a class of type User that holds user and its friends 
public class User 
{
	//Create a linkedHashset that contains the friends i.e. degree 1 transaction participants
	private LinkedHashSet<Integer> friendsList = new LinkedHashSet<Integer>();
	private int userID;
	private int count_on_recieve_end;
	private int count_on_payer_end;
		
	public User(int userID)
	{
		this.userID = userID;  //userID 
		this.count_on_recieve_end = 0; // Count that indicates the no.of times the user requested funds.
		this.count_on_payer_end = 0; // Count that indicates the no.of times the user has payed to friends.
	}
	/*Helper Functions*/		
	public int getUserID() 
	{
		return userID;
	}
		
	public LinkedHashSet<Integer> getFriendsList() 
	{
		return friendsList;
	}
		
	public void addToFriendsList(int friend) 
	{
		this.friendsList.add(friend);
	}
	
	public int getUserDegree()
	{
		return this.friendsList.size();
	}
	
	public int getCountOnRecieveEnd()
	{
		return count_on_recieve_end;
	}
	
	public void addCountOnRecieveEnd()
	{
		this.count_on_recieve_end++;
	}
	public int getCountOnPayerEnd()
	{
		return count_on_payer_end;
	}
	
	public void addCountOnPayerEnd()
	{
		this.count_on_payer_end++;
	}
}


/********************** END OF FILE ******************************/