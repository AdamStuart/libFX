package model;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;



public class Histogram2D
{
	int size;
	int[][] counts;
	Range xRange, yRange;
	
	public Histogram2D(int len, Range inX, Range inY)
	{
		size = len;
		counts = new int[size][];
		for (int i=0; i<len; i++)
			counts[i] = new int[size];
	}
	
	public void count(double x, double y)
	{
		int xBin = (int) (0.5 + ((x - xRange.min) * xRange.width()) / size);
		int yBin = (int) (0.5 + ((y - yRange.min) * yRange.width()) / size);
		counts[xBin][yBin]++;
	}
	
	public Image asImage()
	{
		WritableImage pixels = new WritableImage(size, size);
		
		final PixelWriter pixelWriter = pixels.getPixelWriter();
		
		for (int row=0; row<size; row++)
			for (int col=0; col<size; col++)
				pixelWriter.setColor(col,row, colorLookup(counts[row][col]));
		return pixels;
		
	}
	boolean grayscale = true;
	
	double mode = 0;
	void setMode(double d) { mode = d;	}
	Color colorLookup(int val)
	{
		if (mode != 0) 
		if (grayscale)
		{
			double v = val / mode;
			return new Color(v,v,v,1);
		}
		return Color.RED;
	}
}
