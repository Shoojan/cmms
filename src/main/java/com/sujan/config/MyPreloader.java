package com.sujan.config;

import com.sujan.Main;
import com.sujan.utils.CommonUtils;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MyPreloader extends Preloader {

    private static final double WIDTH = 500;
    private static final double HEIGHT = 300;

    private Stage preloaderStage;
    private Scene scene;

    private Label progress;

    public MyPreloader() {
        // Constructor is called before everything.
        System.out.println(Main.STEP() + "MyPreloader constructor called, thread: " + Thread.currentThread().getName());
    }

    @Override
    public void init() {
        System.out.println(Main.STEP() + "MyPreloader#init (could be used to initialize preloader view), thread: " + Thread.currentThread().getName());

        // If preloader has complex UI it's initialization can be done in MyPreloader#init
        Platform.runLater(() -> {
            Label title = new Label("Loading, please wait...");
            title.setTextAlignment(TextAlignment.CENTER);
            Font font = new Font(13);
            title.setFont(font);
            title.setTextFill(Color.WHITE);
            progress = new Label("0%");
            progress.setTextFill(Color.WHITE);
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/SplashScreen.png")));
            VBox contentBox = new VBox(title, progress);
            contentBox.setAlignment(Pos.BOTTOM_CENTER);

            AnchorPane root = new AnchorPane();

            AnchorPane.setTopAnchor(imageView, 0.0);
            AnchorPane.setTopAnchor(contentBox, 0.0);

            AnchorPane.setLeftAnchor(imageView, 0.0);
            AnchorPane.setLeftAnchor(contentBox, 0.0);

            AnchorPane.setBottomAnchor(imageView, 0.0);
            AnchorPane.setBottomAnchor(contentBox, 30.0);

            AnchorPane.setRightAnchor(imageView, 0.0);
            AnchorPane.setRightAnchor(contentBox, 0.0);

            root.getChildren().addAll(imageView, contentBox);

            scene = new Scene(root, WIDTH, HEIGHT);

        });
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println(Main.STEP() + "MyPreloader#start (showing preloader stage), thread: " + Thread.currentThread().getName());

        this.preloaderStage = primaryStage;

        // Set preloader scene and show stage.
        preloaderStage.setScene(scene);
        preloaderStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/CMMS_Logo.png")));
        preloaderStage.initStyle(StageStyle.UNDECORATED);
        preloaderStage.setResizable(false);
        preloaderStage.centerOnScreen();
        preloaderStage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        // Handle application notification in this point (see MyApplication#init).
        if (info instanceof ProgressNotification) {
            progress.setText(CommonUtils.getDf().format(((ProgressNotification) info).getProgress()) + "%");
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        // Handle state change notifications.
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_LOAD:
                // Called after MyPreloader#start is called.
                System.out.println(Main.STEP() + "BEFORE_LOAD");
                break;
            case BEFORE_INIT:
                // Called before MyApplication#init is called.
                System.out.println(Main.STEP() + "BEFORE_INIT");
                break;
            case BEFORE_START:
                // Called after MyApplication#init and before MyApplication#start is called.
                System.out.println(Main.STEP() + "BEFORE_START");

                preloaderStage.hide();
                break;
        }
    }
}
