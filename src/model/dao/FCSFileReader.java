package model.dao;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.input.Dragboard;
import model.stat.Histogram1D;
import model.stat.Range;
import util.StringUtil;

public class FCSFileReader 
{
	private byte[] content;		// we're going to read the whole file into a byte[]
	private HashMap<String,String> textSection;
	private int textStart, textEnd, bodyStart, bodyEnd;
	private float[] xData, yData;
	String name;
	long date;
	int id;
	class FCSTableColumn extends TableColumn
	{
		FCSTableColumn(String name, int nRows)	
		{ 
			super(name);
			data = new float[nRows];
			for (int i=0; i< nRows; i++) data[i] = 0;
		}
		float[] data;
		public float[] getData()	{ return data;	}
		HashMap<String, String> map = new HashMap<String, String>();
		public String get(String attr)		{ return map.get(attr); }
		public void put(String attr, String val)		{ map.put(attr,val); }
	}
	
	ObservableList<FCSTableColumn> columns = FXCollections.observableArrayList();
	public int getId()			{		return id;	}
	public String getName()		{		return name;	}
	public long getDate()		{		return date;	}
	public float[] getXData()	{		return xData;	}
	public float[] getYData()	{		return yData;	}
	public String getValue(String attr) { return textSection.get(attr);		}

	static public boolean hasFCSFiles(Dragboard db)	{		return db.getFiles().stream().filter(f -> isFCS(f)).count() > 0;	}
	static public boolean isFCS(File f)				{		return f.getName().toUpperCase().trim().endsWith(".FCS");	}

	//-----------------------------------------------------------------

	public FCSFileReader(File file) throws FileNotFoundException
	{
		try
		{
			Path path = Paths.get(file.getAbsolutePath());
			content = Files.readAllBytes(path);
			readHeader();
			readText(); 
			readBody();
		} 
		catch (IOException e)		{	e.printStackTrace();	}
	}
	//-----------------------------------------------------------------	
	private void readHeader()
	{
		String header = new String(content,0,50);
		if ("FCS3.".equals(header.substring(0,5)))
		try {
			String vals = header.substring(10);
			String tmp = vals.substring(0,8).trim();	textStart = Integer.parseInt(tmp);
			tmp = vals.substring(8,16).trim();			textEnd = Integer.parseInt(tmp);
			tmp = vals.substring(16, 24).trim();		bodyStart = Integer.parseInt(tmp);
			tmp = vals.substring(24, 32).trim();		bodyEnd = Integer.parseInt(tmp);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	//-----------------------------------------------------------------
	int nEvents;
	int parms;
	int nBytes;
	int nRows;
	int nValues;
	int bytesPerValue;
	boolean readFloats, readInts;
	//-----------------------------------------------------------------
	private void readText()
	{
		textSection = parseAttributes(new String(content,textStart,textEnd));
		nBytes = bodyEnd - bodyStart +1 ;		
		try
		{
			nEvents = Integer.parseInt(textSection.get("$TOT"));
			parms = Integer.parseInt(textSection.get("$PAR"));
			String type = textSection.get("$DATATYPE");
			readFloats = "F".equals(type);
			assert(readFloats);

		} catch (Exception e)
		{
			assert(e == null);
		}
		bytesPerValue = 4;			// Either Float.SIZE or Integer.SIZE
		nValues = nBytes / bytesPerValue;
		nRows = nValues / parms;
		System.out.println("" +  nEvents * parms * bytesPerValue);
		if (nBytes != nEvents * parms * bytesPerValue)
			System.out.println("size calculation failed: " + nBytes + " != " + nEvents + " * 4 * " + parms + " (" + nEvents * 4 * parms + ")") ;
//		readInts = "I".equals(type);
		buildColumnList();
		
	}
	//-----------------------------------------------------------------	
	
	private void readBody()
	{
		ByteBuffer byteBuffer = ByteBuffer.wrap(content, 0, content.length);		// bodyStart still has to be used as an offset, given the way we access the bytebuffer
		
//		int nBytes = content.length;			

		for (int i = 0; i < nValues; i++) 
		{
			int idx = bodyStart + i * bytesPerValue;
			float nextF = 200.0f * (float) Math.random();
//			float nextF = byteBuffer.getFloat(idx);
			System.out.println("" + nextF);
			FCSTableColumn column = columns.get(i % parms);
			column.getData()[i / parms] = nextF;							// we only care about the first two parameters, the rest are ignored
		}
	}
	//-----------------------------------------------------------------

	public static byte [] floatToByteArray (float value)	{  	     return ByteBuffer.allocate(4).putFloat(value).array();	}

	public static String bytesToHex(byte[] in) {
	    final StringBuilder builder = new StringBuilder();
	    for(byte b : in) 
	        builder.append(String.format("%02x", b));
	    return builder.toString();
	}	
	
	//-----------------------------------------------------------------
	public HashMap<String, String> parseAttributes(String s)
	{
		String delim = s.trim().substring(0, 1);
		StringTokenizer tokenizer = new StringTokenizer(s, delim);
		HashMap<String, String> map = new HashMap<String, String>();
		while (tokenizer.hasMoreTokens())
			map.put(tokenizer.nextToken(), tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "");
		return map;
	}
	//-----------------------------------------------------------------
	public SortedMap<Number, Number> getDataTreeMap()
	{
		SortedMap<Number, Number> map = new TreeMap<Number, Number>();
		for (int i=0; i<nEvents; i++)
			map.put(xData[i],  yData[i]);
			
		return map;
	}
	//-----------------------------------------------------------------
	public Histogram1D getHistogram1D(int col, int size)
	{
		float min = Float.MAX_VALUE, max = 0;
		float[] data = columns.get(col).getData();   //col == 0 ? xData : yData;
		for (float f : data)
		{
			min = Math.min(min, f);
			max = Math.max(max, f);
		}
		if (min <=0) min = 0.1f;
//		Histogram1D histo = new Histogram1D(size, new Range(Math.log(min),Math.log(max-1)));
		Histogram1D histo = new Histogram1D("Col1", size, new Range(min,max), false);
		for (int i=0; i<nEvents; i++) 
			histo.count(data[i]);

		return histo;
	}
	//-----------------------------------------------------------------
	public double[] getStats11()
	{
		return new double[] { medianX, medianY, cvX, cvY, varX, varY, metricX, metricY, var, targetX, targetY };		
	}
	//-----------------------------------------------------------------
	
	public void setTarget(double x, double y)		{ targetX = x;	 targetY = y;	}
	double targetX, targetY;
	double medianX, medianY;
	double cvX, cvY;
	double varX, varY;
	double metricX, metricY;
	double internalXVar, externalXVar = 0;
	double internalYVar, externalYVar = 0;
	double var = 0;
	
	//-----------------------------------------------------------------
	// this is a slingshot specific calculation to measure how well beads
	// hit target expectations
	
	public void calculate()
	{	
		double sumVar = 0;
		 internalXVar = externalXVar =  internalYVar = externalYVar = 0;
		 System.out.println("target: " + targetX + ", " + targetY);
		
		float[] xcopy = xData.clone();  		// sort the arrays to get the medians
		float[] ycopy = yData.clone();  
		Arrays.sort(xcopy);
		Arrays.sort(ycopy);
		int idx = nEvents / 2;
		medianX = nEvents % 2 == 0 ?( (xcopy[idx] + xcopy[idx+1]) / 2.f ): xcopy[nEvents / 2];
		medianY = nEvents % 2 == 0 ?( (ycopy[idx] + ycopy[idx+1]) / 2.f ): ycopy[nEvents / 2];
		
		int startIdx = (int) (nEvents * .1666);			// ignore top and bottom sixth
		int endIdx = (int) (nEvents * .8333);
		
		for (int i=startIdx; i<endIdx; i++)
		{
			float x = xcopy[i];
			double dx2 = ((medianX - x) * (medianX - x));
			internalXVar += dx2;
			double dxt2 =((targetX - x) * (targetX - x));
			externalXVar += dxt2;
			
			float y = ycopy[i];
			double dy2 = ((medianY - y) * (medianY - y));
			internalYVar += dy2;
			double dt2 = ((targetY - y) * (targetY - y));
			externalYVar += dt2;
			
			sumVar += Math.sqrt(dxt2 + dt2);
		}
		
		double count = nEvents-1;
		
		varX = (float) (Math.sqrt(internalXVar)  / count);
		cvX = 100.* varX / medianX;
		metricX = (float) (100.* Math.sqrt(externalXVar)  / (count * medianX));
		
		varY = (float) (Math.sqrt(internalYVar)  / count);
		cvY = 100.* varY / medianY;
		metricY = (float) (100.* Math.sqrt(externalYVar)  / (count * medianY));
		
		double delta = ((targetX - medianX) * (targetX - medianX)) + ((targetY - medianY) * (targetY - medianY));
		double root = (float) Math.sqrt(delta);
		var = sumVar / (count * root);
	}
	//-----------------------------------------------------------------
	// this is the beginning of a project to convert FCS files into a normalized
	// linear transformation between 0 and 1
	
	static public File normalizeFCS(File in)
	{
		try
		{
			String path = in.getAbsolutePath();
			path = StringUtil.chopExtension(path) + "#.FCS";
			File out = new File(path);
			FCSFileReader reader = new FCSFileReader(in);
//			reader.buildColumnList();
			String spill = reader.getValue("$SPILLOVER");
			if (spill != null)
			{
				System.out.println("Compensation adds Columns");
			}
			reader.addNormalizedColumns();
			reader.autogate();
			FCSFileWriter writer = new FCSFileWriter(out);
			writer.toFile(reader.normalForm());
			return out;
		}
		catch (Exception e)		{	e.printStackTrace();	}
	
		return null;
	}
	//-----------------------------------------------------------------
	private void buildColumnList()
	{
		System.out.println("buildColumnList");

		if (parms <= 0) return;
		for (int i=0; i< parms; i++)
		{
			String name = textSection.get("$P" + (i+1) + "N");
			String stain = textSection.get("$P" + (i+1) + "S");
			if (StringUtil.hasText(stain) && !stain.equals(name))
				name +=  ": " + stain;
			FCSTableColumn col = new FCSTableColumn(name, nRows);
			columns.add(col);
			System.out.println("added " + name);
		}
	}

	
	private void addNormalizedColumns()
	{
		System.out.println("addNormalizedColumns");
	}
	private void autogate()
	{
		System.out.println("gate out dead, doublets, boundary, startup");
		
	}
	private TableData normalForm()
	{
		System.out.println("normalForm");
		return new TableData();
	}

}
