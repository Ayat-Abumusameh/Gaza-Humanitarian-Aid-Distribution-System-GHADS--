package controllers;

import dao.FamilyDAO;
import models.Family;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FamilyManagementController implements Initializable {

    @FXML
    private TextField txtSearchFamily;
    @FXML
    private ComboBox<String> comboVulnerabilityFilter;
    @FXML
    private TableView<Family> tblFamilies;
    @FXML
    private TableColumn<Family, Integer> colFamilyId;
    @FXML
    private TableColumn<Family, String> colNationalId;
    @FXML
    private TableColumn<Family, String> colHouseholdName;
    @FXML
    private TableColumn<Family, Integer> colFamilySize;
    @FXML
    private TableColumn<Family, String> colPhoneNumber;
    @FXML
    private TableColumn<Family, String> colLocation;
    @FXML
    private TableColumn<Family, String> colVulnerabilityLevel;
    @FXML
    private TableColumn<Family, java.time.LocalDate> colLastAidDate;

    private FamilyDAO familyDAO = new FamilyDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colFamilyId.setCellValueFactory(new PropertyValueFactory<>("familyId"));
        colNationalId.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        colHouseholdName.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        colFamilySize.setCellValueFactory(new PropertyValueFactory<>("familySize"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colVulnerabilityLevel.setCellValueFactory(new PropertyValueFactory<>("vulnerabilityLevel"));
        colLastAidDate.setCellValueFactory(new PropertyValueFactory<>("lastAidDate"));

        refreshTable();
    }

    private void refreshTable() {
        List<Family> families = familyDAO.getMostVulnerableFamilies();
        tblFamilies.setItems(FXCollections.observableArrayList(families));
    }

    private void handleSearchAndFilter() {
        String keyword = txtSearchFamily.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
        } else {
            List<Family> searchResult = familyDAO.searchFamilies(keyword);
            tblFamilies.setItems(FXCollections.observableArrayList(searchResult));
        }
    }

    @FXML
    private void handleRegisterNewFamily(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddFamilyForm.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.setTitle("GHADS - Register Displaced Family");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setResizable(false);
            
            popupStage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "File Missing", "Could not load AddFamilyForm.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFilterFamilies(ActionEvent event) {
        String selectedFilter = comboVulnerabilityFilter.getValue();
        if (selectedFilter == null || selectedFilter.equals("All Levels")) {
            refreshTable();
        } else {
            ObservableList<Family> filteredList = FXCollections.observableArrayList();
            for (Family f : familyDAO.getAllFamilies()) {
                if (f.getVulnerabilityLevel().equalsIgnoreCase(selectedFilter)) {
                    filteredList.add(f);
                }
            }
            tblFamilies.setItems(filteredList);
        }
    }

    @FXML
    private void handleEditFamilyDetails(ActionEvent event) {
        Family selectedFamily = tblFamilies.getSelectionModel().getSelectedItem();
        if (selectedFamily == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "No Row Selected", "Please select a family to edit.");
            return;
        }
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddFamilyForm.fxml"));
        Parent root = loader.load();
        
        AddFamilyFormController controller = loader.getController();
        controller.setFamilyToEdit(selectedFamily);
        
        Stage stage = new Stage();
        stage.setTitle("Edit family data");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
        
        refreshTable();
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteFamilyRecord(ActionEvent event) {
        Family selectedFamily = tblFamilies.getSelectionModel().getSelectedItem();
        if (selectedFamily == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "No Row Selected", "Please select a family to delete.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText("Action Warning");
        confirm.setContentText("Are you sure you want to permanently delete the family record of: " + selectedFamily.getHouseholdName() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                boolean success = familyDAO.deleteFamily(selectedFamily.getFamilyId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Record Deleted", "Family unit cleared successfully.");
                    refreshTable();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Deletion Blocked", "This family is linked to existing Aid Distribution logs and cannot be deleted.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}