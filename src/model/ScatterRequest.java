package model;

public class ScatterRequest extends GraphRequest
{
	public ScatterRequest(String xDim, String yDim, String parent, String ... children)
	{
		super(Graph.SCATTER, xDim, yDim, parent, children);
	}
}
