package model;

import java.util.Formatter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class XYDataRow
{
	private static final long serialVersionUID = 1252647336147L;

	IntegerProperty id = new SimpleIntegerProperty();
	IntegerProperty dataset = new SimpleIntegerProperty();
	DoubleProperty X = new SimpleDoubleProperty();
	DoubleProperty Y = new SimpleDoubleProperty();

	public XYDataRow(double inX, double inY, int inId, int inDataset)
	{
		id.set(inId);
		dataset.set(inDataset);
		X.set(inX);
		Y.set(inY);
	}

	public void toFormattedString(Formatter outFile)
	{
		outFile.format("%f %f %d %d\n", getX(), getY(), getId(), getDataset());
	}

	// @formatter:off
	public double getX()		{		return X.getValue();		}
	public double getY()		{		return Y.getValue();		}
	public int getId()			{		return id.getValue();		}
	public int getDataset()		{		return dataset.getValue();		}

}
