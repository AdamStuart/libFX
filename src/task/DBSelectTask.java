package task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------

public class DBSelectTask<T> extends DBTask<T>
{
	ObservableList<ObservableList<String>> data;
	ObservableList<String> colNames;
	WrapperClass logString;
    public DBSelectTask(DatabaseAccess db, ObservableList<ObservableList<String>> output, ObservableList<String> cols, WrapperClass log)
    {
    	super(db);
    	data = output;
    	colNames = cols;
    	logString = log;
    }
    
	@Override protected T call() throws Exception
	{
		try (Connection con = database.getConnection())
		{
			Statement st = con.createStatement();
			String statement = "SELECT * FROM " +  database.getTableName() + ";";
			ResultSet rs = st.executeQuery(statement);
			logger.info(statement);
			logString.set("Populating database: " + statement);
			if (rs != null  && !rs.isClosed())
			{
				try
				{
					for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++)
						colNames.add(rs.getMetaData().getColumnName(i));

					while ( rs.next() )
					{
						ObservableList<String> row = FXCollections.observableArrayList();
						for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++)
						{
							  row.add(rs.getString(i));
//							  System.err.println(rs.getString(i));
						 }
						data.add(row);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}  
			}

			return (T) rs;
		}
	}

//	private String getValList()
//	{
//		if (values == null || values.size() == 0) return "";
//		StringBuilder buffer = new StringBuilder(" VALUES (");
//		for (String v : values)  buffer.append("\'" + v + "\', ");
//		String valList = StringUtil.chopLast2(buffer.toString()) + ")";
//		return valList;
//	}
//	
//	private String getFieldList()
//	{
//		if (fields == null || fields.size() == 0) return "";
//		StringBuilder buffer = new StringBuilder(" (");
//		for (String f : fields)  buffer.append(f + ", ");
//		String fldList = StringUtil.chopLast2(buffer.toString()) + ")";
//		return fldList;
//	}
//	
}


