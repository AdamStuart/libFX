package model;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import util.StringUtil;

public class CSVTableData
{
	private List<StringUtil.TYPES> types;
	private List<String> columnNames;
	private List<ObservableList<String>> data;
	
	public CSVTableData()
	{
		types = new ArrayList<StringUtil.TYPES>();
		columnNames = new ArrayList<String>();
		data = new ArrayList<ObservableList<String>>();
	}
	
	public CSVTableData(CSVTableData orig)
	{
		types = new ArrayList<StringUtil.TYPES>(orig.getTypes());
		columnNames = new ArrayList<String>(orig.getColumnNames());
		data = new ArrayList<ObservableList<String>>(orig.getData());
	}
	
	public  List<StringUtil.TYPES> getTypes() { return types; }
	public  List<String> getColumnNames() { return columnNames; }
	public  List<ObservableList<String>> getData() { return data; }
	public  ObservableList<String> getData(int i) { return data.get(i); }

	public  void  setTypes(List<StringUtil.TYPES> t) { types = t; }
	public  void  setColumnNames(List<String> c) { columnNames =c; }
	public  void  setData(List<ObservableList<String>> d) {  data = d; }

//	public TableColumn<Record,String>[] getColumns()
//	{
//		List<TableColumn<Record,String>> list = new ArrayList<TableColumn<Record,String>>();
//		for (String col : columnNames)
//			list.add(new TableColumn<>(col));
//		return (TableColumn<Record,String>[] )list.toArray();
//	}
	

}
