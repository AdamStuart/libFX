package task;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;

abstract public class DBTask<T> extends Task<T>
{
	protected static final Logger logger = Logger.getLogger(DBTask.class.getName());
	protected DatabaseAccess database;

	protected DBTask(DatabaseAccess db)
	{
		database = db;
		setOnFailed(t -> logger.log(Level.SEVERE, null, getException()));
	}
}
