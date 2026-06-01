package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.UserDAO;
import java.io.IOException;
import javafx.stage.Modality;
import models.User;

public class UserManagementController implements Initializable {

    @FXML private TextField txtSearchUser;
    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colOrganization;

    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        colOrganization.setCellValueFactory(cellData -> {
            models.Organization org = cellData.getValue().getOrganization(); 
            if (org != null) {
                return new javafx.beans.property.SimpleStringProperty(org.getName()); 
            }
            return new javafx.beans.property.SimpleStringProperty("Not exist");
        });

        refreshTable();
    }

    @FXML
    private void refreshTable() {
        dao.UserDAO userDAO = new dao.UserDAO();
        tblUsers.setItems(FXCollections.observableArrayList(userDAO.getAllCoordinators()));
    }

    @FXML
    private void handleAddUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddUserForm.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("GHADS - Add New User");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(ownerStage);
            popupStage.setResizable(false);
            popupStage.centerOnScreen();

            popupStage.showAndWait();

            refreshTable(); 

        } catch (java.io.IOException e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Navigation Failed", "Could not open the Add User Form. Please check the file path.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditUser(ActionEvent event) {
        User selectedUser = tblUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "No User Selected", "Please select a user row from the table to edit.");
            return;
        }
        
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddUserForm.fxml"));
        Parent root = loader.load();
        
        AddUserFormController editController = loader.getController();
        editController.setEditMode(selectedUser);
        
        Stage popupStage = new Stage();
        popupStage.setTitle("GHADS - Edit User Details");
        popupStage.setScene(new Scene(root));
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
        
        popupStage.showAndWait();
        refreshTable();
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    @FXML
    private void handleToggleUserStatus(ActionEvent event) {
        User selectedUser = tblUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "No User Selected", "Please select a user row from the table to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Confirmation");
        confirmAlert.setHeaderText("Removing User Account");
        confirmAlert.setContentText("Are you sure you want to completely remove user: " + selectedUser.getFullName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = userDAO.deleteUser(selectedUser.getUserId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User Deleted", "The account has been removed successfully.");
                refreshTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Operation Failed", "Could not delete user account.");
            }
        }
    }

    @FXML
    private void handleFilterUsers(ActionEvent event) {
        String query = txtSearchUser.getText().toLowerCase().trim();
        showAlert(Alert.AlertType.INFORMATION, "Search Operation", "Filtering Users", "Searching for pattern: " + query);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}