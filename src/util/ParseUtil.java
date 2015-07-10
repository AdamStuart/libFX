package util;

import java.util.HashMap;

public class ParseUtil
{
	
	public static HashMap<String, String> makeAttributeMap(String s)
	{
		String trimmed = s.trim();
		HashMap<String, String> map = new HashMap<String, String>();
		String insideBrackets = trimmed.substring(1,s.length()-1);
		String[] attributes =  insideBrackets.split(",");
		for (String attribute : attributes)
		{
			String[] flds = attribute.split("=");
			map.put(flds[0].trim(), flds[1].trim());
		}
		return map;
	}
	
}
