package model.dao;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MixedDataRow 	 
{
    IntegerProperty rowNum = new SimpleIntegerProperty(0);
    private ObservableList<SimpleDoubleProperty> vals = FXCollections.observableArrayList();
    private ObservableList<SimpleStringProperty> strs = FXCollections.observableArrayList();
    private ObservableList<SimpleIntegerProperty> types = FXCollections.observableArrayList();
//    private ObservableList<SimpleDoubleProperty> mins = FXCollections.observableArrayList();
//    private ObservableList<SimpleDoubleProperty> maxs = FXCollections.observableArrayList();
	
    public MixedDataRow(int nCols)
	{
        for(int i=0; i<nCols; ++i)
        {
	        	vals.add(new SimpleDoubleProperty(0.));
	        strs.add(new SimpleStringProperty(""));
//	        types.add(new SimpleIntegerProperty(0));
//	        mins.add(new SimpleDoubleProperty(0.));
//	        maxs.add(new SimpleDoubleProperty(0.));
	    }
	}
    public int getRowNum()				{	return rowNum.get();		}
	public void setRowNum(int row)		{ 	rowNum.set(row); } 
	public void set(int i, Double s)		{ 	vals.get(i).set(s);	} 
	public void set(int i, String s)		{ 	strs.get(i).set(s);	} 
    public DoubleProperty get(int i) 		{ 	return vals.get(i); }
    public StringProperty getString(int i) 	{ 	return strs.get(i); }
    public IntegerProperty getType(int i) 	{ 	return types.get(i); }
//    public DoubleProperty getMin(int i) 	{ 	return mins.get(i); }
//    public DoubleProperty getMax(int i) 	{ 	return maxs.get(i); }
    
    public boolean isString(int i)		{ return types.get(i).intValue() == 0; }
    public boolean isBool(int i)			{ return types.get(i).intValue() == 1; }
    public boolean isNumber(int i)		{ return types.get(i).intValue() > 1; }
    
    public int getWidth()				{ 	return vals.size();	}
}
