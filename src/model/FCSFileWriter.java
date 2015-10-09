package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;

public class FCSFileWriter extends FileWriter
{
	File f;
	public FCSFileWriter(File file) throws IOException
	{
		super(file);
		f = file;
	}

	public void writeAll(HashMap<String, String> textSection, TableData data)
	{
		writeHeader();
		writeText(textSection);
		writeBody(data);
	}
	
	private void writeHeader()
	{
		System.out.println("writeHeader");
		int textStart = 50;
		int textEnd = 2500;
		int bodyStart = textEnd + 1;
		int bodyEnd = 600000;
		String header = String.format("FCS3.0  %8d%8d%8d%8d", textStart, textEnd, bodyStart, bodyEnd);
		System.out.println(header);
	}
	
	private void writeText(HashMap<String, String> textSection)
	{
		System.out.println("writeText");
		String text = streamAttributes(textSection, '\n');
		System.out.println(text);
	}
	private void writeBody(TableData data)
	{
		System.out.println("writeBody");
	}
	public void toFile(TableData data)
	{
		System.out.println("toFile");
	}
	//----------------------------------------------------------
	
	public String streamAttributes( HashMap<String, String> map, char delim)
	{
		StringBuffer buffer = new StringBuffer();
		Set<String> keys = map.keySet();
		for (String key : keys)
			buffer.append(delim).append(key).append(delim).append(map.get(key));
		return buffer.toString();
	}
}
