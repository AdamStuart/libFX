package model;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import util.StringUtil;

public class TableData
{
	private List<StringUtil.TYPES> types;
	private List<TableColumn> columns;
	private List<ObservableList<Double>> data;
	
	public TableData()
	{
		types = new ArrayList<StringUtil.TYPES>();
		columns = new ArrayList<TableColumn>();
		data = new ArrayList<ObservableList<Double>>();
	}
	
	public TableData(TableData orig)
	{
		types = new ArrayList<StringUtil.TYPES>(orig.getTypes());
		columns = new ArrayList<TableColumn>(orig.getColumns());
		data = new ArrayList<ObservableList<Double>>(orig.getData());
	}
	
	public  List<StringUtil.TYPES> getTypes() { return types; }
	public  List<TableColumn> getColumns() { return columns; }
	public  List<ObservableList<Double>> getData() { return data; }

	public  void  setTypes(List<StringUtil.TYPES> t) { types = t; }
	public  void  setColumns(List<TableColumn> c) { columns =c; }
	public  void  setData(List<ObservableList<Double>> d) {  data = d; }

}
