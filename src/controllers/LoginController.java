package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import dao.UserDAO;
import models.User;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private ComboBox<String> comboRole;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> roles = FXCollections.observableArrayList("ADMIN", "COORDINATOR");
        comboRole.setItems(roles);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String selectedRole = comboRole.getValue();

        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!", "Please fill in your username, password, and select a role.");
            return;
        }

        if (password.length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Weak Password", "Password length must be at least 8 characters.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User loggedInUser = userDAO.login(username, password, selectedRole);

        if (loggedInUser != null) {
            Session.setLoggedInUser(loggedInUser);

            String fxmlPath = "";
            String windowTitle = "";

            if ("ADMIN".equals(loggedInUser.getRole())) {
                fxmlPath = "/views/AdminMainDashboard.fxml";
                windowTitle = "GHADS - Admin Dashboard";
            } else if ("COORDINATOR".equals(loggedInUser.getRole())) {
                fxmlPath = "/views/CoordinatorMainDashboard.fxml";
                windowTitle = "GHADS - Coordinator Dashboard";
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Login Successful", "Welcome back, " + loggedInUser.getFullName());
            navigateToDashboard(event, fxmlPath, windowTitle);

        } else {
            showAlert(Alert.AlertType.ERROR, "Authentication Failed", "Invalid Credentials", "The username, password, or role is incorrect.");
        }
    }
    
    @FXML
    private void handleReset(ActionEvent event) {
        txtUsername.clear();
        txtPassword.clear();
        comboRole.getSelectionModel().clearSelection();
        txtUsername.requestFocus();
    }

    private void navigateToDashboard(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Navigation Failed", "Could not load the interface: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}