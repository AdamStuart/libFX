package util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.opencsv.CSVReader;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.stage.FileChooser;
import model.AttributeValue;
import model.CSVTableData;
import model.IntegerDataRow;
import services.SystemInfo;
import xml.XMLTreeItem;

public class FileUtil
{
	static public void openFile(Application app, File f)
	{
		try
		{
			app.getHostServices().showDocument(f.toURI().toURL().toExternalForm());
		}
		catch (Exception e){}
	}

	static public Document openXML(File f)
	{
		StringBuilder buff = new StringBuilder();
		readFileIntoBuffer(f, buff);
		return convertStringToDocument(buff.toString());
	}
	
	static public String openXMLfile(File f)
	{
		Document parseddoc = openXML(f);
		StringBuilder xmlOut = new StringBuilder();
		if (parseddoc != null)
		{
			NodeList nodes = parseddoc.getChildNodes();
			int z = nodes.getLength();
			for (int i=0; i<z; i++) 
				readNode(nodes.item(i), xmlOut, 0);
		}
		return xmlOut.toString();
	}

//	
//	static private Node peek(String[] path, NodeList kids, int idx)
//	{
//		int n1 = kids.getLength();
//		for (int i=0; i<n1; i++)
//		{
//			Node child = kids.item(i);
//			if (path[idx].equals(child.getNodeName()))
//				return peek(path, child.getChildNodes(), idx+1);
//		}
//		return null;
//	}

	
	//-------------------------------------------------------------
	static public XMLTreeItem getXMLtree(File f)	{		return getXMLtree(f, null);	}
	//-------------------------------------------------------------
	static public XMLTreeItem getXMLtree(File f, String[] suppressNames)
	{
		StringBuilder buff = new StringBuilder();
		readFileIntoBuffer(f, buff);
		return getXMLtree(buff.toString(), suppressNames);
	}
	//-------------------------------------------------------------
	static public XMLTreeItem getXMLtree(String rawtext, String[] suppressNames)
	{
		XMLTreeItem root = new XMLTreeItem();
		Document parseddoc = convertStringToDocument(rawtext);
		if (parseddoc != null)
			addKids(root, parseddoc.getChildNodes(), suppressNames);
		return root;
		
	}
	
	static private void addKids(XMLTreeItem parent, NodeList kids, String[] suppressNames)
	{
		int n = kids.getLength();
		for (int i=0; i<n; i++) 
		{
			Node node = kids.item(i);
			if (node == null)  continue;
			String nodeName = node.getNodeName();
			String text = node.getTextContent();
			if (nodeName == null || nodeName.startsWith("#"))  continue;
		
//			System.out.println("adding: " + nodeName);
			XMLTreeItem kid = new XMLTreeItem();
			kid.setValue(node);
			
			if (suppressNames != null)
			{
				boolean exp = true;
				for (String s : suppressNames)
					if (s.equals(nodeName)) exp = false;
				kid.setExpanded(exp);
			}
			parent.getChildren().add(kid);
			addKids(kid,node.getChildNodes(), suppressNames);
		}
	}
	//-------------------------------------------------------------
	
	
	static public void findkeys(Node node, ObservableList<AttributeValue> list)
	{
		String type = node.getNodeName();
		if ("key".equals(type))
		{
			list.add(new AttributeValue());
		}
		NodeList kids = node.getChildNodes();			// recurse
		int sie = kids.getLength();
		for (int i=0; i<sie; i++)
			findkeys(kids.item(i), list);
	}
		
	//TODO ASSUMES INTS NOW
	static public CSVTableData openCSVfile(String absPath, TableView<ObservableList<StringProperty>> table)
	{
		if (absPath == null) return null;				// || table == null
		CSVTableData output = new CSVTableData(absPath.substring(absPath.lastIndexOf(File.pathSeparator)));
		try
		{
			String[] row = null;
			CSVReader csvReader = new CSVReader(new FileReader(absPath));
			List<String[]> content = csvReader.readAll();
			ObservableList<StringProperty> props = FXCollections.observableArrayList();
			csvReader.close();
			int nCols = -1;
			 
			row = (String[]) content.get(0);
			nCols = row.length;
			System.out.println(nCols + " columns");
			boolean isHeader = true;
			List<IntegerDataRow> data = output.getData();
			int idx = 0;
			for (String fld : row)
			{
				StringUtil.TYPES type = StringUtil.inferType(fld);
				isHeader &= StringUtil.isString(type) || StringUtil.isEmpty(type);  
				output.getColumnNames().add(fld);
				data.add(new IntegerDataRow(nCols));
				System.out.println("Column Name: " + fld);
			    if (table != null) table.getColumns().add(TableUtil.createColumn(idx++, fld));
			}
			output.setTypes(StringUtil.inferTypes((isHeader) ? row : (String[]) content.get(1)));
			
			ObservableList<ObservableList<StringProperty>> list = FXCollections.observableArrayList();
			for (Object object : content)
			{
				row = (String[]) object;
				if (isHeader) { isHeader = false; continue;  }
				if (row.length != nCols) throw new NumberFormatException();
				ObservableList<StringProperty> colData = FXCollections.observableArrayList();
				for (String s : row)
					colData.add(new SimpleStringProperty(s));
				list.add(colData);

//				for (int i=0; i<nCols; i++)
//				{
//					data.get(i).add(row[i]);
//					System.out.print(row[i] + "\t");
//				}
//				System.out.println();
			}
	        if (table != null) table.setItems(list);
		} 
		catch (NumberFormatException e)	
		{		 
			System.err.print("Wrong number of columns in row"); 
			e.printStackTrace();	
			return null;
		}
		catch (Exception e)				{			e.printStackTrace();	return null;	}
		return output;
	}
	
		//--------------------------------------------------------------------------------------
	static public File compress(File fileSrc)
	{
       String source = fileSrc.getPath();
       String targetZipPath = source + ".gz";
      int bufferSize = 10000;
         
       try {
                    //Compress file
           FileOutputStream fileOutputStream =  new FileOutputStream(targetZipPath);
           GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(fileOutputStream);
                    
           byte[] buffer = new byte[bufferSize];
           try (FileInputStream fileInputStream = new FileInputStream(source)) 
           {
              int numberOfByte;
              while((numberOfByte = fileInputStream.read(buffer, 0, bufferSize)) != -1)
                    gZIPOutputStream.write(buffer, 0, numberOfByte);
            }
            gZIPOutputStream.close();
            } 
       catch (IOException ex) {      Logger.getLogger(null).log(Level.SEVERE, "compress failed");    }
       return new File(targetZipPath);

   }
// this creates a sibling folder with the same name as the zip file (without .zip)
	
	static public String decompress(File fileSrc)
	{
		String source = fileSrc.getPath();
		File destFolder = new File(StringUtil.chopExtension(fileSrc.getAbsolutePath()));
		if (destFolder.exists()) return ("Target Folder Exists");
		StringBuilder entryList = new StringBuilder();
		if (destFolder.mkdirs())
		{
			String targetUnZipPath = destFolder.getAbsolutePath();
			int bufferSize = 10000;
			try
			{
				FileInputStream fileInputStream = new FileInputStream(source);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);

				ZipEntry zipEntry;
				while ((zipEntry = zipInputStream.getNextEntry()) != null)
				{
					try
					{
						byte[] buffer = new byte[(int) zipEntry.getSize()];
						String unzippedFile = targetUnZipPath + "/" + zipEntry.getName();
						FileOutputStream fileOutputStream = new FileOutputStream(unzippedFile);
						int size;
						while ((size = zipInputStream.read(buffer)) != -1)
							fileOutputStream.write(buffer, 0, size);
						fileOutputStream.flush();
						fileOutputStream.close();
						entryList.append(unzippedFile + "\n");
					} catch (Exception ex)	
					{
						ex.printStackTrace();		// TODO FileNotFoundExceptions seen here
					}
				}
				zipInputStream.close();
			} catch (IOException ex)
			{
				Logger.getLogger(null).log(Level.SEVERE, null, "decompress failed");
			}
		}
		else
			System.out.println("Failed to create directory.  Nothing unzipped.");
		return entryList.toString();
	}
//--------------------------------------------------------------------------------------
	
	static String LINE_DELIM = "\n";
	
	static private void readNode(Node node, StringBuilder buff, int indent)
	{
//		Element elem = node.getOwnerDocument().getDocumentElement();  //ChildNodes();
		String text = node.getTextContent();
		String type = node.getNodeName();
		if ("Keyword".equals(type)) return;
		if ("#text".equals(type)) return;
		buff.append(doublespaced(indent)).append(type);
		if (text != null && text.length() > 0)
			buff.append(": ").append(text);
		buff.append("\n");
		
		NodeList kids = node.getChildNodes();
		int sie = kids.getLength();
		for (int i=0; i<sie; i++)
			readNode(kids.item(i), buff, indent+1);
	}
	
	static public String doublespaced(int indent)
	{
		if (indent > 12)  return "                                   ";
		return "                                   ".substring(0, 2*indent);
	}

	public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try 
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }
	static public boolean hasXMLFiles(Dragboard db)
	{
//		Assert.assertNotNull(db);
		return db.getFiles().stream().filter(f -> isXML(f)).count() > 0;
	}

	public static boolean isImageFile(File f){		return isPNG(f) || isJPEG(f);	}
	public static boolean isTextFile(File f){		return isTXT(f) || isCSV(f);	}
	
	static public boolean isXML(File f)		{ 		return fileEndsWith(f,".xml", ".wsp");	}
	static public boolean isJPEG(File f)	{ 		return fileEndsWith(f,".jpg", ".jpeg");	}
	static public boolean isPNG(File f)		{ 		return fileEndsWith(f,".png");	}
	static public boolean isTXT(File f)		{ 		return fileEndsWith(f,".txt");	}
	static public boolean isCSV(File f)		{ 		return fileEndsWith(f,".csv");	}
	static public boolean isCSS(File f)		{ 		return fileEndsWith(f,".css");	}
	static public boolean isWebloc(File f)	{ 		return fileEndsWith(f,".webloc", ".url");	}
	static public boolean isFCS(File f)		{ 		return fileEndsWith(f,".fcs", ".lmd");	}
	static public boolean isZip(File f)		{ 		return fileEndsWith(f,".zip", ".gz", ".acs");	}
	static public boolean isSVG(File f)		{ 		return fileEndsWith(f,".svg");	}

	static public FileChooser.ExtensionFilter zipFilter = new FileChooser.ExtensionFilter("Zip files (*.zip)", "*.zip", "*.gz", "*.acs");
	static public FileChooser.ExtensionFilter fcsFilter = new FileChooser.ExtensionFilter("FCS files", "*.fcs", "*.lmd");

	
	
	static private boolean fileEndsWith(File f, String ...extensions)
	{
		String path = f.getAbsolutePath().toLowerCase();  
		for (String ext : extensions)
			if (path.endsWith(ext.toLowerCase())) return true;
		return false;
	}
	
	
	static public String readFiles(Dragboard db)
	{
		StringBuilder buff = new StringBuilder();
		db.getFiles().forEach(f ->readFile(f, buff));
		return buff.toString();
	}

	static public void readFile(File inFile, StringBuilder buff)
	{
		if (inFile.isDirectory())
		{
			for (File f : inFile.listFiles())
				readFile(f, buff);
		} else
			readFileIntoBuffer(inFile, buff);
	}

	
	static public void readFileIntoBuffer(File f, StringBuilder buff)
	{
		readFileIntoBuffer(f.getAbsolutePath(), buff);
	}
	
	
	static public String readFileIntoString(String absolutePath)
	{
		StringBuilder buffer = new StringBuilder();
		readFileIntoBuffer(absolutePath, buffer);
		return buffer.toString();
	}
	static public void readFileIntoBuffer(String absolutePath, StringBuilder buff)
	{
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(absolutePath)));
            String line = null;
            String nl = System.getProperty("line.separator", "\n");

            while((line = br.readLine()) != null)
            	buff.append(line + nl);

        } catch (Exception e) {          System.err.println("Error while reading content from selected file");      } 
        finally
        {
            if(br != null)
                try {   br.close();   } catch (Exception e) {}
        }
	}

	static public List<String> readFileIntoStringList(String absolutePath)
	{
        BufferedReader br = null;
        List<String> strs = new ArrayList<String>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(absolutePath)));
            String line = null;
            String nl = System.getProperty("line.separator", "\n");

            while((line = br.readLine()) != null)
            	strs.add(line);

        } catch (Exception e) {          System.err.println("Error while reading content from selected file");      } 
        finally
        {
            if(br != null)
                try {   br.close();   } catch (Exception e) {}
        }
     return strs;
	}

	public static File createFileFromByteArray(final byte[] data, final File target) throws IOException
	{
		final File parent = target.getParentFile();
		if (parent != null && !parent.exists())
		{
			if (!parent.mkdirs())
			{
				throw new IOException("Unable to create directory '" + parent.getPath());
			}
		}
		final OutputStream fos = new FileOutputStream(target);
		try
		{
			fos.write(data);
		}
		finally
		{
			fos.close();
		}
		return target;
	}

	public static byte[] readAsBytes(final File file) throws IOException
	{
		final FileInputStream fis = new FileInputStream(file);
		try
		{
			return inputStreamToByteArray(fis, 8192);
		}
		finally
		{
			fis.close();
		}
	}

	public static byte[] inputStreamToByteArray(final InputStream is, final int bufferSize) throws IOException
	{
		final byte[] buffer = new byte[bufferSize];
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		int length = is.read(buffer);
		while (length > 0)
		{
			os.write(buffer, 0, length);
			length = is.read(buffer);
		}
		return os.toByteArray();
	}

	public static String getHTMLDescription(File f)
	{
		String name = f.getName();
		String path = f.getParent();
		String x = f.isDirectory() ? "DIR/" : "FILE";
		String y = f.lastModified() + "";
		String len = f.length() + " bytes";
		
		return ("<html> "  + name + " <p> " + path + " <p> " + len +  " <p> " + x + " <p> " + y +" <p> </html>");
	}

	public static String getTextDescription(File f)
	{
		String name = f.getName();
		String path = f.getParent();
		String x = f.isDirectory() ? "DIR/" : "FILE";
		String y = f.lastModified() + "";
		String len = f.length() + " bytes";
		
		return (name + "\n" + path + "\n" + len +  "\n" + x + "\n" + y + "\n");
	}

	//--------------------------------------------------------------------------------
	// keep a cache of the images for extensions we've seen
	static HashMap<String, Image> mapOfFileExtToSmallIcon = new HashMap<String, Image>();

	public static String getFileExt(String fname)
	{
		String ext = ".";
		int p = fname.lastIndexOf('.');
		if (p >= 0) ext = fname.substring(p);
		return ext.toLowerCase();
	}

	//--------------------------------------------------------------------------------
	public static javax.swing.Icon getJSwingIconFromFileSystem(File file)
	{

		javax.swing.Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
		if (SystemInfo.isMacOSX())
		{
			final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
			icon = fc.getUI().getFileView(fc).getIcon(file);
		}
		return icon;
	}

	public static Image getFileIcon(String fname)
	{
		final String ext = getFileExt(fname);

		Image fileIcon = mapOfFileExtToSmallIcon.get(ext);
		if (fileIcon == null)
		{
			javax.swing.Icon jswingIcon = null;
			File file = new File(fname);
			if (file.exists()) jswingIcon = getJSwingIconFromFileSystem(file);
			else
			{
				File tempFile = null;
				try
				{
					tempFile = File.createTempFile("icon", ext);
					jswingIcon = getJSwingIconFromFileSystem(tempFile);
				} catch (IOException ignored)
				{} // Cannot create temporary file.
				finally
				{
					if (tempFile != null) tempFile.delete();
				}
			}
			if (jswingIcon != null)
			{
				fileIcon = jswingIconToImage(jswingIcon);
				mapOfFileExtToSmallIcon.put(ext, fileIcon);
			}
		}
		return fileIcon;
	}

	public static Image jswingIconToImage(javax.swing.Icon jswingIcon)
	{
		BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
		return SwingFXUtils.toFXImage(bufferedImage, null);
	}

 

}
