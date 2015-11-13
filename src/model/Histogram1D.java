package model;

import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class Histogram1D
{
	private int size;
	private int[] counts;
	private Range range;
	boolean isLog = true;

	public String toString() { return range.toString(); }
	public Range getRange()	{ return range;	}
	// ----------------------------------------------------------------------------------------------------
	public Histogram1D(int len, Range inX)
	{
		size = len;
		counts = new int[size];
		range = inX;
		if (range.width() == 0)		{ 	range.min = 0;	range.max = 1; }
	}

	public Histogram1D(int len, Range inX, boolean log)
	{
		this(len, inX);
		isLog = log;
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
	
	private int getArea()
	{
		int area = 0;
		for (int i=0; i<size; i++)
			area += counts[i];
		return area;
	}
	int counter = 0;
	// ----------------------------------------------------------------------------------------------------
	public void count(double x)
	{
		int bin = -1;
		if (isLog)
			bin = (int) (0.5 + ((Math.log(x) - range.min) * range.width()) / size);
		else
			bin = (int) (0.5 + ((x - range.min) / range.width()) * size);

		if (bin < 0) bin = 0;
		if (bin >= size) bin = size-1;
		counts[bin]++;
		counter++;
		System.out.println("incrementing " + counter);
	}

//	public void count(float x)
//	{
//		int bin = (int) (0.5 + ((Math.log(x) - range.min) / range.width()) * size);
//		if (bin >= 0 && bin < size)
//			counts[bin]++;
//	}

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
	public XYChart.Series getDataSeries()
	{
		double[] smoothed = smooth();
		double scale = range.width() / size;
		XYChart.Series series = new XYChart.Series();
		for (int i = 0; i < size; i++)
			series.getData().add(new XYChart.Data(i * scale, smoothed[i]));
		
//		if (series.nodeProperty() != null)
//		{
//			ObjectProperty<Node> p = series.nodeProperty();
//					((Node)(series.nodeProperty().getValue())).setVisible(false);
//		}
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

}
