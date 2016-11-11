/* Here the bi-directional Breadth first search is done over the graph to get the shortest path
 * between two nodes. The size of the path is the degree of connection between the.
 * The specialty of Bi-directional is that two BFSs run parallely from either end
 * till they collide thus getting the shortest path faster */

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class BiDirectionalBFS
{
	public long biDirectionalBFS(HashMap<Integer, User> people, int source, int destination) 
	{
		QueueData sourceData = new QueueData(people.get(source));
		QueueData destData = new QueueData(people.get(destination));
		
		int counter = 0;
		while (!sourceData.isFinished() && !destData.isFinished() && counter<=4)
		{
			/* Search out from source. */
			User collision = searchBFS(people, sourceData, destData);
			if (collision != null)
			{
				return detectCollisionMerger(sourceData, destData, collision.getUserID());
			}
			else
			{
				counter++;
			}
			
			/* Search out from destination. */
			collision = searchBFS(people, destData, sourceData);
			if (collision != null) 
			{
				return detectCollisionMerger(sourceData, destData, collision.getUserID());
			}
			else
			{
				counter++;
			}
		}
		return Long.MAX_VALUE;
	}
	
	/* Search one level and return collision, if any. */
	public User searchBFS(HashMap<Integer, User> people, QueueData primary, QueueData secondary) 
	{
		/* We only want to search one level at a time. Count how many nodes are currently in the primary's
		 * level and only do that many nodes. We'll continue to add nodes to the end. */
		int count = primary.toVisit.size(); 
		for (int i = 0; i < count; i++) 
		{
			/* Pull out first node. */
			NodePath NodePath = primary.toVisit.poll();
			int UserId = NodePath.getUser().getUserID();
			
			/* Check if it's already been visited. */
			if (secondary.visited.containsKey(UserId))
			{
				return NodePath.getUser();
			}				
			
			/* Add friends to queue. */
			User User = NodePath.getUser();
			LinkedHashSet<Integer> friends = User.getFriendsList();
			for (int friendId : friends)
			{
				if (!primary.visited.containsKey(friendId))
				{
					User friend = people.get(friendId);
					NodePath next = new NodePath(friend, NodePath);
					primary.visited.put(friendId, next);
					primary.toVisit.add(next);
				}
			}
		}
		return null;
	}
	
	public long detectCollisionMerger(QueueData bfs1, QueueData bfs2, int connection)
	{
		// end1 -> source
		NodePath end1 = bfs1.visited.get(connection); 
		// end2 -> destination
		NodePath end2 = bfs2.visited.get(connection); 
		// forward: source -> connection
		LinkedList<User> pathOne = end1.collapse(false); 
		// reverse: connection -> destination
		LinkedList<User> pathTwo = end2.collapse(true); 
		// remove connection
		pathTwo.removeFirst(); 
		// add second path
		pathOne.addAll(pathTwo); 
		// return the path size that indicates the degree
		return pathOne.size()-1; 
	}
}

/********************** END OF FILE ******************************/