package gui;

import java.util.function.Consumer;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;

public class DropUtil
{
	static Background saveBG;
	public static void makeFileDropPane(Region dropRegion, Consumer<DragEvent> dropAction)
	{
		dropRegion.setOnDragEntered(e ->
			{
				saveBG = dropRegion.getBackground();
				dropRegion.setEffect(Effects.innershadow);
				dropRegion.setBackground(Backgrounds.tan);
				e.consume();
			});
			// drops don't work without this line!
		dropRegion.setOnDragOver(e ->	{	e.acceptTransferModes(TransferMode.ANY);  e.consume();	});
			
		dropRegion.setOnDragExited(e ->
			{
				dropRegion.setEffect(null);
				dropRegion.setBackground(saveBG);
				e.consume();
			});
			
		dropRegion.setOnDragDropped(e -> {	e.acceptTransferModes(TransferMode.ANY);
				Dragboard db = e.getDragboard();
//				Set<DataFormat> formats = db.getContentTypes();
//				formats.forEach(a -> System.out.println("getContentTypes " + a.toString()));
				dropRegion.setEffect(null);
				dropRegion.setBackground(saveBG == null ? Backgrounds.white : saveBG);
				if (db.hasFiles())  
					dropAction.accept(e);
			});
		}
	public static void makeDropPane(Region dropRegion, Consumer<DragEvent> dropAction)
	{
		dropRegion.setOnDragEntered(e ->
			{
				saveBG = dropRegion.getBackground();
				dropRegion.setEffect(Effects.innershadow);
				dropRegion.setBackground(Backgrounds.tan);
				e.consume();
			});
			// drops don't work without this line!
		dropRegion.setOnDragOver(e ->	{	e.acceptTransferModes(TransferMode.ANY);  e.consume();	});
			
		dropRegion.setOnDragExited(e ->
			{
				dropRegion.setEffect(null);
				dropRegion.setBackground(saveBG);
				e.consume();
			});
			
		dropRegion.setOnDragDropped(e -> {	e.acceptTransferModes(TransferMode.ANY);
				Dragboard db = e.getDragboard();
//				Set<DataFormat> formats = db.getContentTypes();
//				formats.forEach(a -> System.out.println("getContentTypes " + a.toString()));
				dropRegion.setEffect(null);
				dropRegion.setBackground(saveBG == null ? Backgrounds.white : saveBG);
				dropAction.accept(e);
			});
		}
}
