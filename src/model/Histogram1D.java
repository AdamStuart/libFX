package model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
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

	// ----------------------------------------------------------------------------------------------------
	public double getPercentile(int perc)
	{
		double val = 0;
		int area = getArea();
		int evCount = area * perc / 100;
		int i;
		for (i=0; val<evCount; i++)
			val += counts[i];
		double out = range.min + (i * range.width() / size);		// TODO LOG??
		return out;
	}
	
	public int getArea()
	{
		int area = 0;
		for (int i=0; i<size; i++)
			area += counts[i];
		return area;
	}
	// ----------------------------------------------------------------------------------------------------
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
			return Math.log(range.min + bin  * range.width()/ size) - 5;   // TODO Transform fn subtracts 5 log  -- asymmetric!!!

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


	void setMode(double d)	{		mode = d;	} // this will determine the top of the Y axis

	public double getMode()	{
		int max = 0;
		for (int row = 0; row < size; row++)
			max = Math.max(max, counts[row]);
		return max;

	}
	public double getModePosition()	{
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
	public void calcDistributionStats()	{
		int sum = 0;
		count = 0;
		for (int row = 0; row < size; row++)
		{
			count += counts[row];
			sum += counts[row] * binToVal(row);
		}
		mean = sum / count;
		 
		int total = 0;
		int row = 0;
		while (total < count/100.)
			total += counts[row++];
		firstPercentile = row;
		while (total < count/10.)
			total += counts[row++];
		tenthPercentile = row;

		while (total < count/2.)
			total += counts[row++];
		median = binToVal(row);
		
		while (total < 9 * count/10.)
			total += counts[row++];
		ninetiethPercentile = row;
		while (total < 99 * count/100.)
			total += counts[row++];
		topPercentile = row;
	}
	
	private	int firstPercentile, tenthPercentile, ninetiethPercentile, topPercentile;
	private double count = 0;
	private double mode = 0;
	private double median;
	private double mean;
	private double stDev;
	private double below1Stdev;
	private double below2Stdev;
	private double above1Stdev;
	private double above2Stdev;
	
	public double getMedian()			{ return median;	}
	public double getMean()				{ return mean;	}
	public double getStDev()			{ return stDev;	}
	
	public double getBelow1Stdev()		{ return below1Stdev;	}
	public double getBelow2Stdev()		{ return below2Stdev;	}
	public double getAbove1Stdev()		{ return above1Stdev;	}
	public double getAbove2Stdev()		{ return above2Stdev;	}

	public int 	getPercentile1()		{ return firstPercentile;	}
	public int 	getPercentile10()		{ return tenthPercentile;	}
	public int 	getPercentile90()		{ return ninetiethPercentile;	}
	public int 	getPercentile99()		{ return topPercentile;	}
	

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
					x = (x > 0) ? (Math.log(x) - 5) : 0;			// resolve this with valtobin
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
	
	// ----------------------------------------------------------------------------------------------------
	public OverlaidLineChart makeChart()
	{
		NumberAxis  xAxis = new NumberAxis();	
		NumberAxis  yAxis = new NumberAxis();
		OverlaidLineChart  chart = new OverlaidLineChart(xAxis, yAxis);
		chart.setTitle(getName());
		chart.setCreateSymbols(false);
		chart.getData().add( getDataSeries());	
		chart.setLegendVisible(false);
		chart.setPrefHeight(100);
		VBox.setVgrow(chart, Priority.ALWAYS);
		chart.setId(getName());
		return chart;
	}
	
	public OverlaidLineChart makePeakFitChart()
	{
		OverlaidLineChart chart = makeChart();
		List<Double> peaks = new ArrayList<Double>();
		List<Double> valleys = new ArrayList<Double>();
		scanPeaks(peaks, valleys);
		
		NumberAxis  xAxis = (NumberAxis) chart.getXAxis();
		NumberAxis  yAxis = (NumberAxis) chart.getYAxis();
	    double min = xAxis.getLowerBound();
	    double max = xAxis.getUpperBound();
	    double ymin = yAxis.getLowerBound();
	    double ymax = yAxis.getUpperBound();

		chart.layout();
//		for (int i=0; i< 10; i++)
		chart.addVerticalValueMarker(new Data<Number, Number>(1, 1 ), Color.RED, 1);
		chart.addVerticalValueMarker(new Data<Number, Number>(2, 1 ), Color.RED, 1);
		for (Double p : peaks)
		{
//			double x = xAxis.getDisplayPosition(p);
//			chart.addVerticalValueMarker(new Data<Number, Number>(x, 1), Color.CYAN, 1.8);
			chart.addVerticalValueMarker(new Data<Number, Number>(p, 1), Color.DARKCYAN, 2.8);
		}
		for (Double v : valleys)
		{
//			double x = xAxis.getValueForDisplay(v).doubleValue();
			chart.addVerticalValueMarker(new Data<Number, Number>(v, 0), Color.DARKGREEN, .7);
		}
	
		return chart;
	}

	
	private void scanPeaks(List<Double> peaksa, List<Double> valleysa)
	{
		int NOISE = 5;
		boolean ascending = false;
		boolean descending = false;
		List<Integer> peaks = new ArrayList<Integer>();
		List<Integer> valleys = new ArrayList<Integer>();
		double[] smoothed = smooth();
		for (int i=1; i<size; i++)
		{
			if (smoothed[i] == smoothed[i-1])		continue;
			if (smoothed[i] < NOISE)				continue;
			if (smoothed[i] > smoothed[i-1])
			{
				if (descending) valleys.add(new Integer(i-1));
				ascending = true;
				descending = false;
			}
			else if (smoothed[i] < smoothed[i-1])
			{
				if (ascending) peaks.add(new Integer(i-1));
				descending = true;
				ascending = false;
			}
		}
		
		// needs a pass here to remove minor peaks and valleys (tho' smoothing does much of this)
		for (int i=peaks.size()-1; i>0; i--)
		{
			int cur = peaks.get(i);
			int prevPeak = peaks.get(i-1);
			int valley = valleys.size() > i ? valleys.get(i-1) : 0;
			if ((valley / (cur + prevPeak) > 0.8) && ((cur - prevPeak < 5)))
			{
				peaks.remove(i);
				valleys.remove(i-1);
			}
		}	
		
		if (peaks.size() == 0)
		{
			System.out.println("Error, peaksize = 0, Nothing rose above 5 event noise");
			return;
		}
		
		if (peaks.size() == 1)
		{
			System.out.println("Single Peak at " + peaks.get(0));
			peaksa.add(new Double(binToVal(peaks.get(0))));
			return;
		}
		for (Integer i : peaks)
			peaksa.add(new Double(binToVal(i)));
		
		for (Integer i : valleys)
			valleysa.add(new Double(binToVal(i)));
		
		System.out.println("Found " + peaks.size() + " peaks and " + valleys.size() + " valleys.");
	
		if (peaks.size() > 1)
		{
			for (int i=0; i<peaks.size()-1; i++)
			{
				System.out.println("Peak at " + peaksa.get(i) + " has height of " + counts[peaks.get(i)]);
				if (i < valleys.size())
					System.out.println("Valley at " + valleysa.get(i) + " has height of " + counts[valleys.get(i)]);
			}
			System.out.println("----------------------------------------------------------");
			System.out.println("");
			System.out.println("");
		}
		
	}
	public LineChart<Number, Number> makeRawDataChart()
	{
		NumberAxis  xAxis = new NumberAxis();	
		NumberAxis  yAxis = new NumberAxis();
		LineChart<Number, Number>  chart = new LineChart<Number, Number>(xAxis, yAxis);
		chart.setTitle(getName());
		chart.setCreateSymbols(false);
		chart.getData().add( rawDataSeries());	
		chart.setLegendVisible(false);
		chart.setPrefHeight(300);
		
		// draw lines at 5 percentiles, mode, median
		
		VBox.setVgrow(chart, Priority.ALWAYS);
		chart.setId("Profile: " + getName());
		return chart;
	}
}
