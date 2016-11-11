/* Class to declare the datatypes that are used in Bi-directional Breadth First Search*/

import java.util.LinkedList;

public class NodePath {
	private User user = null;
	private NodePath previousNode = null;
	public NodePath(User p, NodePath previous)
	{
		user = p;
		previousNode = previous;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public LinkedList<User> collapse(boolean startsWithRoot) 
	{
		LinkedList<User> path = new LinkedList<User>();
		NodePath node = this;
		while (node != null)
		{
			if (startsWithRoot)
			{
				path.addLast(node.user);
			} else 
			{
				path.addFirst(node.user);
			}
			node = node.previousNode;
		}
		return path;
	}
}

/********************** END OF FILE ******************************/