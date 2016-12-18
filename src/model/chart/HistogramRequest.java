package model.chart;

import model.stat.GraphRequest;

public class HistogramRequest extends GraphRequest
{
	public HistogramRequest(String xDim, String parent, String ... children)
	{
		super(Graph.HISTOGRAM, xDim, ".", parent, children);
	}
}
