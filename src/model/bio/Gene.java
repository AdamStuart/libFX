package model.bio;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.AttributeMap;
import util.StringUtil;

public class Gene implements Comparable<Gene> {

	public static String URL_BASE = "http://";
	private List<Double> values = new ArrayList<Double>();
	public double getValue(int i)	{ return i < values.size() ? values.get(i) : Double.NaN;	}
	
	public Gene(String inName)
	{
		this(inName, null, "Human", URL_BASE + "");
	}
	public Gene(String inName, String ensm, String spec, String link)
	{
		name.set(inName);
		ensembl.set(ensm);			// this is also called id
		species.set(spec);
		url.set(link);
	}
	private SimpleStringProperty name = new SimpleStringProperty();		// HGNC
	public StringProperty  nameProperty()  { return name;}
	public String getName()  { return name.get();}
	public void setName(String s)  { name.set(s);}
	
	private SimpleStringProperty idlist = new SimpleStringProperty("");		// all identifiers in one string
	public StringProperty  idlistProperty()  { return idlist;}
	public String getIdlist()  { return idlist.get();}
	public void setIdlist(String s)  { idlist.set(s);}
	
	private SimpleStringProperty ensembl = new SimpleStringProperty("ensembl");
	public StringProperty  ensemblProperty()  { return ensembl;}
	public String getEnsembl()  { return ensembl.get();}
	public void setEnsembl(String s)  { ensembl.set(s);  System.out.println(s);	}
	public String getId()  { return ensembl.get();}
	public void setIdl(String s)  { ensembl.set(s);}

	private SimpleStringProperty species = new SimpleStringProperty("species");
	public StringProperty  speciesProperty()  { return species;}
	public String getSpecies()  { return species.get();}
	public void setSpecies(String s)  { species.set(s);}

	private SimpleStringProperty url = new SimpleStringProperty("url");
	public StringProperty  urlProperty()  { return url;}
	public String getUrl()  { return url.get();}
	public void setUrl(String s)  { url.set(s);}

	private SimpleStringProperty database = new SimpleStringProperty("database");
	public StringProperty  databaseProperty()  { return database;}
	public String getDatabase()  { return database.get();}
	public void setDatabase(String s)  { database.set(s);}

	private SimpleStringProperty location = new SimpleStringProperty("23");
	public StringProperty  locationProperty()  { return location;}
	public String getLocation()  { return location.get();}
	public void setLocation(String s)  { location.set(s);}

	private SimpleStringProperty dbid = new SimpleStringProperty("db");
	public StringProperty  dbidProperty()  { return dbid;}
	public String getDbid()  { return dbid.get();}
	public void setDbid(String s)  { dbid.set(s);}

	private SimpleStringProperty flag = new SimpleStringProperty("flag");
	public StringProperty  flagProperty()  { return flag;}
	public String getFlag()  { return flag.get();}
	public void setFlag(String s)  { flag.set(s);}

	private SimpleStringProperty data = new SimpleStringProperty("data");
	public StringProperty  dataProperty()  { return data;}
	public String getData()  { return data.get();}
	public void setData(String s)  
	{ 
		data.set(s);
		String[] tokens = s.split("\t");
		String nameDesc = tokens[2];
		String[] parts = nameDesc.split(" ");
		String firstWord = parts[0];
		if (firstWord.startsWith("\""))
			firstWord = firstWord.substring(1);
		
		String remnant = nameDesc.substring(firstWord.length()).trim();
						if (parts.length > 1)
		readTerms(remnant);
		description.set(remnant);
		System.out.println(description.get());
		name.set(firstWord);
		for (int i = 3; i < tokens.length; i++)
			values.add(StringUtil.toDouble(tokens[i]));
	}
	
	private SimpleStringProperty description = new SimpleStringProperty();
	public StringProperty  descriptionProperty()  { return description;}
	public String getDescription()  		{ 	return description.get();}
	public void setDescription(String s) 	{	description.set(s);		} 

	private SimpleStringProperty termSummary = new SimpleStringProperty();
	public StringProperty  termSummaryProperty()  { return termSummary;}
	public String getTermSummary()  		{ 	return termSummary.get();}
	public void setTermSummary(String s) 	{	termSummary.set(s);		} 

	List<String> terms = new ArrayList<String>();
	private void readTerms(String description)
	{
		while (description.length() > 0){
			int start = Math.max(0, description.indexOf("  "));
			while (start < description.length() && description.charAt(start) == ' ')
				start++;
			
			int end = description.indexOf( "  ", start);
			if (end < 0)
			{
				terms.add(StringUtil.decapitalize(description.substring(start).trim()));
				description = "";
			}
			else
			{	
				terms.add(StringUtil.decapitalize(description.substring(start, end).trim()));
				description = description.substring(end);
			}
		}
		final String delim = " | ";
		StringBuilder builder = new StringBuilder();
		for (String term : terms)
			builder.append(term + delim);
		setTermSummary(StringUtil.chop(builder.toString(), delim.length()));
	}
	
	public AttributeMap getXRefs()
	{
		return null; 
	}
	public List<Gene> getRefs(Gene other)
	{
		return null; 
	}
	public String toString() {				return getId() + ": " + getName();	}
	public int compareTo(Gene other)
	{
		return getName().compareToIgnoreCase(other.getName());
	}
	public void getInfo()
	{
		   String text = getIdlist();	
		    if (StringUtil.hasText(text))
		   {  
			   text = text.replace(",", "\n");
			   Alert a = new Alert(AlertType.INFORMATION, text);
			   a.setHeaderText("Gene Info Dialog");
			   a.getDialogPane().setMinWidth(600);
			   a.setResizable(true);
			   a.showAndWait();
		   }

		
	}
}
