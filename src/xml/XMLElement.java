package xml;

import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultElement;


//http://tutorials.jenkov.com/java-xml/stax-xmleventwriter.html
public class XMLElement extends DefaultElement
{
	public XMLElement(String name)
	{
		
	}

	public XMLElement(String string, String text)
	{
		// TODO Auto-generated constructor stub
	}

	public void addAll(XMLElement ... elems)	
	{
		for (XMLElement e : elems)
		if (e != null)					// some may be null
			addElement(e);
	}

	public void addAttribute(String string, String selectedItem)
	{
		// TODO Auto-generated method stub
		
	}

	public void addElement(XMLElement xmlElement)
	{
		
	}

	public XMLElement getChild(String string)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
