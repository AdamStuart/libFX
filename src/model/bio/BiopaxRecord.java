package model.bio;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.NamedNodeMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BiopaxRecord extends HashMap<String, String> {

	private SimpleStringProperty rdfid = new SimpleStringProperty();
	public StringProperty  rdfidProperty()  { return rdfid;}
	public String getRdfid()  { return rdfid.get();}
	public void setRdfid(String s)  { rdfid.set(s);}
	
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
	
	private List<String> authors = new ArrayList<String>();
	public List<String> getAuthors()  { return authors;}
	public void addAuthors(String s)  { authors.add(s);}
	

	public String getFirstAuthor()  {
		if (authors.isEmpty()) return "";
		String auths = authors.get(0);
		if (auths == null) return "";
		return auths.split(" ")[0];
	}
	
	
	public BiopaxRecord(org.w3c.dom.Node elem) {
		
//		for (int i=0; i<elem.getChildNodes().getLength(); i++)
//		{
//			org.w3c.dom.Node child = elem.getChildNodes().item(i);
//			String name = child.getNodeName();
////			System.out.println(name);
//			if ("bp:PublicationXref".equals(name))
//			{
				NamedNodeMap attrs = elem.getAttributes();
				setRdfid(attrs.getNamedItem("rdf:id").getNodeValue());
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
					else if ("bp:AUTHORS".equals(subname))	authors.add(kid.getTextContent());
				}
//			}
//		}
	}
	static String pubHeaderCtrl = "<bp:PublicationXref xmlns:bp=\"http://www.biopax.org/release/biopax-level3.owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" rdf:id=\"%s\">\n";
	static String pubHeaderClose = "</bp:PublicationXref>\n";
	
	public String toString()
	{
		String firstAuthor =  getFirstAuthor(); 
		return db.get() + " " +  id.get() + ": " + firstAuthor + ", [" + year.get() + ", " + source.get() + ", " + title.get() + "].";
	}
	public String toGPML()		// TODO --save original text so we can pass external fields thru
	{
		StringBuilder bldr = new StringBuilder();
		String s = String.format(pubHeaderCtrl, getRdfid());
		bldr.append(s);
		addTag(bldr, "bp:ID", id.get());
		addTag(bldr, "bp:DB", db.get());
		addTag(bldr, "bp:TITLE", title.get());
		addTag(bldr, "bp:SOURCE", source.get());
		addTag(bldr, "bp:YEAR", year.get());
		for (String auth : getAuthors())
			addTag(bldr, "bp:AUTHORS", auth);
		
		bldr.append(pubHeaderClose);
		return bldr.toString();
	}
	
	void addTag(StringBuilder bldr, String key, String val)
	{
		bldr.append("<").append(key).append(">");
		bldr.append(val);
		bldr.append("</").append(key).append(">\n");
	}
	

//
//<bp:ID >16374s430</bp:ID>
//<bp:DB >PubMed</bp:DB>
//<bp:TITLE >Renin increases mesangial cell transforming growth factor-beta1 and matrix proteins through receptor-mediated, angiotensin II-independent mechanisms.</bp:TITLE>
//<bp:SOURCE >Kidney Int</bp:SOURCE>
//<bp:YEAR >2006</bp:YEAR>
//<bp:AUTHORS >Huang Y</bp:AUTHORS>
//<bp:AUTHORS >Wongamorntham S</bp:AUTHORS>
//<bp:AUTHORS >Kasting J</bp:AUTHORS>
//<bp:AUTHORS >McQuillan D</bp:AUTHORS>
//<bp:AUTHORS >Owens RT</bp:AUTHORS>
//<bp:AUTHORS >Yu L</bp:AUTHORS>
//<bp:AUTHORS >Noble NA</bp:AUTHORS>
//<bp:AUTHORS >Border W</bp:AUTHORS>



}
