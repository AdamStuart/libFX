package model.bio;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PathwayRecord
{
	StringProperty id = new SimpleStringProperty();
	StringProperty url = new SimpleStringProperty();
	StringProperty name = new SimpleStringProperty();
	StringProperty species = new SimpleStringProperty();
	StringProperty revision = new SimpleStringProperty();
	DoubleProperty score = new SimpleDoubleProperty(0);

	
	public PathwayRecord(org.w3c.dom.Node elem)
	{
		for (int j=0; j<elem.getChildNodes().getLength(); j++)
		{
			org.w3c.dom.Node grandchild = elem.getChildNodes().item(j);
			if (grandchild == null) continue;
			org.w3c.dom.Node kid =  grandchild.getFirstChild();
			if (kid == null) continue;
			
			String subname = grandchild.getNodeName();
			if ("#text".equals(subname)) continue;
			if ("ns2:id".equals(subname))				id.set(kid.getNodeValue());
			else if ("ns2:url".equals(subname))			url.set(kid.getTextContent());
			else if ("ns2:name".equals(subname))		name.set(kid.getTextContent());
			else if ("ns2:species".equals(subname))		species.set(kid.getTextContent());
			else if ("ns2:revision".equals(subname))	revision.set(kid.getTextContent());
			else if ("ns2:score".equals(subname))		score.set(Double.valueOf(kid.getTextContent()));
		}
	}
	

	
	public PathwayRecord(String inId, String inUrl, String inName, String inSpecies, String inRevision)
	{
		set( inId, inUrl, inName, inSpecies, inRevision);
	}
	
	
	private void set(String inId, String inUrl, String inName, String inSpecies, String inRevision)
	{
		id.set(inId);
		url.set(inUrl);
		name.set(inName);
		species.set(inSpecies);
		revision.set(inRevision);
	}
	
	public StringProperty  idProperty()  { return id;}
	public String getId()  { return id.get();}
	public void setId(String s)  { id.set(s);}

	public StringProperty  urlProperty()  { return url;}
	public String getUrl()  { return  "http://webservice.wikipathways.org/getPathway?pwId="  +getId() +  "&revision=0";		}
	public void setUrl(String s)  { url.set(s);}

	public StringProperty  nameProperty()  { return name;}
	public String getName()  { return name.get();}
	public void setName(String s)  { name.set(s);}

	public StringProperty  speciesProperty()  { return species;}
	public String getSpecies()  { return species.get();}
	public void setSpecies(String s)  { species.set(s);}

	public StringProperty  revisionProperty()  { return revision;}
	public String getRevision()  { return revision.get();}
	public void setRevision(String s)  { revision.set(s);}

	public String toString()	{		return id.get() + ": " + species.get() + ": " + name.get();	}


}
