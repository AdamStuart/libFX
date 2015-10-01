package task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import util.DBUtil;

//------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------

public class DBSetupTask<T> extends DBTask<T>
{
	String test = "";
    String create = "";
    String tableName;
//    String[] DATA = { "John", "Jill", "Jack", "Jerry" };
//    String INSERT =  "insert into employee values(1,'" ;

    public DBSetupTask(DatabaseAccess db)
    {
    	super(db);
    	test = db.getTest();
    	tableName = db.getTableName();
    	create = "create table " + db.getSchema();
    }
    
    public DBSetupTask(String name, String schema)
    {
    	super(null);
    	tableName = name;
    	test = DBUtil.testExists(name);
    	create = "create table " + schema;
    }
    
    static boolean reset = false;
    
	@Override protected T call() throws Exception
	{
		try (Connection con = DBUtil.getConnection())
		{
			if (reset && tableName != null)
			{
				Statement st = con.createStatement();
				st.execute("DROP TABLE " + tableName);
				
				createSchema(con);
				return null;
			}

			if (!schemaExists(con))
				createSchema(con);
		}
		return null;
	}

	private boolean schemaExists(Connection con)
	{
		logger.info("Checking for Schema existence");
		try
		{
			Statement st = con.createStatement();
			st.executeQuery(test);
			logger.info("Schema exists");
		} catch (SQLException ex)
		{
			logger.info("Existing DB not found will create a new one");
			return false;
		}

		return true;
	}

	private void createSchema(Connection con) throws SQLException
	{
		logger.info("Creating schema");
		Statement st = con.createStatement();
		st.executeUpdate(create);
		logger.info("Created schema");
	}


}


