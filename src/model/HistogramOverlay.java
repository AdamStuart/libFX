package model;

import java.util.ArrayList;
import java.util.List;

public class HistogramOverlay extends ArrayList<Histogram1D>
{
	double myMin = Double.MAX_VALUE;
	double myMax = Double.MIN_VALUE;
	
	HistogramOverlay(List<Histogram1D> items)
	{
		super();
		for (Histogram1D h : items)
		{
			Range range = h.getRange();
			union(range);
		}
		
	}
	
	private void union(Range r)
	{
		double min = r.min;
		double max = r.max;
		if (min < myMin)	myMin = min;
		if (max > myMax)	myMax = max;
	}
	
}
