package model;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Histogram1D
{
	private static final int DEFAULT_HISTO_LEN = 100;
	private int size;
	private int[] counts;
	private Range range;
	boolean isLog = true;
	private String name;

	public String toString() { return name + "  " + range.toString(); }
	public Range getRange()	{ return range;	}
	public int getSize()	{ return size;	}
	public String getName() { return name; 	}
	public int[] getCounts() { return counts; 	}
	// ----------------------------------------------------------------------------------------------------
	public Histogram1D(int len, Range inX)
	{
		this("", len, inX);
	}
	
	public Histogram1D(String inName,Range inX)
	{
		this(inName, DEFAULT_HISTO_LEN, inX, true);
	}
	
	public Histogram1D(String inName, int len, Range inX)
	{
		name = inName;
		size = len;
		counts = new int[size];
		range = inX;
		if (range.width() == 0)		{ 	range.min = 0;	range.max = 1; }
	}

	public Histogram1D(String inName,int len, Range inX, boolean log)
	{
		this(inName, len, inX);
		isLog = log;
	}

	public Histogram1D(Histogram1D orig)
	{
		this(orig.getName(), orig.getSize(), orig.getRange(), orig.isLog);
		for (int i=0; i< size; i++)
			counts[i] = orig.counts[i];
	}

	public double getPercentile(int perc)
	{
		double val = 0;
		int area = getArea();
		int evCount = area * perc / 100;
		int i;
		for (i=0; val<evCount; i++)
			val += counts[i];
		double out = range.min + (i * range.width() / size);
		return out;
	}
	
	public int getArea()
	{
		int area = 0;
		for (int i=0; i<size; i++)
			area += counts[i];
		return area;
	}
	public int getGutterCount()
	{
		int area = 0;
		for (int i=0; i<GUTTER_WIDTH; i++)
			area += counts[i];
		return area;
	}
//	int counter = 0;
	// ----------------------------------------------------------------------------------------------------
	int GUTTER_WIDTH = 5;
	
	public void count(double x)
	{
		int bin = -1;
		if (x < range.min )
			return;	// System.out.println("out of range " + x);
	
		bin = valToBin(x);
		if (bin < GUTTER_WIDTH) 		return;			//	THROWING AWAY BOTTOM BINS  HERE
		if (bin >= size)  		bin = size-1;
		counts[bin]++;
	}

	
	// ----------------------------------------------------------------------------------------------------
	public double binToVal(int bin)
	{
		if (isLog)
			return range.min + bin  * range.width()/ size;   // ?????		NOT LOG  ENOUGH

		return range.min + ((bin  * range.width()) / size);
	}
	public int valToBin(double d)
	{
		if (isLog)
			return (int) Math.round(((Math.log(d) - Math.log(range.min)) / Math.log(range.width())) * size);
	
		return (int) Math.round((d - range.min) * size / range.width() );
	}
	// ----------------------------------------------------------------------------------------------------
	public void add(Histogram1D other)
	{
		boolean log = other.isLog;
		if (log != isLog)	
			System.out.println("Transform mismatch error");
		
		for (int i=0; i<other.getSize(); i++)
		{
			double ct = (double) other.getCounts()[i];
			double val = other.binToVal(i);
			int bin = valToBin(val);
			if (bin >= 0 && bin < size)
				counts[bin] += ct;
		}
	}

	// ----------------------------------------------------------------------------------------------------
	boolean grayscale = true;

	double mode = 0;

	void setMode(double d)	{		mode = d;	} // this will be the top of the Y axis

	double getMode()	{
		int max = 0;
		for (int row = 0; row < size; row++)
			max = Math.max(max, counts[row]);
		return max;
	}
	// ----------------------------------------------------------------------------------------------------
	double getModePosition()	{
		int max = 0;
		int position = 0;
		for (int row = 0; row < size; row++)
		{
			if (counts[row] > max) position = row;
			max = Math.max(max, counts[row]);
		}
		return position;
	}
	// ----------------------------------------------------------------------------------------------------
	Color colorLookup(int val)
	{
		if (mode != 0)
			if (grayscale)
			{
				double v = val / mode;
				return new Color(v, v, v, 1);
			}
		return Color.RED;
	}
	// ----------------------------------------------------------------------------------------------------
	public XYChart.Series rawDataSeries()
	{
		System.out.println("Mode: " + getMode());
		double scale = range.width() / size;
		XYChart.Series series = new XYChart.Series();
		for (int i = 0; i < size; i++)
			series.getData().add(new XYChart.Data(i * scale, counts[i]));
		return series;
	}

	// ----------------------------------------------------------------------------------------------------
	public XYChart.Series getDataSeries()	{ return getDataSeries(0.);	}
	
		public XYChart.Series getDataSeries(double yOffset)
		{
		XYChart.Series series = new XYChart.Series();
		try
		{
			double[] smoothed = smooth();
		
				double scale = range.width() / (size+1);
				double area = getArea();
				for (int i = 0; i < size; i++)
				{
					double x = range.min + (i * scale);
					x = (x > 0) ? (Math.log(x) - 5) : 0;
//					if (x < 0) x = 0;
					double y = smoothed[i] / area + yOffset;
					series.getData().add(new XYChart.Data(x,y));
				}
		}
		catch (Exception e)
		{
			System.out.println("EXCEPTION CAUGHT");
		}
		System.out.println(getName() + " done");
		return series;
	}

	// ----------------------------------------------------------------------------------------------------
	public double[] smooth()
	{
		int bin, i;
		int resolution =  size;
		int numBins = (int) resolution + 1; // was resolution;
		int radius = (int) getRadius(resolution);
		double binCt;
		int bins = numBins + 2 * radius;
		double[] destvector = new double[bins + 2];
		for (bin = radius + 0; bin < radius + numBins - 1; bin++)
		{
			binCt = counts[bin - radius];
			if (binCt == 0.0)
				continue;
			double[] smoothVector;
			int elements;
			{
				elements = smoothingVectorSize(binCt, resolution);
				smoothVector = smoothingVector(resolution, binCt, 2.4, elements, bin); 
			}
			destvector[bin] += smoothVector[0] * binCt; // 0 point
			for (i = 1; i <= elements; i++) // points to either side
			{
				double v = smoothVector[i] * binCt;
				if (bin + i < destvector.length)
					destvector[bin + i] += v;
				if (bin - i >= 0)
					destvector[bin - i] += v;
			}
		}

		// reflect margins
		for (i = 1; i <= radius; i++)
		{
			int leftEdge = radius;
			int rightEdge = radius + numBins;
			destvector[leftEdge + i - 1] += destvector[leftEdge - i];
			destvector[rightEdge - i + 1] += destvector[rightEdge + i];
		}
		// copy the destMatrix back onto srcMatrix
		double[] destBins = new double[numBins];
		for (bin = 0; bin < numBins; bin++)
			destBins[bin] = destvector[bin + radius];
		return destBins;
	}

	// ----------------------------------------------------------------------------------------------------
	private int smoothingVectorSize(double binCt, int resolution)
	{
		double radius = getRadius(resolution);
		double sqrtZ = Math.sqrt(binCt);
		double r = radius / (Math.sqrt(sqrtZ));
		int vectorElements = (int) r;
		if (r - vectorElements > 0.0)
			vectorElements++; // vectorElements = ceiling(r*nDevs)
		return vectorElements;
	}

	// ----------------------------------------------------------------------------------------------------
	private double[] smoothingVector(int resolution, double binCt, double nDevs, int vSize, int bin)
	{
		double sqrtN = Math.sqrt(binCt);
		double[] vector = new double[vSize + 1];
		double radius = getRadius(resolution);
		double factor = -0.5 * sqrtN * (nDevs / radius) * (nDevs / radius);
		int i;
		for (i = 0; i <= vSize; i++)
			vector[i] = Math.exp(factor * i * i);
		return normalized(vector, vSize);
	}

	private double[] normalized(double[] vector,int vSize)
	{
		double vectorTotal = 0; 
		for (int i = -vSize; i <= vSize; i++)
			for (int j = -vSize; j <= vSize; j++)
				vectorTotal += vector[Math.abs(i)] * vector[Math.abs(j)];
		
		vectorTotal = Math.sqrt(vectorTotal);
		for (int i = 0; i <= vSize; i++)
			vector[i] /= vectorTotal;
		return vector;
	}
	private double getRadius(int resolution)	{	return 10.0;	}
	
	public LineChart<Number, Number> makeChart()
	{
		NumberAxis  xAxis = new NumberAxis();	
		NumberAxis  yAxis = new NumberAxis();
		LineChart<Number, Number>  chart = new LineChart<Number, Number>(xAxis, yAxis);
		chart.setTitle(getName());
		chart.setCreateSymbols(false);
		chart.getData().add( getDataSeries());	
		chart.setLegendVisible(false);
		chart.setPrefHeight(100);
		VBox.setVgrow(chart, Priority.ALWAYS);
		chart.setId(getName());
		return chart;
	}

}
