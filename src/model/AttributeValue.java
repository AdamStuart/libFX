package model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class AttributeValue {

	    private final StringProperty attribute = new SimpleStringProperty();
	    private final StringProperty value = new SimpleStringProperty();

	    public AttributeValue()  					{ 	}
	    public AttributeValue(String a, String v)  	{ setAttribute(a); setValue(v);	}
	    public String getAttribute() 				{	        return attribute.get();	    }
	    public void setAttribute(String s) 			{	    	attribute.set(s);	    }
	    public StringProperty attributeProperty() 	{	        return attribute;	    }

	    public String getValue() 					{	        return value.get();	    }
	    public void setValue(String s) 				{	    	value.set(s);	    }
	    public StringProperty valueProperty() 		{	        return value;	    }
		public String makeString()					{			return getAttribute() + ": " + getValue()  + "; ";		}

}
