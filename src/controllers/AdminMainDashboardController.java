package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminMainDashboardController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private Button btnAdminStats;
    @FXML private Button btnUserManagement;
    @FXML private Button btnOrganizationManagement;
    @FXML private Button btnAdminFamilyManagement;
    @FXML private Button btnAidDistributions;
    @FXML private Button btnAdminChangePassword;
    @FXML private Button btnAdminLogout;
    @FXML private Label lblViewTitle;
    @FXML private Label lblViewSubtitle;
    @FXML private StackPane adminContentArea;
    @FXML private RadioMenuItem menuThemeLight;
    @FXML private RadioMenuItem menuThemeDark;
    @FXML private ToggleGroup fontSizeGroup;
    @FXML private ToggleGroup fontFamilyGroup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSubView("/views/AdminStats.fxml");
        fontSizeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && rootPane.getScene() != null) {
                String size = ((RadioMenuItem)newVal).getText();
                double fontSize = size.equals("Small") ? 10 : (size.equals("Large") ? 18 : 14);
                rootPane.getScene().getRoot().setStyle("-fx-font-size: " + fontSize + "px;");
            }
        });

        fontFamilyGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && rootPane.getScene() != null) {
                String font = ((RadioMenuItem)newVal).getText();
                rootPane.getScene().getRoot().setStyle(rootPane.getScene().getRoot().getStyle() + "-fx-font-family: '" + font + "';");
            }
        });
    }

    @FXML
    private void handleAdminNavigation(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        resetSidebarButtons();
        sourceButton.getStyleClass().add("nav-button-active");        

        if (sourceButton == btnAdminStats) {
            lblViewTitle.setText("Dashboard Overview");
            lblViewSubtitle.setText("System status and summary statistics");
            loadSubView("/views/AdminStats.fxml");
        } else if (sourceButton == btnUserManagement) {
            lblViewTitle.setText("User Management");
            lblViewSubtitle.setText("Create, update, and manage coordinator accounts");
            loadSubView("/views/UserManagement.fxml"); 
        } else if (sourceButton == btnOrganizationManagement) {
            lblViewTitle.setText("Organization Management");
            lblViewSubtitle.setText("Track and coordinate with partner humanitarian organizations");
            loadSubView("/views/OrganizationManagement.fxml");
        } else if (sourceButton == btnAdminFamilyManagement) {
            lblViewTitle.setText("Family Management");
            lblViewSubtitle.setText("Central registry of displaced families and vulnerability criteria");
            loadSubView("/views/FamilyManagement.fxml");
        } else if (sourceButton == btnAidDistributions) {
            lblViewTitle.setText("Aid Distribution Logs");
            lblViewSubtitle.setText("Global tracking table for all delivered aid packages");
            loadSubView("/views/AidDistributionManagement.fxml");
        } else if (sourceButton == btnAdminChangePassword) {
            lblViewTitle.setText("Change Secure Password");
            lblViewSubtitle.setText("Update credentials for your administrator profile");
            loadSubView("/views/ChangePassword.fxml");
        }
    }

    private void loadSubView(String fxmlPath) {
        try {
            Parent subView = FXMLLoader.load(getClass().getResource(fxmlPath));
            adminContentArea.getChildren().clear();
            adminContentArea.getChildren().add(subView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Sub-view Load Failed", "Could not load screen: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void resetSidebarButtons() {
        Button[] buttons = {btnAdminStats, btnUserManagement, btnOrganizationManagement, btnAdminFamilyManagement, btnAidDistributions, btnAdminChangePassword, btnAdminLogout};
        for (Button btn : buttons) {
            btn.getStyleClass().remove("nav-button-active");
        }
    }

    @FXML
    private void handleAdminLogout(ActionEvent event) {
        try {
            Session.setLoggedInUser(null);
            
            Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("GHADS - System Authentication");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Logout Failed", "Could not return to Login screen.");
        }
    }

    @FXML
    private void handleThemeSwitch(ActionEvent event) {
        RadioMenuItem source = (RadioMenuItem) event.getSource();
        if (source == menuThemeDark) {
            applyTheme("dark");
        } else {
            applyTheme("light");
        }
    }

    @FXML
    private void handleApplicationShutdown(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
    
    @FXML
    private void handleShowAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About GHADS");
        alert.setHeaderText("GHADS - Humanitarian Aid Distribution System");
        alert.setContentText("Version 1.0\n\n" +
                             "Developed by: Ayat Yousef Abumusameh\n" +
                             "This system is designed to streamline aid distribution and family registration for humanitarian relief.\n\n" +
                             "© 2026 All Rights Reserved.");
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/help1.jpg")));
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void applyTheme(String theme) {

        if (rootPane.getScene() == null) 
            return;
        
        Scene scene = rootPane.getScene();
        String darkThemePath = getClass().getResource("/styles/dark-theme.css").toExternalForm();

        scene.getStylesheets().remove(darkThemePath);

        if (theme.equals("dark")) {
            scene.getStylesheets().add(darkThemePath);
        }
    }
}