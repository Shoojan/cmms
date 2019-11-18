package com.sujan.utils;

import javafx.scene.control.*;
import javafx.scene.image.Image;

import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * @author Sujan Maharjan
 */
public class CommonUtils {

    private static DecimalFormat df = new DecimalFormat("0.00");

    public static DecimalFormat getDf() {
        return df;
    }

    /**
     * @param title   title
     * @param content content
     * @param type    by default, type -> info. Else, type includes values -> {"error" | "warning"}
     */
    public static void displayMessage(String title, String content, String type) {
        Alert alert = new Alert(
                type.equals("error") ? Alert.AlertType.ERROR : (
                        type.equals("warning") ? Alert.AlertType.WARNING : Alert.AlertType.INFORMATION));
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean confirmMessage(String title, String Header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(Header);
        alert.showAndWait();

        return alert.getResult() == ButtonType.YES;
    }

    public static void deleteFile(File file) {
        //Delete working directory
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    public static String makeDateEmptyIfNull(LocalDate date) {
        return (date == null) ? "" : date.toString();
    }

    public static String makeEmptyIfNull(String string) {
        return (string == null) ? "" : string;
    }

    public static String getFileName(String filePath) {
        try {
            String path = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            if (path.length() > 50) {
                System.out.println("WARNING: File name too long... Trimming file name to 50 characters");
                path = path.substring(0, 50).trim();
            }
            return path;
        } catch (Exception e) {
            return filePath;
        }
    }

    public static String getImagePath() {
        return (new File("").getAbsolutePath()
                + File.separator + ConfigReader.getImageFolder()
                + File.separator);
    }

    public static Image getLogoImage() {
        String logo = CommonUtils.getImagePath() + "system" + File.separator + "logo.png";
        return (new File(logo).exists()) ?
                new Image("file:" + logo) :
                new Image(CommonUtils.class.getResourceAsStream("/images/CMMS_Logo.png"));
    }
}

