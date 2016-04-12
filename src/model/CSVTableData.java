package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.xml.internal.messaging.saaj.util.ParseUtil;

import gui.Borders;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import util.FileUtil;
import util.StringUtil;

public class CSVTableData
{
	private String name;
	private List<StringUtil.TYPES> types;
	private List<String> columnNames;
	private List<IntegerDataRow> rows;
	private List<Range> ranges;
	private Map<String, Histogram1D> histograms;
	private Map<String, Map<String, Histogram1D>> gatedHistogramMap;
//	private List<Histogram2D> histogram2Ds;
	private List<OverlaidScatterChart<Number, Number>> scatters;
	private Map<String, Image> images;
	private Map<String, Integer> gateNames = new HashMap<String, Integer>();
	//--------------------------------------------------------------------------------
	
	
	public CSVTableData(String id)
	{
		name = id;
		types = new ArrayList<StringUtil.TYPES>();
		columnNames = FXCollections.observableArrayList();
		rows = new ArrayList<IntegerDataRow>();
		ranges = new ArrayList<Range>();
		histograms = new HashMap<String,Histogram1D>();
//		histogram2Ds = new ArrayList<Histogram2D>();
		scatters = new ArrayList<OverlaidScatterChart<Number, Number>>();
		images = new HashMap<String, Image>();
		gatedHistogramMap = new HashMap<String, Map<String, Histogram1D>>();
	}
	//--------------------------------------------------------------------------------
	static final String TAB = "\t";
	static public  CSVTableData readZKWfile(Path p)
	{
		CSVTableData tableData = new CSVTableData(p.toString());
		
		int lineCt = 0;
		try
		{
			FileInputStream fis = new FileInputStream(p.toFile());
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			
			line = br.readLine();		// first line is text labels, but not in columns
			String[] columns = line.split(TAB); 
			tableData.setColumnNames(Arrays.asList(line.split(TAB)));
			int len = columns.length;
			line = br.readLine();

			while (line != null) {
				String[] row = line.split(TAB);  
				if (row.length != len)	throw new IllegalArgumentException();		// there must be the same number of tabs in every row
				IntegerDataRow dataRow = new IntegerDataRow(row.length); 
				for (int i = 0; i< row.length; i++)
					dataRow.set(i, StringUtil.readClosestInt(row[i]));
				tableData.getData().add(dataRow);
				line = br.readLine();
				lineCt++;
			}
		 
			br.close();
		}
		catch (NumberFormatException e)		{ e.printStackTrace();	return null; 	}
		catch (IllegalArgumentException e)	{ e.printStackTrace();	return null; 	}
		catch (FileNotFoundException e)		{ e.printStackTrace();	return null; 	}
		catch (IOException e)				{ e.printStackTrace();	return null; 	}
		System.out.println( lineCt + " lines");
		
		tableData.calculateRanges();
//		tableData.generateHistograms();				just building a unit file here.  Segment.java has the full code 
//		tableData.calculateStats();
		System.out.println(tableData.getName() + " has row count: " + tableData.getCount());
		return tableData;
	}

	//--------------------------------------------------------------------------------
	public void clear()  {
		types.clear();
		columnNames.clear();
		rows.clear();
		ranges.clear();
		histograms.clear();		
		scatters.clear();		
		images.clear();		
	}
	//--------------------------------------------------------------------------------
	
	public int nRows()			{ return rows.size(); }
	public int nColumns()		{ return columnNames.size(); }

	private int getIndexByStart(String name)			
	{
		for (int i= 0; i< columnNames.size(); i++)
			if (columnNames.get(i).startsWith(name))
				return i;
		return -1;
	}
	private int getIndex(String name)			{ return columnNames.indexOf(name); }
	private int gateIndex(String name)			{ return name == null ? null : gateNames.get(name); }
	public  String getName() 					{ return name; }
	public  List<StringUtil.TYPES> getTypes() 	{ return types; }
	public  List<Range> getRanges() 			{ return ranges; }
	public  Map<String,Image> getImages() 		{ return images; }
	public  Range getRange(int i) 				{ return ranges.get(i); }
	public  Map<String,Histogram1D> getHistograms() 	{ return histograms; }
	public  Histogram1D getHistogram(int i) 
	{ 
		if (histograms.isEmpty()) generateHistograms(); 
		return histograms.get(i); 
	}
	
	public  Map<String,Histogram1D> getGatedHistograms(String popname) 	
	{ 	 return ("All".equals(popname) || "^".equals(popname)) ? histograms : gatedHistogramMap.get(popname);
	}

	public  List<String> getColumnNames() 	{ 	return columnNames; }
	public 	int getCount()					{ 	return rows.size();	}
	public 	int getWidth()					{	return (rows.size() == 0) ? 0 : rows.get(0).getWidth();	}
	public  List<IntegerDataRow> getData() 	{ 	return rows; }
	public  IntegerDataRow getDataRow(int i) { 	return rows.get(i); }

	public  void  setTypes(List<StringUtil.TYPES> t) { types = t; }
	public  void  setColumnNames(List<String> c) { columnNames = c; }
	public  void  addColumnName(String n) 		{ columnNames.add(n); }
	public  void  setData(List<IntegerDataRow> d) {  rows = d; }
	

//	public  List<OverlaidScatterChart> getScatters() 
//	{ 
//		if (scatters.isEmpty()) generateScatters(); 
//		return scatters; 
//	}
//	public void clearScatters()				{	scatters.clear();		}		// force regeneration
	
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
		{
			int min = Math.max(10,  mins[i]);
			ranges.add(new Range(min, maxs[i]));
		}
	}
	//--------------------------------------------------------------------------------
	public void generateHistograms()
	{
		if (!histograms.isEmpty())	return;
		
		int nRows = rows.size() - 2;			// skip the last row of the file, as it's all 0
		if (nRows <= 0) return ;
//		IntegerDataRow row0 = rows.get(0);
		int nCols = columnNames.size();
		for (int i=5;i<nCols; i++)
//		for (int i=0;i<nCols; i++)
		{
			Histogram1D hist = null;		// put null in the first five slots
//			if (i >= 5) 		// first five columns are position and size,		skip them
//			{
				hist = new Histogram1D(columnNames.get(i) , ranges.get(i-5));
				for (int row=0; row<nRows; row++)		// first pass to calculate tails
				{
					IntegerDataRow aRow = rows.get(row);
					Integer s = aRow.get(i).get();
					hist.count(s);			// we ignore the bottom 5 bins here!!		<<================
				}
//			}
			if (hist != null)
			{	
				histograms.put(hist.getName(), hist);
				System.out.println(hist.getName() +  " has area: " + hist.getArea() + " / " + rows.size()); 
			}
		}
	}
	//--------------------------------------------------------------------------------
	public void generateGatedHistograms(String popName)
	{
		Map<String, Histogram1D> gatedHistograms = new HashMap<String, Histogram1D>();
		int index = gateIndex(popName);
		if (index >= 0)
		{
//			IntegerDataRow row0 = rows.get(0);
			for (int i=5;i<columnNames.size(); i++)
			{
				Histogram1D hist = new Histogram1D(columnNames.get(i) , ranges.get(i));
				for (IntegerDataRow aRow : rows)		
				{
					Integer gate = aRow.get(index).get();
					if (gate == 1)
					{
						Integer val = aRow.get(i).get();
						hist.count(val);	
					}
				}
				gatedHistograms.put(hist.getName(), hist);
				System.out.println(hist.getName() +  " has area: " + hist.getArea() + " / " + rows.size()); 
			}
			gatedHistogramMap.put(popName, gatedHistograms);
		}
	}
	//--------------------------------------------------------------------------------
	public Histogram1D getGatedHistogram(GraphRequest req)
	{
		return getGatedHistogram(req.getX(), req.getPopulation());
	}
	public Histogram1D getGatedHistogram(String dimName, String popName)
	{
		Map<String, Histogram1D> gatedHistograms = gatedHistogramMap.get(popName);
		if (gatedHistograms == null) 
			gatedHistograms = histograms;

		if (gatedHistograms == null) return null;
		return gatedHistograms.get(dimName);
	}

	//--------------------------------------------------------------------------------
	public List<Point2D> getPointList(String popName, String xDim, String yDim)
	{
		List<Point2D> pointList = new ArrayList<Point2D>();
		int index = gateIndex(popName);
		if (index >= 0)
		{
			int xIdx = indexOf(xDim);
			int yIdx = indexOf(yDim);
			for (IntegerDataRow aRow : rows)		
			{
				Integer gate = aRow.get(index).get();
				if (gate == 1)
				{
					Point2D pt = new Point2D(aRow.get(xIdx).doubleValue(), aRow.get(yIdx).doubleValue());
					pointList.add(pt);
				}
			}
		}
		System.out.println( "There are " + pointList.size() + " points in " + popName); 
		return pointList;
	}

	//--------------------------------------------------------------------------------
	//move to app specific subclass 
	
	public void generateScatters(VBox container)
	{
		images.put("CD3/CD4", getImage( "CD3", "CD4"));
		images.put("CD3/CD19", getImage( "CD3", "CD19"));
		images.put("CD25/CD38", getImage( "CD25", "CD38"));
		images.put("CD39/CD38", getImage( "CD39", "CD38"));
		images.put("CD25/CD27", getImage("CD25", "CD27"));
		images.put("CD4/CD161", getImage("CD4", "CD161"));

		for (String label : images.keySet())
		{
			Image img = getImages().get(label);
			ImageView view = new ImageView(img);
			view.setFitWidth(200);
			view.setFitHeight(200);
			view.setScaleY(-1);
			container.getChildren().add(view);
			container.getChildren().add(new Label(label));
		}
}
	//--------------------------------------------------------------------------------
	public OverlaidScatterChart<Number, Number> getGatedScatterChart(GraphRequest req)
	{
		return getGatedScatterChart(req.getPopulation(), req.getX(), req.getY());
	}
	
	public OverlaidScatterChart<Number, Number> getGatedScatterChart(String popName, String xDim, String yDim)
	{
		Map<String, Histogram1D> popHistoList = gatedHistogramMap.get(popName);
		if (popHistoList == null) return null;
			
		Histogram1D xHisto =popHistoList.get(xDim);
		Histogram1D yHisto =popHistoList.get(yDim);
		if ((xHisto == null) || (yHisto == null)) return null;

		NumberAxis xAxis = new NumberAxis(xDim, Math.log(xHisto.getRange().min)-5, Math.log(xHisto.getRange().max)-5, 1);		// needs log transform
		NumberAxis yAxis = new NumberAxis(yDim, Math.log(yHisto.getRange().min)-5, Math.log(yHisto.getRange().max)-5, 1);
		xAxis.setTickLabelsVisible(false);		// use CSS
		yAxis.setTickLabelsVisible(false);
		OverlaidScatterChart<Number, Number> chart = new OverlaidScatterChart<Number, Number>(xAxis, yAxis);
		Series<Number,Number> dataset = new Series<Number,Number>();
		dataset.setNode(new Circle(2));
		chart.getData().add(dataset);
		List<Point2D> pts = getPointList(popName, xDim, yDim);
		for (Point2D p : pts)
		{
			double x = Math.log(p.getX()) - 5;
			double y = Math.log(p.getY()) - 5;
			dataset.getData().add(new XYChart.Data<Number, Number>(x,y));
		}
		chart.setTitle(popName);
		chart.setLegendVisible(false);
		return chart;
	}
	
	
// TODO move up into a controller
	private void setLayer(OverlaidScatterChart<Number, Number> scatter, String xName, String yName, int idx)
	{
//		images.clear();
//		Image img = getImage( xName, yName);
//		images.add(img);
//	
	}
	private Image getImage(String xName, String yName)
	{
		int xIdx = getIndexByStart(xName);
		int yIdx = getIndexByStart(yName);
		if (xIdx < 0 || yIdx < 0)
			return null;		// error;
		Range xRange = ranges.get(xIdx);
		Range yRange = ranges.get(yIdx);
		
		LogHistogram2D histo2D = new LogHistogram2D(100, xRange, yRange);
		for (IntegerDataRow row : rows)
		{
			int x = row.get(xIdx).get();
			int y = row.get(yIdx).get();
			if (insideGates(xIdx, x, yIdx, y))		// stub
				histo2D.count(x, y);
		}
		Image img = histo2D.asImage();
		return img;
	}
	
	//--------------------------------------------------------------------------------
	private boolean insideGates(int xIdx, int x, int yIdx, int y)
	{
		return (x > 0 && y > 0);		// TODO
	}
	int xIndex = 0;
	int yIndex = 1; 
	int nDimensions = 8;
	
	int indexOf(String s)
	{
		for (int i=0; i< columnNames.size(); i++)
			if (s.equals(columnNames.get(i)))	return i;
		return -1;
	}
	
	int findColumnName(String s)
	{
		for (int i=0; i<columnNames.size(); i++)
		{ 
			String name = columnNames.get(i);
			if (name.equals(s))return i;
		}
		return -1;
		
	}
	boolean inRange(double x, double a, double b)	{ return x >= a && x < b;	}
	
	public void makeUnitFile(Path f)
	{
		if (FileUtil.isCSV(f))
		{
			String fName = f.toString().replace(".csv", ".unit");
			File unitFile = new File(fName);
			if (unitFile.exists()) unitFile.delete();
			
			try
			{
				FileOutputStream fileOutputStream = new FileOutputStream(unitFile);
				for (IntegerDataRow row : rows)		
				{
					for (int i=0;i<nColumns();i++)
					{
						double d = transform(i, row.get(i).get());
						byte[] bytes = convertDoubleToBytes(d);
						fileOutputStream.write(bytes);
					}
				}
				fileOutputStream.flush();
				fileOutputStream.close();
			}
			catch (Exception e) {	e.printStackTrace();	}
		}
	}

	private byte[] convertDoubleToBytes(double d)
	{
		byte[] output = new byte[8];
		long lng = Double.doubleToLongBits(d);
		for(int i = 0; i < 8; i++) 
			output[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);		
		return output;
	}

	double transform(int colIndex, int value)
	{
		return Math.log(value) - 4;
	}
	//--------------------------------------------------------------------------------
	public int addPColumn(String parent, String pop)	{		return addPColumn(parent, pop, pop);	}
	
	public int addPColumn(String parent, String pop, String name)
	{
		Map<String, Histogram1D> dataset = getGatedHistograms(parent);
		if (dataset == null) return -1;
//for (String key : dataset.keySet())
//	System.out.println("Area in " + key + ": " + dataset.get(key).getArea());
		
		boolean positivePop = pop.endsWith("+");
		boolean negativePop = pop.endsWith("-");
		if (positivePop == negativePop) return -1;
		if (StringUtil.isEmpty(name))	 name = pop;
		String dim = StringUtil.chopLast(pop);
		Histogram1D histo = dataset.get(dim);
		if (histo == null)  return -1;
		List<Peak> peaks = histo.getPeaks();
		int parentIdx = ("All".equals(parent) || "^".equals(parent)) ? -1 : gateNames.get(parent);
		if (peaks.size() == 1)
		{
			if (negativePop) addPColumnPeakIndex(name, parentIdx, histo, 0);
			if (positivePop) addPColumnAbove(name, parentIdx, histo, peaks.get(0).getMax());
		}
		if (peaks.size() >= 2)
		{
			if (negativePop) addPColumnPeakIndex(name, parentIdx, histo, 0);
			if (positivePop) addPColumnPeakIndex(name, parentIdx, histo, peaks.size()-1);
		}
	
		return -1;
	}

	//--------------------------------------------------------------------------------
	public void addPColumnPeakIndex(String id, int parentIdx, Histogram1D histogram, int peakNum)
	{
		Peak peak = histogram.getPeaks().get(peakNum);
		double minVal = peak.getMin();
		double maxVal = peak.getMax();
		int index = indexOf(histogram.getName());
		int ct = 0;
		for (IntegerDataRow row : rows)
		{
			int x = histogram.valToBin(row.get(index).get());
			int parentVal = parentIdx < 0 ? 1 : row.get(parentIdx).get();
			int val = parentVal == 0 ? 0 : (inRange(x, minVal, maxVal) ? 1 : 0);
			if (val == 1) ct++;
			row.addPColumn(val);
		}
		gateNames.put(id, rows.get(0).getWidth()-1);
		generateGatedHistograms(id);
		System.out.println(ct + " events were in gate " + id);
//		Thread th = new Thread(() -> Platform.runLater(() -> { addColumnName(id);  }) );  
//		columnNames.add(id);
	}

	//--------------------------------------------------------------------------------
	public void addPColumnAbove(String id, int parentIdx, Histogram1D histogram, double floor)
	{
		double minVal = floor;
		int index = indexOf(histogram.getName());
		int ct = 0;
		for (IntegerDataRow row : rows)
		{
			int x = histogram.valToBin(row.get(index).get());
			int parentVal = parentIdx < 0 ? 1 : row.get(parentIdx).get();
			int val= (parentVal == 0) ? 0 : ((x >= minVal) ? 1 : 0);
			if (val == 1) ct++;
			row.addPColumn(val);
		}
		gateNames.put(id, rows.get(0).getWidth()-1);
		generateGatedHistograms(id);
		System.out.println(ct + " events were in gate " + id);
	}

	//--------------------------------------------------------------------------------
	// done implicitly by sending parent to addPColumn calls above
//	public void addPColumnAnd(String newCol, String ... src)
//	{
//		int[] indices = new int[src.length];
//		int ct = 0;
//		for (int i = 0; i < src.length; i++)
//		{
//			String s = src[i];
//			indices[i] = gateNames.get(s);
//		}
//
//		for (IntegerDataRow row : rows)
//		{
//			double val = 1;
//			for (int i = 0; i < src.length; i++)
//				val *= row.get(indices[i]).getValue();			// TODO make double
//			if (val == 1) 
//			{
//				ct++;
//				row.addPColumn((int) val);
//			}
//		}
//		gateNames.put(newCol, rows.get(0).getWidth()-1);
//		generateGatedHistograms(newCol);
//		System.out.println(ct + " events were in gate " + newCol);
//	}

	
	//	private void prevXParm(OverlaidScatterChart scatter)
//	{
//		xIndex--;
//		if (xIndex < 0) xIndex = nDimensions;
//		setLayer(scatter, dims[xIndex], dims[yIndex], -1);
//	}
//	private void nextXParm(OverlaidScatterChart scatter)
//	{
//		xIndex++;
//		if (xIndex >= nDimensions) xIndex = 0;
//		setLayer(scatter, dims[xIndex], dims[yIndex], 1);
//		
//	}
//	private void prevYParm(OverlaidScatterChart scatter)
//	{
//		yIndex--;
//		if (yIndex < 0) yIndex = nDimensions;
//		setLayer(scatter, dims[xIndex], dims[yIndex], -1);
//		
//	}
//	private void nextYParm(OverlaidScatterChart scatter)
//	{
//		yIndex++;
//		if (yIndex >= nDimensions) yIndex = 0;
//		setLayer(scatter, dims[xIndex], dims[yIndex], 1);
//		
//	}
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
			status += " Gutter: " + (int) gutter + " / " + (int) area;
			System.out.println(status);
		}
	}


	//--------------------------------------------------------------------------------
	public void populateCSVTable(TableView<IntegerDataRow> csvtable)
	{
		csvtable.getColumns().clear();
		TableColumn<IntegerDataRow, Integer> rowNumColumn = new TableColumn<>("#");  
        rowNumColumn.setCellValueFactory(cellData -> cellData.getValue().getRowNum().asObject());
		csvtable.getColumns().add(rowNumColumn);
		for (int i=0;i<getColumnNames().size();i++)
		{
            String name = getColumnNames().get(i);
            TableColumn<IntegerDataRow, Integer> newColumn = new TableColumn<>(name);  
            final int j = i;
            newColumn.setCellValueFactory(cellData -> cellData.getValue().get(j).asObject());
			csvtable.getColumns().add(newColumn);
		}
		csvtable.getItems().clear();
		int nCols = csvtable.getColumns().size();

		int nRows = getData().size();  
		for (int row=0; row<nRows; row++)
		{
			IntegerDataRow newRow = new IntegerDataRow(nCols);
			newRow.setRowNum(row);
			for (int i=1;i<nCols;i++)
			{
				Integer k = getDataRow(row).get(i-1).get();
				newRow.set(i-1, k);
			}
			csvtable.getItems().add(newRow);
		}	
	}

	//--------------------------------------------------------------------------------
	public List<XYChart<Number, Number>> process(List<GraphRequest> requests)
	{
		List<XYChart<Number, Number>> charts = new ArrayList<XYChart<Number, Number>>();
		for (GraphRequest req : requests)
		{
			if (req.isHistogram())
				charts.add(showGatedHistogram(req));
			if (req.isScatter())
				charts.add(getGatedScatterChart(req));
		}
		return charts;
	}

	//--------------------------------------------------------------------------------
	public void makeGatedHistogramOverlay(Histogram1D histo, 
					OverlaidLineChart peakFitChart, double offsetIncrement, String ... pops)
	{
		if (peakFitChart != null && histo != null) 
		{
			double yOffset = 0;
			String histoName = histo.getName();
			double area = histo.getArea();
			for (String popName : pops)
			{
				yOffset += offsetIncrement;
				Histogram1D gatedHistogram = getGatedHistogram(histoName, popName);
				if (gatedHistogram != null)
					peakFitChart.getData().add( gatedHistogram.getDataSeries(popName, yOffset, area));	
			}
		}
	}


	//--------------------------------------------------------------------------------
	public OverlaidLineChart showGatedHistogram(GraphRequest req)
	{
		return showGatedHistogram(req.getPopulation(), req.firstChild(), req.getX());
	}
	
	public OverlaidLineChart showGatedHistogram(String parent, String child, String dim)
	{
		if (StringUtil.isEmpty(parent))	parent = "^";
		Histogram1D parentHisto = getGatedHistogram(dim, parent);
		Histogram1D childHisto = getGatedHistogram(dim, child );
		if (parentHisto == null || childHisto == null)		return null;
		System.out.println(parentHisto.getName() + " area: " + parentHisto.getArea());
		System.out.println(childHisto.getName() + " area: " + childHisto.getArea());
		if (parentHisto.getArea() == 0) System.out.println("no parent events");
		else System.out.println("Freq of Parent: " + (int)(100 * childHisto.getArea() / (double) parentHisto.getArea()) + "%");
		
		NumberAxis  xAxis = new NumberAxis();	
		xAxis.setLabel(dim);
		NumberAxis  yAxis = new NumberAxis();
		OverlaidLineChart  chart = new OverlaidLineChart(xAxis, yAxis);
		chart.setTitle(child + " Definition");
		chart.setCreateSymbols(false);
		chart.getData().add( parentHisto.getDataSeries(parent, 0, parentHisto.getArea()));	
		chart.getData().add( childHisto.getDataSeries(child, 0, parentHisto.getArea()));		// scale child to parent area -- not working!!
		chart.setLegendVisible(false);
		chart.getXAxis().setTickLabelsVisible(false);		// use CSS
		chart.getYAxis().setTickLabelsVisible(false);
//		chart.setPrefHeight(100);
		VBox.setVgrow(chart, Priority.ALWAYS);
		chart.setId(parent + "/" + child);
		return chart;
	}
	//--------------------------------------------------------------------------------
	public void generateRawHistogramCharts(VBox graphVBox)
	{
		Map<String, Histogram1D> histos = getHistograms(); 
		if (histos == null) return;
		for (String key : histos.keySet())
		{
			Histogram1D histo = histos.get(key);
			if (histo == null) continue;		// first 5 are null
			Range r = histo.getRange();
			if (r.width() < 50) continue;
			LineChart<Number, Number> chart = histo.makeChart();
			graphVBox.getChildren().add(chart);
//			chart.getXAxis().setTickLabelsVisible(false);		// use CSS
			chart.getYAxis().setTickLabelsVisible(false);
//			chart.getXAxis().setVisible(false);
			VBox.setVgrow(chart, Priority.ALWAYS);
		}
	}
	//--------------------------------------------------------------------------------
	// DEAD CODE
	public OverlaidScatterChart<Number, Number> generateScatter(GraphRequest req)
	{
		return generateScatter(req.getX(), req.getY());
	}
	public OverlaidScatterChart<Number, Number> generateScatter(String xParm, String yParm)
	{
		final NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel(xParm);
		
		final NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel(yParm);
		
		OverlaidScatterChart<Number, Number> scatter = new OverlaidScatterChart<Number, Number>(xAxis, yAxis);
//		
//		xAxis.setOnMouseClicked(ev -> {
//			if (ev.isShiftDown()) prevXParm(scatter);
//			else				nextXParm(scatter);
//		});
//		yAxis.setOnMouseClicked(ev -> {
//			if (ev.isShiftDown()) prevYParm(scatter);
//			else				nextYParm(scatter);
//		});
//
		scatter.setTitle("Ten Bin Gutter");
		Node chartPlotArea = scatter.lookup(".chart-plot-background");
		if (chartPlotArea != null)
		{
			Region rgn = (Region) chartPlotArea;
			rgn.setBorder(Borders.blueBorder1);
		}
		setLayer(scatter, xParm, yParm, 0);
		return scatter;

	}

}
