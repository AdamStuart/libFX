package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RandomAttributeValueData
{
	
	public static ObservableList<AttributeValue> getRandomAttributeValueData()
	{
		String[] names = new String[] { "-fx-fill", "-fx-stroke" };
		String[] descs = new String[] {  "Azure", "Bisque", "Coral", "DimGray", "Navy", "Firebrick", "Honeydew", "Indigo" };

		ObservableList<AttributeValue> vals = FXCollections.observableArrayList();
		for (int i = 0; i < names.length; i++)
		{
			for (int j = 1; j < descs.length; j++)
				vals.add(new AttributeValue(names[i], descs[j]));
		}
		return vals;
	}


}
