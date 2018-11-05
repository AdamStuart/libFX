package gui;

import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import model.IController;
import model.bio.TableRecord;


public class DraggableTreeTableRow<IRecord> extends TreeTableRow<IRecord> {
	private TreeTableView<IRecord> table;
	private TreeTableRow<IRecord> thisRow;
	private IController controller;
	private TableRecord tableRecord;
	public DraggableTreeTableRow(TreeTableView<IRecord> inTable, DataFormat mimeType, IController cntrlr, TableRecord rec)
	{
		table = inTable;
		controller = cntrlr;
		tableRecord = rec;
		
		setOnDragDetected(event -> {
        if (! isEmpty()) {
            Integer index = getIndex();
            IRecord r = null; //table.getChuldren().get(index);
            String id = "";
            if (r != null)
            	id = r.toString();
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            db.setDragView(snapshot(null, null));
            ClipboardContent cc = new ClipboardContent();
            cc.put(mimeType, index);
            cc.put(DataFormat.PLAIN_TEXT, id);
            db.setContent(cc);
            event.consume();
            thisRow = (TreeTableRow<IRecord>) this;
        }
    });

    setOnDragEntered(event -> {
        Dragboard db = event.getDragboard();
        if (db.hasContent(mimeType)) {
            
//             if (thisRow != null) 
//           	   thisRow.setOpacity(0.3);
             Object obj = db.getContent(mimeType);
        	event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        }
    });

    setOnDragExited(event -> {
        if (event.getGestureSource() != thisRow &&
                event.getDragboard().hasString()) {
//           if (thisRow != null) 
//        	   thisRow.setOpacity(1);
           thisRow = null;
        }
    });

    setOnDragOver(event -> {
        Dragboard db = event.getDragboard();
//        if (thisRow != null) 
//        	   thisRow.setOpacity(0.3);
       if (db.hasContent(mimeType)) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
        }
    });

      setOnMouseClicked(event -> {
          if (controller != null) 
          {
              int idx = getIndex();
              String colName = xToColumnId(event.getX());
              if (event.getClickCount() == 2)
              	controller.getInfo(mimeType, "" + idx, colName, event);	//r.getId()
              event.consume();
         }
    });

    setOnDragDropped(event -> {
        Dragboard db = event.getDragboard();
        if (db.hasContent(mimeType)) {
            Object obj = db.getContent(mimeType);
//            if (thisRow != null) 
//	         	   thisRow.setOpacity(1);
            if (obj instanceof Integer)
            {
	            int draggedIndex = (Integer) obj;
	            reorderRecords(draggedIndex, getIndex());
	            event.setDropCompleted(true);
	            event.consume();
	            thisRow = null;
	            controller.resetTableColumns();
            }
         }
    });

   
	}
	private void reorderRecords(int draggedIndex, int index) {
//        if (index >= table.getgetTreeItems().size())
//        	index = table.getItems().size()-1;
//        IRecord draggedNode = table.getItems().remove(draggedIndex);
//        int  dropIndex = index; // (isEmpty()) ? table.getItems().size() : getIndex();
//        if (index > draggedIndex) dropIndex--;
//        table.getItems().add(dropIndex, draggedNode);
//        table.getSelectionModel().clearAndSelect(dropIndex);
//        controller.reorderColumns(draggedIndex,  index);
	}

	public  String xToColumnId(double x)
    {
    	for (TreeTableColumn col : table.getColumns())
    	{
    		double width = col.getWidth();
    		if (width > x) return col.getId();
    		x -= width;
    	}
     	return "";
    }

}
