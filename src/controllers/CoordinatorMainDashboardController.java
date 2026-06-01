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

public class CoordinatorMainDashboardController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private Button btnCoordStats;
    @FXML private Button btnCoordProfile;
    @FXML private Button btnCoordFamilyManagement;
    @FXML private Button btnCoordAidDistribution;
    @FXML private Button btnCoordChangePassword;
    @FXML private Button btnCoordLogout;
    @FXML private Label lblViewTitle;
    @FXML private Label lblViewSubtitle;
    @FXML private StackPane contentArea;
    @FXML private RadioMenuItem menuThemeLight;
    @FXML private RadioMenuItem menuThemeDark;
    @FXML private ToggleGroup fontSizeGroup;
    @FXML private ToggleGroup fontFamilyGroup;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblViewTitle.setText("Field Operations Dashboard");
        lblViewSubtitle.setText("Real-time insights and field distribution metrics");
        
         loadSubView("/views/CoordinatorStats.fxml");

        fontSizeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String size = ((RadioMenuItem)newVal).getText();
                double fontSize = size.equals("Small") ? 10 : (size.equals("Large") ? 18 : 14);

                rootPane.getScene().getRoot().setStyle("-fx-font-size: " + fontSize + "px;");
            }
        }); 
        
        fontFamilyGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String font = ((RadioMenuItem)newVal).getText();
                rootPane.setStyle(rootPane.getStyle() + "-fx-font-family: '" + font + "';");
            }
        });
    }

    @FXML
    private void handleCoordinatorNavigation(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        
        resetButtonStyles();
        
        sourceButton.getStyleClass().add("nav-button-active");

        if (sourceButton == btnCoordStats) {
            lblViewTitle.setText("Field Operations Dashboard");
            lblViewSubtitle.setText("Real-time insights and field distribution metrics");
             loadSubView("/views/CoordinatorStats.fxml");
        } else if (sourceButton == btnCoordProfile) {
            lblViewTitle.setText("My Profile Information");
            lblViewSubtitle.setText("View and update personal assignment metadata");
             loadSubView("/views/CoordinatorProfile.fxml");
        } else if (sourceButton == btnCoordFamilyManagement) {
            lblViewTitle.setText("Family Records Registry");
            lblViewSubtitle.setText("Assess local vulnerability index and log family units");
             loadSubView("/views/CoordinatorFamilyManagement.fxml");
        } else if (sourceButton == btnCoordAidDistribution) {
            lblViewTitle.setText("Direct Aid Handout");
            lblViewSubtitle.setText("Disburse humanitarian aid packages and logs");
             loadSubView("/views/CoordinatorAidDistribution.fxml");
        } else if (sourceButton == btnCoordChangePassword) {
            lblViewTitle.setText("Security Credentials");
            lblViewSubtitle.setText("Update system authentication keys periodically");
             loadSubView("/views/CoordinatorChangePassword.fxml");
        }
    }

    private void resetButtonStyles() {
        Button[] buttons = {btnCoordStats, btnCoordProfile, btnCoordFamilyManagement, btnCoordAidDistribution, btnCoordChangePassword};
        for (Button btn : buttons) {
            btn.getStyleClass().remove("nav-button-active");
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
    private void handleCoordinatorLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(loginRoot));
            currentStage.setTitle("GHADS - Humanitarian System Login");
            currentStage.centerOnScreen();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Logout Failure", "Could not safely redirect to LoginScreen.fxml");
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
    
    private void loadSubView(String fxmlPath) {
        try {
            contentArea.getChildren().clear();
            Parent subView = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().add(subView);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Sub-view Missing", "Failed to resolve view path: " + fxmlPath);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void applyTheme(String theme) {
        Scene scene = rootPane.getScene();
        scene.getStylesheets().remove(getClass().getResource("/styles/dark-theme.css").toExternalForm());

        if (theme.equals("dark")) {
            scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        }
    }
}