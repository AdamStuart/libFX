package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SubjectObjectPredicate
{
    private final StringProperty subject = new SimpleStringProperty();
    private final StringProperty object = new SimpleStringProperty();
    private final StringProperty predicate = new SimpleStringProperty();

    public SubjectObjectPredicate()  					{ 	}
    public SubjectObjectPredicate(String sub, String ob, String pred)  	{ setSubject(sub); setObject(ob);	 setPredicate(pred);	}
    public String getSubject() 				{	        return subject.get();	    }
    public void setSubject(String s) 			{	    	subject.set(s);	    }
    public StringProperty subjectProperty() 	{	        return subject;	    }

    public String getObject() 					{	        return object.get();	    }
    public void setObject(String s) 			{	    	object.set(s);	    }
    public StringProperty objectProperty() 		{	        return object;	    }

    public String getPredicate() 				{	        return predicate.get();	    }
    public void setPredicate(String s) 			{	    	predicate.set(s);	    }
    public StringProperty predicateProperty() 	{	        return predicate;	    }

    public String makeString()					{			return getSubject() + "-> " + getPredicate()  + "-> " + getObject()  + "; ";		}

}
