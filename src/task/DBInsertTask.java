package task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import util.DBUtil;
import util.StringUtil;

//------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------

public class DBInsertTask<T> extends DBTask<T>
{
	 Map<String, String> fields;  
    public DBInsertTask(DatabaseAccess db,  Map<String, String> fieldsMap)
    {
    	super(db);
    	fields = fieldsMap;
    }
      
	@Override protected T call() throws Exception
	{
		try (Connection con = DBUtil.getConnection())
		{
			populateDatabase(con);
		}
		return null;
	}

	private String getValList()
	{
		if (fields == null || fields.size() == 0) return "";
		StringBuffer buffer = new StringBuffer(" VALUES (");
		for (String v : fields.keySet())  
			buffer.append("\'" + fields.get(v) + "\', ");
		String valList = StringUtil.chopLast2(buffer.toString()) + ")";
		return valList;
	}
	
	private String getFieldList()
	{
		if (fields == null || fields.size() == 0) return "";
		StringBuffer buffer = new StringBuffer(" (");
		for (String f : fields.keySet())  buffer.append(f + ", ");
		String fldList = StringUtil.chopLast2(buffer.toString()) + ")";
		return fldList;
	}
	
	private void populateDatabase(Connection con) throws SQLException
	{
		Statement st = con.createStatement();
		String statement = "insert into " +  database.getTableName() + getFieldList() + getValList() + ";";
		st.executeUpdate(statement);
		logger.info("Executed statement: " + statement);
	}
}


