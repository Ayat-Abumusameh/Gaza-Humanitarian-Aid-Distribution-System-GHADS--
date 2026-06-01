package controllers;

import dao.OrganizationDAO;
import models.Organization;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

public class OrganizationManagementController implements Initializable {

    @FXML
    private TableView<Organization> tblOrganizations;
    @FXML
    private TableColumn<Organization, String> colOrgName;
    @FXML
    private TableColumn<Organization, String> colOrgType;
    @FXML
    private TableColumn<Organization, String> colOrgContact;
    @FXML
    private TextField txtSearchOrganization;

    private OrganizationDAO orgDAO = new OrganizationDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colOrgName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOrgType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colOrgContact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

        refreshTable();

        if (txtSearchOrganization != null) {
            txtSearchOrganization.textProperty().addListener((observable, oldValue, newValue) -> {
                filterTable(newValue);
            });
        }
    }

    @FXML
    private void refreshTable() {
        List<Organization> orgList = orgDAO.getAllOrganizations();
        ObservableList<Organization> data = FXCollections.observableArrayList(orgList);
        tblOrganizations.setItems(data);
    }

    @FXML
    private void handleFilterOrganizations(ActionEvent event) {
        if (txtSearchOrganization != null) {
            filterTable(txtSearchOrganization.getText());
        }
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            refreshTable();
        } else {
            List<Organization> filteredList = orgDAO.searchOrganizations(keyword);
            ObservableList<Organization> data = FXCollections.observableArrayList(filteredList);
            tblOrganizations.setItems(data);
        }
    }

    @FXML
    private void handleAddOrganization(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddOrganizationForm.fxml"));
            Parent root = loader.load();
            
            Stage popupStage = new Stage();
            popupStage.setTitle("GHADS - Register Partner Organization");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.WINDOW_MODAL);
            
            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(ownerStage);
            popupStage.setResizable(false);
            popupStage.centerOnScreen();
            
            popupStage.showAndWait();
            
            refreshTable(); 

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Navigation Failed", "Could not load AddOrganizationForm.fxml window.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteOrganization(ActionEvent event) {
        Organization selectedOrg = tblOrganizations.getSelectionModel().getSelectedItem();
        
        if (selectedOrg == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "No Row Selected", "Please choose an organization from the table first.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation Dialog");
        confirmAlert.setHeaderText("Delete Organization");
        confirmAlert.setContentText("Are you sure you want to delete: " + selectedOrg.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            boolean success = orgDAO.deleteOrganization(selectedOrg.getOrgId());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Operation Successful", "Organization has been deleted successfully.");
                refreshTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Operation Failed", "Could not delete organization. It might be linked to existing data (Coordinators or Families)!");
            }
        }
    }
    
    @FXML
    private void handleEditOrganization(ActionEvent event) {
        Organization selectedOrg = tblOrganizations.getSelectionModel().getSelectedItem();

        if (selectedOrg == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Not choose an Organization", "Please choose an organization from table");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddOrganizationForm.fxml"));
            Parent root = loader.load();

            AddOrganizationFormController controller = loader.getController();
            controller.setOrganizationToEdit(selectedOrg); 

            Stage popupStage = new Stage();
            popupStage.setTitle("GHADS - Edit Organization");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.showAndWait();

            refreshTable();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Loading fail", "Can't open the edit widow");
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