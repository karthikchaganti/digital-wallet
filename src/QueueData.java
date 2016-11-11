/* Class to declare the datatypes that are used in Bi-directional Breadth First Search*/

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class QueueData
{
	public Queue<NodePath> toVisit = new LinkedList<NodePath>();
	public HashMap<Integer, NodePath> visited = new HashMap<Integer, NodePath>();

	public QueueData(User root) 
	{
		NodePath sourcePath = new NodePath(root, null);
		toVisit.add(sourcePath);
		visited.put(root.getUserID(), sourcePath);	
	}
	
	public boolean isFinished() 
	{
		return toVisit.isEmpty();
	}
}

/********************** END OF FILE ******************************/