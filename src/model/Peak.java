package model;

import javafx.scene.Node;
import javafx.scene.shape.Path;

public class Peak implements Comparable<Peak>
{
	private double mean;
	private double stdev;
	private double amplitude;
	private double area;
	private double min;
	private double max;
	private Histogram1D histogram;
	private Path path;
	
	public Peak()
	{
		this(1,1,1);
	}
    @Override public int compareTo(Peak x)  {  return  x.mean > mean ? -1 : 1;    }
	public Peak(double m, double s, double a)
	{
		mean = m;
		stdev = s;
		amplitude = a;
		area = 0;
		min = 0;
		max = 1;
		path = null;
	}
	public String toString()	{ return String.format("[ %.0f - %.0f ] %.2f @ %.2f, area: %.2f", min, max, amplitude, mean, area); }

	public Peak(Histogram1D h)
	{
		this();
		histogram = h;
	}
//@formatter:off
	public double getMean()  		{ 	return mean;	}
	public void setMean(double d)  	{  	mean = d;	}
	public double getStdev()  		{ 	return stdev;	}
	public void setStdev(double d)  {  	stdev = d;	}
	public double getAmplitude()  	{ 	return amplitude;	}
	public double getUnitAmplitude()  	{ 	return amplitude  / histogram.getArea();	}
	public void setAmplitude(double d)  {  amplitude = d;	}

	public Histogram1D getHistogram()  {  return histogram;	}
	public void setHistogram(Histogram1D h)  {  histogram = h;	}
	
	public void setBounds(double a, double b)  {  min = a; max = b;	}
	public double getWidth()	{ return max - min;	}
	public double getMin()  		{ 	return  min;	}
	public double getMax()  		{ 	return  max;	}
	public double getArea()			{  	return area; }
	public void setArea(double a)	{   area = a; }
	public void addArea(double a)	{   area += a; }
	public double get(double x)		{	return gauss(x, mean, stdev, getUnitAmplitude());	}
	public double getCV()			{	return stdev / mean;	}
//@formatter:on
	
	public Path getPath()
	{
		if (path == null)
		{
			path = new Path();
			
			
			for (double x = mean - 3 * stdev; x <= mean + 3 * stdev; x += stdev)
			{
				
			}
		}
		return path;
	}
	
	public void setNode(Node n)
	{
		if (n instanceof Path)
			path = (Path) n;
	}

	public void calcStats()
	{
		double width = 1 + max - min;
		amplitude = area / width;
		double var = 0;
		for (int i = (int) min; i<max; i++)
		{	
			double d = amplitude - histogram.get(i); 
			var += (d * d);
		}
		stdev = Math.sqrt(var / width);
	}
	
	public static double gauss(double x,double mn,double sd,double amp)
	{
		double exp = -((x-mn) * (x-mn)) / (2 * sd  * sd);
		double scale = 1 / (sd) * Math.sqrt(2 * Math.PI);
		double val = scale * Math.exp(exp);
//		System.out.println(String.format("%.2f",  val));
		return val * amp;
	}

}
