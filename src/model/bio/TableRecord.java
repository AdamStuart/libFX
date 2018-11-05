package model.bio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import util.StringUtil;

public class TableRecord<ROWTYPE extends Map>
{
	protected List<TableColumn<ROWTYPE, ?>> allColumns;
	public List<TableColumn<ROWTYPE, ?>> getAllColumns() 			{ 	return allColumns;	}
	public void addColumn(TableColumn<ROWTYPE, ?> col, String name) { 	allColumns.add(col); 	}// columnNames.add(name);

	//	protected List<String> columnNames;
//	public List<String> getColumnNames() { return columnNames;	}

	StringProperty id = new SimpleStringProperty();
	StringProperty type = new SimpleStringProperty();
	StringProperty name = new SimpleStringProperty();
	BooleanProperty visible = new SimpleBooleanProperty();
	BooleanProperty editable = new SimpleBooleanProperty();
	List<String> headers = new ArrayList<String>();

	public TableRecord(String n)
	{
		name.set(n);
		allColumns = new ArrayList<TableColumn<ROWTYPE, ?>>();
//		columnNames = new ArrayList<String>();
	}
	
//	public TableRecord(TableRecord parent)
//	{
//		this(parent.getName());
//		allColumns.addAll(parent.getAllColumns());
////		columnNames.addAll(parent.getColumnNames());
//		header1.set(parent.getHeader1());
//		header2.set(parent.getHeader2());
//
//	}
	public String toString()
	{
		return id + ":" + name +  ":" + type +  ":"; // + allColumns.size() + " cols, " + getRowCount() + " rows";
	}
	private int getRowCount()
	{
		return -1;
	}

	public void setVisColumns(List<String> vis)
	{
		int index = 0;
		for (String name : vis)
		{
			TableColumn col = findByText(allColumns, name);
			if (col != null)
				if (allColumns.remove(col))
					allColumns.add(index++, col);
		}
	}
	
	public TableColumn findByText(List<TableColumn<ROWTYPE, ?>> cols, String txt)
	{
		if (txt == null) return null;
		for (TableColumn t : cols)
			if (txt.equals(t.getText()))
				return t;
		return null;
	}
//	public void reorderColumns(int src, int targ)
//	{
//		int siz = allColumns.size();
//		if (src < 0 || targ < 0 || src >= siz || targ >= siz )
//			return;
//			
//		TableColumn s = allColumns.remove(src);
//		allColumns.add(targ, s);
//	}
//	
//	public List<String> getVisColumns()
//	{
//		List<String> colNames = new ArrayList<String>();
//		for (TableColumn col : allColumns)
//		{
//			String s = col.getText();
//			if (s.startsWith("---")) break;
//			colNames.add(s);
//		}
//		return colNames;
//	}
//
	int getValueIndex(String colName)
	{
		if (StringUtil.isEmpty(colName)) return -1;
//		colName = colName.replace(".",  "");
		String header = headers.get(0);
		String[] fields = header.split("\t");
		for (int i=0; i<fields.length; i++)
			if (fields[i].equals(colName))
				return i;
		return -1;
	}
	
	public StringProperty  idProperty()  { return id;}
	public String getId()  { return id.get();}
	public void setId(String s)  { id.set(s);}

	public StringProperty  typeProperty()  { return type;}
	public String getType()  { return  type.get();		}
	public void setType(String s)  { type.set(s);}

	public StringProperty  nameProperty()  { return name;}
	public String getName()  { return name.get();}
	public void setName(String s)  { name.set(s);}

	public BooleanProperty  visibleProperty()  { return visible;}
	public Boolean getVisible()  { return visible.get();}
	public void setVisible(Boolean s)  { visible.set(s);}
	
	public BooleanProperty  editableProperty()  { return editable;}
	public Boolean getEditable()  { return editable.get();}
	public void setEditable(Boolean s)  { editable.set(s);}

	public boolean hasHeaders()  { return headers != null && headers.size() > 0;}
	public String getHeader(int i)  { return headers.get(i);}
	public void addHeader(String s)  { headers.add(s);}

//	public void setColumnList() {
//		String header = header1.get();
//		int skipColumns = 4;
//		String[] fields = header.split("\t");
//		for (int i=skipColumns; i<fields.length; i++)
//		{
//			String fld = fields[i];
//			String format =  "%4.2f";
//			TableColumn<ROWTYPE, Object> column = makeNumericColumn(fld, format);
//			addColumn(column, fld);
//		}
//	}
	protected TableColumn<ROWTYPE, Object> makeNumericColumn(String fld, String format)
	{
		TableColumn<ROWTYPE, Object> column = new TableColumn<ROWTYPE, Object>(fld);
		column.getProperties().put("Numeric", "TRUE");
		column.getProperties().put("Format", format);
		column.setCellValueFactory(new Callback<CellDataFeatures<ROWTYPE, Object>, ObservableValue<Object>>() {
		     public ObservableValue<Object> call(CellDataFeatures<ROWTYPE, Object> p) {
		    	 ROWTYPE row = p.getValue();
		    	 Object obj = row.get(fld);
		        if (obj instanceof Double)
		        {	
		        	double d = (Double) obj;
		        	return new ReadOnlyObjectWrapper(String.format(format, d));
		        }
		        return new ReadOnlyObjectWrapper(obj.toString());
		     }
		  });
		return column;
	}

}
