package com.sujan.controller;

import com.sujan.bean.Member;
import com.sujan.utils.CommonUtils;
import com.sujan.utils.ConfigReader;
import com.sujan.utils.export.PdfExport;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SoloMemberDisplayController {

    private Stage primaryStage;
    private Member soloMember;

    @FXML
    private BorderPane solorMemberPane;

    @FXML
    private VBox namePane;

    @FXML
    private Circle churchLogo, memberImage;

    @FXML
    private Label churchName, churchAddress, churchContact, mId, mFirstName, mLastName, mPhone, mEmail, mGender, mMaritalStatus, mAbout, mAddress, mArea, mFather, mMother, mSpouse, mDOB, mDOM, mDOD, mCurrentDate;


    public Node getNode() {
        return solorMemberPane;
    }

    void populateFields(Member member, Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.soloMember = member;

        try {
            String imageName = CommonUtils.getImagePath() + member.getPhoto();
            Image image;
            if (new File(imageName).exists() && new File(imageName).isFile()) {
                image = new Image(new File(imageName).toURI().toURL().toString());
            } else {
                image = new Image(getClass().getResourceAsStream("/images/profileIcon.png"));
            }
            if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
                churchLogo.setFill(new ImagePattern(CommonUtils.getLogoImage()));
                memberImage.setFill(new ImagePattern(image));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        churchName.setText(ConfigReader.getSystemName());
        churchAddress.setText(ConfigReader.getSystemAddress());
        churchContact.setText(ConfigReader.getSystemContact());

        mId.setText(member.getMemberId());
        mFirstName.setText(member.getFirstName());
        if (!member.getMiddleName().isEmpty()) {
            Label middleName = new Label(member.getMiddleName());
            middleName.setFont(new Font(24));
            namePane.getChildren().add(2, middleName);
        }
        mLastName.setText(member.getLastName());
        mPhone.setText(member.getPhone());
        mEmail.setText(member.getEmail());
        mGender.setText(member.getGender().equals("M") ? "Male" : (member.getGender().equals("F") ? "Female" : "Other"));
        mMaritalStatus.setText(member.getMaritalStatus());
        mAbout.setText(ifEmptyAppendThis(member.getAbout()));
        mAddress.setText(member.getAddress());
        mArea.setText(ifEmptyAppendThis(member.getArea()));
        mFather.setText(member.getFatherName());
        mMother.setText(member.getMotherName());
        mSpouse.setText(ifEmptyAppendThis(member.getSpouseName()));
        mDOB.setText(CommonUtils.makeDateEmptyIfNull(member.getDob()));
        mDOM.setText(CommonUtils.makeDateEmptyIfNull(member.getDom()));
        mDOD.setText(ifEmptyAppendThis(CommonUtils.makeDateEmptyIfNull(member.getDod())));

        mCurrentDate.setText(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date()));
    }


    public void savePdf() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Pdf Save Directory");
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            String pdfName = selectedDirectory.getAbsolutePath() + File.separator + soloMember.getFirstName() + "-" + soloMember.getMemberId() + ".pdf";

            PdfExport pdfExport = new PdfExport();
            boolean isPdfSaved = pdfExport.exportPDF(getNode(), pdfName);
            if (isPdfSaved) {
                CommonUtils.displayMessage("Pdf Saved Successfully!", "Member " + mId.getText() + " details has been saved in pdf format.", "");
                primaryStage.close();
            }
        }
    }

    private String ifEmptyAppendThis(String s) {
        if (s == null || s.isEmpty()) return "-";
        return s;
    }
}
