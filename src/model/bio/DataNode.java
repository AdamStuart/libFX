package model.bio;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.TableType;
import util.StringUtil;

public class DataNode extends XRefable implements Comparable<DataNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String URL_BASE = "http://";
	private GeneSetRecord geneListRecord;
	private List<Double> values = new ArrayList<Double>();
	public double getValue(int i)	{ return i >= 0 && i < size() ? values.get(i) : Double.NaN;	}

	public DataNode(GeneSetRecord record, TableType type, String[] line)
	{
		this(record, line[0], "", null, "Human", "");
		if (line.length > 0)
			graphid.set(line[0]);
		if (line.length == 10)
		{
			geneListRecord = (GeneSetRecord) record;
//			adjPval.set(StringUtil.toDouble(line[1]));
//			pval.set(StringUtil.toDouble(line[2]));
//			foldChange.set(StringUtil.toDouble(line[3]));
//			symbol.set(line[4]);		
			title.set(line[5]);		
//			entrez.set(line[6]);
//			goFunction.set(line[7]);
//			goProcess.set(line[8]);
//			goComponent.set(line[9]);
		}
		
	}

	public DataNode(GeneSetRecord record, TableType type, String line)
	{
		this(record, line.split(type.getDelimiter())[0], "", null, "Unspecified", "");
		
	}
	public DataNode(GeneSetRecord record, String inName, String inGraphId)
	{
		this(record, inName, inGraphId, null, "Unspecified", "");
	}
	public DataNode(TableRecord record, String inName, String inGraphid, String ensm, String spec, String link)
	{
		geneListRecord = (GeneSetRecord) record;
		graphid.set(inGraphid);
		name.set(inName);
		ensembl.set(ensm);			// this is also called id
		species.set(spec);
		url.set(link);
	}
	private SimpleStringProperty idlist = new SimpleStringProperty("");		// all identifiers in one string
	public StringProperty  idlistProperty()  { return idlist;}
	public String getIdlist()  { return idlist.get();}
	public void setIdlist(String s)  { idlist.set(s);}
	
	private SimpleStringProperty ensembl = new SimpleStringProperty("ensembl");
	public StringProperty  ensemblProperty()  { return ensembl;}
	public String getEnsembl()  { return ensembl.get();}
	public void setEnsembl(String s)  { ensembl.set(s); 	}
//	public String getId()  { return ensembl.get();}
//	public void setIdl(String s)  { ensembl.set(s);}

	private SimpleStringProperty species = new SimpleStringProperty("species");
	public StringProperty  speciesProperty()  { return species;}
	public String getSpecies()  { return species.get();}
	public void setSpecies(String s)  { species.set(s);}

	private SimpleStringProperty url = new SimpleStringProperty("url");
	public StringProperty  urlProperty()  { return url;}
	public String getUrl()  { return url.get();}
	public void setUrl(String s)  { url.set(s);}

	private SimpleStringProperty flag = new SimpleStringProperty("flag");
	public StringProperty  flagProperty()  { return flag;}
	public String getFlag()  { return flag.get();}
	public void setFlag(String s)  { flag.set(s);}

//	private SimpleStringProperty chromosome = new SimpleStringProperty("chromosome");
//	public StringProperty  chromosomeProperty()  { return chromosome;}
//	public String getChromosome()  { return chromosome.get();}
//	public void setChromosome(String s)  { chromosome.set(s);}
//
//
//	StringProperty symbol = new SimpleStringProperty();
//	public StringProperty symbolProperty()  { return symbol;}
//	public String getSymbol()  { return symbol.get();}
//	public void setSymbol(String s)  { symbol.set(s);}
//
	StringProperty title = new SimpleStringProperty();
	public StringProperty  titleProperty()  { return title;}
	public String getTitle()  { return title.get();}
	public void setTitle(String s)  { title.set(s);}
//
//
//	DoubleProperty pval = new SimpleDoubleProperty();
//	DoubleProperty adjPval = new SimpleDoubleProperty();
//	DoubleProperty foldChange = new SimpleDoubleProperty();
//	StringProperty entrez = new SimpleStringProperty();
//	StringProperty goFunction = new SimpleStringProperty();
//	StringProperty goProcess = new SimpleStringProperty();
//	StringProperty goComponent = new SimpleStringProperty();
//
	
//	public DoubleProperty  pvalProperty()  { return pval;}
//	public Double getPval()  { return pval.get();}
//	public void setPval(Double s)  { pval.set(s);}
//
//	public DoubleProperty  adjPvalProperty()  { return adjPval;}
//	public Double getAdjPval()  { return adjPval.get();}
//	public void setAdjPval(Double s)  { adjPval.set(s);}
//
//	public DoubleProperty  foldChangeProperty()  { return foldChange;}
//	public Double getFoldChange()  { return foldChange.get();}
//	public void setFoldChange(Double s)  { foldChange.set(s);}
//
//	public StringProperty  entrezProperty()  { return entrez;}
//	public String getEntrez()  { return entrez.get();}
//	public void setEntrez(String s)  { entrez.set(s);}
//
//	public StringProperty  goFunctionProperty()  { return goFunction;}
//	public String getGoFunction()  { return goFunction.get();}
//	public void setGoFunction(String s)  { goFunction.set(s);}
//
//	public StringProperty goProcessProperty()  { return goProcess;}
//	public String getGoProcess()  { return goProcess.get();}
//	public void setGoProcess(String s)  { goProcess.set(s);}
//
//	public StringProperty  goComponentProperty()  { return goComponent;}
//	public String getGoComponent()  { return goComponent.get();}
//	public void setGoComponent(String s)  { goComponent.set(s);}
//
	public String getValue(String fld) 
	{
//		if ("GO.Component".equals(fld))	return getGoComponent();
//		if ("GO.Process".equals(fld))	return getGoProcess();
//		if ("GO.Function".equals(fld))	return getGoFunction();
//		if ("EntrezGene".equals(fld))	return getEntrez();
//		if ("Gene.symbol".equals(fld))	return getSymbol();
		if ("Gene.title".equals(fld))	return getTitle();
		if ("ID".equals(fld))	return getDbid();
		double d = getValueByName(fld) ;
		if (Double.isNaN(d))
			return get(fld);
		return String.format("%5.2f", d);
//		return"";
	}
	
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
//		System.out.println(description.get());
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
//		System.out.println(description.get());
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
	public String toString() {				return getGraphId();	}   // + ": " + getTitle() + " " + getSymbol();
	public int compareTo(DataNode other)
	{
		return getName().compareToIgnoreCase(other.getName());
	}
	
	public boolean match(String upperCaseKeyword)
	{
		String name = getTitle();
		if (name != null)
		{
			String NAME = name.toUpperCase();
			if (NAME.contains(upperCaseKeyword)) return true;
		}
		String data = getData();
		if (data != null)
		{
			String DATA = data.toUpperCase();
			if (DATA.contains(upperCaseKeyword)) return true;
		}
		String title = getTitle();
		if (title != null)
		{
			String TITLE = title.toUpperCase();
			if (TITLE.contains(upperCaseKeyword)) return true;
		}
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
//		if ("logFC".equals(fld)) return getFoldChange();
//		if ("P.Value".equals(fld)) return getPval();
//		if ("adj.P.Val".equals(fld)) return getAdjPval();
		
		int index = geneListRecord.getValueIndex(fld);
		if (index < 0) return Double.NaN;
		return getValue(index);
	
	}
}


