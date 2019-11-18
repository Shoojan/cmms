package com.sujan.config;

import com.sujan.view.FxmlView;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;

import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Manages switching Scenes on the Primary Stage
 */
public class StageManager {

    private static final Logger LOG = getLogger(StageManager.class);
    private final Stage primaryStage;
    private final double[] windowSize = new double[2];
    private final SpringFXMLLoader springFXMLLoader;

    StageManager(SpringFXMLLoader springFXMLLoader, Stage stage) {
        this.springFXMLLoader = springFXMLLoader;
        this.primaryStage = stage;
        this.windowSize[0] = stage.getMaxHeight();
        this.windowSize[1] = stage.getMaxWidth();
    }

    public void switchScene(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        show(viewRootNodeHierarchy, view);
    }

    private void setSize(double minH, double minW, double H, double W, double maxH, double maxW) {
        primaryStage.setMinHeight(minH);
        primaryStage.setMinWidth(minW);
        primaryStage.setHeight(H);
        primaryStage.setWidth(W);
        primaryStage.setMaxHeight(maxH);
        primaryStage.setMaxWidth(maxW);
    }

    private void show(final Parent rootnode, FxmlView view) {

        Scene scene = prepareScene(rootnode);

        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(view.getTitle());
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/CMMS_Logo.png")));

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();

        if (view == FxmlView.LOGIN) {
            setSize(300, 500, 300, 500, 300, 500);
            primaryStage.setResizable(false);
        } else if (view == FxmlView.MEMBER) {
            primaryStage.setResizable(true);
            setSize(550, 400, 700, 1000, windowSize[0], windowSize[1]);
        }

        primaryStage.centerOnScreen();

        try {
            primaryStage.show();
        } catch (Exception exception) {
            logAndExit("Unable to show scene for title" + view.getTitle(), exception);
        }
    }

    private Scene prepareScene(Parent rootnode) {
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootnode);
        }
        scene.setRoot(rootnode);
        return scene;
    }

    /**
     * Loads the object hierarchy from a FXML document and returns to root node
     * of that hierarchy.
     *
     * @return Parent root node of the FXML document hierarchy
     */
    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        Parent rootNode = null;
        try {
            rootNode = springFXMLLoader.load(fxmlFilePath);
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception exception) {
            logAndExit("Unable to load FXML view" + fxmlFilePath, exception);
        }
        return rootNode;
    }


    private void logAndExit(String errorMsg, Exception exception) {
        LOG.error(errorMsg, exception, exception.getCause());
        Platform.exit();
    }

}
