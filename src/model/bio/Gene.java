package model.bio;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.AttributeMap;
import util.StringUtil;

public class Gene implements Comparable<Gene>{

	static String URL_BASE = "http://";
	
	
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
	
	private SimpleStringProperty idlist = new SimpleStringProperty();		// all identifiers in one string
	public StringProperty  idlistProperty()  { return idlist;}
	public String getIdlist()  { return idlist.get();}
	public void setIdlist(String s)  { idlist.set(s);}
	
	private SimpleStringProperty ensembl = new SimpleStringProperty();
	public StringProperty  ensemblProperty()  { return ensembl;}
	public String getEnsembl()  { return ensembl.get();}
	public void setEnsembl(String s)  { ensembl.set(s);  System.out.println(s);	}
	public String getId()  { return ensembl.get();}
	public void setIdl(String s)  { ensembl.set(s);}

	private SimpleStringProperty species = new SimpleStringProperty();
	public StringProperty  speciesProperty()  { return species;}
	public String getSpecies()  { return species.get();}
	public void setSpecies(String s)  { species.set(s);}

	private SimpleStringProperty url = new SimpleStringProperty();
	public StringProperty  urlProperty()  { return url;}
	public String getUrl()  { return url.get();}
	public void setUrl(String s)  { url.set(s);}

	private SimpleStringProperty dababase = new SimpleStringProperty();
	public StringProperty  dababaseProperty()  { return dababase;}
	public String getDababase()  { return dababase.get();}
	public void setDababase(String s)  { dababase.set(s);}

	private SimpleStringProperty dbid = new SimpleStringProperty();
	public StringProperty  dbidProperty()  { return dbid;}
	public String getDbid()  { return dbid.get();}
	public void setDbid(String s)  { dbid.set(s);}

	private SimpleStringProperty data = new SimpleStringProperty();
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
		name.set(firstWord);
		if (parts.length > 1)
			description.set(parts[1].trim());
	}
	
	private SimpleStringProperty description = new SimpleStringProperty();
	public StringProperty  descriptionProperty()  { return description;}
	public String getDescription()  		{ 	return description.get();}
	public void setDescription(String s) 	{	description.set(s);		} 

	
	public AttributeMap getXRefs()
	{
		return null; 
	}
	public GeneList getRefs(Gene other)
	{
		return null; 
	}
	public String toString() {				return getName();	}
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
