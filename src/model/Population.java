package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Population
{
	SimpleStringProperty name = new SimpleStringProperty();
	SimpleDoubleProperty count = new SimpleDoubleProperty();
	SimpleDoubleProperty expected = new SimpleDoubleProperty();
	SimpleDoubleProperty observed = new SimpleDoubleProperty();
	
	public Population(String s)
	{
		setName(s);
	}
	
	public String getName()				{ return name.get();	}
	public double getCount()			{ return count.get();	}
	public double getExpected()			{ return expected.get();	}
	public double getObserved()			{ return observed.get();	}
	
	public void setName(String s)		{ name.set(s);	}
	public void setCount(double ct)		{  count.set(ct);	}
	public void setExpected(double ct)	{  expected.set(ct);	}
	public void setObserved(double ct)	{  observed.set(ct);	}
	
	public SimpleStringProperty nameProperty()	{ return name;	}
	public SimpleDoubleProperty countProperty()	{ return count;	}
	public SimpleDoubleProperty expectedProperty()	{ return expected;	}
	public SimpleDoubleProperty observedProperty()	{ return observed;	}
}
