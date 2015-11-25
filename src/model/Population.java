package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Population
{
	SimpleStringProperty name = new SimpleStringProperty();
	SimpleDoubleProperty count = new SimpleDoubleProperty();
	
	public Population(String s)
	{
		setName(s);
	}
	public double getCount()	{ return count.get();	}
	public String getName()	{ return name.get();	}
	public void setCount(double ct)	{  count.set(ct);	}
	public void setName(String s)	{ name.set(s);	}
	public SimpleStringProperty nameProperty()	{ return name;	}
	public SimpleDoubleProperty countProperty()	{ return count;	}
}
