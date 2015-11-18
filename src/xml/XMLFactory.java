package xml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLFactory
{	

	List<XMLEvent> events = new ArrayList<XMLEvent>();
	List<XMLEvent> getEvents()	{ return events;	}
	
	public static Map<String, Node> readElements(Element parent)
	{
		HashMap<String, Node> map = new HashMap<String, Node>();
		if (parent != null)
		{
			NodeList nodes = parent.getChildNodes();
			int sz = nodes.getLength();
			for (int i=0; i<sz; i++)
			{
				Node n = nodes.item(i);
				String name = n.getNodeName();
				map.put(name, n);
			}
		}
		return map;
	}
	
	//http://tutorials.jenkov.com/java-xml/stax-xmleventwriter.html
	public static void writeEvents(List<XMLEvent> steps, String path)
	{
		XMLOutputFactory factory      = XMLOutputFactory.newInstance();
		XMLEventFactory  eventFactory = XMLEventFactory.newInstance();

			try {
			    XMLEventWriter writer = factory.createXMLEventWriter( new FileWriter(path));
			    XMLEvent event = eventFactory.createStartDocument();
			    writer.add(event);
			    for (XMLEvent ev : steps)
			    	if (ev != null)
			    		writer.add(ev);
			    writer.flush();
			    writer.close();
			} 
			catch (XMLStreamException ex) {	    ex.printStackTrace();} 
			catch (IOException e) {	    e.printStackTrace();	}
	}
	
}