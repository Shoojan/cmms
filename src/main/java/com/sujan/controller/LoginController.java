package com.sujan.controller;


import com.sujan.bean.Admin;
import com.sujan.config.StageManager;
import com.sujan.service.AdminService;
import com.sujan.utils.CommonUtils;
import com.sujan.utils.SystemWizard;
import com.sujan.utils.shakydialog.ShakeTransition;
import com.sujan.view.FxmlView;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Sujan Maharjan
 * @since 02-25-2019
 */

@Controller
public class LoginController implements Initializable {

    @FXML
    private AnchorPane loginRoot;

    @FXML
    private PasswordField password;

    @FXML
    private TextField adminName;

    @FXML
    private Label lblLogin;

    @FXML
    private ImageView loginImage;

    @FXML
    private Circle churchLogo;

    @Autowired
    private AdminService adminService;

    @Lazy
    @Autowired
    private StageManager stageManager;

    @FXML
    private void login() {
        if (adminService.authenticate(getAdminName(), getPassword())) {
            stageManager.switchScene(FxmlView.MEMBER);
        } else {
            ShakeTransition anim = new ShakeTransition(loginRoot, null);
            anim.playFromStart();

            lblLogin.setTextFill(Color.web("#f41e1e"));
            lblLogin.setText("Login Failed.");
        }
    }

    private String getPassword() {
        return password.getText();
    }

    private String getAdminName() {
        return adminName.getText();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loginImage.setImage(new Image(getClass().getResourceAsStream("/images/church.png")));
            if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP))
                churchLogo.setFill(new ImagePattern(CommonUtils.getLogoImage()));
            churchLogo.setSmooth(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Admin> admins = adminService.getAllAdmins();
        if (admins.isEmpty()) {
            SystemWizard systemWizard = new SystemWizard();
            systemWizard.showLinearWizard(admins, adminService);
        }
    }

}
