package gui;

import gui.Action.ActionType;

public class Log {
	
	// SessionID, TimeStamp, UserID, ActionType, Priority
	
	public static void getSessionId()
	{
		
	}

	public static void getUserId()
	{
		
	}

	public static void getConfig()
	{
		//list apps
		// list machine config and software versions
	}

	public static void info(String s)
	{
		
	}

	public static void action(ActionType action)
	{
		System.err.println("Action logged: " + action);
	}

	public static void error(String s)
	{
		
	}
}
