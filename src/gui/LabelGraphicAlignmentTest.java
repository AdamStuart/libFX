package gui;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LabelGraphicAlignmentTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Label label = new Label("Some\ntext");
        label.setGraphic(new ImageView(getClass().getResource("sink.JPG").toExternalForm()));
        label.setMaxWidth(Double.POSITIVE_INFINITY);
        label.setMaxHeight(Double.POSITIVE_INFINITY);
        label.setStyle("-fx-border-color: blue;");
        root.setCenter(label);

        ComboBox<ContentDisplay> contentDisplayBox = new ComboBox<>();
        contentDisplayBox.getItems().addAll(ContentDisplay.values());
        contentDisplayBox.getSelectionModel().select(ContentDisplay.LEFT);
        label.contentDisplayProperty().bind(contentDisplayBox.valueProperty());

        ComboBox<Pos> alignmentBox = new ComboBox<>();
        alignmentBox.getItems().addAll(Pos.values());
        alignmentBox.getSelectionModel().select(Pos.CENTER);
        label.alignmentProperty().bind(alignmentBox.valueProperty());

        ComboBox<TextAlignment> textAlignmentBox = new ComboBox<>();
        textAlignmentBox.getItems().addAll(TextAlignment.values());
        textAlignmentBox.getSelectionModel().select(TextAlignment.LEFT);
        label.textAlignmentProperty().bind(textAlignmentBox.valueProperty());

        GridPane ctrls = new GridPane();
        ctrls.setHgap(5);
        ctrls.setVgap(5);
        ctrls.setPadding(new Insets(10));

        ctrls.addRow(0, new Label("Content display:"), new Label("Alignment:"), new Label("Text Alignment:"));
        ctrls.addRow(1,  contentDisplayBox, alignmentBox, textAlignmentBox);

        root.setTop(ctrls);

        Scene scene = new Scene(root, 600, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}