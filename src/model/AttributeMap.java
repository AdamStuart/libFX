package model;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.w3c.dom.NamedNodeMap;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
	public AttributeMap(StackPane sp)
	{
		this();
		put("x", "" + sp.getLayoutX());
		put("y", "" + + sp.getLayoutY());
		put("width", "" + + sp.getLayoutY());
		put("height", "" + + sp.getLayoutY());
	}
	public AttributeMap(File f, double x, double y)
	{
		this();
		put("file", f.getAbsolutePath());
		put("x", "" + x);
		put("y", "" + y);
	}
	public AttributeMap(String s)
	{
		this();
		String trimmed = s.trim();
		
		if (trimmed.startsWith("<")) 
			parseElement(trimmed);
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
	
	public AttributeMap(String s, boolean asFx)
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
	public void add(NamedNodeMap map)
	{
		for (int i=0; i<map.getLength(); i++)
		{
			org.w3c.dom.Node a = map.item(i);
			if (a != null)
				put(a.getNodeName(), a.getNodeValue());
		}
	}
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
		}
		
	}
	//--------------------------------------------------------------------------------
	void parseElement(String s)
	{
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
	public String getId()				{		return get("id");	}
	public double getDouble(String key)	{		return StringUtil.toDouble(get(key));	}

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
		r.setX(getDouble("x"));
		r.setY(getDouble("y"));
		r.setWidth(getDouble("width"));
		r.setHeight(getDouble("height"));
		return r;
	}
	//-------------------------------------------------------------
	public void putRect(Rectangle r)	
	{
		put("x", "" + r.getX());
		put("y", "" + r.getY());
		put("width", "" + r.getWidth());
		put("height", "" + r.getHeight());
	}
	//-------------------------------------------------------------
	public void putCircle(Circle c)	
	{
		put("centerX", "" + c.getCenterX());
		put("centerY", "" + c.getCenterY());
		put("radius", "" + c.getRadius());
	}
	public Circle getCircle()	
	{
		Circle r = new Circle();
		r.setCenterX(getDouble("x"));
		r.setCenterY(getDouble("y"));
		r.setRadius(getDouble("radius"));
		return r;
	}
	//-------------------------------------------------------------
	public void putFillStroke(Color fill, Color stroke)	
	{
		put("-fx-fill", fill.toString());
		put("-fx-stroke", stroke.toString());
	}
	//-------------------------------------------------------------
	public void putFillStroke(Color fill, Color stroke, double width)	
	{
		put("-fx-fill", fill.toString());
		put("-fx-stroke", stroke.toString());
		put("-fx-stroke-weight", ""  + width);
	}
	//-------------------------------------------------------------
	public void putPaint(String key, Color stroke)	
	{
		put(key, stroke.toString());
	}
	//-------------------------------------------------------------
	public Paint getPaint(String key)	
	{
		return Paint.valueOf(get(key));
	}
	 
	 //-------------------------------------------------------------
	public void putBool(String key, boolean b )	
	{
		put(key, b ? "true" : "false");
	}
	//-------------------------------------------------------------
	public boolean getBool(String key, boolean b )	
	{
		String val = get(key);
		return val != null && val.toLowerCase().equals("true");
	}
	//-------------------------------------------------------------
	public void putAll(String... strs )	
	{
		for (int i=0; i < strs.length-1; i+=2)
			put(strs[i], strs[i+1]);
	}
	//-------------------------------------------------------------
	public Color getColor(String key)
	{
		String s = get(key);
		if (s == null) return Color.RED;
		try
		{
			Color c = Color.web(s);
			if (c != null) 
				return c;
		}
		catch (Exception e) { } 		//e.printStackTrace();
		return Color.RED;
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
	public String makeElementString(String name)
	{
		StringBuilder buff = new StringBuilder("<" + name + " ");
		for (String key : keySet())
			buff.append(key).append("=").append(get(key)).append(", ");
		String trunc =  StringUtil.chopLast2(buff.toString());
		return trunc + " />\n";
			
	}

}
