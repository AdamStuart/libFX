package util;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 * original by hansolo  Date: 18.06.13
 * 
 * most of these break the image into tiles and make multiple time lines to animate tiles independently
 * Simpler node transitions are in animation.AnimationUtils
 */
public class UtilTransitions  {

	public static enum Transition {
	    VERTICAL_AROUND_X,  		VERTICAL_AROUND_Y,	    	VERTICAL_AROUND_X_AND_Y,
	    HORIZONTAL_AROUND_X,	    HORIZONTAL_AROUND_Y,	    HORIZONTAL_AROUND_X_AND_Y,
	    RECTANGULAR_AROUND_X,	    RECTANGULAR_AROUND_Y,	    RECTANGULAR_AROUND_X_AND_Y,
	    DISOLVING_BLOCKS,	    	CUBE,	    				PAPER_FOLD,
	    FLIP_HORIZONTAL,	    	FLIP_VERTICAL
	}
     //---------------------------------------------------------------------
    private Image A, B;
    private Duration duration = Duration.millis(1000);
    private int  DELAY  = 100;
    Interpolator interpolator;
    private int               noOfTilesX, noOfTilesY;
    private double            stepSizeX, stepSizeY;
    private Interpolator      spring;
    private static Interpolator  spline = Interpolator.SPLINE(0.7, 0, 0.3, 1);
    private static Interpolator easeBoth = Interpolator.EASE_BOTH;
    private List<ImageView>   imageViewsFront, imageViewsBack;
    private List<Rectangle2D> viewPorts;
    private List<Timeline>    timelines;
    private List<StackPane>   tiles;
    private boolean           playing;
    int nTiles = 1;
    double width = 1, height = 1;

    public List<StackPane> getTiles()	{ return tiles;	}
    // ******************** Initialization ************************************
    public UtilTransitions(Image front, Image back) {
        A         			= front; 
        B          			= back; 

        noOfTilesX         = 8;
        noOfTilesY         = 6;
        width = A.getWidth();
        height = A.getHeight();
        stepSizeX          = width / noOfTilesX;
        stepSizeY          = B.getHeight() / noOfTilesY;
        spring             = new SpringInterpolator(1.0, 0.1, 1.5, 0.0, false);
        
        nTiles = noOfTilesX * noOfTilesY;
        imageViewsFront    = new ArrayList<>(nTiles);
        imageViewsBack     = new ArrayList<>(nTiles);
        viewPorts          = new ArrayList<>(nTiles);
        timelines          = new ArrayList<>(nTiles);
        tiles              = new ArrayList<>(nTiles);
        playing            = false;
        // init the lists
        int count = 0;
        for (int y = 0 ; y < noOfTilesY ; y++) {
            for (int x = 0 ; x < noOfTilesX ; x++) {
                imageViewsFront.add(new ImageView());
                imageViewsBack.add(new ImageView());
                timelines.add(new Timeline());
                tiles.add(new StackPane(imageViewsBack.get(count), imageViewsFront.get(count)));
                count++;
            }
        }
        interpolator   =  spline; 
    }
    // ******************** Methods *******************************************
    public void play(int i)   
    {
    	if (i < 0 || i >= Transition.values().length)    		i = 0;
//    	play(Transition.values()[i]);
    	play(Transition.values()[i]);			
    }
    
     public void play(Transition currentTransition) 
    {
        if (playing) return;
        if (A == null) return;
        if (B == null) return;			// BUG????
        playing = true;
        boolean backToFront = A.equals(imageViewsFront.get(0).getImage());
        if (backToFront)        {  	Image temp = A; 	A = B; 	B = temp;   }

        switch(currentTransition) 
        {
        	case VERTICAL_AROUND_X:    interpolator =  easeBoth; 	rotateVerticalTilesAroundX();       break;
            case VERTICAL_AROUND_Y:    interpolator =  easeBoth; 	rotateVerticalTilesAroundY();      	break;
            case VERTICAL_AROUND_X_AND_Y: interpolator =  spline; 	rotateVerticalTilesAroundXandY();	break;
            case HORIZONTAL_AROUND_X:  	interpolator =  spring;		rotateHorizontalTilesAroundX(); 	break;
            case HORIZONTAL_AROUND_Y:	interpolator =  easeBoth; 	rotateHorizontalTilesAroundY();   	break;
            case HORIZONTAL_AROUND_X_AND_Y:interpolator =  spline; 	rotateHorizontalTilesAroundXandY(); break;
            case RECTANGULAR_AROUND_X:   interpolator =  spring;	rotateRectangularTilesAroundX(); 	break;
            case RECTANGULAR_AROUND_Y:  interpolator =  spring;		rotateRectangularTilesAroundY();  	break;
            case RECTANGULAR_AROUND_X_AND_Y: interpolator =  spline; rotateRectangularTilesAroundXandY(); break;
            case DISOLVING_BLOCKS:  	interpolator =  spline; 	disolvingBlocks();         			break;
            case CUBE:     				interpolator =  spline; 	cube();            					break;
            case FLIP_HORIZONTAL:      	interpolator =  spline; 	flipHorizontal();	  				break;
            case FLIP_VERTICAL:      	interpolator =  spline; 	flipVertical();	  					break;
            case PAPER_FOLD:      		interpolator =  spline; 	paperFolder();	  					break;
            default: 	break;
        }
        
        for (Timeline timeline : timelines)      timeline.play();
        playing = false;
    }

    /**
     * Split the given images into vertical tiles defined by noOfTilesX
     */
    private void splitImageX() {
        viewPorts.clear();
        for (int i = 0 ; i < noOfTilesX; i++) {
            // Create the viewports
            viewPorts.add(new Rectangle2D(i * stepSizeX, 0, stepSizeX, height));

            ImageView iFront = imageViewsFront.get(i);		  // Update the frontside imageviews
            iFront.getTransforms().clear();
            iFront.toFront();
            iFront.setImage(A);
            iFront.setViewport(viewPorts.get(i));

            ImageView iBack = imageViewsFront.get(i);		  // Update the backside imageviews
            iBack.getTransforms().clear();
            iBack.setImage(B);
            iBack.setViewport(viewPorts.get(i));
        }
    }

    /**
     * Split the given images into horizontal tiles defined by noOfTilesY
     */
    private void splitImageY() {
        viewPorts.clear();
        for (int i = 0 ; i < noOfTilesY; i++) {
            // Create the viewports
            viewPorts.add(new Rectangle2D(0, i * stepSizeY, width, stepSizeY));
            ImageView iFront = imageViewsFront.get(i);		  // Update the frontside imageviews
            iFront.getTransforms().clear();		
            iFront.toFront();
            iFront.setImage(A);
            iFront.setViewport(viewPorts.get(i));

            ImageView iBack = imageViewsFront.get(i);		  // Update the backside imageviews
            iBack.getTransforms().clear();
            iBack.setImage(B);
            iBack.setViewport(viewPorts.get(i));
        }
    }

    /**
     * Split the given images into rectangular tiles defined by noOfTilesX and noOfTilesY
     */
    private void splitImageXY() {
        int count = 0;
        viewPorts.clear();
        for (int y = 0 ; y < noOfTilesY; y++) {
            for (int x = 0 ; x < noOfTilesX; x++) {
                // Create the viewports
                viewPorts.add(new Rectangle2D(x * stepSizeX, y * stepSizeY, stepSizeX, stepSizeY));
                ImageView iFront = imageViewsFront.get(count);		  // Update the frontside imageviews
                iFront.getTransforms().clear();
                iFront.toFront();
                iFront.setImage(A);
                iFront.setViewport(viewPorts.get(count));
              
                ImageView iBack = imageViewsFront.get(count);		  // Update the backside imageviews
                iBack.getTransforms().clear();
                iBack.setImage(B);
                iBack.setViewport(viewPorts.get(count));

                count++;
            }
        }
    }

    /**
     * All tiles with an index larger than VISIBLE_UP_TO will be set invisible
     * @param VISIBLE_UP_TO
     */
    private void adjustTilesVisibility(final int VISIBLE_UP_TO) {
        for (int i = 0 ; i < (nTiles) ; i++) {
            tiles.get(i).setVisible(i >= VISIBLE_UP_TO ? false : true);
            tiles.get(i).getTransforms().clear();
            NodeUtil.reset(imageViewsFront.get(i), imageViewsBack.get(i));
        }
    }
    

    // ******************** Methods for vertical tiles ************************
    /**
     * Rotate vertical tiles around x transition between front- and backimage
     */
    private void rotateVerticalTilesAroundX() {
        splitImageX();

        // PreTransform backside imageviews
        for (int i = 0 ; i < noOfTilesX; i++) {
            Rotate rotateX = new Rotate(180, 0, height * 0.5, 0, Rotate.X_AXIS);
            imageViewsBack.get(i).getTransforms().setAll(rotateX);
        }

        for (int i = 0 ; i < noOfTilesX; i++) {
            Rotate rotateX = new Rotate(0, 0, height * 0.5, 0, Rotate.X_AXIS);		 // Create the animations
            checkVisibility(rotateX, i);

            imageViewsFront.get(i).getTransforms().setAll(rotateX);
            imageViewsBack.get(i).getTransforms().addAll(rotateX);

            // Layout the tiles horizontal
            tiles.get(i).setTranslateX(i * stepSizeX);
            tiles.get(i).setTranslateY(0);

            KeyValue kvXBegin = new KeyValue(rotateX.angleProperty(), 0, interpolator);
            KeyValue kvXEnd   = new KeyValue(rotateX.angleProperty(), 180, interpolator);
            KeyFrame kf0      = new KeyFrame(Duration.ZERO, kvXBegin);
            KeyFrame kf1      = new KeyFrame(duration, kvXEnd);

            timelines.get(i).setDelay(Duration.millis(DELAY * i));
            timelines.get(i).getKeyFrames().setAll(kf0, kf1);
        }
        timelines.get(noOfTilesX - 1).setOnFinished(observable -> finish());
        adjustTilesVisibility(noOfTilesX);
    }

    /**
     * Rotate vertical tiles around y transition between front- and backimage
     */
    private void rotateVerticalTilesAroundY() {
        splitImageX();

        // PreTransform backside imageviews
        for (int i = 0 ; i < noOfTilesX; i++) {
            Rotate rotateY = new Rotate(180, stepSizeX * 0.5, 0, 0, Rotate.Y_AXIS);
            imageViewsBack.get(i).getTransforms().setAll(rotateY);
        }

        for (int i = 0 ; i < noOfTilesX; i++) {
            Rotate    rotateY     = new Rotate(0, stepSizeX * 0.5, 0, 0, Rotate.Y_AXIS);		 // Create the animations
            Translate translateX1 = new Translate(0, 0, 0);
            Translate translateX2 = new Translate(0, 0, 0);
            checkVisibility(rotateY, i);

            imageViewsFront.get(i).getTransforms().setAll(rotateY, translateX1);
            imageViewsBack.get(i).getTransforms().addAll(rotateY, translateX2);

            tiles.get(i).setTranslateX(i * stepSizeX);		     // Layout the tiles horizontal
            tiles.get(i).setTranslateY(0);

            KeyValue kvRotateBegin      = new KeyValue(rotateY.angleProperty(), 0, interpolator);
            KeyValue kvRotateEnd        = new KeyValue(rotateY.angleProperty(), 180, interpolator);
            KeyValue kvTranslate1Begin  = new KeyValue(translateX1.xProperty(), 0, interpolator);
            KeyValue kvTranslate2Begin  = new KeyValue(translateX2.xProperty(), 0, interpolator);

            KeyValue kvTranslate1Middle = new KeyValue(translateX1.xProperty(), -stepSizeX * 0.5, interpolator);
            KeyValue kvTranslate2Middle = new KeyValue(translateX2.xProperty(), stepSizeX * 0.5, interpolator);

            KeyValue kvTranslate1End    = new KeyValue(translateX1.xProperty(), 0, interpolator);
            KeyValue kvTranslate2End    = new KeyValue(translateX2.xProperty(), 0, interpolator);

            KeyFrame kf0                = new KeyFrame(Duration.ZERO, kvRotateBegin, kvTranslate1Begin, kvTranslate2Begin);
            KeyFrame kf1                = new KeyFrame(duration.multiply(0.5), kvTranslate1Middle, kvTranslate2Middle);
            KeyFrame kf2                = new KeyFrame(duration, kvRotateEnd, kvTranslate1End, kvTranslate2End);

            timelines.get(i).setDelay(Duration.millis(DELAY * i));
            timelines.get(i).getKeyFrames().setAll(kf0, kf1, kf2);
        }
        timelines.get(noOfTilesX - 1).setOnFinished(observable -> finish());
        adjustTilesVisibility(noOfTilesX);
    }

    /**
     * Rotate vertical tiles around x and y transition between front- and backimage
     */
    private void rotateVerticalTilesAroundXandY() {
        splitImageX();

        for (int i = 0 ; i < noOfTilesX; i++) {
            // Create the rotation objects
            Rotate rotateXFront = new Rotate(0, 0, height * 0.5, 0, Rotate.X_AXIS);
            Rotate rotateYFront = new Rotate(0, stepSizeX * 0.5, 0, 0, Rotate.Y_AXIS);
            Rotate rotateXBack  = new Rotate(180, 0, height * 0.5, 0, Rotate.X_AXIS);
            Rotate rotateYBack  = new Rotate(0, stepSizeX * 0.5, 0, 0, Rotate.Y_AXIS);

            checkVisibility(rotateXFront, rotateYFront, i);      // Add a listener to the rotation objects

            // Add the rotations to the image views
            imageViewsFront.get(i).getTransforms().setAll(rotateXFront, rotateYFront);
            imageViewsBack.get(i).getTransforms().setAll(rotateXBack, rotateYBack);

            // Layout the tiles horizontal
            tiles.get(i).setTranslateX(i * stepSizeX);
            tiles.get(i).setTranslateY(0);

            // Create the key-values and key-frames and add them to the timelines
            KeyValue kvXFrontBegin = new KeyValue(rotateXFront.angleProperty(), 0, interpolator);
            KeyValue kvXFrontEnd   = new KeyValue(rotateXFront.angleProperty(), 180, interpolator);
            KeyValue kvXBackBegin  = new KeyValue(rotateXBack.angleProperty(), -180, interpolator);
            KeyValue kvXBackEnd    = new KeyValue(rotateXBack.angleProperty(), 0, interpolator);

            KeyValue kvYFrontBegin = new KeyValue(rotateYFront.angleProperty(), 0, interpolator);
            KeyValue kvYFrontEnd   = new KeyValue(rotateYFront.angleProperty(), 360, interpolator);
            KeyValue kvYBackBegin  = new KeyValue(rotateYBack.angleProperty(), 360, interpolator);
            KeyValue kvYBackEnd    = new KeyValue(rotateYBack.angleProperty(), 0, interpolator);

            KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvXFrontBegin, kvXBackBegin, kvYFrontBegin, kvYBackBegin);
            KeyFrame kf1 = new KeyFrame(duration, kvXFrontEnd, kvXBackEnd, kvYFrontEnd, kvYBackEnd);

            timelines.get(i).setDelay(Duration.millis(DELAY * i));
            timelines.get(i).getKeyFrames().setAll(kf0, kf1);
        }
        // Listen for the last timeline to finish and switch the images
        timelines.get(noOfTilesX - 1).setOnFinished(observable -> finish());
        adjustTilesVisibility(noOfTilesX);
    }


    // ******************** Methods for horizontal tiles **********************
    /**
     * Rotate horizontal tiles around x transition between front- and backimage
     */
    private void rotateHorizontalTilesAroundX() {
        splitImageY();

        for (int i = 0 ; i < noOfTilesY; i++) {			 // PreTransform backside imageviews
            Rotate rotateX = new Rotate(180, 0, stepSizeY * 0.5, 0, Rotate.X_AXIS);
            imageViewsBack.get(i).getTransforms().setAll(rotateX);
        }

        for (int i = 0 ; i < noOfTilesY; i++) {				    // Create the animations
            Rotate rotateX = new Rotate(0, 0, stepSizeY * 0.5, 0, Rotate.X_AXIS);
            Translate translateY1 = new Translate(0, 0, 0);
            Translate translateY2 = new Translate(0, 0, 0);

            checkVisibility(rotateX, i);
            imageViewsFront.get(i).getTransforms().setAll(rotateX, translateY1);
            imageViewsBack.get(i).getTransforms().addAll(rotateX, translateY2);

            tiles.get(i).setTranslateX(0);					 // Layout the tiles vertical
            tiles.get(i).setTranslateY(i * stepSizeY);

            KeyValue kvXBegin           = new KeyValue(rotateX.angleProperty(), 0, interpolator);
            KeyValue kvXEnd             = new KeyValue(rotateX.angleProperty(), 180, interpolator);
            KeyValue kvTranslate1Begin  = new KeyValue(translateY1.yProperty(), 0, interpolator);
            KeyValue kvTranslate2Begin  = new KeyValue(translateY2.yProperty(), 0, interpolator);

            KeyValue kvTranslate1Middle = new KeyValue(translateY1.yProperty(), -stepSizeY * 0.25, interpolator);
            KeyValue kvTranslate2Middle = new KeyValue(translateY2.yProperty(), stepSizeY * 0.25, interpolator);

            KeyValue kvTranslate1End    = new KeyValue(translateY1.yProperty(), 0, interpolator);
            KeyValue kvTranslate2End    = new KeyValue(translateY2.yProperty(), 0, interpolator);

            KeyFrame kf0      = new KeyFrame(Duration.ZERO, kvXBegin, kvTranslate1Begin, kvTranslate2Begin);
            KeyFrame kf1      = new KeyFrame(duration.multiply(0.25), kvTranslate1Middle, kvTranslate2Middle);
            KeyFrame kf2      = new KeyFrame(duration, kvXEnd, kvTranslate1End, kvTranslate2End);

            timelines.get(i).setDelay(Duration.millis(DELAY * i));
            timelines.get(i).getKeyFrames().setAll(kf0, kf1, kf2);
        }
        timelines.get(noOfTilesX - 1).setOnFinished(observable -> finish());

        adjustTilesVisibility(noOfTilesY);
    }

    /**
     * Rotate horizontal tiles around y transition between front- and backimage
     */
    private void rotateHorizontalTilesAroundY() {
        splitImageY();
        
        for (int i = 0 ; i < noOfTilesY; i++) {				// PreTransform backside imageviews
            Rotate rotateY = new Rotate(180, width * 0.5, 0, 0, Rotate.Y_AXIS);
            imageViewsBack.get(i).getTransforms().setAll(rotateY);
        }

        for (int i = 0 ; i < noOfTilesX; i++) {			    // Create the animations
            Rotate rotateY = new Rotate(0, width * 0.5, 0, 0, Rotate.Y_AXIS);
            checkVisibility(rotateY, i);

            imageViewsFront.get(i).getTransforms().setAll(rotateY);
            imageViewsBack.get(i).getTransforms().addAll(rotateY);

            tiles.get(i).setTranslateX(0);					   // Layout the tiles vertical
            tiles.get(i).setTranslateY(i * stepSizeY);

            KeyValue kvXBegin = new KeyValue(rotateY.angleProperty(), 0, interpolator);
            KeyValue kvXEnd   = new KeyValue(rotateY.angleProperty(), 180, interpolator);

            KeyFrame kf0      = new KeyFrame(Duration.ZERO, kvXBegin);
            KeyFrame kf1      = new KeyFrame(duration, kvXEnd);

            timelines.get(i).setDelay(Duration.millis(DELAY * i));
            timelines.get(i).getKeyFrames().setAll(kf0, kf1);
        }
        timelines.get(noOfTilesX - 1).setOnFinished(observable -> finish());

        adjustTilesVisibility(noOfTilesY);
    }

    /**
     * Rotate horizontal tiles around x and y transition between front- and backimage
     */
    private void rotateHorizontalTilesAroundXandY() {
        splitImageY();

        for (int i = 0 ; i < noOfTilesY; i++) {		 // Create the rotation objects
            Rotate rotateXFront = new Rotate(0, 0, stepSizeY * 0.5, 0, Rotate.X_AXIS);
            Rotate rotateYFront = new Rotate(0, width * 0.5, 0, 0, Rotate.Y_AXIS);
            Rotate rotateXBack = new Rotate(180, 0, stepSizeY * 0.5, 0, Rotate.X_AXIS);
            Rotate rotateYBack = new Rotate(0, width * 0.5, 0, 0, Rotate.Y_AXIS);
         
            checkVisibility(rotateXFront, rotateYFront, i);			   // Add a listener to the rotation objects

            imageViewsFront.get(i).getTransforms().setAll(rotateXFront, rotateYFront);   // Add the rotations to the image views
            imageViewsBack.get(i).getTransforms().setAll(rotateXBack, rotateYBack);

            tiles.get(i).setTranslateX(0);			    // Layout the tiles vertical
            tiles.get(i).setTranslateY(i * stepSizeY);

            // Create the key-values and key-frames and add them to the timelines
            KeyValue kvXFrontBegin = new KeyValue(rotateXFront.angleProperty(), 0, interpolator);
            KeyValue kvXFrontEnd   = new KeyValue(rotateXFront.angleProperty(), 180, interpolator);
            KeyValue kvXBackBegin  = new KeyValue(rotateXBack.angleProperty(), -180, interpolator);
            KeyValue kvXBackEnd    = new KeyValue(rotateXBack.angleProperty(), 0, interpolator);

            KeyValue kvYFrontBegin = new KeyValue(rotateYFront.angleProperty(), 0, interpolator);
            KeyValue kvYFrontEnd   = new KeyValue(rotateYFront.angleProperty(), 360, interpolator);
            KeyValue kvYBackBegin  = new KeyValue(rotateYBack.angleProperty(), 360, interpolator);
            KeyValue kvYBackEnd    = new KeyValue(rotateYBack.angleProperty(), 0, interpolator);

            KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvXFrontBegin, kvXBackBegin, kvYFrontBegin, kvYBackBegin);
            KeyFrame kf1 = new KeyFrame(duration, kvXFrontEnd, kvXBackEnd, kvYFrontEnd, kvYBackEnd);

            timelines.get(i).setDelay(Duration.millis(DELAY * i));
            timelines.get(i).getKeyFrames().setAll(kf0, kf1);
        }
        // Listen for the last timeline to finish and switch the images
        timelines.get(noOfTilesX - 1).setOnFinished(observable -> finish());

        adjustTilesVisibility(noOfTilesY);
    }


    // ******************** Methods for rectangular tiles *********************
    /**
     * Rotating tiles around x transition between front- and backimage
     */
    private void rotateRectangularTilesAroundX() 
    {
        splitImageXY();

        // PreTransform backside imageviews
        int count = 0;
        for (int y = 0 ; y < noOfTilesY ; y++) {
            for (int x = 0 ; x < noOfTilesX ; x++) {
                Rotate rotateX = new Rotate(180, 0, stepSizeY * 0.5, 0, Rotate.X_AXIS);
                imageViewsBack.get(count).getTransforms().setAll(rotateX);
                count++;
            }
        }

        count = 0;
        for (int y = 0 ; y < noOfTilesY ; y++) {
            for (int x = 0 ; x < noOfTilesX ; x++) {
                // Create the animations
                Rotate rotateX = new Rotate(0, 0, stepSizeY * 0.5, 0, Rotate.X_AXIS);
                checkVisibility(rotateX, count);

                imageViewsFront.get(count).getTransforms().setAll(rotateX);
                imageViewsBack.get(count).getTransforms().addAll(rotateX);

                // Layout the tiles in grid
                tiles.get(count).setTranslateX(x * stepSizeX);
                tiles.get(count).setTranslateY(y * stepSizeY);

                KeyValue kvXBegin = new KeyValue(rotateX.angleProperty(), 0, interpolator);
                KeyValue kvXEnd   = new KeyValue(rotateX.angleProperty(), 180, interpolator);

                KeyFrame kf0      = new KeyFrame(Duration.ZERO, kvXBegin);
                KeyFrame kf1      = new KeyFrame(duration, kvXEnd);

                timelines.get(count).setDelay(Duration.millis(DELAY * (2 * x + y)));
                timelines.get(count).getKeyFrames().setAll(kf0, kf1);

                count++;
            }
        }
        timelines.get((nTiles) - 1).setOnFinished(observable -> finish());

        adjustTilesVisibility(nTiles);
    }

    /**
     * Rotating tiles around y transition between front- and backimage
     */
    private void rotateRectangularTilesAroundY() {
        splitImageXY();

        // PreTransform backside imageviews
        int count = 0;
        for (int y = 0 ; y < noOfTilesY ; y++) {
            for (int x = 0 ; x < noOfTilesX ; x++) {
                Rotate rotateY = new Rotate(180, stepSizeX * 0.5, 0, 0, Rotate.Y_AXIS);
                imageViewsBack.get(count).getTransforms().setAll(rotateY);
                count++;
            }
        }

        count = 0;
        for (int y = 0 ; y < noOfTilesY ; y++) {
            for (int x = 0 ; x < noOfTilesX ; x++) {
                // Create the animations
                Rotate rotateY = new Rotate(0, stepSizeX * 0.5, 0, 0, Rotate.Y_AXIS);

                checkVisibility(rotateY, count);

                imageViewsFront.get(count).getTransforms().setAll(rotateY);
                imageViewsBack.get(count).getTransforms().addAll(rotateY);

                // Layout the tiles in grid
                tiles.get(count).setTranslateX(x * stepSizeX);
                tiles.get(count).setTranslateY(y * stepSizeY);

                KeyValue kvXBegin = new KeyValue(rotateY.angleProperty(), 0, interpolator);
                KeyValue kvXEnd   = new KeyValue(rotateY.angleProperty(), 180, interpolator);

                KeyFrame kf0      = new KeyFrame(Duration.ZERO, kvXBegin);
                KeyFrame kf1      = new KeyFrame(duration, kvXEnd);

                timelines.get(count).setDelay(Duration.millis(DELAY * (x + 2 * y)));
                timelines.get(count).getKeyFrames().setAll(kf0, kf1);

                count++;
            }
        }
        timelines.get((nTiles) - 1).setOnFinished(observable -> finish());

        adjustTilesVisibility(nTiles);
    }

    /**
     * Rotating tiles in x and y transition between front- and backimage
     */
    private void rotateRectangularTilesAroundXandY() {
        splitImageXY();

        int count = 0;
        for (int y = 0 ; y < noOfTilesY ; y++) {
            for (int x = 0 ; x < noOfTilesX; x++) {
                // Create the rotation objects
                Rotate rotateXFront = new Rotate(0, 0, stepSizeY * 0.5, 0, Rotate.X_AXIS);
                Rotate rotateYFront = new Rotate(0, stepSizeX * 0.5, 0, 0, Rotate.Y_AXIS);

                Rotate rotateXBack = new Rotate(180, 0, stepSizeY * 0.5, 0, Rotate.X_AXIS);
                Rotate rotateYBack = new Rotate(0, stepSizeX * 0.5, 0, 0, Rotate.Y_AXIS);

                Translate translateZFront = new Translate();
                Translate translateZBack  = new Translate();
            
                checkVisibility(rotateXFront, rotateYFront, count);    // Add a listener to the rotation objects

                // Add the rotations to the image views
                imageViewsFront.get(count).getTransforms().setAll(rotateXFront, rotateYFront, translateZFront);
                imageViewsBack.get(count).getTransforms().setAll(rotateXBack, rotateYBack, translateZBack);

                tiles.get(count).setTranslateX(x * stepSizeX);		  // Layout the tiles in grid
                tiles.get(count).setTranslateY(y * stepSizeY);

                // Create the key-values and key-frames and add them to the timelines
                KeyValue kvXFrontBegin  = new KeyValue(rotateXFront.angleProperty(), 0, interpolator);
                KeyValue kvXFrontEnd    = new KeyValue(rotateXFront.angleProperty(), 180, interpolator);
                KeyValue kvXBackBegin   = new KeyValue(rotateXBack.angleProperty(), -180, interpolator);
                KeyValue kvXBackEnd     = new KeyValue(rotateXBack.angleProperty(), 0, interpolator);
                KeyValue kvZFrontBegin  = new KeyValue(translateZFront.zProperty(), 0, interpolator);
                KeyValue kvzBackBegin   = new KeyValue(translateZBack.zProperty(), 0, interpolator);

                KeyValue kvZFrontMiddle = new KeyValue(translateZFront.zProperty(), 50, interpolator);
                KeyValue kvZBackMiddle  = new KeyValue(translateZBack.zProperty(), -50, interpolator);

                KeyValue kvYFrontBegin  = new KeyValue(rotateYFront.angleProperty(), 0, interpolator);
                KeyValue kvYFrontEnd    = new KeyValue(rotateYFront.angleProperty(), 360, interpolator);
                KeyValue kvYBackBegin   = new KeyValue(rotateYBack.angleProperty(), 360, interpolator);
                KeyValue kvYBackEnd     = new KeyValue(rotateYBack.angleProperty(), 0, interpolator);
                KeyValue kvZFrontEnd    = new KeyValue(translateZFront.zProperty(), 0, interpolator);
                KeyValue kvZBackEnd     = new KeyValue(translateZBack.zProperty(), 0, interpolator);

                KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvXFrontBegin, kvXBackBegin, kvYFrontBegin, kvYBackBegin, kvZFrontBegin, kvzBackBegin);
                KeyFrame kf1 = new KeyFrame(duration.multiply(0.5), kvZFrontMiddle, kvZBackMiddle);
                KeyFrame kf2 = new KeyFrame(duration, kvXFrontEnd, kvXBackEnd, kvYFrontEnd, kvYBackEnd, kvZFrontEnd, kvZBackEnd);

                timelines.get(count).setDelay(Duration.millis(DELAY * (x + y)));
                timelines.get(count).getKeyFrames().setAll(kf0, kf1, kf2);
                count++;
            }
        }
        // Listen for the last timeline to finish and switch the images
        timelines.get((nTiles) - 1).setOnFinished(observable -> finish());
        adjustTilesVisibility(nTiles);
    }

    // ******************** Other transitions *********************************
    /**
     * Disolving tiles transition
     */
    private void disolvingBlocks() {
        splitImageXY();

        int count = 0;
        for (int y = 0 ; y < noOfTilesY ; y++) {
            for (int x = 0 ; x < noOfTilesX; x++) {
                // Layout the tiles in grid
                tiles.get(count).setTranslateX(x * stepSizeX);
                tiles.get(count).setTranslateY(y * stepSizeY);

                tiles.get(count).getTransforms().clear();
                ImageView img = imageViewsFront.get(count);
                img.getTransforms().clear();
                imageViewsBack.get(count).getTransforms().clear();

                // Create the key-values and key-frames and add them to the timelines
                KeyValue kvFrontOpacityBegin = new KeyValue(img.opacityProperty(), 1, interpolator);
                KeyValue kvFrontOpacityEnd   = new KeyValue(img.opacityProperty(), 0, interpolator);

                KeyValue kvFrontScaleXBegin  = new KeyValue(img.scaleXProperty(), 1, interpolator);
                KeyValue kvFrontScaleXEnd    = new KeyValue(img.scaleXProperty(), 0, interpolator);

                KeyValue kvFrontScaleYBegin  = new KeyValue(img.scaleYProperty(), 1, interpolator);
                KeyValue kvFrontScaleYEnd    = new KeyValue(img.scaleYProperty(), 0, interpolator);

                KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvFrontOpacityBegin, kvFrontScaleXBegin, kvFrontScaleYBegin);
                KeyFrame kf2 = new KeyFrame(duration, kvFrontOpacityEnd, kvFrontScaleXEnd, kvFrontScaleYEnd);

                timelines.get(count).setDelay(Duration.millis(DELAY * (x + y)));
                timelines.get(count).getKeyFrames().setAll(kf0, kf2);

                count++;
            }
        }
        // Listen for the last timeline to finish and switch the images
        timelines.get((nTiles) - 1).setOnFinished(observable -> finish());

        adjustTilesVisibility(nTiles);
    }

    /**
     * Cube transition 
     */
    private void cube() {
        adjustTilesVisibility(1);
        viewPorts.clear();

        final Rectangle2D VIEW_PORT = new Rectangle2D(0, 0, width, height);
        for (int i = 0 ; i < nTiles ; i++) 
        {
            imageViewsFront.get(i).setViewport(VIEW_PORT);
            imageViewsBack.get(i).setViewport(VIEW_PORT);
        }
        ImageView iFront = imageViewsFront.get(0);
        ImageView iBack = imageViewsBack.get(0);
        iFront.setImage(A);
        iBack.setImage(B);

        iFront.setTranslateZ(-0.5 * width);

        iBack.setTranslateX(0.5 * width);
        iBack.setRotationAxis(Rotate.Y_AXIS);
        iBack.setRotate(90);

        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        rotateY.setPivotX(width * 0.5);
        rotateY.setPivotZ(width * 0.5);
        rotateY.angleProperty().addListener((ov, oldAngle, newAngle) -> {
            if (73 < newAngle.intValue())     iBack.toFront();   });

        Translate translateZ = new Translate(0, 0, width * 0.5);
        StackPane tile =  tiles.get(0);
        tile.getTransforms().setAll(rotateY, translateZ);

        KeyValue kvRotateBegin        = new KeyValue(rotateY.angleProperty(), 0, interpolator);
        KeyValue kvRotateEnd          = new KeyValue(rotateY.angleProperty(), 90, interpolator);

        KeyValue kvOpacityFrontBegin  = new KeyValue(iFront.opacityProperty(), 1.0, interpolator);
        KeyValue kvOpacityBackBegin   = new KeyValue(iBack.opacityProperty(), 0.0, interpolator);

        KeyValue kvScaleXBegin        = new KeyValue(tile.scaleXProperty(), 1.0, interpolator);
        KeyValue kvScaleYBegin        = new KeyValue(tile.scaleYProperty(), 1.0, interpolator);

        KeyValue kvScaleXMiddle       = new KeyValue(tile.scaleXProperty(), 0.85, interpolator);
        KeyValue kvScaleYMiddle       = new KeyValue(tile.scaleYProperty(), 0.85, interpolator);

        KeyValue kvOpacityFrontMiddle = new KeyValue(iFront.opacityProperty(), 1.0, interpolator);
        KeyValue kvOpacityBackMiddle  = new KeyValue(iBack.opacityProperty(), 1.0, interpolator);

        KeyValue kvScaleXEnd          = new KeyValue(tile.scaleXProperty(), 1.0, interpolator);
        KeyValue kvScaleYEnd          = new KeyValue(tile.scaleYProperty(), 1.0, interpolator);

        KeyValue kvOpacityFrontEnd    = new KeyValue(iFront.opacityProperty(), 0.0, interpolator);
        KeyValue kvOpacityBackEnd     = new KeyValue(iBack.opacityProperty(), 1.0, interpolator);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvRotateBegin, kvScaleXBegin, kvScaleYBegin, kvOpacityFrontBegin, kvOpacityBackBegin);
        KeyFrame kf1 = new KeyFrame(duration.multiply(0.5), kvScaleXMiddle, kvScaleYMiddle, kvOpacityFrontMiddle, kvOpacityBackMiddle);
        KeyFrame kf2 = new KeyFrame(duration, kvRotateEnd, kvScaleXEnd, kvScaleYEnd, kvOpacityFrontEnd, kvOpacityBackEnd);

        timelines.get(0).setDelay(Duration.millis(DELAY));
        timelines.get(0).getKeyFrames().setAll(kf0, kf1, kf2);

        // Listen for the last timeline to finish and switch the images
        timelines.get(0).setOnFinished(observable -> finish() );
    }

    /**
     * flipHorizontal transition 
     */
   private void flipHorizontal() {
        viewPorts.clear();

        Rotate preRotateX = new Rotate(180, 0, -height * 0.5, 0, Rotate.X_AXIS);    // PreTransform backside imageview
        imageViewsBack.get(0).getTransforms().setAll(preRotateX);

        Rotate rotateX = new Rotate(0, 0, height * 0.5, 0, Rotate.X_AXIS);			 // Create the animations
        checkVisibility(rotateX, 0);

        imageViewsFront.get(0).getTransforms().setAll(rotateX);
        imageViewsBack.get(0).getTransforms().addAll(rotateX);

        KeyValue kvXBegin = new KeyValue(rotateX.angleProperty(), 0, interpolator);
        KeyValue kvXEnd   = new KeyValue(rotateX.angleProperty(), 180, interpolator);
        KeyFrame kf0      = new KeyFrame(Duration.ZERO, kvXBegin);
        KeyFrame kf1      = new KeyFrame(duration, kvXEnd);
        timelines.get(0).getKeyFrames().setAll(kf0, kf1);
        timelines.get(0).setOnFinished(observable -> finish());        
        adjustTilesVisibility(1);
    }
    
   /**
    * flipVertical transition 
    */
  private void flipVertical() {
       viewPorts.clear();
       Rotate preRotateY = new Rotate(180, -width * 0.5, 0, 0, Rotate.Y_AXIS);	 // PreTransform backside imageview
       imageViewsBack.get(0).getTransforms().setAll(preRotateY);
      
       Rotate rotateY = new Rotate(0, width * 0.5, 0, 0, Rotate.Y_AXIS);		 // Create the animations
       checkVisibility(rotateY, 0);

       imageViewsFront.get(0).getTransforms().setAll(rotateY);
       imageViewsBack.get(0).getTransforms().addAll(rotateY);

       KeyValue kvXBegin = new KeyValue(rotateY.angleProperty(), 0, interpolator);
       KeyValue kvXEnd   = new KeyValue(rotateY.angleProperty(), 180, interpolator);

       KeyFrame kf0      = new KeyFrame(Duration.ZERO, kvXBegin);
       KeyFrame kf1      = new KeyFrame(duration, kvXEnd);

       timelines.get(0).getKeyFrames().setAll(kf0, kf1);
       timelines.get(0).setOnFinished(observable -> finish());        
       adjustTilesVisibility(1);
   }

    /**
     * Check which side of the tile is visible when rotating around x and y axis
     * @param ROTATE_X
     * @param ROTATE_Y
     * @param INDEX
     */
    private void checkVisibility(final Rotate ROTATE_X, final Rotate ROTATE_Y, final int INDEX) {
        ROTATE_X.angleProperty().addListener(observable -> {
            int angleX = (int) ROTATE_X.getAngle();
            int angleY = (int) ROTATE_Y.getAngle();
            ImageView iFront = imageViewsFront.get(INDEX);
            ImageView iBack = imageViewsBack.get(INDEX);
            if (angleX > 0 && angleX < 90) {
                if (angleY > 0 && angleY < 90)            	iFront.toFront();
                 else if (angleY > 90 && angleY < 270)    	iBack.toFront();
                 else if (angleY > 270 && angleY < 360)   	iFront.toFront();
                
            } else if (angleX > 90 && angleX < 270) {
                if (angleY > 0 && angleY < 90)            	iBack.toFront();
                 else if (angleY > 90 && angleY < 270)    	iFront.toFront();
                 else if (angleY > 270 && angleY < 360)   	iBack.toFront();
               
            } else {
                if (angleY > 0 && angleY < 90)            	iFront.toFront();
                 else if (angleY > 90 && angleY < 270)    	iBack.toFront();
                 else if (angleY > 270 && angleY < 360)  	iFront.toFront();
                
            }    });
    }
    /**
     * Check which side of the tile is visible when rotating around one axis
     * @param ROTATE
     * @param INDEX
     */
    private void checkVisibility(final Rotate ROTATE, final int INDEX) 
    {
        ROTATE.angleProperty().addListener((ov, oldAngle, newAngle) -> {
            if (newAngle.doubleValue() > 360)             imageViewsFront.get(INDEX).toFront();
             else if (newAngle.doubleValue() > 270)       imageViewsFront.get(INDEX).toFront();
             else if (newAngle.doubleValue() > 180)       imageViewsBack.get(INDEX).toFront();
             else if (newAngle.doubleValue() > 90)        imageViewsBack.get(INDEX).toFront();
        });
    }
    /**  finish, 
     * clear our flag
     * this used to be called bringBackToFront
     */
    private void finish() {        playing = false;    }
    
    //--------------------------------------------------------------
	private DoubleProperty inset;
	private double insetValue = 10;
	private Timeline timelineHide, timelineShow;
	private InnerShadow shadowLeft = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.1), 0, 0, 0, 0);
	private InnerShadow shadowRight = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.1), 0, 0, 0, 0);

	private void paperFolder()
	{
		final Rectangle2D VIEW_PORT = new Rectangle2D(0, 0, width, height);
		for (int i = 0; i < (nTiles); i++)
		{
			ImageView iFront = imageViewsFront.get(i);
			iFront.setViewport(VIEW_PORT);
			iFront.getTransforms().clear();
			iFront.toFront();
			ImageView iBack = imageViewsBack.get(i);
			iBack.setViewport(VIEW_PORT);
			iBack.getTransforms().clear();
		}
		inset = new SimpleDoubleProperty(this, "inset", 10);

		shadowLeft.radiusProperty().bind(inset.multiply(5));
		shadowLeft.offsetXProperty().bind(inset.multiply(1));
		shadowRight.radiusProperty().bind(inset.multiply(5));
		shadowRight.offsetXProperty().bind(inset.multiply(-1));

		stepSizeX = A.getWidth() / noOfTilesX;
		splitImage();
		for (int i=0; i< noOfTilesX; i++)
		{
			ImageView iFront = imageViewsFront.get(i);
			ImageView iBack = imageViewsBack.get(i);
			initTimelineHide(iFront);
			initTimelineShow(iFront);
			iFront.setImage(A);
			iBack.setImage(A);
			iFront.toFront();
		}
	}

	private void splitImage()
	{
		PixelReader pixelReader = A.getPixelReader();
		for (int i = 0; i < noOfTilesX; i++)
		{
			Image tile = new WritableImage(pixelReader, (int) (i * stepSizeX), 0, (int) stepSizeX, (int) A.getHeight());
			ImageView view = imageViewsFront.get(i);

			view.getTransforms().clear(); // Update the imageviews
			view.setImage(tile);
			view.setTranslateX(i * stepSizeX); // Position image views
			view.setCache(true);
			view.setCacheHint(CacheHint.SPEED);

			// Add perspective transforms
			PerspectiveTransform transform = new PerspectiveTransform();
			double layX = view.getLayoutX();
			if (i % 2 == 0)
			{
				transform.setUlx(layX);			transform.setUly(0); // UpperLeft
				transform.setLlx(layX); 		transform.setLly(A.getHeight());// LowerLeft
				transform.setUrx(layX + stepSizeX); transform.uryProperty().bind(inset);// UpperRight
				transform.setLrx(layX + stepSizeX); transform.lryProperty().bind(inset.negate().add(A.getHeight()));// LowerRight

				shadowRight.setInput(transform);
				view.setEffect(shadowRight);
			} else
			{
				transform.setUlx(layX); 			transform.ulyProperty().bind(inset);// UpperLeft
				transform.setLlx(layX); 			transform.llyProperty().bind(inset.negate().add(A.getHeight()));// LowerLeft
				transform.setUrx(layX + stepSizeX);	transform.setUry(0);// UpperRight
				transform.setLrx(layX + stepSizeX); transform.setLry(A.getHeight());	// LowerRight

				shadowLeft.setInput(transform);
				view.setEffect(shadowLeft);
			}
		}
	}

	private void initTimelineHide(ImageView imgPane)
	{
		KeyValue kv0 = new KeyValue(inset, 0);
		KeyValue kv1 = new KeyValue(inset, insetValue);
		KeyValue kv2 = new KeyValue(imgPane.scaleXProperty(), 1);
		KeyValue kv3 = new KeyValue(imgPane.scaleXProperty(), 0);
		KeyValue kv4 = new KeyValue(imgPane.translateXProperty(), - imgPane.getImage().getWidth() * 0.5);

		KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0, kv2);
		KeyFrame kf1 = new KeyFrame(duration, kv1, kv3, kv4);
		timelineHide = new Timeline();
		timelineHide.getKeyFrames().setAll(kf0, kf1);
		timelines.add(timelineHide);
	}

	private void initTimelineShow(ImageView imgPane)
	{
		KeyValue kv0 = new KeyValue(inset, insetValue);
		KeyValue kv1 = new KeyValue(inset, 0);
		KeyValue kv2 = new KeyValue(imgPane.scaleXProperty(), 0);
		KeyValue kv3 = new KeyValue(imgPane.scaleXProperty(), 1);
		KeyValue kv4 = new KeyValue(imgPane.translateXProperty(), 0);

		KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0, kv2);
		KeyFrame kf1 = new KeyFrame(duration, kv1, kv3, kv4);
		timelineShow = new Timeline();
		timelineShow.getKeyFrames().setAll(kf0, kf1);
		timelines.add(timelineShow);
	}
}