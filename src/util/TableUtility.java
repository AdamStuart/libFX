package util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class TableUtility
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

}
