package model.bio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NodeList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import model.bio.BiopaxRecord;
import model.bio.TableRecord;
import util.FileUtil;

public class ReferenceListRecord extends TableRecord<BiopaxRecord> {

	private SimpleStringProperty db = new SimpleStringProperty();
	public StringProperty  dbProperty()  { return db;}
	public String getDb()  { return db.get();}
	public void setDb(String s)  { db.set(s);}	
	
	public ReferenceListRecord(String inName)
	{		
		super(inName);
		setName(inName);	
	}
	List<BiopaxRecord> referenceList = new ArrayList<BiopaxRecord>();
	public void addReferences(List<BiopaxRecord> list) {		referenceList.addAll(list);	}
	public List<BiopaxRecord> getReferences() 			{		return referenceList;	}

	
	public static ReferenceListRecord readReferenceList(File file)
	{
		org.w3c.dom.Document doc = FileUtil.openXML(file);
		if (doc == null) return null;
		List<BiopaxRecord> list = FXCollections.observableArrayList();
		ReferenceListRecord record = new ReferenceListRecord(file.getName());
		
		NodeList nodes = doc.getElementsByTagName("Biopax");
		int len = nodes.getLength();
		for (int i=0; i<len; i++)
		{
			org.w3c.dom.Node domNode = nodes.item(i);
			int childLen = domNode.getChildNodes().getLength();
			for (int j=0; j<childLen; j++)
			{
				org.w3c.dom.Node gchild = domNode.getChildNodes().item(j);
				String jname = gchild.getNodeName();
				if ("bp:PublicationXref".equals(jname))
					list.add(new BiopaxRecord(gchild));
			}
		}
		record.addReferences(list);
		return record;
	}
}
