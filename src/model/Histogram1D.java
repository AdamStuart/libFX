package model;

import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class Histogram1D
{
	private int size;
	private int[] counts;
	private Range range;

	// ----------------------------------------------------------------------------------------------------
	public Histogram1D(int len, Range inX)
		{
			size = len;
			counts = new int[size];
			range = inX;
		}

	// ----------------------------------------------------------------------------------------------------
	public void count(double x)
	{
		int bin = (int) (0.5 + ((Math.log(x) - range.min) * range.width()) / size);
		counts[bin]++;
	}

	public void count(float x)
	{
		int bin = (int) (0.5 + ((Math.log(x) - range.min) / range.width()) * size);
		if (bin >= 0 && bin < size)
			counts[bin]++;
	}

	// ----------------------------------------------------------------------------------------------------
	boolean grayscale = true;

	double mode = 0;
	void setMode(double d)	{		mode = d;	}		// this will be the top of the Y axis

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
	public XYChart.Series getDataSeries()
	{
		double[] smoothed = smooth();
		XYChart.Series series = new XYChart.Series();
		for (int i = 0; i < size; i++)
			series.getData().add(new XYChart.Data(i, smoothed[i]));
		
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
