package util;

import java.util.StringTokenizer;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class TreeUtil
{
	public static void expandAll(TreeItem branch, boolean open)
	{
		branch.setExpanded(open);
		ObservableList<TreeItem<String> >children = branch.getChildren();
		for (TreeItem<String> child : children)
			expandAll(child, open);
	}
	
	static public void dumpTree(TreeItem<String> t, int indent, StringBuilder buff)
	{
		String pad = "          ".substring(0,indent);
//		String pad = "   ";
		buff.append(pad + t.getValue());
		ObservableList<TreeItem<String> >children = t.getChildren();
		buff.append(pad + "(\n" );
		if (children.size() > 0)
			for (TreeItem<String> child : children)
				dumpTree(child, indent + 1, buff);
		buff.append(pad + ")\n" );
	}
	static public void xmlTree(TreeItem<String> t, int indent, StringBuilder buff)
	{
		String pad = "                ".substring(0,2 * indent);
//		String pad = "   ";
//		buff.append(pad + t.getValue());
		ObservableList<TreeItem<String> >children = t.getChildren();
		if (children.size() == 0)
			buff.append(pad + "<Element name=\"" + t.getValue() + "\" />" );
		else
		{
			buff.append("\n" + pad + "<Element name=\"" + t.getValue() + "\">" );
			for (TreeItem<String> child : children)
				xmlTree(child, indent + 1, buff);
			buff.append(pad + " </Element>\n" );
		}
	}


public static TreeItem<String> readTree(String s)
	{
		StringTokenizer tknize = new StringTokenizer(s);

		int indent  = 0;
		TreeItem<String> root = makeTree("Root");
		String token;
		TreeItem<String> lastChild = null;
		TreeItem<String> parent  = root;
		System.out.println(indent);
		
		token = tknize.nextToken();		assert(token.equals("("));		indent++;
		System.out.println(indent);
		token = tknize.nextToken();		assert(token.equals("Root"));
		while (tknize.hasMoreTokens())
		{
			token = tknize.nextToken();
			if (token.equals("("))
			{
				indent++;
				if (lastChild != null) 
					parent = lastChild;
				System.out.println(indent);
			}
			else if (token.equals(")"))
			{
				indent--;
				if (parent != null)
					parent = parent.getParent();
				System.out.println(indent);
			}
			else
			{
				lastChild = makeTree(token);
				parent.getChildren().add(lastChild);
				System.out.println(indent + " " + lastChild.getValue() );

			}
		}
		assert(indent == 0);
		
		return root;
	}

public static TreeItem<String> makeTree(String s)
{
	TreeItem<String> a = new TreeItem<String>(s);
	a.setExpanded(true);
	return a;
}
	

}
