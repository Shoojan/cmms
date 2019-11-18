package com.sujan.view;

import com.sujan.utils.ConfigReader;

import java.util.ResourceBundle;

public enum FxmlView {

    MEMBER {
        @Override
        public String getTitle() {
//            if (ConfigReader.getSystemName().isEmpty())
            return getStringFromResourceBundle("member.title");
//            else
//                return ConfigReader.getSystemName();
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Member.fxml";
        }
    },
    LOGIN {
        @Override
        public String getTitle() {
            return getStringFromResourceBundle("login.title");
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/Login.fxml";
        }
    };

    public abstract String getTitle();

    public abstract String getFxmlFile();

    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
