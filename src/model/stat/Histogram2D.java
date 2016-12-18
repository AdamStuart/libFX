package model.stat;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;



public class Histogram2D
{
	public int size;
	public int[][] counts;
	int mode = 0;
	public Range xRange, yRange;
	
	public Histogram2D(int len, Range inX, Range inY)
	{
		size = len;
		counts = new int[size][];
		xRange = inX;
		yRange = inY;
		
		for (int i=0; i<len; i++)
			counts[i] = new int[size];
	}
	
	public void count(double x, double y)
	{
		int xBin = (int) (0.5 + ((x - xRange.min) * xRange.width()) / size);
		int yBin = (int) (0.5 + ((y - yRange.min) * yRange.width()) / size);
		counts[xBin][yBin]++;
	}
	
	public int calcMode()
	{
		for (int i=0; i<size; i++)
			for (int j=0; j<size; j++)
				mode = Math.max(mode, counts[i][j]);
		return mode;
	}
	
	public Image asImage()
	{
		if (mode <= 0) 	calcMode();
		WritableImage pixels = new WritableImage(size, size);
		
		final PixelWriter pixelWriter = pixels.getPixelWriter();
		
		for (int row=0; row<size; row++)
			for (int col=0; col<size; col++)
				pixelWriter.setColor(col,row, colorLookup(counts[row][col]));
		return pixels;
		
	}
	boolean grayscale = true;
	

	Color colorLookup(int val)
	{
		if (mode != 0) 
		if (grayscale)
		{
			double v = Math.log(val) / Math.log(mode);
			if (v > 1) v= 1;
			if (v < 0) v= 0;
			return new Color(v,v,v,1);
		}
		return Color.RED;
	}
}
