package model;

public class LogHistogram2D extends Histogram2D
{
	
	public LogHistogram2D(int len, Range inX, Range inY)
	{
		super(len, inX, inY);
	}
	
	public void count(double x, double y)
	{
		double xWidth = Math.log(xRange.max / xRange.min);
		double yWidth =  Math.log(yRange.max / yRange.min);
		double xPos = Math.log(x / xRange.min);
		double yPos = Math.log(y / yRange.min);
		int xBin = Math.max(1,(int) ((xPos / xWidth) * size));
		int yBin = Math.max(1,(int) ((yPos / yWidth) * size));
		if ((xBin < 0 || xBin > size) ||(yBin < 0 || yBin > size)) 
			System.err.println("bin out of range: " + xBin + ", " + yBin);
		else
			counts[xBin-1][yBin-1]++;
	}
}
