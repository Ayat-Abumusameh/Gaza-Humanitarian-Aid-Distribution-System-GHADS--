package controllers;

import dao.FamilyDAO;
import models.Family;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CoordinatorFamilyManagementController implements Initializable {

    @FXML private TableView<Family> tblFamilies;
    @FXML private TableColumn<Family, Integer> colFamilyId;
    @FXML private TableColumn<Family, String> colNationalId;
    @FXML private TableColumn<Family, String> colHouseholdName;
    @FXML private TableColumn<Family, Integer> colFamilySize;
    @FXML private TableColumn<Family, String> colPhoneNumber;
    @FXML private TableColumn<Family, String> colLocation;
    @FXML private TableColumn<Family, String> colVulnerabilityLevel;
    @FXML private TableColumn<Family, LocalDate> colLastAidDate;

    private FamilyDAO familyDAO = new FamilyDAO();
    private final ObservableList<Family> familyMasterData = FXCollections.observableArrayList();

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
        
        loadDatabaseRecords();
        tblFamilies.setItems(familyMasterData);
    }

    private void loadDatabaseRecords() {
        familyMasterData.clear();
        List<Family> families = familyDAO.getAllFamilies();
        if (families != null) {
            familyMasterData.addAll(families);
        }
    }
    
    @FXML
    private void handleOpenAddFamilyForm(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/CoordinatorAddFamilyForm.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage popUpStage = new javafx.stage.Stage();
            popUpStage.setTitle("GHADS - Register New Beneficiary Family");

            popUpStage.initModality(javafx.stage.Modality.APPLICATION_MODAL); 
            popUpStage.setScene(new javafx.scene.Scene(root));

            popUpStage.showAndWait();
            loadDatabaseRecords();

        } catch (java.io.IOException e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("System Error");
            alert.setHeaderText("UI Load Failure");
            alert.setContentText("Could not find or load CoordinatorAddFamilyForm.fxml. Please check the views folder.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}