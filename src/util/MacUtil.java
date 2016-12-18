package util;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import xml.XMLTools;

public class MacUtil
{
	//--------------------------------------------------------------------------------
	// plist is an xml structure holding property values  
	// it is found in a webloc file 
	
	static public String urlFromPlist(File f)
	{
		return f == null ? null : urlFromPlist(f.getAbsoluteFile());
	}
	static public String urlFromPlist(String path)
		{
		StringBuilder buff = new StringBuilder();
		FileUtil.readFileIntoBuffer(path, buff);
		int startDict = buff.indexOf("dict");
		int idx = buff.indexOf("http", startDict);
		if (idx < 0 )	return null;
		int end = buff.indexOf("</string>", idx);
		if (end < 0 )	return null;
		return buff.substring(idx, end);
		
		}
	//--------------------------------------------------------------------------------
	// TODO --  a xml path extractor getChildByPath was input, but no testing has been done.
	static public ObservableList<String> urlsFromPlist(File f)
	{
		ObservableList<String> list = FXCollections.observableArrayList();
		StringBuilder buff = new StringBuilder();
		FileUtil.readFileIntoBuffer(f, buff);
		try
		{
			Document parseddoc = FileUtil.convertStringToDocument(buff.toString());
			Node strList = XMLTools.getChildByPath(parseddoc, "plist", "dict", "key", "string");
			int nodeLen = strList.getChildNodes().getLength();
			for (int l=0; l< nodeLen; l++)
			{	
				Node key = strList.getChildNodes().item(l);
				String val = key.getNodeValue();
				if (val.startsWith("http"))
					list.add(val);
				System.out.println(key.getNodeName() + " = " + key.getNodeValue());
			}
		}
		catch (Exception e ){}
		return list;
	}
//		if (parseddoc != null)
//		{
//			NodeList first = parseddoc.getChildNodes();
//			int n1 = first.getLength();
//			for (int i=0; i<n1; i++) 
//			{
//				Node one = first.item(i);
//				if (one != null && one.getNodeName().equals("plist"))
//				{
//					NodeList second = one.getChildNodes();
//					int n2 = second.getLength();
//					for (int j=0; j<n2; j++)
//					{
//						Node two = second.item(j);
//						if (two != null && two.getNodeName().equals("dict"))
//						{
//							NodeList third = two.getChildNodes();
//							int n3 = third.getLength();
//							for (int k=0; k< n3; k++)
//							{
//								Node three = third.item(k);
//								if (three != null && three.getNodeName().equals("key"))
//								{
//									NodeList fourth = three.getChildNodes();
//									int n4 = fourth.getLength();
//									for (int l=0; l< n4; l++)
//									{	Node key = fourth.item(l);
//										System.out.println("fourth: " +  key.getNodeName() + " = " + key.getNodeValue());
//									}
//								}
//								if (three != null && three.getNodeName().equals("string"))
//								{
//									strList = three.getChildNodes();
//									int n4 = strList.getChildNodes().getLength();
//									for (int l=0; l< n4; l++)
//									{	
//										Node key = strList.getChildNodes().item(l);
//										String val = key.getNodeValue();
//										if (val.startsWith("http"))
//											list.add(val);
//										System.out.println("fourth: " + key.getNodeName() + " = " + key.getNodeValue());
//									}
//								}
//							}
//							
//						}
//					}
//				}
//				
//			}
//		}
//		return list;
//	}

}
