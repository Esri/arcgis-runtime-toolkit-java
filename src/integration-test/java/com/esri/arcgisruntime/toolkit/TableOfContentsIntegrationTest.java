package com.esri.arcgisruntime.toolkit;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.layers.*;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * Integration tests for TableOfContents.
 */
public class TableOfContentsIntegrationTest extends ApplicationTest {

  private StackPane stackPane;

  final String WILDFIRE_RESPONSE_URL = "https://sampleserver6.arcgisonline" +
      ".com/arcgis/rest/services/Wildfire/FeatureServer/0";
  final String WORLD_STREET_MAP = "http://services.arcgisonline" +
      ".com/ArcGIS/rest/services/World_Street_Map/MapServer";
  final String DEVA_TREES = "https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Trees" +
      "/SceneServer";
  final String DEVA_BUILDINGS = "https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services" +
      "/DevA_BuildingShells/SceneServer";
  final String DEVA_PATHWAYS = "https://services.arcgis" +
      ".com/P3ePLMYs2RVChkJx/arcgis/rest/services/DevA_Pathways/FeatureServer/1";


  @Override
  public void start(Stage primaryStage) {
    stackPane = new StackPane();
    Scene fxScene = new Scene(stackPane);
    primaryStage.setWidth(500);
    primaryStage.setHeight(500);
    primaryStage.setScene(fxScene);
    primaryStage.show();
    primaryStage.toFront();
  }

  @After
  public void cleanup() throws Exception {
    FxToolkit.cleanupStages();
  }

  /**
   * Tests that every operational layer in the map has its name displayed.
   */
  @Test
  public void itemForEveryOperationalLayerInMap() {
    // given a map view containing a map with an operational layer
    MapView mapView = new MapView();
    Platform.runLater(() -> stackPane.getChildren().add(mapView));

    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    map.getOperationalLayers().add(featureLayer);
    mapView.setMap(map);

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(mapView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_LEFT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(2000);

    // every layer's name will be displayed in the view
    map.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));
  }

  /**
   * Tests that every operational layer in the scene has its name displayed.
   */
  @Test
  public void itemForEveryOperationalLayerInScene() {
    // given a scene view containing a scene with an operational layer
    SceneView sceneView = new SceneView();
    Platform.runLater(() -> stackPane.getChildren().add(sceneView));

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());

    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    scene.getOperationalLayers().add(featureLayer);
    sceneView.setArcGISScene(scene);

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(sceneView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_LEFT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(2000);

    // every layer's name will be displayed in the view
    scene.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));
  }

  /**
   * Tests that every operational layer in the scene has its name displayed.
   */
  @Test
  public void removeLayer() {
    // given a scene view containing a scene with an operational layer
    SceneView sceneView = new SceneView();
    Platform.runLater(() -> stackPane.getChildren().add(sceneView));

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    scene.getOperationalLayers().add(featureLayer);
    sceneView.setArcGISScene(scene);

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(sceneView);
    tableOfContents.setMaxSize(150, 300);
    StackPane.setAlignment(tableOfContents, Pos.TOP_LEFT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(2000);

    // every layer's name will be displayed in the view
    scene.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));

    WaitForAsyncUtils.waitForFxEvents();

    scene.getOperationalLayers().remove(featureLayer);

    WaitForAsyncUtils.waitForFxEvents();

    Assertions.assertThrows(FxRobotException.class, () -> clickOn(featureLayer.getName()));
  }

  /**
   * Tests that every item which can change its visibility can have its visibility toggled via its checkbox.
   */
  @Test
  public void toggleVisibilityWithCheckbox() {
    // given a map view containing a map with an operational layer
    MapView mapView = new MapView();
    Platform.runLater(() -> stackPane.getChildren().add(mapView));

    ArcGISMap map = new ArcGISMap(Basemap.createImagery());
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    map.getOperationalLayers().add(featureLayer);
    mapView.setMap(map);

    TableOfContents tableOfContents = new TableOfContents(mapView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_LEFT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(2000);

    // when the item's checkbox is deselected
    Set<CheckBox> visibilityCheckboxes = lookup(n -> n instanceof CheckBox).queryAll();
    CheckBox checkBox = (CheckBox) visibilityCheckboxes.toArray()[0];
    clickOn(checkBox);

    WaitForAsyncUtils.waitForFxEvents();

    // the layer will not be visible
    Assertions.assertFalse(featureLayer.isVisible());

    // when the item's checkbox is selected
    clickOn(checkBox);

    WaitForAsyncUtils.waitForFxEvents();

    // the layer will be visible
    Assertions.assertTrue(featureLayer.isVisible());
  }

  /**
   * Tests that the checkbox is disabled when visibility cannot be changed.
   */
  @Test
  public void basemapLayers() {
    // given a map view containing a map with a basemap
    MapView mapView = new MapView();
    Platform.runLater(() -> stackPane.getChildren().add(mapView));

    ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(WORLD_STREET_MAP);
    ArcGISMap map = new ArcGISMap(new Basemap(tiledLayer));
    mapView.setMap(map);

    TableOfContents tableOfContents = new TableOfContents(mapView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_LEFT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(2000);

    ArcGISSublayer subLayer = tiledLayer.getSublayers().get(0);
    Assertions.assertFalse(subLayer.canChangeVisibility());

    // double-click parent to expand
    doubleClickOn(tiledLayer.getName());

    // the sublayer's item should not have a checkbox
    Set<CheckBox> visibilityCheckboxes = lookup(n -> n instanceof CheckBox).queryAll();
    Assertions.assertEquals(1, visibilityCheckboxes.size());
  }

  /**
   * Tests group layers in the table of contents.
   */
  @Test
  public void groupLayers() {
    // given a scene view containing a scene with a group layer
    SceneView sceneView = new SceneView();
    Platform.runLater(() -> stackPane.getChildren().add(sceneView));

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    ArcGISSceneLayer devOne = new ArcGISSceneLayer(DEVA_TREES);
    ArcGISSceneLayer devTwo = new ArcGISSceneLayer(DEVA_BUILDINGS);
    FeatureTable featureTable = new ServiceFeatureTable(DEVA_PATHWAYS);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);

    GroupLayer groupLayer = new GroupLayer();
    groupLayer.setName("Group: Dev A");
    groupLayer.getLayers().addAll(Arrays.asList(devOne, devTwo, featureLayer));
    scene.getOperationalLayers().add(groupLayer);
    sceneView.setArcGISScene(scene);

    featureLayer.addDoneLoadingListener(() -> sceneView.setViewpointCamera(new Camera(featureLayer.getFullExtent().getCenter(), 700, 0, 60, 0)));

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(sceneView);
    tableOfContents.setMaxSize(150, 300);
    StackPane.setAlignment(tableOfContents, Pos.TOP_LEFT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(2000);

    // the group layer's name and its children's names should be viewable
    clickOn(groupLayer.getName());
    clickOn(groupLayer.getName()); // double-click to expand
    groupLayer.getLayers().forEach(layer -> clickOn(layer.getName()));

    Set<CheckBox> visibilityCheckboxes = lookup(n -> n instanceof CheckBox).queryAll();
    Assertions.assertEquals(5, visibilityCheckboxes.size());

    // turn off parent
    CheckBox checkBox = (CheckBox) visibilityCheckboxes.toArray()[0];
    clickOn(checkBox);

    WaitForAsyncUtils.waitForFxEvents();

    Assertions.assertFalse(groupLayer.isVisible());
  }

  /**
   * Tests that one can create their own custom layer tree with most of the behavior of table of contents without a
   * map, scene, or geoview.
   */
  @Test
  public void customLayerTree() {
    // given a normal TreeView with some layer contents
    TreeView<LayerContent> layerTree = new TreeView<>();
    layerTree.setMaxSize(150, 100);
    Platform.runLater(() -> stackPane.getChildren().add(layerTree));
    StackPane.setAlignment(layerTree, Pos.TOP_LEFT);
    StackPane.setMargin(layerTree, new Insets(10));

    GroupLayer groupLayer = new GroupLayer();
    groupLayer.setName("Group");
    ArcGISSceneLayer devOne = new ArcGISSceneLayer(DEVA_TREES);
    ArcGISSceneLayer devTwo = new ArcGISSceneLayer(DEVA_BUILDINGS);
    FeatureTable featureTable = new ServiceFeatureTable(DEVA_PATHWAYS);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    groupLayer.getLayers().addAll(Arrays.asList(devOne, devTwo, featureLayer));

    TreeItem<LayerContent> root = new TreeItem<>();
    Platform.runLater(() -> layerTree.setRoot(root));
    layerTree.setShowRoot(false);

    // when the cell factory is LayerContentTreeCell and the items are LayerContentTreeItems
    layerTree.setCellFactory(new TableOfContents.LayerContentTreeCellFactory());
    root.getChildren().add(new TableOfContents.LayerContentTreeItem(groupLayer));

    sleep(2000);

    // then the items should show the layer content's name and a checkbox to change the visibility
    doubleClickOn("Group");
    groupLayer.getLayers().forEach(l -> clickOn(l.getName()));

    // when the cell factory is LayerContentTreeCell and the items are normal TreeItems
    root.getChildren().clear();
    root.getChildren().add(new TreeItem<>(groupLayer));

    WaitForAsyncUtils.waitForFxEvents();

    // then the child layers will not be shown
    doubleClickOn("Group");
    Assertions.assertThrows(FxRobotException.class, () -> clickOn(devOne.getName()));
  }

  @Test
  public void switchGeoView() {
    SceneView sceneView = new SceneView();
    Platform.runLater(() -> stackPane.getChildren().add(sceneView));

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    FeatureTable featureTable = new ServiceFeatureTable(WILDFIRE_RESPONSE_URL);
    FeatureLayer featureLayer = new FeatureLayer(featureTable);
    scene.getOperationalLayers().add(featureLayer);
    sceneView.setArcGISScene(scene);

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(sceneView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_LEFT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(2000);

    // every layer's name will be displayed in the view
    scene.getOperationalLayers().forEach(layer -> clickOn(layer.getName()));

    MapView mapView = new MapView();
    Platform.runLater(() -> {
      stackPane.getChildren().remove(sceneView);
      stackPane.getChildren().add(0, mapView);
    });

    ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(WORLD_STREET_MAP);
    ArcGISMap map = new ArcGISMap(new Basemap(tiledLayer));
    mapView.setMap(map);
    tableOfContents.setGeoView(mapView);

    sleep(2000);

    map.getBasemap().getBaseLayers().forEach(l -> clickOn(l.getName()));
  }

  @Test
  public void selection() {
    // given a map view containing a map with an operational layer
    SceneView sceneView = new SceneView();
    Platform.runLater(() -> stackPane.getChildren().add(sceneView));

    ArcGISScene scene = new ArcGISScene(Basemap.createImagery());
    ArcGISSceneLayer devOne = new ArcGISSceneLayer(DEVA_TREES);
    ArcGISSceneLayer devTwo = new ArcGISSceneLayer(DEVA_BUILDINGS);
    scene.getOperationalLayers().addAll(Arrays.asList(devOne, devTwo));
    sceneView.setArcGISScene(scene);

    // when the table of contents is added
    TableOfContents tableOfContents = new TableOfContents(sceneView);
    tableOfContents.setMaxSize(150, 100);
    StackPane.setAlignment(tableOfContents, Pos.TOP_LEFT);
    StackPane.setMargin(tableOfContents, new Insets(10));
    Platform.runLater(() -> stackPane.getChildren().add(tableOfContents));

    sleep(4000);

    clickOn(devOne.getName());

    WaitForAsyncUtils.waitForFxEvents();

    Assertions.assertEquals(devOne, tableOfContents.getSelectedItem());

    tableOfContents.selectedItemProperty().addListener(new ChangeListener<>() {
      @Override
      public void changed(ObservableValue<? extends LayerContent> observable, LayerContent oldValue, LayerContent newValue) {
        if (newValue instanceof Layer) {
          sceneView.setViewpoint(new Viewpoint(((Layer) newValue).getFullExtent()));
          tableOfContents.selectedItemProperty().removeListener(this);
        }
      }
    });

    tableOfContents.setSelectedItem(devTwo);

    WaitForAsyncUtils.waitForFxEvents();

    Assertions.assertTrue(devOne.getFullExtent().equals(
        GeometryEngine.project(sceneView.getCurrentViewpoint(Viewpoint.Type.BOUNDING_GEOMETRY).getTargetGeometry(),
            devOne.getFullExtent().getSpatialReference()), 0.01));
  }
}