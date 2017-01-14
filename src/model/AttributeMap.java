package model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import util.FileUtil;
import util.StringUtil;

public class AttributeMap extends HashMap<String, String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//--------------------------------------------------------------------------------
	public AttributeMap()
	{
	}
	
	public AttributeMap(NamedNodeMap xmlAttributes)
	{
		this();
		for (int i=0; i<xmlAttributes.getLength(); i++)
		{
			org.w3c.dom.Node child = xmlAttributes.item(i);
			put(child.getNodeName(), child.getNodeValue());
		}
	}
	
	public AttributeMap(NamedNodeMap xmlAttributes, String key, String val)
	{
		this(xmlAttributes);
		if (key != null && val != null) put(key,val);
	}
	
	public AttributeMap(StackPane sp)
	{
		this();
		put("Width", "" + sp.getLayoutBounds().getWidth());
		put("Height", "" + sp.getLayoutBounds().getHeight());
		put("X", "" + sp.getLayoutX());
		put("Y", "" + + sp.getLayoutY());
		put("CenterX", "" + sp.getLayoutX() +  sp.getLayoutBounds().getWidth() /2);
		put("CenterY", "" + + sp.getLayoutY() +  sp.getLayoutBounds().getHeight() /2);
	}
	
	public AttributeMap(File f, double x, double y)
	{
		this();
		put("file", f.getAbsolutePath());
		put("X", "" + x);
		put("Y", "" + y);
	}
	public AttributeMap(String s, String val)
	{
		this();
		put(s, val);
	}	
	public AttributeMap(String s)
	{
		this();
		String trimmed = s.trim();
		
		if (trimmed.startsWith("<")) 
		{
			try
			{
				parseElement(trimmed);
			}
			catch (Exception e)	{}
		}
		else if (trimmed.startsWith("["))
		{
			String insideBrackets = trimmed.substring(1,s.length()-1);
			String[] attributes =  insideBrackets.split(",");
			for (String attribute : attributes)
			{
				String[] flds = attribute.split("=");
				if (flds.length < 2) continue;
				put(flds[0].trim(), flds[1].trim());
			}
		}
		else
		{
			String[] lines = trimmed.split(";");
			for (String line : lines)
			{
				String[] flds = line.split(":");
				if (flds.length > 1)
					put(flds[0].trim(), flds[1].trim());
			}
		}
	}
	public AttributeMap(AttributeMap orig)
	{
		this();
		addAll(orig);
	}	
	
	public AttributeMap(String s, boolean asFx)		// second arg is just to disambiguate
	{
		this();
		s = s.replaceAll(";", "\n");
		Scanner scan = new Scanner(s);
//		scan.useDelimiter(";");
		while (scan.hasNextLine())
		{
			String line = scan.nextLine().trim();
			if (line.endsWith(";"))
				line = line.substring(0,line.length()-1);
			
			String[] flds =  line.split(":");
			if (flds.length < 2) continue;
			put(flds[0].trim(), flds[1].trim());
		}
        scan.close();
	}
	//--------------------------------------------------------------------------------
	public void addAll(AttributeMap orig)
	{
		for (Map.Entry<String, String> entry : orig.entrySet())
			put(entry.getKey(), entry.getValue());
	}	

	public void add(NamedNodeMap map)
	{
		for (int i=0; i<map.getLength(); i++)
		{
			org.w3c.dom.Node a = map.item(i);
			if (a != null)
				put(a.getNodeName(), a.getNodeValue());
		}
	}
	
	//--------------------------------------------------------------------------------
	void parseElement(String s) throws Exception
	{
		Document doc = FileUtil.convertStringToDocument(s);
		
		String local = s.substring(s.indexOf(" "));		// first token is element name
		local = local.replaceAll(",", "\n");			// convert commas to newlines  (sketchy!)
		
		Scanner scan = new Scanner(s);
		while (scan.hasNextLine())
		{
			String line = scan.nextLine().trim();
			String[] flds =  line.split("=");
			if (flds.length < 2) continue;
			put(flds[0].trim(), flds[1].trim());
		}
        scan.close(); 
	
	}

	//-------------------------------------------------------------
	public String getId()				{		return get("GraphId");	}
	public double getDouble(String key)	{		return StringUtil.toDouble(get(key));	}
	public void putDouble(String key, double d)	{		put(key, String.format("%5.3f", d));	}
	public int getInteger(String key)	{		return StringUtil.toInteger(get(key));	}
	public void putInteger(String key, int i)	{		put(key, "" + i);	}

	public double getDouble(String key, double dflt)	
	{		
		double val = StringUtil.toDouble(get(key));
		if (Double.isNaN(val)) return dflt;
		return val;	
	}
	
	public boolean getBool(String key)
	{
		String s = get(key);
		return s != null && s.toLowerCase().equals("true");
	}
	//-------------------------------------------------------------
	public Rectangle getRect()	
	{
		Rectangle r = new Rectangle();
		r.setWidth(getDouble("Width"));
		r.setHeight(getDouble("Height"));
		r.setX(getDouble("CenterX") - r.getWidth()/2);
		r.setY(getDouble("CenterY") - r.getHeight()/2);
		return r;
	}
	//-------------------------------------------------------------
	public void putRect(Rectangle r)	
	{
		put("ShapeType", "Rectangle");
		put("Width", "" + r.getWidth());
		put("Height", "" + r.getHeight());
		put("CenterX", "" + r.getX()+ r.getWidth()/2);
		put("CenterY", "" + r.getY()+ r.getHeight()/2);
	}
	//-------------------------------------------------------------
	public Circle getCircle()	
	{
		Circle circle = new Circle();
		circle.setCenterX(getDouble("CenterX"));
		circle.setCenterY(getDouble("CenterY"));
		circle.setRadius(getDouble("Radius"));
		return circle;
	}
	public void putCircle(Circle c)	
	{
		put("CenterX", "" + c.getCenterX());
		put("CenterY", "" + c.getCenterY());
		put("Radius", "" + c.getRadius());
	}
	
	public void setTool(String t)	{		put("ShapeType", t);	}
	public String getToolName()		{		return get("ShapeType");	}
	
	//-------------------------------------------------------------
	public void putFillStroke(Color fill, Color stroke)	
	{
		put("-fx-fill", fill.toString());
		put("-fx-stroke", stroke.toString());
	}
	public void putFillStroke(Color fill, Color stroke, double width)	
	{
		put("-fx-fill", fill.toString());
		put("-fx-stroke", stroke.toString());
		put("-fx-stroke-weight", ""  + width);
	}
	//-------------------------------------------------------------
	//-------------------------------------------------------------
	public Paint getPaint(String key)		{	return Paint.valueOf(get(key));	}
	public void putPaint(String key, Color stroke)		{	put(key, stroke.toString());	}
 
	 //-------------------------------------------------------------
	public void putBool(String key, boolean b )		{	put(key, b ? "true" : "false");	}
	public boolean getBool(String key, boolean b )	
	{
		String val = get(key);
		if (val == null) return b;
		return val.toLowerCase().equals("true");
	}
	 //-------------------------------------------------------------
	public void putColor(String key, Color c )		{	put( key, c.toString());	}
	
	public Color getColor(String key )	
	{	
		String val = get( key);
		if (StringUtil.isEmpty(val))	return Color.WHITE;
		try
		{
			return Color.web(val);	
		}
		catch (Exception e)
		{
			return Color.RED;
		}
	}
	//-------------------------------------------------------------
	public void putAll(String... strs )	
	{
		for (int i=0; i < strs.length-1; i+=2)
			put(strs[i], strs[i+1]);
	}
	//-------------------------------------------------------------
	public String getStyleString()
	{
		StringBuilder buff = new StringBuilder();
		for (String key : keySet())
			if (key.startsWith("-fx-"))
				buff.append(key).append(": ").append(get(key)).append("; ");
		return buff.toString();
	}
	//-------------------------------------------------------------
	//  build a string that looks like this:  <name a="1" b="2" >\n
	public String makeElementString(String name)
	{
		StringBuilder buff = new StringBuilder("<" + name + " ");
		buff.append(getAttributes(false));
		return buff.toString() + " />\n";
			
	}
	//-------------------------------------------------------------
	//  build a string that looks like this:  <name a="1" b="2" >\n
	public String makeElementStartString(String name)
	{
		return "<" + name + " " + getAttributes(false) + " >\n";
	}
	
	public String getAttributes(boolean lineBreaks)
	{
		StringBuilder buff = new StringBuilder();
		for (String key : keySet())
		{
			buff.append(key).append("=\"").append(get(key)).append("\" ");
			if (lineBreaks) buff.append("\n");
		}
		return buff.toString();
	}
	
	public String getSafe(String key)
	{
		String val = get(key);
		return val == null ? "" : val.toString();
	}
	//-------------------------------------------------------------
	// GPML
	public void addDataNodeGPML(String gpml)
	{
		String txt = gpml.trim();
		int nodeLen = "<DataNode ".length();
		if (txt.startsWith("<DataNode "))
		{
			String attrs = txt.substring(nodeLen, txt.indexOf(">"));
			addGPML(attrs);
			int graphicsLen = "<Graphics ".length();
			int graphicsStart = txt.indexOf("<Graphics ");
			int graphicsEnd = txt.indexOf("/>", graphicsStart + graphicsLen);
			String graphics =  txt.substring(graphicsLen + graphicsStart, graphicsEnd);
			addGPML(graphics);
			
			int xrefLen = "<Xref ".length();
			int xrefStart = txt.indexOf("<Xref ");
			int xrefEnd = txt.indexOf("/>", xrefStart + xrefLen);
			String xref =  txt.substring(xrefLen + xrefStart, xrefEnd);
			addXref(xref);
		}
	}
//	private void addXref(org.w3c.dom.Node n)	{	add(n.getAttributes());		}
	private void addXref(String attr)			{	addGPML(attr);		}
	public void addGPML(String s)
	{
		String[] tokens = s.split(" ");
		for (String token : tokens)
		{
			int eq = token.indexOf('=');
			String attr = token.substring(0,eq);
			String val = token.substring(eq+2);
			int valEnd = val.indexOf('"');
			val = val.substring(0,valEnd);
			put(attr, val);
		}
	}
	public void addGPMLEdgeInfo(String graphics)
	{
		int eol = graphics.indexOf(">/n");
		addGPML(graphics.substring(10, eol));		// ZOrder and LineThickness
		String nextLine = graphics.substring(eol+2, graphics.indexOf("/n"));
		if (nextLine.startsWith("<Point"))
		{
			String line = nextLine.substring(7, nextLine.indexOf("/n"));		
			//TODO
		}
		
	}

	public void addPoint(double x, double y)
	{
		put("X", "" + x);
		put("Y", "" + y);
		put("Width", "85");
		put("Height", "65");
		
	}

	public int incrementZOrder()
	{
		int z = getInteger("ZOrder");
		putInteger("ZOrder", z + 1);
		return z+1;
	}

	public void setPosition(Point2D pt)
	{
		putDouble("CenterX", pt.getX());
		putDouble("CenterY", pt.getY());
	}
	
	public Point2D getPosition()
	{
		double centerX = getDouble("CenterX");
		double centerY = getDouble("CenterY");
		if (!Double.isNaN(centerX) && !Double.isNaN(centerY))
			return new Point2D(centerX, centerY);
		double x = getDouble("X");
		double y = getDouble("Y");
		if (Double.isNaN(x) || Double.isNaN(y))
			return new Point2D(0,0);
		return new Point2D(x,y);
	}
}
