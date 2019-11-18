package com.sujan.utils;

import com.sujan.bean.Admin;
import com.sujan.service.AdminService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class SystemWizard {

    private ObservableList<String> systemNames = FXCollections.observableArrayList(ResourceBundle.getBundle("Bundle").getString("churchNames").split(","));

    private boolean[] churchDetailsChanged = new boolean[2];    //0 -> Church Logo and 1 -> Church Name

    public boolean[] showLinearWizard(List<Admin> admins, AdminService adminService) {

        // define pages to show
        Wizard wizard = new Wizard();
        wizard.setTitle("System Setup Configuration");

        // --- page 1
        int row = 0;

        GridPane page1Grid = new GridPane();
        page1Grid.setVgap(10);
        page1Grid.setHgap(10);

        ObservableList<String> adminNames = FXCollections.observableArrayList();
        ChoiceBox<String> allAdmins = new ChoiceBox<>(adminNames);

        if (!admins.isEmpty()) {
            page1Grid.add(new Label("All Admins :"), 0, row);
            for (Admin a : admins) {
                adminNames.add(a.getAdminName());
            }
            page1Grid.add(allAdmins, 1, row++);
        }

        page1Grid.add(new Label("Admin :"), 0, row);
        final TextField userTF = createTextField("adminName");
        page1Grid.add(userTF, 1, row++);

        page1Grid.add(new Label("Password :"), 0, row);
        final TextField passTF = createTextField("password");
        // Set initial state
        passTF.setManaged(false);
        passTF.setVisible(false);
        page1Grid.add(passTF, 1, row);

        final PasswordField passPF = new PasswordField();
        passPF.setId("password");
        GridPane.setHgrow(passPF, Priority.ALWAYS);
        page1Grid.add(passPF, 1, row);

        CheckBox checkBox = new CheckBox("Show/Hide password");
        page1Grid.add(checkBox, 1, ++row);

        // Bind properties. Toggle textField and passwordField
        // visibility and manageability properties mutually when checkbox's state is changed.
        // Because we want to display only one component (textField or passwordField)
        // on the scene at a time.
        passTF.managedProperty().bind(checkBox.selectedProperty());
        passTF.visibleProperty().bind(checkBox.selectedProperty());

        passPF.managedProperty().bind(checkBox.selectedProperty().not());
        passPF.visibleProperty().bind(checkBox.selectedProperty().not());

        // Bind the textField and passwordField text values bidirectionally.
        passTF.textProperty().bindBidirectional(passPF.textProperty());

        WizardPane page1 = new WizardPane() {

            @Override
            public void onEnteringPage(Wizard wizard) {
                if (!admins.isEmpty()) {
                    System.out.println("Skipped!");
                } else {
                    for (ButtonType type : getButtonTypes()) {
                        if (type.getButtonData().equals(ButtonBar.ButtonData.NEXT_FORWARD)) {
                            Node next = lookupButton(type);
                            Platform.runLater(() -> next.setDisable(true));
                            userTF.textProperty().addListener((observable, oldValue, newValue) -> next.setDisable(newValue.trim().isEmpty() || passPF.getText().isEmpty()));
                            passPF.textProperty().addListener((observable, oldValue, newValue) -> next.setDisable(newValue.trim().isEmpty() || userTF.getText().isEmpty()));
                        } else if (type.getButtonData().equals(ButtonBar.ButtonData.CANCEL_CLOSE)) {
                            Node cancel = lookupButton(type);
                            Platform.runLater(() -> cancel.setVisible(false));
                        } else if (type.getButtonData().equals(ButtonBar.ButtonData.BACK_PREVIOUS)) {
                            Node previous = lookupButton(type);
                            Platform.runLater(() -> previous.setVisible(false));
                        }
                    }
                }
            }

            @Override
            public void onExitingPage(Wizard wizard) {

                String adminName = (String) wizard.getSettings().get("adminName");
                String pass = (String) wizard.getSettings().get("password");

                Validation validation = new Validation();
                if (!admins.isEmpty() && adminName.isEmpty() && pass.isEmpty()) {
                    System.out.println("Skipped!");
                } else {
                    if (validation.emptyValidation(userTF, "Admin", adminName.isEmpty()) &&
                            validation.emptyValidation(passTF, "Password", pass.isEmpty())) {
                        //save
                        if (adminNames.isEmpty()) {
                            Admin admin = new Admin();
                            admin.setAdminName(adminName);
                            admin.setPassword(pass);

                            adminService.save(admin);

                            adminNames.add(adminName);
                            admins.add(admin);
                        } else {
                            for (Admin existingAdmin : admins) {
                                //update
                                if (!existingAdmin.getAdminName().equals(adminName) || !existingAdmin.getPassword().equals(pass)) {
                                    boolean yesIdo = CommonUtils.confirmMessage("Are you Sure?", "Warning: Admin Credentials Changed! ", "Do you really want to update Admin Details?");
                                    if (yesIdo) {
                                        existingAdmin.setAdminName(adminName);
                                        existingAdmin.setPassword(pass);
                                        adminService.update(existingAdmin);
                                    } else {
                                        userTF.setText(existingAdmin.getAdminName());
                                        passTF.setText(existingAdmin.getPassword());
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        wizard.getSettings().put("adminName", "");
                    }
                }
            }
        };
        String adminHeaderTest;
        if (admins.isEmpty()) {
            adminHeaderTest = "No Admin Found. \nPlease add new Admin Details.";
        } else {
            adminHeaderTest = "Admin Details!";
        }
        page1.setHeaderText(adminHeaderTest);
        page1.setContent(page1Grid);

        allAdmins.getSelectionModel().

                selectedIndexProperty().

                addListener((observableValue, oldIndex, newIndex) ->

                {
                    if (newIndex != null) {
                        Admin existingAdmin = adminService.findByAdminName(allAdmins.getItems().get((Integer) newIndex));
                        userTF.setText(existingAdmin.getAdminName());
                        passTF.setText(existingAdmin.getPassword());
                    }
                });

        GridPane page3Grid = new GridPane();
        page3Grid.setVgap(10);
        page3Grid.setHgap(10);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setImage(CommonUtils.getLogoImage());
        imageView.setSmooth(true);

        page3Grid.add(imageView, 0, 0, 1, 3);

        page3Grid.add(new

                Label("Name   :"), 1, 0);
        page3Grid.add(new

                Label("Address:"), 1, 1);
        page3Grid.add(new

                Label("Contact:"), 1, 2);


        ChoiceBox<String> systemNameCB = new ChoiceBox<>(systemNames);
        systemNameCB.setId("churchName");
        GridPane.setHgrow(systemNameCB, Priority.ALWAYS);
        systemNameCB.getSelectionModel().select(ConfigReader.getSystemIndex());
        page3Grid.add(systemNameCB, 2, 0);

        TextField systemAddress = createTextField("churchAddress");
        systemAddress.setPromptText("Eg. Kalanki, Kathmandu");
        systemAddress.setText(ConfigReader.getSystemAddress());
        page3Grid.add(systemAddress, 2, 1);

        TextField systemContact = createTextField("churchContact");
        systemContact.setPromptText("Eg. 01-4XXXXXX");
        systemContact.setText(ConfigReader.getSystemContact());
        page3Grid.add(systemContact, 2, 2);

        // --- page 3
        WizardPane page3 = new WizardPane();
        page3.setHeaderText("Church Details!");

        page3.setContentText("Please provide Church Name and photo!\nNote: Changes will be active after Restart only.");
        page3.setContent(page3Grid);

        ButtonType photoDialogButton = new ButtonType("Change Logo", ButtonBar.ButtonData.HELP_2);
        page3.getButtonTypes().

                add(photoDialogButton);

        Button photoButton = (Button) page3.lookupButton(photoDialogButton);
        photoButton.setStyle("-fx-background-color: DODGERBLUE;");
        photoButton.setTextFill(Color.WHITE);
        photoButton.addEventFilter(ActionEvent.ACTION, actionEvent ->

        {
            actionEvent.consume(); // stop hello.dialog from closing
            getImage();
            imageView.setImage(CommonUtils.getLogoImage());
        });

        // create wizard
        wizard.setFlow(new Wizard.LinearFlow(page1, page3));

        // show wizard and wait for response
        wizard.showAndWait().

                ifPresent(result ->

                {
                    if (result == ButtonType.FINISH) {
                        try {
                            String[] currentChurchDetails = new String[]{String.valueOf(ConfigReader.getSystemIndex()), ConfigReader.getSystemAddress(), ConfigReader.getSystemContact()};
                            String[] newChurchDetails = new String[]{String.valueOf(systemNameCB.getSelectionModel().getSelectedIndex()), systemAddress.getText(), systemContact.getText()};
                            if (!Arrays.equals(currentChurchDetails, newChurchDetails)) {
                                ConfigReader.setSystemDetails(newChurchDetails);
                                churchDetailsChanged[1] = true;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        return churchDetailsChanged;
    }

    private TextField createTextField(String id) {
        TextField textField = new TextField();
        textField.setId(id);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        return textField;
    }

    private void getImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Church Logo");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.png", "*.jpg", "*.jpeg"));

        File sourceImageFile = chooser.showOpenDialog(null);

        if (sourceImageFile != null) {
            try {
                File destImageFile = new File(CommonUtils.getImagePath() + "system/logo.png");
                if (!destImageFile.exists()) {
                    destImageFile.getParentFile().mkdirs();
                    destImageFile.createNewFile();
                }
                FileSystemUtils.copyRecursively(sourceImageFile, destImageFile);
                churchDetailsChanged[0] = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
