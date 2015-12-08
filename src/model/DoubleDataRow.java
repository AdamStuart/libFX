package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DoubleDataRow 	 
{
    IntegerProperty rowNum = new SimpleIntegerProperty(0);
    private ObservableList<SimpleDoubleProperty> vals = FXCollections.observableArrayList();
	
    public DoubleDataRow(int nCols)
	{
        for(int i=0; i<nCols; ++i)
        	vals.add(new SimpleDoubleProperty(0.));
	}
    public int getRowNum()				{	return rowNum.get();		}
	public void setRowNum(int row)		{ 	rowNum.set(row); } 
	public void set(int i, Double s)	{ 	vals.get(i).set(s);	} 
    public DoubleProperty get(int i) 	{ 	return vals.get(i); }
    public int getWidth()				{ 	return vals.size();	}
}
