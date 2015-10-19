package model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;


public class AttributeValue {

	    private final StringProperty attribute = new SimpleStringProperty();
	    private final StringProperty value = new SimpleStringProperty();

	    public AttributeValue()  					{ 	}
	    public AttributeValue(String rawString)  	{ 	
	    	String[] duo = rawString.split(":");
	    	if (duo.length == 2) { setAttribute(duo[0].trim()); setValue(duo[1].trim());	}}
	    
	    public AttributeValue(String a, String v)  	{ setAttribute(a); setValue(v);	}
	    public String getAttribute() 				{	        return attribute.get();	    }
	    public void setAttribute(String s) 			{	    	attribute.set(s);	    }
	    public StringProperty attributeProperty() 	{	        return attribute;	    }

	    public String getValue() 					{	        return value.get();	    }
	    public void setValue(String s) 				{	    	value.set(s);	    }
	    public StringProperty valueProperty() 		{	        return value;	    }
		public String makeString()					{			return getAttribute() + ": " + getValue()  + "; ";		}
		public static ObservableList<AttributeValue> parseList(String string)
		{
			System.err.println(" TODO Auto-generated method stub");
			return null;
		}
		@Override public String toString()	{ return getAttribute() + ": " + getValue();	}

}
