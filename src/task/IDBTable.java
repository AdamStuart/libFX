package task;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.layout.Region;

public interface IDBTable
{
	public Region getForm();
	public String getTableName();
	public String getSchema();
	public ObservableList<String> getFieldList();
	public void install(ObservableMap<String, String> fields);
	public void extract(ObservableMap<String, String> fields);

};
