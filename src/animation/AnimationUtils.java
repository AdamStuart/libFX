/*
 * @(#)AnimationUtils.java 5/19/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 * 
 * for tile based animations, see util.TransitionUtil
 */

package animation;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * {@code AnimationUtils} creates built-in Transition using enums defined in the {@link AnimationType}. Most of the
 * animations are from Jasper Potts porting of Animate.css http://daneden.me/animate by Dan Eden, with some from JideFX
 * 
 * 20150925 - Adam Treister edited this version down from 50+ separate classes that were very similar
 * Using sequential and parallel transitions would chop this down further
 */
public class AnimationUtils {

	   static  AnimationUtils instance = new AnimationUtils();		// needed to call the transitions that are subclassed herein
    /**
     * Create default transition according to the animation type.
     *
     * @param node the target node
     * @param type the AnimationType
     * @return the animation for the node.
     */

	   public static Transition createTransition(Node node, AnimationType type) {
        if (node == null || type == null) {
            return null;
        }
        switch (type) {
            // From FxExperience
            case FLASH:                return getFlashTransition(node);
            case BOUNCE:               return getBounceTransition(node);
            case BUBBLE:               return getBubbleTransition(node);
            case SHAKE:                return getShakeTransition(node);
            case TADA:                 return getTadaTransition(node);
            case SWING:                return getSwingTransition(node);
            case WOBBLE:   			   return getWobbleTransition(node);
            case PULSE:                return getPulseTransition(node);
            
            case FLIP:                 return instance.new FlipTransition(node);
            case FLIP_IN_X:            return makeFlipRotation(node, Rotate.Y_AXIS, true); // FlipInXTransition(node);
            case FLIP_OUT_X:           return makeFlipRotation(node, Rotate.Y_AXIS, false); 
            case FLIP_IN_Y:            return makeFlipRotation(node, Rotate.X_AXIS, true);
            case FLIP_OUT_Y:           return makeFlipRotation(node, Rotate.X_AXIS, false); 
            
            case FADE_IN:              return makeFader(node, true,0,0); 
            case FADE_IN_UP:           return makeFader(node, true,0,20); 
            case FADE_IN_DOWN:         return makeFader(node, true,0,-20);  
            case FADE_IN_UP_BIG:       return makeFader(node, true,0,100);
            case FADE_IN_DOWN_BIG:     return makeFader(node, true,0,-100);

            case FADE_OUT:              return makeFader(node, false,0,0); 
            case FADE_OUT_UP:           return makeFader(node, false,0,-20);  
            case FADE_OUT_DOWN:         return makeFader(node, false,0,20);
            case FADE_OUT_UP_BIG:       return makeFader(node, false,0,-100);
            case FADE_OUT_DOWN_BIG:     return makeFader(node, false,0,100);

            
            case FADE_IN_LEFT:          return makeFader(node, true,20,0); 
            case FADE_IN_RIGHT:         return makeFader(node, true,-20,0); 
            case FADE_IN_LEFT_BIG:      return makeFader(node, true,150,0);
            case FADE_IN_RIGHT_BIG:     return makeFader(node, true,-150,0);

            case FADE_OUT_LEFT:         return makeFader(node, false,-20,0); 
            case FADE_OUT_RIGHT:        return makeFader(node, false,20,0); 
            case FADE_OUT_LEFT_BIG:     return makeFader(node, false,-150,0);
            case FADE_OUT_RIGHT_BIG:    return makeFader(node, false,150,0);
            
            case BOUNCE_IN:             return makeBouncer(node, false,20,0);
            case BOUNCE_IN_UP:          return makeBouncer(node, true,  0, 20);	
            case BOUNCE_IN_DOWN:        return makeBouncer(node, true,0, -20); 
            case BOUNCE_IN_LEFT:        return makeBouncer(node, true,-20, 0); 		
            case BOUNCE_IN_RIGHT:       return makeBouncer(node, true, 20, 0);	
            
            case BOUNCE_OUT:            return makeBounceOutTransition(node);
            case BOUNCE_OUT_UP:         return makeBounceOutUpTransition(node, true);
            case BOUNCE_OUT_DOWN:       return makeBounceOutUpTransition(node, false);
            case BOUNCE_OUT_LEFT:       return makeBounceOutLeftTransition(node, true);
            case BOUNCE_OUT_RIGHT:      return makeBounceOutLeftTransition(node, false);
            
            case ROTATE_IN_LEFT:   		return makeRotater(node, true, true, 0, 0);  
            case ROTATE_IN_RIGHT:  		return makeRotater(node, true, false, 0, 0); 
            
            case ROTATE_OUT_LEFT:  		return makeRotater(node, false, false, 0, 0);  
            case ROTATE_OUT_RIGHT: 		return makeRotater(node, false, true, 0, 0);   
            
            case HINGE_OUT:             return instance.new HingeTransition(node);
            case ROLL_IN:               return getRollInTransition(node);
            case ROLL_OUT:              return getRollOutTransition(node);
            case PANIC_SHAKE:           return getPanicShakeTransition(node);
            default:
                return null;
        }
    }
  
	//-----------------------------------------------------------------------------------------------------
    class FlipRotation  extends CachedTimelineTransition 
    {
        private Camera oldCamera;
        
        public FlipRotation(final Node node, Point3D axis, boolean incoming) {
           super( node,  new Timeline(
                    new KeyFrame(Duration.millis(0), 
                        new KeyValue(node.rotateProperty(), incoming ? -90 : 0, WEB_EASE),
                        new KeyValue(node.opacityProperty(), incoming ? 0 : 1, WEB_EASE)  ),
                    new KeyFrame(Duration.millis(400),   new KeyValue(node.rotateProperty(), 10, WEB_EASE)),
                    new KeyFrame(Duration.millis(700),  new KeyValue(node.rotateProperty(), -10, WEB_EASE)),
                    new KeyFrame(Duration.millis(1000), 
                        new KeyValue(node.rotateProperty(), incoming ? 0 : 90, WEB_EASE),
                        new KeyValue(node.opacityProperty(), incoming ? 1 : 0, WEB_EASE))));
           node.setRotationAxis(axis);
      }

       @Override protected void starting() {
           super.starting();
           oldCamera = node.getScene().getCamera();
           node.getScene().setCamera(new PerspectiveCamera());
       }

       @Override protected void stopping() {
           super.stopping();
           node.setRotate(0);
           node.setRotationAxis(Rotate.Z_AXIS);
           node.getScene().setCamera(oldCamera);
       }
    }

 static  Transition makeFlipRotation(final Node node, Point3D axis, boolean incoming)
{
	return instance.new FlipRotation(node, axis, incoming);
}
   
 class HingeTransition extends CachedTimelineTransition {
	    private Rotate rotate;
	    /**
	     * Create new HingeTransition
	     * 
	     * @param node The node to move
	     */
	    public HingeTransition(final Node node) {
	        super(node, null);
	        setCycleDuration(Duration.seconds(2));
	        setDelay(Duration.seconds(0.2));
	    }

	    @Override protected void starting() {
	        super.starting();
	        double endY = node.getScene().getHeight() - node.localToScene(0, 0).getY();
	        rotate = new Rotate(0,0,0);
	        timeline = new Timeline(

	                new KeyFrame(Duration.millis(0),     new KeyValue(rotate.angleProperty(), 0, Interpolator.EASE_BOTH)   ),
	                new KeyFrame(Duration.millis(200),   new KeyValue(rotate.angleProperty(), 80, Interpolator.EASE_BOTH)  ),
	                new KeyFrame(Duration.millis(400),   new KeyValue(rotate.angleProperty(), 60, Interpolator.EASE_BOTH)  ),
	                new KeyFrame(Duration.millis(600),    new KeyValue(rotate.angleProperty(),80, Interpolator.EASE_BOTH)
	                ),
	                new KeyFrame(Duration.millis(800),    
	                    new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH),
	                    new KeyValue(node.translateYProperty(), 0, Interpolator.EASE_BOTH),
	                    new KeyValue(rotate.angleProperty(), 60, Interpolator.EASE_BOTH)
	                ),
	                new KeyFrame(Duration.millis(1000),    
	                    new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH),
	                    new KeyValue(node.translateYProperty(), endY, Interpolator.EASE_BOTH),
	                    new KeyValue(rotate.angleProperty(), 60, Interpolator.EASE_BOTH)
	                )
	            )
	            ;
	        node.getTransforms().add(rotate);
	    }

	    @Override protected void stopping() {
	        super.stopping();
	        node.getTransforms().remove(rotate);
	        node.setTranslateY(0);
	    }
 }//-----------------------------------------------------------------------------------------------------
   private static Transition makeBounceOutTransition(Node node)
	{
    	  Timeline line = new Timeline(
    					  scaleFade(node, 0, 1, 1),
    					    scaleFade(node, 250, 0.95, 1),
    					    scaleFade(node, 500, 1.1, 1),
    					    scaleFade(node, 1000, 0.3, 0));
    		return   new  CachedTimelineTransition(  node, line );

	}
    
    private static Transition makeBounceOutUpTransition(Node node, boolean isUp)
    {
  	  Timeline line = new Timeline(
					  fadeXY(node, 0, 1, 0, 0),
					  fadeXY(node, 200, 1, 0,	isUp ?  10 : -10),
					  fadeXY(node, 1000, 0, 0, 	isUp ? -30 : 30));
		return   new  CachedTimelineTransition(  node, line );
     }
    
    private static Transition makeBounceOutLeftTransition(Node node, boolean isLeft)
    {
  	  Timeline line = new Timeline(
					  fadeXY(node, 0, 1, 0, 0),
					  fadeXY(node, 200, 1, isLeft ? 10 : -10, 0),
					  fadeXY(node, 1000, 0, isLeft ? -30 : -30, 0));
		return   new  CachedTimelineTransition(  node, line );
     }
    
    
	private static Transition makeBouncer(Node node, boolean incoming, double x, double y)
    {
  		Timeline line;		
  		if (incoming)
  			line = new Timeline( 
  				fadeXY(node, 0,0,x, y),  fadeXY(node, 600, 0.9,   -0.1 * x, -0.1 * y), fadeXY(node, 800, 1,  0.1 * x,  0.1 * y), fadeXY(node, 1000, 1, 0, 0)	);
  		else
  			line = new Timeline( 
  	  			fadeXY(node, 0,1,0, 0),  fadeXY(node, 600, 0.9,  0.5 * x, 0.5 * y), fadeXY(node, 800, 0.9,  -0.2 * x,  -0.2 * y), fadeXY(node, 1000, 0, x, y)	);
  	  					
  		return   new  CachedTimelineTransition(  node, line );
   	
    }
    
    private static Transition makeRotater(Node node, boolean fadeIn, boolean clockwise, double x, double y)
   	{
   		double direction = clockwise ? 1 : -1;
   		Timeline line;
   		if (fadeIn)
   			line =  new Timeline( rotatingFade(node, 0, 0,  direction * 90), rotatingFade(node, 1000, 1, 0));
   		 else 
   			 line = new Timeline( rotatingFade(node, 0, 1, 0), rotatingFade(node, 1000, 0, direction * 90));
   		return   new  RotatedCachedTimelineTransition(  node, fadeIn, clockwise, node.getLayoutBounds().getWidth() / 2,
   						node.getLayoutBounds().getHeight() / 2, line);
   	}
  
    private static Transition makeFader(Node node, boolean fadeIn, double x, double y)
  	{
  		double opac0 = 0;
  		double opac1 = 1;
 		double t0 = 0;
 		double t1 = 1000;
  		Timeline t;
  		
  		if (fadeIn)	t = new Timeline( fadeXY(node, t0,opac0,x, y), fadeXY(node, t1, opac1, 0, 0));
  		else 		t = new Timeline( fadeXY(node, t0,opac1, 0, 0), fadeXY(node, t1, opac0,x, y));
  		return   new  CachedTimelineTransition(  node,  t);
  	}

	static  KeyFrame rotatingFade(final Node node, double t, double opac, double a)
    {
    	return new KeyFrame(Duration.millis(t), 
  			new KeyValue(node.opacityProperty(), opac, CachedTimelineTransition.WEB_EASE),
			  new KeyValue(node.rotateProperty(), a, CachedTimelineTransition.WEB_EASE));
    }
	
	static  KeyFrame fadeXY(final Node node, double t, double opac, double transX, double transY)
    {
    	return new KeyFrame(Duration.millis(t), 
  					  new KeyValue(node.opacityProperty(), opac, CachedTimelineTransition.WEB_EASE),
					  new KeyValue(node.translateXProperty(), transX, CachedTimelineTransition.WEB_EASE),
    					new KeyValue(node.translateYProperty(), transY, CachedTimelineTransition.WEB_EASE));
    }

	//----------------------------------------------------------------------------------------
	static CachedTimelineTransition getShakeTransition(Node node)
	{
	  return 	 new CachedTimelineTransition(    node,new Timeline(
					  keyX(node, 0,	  0),
					  keyX(node, 100,	-10),
					  keyX(node, 200,	10),
					  keyX(node, 300,	-10),
					  keyX(node, 400, 	10),
					  keyX(node, 500,	-10),
					  keyX(node, 600, 	10),
					  keyX(node, 700,	-10),
					  keyX(node, 800, 	10),
					  keyX(node, 900,	-10),
					  keyX(node, 1000,  0)
	         ));
	 }
	
	private static Transition getPanicShakeTransition(Node node)
	{
		 double DURATION = 500;
		 return 	 new CachedTimelineTransition(    node,new Timeline(
						  keyX(node, 0,	  0),
						  keyX(node, DURATION* .08,	-10),
						  keyX(node, DURATION * .25,	10),
						  keyX(node, DURATION * .41,	-10),
						  keyX(node, DURATION * .58, 	10),
						  keyX(node, DURATION * .75,	-5),
						  keyX(node, DURATION * .92, 	5),
						  keyX(node, DURATION,  0)
					         ));
					 }
	
	static CachedTimelineTransition getWobbleTransition(Node node)
	{
	  return 	 new CachedTimelineTransition(    node,new Timeline(
	         				key(node, 0,	0,	 0),
	         				key(node, 150, -20, -5),
	         				key(node, 300, 	15,  4),
	         				key(node, 450,  -10, -3),
	         				key(node, 600,  10,  2),
	         				key(node, 750,  -5,  -1),
	         				key(node, 1000,  0,  0)
	         ));
	 }
	 
	static CachedTimelineTransition getSwingTransition(Node node)
	{
	  return 	 new CachedTimelineTransition(    node,new Timeline(
	         				key(node, 0,	 0),
	         				key(node, 200,	15),
	         				key(node, 400, 	 -10),
	         				key(node, 600,  5),
	         				key(node, 800,  -5),
	         				key(node, 1000,   0)
	         ));
	 }
	
	static CachedTimelineTransition getTadaTransition(Node node)
	{
	  return 	 new CachedTimelineTransition(    node,new Timeline(
					  keyScale(node, 0,	 1, 0),
					  keyScale(node, 100,	0.9, -3),
					  keyScale(node, 300,	1.1, 3),
					  keyScale(node, 400, 	1.1, -3),
					  keyScale(node, 500,	1.1, 3),
					  keyScale(node, 600, 	1.1, -3),
					  keyScale(node, 700,	1.1, 3),
					  keyScale(node, 800, 	1.1, -3),
					  keyScale(node, 900,	1.1, 3),
					  keyScale(node, 1000,  1,  0)
	         ));
	 }

	private static Transition getPulseTransition(Node node)
	{
		  return 	 new CachedTimelineTransition(    node,new Timeline(
						  keyScale(node, 0,	 1, 0),
						  keyScale(node, 500,	1.1, 0),
						  keyScale(node, 1000,  1,  0)
		         ));
	}
	
	
	private static Transition getFlashTransition(Node node)
		{
		  return 	 new CachedTimelineTransition(    node,new Timeline(
						  keyOpac(node, 0,	 1),
						  keyOpac(node, 250, 0),
						  keyOpac(node, 500, 1),
						  keyOpac(node, 750, 0),
						  keyOpac(node, 1000,  1)
		         ));
		}
	
	private static Transition getBounceTransition(Node node)
	{
		double h = -0.30 * node.getBoundsInParent().getHeight();
		  return 	 new CachedTimelineTransition(    node,new Timeline(
						  keyY(node, 0,	  0),
						  keyY(node, 200, 0),
						  keyY(node, 400, h),
						  keyY(node, 500,0),
						  keyY(node, 600, h/2),
						  keyY(node, 800, 0),
						  keyY(node, 1000,  0)
		         ));
		 }
	
	private static Transition getBubbleTransition(Node node)
		{
		double DURATION = 400;
		  return 	 new CachedTimelineTransition(    node,new Timeline(
						  keyScaleX(node, 0,	0),
						  keyScaleX(node, DURATION * .60, 0.5),
						  keyScaleX(node, DURATION * .75, 1.2),
						  keyScaleX(node, DURATION * .85, 1.3),
						  keyScaleX(node, DURATION,  1)
		         ));	
		}
	
	private static Transition getRollOutTransition(Node node)
		{
		  return 	 new CachedTimelineTransition(    node,new Timeline(
						  keyOpacXRot(node, 0,  1, 0, 0),
						  keyOpacXRot(node, 1000,	 0, node.getBoundsInLocal().getWidth(), 120)
		         ));
		}

	private static Transition getRollInTransition(Node node)
	{
	  return 	 new CachedTimelineTransition(    node,new Timeline(
					  keyOpacXRot(node, 		0,	 0, -node.getBoundsInLocal().getWidth(), -120),
					  keyOpacXRot(node, 1000,  	1, 	0, 0)
	         ));
	}

//---------------------------------------------------------------------------------------------------
// a whole bunch of helper functions to build KeyFrames

	static KeyFrame scaleFade(Node node, double t, double scale, double opac)
	{
		return new KeyFrame(Duration.millis(t), 
			  			new KeyValue(node.opacityProperty(), opac, CachedTimelineTransition.WEB_EASE),
			  			new KeyValue(node.scaleXProperty(), scale, CachedTimelineTransition.WEB_EASE),
						  new KeyValue(node.scaleYProperty(), scale, CachedTimelineTransition.WEB_EASE));
	}
    
	static KeyFrame keyX(final Node node, double t, double x)
	{
		   return new KeyFrame(Duration.millis(t),    
	                    new KeyValue(node.translateXProperty(), x , CachedTimelineTransition.WEB_EASE) );
	}
	
	static KeyFrame keyScaleX(final Node node, double t, double scaleX)
	{
		   return new KeyFrame(Duration.millis(t),    
	                    new KeyValue(node.scaleXProperty(), scaleX , CachedTimelineTransition.WEB_EASE) );
	}
	
	static KeyFrame keyY(final Node node, double t, double y)
	{
		   return new KeyFrame(Duration.millis(t),    
	                 new KeyValue(node.translateYProperty(), y , CachedTimelineTransition.WEB_EASE) );
	}
	
	static KeyFrame key(final Node node, double t, double x, double a)
	{
		   return new KeyFrame(Duration.millis(t),    
	                    new KeyValue(node.translateXProperty(), x , CachedTimelineTransition.WEB_EASE),
	                    new KeyValue(node.rotateProperty(), a, CachedTimelineTransition.WEB_EASE)  );
	}
	
	static KeyFrame keyOpacXRot(final Node node, double t, double opac, double x, double a)
	{
		   return new KeyFrame(Duration.millis(t),    
			  		 new KeyValue(node.opacityProperty(), opac, CachedTimelineTransition.WEB_EASE),
	                 new KeyValue(node.translateXProperty(), x , CachedTimelineTransition.WEB_EASE),
	                 new KeyValue(node.rotateProperty(), a, CachedTimelineTransition.WEB_EASE)  );
	}
	
	static KeyFrame key(final Node node, double t, double a)
	{
		   return new KeyFrame(Duration.millis(t),    
	                    new KeyValue(node.rotateProperty(), a, CachedTimelineTransition.WEB_EASE));
	}
	
	static KeyFrame keyOpac(final Node node, double t, double opac)
	{
		   return new KeyFrame(Duration.millis(t),    
		  				 new KeyValue(node.opacityProperty(), opac, CachedTimelineTransition.WEB_EASE)  );
	}
	
	static KeyFrame keyScale(final Node node, double t, double scale, double a)
	{
		   return new KeyFrame(Duration.millis(t),    
	            new KeyValue(node.rotateProperty(), a, CachedTimelineTransition.WEB_EASE),
	            new KeyValue(node.scaleXProperty(), scale , CachedTimelineTransition.WEB_EASE),
	            new KeyValue(node.scaleYProperty(), scale , CachedTimelineTransition.WEB_EASE) );
	}
//---------------------------------------------------------------------------------------------------
	class FlipTransition extends CachedTimelineTransition {
	    private final Node node;
	    private boolean first = true;
	    private Camera oldCamera;
	    
    /**
     * Create new FlipTransition
     * 
     * @param node The node to affect
     */
    public FlipTransition(final Node node) {
        super(  node,  new Timeline(
            new KeyFrame(Duration.millis(0), 
                new KeyValue(node.rotateProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(node.translateZProperty(), 0, Interpolator.EASE_OUT) ),
            new KeyFrame(Duration.millis(400), 
                new KeyValue(node.translateZProperty(), -150, Interpolator.EASE_OUT),
                new KeyValue(node.rotateProperty(), -170, Interpolator.EASE_OUT)  ),
            new KeyFrame(Duration.millis(500), 
                new KeyValue(node.translateZProperty(), -150, Interpolator.EASE_IN),
                new KeyValue(node.rotateProperty(), -190, Interpolator.EASE_IN),
                new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_IN),
                new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_IN)  ),
            new KeyFrame(Duration.millis(800), 
                new KeyValue(node.translateZProperty(), 0, Interpolator.EASE_IN),
                new KeyValue(node.rotateProperty(), -360, Interpolator.EASE_IN),
                new KeyValue(node.scaleXProperty(), 0.95, Interpolator.EASE_IN),
                new KeyValue(node.scaleYProperty(), 0.95, Interpolator.EASE_IN)  ),
            new KeyFrame(Duration.millis(1000), 
                new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_IN),
                new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_IN)  )   )   );
        this.node = node;
    }

		@Override protected void interpolate(double d)
		{
			if (first)
			{ // setup
				node.setRotationAxis(Rotate.Y_AXIS);
				oldCamera = node.getScene().getCamera();
				node.getScene().setCamera(new PerspectiveCamera());
				first = false;
			}
			super.interpolate(d);
			if (d == 1)
			{ // restore
				first = true;
				node.setRotate(0);
				node.setRotationAxis(Rotate.Z_AXIS);
				node.getScene().setCamera(oldCamera);
			}
		}
	}
}


