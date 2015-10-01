package task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import util.DBUtil;

//------------------------------------------------------------------------------------

public class DBDeleteTableTask<T> extends DBTask<T>
{
    String DELETE = "";

    public DBDeleteTableTask(DatabaseAccess db)
    {
    	super(db);
    	DELETE = "DROP TABLE " + db.getSchema();
    }
    
    public DBDeleteTableTask( String tableName)
    {
    	super(null);
    	DELETE = "DROP TABLE " + tableName;
    }
    
    
	@Override protected T call() throws Exception
	{
		try (Connection con = DBUtil.getConnection())
		{
			deleteTable(con);
		}
		return null;
	}

	private void deleteTable(Connection con) throws SQLException
	{
		logger.info("deleteTable");
		Statement st = con.createStatement();
		st.executeUpdate(DELETE);
	}


}


