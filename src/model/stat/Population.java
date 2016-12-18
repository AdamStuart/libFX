package model.stat;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Population
{
	SimpleStringProperty name = new SimpleStringProperty();
	SimpleStringProperty marker = new SimpleStringProperty();
	SimpleDoubleProperty count = new SimpleDoubleProperty();
	SimpleDoubleProperty low = new SimpleDoubleProperty();
	SimpleDoubleProperty high = new SimpleDoubleProperty();
	
	public Population(String s)
	{
		setName(s);
	}
	
	public void setRange(double lo, double hi)	{	setLow(lo);		setHigh(hi);	}
	
	public String getName()				{ return name.get();	}
	public double getCount()			{ return count.get();	}
	public String getCountStr()			{ return String.format("%.1f", count.get());	}
	public double getLow()				{ return low.get();	}
	public double getHigh()				{ return high.get();	}
	public String getMarker()			{ return marker.get();	}
	
	public void setName(String s)		{ name.set(s);	}
	public void setCount(double ct)		{  count.set(ct);	}
	public void setLow(double ct)		{  low.set(ct);	}
	public void setHigh(double ct)		{  high.set(ct);	}
	public void setMarker(String s)		{  marker.set(s);	}
	
	public SimpleStringProperty nameProperty()	{ return name;	}
	public SimpleStringProperty markerroperty()	{ return marker;	}
	public SimpleDoubleProperty countProperty()	{ return count;	}
	public SimpleDoubleProperty lowProperty()	{ return low;	}
	public SimpleDoubleProperty highProperty()	{ return high;	}
}
