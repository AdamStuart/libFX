package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class IntegerDataRow 	 
{
    IntegerProperty rowNum = new SimpleIntegerProperty(0);
    private ObservableList<SimpleIntegerProperty> vals = FXCollections.observableArrayList();
	
    public IntegerDataRow(int nCols)
	{
    	for(int i=0; i<nCols; ++i)
        	vals.add(new SimpleIntegerProperty(0));
	}
    public IntegerProperty getRowNum()	{	return rowNum;		}
	public void setRowNum(int row)		{ 	rowNum.set(row); } 
	public void set(int i, Integer s)	{ 	vals.get(i).set(s);	} 
    public IntegerProperty get(int i) 	{ 	return vals.get(i); }
    public int getWidth()				{ 	return vals.size();	}
    public void addPColumn(int v)		{	vals.add(new SimpleIntegerProperty(v)); }
}
