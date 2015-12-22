package model;

import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
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
	private List<Peak> peaks = FXCollections.observableArrayList();
	public List<Peak> getPeaks()	{ return peaks;	}
// ----------------------------------------------------------------------------------------------------
	
	public String toString() { return name + "  " + range.toString(); }
	public Range getRange()	{ return range;	}
	public int getSize()	{ return size;	}
	public String getName() { return name; 	}
	public int[] getCounts() { return counts; 	}
	public int get(int i)	{ return counts[i];	}
	public double getValue(int i)	{ return smoothed == null ? get(i) : smoothed[i];	}
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
		if (range.width() == 0)		{ 	range.min = 0;	range.max = size; }
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
	int area = 0;

	public int getArea()
	{
		if (area == 0)
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
		return range.min + (range.width() * position) / size;
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
		while (total < count/100.)			total += counts[row++];
		firstPercentile = row;
		while (total < count/10.)			total += counts[row++];
		tenthPercentile = row;

		while (total < count/2.)			total += counts[row++];
		median = binToVal(row);
		
		while (total < 9 * count/10.)		total += counts[row++];
		ninetiethPercentile = row;
		while (total < 99 * count/100.)		total += counts[row++];
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
	
	public double 	getPercentile1Val()			{ return binToVal(firstPercentile);	}
	public double 	getPercentile10Val()		{ return binToVal(tenthPercentile);	}
	public double 	getPercentile90Val()		{ return binToVal(ninetiethPercentile);	}
	public double 	getPercentile99Val()		{ return binToVal(topPercentile);	}
	
	public String getStatString()
	{
		double modeHeight = getMode();
		double mode =  getModePosition();
		double area = getArea();
		double mean = getMean();
		modeHeight /= area;
		mode = Math.log(mode) - 5;
		String s = String.format("Stats: \nHeight: %.2f\nMode:  %.2f\n", modeHeight, mode);
		s += String.format("Mean: %.2f\n",  mean);
		s += String.format("Median: %.2f\n90th:%.2f\n99th: %.2f\n",  
						getMedian(),getPercentile90Val(), getPercentile99Val());
		return s;
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
	
	public XYChart.Series getDataSeries(double yOffset )
	{
		return getDataSeries(yOffset, getArea());
	}
	
	public XYChart.Series getDataSeries(double yOffset, double area)
	{
		XYChart.Series series = new XYChart.Series();
		try
		{
			double[] smoothed = smooth();
		
				double scale = range.width() / (size+1);
				for (int i = 0; i < size; i++)
				{
					double x = range.min + (i * scale);
					x = (x > 0) ? (Math.log(x) - 5) : 0;			// resolve this with valtobin
	//				if (x < 0) x = 0;
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
	
	private double[] smoothed;
	// ----------------------------------------------------------------------------------------------------
	public double[] smooth()
	{
		if (smoothed != null) return smoothed;
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
		smoothed = destBins;
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
	
//	List<Double> valleys = new ArrayList<Double>();
	public OverlaidLineChart makePeakFitChart()
	{
		OverlaidLineChart chart = makeChart();
		return chart;
	}

	public void addPeakMarkers(OverlaidLineChart peakFitChart)
	{
		scanPeaks();
		for (Peak p : peaks)
		{
			p.calcStats();
			peakFitChart.addVerticalValueMarker(new Data<Number, Number>(binToVal((int) p.getMean()), -1), Color.DARKCYAN, 2.8);
			p.setHistogram(this);
			peakFitChart.addBellCurveMarker(p, Color.GREENYELLOW, 1);
		}
	}
	
	// ----------------------------------------------------------------------------------------------------
//	private void scanPeaks(List<Peak> peaksa, List<Double> valleysa)
//	{
//		int NOISE = 5;
//		boolean ascending = false;
//		boolean descending = false;
//		List<Integer> peaks = new ArrayList<Integer>();
//		List<Integer> valleys = new ArrayList<Integer>();
//		double[] smoothed = smooth();
//		for (int i=1; i<size; i++)
//		{
//			if (smoothed[i] == smoothed[i-1])		continue;
//			if (smoothed[i] < NOISE)				continue;
//			if (smoothed[i] > smoothed[i-1])
//			{
//				if (descending) valleys.add(new Integer(i-1));
//				ascending = true;
//				descending = false;
//			}
//			else if (smoothed[i] < smoothed[i-1])
//			{
//				if (ascending) peaks.add(new Integer(i-1));
//				descending = true;
//				ascending = false;
//			}
//		}
//		
//		// needs a pass here to remove minor peaks and valleys (tho' smoothing does much of this)
//		for (int i=peaks.size()-1; i>0; i--)
//		{
//			int cur = peaks.get(i);
//			int prevPeak = peaks.get(i-1);
//			int valley = valleys.size() > i ? valleys.get(i-1) : 0;
//			if ((valley / (cur + prevPeak) > 0.8) && ((cur - prevPeak < 5)))
//			{
//				peaks.remove(i);
//				valleys.remove(i-1);
//			}
//		}	
//		
//		if (peaks.size() == 0)
//		{
//			System.out.println("Error, peaksize = 0, Nothing rose above 5 event noise");
//			return;
//		}
//		
//		if (peaks.size() == 1)
//		{
//			System.out.println("Single Peak at " + peaks.get(0));
//			double mean =binToVal(peaks.get(0));
//			double stdev = 0.5 * mean;
//			double amp = smoothed[peaks.get(0)];
//			Peak pk = new Peak(mean, stdev, amp);
//			peaksa.add(pk);
//			return;
//		}
//		for (Integer i : peaks)
//		{
//			double mean =binToVal(i);
//			double stdev = 0.5 * mean;
//			double amp = smoothed[i];
//			Peak pk = new Peak(mean, stdev, amp);
//			peaksa.add(pk);
//		}
//		
//		for (Integer i : valleys)
//			valleysa.add(new Double(binToVal(i)));
//		
//		System.out.println("Found " + peaks.size() + " peaks and " + valleys.size() + " valleys.");
//	
//		if (peaks.size() > 1)
//		{
//			for (int i=0; i<peaks.size()-1; i++)
//			{
//				System.out.println("Peak at " + peaksa.get(i) + " has height of " + counts[peaks.get(i)]);
//				if (i < valleys.size())
//					System.out.println("Valley at " + valleysa.get(i) + " has height of " + counts[valleys.get(i)]);
//			}
//			System.out.println("----------------------------------------------------------");
//			System.out.println("");
//			System.out.println("");
//		}
//		
//	}
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
//------------------------------------------------------------------------------
	public void scanPeaks()
	{
		peaks.clear();
//		if (peaks.size() > 0) 		return;  // already done
		boolean useMean = false;  		
		boolean allPeaks = false;  	
		boolean shoulderPeaks = false; 

		int mode = 0; 
		double[] peakHisto = new double[size];
		double maxPeakHeight = 0;
		try
		{
			int bin;
			boolean finished = false;
			int totalPeakArea = 0;
			int area = 0;
			for (int i = 0; i < size; i++)
	            area += (peakHisto[i] = getValue(i));
			while (!finished && peaks.size() < 10)
			{
				double modeValue = 0.;
				for (bin = 0; bin < size; bin++)  // A
					if (peakHisto[bin] > modeValue)
					{
						modeValue = peakHisto[bin];
						mode = bin;
					}
				if (modeValue == 0)			{	finished = true;	break;		}

				int[] pkBounds = new int[2];			
				int[] fringe = new int[2];
				pkBounds[0] = pkBounds[1] = fringe[0] = fringe[1] = mode; // for now
				double halfMax = modeValue / 2.;
				double WIDTHFACTOR = 1.5;
				int dx = -1; 
				for (int direction = 0; direction < 2; direction++)
				{
					double halfWidthHalfMax = 0.;
					boolean everDroppedBelow75 = false;
					boolean stop = false;
					double FLOOR = 0.01;
					for (bin = mode; bin < size && bin >= 0 && !stop; bin += dx)
					{
						int slopeWidth = (int) pin(halfWidthHalfMax, size / 32., size / 8.);
						int slopeLow = Math.max(0, bin - slopeWidth / 2);
						int slopeHigh = Math.min(size - 1, bin + slopeWidth / 2);
						double slope = getSlope(peakHisto, slopeLow, slopeHigh - slopeLow + 1);
//						boolean rising = (direction == 0) == (slope < 0);
						boolean rising = (direction == 0) ? slope < 0 : slope > 0;

						if (halfWidthHalfMax > 0 && pkBounds[direction] == mode) 
						{
							double curWidth = Math.abs(bin - mode);
							double expected = Math.abs(modeValue - peakHisto[bin]) / halfMax * halfWidthHalfMax;
							if (peakHisto[bin] < FLOOR)
							{
								pkBounds[direction] = bin;
								stop = true; 
							}
							else if (curWidth > WIDTHFACTOR * expected) 		
								pkBounds[direction] = bin;						
							else if (curWidth > expected && rising) 			
							{
								pkBounds[direction] = bin;
								stop = true;
							}
						}
						else if (halfWidthHalfMax > 0) 		{	if (peakHisto[bin] == 0. || rising)	 stop = true;	}
						else if (peakHisto[bin] < halfMax) 					
						{
							int chanMinusOne = Math.max(0, bin - 1);
							halfWidthHalfMax = Math.abs(bin - mode) - ((halfMax - peakHisto[bin]) / (peakHisto[chanMinusOne] - halfMax));
						}
						else if (rising && shoulderPeaks && everDroppedBelow75)
						{
							pkBounds[direction] = bin;
							stop = true;
						}
						if (peakHisto[bin] < halfMax * 75. / 50.) everDroppedBelow75 = true;
					}
					
					fringe[direction] = pin(bin, 0, size - 1);
					if (pkBounds[direction] == mode)
						pkBounds[direction] = fringe[direction] = ((direction == 0) ? 0 : (size - 1)); 
					dx = 1;
				}
				Peak peak = new Peak(this);
				if (pkBounds[0] <= 0)			pkBounds[0] = 0;
				if (fringe[0] <= 0)		fringe[0] = 0;

				peak.setBounds(pkBounds[0], pkBounds[1]);
				peak.setAmplitude( modeValue);
				maxPeakHeight =Math.max(maxPeakHeight, modeValue);
				peak.setArea(0);
				if (useMean)          											
				{
					double tempArea = 0.;
					double count = 0.;
					for (bin = pkBounds[0]; bin <= pkBounds[1]; bin++)
					{
						count += peakHisto[bin];
						tempArea += bin * peakHisto[bin];
					}
//					peak.setMean(tempArea / count);
					peak.setArea(count);
				}
				else
				{ 
					for (bin = pkBounds[0]; bin < pkBounds[1]; bin++)
						peak.addArea(peakHisto[bin]);
					double seen = 0.;
					for (bin = pkBounds[0]; bin <= pkBounds[1] && seen < peak.getArea() / 2; bin++)
						seen += peakHisto[bin];
					if (bin == 0) peak.setMean(bin);
					else
					{
						double crossover = peakHisto[bin - 1];
						double intoChannel = seen - peak.getArea() / 2;
						double ratio = intoChannel / crossover;
						peak.setMean(bin - ratio);
					}
				}
				
				for (bin = fringe[0]; bin <= fringe[1]; bin++)
				{
					totalPeakArea += peakHisto[bin];
					peakHisto[bin] = 0.;
				}
				double minArea = getMinPeakArea(area);
				if (!allPeaks && (peak.getArea() < minArea)) 
					finished = (((double) (totalPeakArea) / (double) (peak.getArea())) > 0.95);		// STOP at > 96% classified
				else peaks.add(peak);
			}
		}
		catch (Throwable ex)		{			ex.printStackTrace();		}
		Collections.sort(peaks);
		for (Peak p: peaks)
			System.out.println(toString() + " " + p.toString());
	}
	
	private double getMinPeakArea(int area)	{		return Math.min(100., area / 100.);	}
		
	int pin(int x,int min,int max) { return Math.min(max,  Math.max(x, min));  }
	double pin(double x,double min,double max) { return Math.min(max,  Math.max(x, min));  }
	boolean between(double x,double min,double max) { return min <= x && x < max;	} 
	
//  ---------------------------------------------------
   static public Double getSlope(double[] x, int start, int nValues)
    {
        double totalXY = 0., totalX = 0., totalY = 0., totalXSquared = 0.;
        for (int i = 0; i < nValues; i++)
        {
        	double xi = x[start + i];
            totalX += i;
            totalXSquared += i * i;
            totalY += xi;
            totalXY += i * xi;
        }
        double slope = 0.;
        double denom = (nValues * totalXSquared - totalX * totalX);
        if (denom != 0)
            slope = (nValues * totalXY - totalX * totalY) / denom;
        return slope;
    }
//  ---------------------------------------------------

}

