package model;

import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeType;

public class OverlaidLineChart extends LineChart<Number,Number>
{
	   public OverlaidLineChart(Axis<Number> xAxis, Axis<Number> yAxis) {
	       super(xAxis, yAxis);
	   }
	   /**
	    * Add vertical value marker. The marker's X value is used to plot a
	    * horizontal line across the plot area, its Y value is ignored.
	    * 
	    * @param marker must not be null.
	    */
	   public void addVerticalValueMarker(Data<Number, Number> marker, Color c, double strokeWid) {
		   addVerticalValueMarker(marker, c, strokeWid, null);
	   }
	   public void addVerticalValueMarker(Data<Number, Number> marker, Color c, double strokeWid, StrokeType strokeType) {
	       Objects.requireNonNull(marker, "the marker must not be null");
//	       if (horizontalMarkers.contains(marker)) return;
	       Line line = new Line();
	
	       ObjectProperty<Number> x = marker.XValueProperty();
	       Number xn = x.getValue();
	       double d = xn.doubleValue();
//	       double xP =  getXAxis().getDisplayPosition(marker.getXValue());
//	       double wid = widthProperty().getValue().doubleValue();
	       NumberAxis xAxis = (NumberAxis) getXAxis();
	       double min = xAxis.getLowerBound();
	       double max = xAxis.getUpperBound();
	       double disp = getXAxis().getDisplayPosition(xn);
	       line.setStartX(d*64.5);
	       line.setEndX(d*64.5);
//	       line.startXProperty().bind(x);
//	       line.endXProperty().bind(x);
	       
	       line.startYProperty().bind(heightProperty());
	       line.setEndY(0);
	       line.setStroke(c);
	       line.setStrokeWidth(strokeWid);
	       if (strokeType != null)
	    	   line.setStrokeType(strokeType);
	       marker.setNode(line );
	       getPlotChildren().add(line);
//	       horizontalMarkers.add(marker);
	   }

	   public void addBellCurveMarker(Data<Number, Number> marker, Color c, double strokeWid) {
	       Objects.requireNonNull(marker, "the marker must not be null");
//	       if (horizontalMarkers.contains(marker)) return;
	       Path path = new Path();
	       ObservableList<PathElement> strokeElements = FXCollections.observableArrayList();
	       ObservableList<PathElement> fillElements;    
	       
	       final Point2D[] dataPoints = new Point2D[strokeElements.size()];
	       for (int i = 0; i < strokeElements.size(); i++) {
	           final PathElement element = strokeElements.get(i);
	           if (element instanceof MoveTo) {
	               final MoveTo move = (MoveTo)element;
	               dataPoints[i] = new Point2D(move.getX(), move.getY());
	           } else if (element instanceof LineTo) {
	               final LineTo line = (LineTo)element;
	               final double x = line.getX(), y = line.getY();
	               dataPoints[i] = new Point2D(x, y);
	           }
	       }
	       
	   }


}
