package util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.util.Callback;
import model.AttributeValue;

public class TableUtil
{
	public static TableColumn createColumn(final int columnIndex, String columnTitle)
	{
		TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
		String title;
		if (columnTitle == null || columnTitle.trim().length() == 0)
			title = "Column " + (columnIndex + 1);
		else
			title = columnTitle;
		column.setText(title);
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>()
		{
			public StringProperty call(CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures)
			{
				ObservableList<StringProperty> values = cellDataFeatures.getValue();
				if (columnIndex >= values.size())
					return new SimpleStringProperty("C"+ (columnIndex + 1));
				return values.get(columnIndex);
			}
		});
//		column.setCellValueFactory(new PropertyValueFactory(columnTitle));
		return column;
	}

	public static void selectRow(TableView<AttributeValue> tableView, TableRow<AttributeValue> row)
	{
		if (tableView != null && row != null)
		{
			TableViewSelectionModel selMod = tableView.getSelectionModel();
			int idx = row.getIndex();
			if (row.isSelected())
				selMod.clearSelection(idx);
			 else
				 selMod.select(idx);
		}
	}

	public static void clearRowsAndColumns(TableView<?> inTable)
	{
		if (inTable != null)
		{
			inTable.getItems().removeAll(inTable.getItems());
			inTable.getColumns().removeAll(inTable.getColumns());
		}
	}

//	public static void addSelectionListener(TableView table, ChangeListener object)
//	{
//		table.getSelectionModel().selectedItemProperty().addListener(object);		
//	}		

	
	
}
