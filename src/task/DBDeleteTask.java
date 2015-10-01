package task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import util.DBUtil;
import util.StringUtil;

//------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------

public class DBDeleteTask<T> extends DBTask<T>
{
	 Map<String, String> criteria;  
	   public DBDeleteTask(DatabaseAccess db,  Map<String, String> crit)
	    {
	    	super(db);
	    	criteria = crit;
	    }
	      
	   public DBDeleteTask(DatabaseAccess db, String id)
	    {
	    	super(db);
	    	criteria = FXCollections.observableHashMap();
	    	criteria.put("id", id);
	    }
	      
	@Override protected T call() throws Exception
	{
		try (Connection con = DBUtil.getConnection())
		{
			Statement st = con.createStatement();
			String statement = "DELETE FROM " +  database.getTableName() + " " + DBUtil.getWhere(criteria) + ";";
			st.executeUpdate(statement);
			logger.info("Executed statement: " + statement);
		}
		return null;
	}
}


