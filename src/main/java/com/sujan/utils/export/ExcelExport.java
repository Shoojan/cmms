package com.sujan.utils.export;

import com.sujan.bean.Member;
import com.sujan.utils.CommonUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelExport {

    private TableView<Member> memberTableView;
    private File outputFile;
    private List<String> ignoreHeadersList = new ArrayList<>(Arrays.asList("ID", "Photo", "Edit"));

    private static DoubleProperty taskDone;
    private static StringProperty taskMessage;

    public ExcelExport(TableView<Member> memberTableView, File outputFile) {
        this.memberTableView = memberTableView;
        this.outputFile = outputFile;
    }

    private void updateTaskProgress(double value, String msg) {
        double wd = Double.parseDouble(CommonUtils.getDf().format((value * 10)));
        ExcelExport.taskDone.setValue(wd);
        ExcelExport.taskMessage.setValue(wd + "%  | " + msg);
    }

    public void export(DoubleProperty configTaskDone, StringProperty taskMessage) {
        ExcelExport.taskDone = configTaskDone;
        ExcelExport.taskMessage = taskMessage;

        try {
            updateTaskProgress(1, "Creating workbook.");

            int[] ignoreHeaderIndex = new int[ignoreHeadersList.size()];
            Workbook workbook = new XSSFWorkbook();

            Sheet spreadsheet = workbook.createSheet("Members");

            // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.BLACK.getIndex());

            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            updateTaskProgress(1.5, "Collecting Header Names...");

            Row row = spreadsheet.createRow(0);

            int colInd = 0;
            for (int j = 0; j < memberTableView.getColumns().size(); j++) {
                if (memberTableView.getColumns().get(j).isVisible()) {
                    String header = memberTableView.getColumns().get(j).getText();
                    boolean addHeader = true;
                    for (int i = 0; i < ignoreHeadersList.size(); i++) {
                        if (ignoreHeadersList.get(i).equals(header)) {
                            ignoreHeaderIndex[i] = j;
                            addHeader = false;
                            break;
                        }
                    }
                    if (addHeader) {
                        Cell cell = row.createCell(colInd);
                        cell.setCellValue(header);
                        cell.setCellStyle(headerCellStyle);
                        colInd++;
                    }
                }
            }
            updateTaskProgress(2, "Collecting Member Details...");

            double inititalTask = 2;
            double forEachRow = 7.0 / memberTableView.getItems().size();

            for (int i = 0; i < memberTableView.getItems().size(); i++) {
                row = spreadsheet.createRow(i + 1);
                int colind = 0;
                for (int j = 0; j < memberTableView.getColumns().size(); j++) {
                    if (memberTableView.getColumns().get(j).isVisible()) {
                        int finalJ = j;
                        if (Arrays.stream(ignoreHeaderIndex).noneMatch(index -> index == finalJ)) {
                            if (memberTableView.getColumns().get(j).getCellData(i) != null) {
                                row.createCell(colind).setCellValue(memberTableView.getColumns().get(j).getCellData(i).toString());
                            } else {
                                row.createCell(colind).setCellValue("");
                            }
                            colind++;
                        }
                    }
                }
                updateTaskProgress(inititalTask, "Populating Member Details...");
                inititalTask += forEachRow;
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < memberTableView.getItems().size(); i++) {
                spreadsheet.autoSizeColumn(i);
            }

            updateTaskProgress(9.0, "Preparing excel file creation...");

            FileOutputStream fileOut = new FileOutputStream(outputFile);
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();

            updateTaskProgress(10.0, "Excel file created successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
