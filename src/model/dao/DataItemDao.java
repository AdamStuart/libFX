package model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataItemDao {

	public static List<DataItem> importFromFile(String filePath, FileFormat fileFormat) {
		if (filePath != null)
		{
			SortedMap<Number, Number> data = null;
			switch (fileFormat) {
			case CSV:	data = CsvDao.importCsv(filePath);		break;		
//			case XLS: 	data = XlsDao.importXls(filePath);		break;
			case HDF5: 	data = Hdf5Dao.importHdf5(filePath);	break;	
			case FCS: 	data = FcsDao.importFcs(filePath);		break;	
			}
			if (data != null)
				return createDataItems(data);
		}
		return FXCollections.observableArrayList();
	}

	public static void exportToFile(List<DataItem> dataItems, String filePath, FileFormat fileFormat) {
		SortedMap<Number, Number> data = createEntries(dataItems);
		if (filePath != null) {
			switch (fileFormat) {
			case CSV:	CsvDao.exportCsv(data, filePath);		break;
//			case XLS:	XlsDao.exportXls(data, filePath);		break;
			case HDF5:	Hdf5Dao.exportHdf5(data, filePath);		break;
			case FCS: 	FcsDao.exportFcs(makeFCSText(), makeTableData(dataItems), filePath);		break;	
			}
		}
	}
	static TableData makeTableData (List<DataItem> list)
	{
		return new TableData();
	}
	static HashMap<String, String> makeFCSText()
	{
		HashMap<String, String> map = new HashMap<String, String>();
		return map;
	}
	private static SortedMap<Number, Number> createEntries(List<DataItem> dataItems) 
	{
		SortedMap<Number, Number> data = new TreeMap<>();
		for (DataItem item : dataItems) 
			data.put(item.getX(), item.getY());
		return data;
	}

	private static List<DataItem> createDataItems(SortedMap<Number, Number> data)
	{
		ObservableList<DataItem> items = FXCollections.observableArrayList();
		for (Number x : data.keySet()) {
			Number y = data.get(x);
			DataItem item = new DataItem(x, y);
			items.add(item);
		}

		return items;
	}

	public enum FileFormat {
		CSV("csv"), XLS("xls"), HDF5("h5"), FCS("fcs"), FXML("fxml");

		private final String extension;
		FileFormat(String ex) 				{	extension = ex;		}
		public String getFileExtension() 	{	return extension;		}
	}

}
