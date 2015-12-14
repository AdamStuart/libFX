package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import task.DatabaseAccess;

public class DBUtil
{
	  //------------------------------------------------------------------------------------
	  private static final Logger logger = Logger.getLogger(DBUtil.class.getName());

	  // to install the h2 jar:
	  //http://www.h2database.com/html/installation.html
	  public static Connection getConnection() throws ClassNotFoundException, SQLException {
//	    logger.info("Getting a database connection");
	    Class.forName("org.h2.Driver");
	    return DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
	  }  
	 
	  //------------------------------------------------------------------------------------
		// ----------------------------------------------------
	  public static	String createSchema(String tableName, String ... fields)
	{
		 StringBuilder line = new StringBuilder(tableName + "\n(");
		 line.append("id VARCHAR(100),\n");
		 for (String fld : fields)
		 {
			 fld = fld.replaceAll("_STR", "VARCHAR(10000)").replaceAll("_SHORT", "VARCHAR(100)");
			 line.append(fld).append(",\n");
		 }
		 String outline = StringUtil.chopLast2(line.toString()) + " );";
		 System.err.println(outline);
		 return outline;
	}

		// ----------------------------------------------------
	  public static	String createSQL(String tableName, String ... fields)
	{
		 return "CREATE TABLE " + createSchema(tableName, fields);
	}

	public static HBox makeCrudBar(DatabaseAccess db)
	{
   		Button create = new Button("Create");
   		create.setOnAction(event -> { db.create(); } );
		Button read = new Button("Read");
		read.setOnAction(event -> { db.read(); } );
		Button update = new Button("Update");
		update.setOnAction(event -> { db.update(); } );
		Button delete = new Button("Delete");
		delete.setOnAction(event -> { db.delete(); } );
		HBox crudBar = new HBox(12, create, update, read, delete);
		return crudBar;
	}

	public static HBox makeDBControls(DatabaseAccess db)
	{
   		Button connect = new Button("Connect");
   		connect.setOnAction(event -> { db.connect(null); } );
  		Button disconnect = new Button("Disconnect");
  		disconnect.setOnAction(event -> { db.disconnect(); } );
  		Button ping = new Button("Ping");
  		ping.setOnAction(event -> { db.ping(); } );
  		Button exec = new Button("Execute");
  		exec.setOnAction(event -> {  } );
		HBox dbControls = new HBox(12, connect, ping, exec, disconnect);
 		return dbControls;
	}

	public static String testExists(String string)
	{
		return  "select count(*) from " + string;
	}

	// TODO just scans for textFields.  Checkboxes, ChoiceBoxes and lists, etc don't report their state
	
	public static void extract(Parent parent, ObservableMap<String, String> fields) //List<String> fieldCollector, List<String> valueCollector)
	{
		boolean verbose = false;

		String id = parent.getId();
		if (verbose)
			System.out.println(NodeUtil.shortClassname(parent.getClass().toString()) + ":  "
							+ (id == null ? "-" : id));
		if (parent instanceof SplitPane)
		{
			for (Node n : ((SplitPane) parent).getItems())
				if (n instanceof Parent)
					extract((Parent) n, fields);
		} else if (parent instanceof ScrollPane)
		{
			Node content = ((ScrollPane) parent).getContent();
			if (content instanceof Parent)
				extract((Parent) content, fields);
			if (verbose)
				System.out.println(NodeUtil.shortClassname(content.getClass().toString()) + ":  "
								+ (content.getId() == null ? "-" : content.getId()));
		} else
			for (Node n : parent.getChildrenUnmodifiable())
			{
				if (n instanceof Label) 
				{
					Label lab = (Label) n;
					if ("id".equals(lab.getId()))
					{
						String idVal = lab.getText();
						if (StringUtil.isEmpty(id))
							id = StringUtil.gensym();		// TODO random, should be sequential
						fields.put("id", idVal);
					}
				}
				if (n instanceof TextField) 
					{
					TextField tf = (TextField) n;
					fields.put(tf.getId(), tf.getText());
					}
				if (n instanceof CheckBox) 			// TODO
				{
				}
				if (n instanceof ChoiceBox) 
				{
				}
				if (n instanceof RadioButton) 
				{
				}
				if (n instanceof Parent)
					extract((Parent) n, fields);
			}

	}
	

	
	public static void install(Parent parent, ObservableMap<String, String> record) 
	{
		boolean verbose = false;

		String id = parent.getId();
		if (verbose)
			System.out.println(NodeUtil.shortClassname(parent.getClass().toString()) + ":  "
							+ (id == null ? "-" : id));
		if (parent instanceof SplitPane)
		{
			for (Node n : ((SplitPane) parent).getItems())
				if (n instanceof Parent)
					install((Parent) n, record);
		} else if (parent instanceof ScrollPane)
		{
			Node content = ((ScrollPane) parent).getContent();
			if (content instanceof Parent)
				install((Parent) content, record);
			if (verbose)
				System.out.println(NodeUtil.shortClassname(content.getClass().toString()) + ":  "
								+ (content.getId() == null ? "-" : content.getId()));
		} else
			for (Node n : parent.getChildrenUnmodifiable())
			{
				if (n instanceof TextField) 
					{
						TextField tf = (TextField) n;
						String s = tf.getId().toUpperCase();
						String value = record.get(s);
						tf.setText(value);
					}
				if (n instanceof CheckBox) 			// TODO
				{
				}
				if (n instanceof ChoiceBox) 
				{
				}
				if (n instanceof RadioButton) 
				{
				}
				
				if (n instanceof Parent)
					install((Parent) n, record);
			}

	}

	public static void clearForm(Node form)
	{
		
			boolean verbose = false;

			String id = form.getId();
			if (verbose)
				System.out.println(NodeUtil.shortClassname(form.getClass().toString()) + ":  "
								+ (id == null ? "-" : id));
			if (form instanceof SplitPane)
			{
				for (Node n : ((SplitPane) form).getItems())
					if (n instanceof Parent)
						clearForm((Parent)n);
			} else if (form instanceof ScrollPane)
			{
				Node content = ((ScrollPane) form).getContent();
				if (content instanceof Parent)
					clearForm(form);
				if (verbose)
					System.out.println(NodeUtil.shortClassname(content.getClass().toString()) + ":  "
									+ (content.getId() == null ? "-" : content.getId()));
			} else if (form instanceof Parent)
				for (Node n : ((Parent)form).getChildrenUnmodifiable())
				{
					if (n instanceof TextField) 
						{
							TextField tf = (TextField) n;
							tf.setText("");
						}
					if (n instanceof CheckBox) 			// TODO
					{
					}
					if (n instanceof ChoiceBox) 
					{
					}
					if (n instanceof RadioButton) 
					{
					}
					
					if (n instanceof Parent)
						clearForm(n);
				}

		}

	public static String getWhere(Map<String, String> criteria)
	{
		StringBuilder buffer = new StringBuilder (" WHERE ");
		boolean first = true;
		for (String key : criteria.keySet())
		{
			if (!first) buffer.append(" AND ");
			 buffer.append(key).append(" LIKE ").append(SINGLEQUOTE(criteria.get(key)));
			first = false;
		}
		return buffer.toString();
	}

static char SQ =  '\'';
static String SINGLEQUOTE(String s)
{
	if (s == null) return "''";
	if (s.charAt(0) ==  SQ) return s;
	return  SQ + s + SQ;
}
}
