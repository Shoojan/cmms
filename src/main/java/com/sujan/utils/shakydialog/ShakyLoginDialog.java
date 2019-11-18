package com.sujan.utils.shakydialog;

import com.sujan.bean.Admin;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.util.Pair;

public class ShakyLoginDialog {
    private Admin admin;

    public ShakyLoginDialog(Admin admin) {
        this.admin = admin;
    }

    public boolean display() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Admin Login");
        dialog.setHeaderText("LOGIN");
        dialog.setContentText("Please enter your Admin Credential: ");
        dialog.initModality(Modality.NONE);
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfAdmin = new TextField();
        tfAdmin.setPromptText("admin");
        PasswordField tfPassword = new PasswordField();
        tfPassword.setPromptText("xxxx");

        grid.add(new Label("Admin Name :"), 0, 0);
        grid.add(tfAdmin, 1, 0);
        grid.add(new Label("Password   :"), 0, 1);
        grid.add(tfPassword, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        tfAdmin.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        // Request focus on the player name field by default.
        Platform.runLater(tfAdmin::requestFocus);

        loginButton.addEventFilter(EventType.ROOT,
                e -> {
                    if (e.getEventType().equals(ActionEvent.ACTION)) {
                        e.consume();

                        boolean adminCorrect = tfAdmin.getText().equals(admin.getAdminName());
                        boolean passCorrect = tfPassword.getText().equals(admin.getPassword());
                        if (adminCorrect && passCorrect) {
                            dialog.setHeaderText("Successful!");
                            dialog.close();
                        } else {
                            ShakeTransition anim = new ShakeTransition(dialog.getDialogPane(), null);
                            anim.playFromStart();
                            dialog.setHeaderText("Login Failed!");

                            String style = "-fx-text-box-border: red ; -fx-focus-color: red ;";
                            if (adminCorrect) tfAdmin.setStyle(null);
                            else tfAdmin.setStyle(style);

                            if (passCorrect) tfPassword.setStyle(null);
                            else tfPassword.setStyle(style);
                        }
                    }
                });

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();

        return dialog.getHeaderText().equals("Successful!");
    }

}
