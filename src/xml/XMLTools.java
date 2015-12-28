package xml;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.StringUtil;

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
	public static Node getChildByPath(Node value, String... path)
	{
		for (String step : path)
		{
			value = getChildByName(value, step);
			if (value == null) return null;
		}
		return value;
	}
	
	//-----------------------------------------------------------------------------
	public static String getChildAttribute(Node node, String attr)
	{
		Node attrNode = node.getAttributes().getNamedItem(attr);
		String ref = attrNode == null ? "" : attrNode.getTextContent();
		return ref;
	}
	
	public static double getDoubleAttribute(org.w3c.dom.Node elem, String attrName)
	{
		return StringUtil.toDouble(getChildAttribute(elem, attrName));
	}

	//--------------------------------------------------------------------------------

	public static boolean nodeContains(org.w3c.dom.Node node, String  strUpper)
	{
//		String strUpper = str.toUpperCase();
		if (node == null) return false;
		String nodeName = node.getNodeName();
		if (nodeName != null)
		{
			System.out.println(nodeName);
			if (nodeName.toUpperCase().contains(strUpper))
				return true;
			if (node instanceof Element)
			{
				String s = getTextValue((Element) node).trim().toUpperCase();
				System.out.println(s);

				if (s.contains(strUpper))
					return true;
			}
			NamedNodeMap map = node.getAttributes();
			if (map != null)
			for (int x=0; x<map.getLength(); x++)
			{
				Node n = map.item(x);
				if (nodeContains(n, strUpper))
								return true;
			}
		}

        for (int i=0; i<node.getChildNodes().getLength(); i++)
        	if (nodeContains(node.getChildNodes().item(i), strUpper))
        		return true;
		return false;
	}
	

	public static String getTextValue(org.w3c.dom.Element valueEle) {
		    StringBuffer value = new StringBuffer();
		    NodeList nl = valueEle.getChildNodes();
		    for (int i = 0; i < nl.getLength(); i++) {
		      Node item = nl.item(i);
		      if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
		        value.append(item.getNodeValue());
		      }
		    }
		    return value.toString();
		  }

}
