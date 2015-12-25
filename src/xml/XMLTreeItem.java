package xml;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

public class XMLTreeItem extends TreeItem<org.w3c.dom.Node>
{
	public XMLTreeItem()
	{
		super();
	}
	public XMLTreeItem(XMLTreeItem orig)
	{
		super();
		setValue(orig.getValue());
	}
//	
//	public XMLTreeItem getChild(String name)
//	{
//		return XMLTools.getChildByName(getValue(), name);
//	}

	
	
	public XMLTreeItem getChild(String elemName)
	{
		for (TreeItem child :  getChildren())
		{	
			org.w3c.dom.Node node = ((XMLTreeItem) child).getValue();
			String name = node.getNodeName();
			if (name.equals(elemName))
				return (XMLTreeItem) child; 
		}
		return null;
	}

	
	
	
	
	private ObjectProperty<TreeItemPredicate<org.w3c.dom.Node>> predicate = new SimpleObjectProperty<TreeItemPredicate<org.w3c.dom.Node>>();
	/**
	 * @return the predicate property
	 */
	public final ObjectProperty<TreeItemPredicate<org.w3c.dom.Node>> predicateProperty() {	 return predicate;	    }
	/**
	 * @return the predicate
	 */
    public final TreeItemPredicate<org.w3c.dom.Node> getPredicate() 					{	 return predicate.get();	    }
    /**
     * Set the predicate
     * @param predicate the predicate
     */
    public final void setPredicate(TreeItemPredicate<org.w3c.dom.Node> p) 				{	 predicate.set(p);	    }

}
