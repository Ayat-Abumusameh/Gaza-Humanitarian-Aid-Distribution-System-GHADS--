package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import dao.UserDAO;
import models.User;

public class CoordinatorChangePasswordController implements Initializable {

    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    private void handleUpdatePassword(ActionEvent event) {
        String currentPass = txtCurrentPassword.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Empty Fields", "All password fields are mandatory.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Security Error", "Mismatch Detected", "New password and confirmation do not match.");
            return;
        }

        if (newPass.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Security Error", "Weak Password", "The new password must be at least 8 characters long.");
            return;
        }

        User currentCoordinator = Session.getLoggedInUser();
        if (currentCoordinator == null) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Session Expired", "No active coordinator session found.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.updatePassword(currentCoordinator.getUserId(), currentPass, newPass);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Credentials Updated", "Your coordinator password has been changed successfully.");
            closeWindow(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Security Error", "Incorrect Current Password", "The current password you entered is incorrect.");
        }
    }
    
    
    @FXML
    private void handleReset(ActionEvent event) {
        txtCurrentPassword.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
        txtCurrentPassword.requestFocus();
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}