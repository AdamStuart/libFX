package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public 
class DBConnect {

  private static  Connection conn;
  
  private static  String url = "jdbc:derby://localhost:1527/sample";
  private static   String user = "app";
  private static   String pass = "app";
    
  public static   Connection connect() throws SQLException
  {
    try
    {
      Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
      System.out.println("derby jdbc driver located and loaded");
    }catch(ClassNotFoundException | InstantiationException | IllegalAccessException cnfe) {
      System.err.println("Error: "+cnfe.getMessage());
    }

    conn = DriverManager.getConnection(url,user,pass);
    return conn;
  }

  public static   Connection getConnection() throws SQLException
  {
    if(conn !=null && !conn.isClosed()) 
      return conn;
    
    connect();
    return conn;

  }
}