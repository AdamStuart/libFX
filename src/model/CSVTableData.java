package model;

import java.util.ArrayList;
import java.util.List;

import util.StringUtil;

public class CSVTableData
{
	private String name;
	private List<StringUtil.TYPES> types;
	private List<String> columnNames;
	private List<IntegerDataRow> rows;
	private List<Range> ranges;
	private List<Histogram1D> histograms;
	
	public CSVTableData(String id)
	{
		name = id;
		types = new ArrayList<StringUtil.TYPES>();
		columnNames = new ArrayList<String>();
		rows = new ArrayList<IntegerDataRow>();
		ranges = new ArrayList<Range>();
		histograms = new ArrayList<Histogram1D>();
	}
	
	public CSVTableData(CSVTableData orig, String newid)
	{
		name = newid;
		types = new ArrayList<StringUtil.TYPES>(orig.getTypes());
		columnNames = new ArrayList<String>(orig.getColumnNames());
		rows = new ArrayList<IntegerDataRow>(orig.getData());
		ranges = new ArrayList<Range>(orig.getRanges());
		histograms = new ArrayList<Histogram1D>(orig.getHistograms());
	}
	
	public void clear()  {
		types.clear();
		columnNames.clear();
		rows.clear();
		ranges.clear();
		histograms.clear();		
	}
	
	public  String getName() 				{ return name; }
	public  List<StringUtil.TYPES> getTypes() { return types; }
	public  List<Range> getRanges() 		{ return ranges; }
	public  Range getRange(int i) 			{ return ranges.get(i); }
	public  List<Histogram1D> getHistograms() { return histograms; }
	public  Histogram1D getHistogram(int i) { return histograms.get(i); }
	
	public  List<String> getColumnNames() 	{ return columnNames; }
	public 	int getCount()					{ return rows.size();	}
	public 	int getWidth()					{	return (rows.size() == 0) ? 0 : rows.get(0).getWidth();	}
	public  List<IntegerDataRow> getData() 	{ return rows; }
	public  IntegerDataRow getDataRow(int i) { return rows.get(i); }

	public  void  setTypes(List<StringUtil.TYPES> t) { types = t; }
	public  void  setColumnNames(List<String> c) { columnNames =c; }
	public  void  setData(List<IntegerDataRow> d) {  rows = d; }

	
	//--------------------------------------------------------------------------------
	public void calculateRanges()
	{
		if (!ranges.isEmpty())	return;
		int nRows = rows.size() - 2;			// skip the last row, as it's all 0
		if (nRows <= 0) return ;
//		IntegerDataRow row0 = rows.get(0);
		int nCols = getWidth();
	
		int[] mins = new int[nCols];
		int[] maxs = new int[nCols];
		for (int i=0;i<nCols;i++)
		{
			mins[i] = Integer.MAX_VALUE;
			maxs[i] = Integer.MIN_VALUE;
		}
		for (int row=0; row < nRows; row++)		// scan for ranges of all columns
		{
			IntegerDataRow aRow = rows.get(row);
			for (int i=0;i<nCols;i++)
			{
				Integer s = aRow.get(i).get();
				if (s <= 0) continue;	// ONLY POSITIVE NUMBERS ALLOWED
//				{
//					System.out.println("STOP");
//					break;
//				}
				mins[i] = Math.min(mins[i],  s);
				maxs[i] = Math.max(maxs[i],  s);
			}
		}
		
		for (int i=0;i<nCols;i++)
			ranges.add(new Range(mins[i], maxs[i]));
	}
	//--------------------------------------------------------------------------------
	public void generateHistograms()
	{
		if (!histograms.isEmpty())	return;
		
		int nRows = rows.size() -2;			// skip the last row of the file, as it's all 0
		if (nRows <= 0) return ;
		IntegerDataRow row0 = rows.get(0);
		int nCols = row0.getWidth();
		for (int i=0;i<nCols; i++)
		{
			Histogram1D hist = null;		// put null in the first five slots
			if (i >= 5) 		// first five columns are position and size,		skip them
			{
				hist = new Histogram1D(columnNames.get(i) , ranges.get(i));
				for (int row=0; row<nRows; row++)		// first pass to calculate tails
				{
					IntegerDataRow aRow = rows.get(row);
					Integer s = aRow.get(i).get();
					hist.count(s);			// we ignore the bottom 5 bins here!!		<<================
				}
			}
			histograms.add(hist);
		}
	}
	//--------------------------------------------------------------------------------
	public void calculateStats()
	{
		int nCols = getWidth();
		for (int i=0;i<nCols;i++)
		{
			String status = columnNames.get(i) + " has range: " + ranges.get(i);
			Histogram1D h =  histograms.get(i);
			if (h == null) continue;
			double area = h.getArea(); 
			double gutter = h.getGutterCount(); 
			System.out.println(status);
			status += " Gutter: " + (int) gutter + " / " + (int) area;
		}
	}
}
