package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import dao.UserDAO;
import models.User;

public class ChangePasswordController implements Initializable {

    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    private void handleUpdatePassword(ActionEvent event) {
        String currentPwd = txtCurrentPassword.getText();
        String newPwd = txtNewPassword.getText();
        String confirmPwd = txtConfirmPassword.getText();

        if (currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Missing Fields", "All password fields must be filled out!");
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password Mismatch", "The new password and confirmation password do not match.");
            return;
        }

        if (newPwd.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Security Warning", "Weak Password", "For safety, new password must be at least 8 characters long.");
            return;
        }

        User currentAdmin = Session.getLoggedInUser();
        if (currentAdmin == null) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Session Expired", "No active session found. Please login again.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.updatePassword(currentAdmin.getUserId(), currentPwd, newPwd);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password Updated", "Your administrator password has been changed successfully.");
            txtCurrentPassword.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Security Error", "Incorrect Current Password", "The current password you entered is incorrect. Please try again.");
        }
    }
    
    @FXML
    private void handleReset(ActionEvent event) {
        txtCurrentPassword.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
        txtCurrentPassword.requestFocus();
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}