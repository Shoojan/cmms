package com.sujan.utils.imageprocess;

import com.sujan.utils.CommonUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.SnapshotView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ImageCrop {
    private CountDownLatch latch;
    private BorderPane root;
    private VBox imgVBox;
    private String imageName;
    private Image image;
    private ObservableList<Boolean> cancelGarneyHo;


    public ImageCrop(VBox imgVBox, Image image, String imageName, CountDownLatch latch, ObservableList<Boolean> cancelGarneyHo) {
        this.imgVBox = imgVBox;
        this.imageName = imageName;
        this.image = image;
        this.latch = latch;
        this.cancelGarneyHo = cancelGarneyHo;
    }

    public void startCropping() {

        root = new BorderPane();

        // load the image and set the container for the image as a javafx node
        double preservedRatio;
        ImageView imageView = new ImageView(image);
        if (image.getHeight() > image.getWidth()) {
            imageView.setFitHeight(480);
            preservedRatio = image.getHeight() / imageView.getFitHeight();
        } else {
            imageView.setFitWidth(480);
            preservedRatio = image.getWidth() / imageView.getFitWidth();
        }
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        SnapshotView snapshotView = new SnapshotView(imageView);
//        snapshotView.setPrefSize(250, 250);
//            snapshotView.setSelection(50.0, 50.0, 200.0, 200.0);
//        snapshotView.setSelectionRatioFixed(true);
//        snapshotView.setFixedSelectionRatio(1);
        snapshotView.selectionBorderPaintProperty().setValue(Color.AQUA);

        //create button for cropping
        Button cropBtn = new Button("Crop Image");
        cropBtn.setDisable(true);

        cropBtn.disableProperty().bind(snapshotView.selectionActiveProperty().not());

        //create another button for canceling
        Button cancelBtn = new Button("Cancel");

        //create a HBox to keep both buttons
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(cropBtn, cancelBtn);

        // put imageLayer and cropBtn in borderPane
        root.setCenter(snapshotView);
        root.setBottom(hBox);

        cropBtn.setOnAction(e -> {

            File file = new File(imageName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException es) {
                    es.printStackTrace();
                }
            }

            // preserve the quality of image
            Rectangle2D rectangle2D = snapshotView.getSelection();
            int width = (int) (preservedRatio * rectangle2D.getWidth());
            int height = (int) (preservedRatio * rectangle2D.getHeight());
            int x = (int) (preservedRatio * rectangle2D.getMinX());
            int y = (int) (preservedRatio * rectangle2D.getMinY());

            PixelReader reader = image.getPixelReader();
            WritableImage writableImage = new WritableImage(reader, x, y, width, height);

//            WritableImage writableImage = snapshotView.createSnapshot();

            // save image (without alpha)
            // --------------------------------
            BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(writableImage, null);
            BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(), bufImageARGB.getHeight(), BufferedImage.OPAQUE);

            Graphics2D graphics = bufImageRGB.createGraphics();
            graphics.drawImage(bufImageARGB, 0, 0, null);

            try {
                if (ImageIO.write(bufImageRGB, "jpg", file)) {
                    System.out.println("Image saved to " + file.getAbsolutePath());
                } else
                    System.out.println("Image not saved.");

            } catch (IOException ea) {
                ea.printStackTrace();
            }

            graphics.dispose();
            exit();
        });

        cancelBtn.setOnAction(e -> {
            boolean sure = CommonUtils.confirmMessage("Are you sure?", "Cancel Image Cropping!", "Are you sure you want to cancel Image Cropping?");
            if (sure) {
                cancelGarneyHo.add(true);
                exit();
            }
        });

        AnchorPane.setTopAnchor(root, 5.0);
        Platform.runLater(() -> {
            imgVBox.getChildren().add(0, root);
            imgVBox.setAlignment(Pos.TOP_LEFT);
        });
    }

    private void exit() {
        Platform.runLater(() -> {
            imgVBox.getChildren().remove(root);
            imgVBox.setAlignment(Pos.CENTER_LEFT);
        });
        latch.countDown();
    }

}
