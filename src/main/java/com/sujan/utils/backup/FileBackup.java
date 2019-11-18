package com.sujan.utils.backup;

import com.sujan.utils.CommonUtils;
import com.sujan.utils.ConfigReader;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.Modality;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.ProgressDialog;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipFile;

public class FileBackup {

    private static int NO_FILES;
    private static final String pattern = "-dd_M_yyyy-hh_mm_ss.";
    private static final DateFormat dateFormat = new SimpleDateFormat(pattern);

    private File zipArchive;
    private String zipDir;

    public FileBackup(String zipDir) {
        this.zipDir = zipDir;
    }

    public FileBackup(File zipArchive) {
        this.zipArchive = zipArchive;
    }

    public void startBackupProcess() {

        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        updateMessage("Initializing CMMS Backup...");
                        updateProgress(0, 1);

                        zipArchive = new File(new File(zipDir), "CMMS_conf_Backup" + dateFormat.format(new Date()) + ".zip");
                        File backupFileDir = new File(ConfigReader.getConfigDir());
                        NO_FILES = countFilesInDirectory(backupFileDir);

                        if (zipArchive.exists()) {
                            zipArchive.delete();
                        }

                        // I'm using a set to keep track of how many files have been
                        // processed already, as I work in an anonymous class and I
                        // can't reference a non final variable - other solutions
                        // are class/object variables/fields
                        List<String> progress = new ArrayList<>();

                        ZipUtil.pack(backupFileDir, zipArchive, nameMapper -> {
                            progress.add(nameMapper);
                            double perc = (double) progress.size() / (double) NO_FILES;
                            updateProgress(perc, 1);
                            updateMessage(CommonUtils.getDf().format(perc * 100) + "%\t| Backed up " + progress.size() + " files!");
                            return nameMapper;
                        });
                        updateMessage("Backup Successful!");
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        Platform.runLater(() ->
                                Notifications.create()
                                        .title("CMMS Application")
                                        .text("Backup successful!")
                                        .showInformation()
                        );
                    }

                };
            }
        };

        ProgressDialog progressDialog = new ProgressDialog(service);
        progressDialog.initOwner(null);
        progressDialog.setTitle("Backup Progress");
        progressDialog.setHeaderText("Copying files.");
        progressDialog.initModality(Modality.WINDOW_MODAL);

        service.start();

    }

    public void restoreFromBackup() {

        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        updateMessage("Initializing CMMS Backup Restore...");
                        updateProgress(0, 1);

                        File backupFileDir = new File(ConfigReader.getConfigDir());
                        try {
                            NO_FILES = new ZipFile(zipArchive).size();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (backupFileDir.exists()) {
                            backupFileDir.delete();
                        }

                        // I'm using a set to keep track of how many files have been
                        // processed already, as I work in an anonymous class and I
                        // can't reference a non final variable - other solutions
                        // are class/object variables/fields
                        List<String> progress = new ArrayList<>();

                        ZipUtil.unpack(zipArchive, backupFileDir, nameMapper -> {
                            progress.add(nameMapper);
                            double perc = (double) progress.size() / (double) NO_FILES;
                            updateProgress(perc, 1);
                            updateMessage(CommonUtils.getDf().format(perc * 100) + "%\t| Restoring " + progress.size() + " files!");
                            return nameMapper;
                        });

                        updateMessage("Backup Restore Successful!");
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        Platform.runLater(() ->
                                Notifications.create()
                                        .title("CMMS Application")
                                        .text("Backup Restore successful! \nPlease Restart the program to retrieve the changes.")
                                        .showInformation()
                        );
                    }

                };
            }
        };

        ProgressDialog progressDialog = new ProgressDialog(service);
        progressDialog.initOwner(null);
        progressDialog.setTitle("Backup Progress");
        progressDialog.setHeaderText("Copying files.");
        progressDialog.initModality(Modality.WINDOW_MODAL);

        service.start();

    }

    /**
     * Count files in a directory (including files in all subdirectories)
     *
     * @param directory the directory to start in
     * @return the total number of files
     */
    private static int countFilesInDirectory(File directory) {
        int count = 0;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                count++;
            }
            if (file.isDirectory()) {
                count += countFilesInDirectory(file);
            }
        }
        return count;
    }

}