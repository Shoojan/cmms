package com.sujan.controller;

import com.sujan.bean.Admin;
import com.sujan.bean.Member;
import com.sujan.config.StageManager;
import com.sujan.service.AdminService;
import com.sujan.service.MemberService;
import com.sujan.utils.*;
import com.sujan.utils.export.ExcelExport;
import com.sujan.utils.backup.FileBackup;
import com.sujan.utils.imageprocess.ImageCrop;
import com.sujan.utils.imageprocess.WebCamLauncher;
import com.sujan.utils.nepalicalender.NepaliDateConverter;
import com.sujan.utils.shakydialog.ShakyLoginDialog;
import com.sujan.view.FxmlView;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.ProgressDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @author Sujan Maharjan
 * @since 02-25-2019
 */

@Controller
public class MemberController implements Initializable {

    public MenuBar menu;
    public ToggleGroup maritalStatus_TG, gender;
    @FXML
    private Button cameraBtn;

    @FXML
    private Circle churchLogo;

    @FXML
    private Label uniqueId, agelbl, churchName, churchAddress, churchContact;

    @FXML
    private TextField memberId, firstName, middleName, lastName, fatherName, motherName, spouseName, address, phone, email, area;

    @FXML
    private TextArea about;

    @FXML
    private TextField searchTF, dobYear, dobDay, domYear, domDay, dodYear, dodDay;

    @FXML
    private RadioButton rbMale, rbFemale, rbOther, rbSingle, rbMarried;

    @FXML
    private Circle imageCircle;

    @FXML
    private ComboBox<String> cb_AgeGroup, exportCB, dobMonth, domMonth, dodMonth;

    @FXML
    private CheckComboBox<String> fieldsCB;

    @FXML
    private Button addBtn, expandBtn, saveMember;

    @FXML
    private HBox slider;

    @FXML
    private VBox imgVBox;

    @FXML
    private ScrollPane sliderContent;

    @FXML
    private TableView<Member> userTable;

    @FXML
    private TableColumn<Member, Long> colId;

    @FXML
    private TableColumn<Member, String> colMemberId,
            colFirstName,
            colMiddleName,
            colLastName,
            colAddress,
            colFatherName,
            colMotherName,
            colSpouseName,
            colMaritalStatus,
            colPhone,
            colEmail,
            colGender,
            colArea,
            colAbout,
            colAgeGroup,
            colPhoto;

    @FXML
    private TableColumn<Member, LocalDate> colDOB, colDOD, colDOM;

    @FXML
    private TableColumn<Member, Boolean> colEdit;

    @FXML
    private TitledPane moreDetailsTP, mmbersTP;

    @FXML
    private MenuItem adminSetting_Btn, about_Btn, backupBtn, restoreBackupBtn, exitBtn;

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AdminService adminService;

    private ObservableList<Member> memberList = FXCollections.observableArrayList();
    private ObservableList<String> ageGroup = FXCollections.observableArrayList("12-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90-99", "100+");

    private ObservableList<String> memberFields = FXCollections.observableArrayList(getSearchFields());

    private NepaliDateConverter nepaliDateConverter = new NepaliDateConverter();

    private List<String> getSearchFields() {
        List<String> searchFields = new ArrayList<>();
        ConfigReader.getTableColumnsArray().forEach(field -> {
            if (!field.equals("S.N") && !field.equals("Edit"))
                searchFields.add(field);
        });
        return searchFields;
    }

    private Validation validation;
    private Member soloMember;

    /**
     * Logout and go to the login page
     */
    @FXML
    private void logout() {
        boolean confirmLogOut = CommonUtils.confirmMessage("Logout Confirmation!", null, "Are you sure?");
        if (confirmLogOut)
            stageManager.switchScene(FxmlView.LOGIN);
    }

    @FXML
    void reset() {
        clearFields();
    }

//    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//    private static final String NUMERIC_STRING = "123456789";
//
//    public static String randomAlphaNumeric(int count) {
//        StringBuilder builder = new StringBuilder();
//        while (count-- != 0) {
//            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
//            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
//        }
//        return builder.toString();
//    }
//
//    public static String randomNumeric(int count) {
//        StringBuilder builder = new StringBuilder();
//        while (count-- != 0) {
//            int character = (int) (Math.random() * NUMERIC_STRING.length());
//            builder.append(NUMERIC_STRING.charAt(character));
//        }
//        return builder.toString();
//    }
//
//    public static final LocalDate LOCAL_DATE(String dateString) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        return LocalDate.parse(dateString, formatter);
//    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        for (int i = 1; i < 100; i++) {
//            AtomicLong currentIncrementedId = new AtomicLong(memberService.getRecentId());
//            Member member = new Member();
//            member.setId(currentIncrementedId.incrementAndGet());
//            member.setMemberId("Id-" + i);
//            member.setFirstName("First" + randomAlphaNumeric(4));
//            member.setMiddleName("Middle" + randomAlphaNumeric(2));
//            member.setLastName("Last" + randomAlphaNumeric(4));
//            member.setFatherName("Father " + randomAlphaNumeric(5));
//            member.setMotherName("MotherName" + randomAlphaNumeric(5));
//            member.setAddress("Address " + randomAlphaNumeric(5));
//            member.setPhone("9841" + randomNumeric(6));
//            member.setEmail(randomAlphaNumeric(6) + "@b.com");
//            member.setDob(LOCAL_DATE("20" + randomNumeric(2) + "-0" + randomNumeric(1) + "-0" + randomNumeric(1)));
//            member.setDod(null);
//            member.setDom(LOCAL_DATE("20" + randomNumeric(2) + "-0" + randomNumeric(1) + "-0" + randomNumeric(1)));
//            member.setAgeGroup(getAgeGroup());
//            boolean flag = false;
//            for (int c = 2; c <= i / 2; ++c) {
//                // condition for nonprime number
//                if (i % c == 0) {
//                    flag = true;
//                    break;
//                }
//            }
//            if (!flag) {
//                member.setGender("M");
//                if (i % 2 == 0) {
//                    member.setMaritalStatus("Married");
//                    member.setSpouseName("Spouse" + randomAlphaNumeric(5));
//                } else member.setMaritalStatus("Single");
//
//            } else {
//                if (i % 2 == 0) member.setGender("F");
//                else member.setGender("O");
//                if (i % 2 == 0) member.setMaritalStatus("Single");
//                else {
//                    member.setMaritalStatus("Married");
//                    member.setSpouseName("Spouse" + randomAlphaNumeric(5));
//                }
//            }
//            member.setAbout("Hi, I generated random string as " + randomAlphaNumeric(10));
//            member.setArea(String.valueOf(i));
//
//            Member newMember = memberService.save(member);
//        }

        //fill Church Details
        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP))
            churchLogo.setFill(new ImagePattern(CommonUtils.getLogoImage()));
        churchName.setText(ConfigReader.getSystemName());
        churchAddress.setText(ConfigReader.getSystemAddress());
        churchContact.setText(ConfigReader.getSystemContact());

        expandBtn.setTextFill(Color.WHITE);
        initializeExportButtons();

        initializeDate();

        setImage("");

        cb_AgeGroup.setItems(ageGroup);
        new AutoCompleteComboBoxListener(cb_AgeGroup);

        fieldsCB.getItems().addAll(memberFields);

        //make spouse Name TextField Visible with the member is single.
        spouseName.disableProperty().bind(rbSingle.selectedProperty());

        addCustomContextMenu();
        initializeColumnListeners();
        userTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setColumnProperties();

        // Add all members into table
        loadMemberDetails();

        addBtn.setOnAction(event -> {
            clearFields();
            if (expandBtn.getText().equals(">")) {
                expandBtn.fire();
            }
        });

        // force the field to be numeric only
        phone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phone.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        area.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                area.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        /* ***** Panel Slide Animation ***** */
        // start out of view
        slider.setTranslateX(-sliderContent.getPrefWidth());
        StackPane.setAlignment(slider, Pos.CENTER_LEFT);

        // animation for moving the slider
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(slider.translateXProperty(), -sliderContent.getPrefWidth())),
                new KeyFrame(Duration.millis(500), new KeyValue(slider.translateXProperty(), 0d))
        );

        expandBtn.setOnAction(evt -> {
            // adjust the direction of play and start playing, if not already done
            String text = expandBtn.getText();
            boolean playing = timeline.getStatus() == Animation.Status.RUNNING;
            if (">".equals(text)) {
                timeline.setRate(1);
                if (!playing) {
                    timeline.playFromStart();
                }
                expandBtn.setText("<");
            } else {
                timeline.setRate(-1);
                if (!playing) {
                    timeline.playFrom("end");
                }
                expandBtn.setText(">");
            }
        });

        /* ***** For Data Search in entire TableView. ***** */
        FilteredList<Member> filteredList = new FilteredList<>(memberList, e -> true);
        searchTF.setOnKeyPressed(e -> {
            searchTF.textProperty().addListener((obsValue, oldValue, newValue) -> filteredList.setPredicate((Predicate<? super Member>) member -> {
                //whenever the new value is empty or null, add all memberList to filteredList
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                List<String> searchFieldLists = new ArrayList<>();
                if (fieldsCB.getCheckModel().getCheckedItems() == null || fieldsCB.getCheckModel().getCheckedItems().isEmpty()) {
                    searchFieldLists.addAll(memberFields);
                } else {
                    searchFieldLists.addAll(fieldsCB.getCheckModel().getCheckedItems());
                }
                for (String memberField : searchFieldLists) {
                    boolean check = searchFoundInSpecificField(memberField, member, lowerCaseFilter);
                    if (check)
                        return true;
                }
                return false;
            }));

            SortedList<Member> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(userTable.comparatorProperty());
            userTable.setItems(sortedList);
        });
        searchTF.setOnKeyReleased(e -> mmbersTP.setText(userTable.getItems().size() + " Members"));

        fieldsCB.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            System.out.println(fieldsCB.getCheckModel().getCheckedItems());
            String searchedData = searchTF.getText();
            searchTF.setText(null);
            searchTF.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, searchedData, searchedData, KeyCode.UNDEFINED, false, false, false, false));
            searchTF.setText(searchedData);
            searchTF.fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.UNDEFINED, false, false, false, false));
        });

        uniqueId.textProperty().addListener((ob, ov, nv) -> {
            if (nv == null || nv.isEmpty()) {
                saveMember.setText("Save");
            } else {
                saveMember.setText("Update");
            }
        });

        validation = new Validation(moreDetailsTP);

        // enable table menu button and add a custom menu to it
        TableUtils.showTableVisibleColumns(userTable);
        Platform.runLater(() -> {
            TableUtils tableUtils = new TableUtils(fieldsCB, memberFields);
            tableUtils.addCustomTableMenu(userTable);
        });
    }

    private void initializeDate() {

        dobMonth.setItems(FXCollections.observableArrayList(NepaliDateConverter.VALID_NEPALI_MONTHS));
        domMonth.setItems(FXCollections.observableArrayList(NepaliDateConverter.VALID_NEPALI_MONTHS));
        dodMonth.setItems(FXCollections.observableArrayList(NepaliDateConverter.VALID_NEPALI_MONTHS));

        final UnaryOperator<TextFormatter.Change> filterYear = (TextFormatter.Change change) -> {
            try {
                if (change.getControlNewText().length() > 4) {
                    return null;
                }
                if (!change.getText().matches("\\d*")) {
                    change.setText(change.getText().replaceAll("[^\\d]", ""));
                }
            } catch (Exception ignored) {
            }
            return change;
        };

        final UnaryOperator<TextFormatter.Change> filterDay = (TextFormatter.Change change) -> {
            try {
                if (change.getControlNewText().length() > 2) {
                    return null;
                }
                if (!change.getText().matches("\\d*")) {
                    change.setText(change.getText().replaceAll("[^\\d]", ""));
                }
            } catch (Exception ignored) {
            }
            return change;
        };

        dobYear.setTextFormatter(new TextFormatter<>(filterYear));
        dobDay.setTextFormatter(new TextFormatter<>(filterDay));
        domYear.setTextFormatter(new TextFormatter<>(filterYear));
        domDay.setTextFormatter(new TextFormatter<>(filterDay));
        dodYear.setTextFormatter(new TextFormatter<>(filterYear));
        dodDay.setTextFormatter(new TextFormatter<>(filterDay));

        dobYear.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                if ((newValue.length() == 4) && dobMonth.getSelectionModel().getSelectedItem() != null && !Objects.requireNonNull(dobDay.getText()).isEmpty()) {
                    nepaliDateConverter = setNepaliDate("dob");
                    if (nepaliDateConverter != null) {
                        calculateAgeGetAgeGroup(LocalDate.parse(nepaliDateConverter.toEnglishString()));
                    }
                }
        });
        dobDay.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                if ((dobYear.getText() != null && dobYear.getText().length() == 4) && dobMonth.getSelectionModel().getSelectedItem() != null && !Objects.requireNonNull(dobDay.getText()).isEmpty()) {
                    nepaliDateConverter = setNepaliDate("dob");
                    if (nepaliDateConverter != null) {
                        calculateAgeGetAgeGroup(LocalDate.parse(nepaliDateConverter.toEnglishString()));
                    }
                }
        });
        dobMonth.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if ((dobYear.getText() != null && dobYear.getText().length() == 4) && dobMonth.getSelectionModel().getSelectedItem() != null && !Objects.requireNonNull(dobDay.getText()).isEmpty()) {
                nepaliDateConverter = setNepaliDate("dob");
                if (nepaliDateConverter != null) {
                    calculateAgeGetAgeGroup(LocalDate.parse(nepaliDateConverter.toEnglishString()));
                }
            }
        });
    }

    private NepaliDateConverter setNepaliDate(String dateType) {
        NepaliDateConverter nepaliDateConverter = new NepaliDateConverter();
        try {
            int day, month, year;
            switch (dateType) {
                case "dob":
                    day = Integer.parseInt(dobDay.getText());
                    month = dobMonth.getSelectionModel().getSelectedIndex() + 1;
                    year = Integer.parseInt(dobYear.getText());
                    break;
                case "dom":
                    day = Integer.parseInt(domDay.getText());
                    month = domMonth.getSelectionModel().getSelectedIndex() + 1;
                    year = Integer.parseInt(domYear.getText());
                    break;
                case "dod":
                    day = Integer.parseInt(dodDay.getText());
                    month = dodMonth.getSelectionModel().getSelectedIndex() + 1;
                    year = Integer.parseInt(dodYear.getText());
                    break;
                default:
                    return null;
            }
            nepaliDateConverter.setNepaliDate(year, month, day);
        } catch (Exception e) {
            return null;
        }
        return nepaliDateConverter;
    }

    //automatically calculate age of the member and set the age group accordingly
    private void calculateAgeGetAgeGroup(LocalDate date) {
        int age = calculateAge(date, LocalDate.now());
        AtomicReference<String> ageRange = new AtomicReference<>("");
        ageGroup.stream().anyMatch(r -> {
            if (r.contains("+")) {
                if (Integer.parseInt(r.replace("+", "")) <= age) {
                    ageRange.set(r);
                    return true;
                }
            } else {
                String[] range = r.split("-");
                if (Integer.parseInt(range[0]) <= age && Integer.parseInt(range[1]) >= age) {
                    ageRange.set(r);
                    return true;
                }
            }
            return false;
        });
        agelbl.setText("Age: " + age);
        cb_AgeGroup.setValue(ageRange.get());
    }

    private int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    private void initializeColumnListeners() {
        listen(colMemberId);
        listen(colFirstName);
        listen(colMiddleName);
        listen(colLastName);
        listen(colAddress);
        listen(colFatherName);
        listen(colMotherName);
        listen(colSpouseName);
        listen(colMaritalStatus);
        listen(colPhone);
        listen(colEmail);
        listen(colGender);
        listen(colArea);
        listen(colAbout);
        listen(colAgeGroup);
        listen(colDOB);
        listen(colDOD);
        listen(colDOM);
    }

    private void initializeExportButtons() {
        exportCB.getItems().addAll("Filtered Members", "All Members");

        // Set a cell factory for the ComboBox.
        exportCB.setCellFactory(lv ->
                new ListCell<String>() {
                    // This is the node that will display the text and the cross.
                    // I chose a hyperlink, but you can change to button, image, etc.
                    private HBox graphic;

                    // this is the constructor for the anonymous class.
                    {
                        Label label = new Label();
                        // Bind the label text to the item property. If your ComboBox items are not Strings you should use a converter.
                        label.textProperty().bind(itemProperty());
                        // Set max width to infinity so the cross is all the way to the right.
                        label.setMaxWidth(Double.POSITIVE_INFINITY);
                        // We have to modify the hiding behavior of the ComboBox to allow clicking on the hyperlink,
                        // so we need to hide the ComboBox when the label is clicked (item selected).
                        label.setOnMouseClicked(event -> exportCB.hide());

                        Button btnExport = new Button();
                        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
                        icon.setSize("15.0");
                        btnExport.setTextFill(Color.web("#555555"));
                        btnExport.setGraphic(icon);
                        btnExport.setOnAction(event ->
                                {
                                    switch (label.getText()) {
                                        case "Filtered Members":
                                            onExportExcelButtonClicked();
                                            break;
                                        case "All Members":
                                            searchTF.setText(null);
                                            searchTF.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UNDEFINED, false, false, false, false));
                                            searchTF.setText("");
                                            searchTF.fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.UNDEFINED, false, false, false, false));
                                            onExportExcelButtonClicked();
                                            break;
                                    }
                                }
                        );

                        // Arrange controls in a HBox, and set display to graphic only (the text is included in the graphic in this implementation).
                        graphic = new HBox(label, btnExport);
                        HBox.setMargin(btnExport, new Insets(0, 5, 0, 0));
                        HBox.setHgrow(label, Priority.ALWAYS);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(graphic);
                        }
                    }
                });

        // We have to set a custom skin, otherwise the ComboBox disappears before the click on the Hyperlink is registered.
        exportCB.setSkin(new ComboBoxListViewSkin<String>(exportCB) {
            @Override
            protected boolean isHideOnClickEnabled() {
                return false;
            }
        });
    }


    private void listen(TableColumn column) {
        column.visibleProperty().addListener((ob, ov, nv) -> {
            String value = column.getText();
            if (!value.equals("ID") && !value.equals("Photo") && !value.equals("Edit")) {
                if (column.isVisible()) {
                    if (!memberFields.contains(value))
                        memberFields.add(value);
                } else {
                    memberFields.remove(value);
                }
            }
        });
    }

    private void addCustomContextMenu() {
        // add context menu
        ContextMenu cm = new ContextMenu();

        Label viewPdf = TableUtils.createNewContextMenuItem("View PDF", cm);
        viewPdf.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            List<Member> memberList = userTable.getSelectionModel().getSelectedItems();
            if (memberList.size() > 1) {
                CommonUtils.displayMessage("Multiple Members Detected!", "Can't show PDF for multiple members at once. Please try seleting single Member.", "warning");
            } else {
                Member member = memberList.get(0);
                updateMember(member);
                onExportPdfButtonClicked();
                clearFields();
            }
        });

        // separator
        cm.getItems().add(new SeparatorMenuItem());

        Label addMenu = TableUtils.createNewContextMenuItem("Add New Member", cm);
        addMenu.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            clearFields();
            if (expandBtn.getText().equals(">")) {
                expandBtn.fire();
            }
        });

        Label deleteMenu = TableUtils.createNewContextMenuItem("Delete", cm);
        deleteMenu.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            List<Member> memberList = userTable.getSelectionModel().getSelectedItems();
            String s = memberList.size() > 1 ? "s" : "";
            boolean confirmDelete = CommonUtils.confirmMessage("Are you sure?", null, "Are you sure you want to delete the selected member" + s + " ?");
            if (confirmDelete) {
                memberService.deleteInBatch(memberList);
                for (Member member : memberList)
                    CommonUtils.deleteFile(new File(CommonUtils.getImagePath() + member.getPhoto()));
                clearFields();
            }
            loadMemberDetails();
        });

        // set context menu
        userTable.setContextMenu(cm);
    }

    @FXML
    private void menuItem_Clicked(ActionEvent actionEvent) {

        if (actionEvent.getSource() == adminSetting_Btn) {
            List<Admin> admins = adminService.getAllAdmins();

            ShakyLoginDialog shakyLoginDialog = new ShakyLoginDialog(admins.get(0));
            boolean validate = shakyLoginDialog.display();

            if (validate) {
                SystemWizard systemWizard = new SystemWizard();
                boolean[] changes = systemWizard.showLinearWizard(admins, adminService);
                if (changes[0])
                    if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP))
                        churchLogo.setFill(new ImagePattern(CommonUtils.getLogoImage()));
                if (changes[1])
                    if (CommonUtils.confirmMessage(
                            "Setup Complete!",
                            "Please restart the program to reflect the changes.",
                            "Do you want to exit the program now?")) {
                        Platform.exit();
                    }
            }
        }

        //Backup (ctrl + B)
        else if (actionEvent.getSource() == backupBtn) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("CMMS Backup Path");
            File selectedDirectory = directoryChooser.showDialog(null);
            if (selectedDirectory != null) {
                FileBackup fileBackup = new FileBackup(selectedDirectory.getAbsolutePath());
                fileBackup.startBackupProcess();
            }
        }

        //Restore Data from Backup (ctrl + Shift + B)
        else if (actionEvent.getSource() == restoreBackupBtn) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("CMMS Backup File");
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Zip File", "*.zip");
            fileChooser.getExtensionFilters().add(filter);
            File selectedDirectory = fileChooser.showOpenDialog(null);
            if (selectedDirectory != null) {
                FileBackup fileBackup = new FileBackup(selectedDirectory);
                fileBackup.restoreFromBackup();
            }
        }

        //Exit
        else if (actionEvent.getSource() == exitBtn) {
            if (CommonUtils.confirmMessage("Exit Confirmation!", null, "Are you sure you want to exit the program?"))
                Platform.exit();
        }

        //About
        else if (actionEvent.getSource() == about_Btn) {
            CommonUtils.displayMessage(
                    "Information",
                    "System\t\t: Church Member Management System(CMMS)\n" +
                            "Developer\t: Sujan Maharjan\n" +
                            "Email\t\t: sujan.mhrzn2@gmail.com",
                    "");
        }
    }

    private void clearFields() {
        uniqueId.setText(null);
        memberId.setText(null);
        agelbl.setText(null);

        firstName.clear();
        middleName.clear();
        lastName.clear();
        fatherName.clear();
        motherName.clear();
        spouseName.clear();
        address.clear();
        phone.clear();
        email.clear();
        area.clear();
        about.clear();

        setImage("");


        dobMonth.getSelectionModel().clearSelection();
        domMonth.getSelectionModel().clearSelection();
        dodMonth.getSelectionModel().clearSelection();

        dobYear.setText(null);
        domYear.setText(null);
        dodYear.setText(null);

        dobDay.setText(null);
        domDay.setText(null);
        dodDay.setText(null);

        rbSingle.setSelected(true);
        rbMarried.setSelected(false);

        rbMale.setSelected(true);
        rbFemale.setSelected(false);
        rbOther.setSelected(false);

        cb_AgeGroup.getSelectionModel().clearSelection();

        soloMember = null;
    }

    @FXML
    private void saveMember() {
        if (validation.emptyValidation(memberId, "Member Id", CommonUtils.makeEmptyIfNull(getMemberId()).isEmpty()) &&
                validation.validate(firstName, "First Name", getFirstName(), "[a-zA-Z]+") &&
                validation.validate(lastName, "Last Name", getLastName(), "[a-zA-Z]+") &&
                (getEmail() == null || getEmail().isEmpty() || validation.validate(email, "Email", getEmail(), "[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+")) &&
                validation.emptyValidation(address, "Address", getAddress().isEmpty()) &&
                validation.emptyValidation(dobYear, "Date of Birth", getDob() == null) &&
                (getPhone() == null || getPhone().isEmpty() || validation.validate(phone, "Phone", getPhone(), "^[0-9]*$")) &&
                validation.validate(fatherName, "Father's Name", getFatherName(), "[a-zA-Z ]+") &&
                validation.validate(motherName, "Mother's Name", getMotherName(), "[a-zA-Z ]+") &&
                (getSpouseName() == null || getSpouseName().isEmpty() || validation.validate(spouseName, "Spouse's Name", getSpouseName(), "[a-zA-Z ]+")) &&
                validation.emptyValidation(domYear, "Date of Membership", getDom() == null) &&
                validation.emptyValidation(cb_AgeGroup, "Age Group", getAgeGroup() == null)) {

            if (uniqueId.getText() == null || uniqueId.getText().isEmpty()) {

                AtomicLong currentIncrementedId = new AtomicLong(memberService.getRecentId());

                Member member = new Member();
                setParameter(member, currentIncrementedId.incrementAndGet());

                Member newMember = memberService.save(member);
                showAlert(newMember, true);

            } else {
                Member member = memberService.find(getUniqueId());
                setParameter(member, getUniqueId());

                Member updatedMember = memberService.update(member);
                showAlert(updatedMember, false);
            }

            loadMemberDetails();
        }


    }

    private void setParameter(Member member, long id) {
        member.setId(id);
        member.setMemberId(getMemberId());
        member.setFirstName(getFirstName());
        member.setMiddleName(getMiddleName());
        member.setLastName(getLastName());
        member.setFatherName(getFatherName());
        member.setMotherName(getMotherName());
        member.setSpouseName(getSpouseName());
        member.setAddress(getAddress());
        member.setPhone(getPhone());
        member.setEmail(getEmail());
        member.setDob(getDob());
        member.setDod(getDod());
        member.setDom(getDom());
        member.setGender(getGender());
        member.setMaritalStatus(getMaritalStatus());
        member.setAbout(getAbout());
        member.setArea(getArea());
        member.setAgeGroup(getAgeGroup());
        member.setPhoto(imageCircle.getUserData().toString());
    }

    private void showAlert(Member member, boolean isSave) {
        soloMember = member;

        String title = isSave ? "saved" : "updated";
        String content = isSave ? "Congratulations, " + member.getFirstName() + "." : "Member [" + member.getMemberId() + "-" + member.getFirstName() + "] has been updated.";
        CommonUtils.displayMessage("Member " + title + " successfully.", content, "");
        if (isSave) clearFields();
    }


    /*
     *  Set All memberTable column properties
     */
    private void setColumnProperties() {

        //for auto Numbering
        TableColumn<Member, Member> numberCol = new TableColumn<>("S.N");
        numberCol.setMinWidth(25);
        numberCol.setMaxWidth(25);
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper(p.getValue()));

        numberCol.setCellFactory(new Callback<TableColumn<Member, Member>, TableCell<Member, Member>>() {
            @Override
            public TableCell<Member, Member> call(TableColumn<Member, Member> param) {
                return new TableCell<Member, Member>() {
                    @Override
                    protected void updateItem(Member item, boolean empty) {
                        super.updateItem(item, empty);
                        if (this.getTableRow() != null && item != null) {
                            setText((this.getTableRow().getIndex() + 1) + "");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        numberCol.setSortable(false);
        numberCol.setVisible(true);
        userTable.getColumns().add(0, numberCol);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMiddleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colFatherName.setCellValueFactory(new PropertyValueFactory<>("fatherName"));
        colMotherName.setCellValueFactory(new PropertyValueFactory<>("motherName"));
        colSpouseName.setCellValueFactory(new PropertyValueFactory<>("spouseName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colMaritalStatus.setCellValueFactory(new PropertyValueFactory<>("maritalStatus"));

        colDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colDOD.setCellValueFactory(new PropertyValueFactory<>("dod"));
        colDOM.setCellValueFactory(new PropertyValueFactory<>("dom"));

        colDOD.setCellValueFactory(new PropertyValueFactory<>("dod"));
        colDOM.setCellValueFactory(new PropertyValueFactory<>("dom"));

        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colAbout.setCellValueFactory(new PropertyValueFactory<>("about"));

        colAgeGroup.setCellValueFactory(new PropertyValueFactory<>("ageGroup"));
        colPhoto.setCellValueFactory(new PropertyValueFactory<>("photo"));

        colEdit.setCellFactory(cellFactory);
    }

    private Callback<TableColumn<Member, Boolean>, TableCell<Member, Boolean>> cellFactory =
            new Callback<TableColumn<Member, Boolean>, TableCell<Member, Boolean>>() {
                @Override
                public TableCell<Member, Boolean> call(final TableColumn<Member, Boolean> param) {
                    return new TableCell<Member, Boolean>() {
                        final Button btnEdit = new Button();
                        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);

                        @Override
                        public void updateItem(Boolean check, boolean empty) {
                            super.updateItem(check, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                btnEdit.setOnAction(e -> {
                                    Member member = getTableView().getItems().get(getIndex());
                                    updateMember(member);
                                    if (expandBtn.getText().equals(">")) {
                                        expandBtn.fire();
                                    }
                                });

                                icon.setSize("15.0");
                                btnEdit.setTextFill(Color.web("#555555"));
                                btnEdit.setGraphic(icon);

                                setGraphic(btnEdit);
                                setAlignment(Pos.CENTER);
                                setText(null);
                            }
                        }

                    };
                }
            };

    private void updateMember(Member member) {
        uniqueId.setText(Long.toString(member.getId()));

        memberId.setText(member.getMemberId());
        firstName.setText(member.getFirstName());
        middleName.setText(member.getMiddleName());
        lastName.setText(member.getLastName());

        fatherName.setText(member.getFatherName());
        motherName.setText(member.getMotherName());
        spouseName.setText(member.getSpouseName());

        address.setText(member.getAddress());
        email.setText(member.getEmail());
        phone.setText(member.getPhone());
        about.setText(member.getAbout());
        area.setText(member.getArea());

        LocalDate dobString = member.getDob();
        if (dobString != null) {
            dobDay.setText(String.valueOf(dobString.getDayOfMonth()));
            dobMonth.getSelectionModel().select(dobString.getDayOfMonth() - 1);
            dobYear.setText(String.valueOf(dobString.getYear()));
        } else {
            dobDay.setText(null);
            dobMonth.getSelectionModel().select(null);
            dobYear.setText(null);
        }

        LocalDate domString = member.getDom();
        if (domString != null) {
            domDay.setText(String.valueOf(domString.getDayOfMonth()));
            domMonth.getSelectionModel().select(domString.getDayOfMonth() - 1);
            domYear.setText(String.valueOf(domString.getYear()));
        } else {
            domDay.setText(null);
            domMonth.getSelectionModel().select(null);
            domYear.setText(null);
        }

        LocalDate dodString = member.getDod();
        if (dodString != null) {
            dodDay.setText(String.valueOf(dodString.getDayOfMonth()));
            dodMonth.getSelectionModel().select(dodString.getDayOfMonth() - 1);
            dodYear.setText(String.valueOf(dodString.getYear()));
        } else {
            dodDay.setText(null);
            dodMonth.getSelectionModel().select(null);
            dodYear.setText(null);
        }

//        dob.setValue(member.getDob());
//        dod.setValue(member.getDod());
//        dom.setValue(member.getDom());

        setImage(CommonUtils.getImagePath() + member.getPhoto());

        switch (member.getGender()) {
            case "M":
                rbMale.setSelected(true);
                break;
            case "F":
                rbFemale.setSelected(true);
                break;
            case "O":
                rbOther.setSelected(true);
                break;
        }

        switch (member.getMaritalStatus()) {
            case "Single":
                rbSingle.setSelected(true);
                break;
            case "Married":
                rbMarried.setSelected(true);
                break;
        }

        cb_AgeGroup.getSelectionModel().select(member.getAgeGroup());
        soloMember = member;
    }


    /*
     *  Add All users to observable list and update table
     */
    private void loadMemberDetails() {
        memberList.clear();
        memberList.addAll(memberService.findAll());
        userTable.setItems(memberList);
        mmbersTP.setText(memberList.size() + " Members");
    }

    /***************************************** START: Search Operation *****************************************/
    private boolean searchFoundInSpecificField(String headerField, Member member, String lowerCaseFilter) {
        switch (headerField) {
            case "First Name":
                return searchFound(member.getFirstName(), lowerCaseFilter);
            case "Member ID":
                return searchFound(member.getMemberId(), lowerCaseFilter);
            case "Middle Name":
                return searchFound(member.getMiddleName(), lowerCaseFilter);
            case "Last Name":
                return searchFound(member.getLastName(), lowerCaseFilter);
            case "Father":
                return searchFound(member.getFatherName(), lowerCaseFilter);
            case "Mother":
                return searchFound(member.getMotherName(), lowerCaseFilter);
            case "Spouse":
                return searchFound(member.getSpouseName(), lowerCaseFilter);
            case "Marital Status":
                return searchFound(member.getMaritalStatus(), lowerCaseFilter);
            case "Address":
                return searchFound(member.getAddress(), lowerCaseFilter);
            case "Phone":
                return searchFound(member.getPhone(), lowerCaseFilter);
            case "Gender":
                return searchFound(member.getGender(), lowerCaseFilter);
            case "Email":
                return searchFound(member.getEmail(), lowerCaseFilter);
            case "Age Group":
                return searchFound(member.getAgeGroup(), lowerCaseFilter);
            case "About":
                return searchFound(member.getAbout(), lowerCaseFilter);
            case "Area":
                return searchFound(member.getArea(), lowerCaseFilter);
            case "Date of Birth":
                return searchFound(CommonUtils.makeDateEmptyIfNull(member.getDob()), lowerCaseFilter);
            case "Date of Membership":
                return searchFound(CommonUtils.makeDateEmptyIfNull(member.getDom()), lowerCaseFilter);
            case "Date of Baptism":
                return searchFound(CommonUtils.makeDateEmptyIfNull(member.getDod()), lowerCaseFilter);
            default:
                return false;
        }
    }

    private boolean searchFound(String field, String lowerCaseFilter) {
        try {
            if (field.toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    /* **************************************** END: Search Operation **************************************** */


    /***************************************** START: Excel File Export *****************************************/
    @FXML
    private void onExportPdfButtonClicked() {
        if (soloMember == null) {
            CommonUtils.displayMessage("Member Not Selected!", "Please select the member to print his/her details", "warn");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SoloMemberDisplay.fxml"));

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.centerOnScreen();

                SoloMemberDisplayController controller = loader.getController();
                controller.populateFields(soloMember, stage);

                stage.setOnCloseRequest(e -> {
                    stage.close();
                    e.consume();
                });

                stage.setOnShown(e -> {
                    stage.toFront();
                    stage.requestFocus();
                });

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onExportExcelButtonClicked() {
        //DO BACKGROUND WORK HERE
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export to Excel File");
        chooser.setInitialFileName("member_list.xlsx");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Excel File(XLSX)", "*.xlsx");
        chooser.getExtensionFilters().add(filter);
        File file = chooser.showSaveDialog(null);
        if (file != null) {

            DoubleProperty configTaskDone = new SimpleDoubleProperty(-1);
            StringProperty statusMessage = new SimpleStringProperty("No Task Running");

            Service<Double> service = new Service<Double>() {
                @Override
                protected Task<Double> createTask() {
                    return new Task<Double>() {
                        @Override
                        protected Double call() throws InterruptedException {

                            configTaskDone.addListener((ov, old_val, new_val) -> {
                                updateProgress((Double) new_val, 1);
                                if (isCancelled()) {
                                    cancel(true);
                                }
                            });
                            statusMessage.addListener((ov, oldValue, newValue) -> {
                                updateMessage("Data Export - " + newValue);
                                if (isCancelled()) {
                                    cancel(true);
                                }
                            });

                            statusMessage.setValue("Initializing Export");
                            configTaskDone.setValue(0.5);

                            Thread.sleep(300);

                            ExcelExport excelExport = new ExcelExport(userTable, file);
                            excelExport.export(configTaskDone, statusMessage);

                            return configTaskDone.getValue();
                        }

                        @Override
                        public void updateProgress(double workDone, double max) {
                            super.updateProgress(workDone / 100.0, max);
                        }

                        @Override
                        protected void succeeded() {
                            super.succeeded();
                            Platform.runLater(() ->
                                    Notifications.create()
                                            .title("CMMS Application")
                                            .text("Excel file exported successfully!")
                                            .showInformation()
                            );

                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().open(file);
                                } catch (IOException e) {
                                    System.out.println("ERROR: Couldn't open output directory.");
                                }
                            }
                        }
                    };
                }
            };

            ProgressDialog progDiag = new ProgressDialog(service);
            progDiag.setTitle("Excel Data Export");
            progDiag.setHeaderText("Exporting Excel file...");
            service.start();
        }
    }
    /* **************************************** END: Excel File Export **************************************** */


    /***************************************** START: Image Grabbing Process *****************************************/
    @FXML
    private void onCameraBtnClick() {

        boolean launchCamera = CommonUtils.confirmMessage("Image Capture", null, "Do you want to open the Camera?");
        if (launchCamera) {
            cameraBtn.setDisable(true);
            long number;
            if (uniqueId.getText() == null || uniqueId.getText().isEmpty()) {
                AtomicLong currentIncrementedId = new AtomicLong(memberService.getRecentId());
                number = currentIncrementedId.incrementAndGet();
            } else {
                number = Long.parseLong(uniqueId.getText());
            }
            String fullImageName = CommonUtils.getImagePath() + "member" + number + ".png";

            Task<Void> processTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    final CountDownLatch latch = new CountDownLatch(1);
                    ObservableList<Boolean> cancelGarneyHo = FXCollections.observableArrayList();
                    WebCamLauncher webCamLauncher = new WebCamLauncher(latch, fullImageName, imgVBox, cancelGarneyHo);
                    webCamLauncher.startCamera();

                    latch.await();
                    if (!cancelGarneyHo.isEmpty()) {
                        cancel(true);
                        cameraBtn.setDisable(false);
                    }
                    return null;
                }
            };

            processTask.setOnSucceeded(evt -> {
                // executed on fx application thread:
                setImage(fullImageName);
                cameraBtn.setDisable(false);
            });
            new Thread(processTask).start();

        }
    }

    @FXML
    private void onPhotoBtnClick() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Image File");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.png", "*.jpg", "*.jpeg"));

        File file = chooser.showOpenDialog(null);

        if (file != null) {
            try {
                String imagePath = file.toURI().toURL().toString();
                Image image = new Image(imagePath);

                long number;
                if (uniqueId.getText() == null || uniqueId.getText().isEmpty()) {
                    AtomicLong currentIncrementedId = new AtomicLong(memberService.getRecentId());
                    number = currentIncrementedId.incrementAndGet();
                } else {
                    number = Long.parseLong(uniqueId.getText());
                }
                String imageName = file.getName();
                String ext = imageName.substring(imageName.lastIndexOf("."));

                String fullImageName = CommonUtils.getImagePath() + "member" + number + ext;
                boolean cropImage = CommonUtils.confirmMessage(
                        "Image Cropping",
                        "Do you want to crop the Image?",
                        "Tips for better photo: \n1. Center the person while cropping.\n2. Try to get a square shape while cropping.");

                if (cropImage) {
                    Task<Void> processTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            final CountDownLatch latch = new CountDownLatch(1);
                            ObservableList<Boolean> cancelGarneyHo = FXCollections.observableArrayList();
                            ImageCrop imageCrop = new ImageCrop(imgVBox, image, fullImageName, latch, cancelGarneyHo);
                            imageCrop.startCropping();

                            latch.await();
                            if (!cancelGarneyHo.isEmpty()) {
                                cancel(true);
                            }
                            return null;
                        }
                    };

                    processTask.setOnSucceeded(evt -> {
                        // executed on fx application thread:
                        setImage(fullImageName);
                    });
                    new Thread(processTask).start();

                } else {
                    try {
                        File destImageFile = new File(fullImageName);
                        if (!destImageFile.exists()) {
                            destImageFile.getParentFile().mkdirs();
                            destImageFile.createNewFile();
                        }
                        FileSystemUtils.copyRecursively(file, destImageFile);
                        setImage(fullImageName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

    }

    private void setImage(String fullImageName) {
        try {

            Image image;
            if (fullImageName.isEmpty()) {
                image = new Image(getClass().getResourceAsStream("/images/profileIcon.png"));
            } else {
                if (new File(fullImageName).exists() && new File(fullImageName).isFile()) {
                    image = new Image(new File(fullImageName).toURI().toURL().toString());
                } else {
//                    CommonUtils.displayMessage("Image Not Found", "Image couldn't be found for this member. Please Add image,", "warn");
                    image = new Image(getClass().getResourceAsStream("/images/profileIcon.png"));
                }
            }

            if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP))
                imageCircle.setFill(new ImagePattern(image));
            imageCircle.setUserData(CommonUtils.getFileName(fullImageName));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    /* **************************************** END: Image Grabbing Process **************************************** */


    /***************************************** START: Getter Function for Member Fields *****************************************/
    private long getUniqueId() {
        return Long.parseLong(uniqueId.getText());
    }

    private String getMemberId() {
        return memberId.getText();
    }

    private String getFirstName() {
        return firstName.getText();
    }

    private String getMiddleName() {
        return middleName.getText();
    }

    private String getLastName() {
        return lastName.getText();
    }

    private String getFatherName() {
        return fatherName.getText();
    }

    private String getMotherName() {
        return motherName.getText();
    }

    private String getSpouseName() {
        return spouseName.getText();
    }

    private String getAddress() {
        return address.getText();
    }

    private String getPhone() {
        return phone.getText();
    }

    private String getEmail() {
        return email.getText();
    }

    private LocalDate getDob() {
        NepaliDateConverter nepaliDateConverter = setNepaliDate("dob");
        if (nepaliDateConverter != null)
            return LocalDate.parse(nepaliDateConverter.toNepaliString());
        return null;
    }

    private LocalDate getDod() {
        NepaliDateConverter nepaliDateConverter = setNepaliDate("dod");
        if (nepaliDateConverter != null)
            return LocalDate.parse(nepaliDateConverter.toNepaliString());
        return null;
    }

    private LocalDate getDom() {
        NepaliDateConverter nepaliDateConverter = setNepaliDate("dom");
        if (nepaliDateConverter != null)
            return LocalDate.parse(nepaliDateConverter.toNepaliString());
        return null;
    }

    private String getGender() {
        return rbMale.isSelected() ? "M" : (rbFemale.isSelected() ? "F" : "O");
    }

    private String getMaritalStatus() {
        return rbSingle.isSelected() ? "Single" : "Married";
    }

    private String getAgeGroup() {
        return cb_AgeGroup.getSelectionModel().getSelectedItem();
    }

    private String getArea() {
        return area.getText();
    }

    private String getAbout() {
        return about.getText();
    }

    /* **************************************** END: Getter Function for Member Fields  **************************************** */

}
