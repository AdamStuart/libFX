package xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLTools
{
	//-----------------------------------------------------------------------------
	public static Node getChildByName(Node value, String name)
	{
		if (value == null) return null;
		NodeList children = value.getChildNodes();
		if (children == null) return null;
		int sz = children.getLength();
		for (int i=0; i<sz; i++)
		{
			Node child = children.item(i);
			String nodename = child.getNodeName();
			if (name.equals( nodename))
				return child;
		}
		return null;
	}
	
	//-----------------------------------------------------------------------------
	public Node getChildByPath(Node value, String[] path)
	{
		for (String step : path)
		{
			value = getChildByName(value, step);
			if (value == null) return null;
		}
		return value;
	}
}
