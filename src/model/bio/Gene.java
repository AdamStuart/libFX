package model.bio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.AttributeMap;
import model.TableType;
import util.StringUtil;

public class Gene extends HashMap<String, String> implements Comparable<Gene> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String URL_BASE = "http://";
	private GeneListRecord geneListRecord;
	private List<Double> values = new ArrayList<Double>();
	public double getValue(int i)	{ return i >= 0 && i < size() ? values.get(i) : Double.NaN;	}

	public Gene(GeneListRecord record, TableType type, String line)
	{
		this(record, line.split(type.getDelimiter())[0], null, "Human", "");
	}
	public Gene(GeneListRecord record, String inName)
	{
		this(record, inName, null, "Human", URL_BASE + "");
	}
	public Gene(TableRecord record, String inName, String ensm, String spec, String link)
	{
		geneListRecord = (GeneListRecord) record;
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

//	private SimpleStringProperty location = new SimpleStringProperty("23");
//	public StringProperty  locationProperty()  { return location;}
//	public String getLocation()  { return location.get();}
//	public void setLocation(String s)  { location.set(s);}

	private SimpleStringProperty dbid = new SimpleStringProperty("db");
	public StringProperty  dbidProperty()  { return dbid;}
	public String getDbid()  { return dbid.get();}
	public void setDbid(String s)  { dbid.set(s);}

	private SimpleStringProperty flag = new SimpleStringProperty("flag");
	public StringProperty  flagProperty()  { return flag;}
	public String getFlag()  { return flag.get();}
	public void setFlag(String s)  { flag.set(s);}

	private SimpleStringProperty chromosome = new SimpleStringProperty("chromosome");
	public StringProperty  chromosomeProperty()  { return chromosome;}
	public String getChromosome()  { return chromosome.get();}
	public void setChromosome(String s)  { chromosome.set(s);}

	private SimpleStringProperty data = new SimpleStringProperty("data");
	public StringProperty  dataProperty()  { return data;}
	public String getData()  { return data.get();}
	public void setData(String s, TableType type)  
	{ 
		data.set(s);
		String[] tokens = s.split(type.getDelimiter());
		if (tokens.length < 3) 
		{
			if (tokens.length == 1) 
				name.set(tokens[0]);
			return;
		}
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
		for (int i = 8; i < tokens.length; i++)
			values.add(StringUtil.toDouble(tokens[i]));
	}
	

	public void setData2(String s)  
	{ 
		data.set(s);
		String[] tokens = s.split("\t");
		String nameDesc = (tokens.length < 3) ? "" : tokens[2];
		ensembl.set( tokens[0]);
		name.set( tokens[1]);
//		String[] parts = nameDesc.split(" ");
//		String firstWord = parts[0];
//		if (firstWord.startsWith("\""))
//			firstWord = firstWord.substring(1);
		
//		String remnant = nameDesc.substring(firstWord.length()).trim();
//		if (parts.length > 1)
//			readTerms(remnant);
		int start = nameDesc.indexOf("[");
		if (start >= 0)
		{
			String firstname = nameDesc.substring(0, start);
			int end = nameDesc.indexOf("]");
			if (end > 0)
			{
				String source = nameDesc.substring(start+1, end-1);
				if (source.contains("Source"))
				{
					int semi = source.indexOf(";");
					database.set(source.substring(7, semi));
					dbid.set(source.substring(semi+1));
				}
			}
		}
		
		description.set(nameDesc);
		System.out.println(description.get());
		for (int i = 8; i < tokens.length; i++)
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
	
	public boolean match(String upperCaseKeyword)
	{
		String NAME = getName().toUpperCase();
		if (NAME.contains(upperCaseKeyword)) return true;
		String DATA = getData().toUpperCase();
		if (DATA.contains(upperCaseKeyword)) return true;
		return false;
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

	public double getValueByName(String fld) 
	{
		int index = geneListRecord.getValueIndex(fld);
		if (index < 0) return Double.NaN;
		return getValue(index);
	}
}
