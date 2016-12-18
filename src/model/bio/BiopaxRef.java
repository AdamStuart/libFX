package model.bio;
import org.w3c.dom.NamedNodeMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BiopaxRef {

	private SimpleStringProperty xrefid = new SimpleStringProperty();
	public StringProperty  xrefidProperty()  { return xrefid;}
	public String getXrefid()  { return xrefid.get();}
	public void setXrefid(String s)  { xrefid.set(s);}
	
	private SimpleStringProperty id = new SimpleStringProperty();
	public StringProperty  idProperty()  { return id;}
	public String getId()  { return id.get();}
	public void setId(String s)  { id.set(s);}
	
	private SimpleStringProperty db = new SimpleStringProperty();
	public StringProperty  dbProperty()  { return db;}
	public String getDb()  { return db.get();}
	public void setDb(String s)  { db.set(s);}
	
	private SimpleStringProperty title = new SimpleStringProperty();
	public StringProperty  titleProperty()  { return title;}
	public String getTitle()  { return title.get();}
	public void setTitle(String s)  { title.set(s);}
	
	private SimpleStringProperty source = new SimpleStringProperty();
	public StringProperty  sourceProperty()  { return source;}
	public String getSource()  { return source.get();}
	public void setSource(String s)  { source.set(s);}
	
	private SimpleStringProperty year = new SimpleStringProperty();
	public StringProperty  yearProperty()  { return year;}
	public String getYear()  { return year.get();}
	public void setYear(String s)  { year.set(s);}
	
	private SimpleStringProperty authors = new SimpleStringProperty();
	public StringProperty  authorsProperty()  { return authors;}
	public String getAuthors()  { return authors.get();}
	public void setAuthors(String s)  { authors.set(s);}
	

	public String getFirstAuthor()  {
		return authors.get();
	}
	
	
	public BiopaxRef(org.w3c.dom.Node elem) {
		
//		for (int i=0; i<elem.getChildNodes().getLength(); i++)
//		{
//			org.w3c.dom.Node child = elem.getChildNodes().item(i);
//			String name = child.getNodeName();
////			System.out.println(name);
//			if ("bp:PublicationXref".equals(name))
//			{
				NamedNodeMap attrs = elem.getAttributes();
				xrefid.set(attrs.getNamedItem("rdf:id").getNodeValue());
				for (int j=0; j<elem.getChildNodes().getLength(); j++)
				{
					org.w3c.dom.Node grandchild = elem.getChildNodes().item(j);
					if (grandchild == null) continue;
					org.w3c.dom.Node kid =  grandchild.getFirstChild();
					if (kid == null) continue;
					
					String subname = grandchild.getNodeName();
					if ("#text".equals(subname)) continue;
					if ("bp:ID".equals(subname))			id.set(kid.getNodeValue());
					else if ("bp:DB".equals(subname))		db.set(kid.getTextContent());
					else if ("bp:TITLE".equals(subname))	title.set(kid.getTextContent());
					else if ("bp:SOURCE".equals(subname))	source.set(kid.getTextContent());
					else if ("bp:YEAR".equals(subname))		year.set(kid.getTextContent());
					else if ("bp:AUTHORS".equals(subname))	
						{
							String current = authors.get();
							if (current == null) current = "";
							if (current.length() > 0) current += ", ";
							authors.set(current + kid.getTextContent());
						}
				}
//			}
//		}
	}
	public String toString()
	{
		String firstAuthor =  getFirstAuthor(); 
		return db.get() + ": " + id.get() + ", " + firstAuthor + ", [" + year.get() + ", " + source.get() + ", " + title.get() + "].";
	}

}
