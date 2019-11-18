package com.sujan.utils;

import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    private TitledPane moreDetailsTP;

    Validation() {
    }

    public Validation(TitledPane moreDetailsTP) {
        this.moreDetailsTP = moreDetailsTP;
    }

    /*
     * Validations
     */
    public boolean validate(Node node, String field, String value, String pattern) {
        if (value != null && !value.isEmpty()) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(value);
            if (m.find() && m.group().equals(value)) {
                validationTrue(node);
                return true;
            } else {
                validationAlert(node, field, false);
                return false;
            }
        } else {
            validationAlert(node, field, true);
            return false;
        }
    }

    public boolean emptyValidation(Node node, String field, boolean empty) {
        if (!empty) {
            validationTrue(node);
            return true;
        } else {
            validationAlert(node, field, true);
            return false;
        }
    }

    private void validationTrue(Node node) {
        node.setStyle(null);
    }

    private void validationAlert(Node node, String field, boolean empty) {
        node.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        if (field.equals("Role")) alert.setContentText("Please Select " + field);
        else {
            if (empty) alert.setContentText("Please Enter " + field);
            else alert.setContentText("Please Enter Valid " + field);
        }
        alert.showAndWait();

        if (field.equals("Father's Name") || field.equals("Mother's Name") || field.equals("Spouse's Name") || field.equals("Date of Membership") || field.equals("Age Group")) {
            if (!moreDetailsTP.isExpanded()) {
                moreDetailsTP.setExpanded(true);
            }
        }
    }

}
