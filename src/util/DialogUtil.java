package util;

import java.io.File;
import java.util.List;

import icon.FontAwesomeIcons;
import icon.GlyphIcon;
import icon.GlyphsDude;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.DataItem;
import model.DataItemDao.FileFormat;

public class DialogUtil {

	
public void Alert(String s)  {
	System.out.println(s);
}

public static void useGlyph(Button b, FontAwesomeIcons def)
{
	b.setGraphic(	GlyphsDude.createIcon(def, GlyphIcon.DEFAULT_ICON_SIZE));
	b.setText("");
}


public static void useGlyphToo(Button b, FontAwesomeIcons def)
{
	b.setGraphic(	GlyphsDude.createIcon(def, GlyphIcon.DEFAULT_ICON_SIZE));
//	b.setText("");
}

public static File findFileInClassPath(String string, FileFormat fmt)
{
	FileChooser fileChooser = createFileChooser("Find find named: " + string, fmt );
	File file = fileChooser.showOpenDialog(null);
	if (file != null)
		System.out.println("The path of the file is " + file.getAbsolutePath());
	return file;
}

public static void importFromFile(int index, FileFormat fileFormat)
{
	FileChooser fileChooser = createFileChooser("Import a " + fileFormat.name() + " file", fileFormat);
	File file = fileChooser.showOpenDialog(null);
	if (file != null)
		System.out.println("Read the file, as per FileUtil");
}

public static void exportToFile(List<DataItem> dataItems, FileFormat fileFormat)
{
	FileChooser fileChooser = createFileChooser("Export a " + fileFormat + " file", fileFormat);
	fileChooser.setInitialFileName("export." + fileFormat.getFileExtension());
	File file = fileChooser.showSaveDialog(null);
	if (file != null)
		System.out.println("Write the file, as per FileUtil");
}

public static FileChooser createFileChooser(String title, FileFormat fileFormat)
{
	FileChooser fileChooser = new FileChooser();
	fileChooser.setInitialDirectory(File.listRoots()[0]);
	fileChooser.setTitle(title);
	fileChooser.setSelectedExtensionFilter(new ExtensionFilter(fileFormat.name() + " files", "*." + fileFormat.getFileExtension()));
	return fileChooser;
}

}
