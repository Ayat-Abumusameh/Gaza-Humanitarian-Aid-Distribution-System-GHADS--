package controllers;

import dao.UserDAO;
import models.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class CoordinatorProfileController implements Initializable {

    @FXML private TextField txtCoordId, txtCoordUsername, txtCoordRole, txtCoordOrgId, txtCoordFullName, txtCoordEmail;

    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentUser = Session.getLoggedInUser(); 
        
        if (currentUser != null) {
            loadData();
        }
    }

    private void loadData() {
        txtCoordId.setText(String.valueOf(currentUser.getUserId()));
        txtCoordUsername.setText(currentUser.getUsername());
        txtCoordRole.setText(currentUser.getRole());
        txtCoordOrgId.setText(currentUser.getOrganization() != null ? String.valueOf(currentUser.getOrganization().getOrgId()) : "N/A");
        txtCoordFullName.setText(currentUser.getFullName());
        txtCoordEmail.setText(currentUser.getEmail());
        
        txtCoordId.setEditable(false);
        txtCoordUsername.setEditable(false);
        txtCoordRole.setEditable(false);
        txtCoordOrgId.setEditable(false);
    }

    @FXML
    private void handleSaveProfileChanges(ActionEvent event) {
        String newFullName = txtCoordFullName.getText().trim();
        String newEmail = txtCoordEmail.getText().trim();

        if (newFullName.isEmpty() || newEmail.isEmpty()) {
            showNotify(Alert.AlertType.ERROR, "Error", "Validation Failed", "Full Name and Email cannot be empty.");
            return;
        }

        currentUser.setFullName(newFullName);
        currentUser.setEmail(newEmail);

        if (userDAO.updateUser(currentUser)) {
            showNotify(Alert.AlertType.INFORMATION, "Success", "Profile Updated", "Your information has been saved successfully.");
        } else {
            showNotify(Alert.AlertType.ERROR, "Database Error", "Save Failed", "Could not synchronize with the server.");
        }
    }

    @FXML
    private void handleCancelProfileChanges(ActionEvent event) {
        currentUser = userDAO.findUserById(currentUser.getUserId()); 

        loadData();

        showNotify(Alert.AlertType.INFORMATION, "Cancel", "Reverted", "Changes discarded and data refreshed.");
        }

    private void showNotify(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}