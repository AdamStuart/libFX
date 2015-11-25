/* 
 * Copyright 2014 Jens Deters.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * A simple Utility to make all {@link Tab}s of a {@link TabPane} detachable.
 * <br>
 * <h1>Usage</h1>
 * To get a {@link TabPane} in charge of control:
 * <br>
 * <b>Hint: Only already added {@link Tab}s are going to be in charge of control!</b>
 * <pre>
 * {@code
 * TabPaneDude.create().makeTabsDetachable(myTapPane);
 * }
 * </pre> Tabs can then be detached simply by dragging a tab title to the desired window position.
 *
/**
 *  https://bitbucket.org/Jerady/shichimifx/
 * @author Jens Deters (www.jensd.de) 
 * @version 1.0.0
 * @since 14-10-2014
 */
public class TabPaneDetacher {

    private TabPane tabPane;
    private Tab currentTab;
    private final List<Tab> originalTabs;
    private final Map<Integer, Tab> tabTransferMap;
    private String[] stylesheets;
    private final BooleanProperty alwaysOnTop;

    private TabPaneDetacher() {
        originalTabs = new ArrayList<>();
        stylesheets = new String[]{};
        tabTransferMap = new HashMap<>();
        alwaysOnTop = new SimpleBooleanProperty();
    }

    /**
     * Creates a new instance of the TabPaneDetacher
     *
     * @return The new instance of the TabPaneDetacher.
     */
    public static TabPaneDetacher create() {        return new TabPaneDetacher();    }

    public BooleanProperty alwaysOnTopProperty() {        return alwaysOnTop;    }
    public Boolean isAlwaysOnTop() {        return alwaysOnTop.get();    }

    /**
     * 
     * Sets whether detached Tabs should be always on top.
     * 
     * @param alwaysOnTop The state to be set.
     * @return The current TabPaneDetacher instance.
     */
    public TabPaneDetacher alwaysOnTop(boolean alwaysOnTop){
        alwaysOnTopProperty().set(alwaysOnTop);
        return this;
    }
    
    /**
     * Sets the stylesheets that should be assigned to the new created {@link Stage}.
     *
     * @param stylesheets The stylesheets to be set.
     * @return The current TabPaneDetacher instance.
     */
    public TabPaneDetacher stylesheets(String... stylesheets) {
        this.stylesheets = stylesheets;
        return this;
    }

    /**
     * Make all added {@link Tab}s of the given {@link TabPane} detachable.
     *
     * @param tabPane The {@link TabPane} to take over.
     * @return The current TabPaneDetacher instance.
     */
    public TabPaneDetacher makeTabsDetachable(TabPane tabPane) {
        this.tabPane = tabPane;
        originalTabs.addAll(tabPane.getTabs());
        for (int i = 0; i < tabPane.getTabs().size(); i++) 
            tabTransferMap.put(i, tabPane.getTabs().get(i));
       
        Scene scene = tabPane.getScene();			// AST copy the stylesheets across (must be a better way!)
        if (scene != null)
        {
        	ObservableList<String> objs = scene.getStylesheets();
        	String[] strs = new String[objs.size()];
        	int i = 0;
        	for (String s : objs) 	strs[i++] = s;
        	stylesheets(strs);
        }											//----------------------------------
        tabPane.getTabs().stream().forEach(t -> {   t.setClosable(false);    });
        tabPane.setOnDragDetected(
                (MouseEvent event) -> {
                    if (event.getSource() instanceof TabPane) {
                        Parent rootPane = tabPane.getScene().getRoot();
                        rootPane.setOnDragOver((DragEvent event1) -> {
                            event1.acceptTransferModes(TransferMode.ANY);
                            event1.consume();
                        });
                        currentTab = tabPane.getSelectionModel().getSelectedItem();
                        SnapshotParameters snapshotParams = new SnapshotParameters();
                        snapshotParams.setTransform(Transform.scale(0.4, 0.4));
                        WritableImage snapshot = currentTab.getContent().snapshot(snapshotParams, null);
                        Dragboard db = tabPane.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent clipboardContent = new ClipboardContent();
                        clipboardContent.put(DataFormat.PLAIN_TEXT, "detach");
                        db.setDragView(snapshot, 0, -100);
                        db.setContent(clipboardContent);
                    }
                    event.consume();
                }
        );
        tabPane.setOnDragDone(
                (DragEvent event) -> {
                    openTabInStage(currentTab);
                    tabPane.setCursor(Cursor.DEFAULT);
                    event.consume();
                }
        );
        return this;
    }

    /**
     * Opens the content of the given {@link Tab} in a separate Stage. While the content is removed from the {@link Tab} it is
     * added to the root of a new {@link Stage}. The Window title is set to the name of the {@link Tab};
     *
     * @param tab The {@link Tab} to get the content from.
     */
    public void openTabInStage(final Tab tab) {
        
    	if(tab == null)           return;
        
    	int W = 800;
    	int H = 500;
        int originalTab = originalTabs.indexOf(tab);
        tabTransferMap.remove(originalTab);
        Pane content = (Pane) tab.getContent();
        if (content == null) {
            throw new IllegalArgumentException("Can not detach Tab '" + tab.getText() + "': content is empty (null).");
        }
        tab.setContent(null);
        final Scene scene = new Scene(content, content.getPrefWidth(), content.getPrefHeight());
        scene.getStylesheets().addAll(stylesheets);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(tab.getText());
        stage.setAlwaysOnTop(isAlwaysOnTop());
        Point2D p = MouseRobot.getMousePosition();
        stage.setWidth(W);
        stage.setHeight(H);
        stage.setX(p.getX()-W/2);
        stage.setY(p.getY());
        stage.setOnCloseRequest((WindowEvent t) -> {
            stage.close();
            tab.setContent(content);
            int originalTabIndex = originalTabs.indexOf(tab);
            tabTransferMap.put(originalTabIndex, tab);
            int index = 0;
            SortedSet<Integer> keys = new TreeSet<>(tabTransferMap.keySet());
            for (Integer key : keys) {
                Tab value = tabTransferMap.get(key);
                if(!tabPane.getTabs().contains(value)){
                    tabPane.getTabs().add(index, value);
                }
                index++;
            }
            tabPane.getSelectionModel().select(tab);
        });
        stage.setOnShown((WindowEvent t) -> {   tab.getTabPane().getTabs().remove(tab);  });		// TODO -- make it possible to clone here, instead of remove
        stage.show();
    }

}