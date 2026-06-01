package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import dao.OrganizationDAO;
import java.util.List;
import models.Organization;

public class AddUserFormController implements Initializable {

    @FXML
    private TextField txtFullName;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtEmail;
    @FXML
    private ComboBox<String> comboRole;
    @FXML
    private ComboBox<Organization> comboOrganization;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboRole.setItems(javafx.collections.FXCollections.observableArrayList("ADMIN", "COORDINATOR"));

        OrganizationDAO orgDAO = new OrganizationDAO();
        List<Organization> allOrganizations = orgDAO.getAllOrganizations();
        comboOrganization.setItems(FXCollections.observableArrayList(allOrganizations));
        comboOrganization.setCellFactory(lv -> new javafx.scene.control.ListCell<Organization>() {
            @Override
            protected void updateItem(Organization item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
        comboOrganization.setButtonCell(new javafx.scene.control.ListCell<Organization>() {
            @Override
            protected void updateItem(Organization item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
    }

    @FXML
    private void handleSaveUser(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String role = comboRole.getValue();
        models.Organization selectedOrg = comboOrganization.getValue();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty() || role == null || selectedOrg == null) {
            showAlert(Alert.AlertType.ERROR, "Error in validation", "Not Complete data", "Please fill all fileds.");
            return;
        }
        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Security Warning", "Weak Password", "For safety, new password must be at least 8 characters long.");
            return;
        }
        dao.UserDAO userDAO = new dao.UserDAO();
        boolean success;
        if (isEditMode && existingUser != null) {
            existingUser.setPassword(password);
            existingUser.setFullName(fullName);
            existingUser.setUsername(username);
            existingUser.setEmail(email);
            existingUser.setRole(role);
            existingUser.setOrganization(selectedOrg);
            success = userDAO.updateUser(existingUser);
        } else {
            models.User newUser = new models.User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setRole(role);
            newUser.setOrganization(selectedOrg);
            success = userDAO.addUser(newUser);
        }
        if (success) {
            String msg = isEditMode ? "User data have been edited sucessfully" : "New User has been added sucessfully";
            showAlert(Alert.AlertType.INFORMATION, "done successfully", "Data save", msg);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error in Operation", "save fail", 
                    "Error while save data، Please make sure you not repeate user name.");
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        txtFullName.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtEmail.clear();
        comboRole.getSelectionModel().clearSelection();
        comboOrganization.getSelectionModel().clearSelection();
        txtFullName.requestFocus();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private models.User existingUser;
    private boolean isEditMode = false;

    public void setEditMode(models.User user) {
        this.existingUser = user;
        this.isEditMode = true;
        txtUsername.setEditable(false);
        txtPassword.setText(user.getPassword());
        txtFullName.setText(user.getFullName());
        txtEmail.setText(user.getEmail());
        comboRole.setValue(user.getRole());
    }
}