package model.chart;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.LineChart;
import model.stat.Histogram1D;
import model.stat.Range;

public class DimensionRecord {
	String title;
	List<Double> values;
	Range range;
	Histogram1D histogram;
	LineChart<Number, Number> chart;
	public String getTitle()	{ return title; }
	public LineChart<Number, Number> getChart()	{ return chart;	}
	public double getValue(int i)	{ return values.get(i);	}
	public DimensionRecord(String s, List<Double> vals)
	{
		title = s;
		values = new ArrayList<Double>();
		values.addAll(vals);
		calcRange();
		histogram = new Histogram1D(title, 200, range);
		histogram.count(values);
	}
	@Override public String toString()	{ return title;	}
	public int getNValues()	{ return values.size();	}
	void calcRange()
	{
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (double d : values)
		{
			if (Double.isNaN(d)) continue;
			if (d > max) max = d;
			if (d < min) min = d;
		}
		range = new Range(min, max);
	}
	public Range getRange() {		return range;	}
	
	public void build1DChart()
	{
		chart = histogram.makeRawDataChart();
		chart.setPrefHeight(100);
		System.out.println("Charting " + title);
	}

}
