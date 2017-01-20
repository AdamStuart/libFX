package model;



public enum TableType {
	TXT(0, 0, "\t"),
	CDT(2, 3, "\t"),
	CSV(1, 0, ",");
	
	String EOL = "\n";
	final int nHeaderRows;
	final int nColumns;
	final String delimiter;
	private TableType(int nRowsHeader, int expectedNColumns, String delim)
	{
		delimiter = delim;
		nHeaderRows = nRowsHeader;
		nColumns = expectedNColumns;
	}
	
	public String getDelimiter() 	{ return delimiter;	}
	public int getNHeaderRows() 	{ return nHeaderRows;	}
	public int getNColumns() 		{ return nColumns;	}
}