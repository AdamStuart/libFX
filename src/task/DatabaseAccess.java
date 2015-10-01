package task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.util.Pair;
import util.DBUtil;
import util.StringUtil;
import util.TableUtil;

// DatabaseAccess has the connection, buffer, schema to maintain a database session.

public class DatabaseAccess
{
	
	static  private ExecutorService databaseExecutor;		 // executes database operations concurrent to JavaFX operations.
	private Future<?>       databaseSetupFuture;	 // the future's data will be available once the database setup has been complete.
 	Connection theConnection = null;
 	final TableView<ObservableList<String>> tableView;

	//----------------------------------------------------------------------
	public static ExecutorService init()
	{
		if (databaseExecutor == null)
			databaseExecutor = Executors.newFixedThreadPool(  1,   new DatabaseThreadFactory() );  
		return databaseExecutor;
	}
	//----------------------------------------------------------------------
	public Connection getConnection() 
 	{ 
 		try
 		{
 			if (theConnection == null || theConnection.isClosed())
 		 		theConnection = DBUtil.getConnection();
 	 		return theConnection;	
		}
 		catch (Exception e) { return null; 	}
 	}
	//----------------------------------------------------------------------
	public DatabaseAccess(IDBTable inTable, TableView inView)
	{
		init();
		theTable = inTable;
		tableName = inTable.getTableName();
		schemaString = inTable.getSchema();
		tableView = inView;

		DBSetupTask<?> setup = new DBSetupTask<>(tableName, schemaString);
		databaseSetupFuture = databaseExecutor.submit(setup);
	}
	//----------------------------------------------------------------------

	public void connect(ResultSet map)
	{
		System.err.println("connect");
	}
	HashMap<String, String> credentials;
	HashMap<String, String> getCredentials() { return credentials; 	}
	
	//----------------------------------------------------------------------
	public void authenticate(Pair<String, String> keyval)
	{
		if (credentials == null && keyval != null)
		{
			credentials = new HashMap<String, String>();
			credentials.put("user", keyval.getKey());
			credentials.put("pass", keyval.getValue());
		}
	}
	//----------------------------------------------------------------------
	public void disconnect()
	{
		System.err.println("disconnect");
		try
		{
			if (theConnection != null && !theConnection.isClosed())
				theConnection.close();
		} catch (SQLException e)		{	}
	
	}
	//----------------------------------------------------------------------

	public void update()
	{
		System.err.println("update");
		activeRecord = FXCollections.observableHashMap();
		theTable.extract(activeRecord);  
		DBInsertTask<?> task = new DBInsertTask<>(this, activeRecord);
		databaseSetupFuture = databaseExecutor.submit(task);
	}
	private ObservableList<ObservableList<String>> data;
	private ObservableMap<String, String> activeRecord;
	//----------------------------------------------------------------------
	public void fillForm()
	{
		if (tableView.getSelectionModel().getSelectedIndices().size() == 1)
		{
			activeRecord = buildMap(tableView.getSelectionModel().getSelectedItem());
			theTable.install(activeRecord);  
		}; 		
	}
	//----------------------------------------------------------------------
	public ObservableList<String> getFieldList()
	{
		ObservableList<String>  fields = FXCollections.observableArrayList();
		for (TableColumn col : tableView.getColumns())
			fields.add(col.getId());
		return fields;
	}
	
	//----------------------------------------------------------------------	
	// create a map from UPPER of the fields to values
	// Assumes: order of the field list matches incoming vals array
	
	private ObservableMap<String, String> buildMap(ObservableList<String> vals)
	{
		ObservableList<String> fields = getFieldList();
		
		ObservableMap<String, String> map = FXCollections.observableHashMap();
		for (int i = 0; i< fields.size(); i++)
			map.put(fields.get(i).toUpperCase(), vals.get(i));
		return map;
	}
	//----------------------------------------------------------------------

	public void doSelect()
					
	{
		WrapperClass logString = new WrapperClass("");
		data = FXCollections.observableArrayList();
		ObservableList<String> colNames = FXCollections.observableArrayList();
		DBSelectTask<?> task = new DBSelectTask(this, data, colNames, logString);
		task.setOnSucceeded(t -> 
		{ 
			if (tableView == null) return;
			TableUtil.clearRowsAndColumns(tableView);
			System.err.println("task completed " + tableView.getColumns().size() + ", " + tableView.getItems().size());

			
			for (ObservableList<String> row : data)
				tableView.getItems().add(row);
			
          for(int i=0 ; i<colNames.size(); i++)
          {
              //We are using non property style for making dynamic table
              final int j = i;
              String id =  StringUtil.decapitalize( colNames.get(i));     
              TableColumn col = new TableColumn(id);
              col.setId(id);
              col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                  public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                      String val;
                      if (param == null || param.getValue() == null ||  param.getValue().get(j) == null) val = "";
                      else val = param.getValue().get(j).toString();
                     return new SimpleStringProperty(val);                        
                  }                    
              });
           
            
              tableView.getColumns().addAll(col); 
              System.out.println("Column ["+i+"] = " + colNames.get(i));
              System.out.println(logString.get());
          }
		});  

		databaseSetupFuture = databaseExecutor.submit(task);
	}  
	//----------------------------------------------------------------------
	public void deleteActiveRecord()
	{
		Map<String, String> crit = FXCollections.observableHashMap();
		crit.put("id", activeRecord.get("id"));
		DBDeleteTask<?> task = new DBDeleteTask<>(this, crit);
		databaseSetupFuture = databaseExecutor.submit(task);
	}
	//----------------------------------------------------------------------

	public void ping()	{		System.err.println("ping");		}
	public void create()	{		System.err.println("create");	}
	public void read()		{				System.err.println("read");		}
	public void delete()	{		System.err.println("delete");		}
	//----------------------------------------------------------------------
	private IDBTable theTable;
	private String tableName;
	private String schemaString;
	
	public String getTest()			{ return "SELECT COUNT(*) FROM " + tableName;	}
	public String getSchema()		{ return schemaString;	}
	public String getTableName() 	{ return tableName; }
	public IDBTable getTable() 		{ return theTable; }
	//----------------------------------------------------------------------
	static class DatabaseThreadFactory implements ThreadFactory {
	    static final AtomicInteger poolNumber = new AtomicInteger(1);
	    
	    @Override public Thread newThread(Runnable runnable) {
	      Thread thread = new Thread(runnable, "Database-Connection-" + poolNumber.getAndIncrement() + "-thread");
	      thread.setDaemon(true);
	 
	      return thread;
	    }
	  }

}
