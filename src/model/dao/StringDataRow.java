package model.dao;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StringDataRow 	 
{
    IntegerProperty rowNum = new SimpleIntegerProperty(0);
    private ObservableList<SimpleStringProperty> vals = FXCollections.observableArrayList();
	
    public StringDataRow(int nCols)
 	{
         for(int i=0; i<nCols; ++i)
         	vals.add(new SimpleStringProperty(""));
 	}
    public StringDataRow(String[] strs)
 	{
         for(String s : strs)
         	vals.add(new SimpleStringProperty(s));
 	}
    public int getRowNum()				{	return rowNum.get();		}
	public void setRowNum(int row)		{ 	rowNum.set(row); } 
	public void set(int i, String s)	{ 	vals.get(i).set(s);	} 
    public StringProperty get(int i) 	{ 	return vals.get(i); }
    public int getWidth()				{ 	return vals.size();	}
}
