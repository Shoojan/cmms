package com.sujan.utils;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.util.List;

public class TableUtils {
    private static ObservableList<String> memberFields;
    private static CheckComboBox<String> fieldsCB;

    public TableUtils(CheckComboBox<String> fieldsCB, ObservableList<String> memberFields) {
        TableUtils.memberFields = memberFields;
        TableUtils.fieldsCB = fieldsCB;
    }

    /**
     * Make table menu button visible and replace the context menu with a custom context menu via reflection.
     * The preferred height is modified so that an empty header row remains visible. This is needed in case you remove all columns, so that the menu button won't disappear with the row header.
     * IMPORTANT: Modification is only possible AFTER the table has been made visible, otherwise you'd get a NullPointerException
     *
     * @param tableView TableView it is
     */
    public void addCustomTableMenu(TableView tableView) {

        // enable table menu
        tableView.setTableMenuButtonVisible(true);

        // replace internal mouse listener with custom listener
        setCustomContextMenu(tableView);
    }


    private void setCustomContextMenu(TableView table) {

        TableViewSkin<?> tableSkin = (TableViewSkin<?>) table.getSkin();

        // get all children of the skin
        ObservableList<Node> children = tableSkin.getChildren();

        // find the TableHeaderRow child
        for (Node node : children) {
            if (node instanceof TableHeaderRow) {
                TableHeaderRow tableHeaderRow = (TableHeaderRow) node;

                // setting the preferred height for the table header row
                // if the preferred height isn't set, then the table header would disappear if there are no visible columns
                // and with it the table menu button
                // by setting the preferred height the header will always be visible
                // note: this may need adjustments in case you have different heights in columns (eg when you use grouping)
                double defaultHeight = tableHeaderRow.getHeight();
                tableHeaderRow.setPrefHeight(defaultHeight);

                for (Node child : tableHeaderRow.getChildren()) {
                    // child identified as cornerRegion in TableHeaderRow.java
                    if (child.getStyleClass().contains("show-hide-columns-button")) {
                        // get the context menu
                        ContextMenu columnPopupMenu = createContextMenu(table);

                        // replace mouse listener
                        child.setOnMousePressed(me -> {
                            // show a popupMenu which lists all columns
                            columnPopupMenu.show(child, Side.BOTTOM, 0, 0);
                            me.consume();
                        });
                    }
                }
            }
        }
    }


    public static void showTableVisibleColumns(TableView table) {
        List<String> columns = ConfigReader.getTableColumnsArray();
        for (Object tableColumn : table.getColumns()) {
            TableColumn<?, ?> tc = (TableColumn<?, ?>) tableColumn;
            if (columns.contains(tc.getText()))
                tc.setVisible(true);
            else
                tc.setVisible(false);
        }
    }

    public static Label createNewContextMenuItem(String labelName, ContextMenu contextMenu) {
        Label label = new Label(labelName);
        label.setMnemonicParsing(false);

        CustomMenuItem customMenuItem = new CustomMenuItem(label);
        customMenuItem.setHideOnClick(false);
        contextMenu.getItems().add(customMenuItem);
        return label;
    }

    /**
     * Create a menu with custom items. The important thing is that the menu remains open while you click on the menu items.
     *
     * @param table TableView it is
     */
    private static ContextMenu createContextMenu(TableView table) {
        ContextMenu cm = new ContextMenu();

        // create new context menu
        CustomMenuItem cmi;

        Label saveConf = createNewContextMenuItem("Save current TableView", cm);
        saveConf.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            StringBuilder visibleColumns = new StringBuilder();
            for (Object tableColumn : table.getColumns()) {
                TableColumn tC = (TableColumn) tableColumn;
                if (tC.isVisible()) {
                    if (!visibleColumns.toString().isEmpty()) visibleColumns.append(",");
                    visibleColumns.append(tC.getText());
                }
            }

            try {
                ConfigReader.setTableView(visibleColumns.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            CommonUtils.displayMessage("Save Successful!", "Awesome!\nThe current Table View has been saved successfully.", "");
        });

        Label getSavedConf = createNewContextMenuItem("Get saved TableView", cm);
        getSavedConf.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showTableVisibleColumns(table));

        // separator
        cm.getItems().add(new SeparatorMenuItem());

        // select all item
        Label showAll = createNewContextMenuItem("Show all", cm);
        showAll.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            for (Object obj : table.getColumns()) {
                TableColumn tc = (TableColumn<?, ?>) obj;
                if (!tc.getText().equals("ID") && !tc.getText().equals("Photo"))
                    tc.setVisible(true);
            }
        });

        // deselect all item
        Label hideAll = createNewContextMenuItem("Hide all", cm);
        hideAll.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            for (Object obj : table.getColumns()) {
                ((TableColumn<?, ?>) obj).setVisible(false);
            }
        });

        // separator
        cm.getItems().add(new SeparatorMenuItem());

        // menu item for each of the available columns
        for (Object obj : table.getColumns()) {
            TableColumn<?, ?> tableColumn = (TableColumn<?, ?>) obj;
            if (!tableColumn.getText().equals("S.N") && !tableColumn.getText().equals("ID") && !tableColumn.getText().equals("Photo")) {
                CheckBox cb = new CheckBox(tableColumn.getText());
                cb.selectedProperty().bindBidirectional(tableColumn.visibleProperty());
                cb.selectedProperty().addListener((ob, ov, nv) -> {
                    if (nv) {
                        if (!memberFields.contains(cb.getText())) {
                            memberFields.add(cb.getText());
                        }
                    } else {
                        fieldsCB.getCheckModel().clearCheck(cb.getText());
                        memberFields.remove(cb.getText());
                    }
                    List<Integer> checkedFields = fieldsCB.getCheckModel().getCheckedIndices();
                    fieldsCB.getItems().clear();
                    memberFields.remove("Edit");
                    fieldsCB.getItems().addAll(memberFields);
                    checkedFields.forEach(i -> fieldsCB.getCheckModel().check(i));
                });
                cmi = new CustomMenuItem(cb);
                cmi.setHideOnClick(false);
                cm.getItems().add(cmi);
            }
        }
        return cm;
    }

}