package com.sujan.utils.imageprocess;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.github.sarxos.webcam.WebcamImageTransformer;
import com.github.sarxos.webcam.WebcamResolution;
import com.sujan.utils.CommonUtils;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.github.sarxos.webcam.Webcam;
import javafx.scene.shape.StrokeType;

import javax.imageio.ImageIO;

public class WebCamLauncher {
    private CountDownLatch latch;
    private VBox imgVBox;
    private String imageName;


    private FlowPane bottomCameraControlPane;
    private FlowPane topPane;
    private BorderPane root;
    private String cameraListPromptText = "Choose Camera";

    private ImageView imgWebCamCapturedImage;
    private Webcam webCam = null;

    private boolean stopCamera = false;
    private BufferedImage grabbedImage;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    private StackPane webCamPane;

    private Rectangle rectangle;
    private ComboBox<WebCamInfo> cameraOptions = new ComboBox<>();
    private ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
    private ObservableList<Boolean> cancelGarneyHo;

    public WebCamLauncher(CountDownLatch latch, String imageName, VBox imgVBox, ObservableList<Boolean> cancelGarneyHo) {
        this.latch = latch;
        this.imageName = imageName;
        this.imgVBox = imgVBox;
        this.cancelGarneyHo = cancelGarneyHo;
    }

    public void startCamera() {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #ccc;");

        topPane = new FlowPane();
        topPane.setAlignment(Pos.CENTER);
        topPane.setHgap(20);
        topPane.setOrientation(Orientation.HORIZONTAL);
        topPane.setPrefHeight(40);
        root.setTop(topPane);

        createTopPanel();

        createCameraControls();

        webCamPane = new StackPane();
        webCamPane.setMaxSize(640, 480);

        imgWebCamCapturedImage = new ImageView();
        webCamPane.getChildren().addAll(imgWebCamCapturedImage);
        webCamPane.setAlignment(Pos.CENTER);

        rectangle = new Rectangle(80, 2.5, 475, 475);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.GREENYELLOW);
        rectangle.setStrokeType(StrokeType.INSIDE);
        rectangle.setStrokeWidth(2.0);
        webCamPane.getChildren().add(rectangle);

        root.setCenter(webCamPane);

        Platform.runLater(this::setImageViewSize);

        if (!options.isEmpty()) {
            cameraOptions.getSelectionModel().select(0);
        }

        Platform.runLater(() -> {
            imgVBox.getChildren().add(0, root);
            imgVBox.setAlignment(Pos.TOP_LEFT);
        });
    }

    protected void setImageViewSize() {

        double height = webCamPane.getMaxHeight();
        double width = webCamPane.getMaxWidth();

        imgWebCamCapturedImage.setFitHeight(height);
        imgWebCamCapturedImage.setFitWidth(width);
        imgWebCamCapturedImage.prefHeight(height);
        imgWebCamCapturedImage.prefWidth(width);
        imgWebCamCapturedImage.setPreserveRatio(true);
    }


    private void createTopPanel() {

        int webCamCounter = 0;
        Label lbInfoLabel = new Label("Select Your WebCam Camera");
        options.clear();

        topPane.getChildren().add(lbInfoLabel);

        for (Webcam webcam : Webcam.getWebcams()) {
            WebCamInfo webCamInfo = new WebCamInfo();
            webCamInfo.setWebCamIndex(webCamCounter);
            webCamInfo.setWebCamName(webcam.getName());
            options.add(webCamInfo);
            webCamCounter++;
        }

        cameraOptions.setItems(options);
        cameraOptions.setPromptText(cameraListPromptText);
        cameraOptions.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) -> {
            if (arg2 != null) {
                System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
                initializeWebCam(arg2.getWebCamIndex());
            }
        });
        topPane.getChildren().add(cameraOptions);
    }


    private void initializeWebCam(final int webCamIndex) {

        /*
         * When you set custom resolutions you have to be sure that your webcam
         * device will handle them!
         */
        //@formatter:off
        Dimension[] nonStandardResolutions = new Dimension[]{
                WebcamResolution.PAL.getSize(),
                WebcamResolution.HD.getSize(),
                new Dimension(640, 480),
                new Dimension(320, 240)
        };
        //@formatter:on

        Task<Void> webCamTask = new Task<Void>() {
            @Override
            protected Void call() {
                if (webCam != null) {
                    disposeWebCamCamera();
                }
                webCam = Webcam.getWebcams().get(webCamIndex);
                WebcamImageTransformer webcamImageTransformer = (bufferedImage -> ImageFlip.flip(bufferedImage, ImageFlip.FLIP_HORIZONTAL));
                webCam.setImageTransformer(webcamImageTransformer);
                webCam.setCustomViewSizes(nonStandardResolutions);
                webCam.setViewSize(nonStandardResolutions[2]);
                webCam.open();

                startWebCamStream();

                return null;
            }
        };

        Thread webCamThread = new Thread(webCamTask);
        webCamThread.setDaemon(true);
        webCamThread.start();

        bottomCameraControlPane.setDisable(false);

    }


    private void startWebCamStream() {
        stopCamera = false;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                while (!stopCamera) {
                    try {
                        if ((grabbedImage = webCam.getImage()) != null) {
//                            System.out.println("Captured Image height*width:" + grabbedImage.getWidth() + "*" + grabbedImage.getHeight());
                            Platform.runLater(() -> imageProperty.set(SwingFXUtils.toFXImage(grabbedImage, null)));
                            grabbedImage.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        imgWebCamCapturedImage.imageProperty().bind(imageProperty);
    }

    private void createCameraControls() {
        bottomCameraControlPane = new FlowPane();
        bottomCameraControlPane.setOrientation(Orientation.HORIZONTAL);
        bottomCameraControlPane.setAlignment(Pos.CENTER);
        bottomCameraControlPane.setHgap(20);
        bottomCameraControlPane.setVgap(10);
        bottomCameraControlPane.setPrefHeight(40);
        bottomCameraControlPane.setDisable(true);

        Button btnCameraCapture = new Button();
        btnCameraCapture.setText("Capture");
        btnCameraCapture.setOnAction(arg0 -> captureImage());

        Button btnCamreaStop = new Button();
        btnCamreaStop.setOnAction(arg0 -> exitCamera());
        btnCamreaStop.setText("Cancel");

        bottomCameraControlPane.getChildren().add(btnCameraCapture);
        bottomCameraControlPane.getChildren().add(btnCamreaStop);

        root.setBottom(bottomCameraControlPane);
    }

    private void captureImage() {
        // get image
        Image image = imgWebCamCapturedImage.getImage();
        File file = new File(imageName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException es) {
                es.printStackTrace();
            }
        }

        PixelReader reader = image.getPixelReader();
        WritableImage writableImage = new WritableImage(reader, (int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());

        // save image (without alpha)
        // --------------------------------
        BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(writableImage, null);
        BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(), bufImageARGB.getHeight(), BufferedImage.OPAQUE);

        Graphics2D graphics = bufImageRGB.createGraphics();
        graphics.drawImage(bufImageARGB, 0, 0, null);

        // save image to PNG file
        try {
            ImageIO.write(bufImageRGB, "jpg", new File(imageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphics.dispose();
        exit();
    }

    private void disposeWebCamCamera() {
        stopCamera = true;
        webCam.close();
    }

    private void exitCamera() {
        boolean sureExit = CommonUtils.confirmMessage("Are you sure?", null, "Do you really want to exit the Camera Capture?");
        if (sureExit) {
            exit();
            cancelGarneyHo.add(true);
        }
    }

    private void exit() {
        Platform.runLater(() -> {
            imgVBox.getChildren().remove(root);
            imgVBox.setAlignment(Pos.CENTER_LEFT);
        });
        disposeWebCamCamera();
        latch.countDown();
    }

    class WebCamInfo {
        private String webCamName;
        private int webCamIndex;

        String getWebCamName() {
            return webCamName;
        }

        void setWebCamName(String webCamName) {
            this.webCamName = webCamName;
        }

        int getWebCamIndex() {
            return webCamIndex;
        }

        void setWebCamIndex(int webCamIndex) {
            this.webCamIndex = webCamIndex;
        }

        @Override
        public String toString() {
            return webCamName;
        }

    }

}
