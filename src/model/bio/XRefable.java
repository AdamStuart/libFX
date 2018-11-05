package model.bio;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.AttributeMap;
import util.StringUtil;

abstract public class XRefable extends AttributeMap
{
	protected SimpleStringProperty name = new SimpleStringProperty();		// HGNC
	public XRefable()
	{
		this("", "","","","");
	}
	public XRefable(String inName)
	{
		this(inName, "","","","");
	}
	public XRefable(AttributeMap other)
	{
		this();
		if (other != null)
		for(String s: other.keySet())
			put(s,other.get(s));
		}
	public void copyAttributesToProperties()
	{
		String s;
		s = get("Name"); 
		if (s != null) 
			setName(s);
		s = get("GraphId");
		if (s != null) 
			setGraphId(s);
		s = get("Database"); if (s != null) setDatabase(s);
		s = get("ID"); if (s != null) setDbid(s);
		s = get("Type"); if (s != null) setType(s);
	}
	public void copyPropertiesToAttributes()
	{
		put("Name", getName());
		put("GraphId", getGraphId());
		put("Database", getDatabase());
		put("Dbid", getDbid());
		put("Type", getType());
	}
	public XRefable(String inName, String inId, String inDb, String inDbid, String inType)
	{
		setName(inName);
		setGraphId(inId);
		setDatabase(inDb);
		setDbid(inDbid);
		setType(inType);
	}
//	public void setProperties() {
//		setName(get("TextLabel"));
//		setGraphId(get("GraphId"));
//		setDatabase(get("Database"));
//		setDbid(get("ID"));
//		setType(get("Type"));
//	}
	
	String[]  xrefattrs = {  "Database", "ID"};
	protected void buildXRefTag(StringBuilder bldr)
	{
		String attributes = attributeList(xrefattrs);
		if (StringUtil.hasText(attributes))
			bldr.append( "<Xref ").append(attributes).append( " >\n");
	}
	
	protected String attributeList(String[] strs)
	{
		StringBuilder bldr = new StringBuilder();
		for (String attr : strs)
		{
			String val = get(attr);
			if (val != null)
				bldr.append(attr + "=\"" + val + "\" ");
		}
		return bldr.toString();
	}

	DoubleProperty valuePropety = new SimpleDoubleProperty();
	public DoubleProperty  valueProperty()  { return valuePropety;}
	public Double getValue()  { return valuePropety.get();}
	public void setValue(Double s)  { valuePropety.set(s);}
	
	public StringProperty  nameProperty()  { return name;}
	public String getName()  { return name.get();}
	public void setName(String s)  { name.set(s);}
	
	StringProperty graphid = new SimpleStringProperty();
	public StringProperty  graphidProperty()  { return graphid;}
	public String getGraphId()  { return graphid.get();}
	public void setGraphId(String s)  { graphid.set(s);}

	protected SimpleStringProperty database = new SimpleStringProperty("");
	public StringProperty  databaseProperty()  { return database;}
	public String getDatabase()  { return database.get();}
	public void setDatabase(String s)  { database.set(s);}

	protected SimpleStringProperty dbid = new SimpleStringProperty("");
	public StringProperty  dbidProperty()  { return dbid;}
	public String getDbid()  { return dbid.get();}
	public void setDbid(String s)  { dbid.set(s);}

	protected SimpleStringProperty type = new SimpleStringProperty("type");
	public StringProperty  typeProperty()  { return type;}
	public String getType()  { return type.get();}
	public void setType(String s)  { type.set(s);}

	public String toString()	{ return getName() + " (" + getGraphId() + ")"; }
}
