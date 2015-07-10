package model;

import java.util.List;

import javafx.collections.FXCollections;

/*
 * preliminary implementation of a map of triples.
 * now that Java8 defines Triple as a type, I should use that
 */
public class RDFMap
{

	List<String> strings = FXCollections.observableArrayList();
	
	public RDFMap()
	{
	}

	public RDFMap(SubjectObjectPredicate entry)
	{
		this();
		add(entry);
	}
	
	public void add(SubjectObjectPredicate entry)
	{
		strings.add(entry.getSubject());
		strings.add(entry.getObject());
		strings.add(entry.getPredicate());
	}

}
