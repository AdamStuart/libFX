package util;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ImageUtil {

	static Image cat = null; //new Image(ImageUtil.class.getResource("cat.png"));
	
	
//http://stackoverflow.com/questions/26515326/create-a-image-from-text-with-background-and-wordwrap
	
public static Image textToImage(String text) {
    Label label = new Label(text);
    label.setMinSize(125, 125);
    label.setMaxSize(125, 125);
    label.setPrefSize(125, 125);
    label.setStyle("-fx-background-color: white; -fx-text-fill:black;");
    label.setWrapText(true);
    Scene scene = new Scene(new Group(label));
    WritableImage img = new WritableImage(125, 125) ;
    scene.snapshot(img);
    return img ;
}

}
