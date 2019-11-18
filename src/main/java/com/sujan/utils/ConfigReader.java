package com.sujan.utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.controlsfx.dialog.ProgressDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConfigReader {

    private static final String configFile = "conf/conf.properties";

    private static final Properties prop = new Properties();

    static {
        try {
            prop.load(new FileInputStream(configFile));
        } catch (IOException ex) {
            CommonUtils.displayMessage("conf.properties not found!", " Cannot proceed without config files. Please check \"conf\" directory.", "error");
            System.out.println("Error:- " + ex.toString() + "\n Cannot proceed without config files.");
            System.exit(0);
        }
    }

    public static String getConfigDir() {
        return prop.getProperty("CONF_DIR");
    }

    static String getImageFolder() {
        return (getConfigDir() + File.separator + prop.getProperty("IMAGES_FOLDER"));
    }

    public static String getSystemName() {
        String[] names = ResourceBundle.getBundle("Bundle").getString("churchNames").split(",");
        String systemName = "WARNING: Church Name not found!!!";
        switch (prop.getProperty("SYSTEM_INDEX")) {
            case "0":
                systemName = names[0];
                break;
            case "1":
                systemName = names[1];
                break;
            default:
                int max = 5;

                final Executor exec = Executors.newCachedThreadPool(runnable -> {
                    Thread t = new Thread(runnable);
                    t.setDaemon(true);
                    return t;
                });

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() {
                        int iterations;
                        for (iterations = 1; iterations <= max; iterations++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            try {
                                Properties updatedPro = new Properties();
                                updatedPro.load(new FileInputStream(configFile));
                                if (updatedPro.getProperty("SYSTEM_INDEX").equals("0") || updatedPro.getProperty("SYSTEM_INDEX").equals("1")) {
                                    cancel(true);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (isCancelled()) {
                                updateMessage("Program Termination Cancelled! ");
                                break;
                            }
                            updateMessage("Count : " + iterations + " seconds");
                            updateProgress(iterations, max);
                        }
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        Platform.exit();
                    }
                };
                ProgressDialog progDiag = new ProgressDialog(task);
                progDiag.setTitle("INVALID CHURCH NAME!!!");
                progDiag.setHeaderText("Please select the correct Church Name. \n Program SHUTDOWN in " + max + " seconds...");
                progDiag.show();
                exec.execute(task);
        }
        return systemName;
    }

    static int getSystemIndex() {
        try {
            return Integer.parseInt(prop.getProperty("SYSTEM_INDEX"));
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

    public static String getSystemAddress() {
        return prop.getProperty("SYSTEM_ADDRESS");
    }

    public static String getSystemContact() {
        return prop.getProperty("SYSTEM_CONTACT");
    }

    private static void saveProperties(Properties p) throws IOException {
        FileOutputStream fr = new FileOutputStream(configFile);
        p.store(fr, "Properties");
        fr.close();
        try {
            prop.load(new FileInputStream(configFile));
        } catch (IOException ex) {
            CommonUtils.displayMessage("conf.properties not found!", " Cannot proceed without config files. Please check \"conf\" directory.", "error");
            System.out.println("Error:- " + ex.toString() + "\n Cannot proceed without config files.");
            System.exit(0);
        }
    }

    public static List<String> getTableColumnsArray() {
        String value = prop.getProperty("TABLE_VISIBLE_COLUMNS");
        if (value.contains(","))
            return Arrays.asList(value.split(","));
        return Collections.singletonList(value);
    }

    private static String getTableColumns() {
        return prop.getProperty("TABLE_VISIBLE_COLUMNS");
    }

    static void setSystemDetails(String[] values) throws IOException {
        Properties newProperties = new Properties();
        newProperties.setProperty("CONF_DIR", getConfigDir());
        newProperties.setProperty("IMAGES_FOLDER", prop.getProperty("IMAGES_FOLDER"));
        newProperties.setProperty("SYSTEM_INDEX", values[0]);
        newProperties.setProperty("SYSTEM_ADDRESS", values[1]);
        newProperties.setProperty("SYSTEM_CONTACT", values[2]);
        newProperties.setProperty("TABLE_VISIBLE_COLUMNS", getTableColumns());
        saveProperties(newProperties);
    }

    static void setTableView(String columns) throws IOException {
        Properties newProperties = new Properties();
        newProperties.setProperty("CONF_DIR", getConfigDir());
        newProperties.setProperty("IMAGES_FOLDER", prop.getProperty("IMAGES_FOLDER"));
        newProperties.setProperty("SYSTEM_INDEX", prop.getProperty("SYSTEM_INDEX"));
        newProperties.setProperty("SYSTEM_ADDRESS", getSystemAddress());
        newProperties.setProperty("SYSTEM_CONTACT", getSystemContact());
        newProperties.setProperty("TABLE_VISIBLE_COLUMNS", columns);
        saveProperties(newProperties);
    }

}
