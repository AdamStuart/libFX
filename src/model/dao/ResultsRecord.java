package model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.bio.Species;
import services.bridgedb.MappingSource;
/***
 * ResultsRecord has some set of fields that you can get by name
 * In a table, the column head can be used as the key to look up fields
 * 
 * @author adamtreister
 *
 */
public class ResultsRecord {

	public ResultsRecord()
	{
		map = new HashMap<String, StringProperty>();
		value0.set(Math.random());
		value1.set(Math.random());
	}
	public ResultsRecord(String inSpecies, String orig)
	{
		this(Species.lookup(inSpecies), orig);
	}
	public ResultsRecord(Species inSpecies, String orig)
	{
		this();
		species = inSpecies;
		setOriginal(orig);
		MappingSource src = MappingSource.guessSource(inSpecies,orig);
		setSource(src==null ? "?": src.system());
	}
	public ResultsRecord(List<String> targets)
	{
		this();
		int sz = Math.min(8, targets.size());
		for (int i=0; i<sz; i++)
			map.put(targets.get(i), results[i]);
	}
	public StringProperty resultProperties(String colName)
	{
		String system = MappingSource.nameToSystem(colName);
		StringProperty s = map.get(system);
		return (s == null) ? new SimpleStringProperty("X") : s;
	}
	
	Map<String, StringProperty> map;
	public void setByName(String target, String mappedId) {
//		MappingSource source = MappingSource.systemLookup(target);
		StringProperty prop = map.get(target);
		if (prop != null) 	prop.set(mappedId);
		else 
		{
			prop = new SimpleStringProperty(mappedId);
			map.put(target, prop);
		}
	}
	public void reguess(Species inSpecies) 			
	{		
		species = inSpecies;
		MappingSource src = MappingSource.guessSource(inSpecies, getOriginal());
		setSource(src.system());
	}

	Species species = null;		// species is needed to guess Ids
	public int getNResults() 					{		return 8;	}

	public String toString() {  return getOriginal() + " " + result0; }
	
	public String getOriginal() 				{		return original.get();	}
	public void setOriginal(String s) 			{		original.set(s);	}
	public StringProperty originalProperty()	{		return original;	}

	public String getSource() 					{		return source.get();	}
	public void setSource(String s) 			{		source.set(s);	}
	public StringProperty sourceProperty()		{		return source;	}

	// JUNK -- replace with mapped properties
//------------------
	DoubleProperty value0 = new SimpleDoubleProperty();
	DoubleProperty value1 = new SimpleDoubleProperty();
	public Double getValue0() 				{		return value0.get();	}
	public Double getValue1() 				{		return value1.get();	}
	public void setValue0(Double d) 		{		value0.set(d);	}
	public void setValue1(Double d) 		{		value1.set(d);	}
	public DoubleProperty value0Property() 	{		return value0;	}
	public DoubleProperty value1Property() 	{		return value1;	}

	//------------------
	StringProperty result0 = new SimpleStringProperty();
	StringProperty result1 = new SimpleStringProperty();
	StringProperty result2 = new SimpleStringProperty();
	StringProperty result3 = new SimpleStringProperty();
	StringProperty result4 = new SimpleStringProperty();
	StringProperty result5 = new SimpleStringProperty();
	StringProperty result6 = new SimpleStringProperty();
	StringProperty result7 = new SimpleStringProperty();
	StringProperty original = new SimpleStringProperty();
	StringProperty source = new SimpleStringProperty();
	
	StringProperty[] results = { result0, result1, result2, result3, result4, result5, result6, result7 }; 



	public String getResult0() 				{		return result0.get();	}
	public String getResult1() 				{		return result1.get();	}
	public String getResult2() 				{		return result2.get();	}
	public String getResult3() 				{		return result3.get();	}
	public String getResult4() 				{		return result4.get();	}
	public String getResult5() 				{		return result5.get();	}
	public String getResult6() 				{		return result6.get();	}
	public String getResult7() 				{		return result7.get();	}

	public StringProperty result0Property() 				{		return result0;	}
	public StringProperty result1Property() 				{		return result1;	}
	public StringProperty result2Property() 				{		return result2;	}
	public StringProperty result3Property() 				{		return result3;	}
	public StringProperty result4Property() 				{		return result4;	}
	public StringProperty result5Property() 				{		return result5;	}
	public StringProperty result6Property() 				{		return result6;	}
	public StringProperty result7Property() 				{		return result7;	}

	
	public void setResult0(String s) 		{		result0.set(s);	}
	public void setResult1(String s) 		{		result1.set(s);	}
	public void sgetResult2(String s) 		{		result2.set(s);	}
	public void setResult3(String s) 		{		result3.set(s);	}
	public void setResult4(String s) 		{		result4.set(s);	}
	public void setResult5(String s) 		{		result5.set(s);	}
	public void setResult6(String s) 		{		result6.set(s);	}
	public void setResult7(String s) 		{		result7.set(s);	}
	
	
}
