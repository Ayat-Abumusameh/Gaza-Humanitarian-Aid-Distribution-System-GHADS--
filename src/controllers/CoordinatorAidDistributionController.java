package controllers;

import dao.FamilyDAO;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Family;

public class CoordinatorAidDistributionController implements Initializable {

    @FXML private Button btnFilterHighVulnerability;
    @FXML private Button btnFilterNeverServed;
    @FXML private Button btnClearFilters;
    @FXML private Button btnRecordAidDistribution;
    
    @FXML private TableView<Family> tblDistributionFamilies;
    @FXML private TableColumn<Family, Integer> colDistFamilyId;
    @FXML private TableColumn<Family, String> colDistNationalId;
    @FXML private TableColumn<Family, String> colDistHouseholdName;
    @FXML private TableColumn<Family, Integer> colDistFamilySize;
    @FXML private TableColumn<Family, String> colDistLocation;
    @FXML private TableColumn<Family, String> colDistVulnerabilityLevel;
    @FXML private TableColumn<Family, LocalDate> colDistLastAidDate;
    
    private FamilyDAO familyDAO = new FamilyDAO();

    private final ObservableList<Family> masterDistributionList = FXCollections.observableArrayList();
    private FilteredList<Family> filteredDistributionData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colDistFamilyId.setCellValueFactory(new PropertyValueFactory<>("familyId"));
        colDistNationalId.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        colDistHouseholdName.setCellValueFactory(new PropertyValueFactory<>("householdName"));
        colDistFamilySize.setCellValueFactory(new PropertyValueFactory<>("familySize"));
        colDistLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colDistVulnerabilityLevel.setCellValueFactory(new PropertyValueFactory<>("vulnerabilityLevel"));
        colDistLastAidDate.setCellValueFactory(new PropertyValueFactory<>("lastAidDate"));

        loadDatabaseData();

        filteredDistributionData = new FilteredList<>(masterDistributionList, p -> true);
        tblDistributionFamilies.setItems(filteredDistributionData);
    }

    private void loadDatabaseData() {
        masterDistributionList.clear();
        List<Family> families = familyDAO.getAllFamilies();
        if (families != null) {
            masterDistributionList.addAll(families);
        }
    }

    @FXML
    private void handleFilterHighVulnerability(ActionEvent event) {
        filteredDistributionData.setPredicate(family -> {
            String level = family.getVulnerabilityLevel();
            return "HIGH".equalsIgnoreCase(level);
        });
    }

    @FXML
    private void handleFilterNeverServed(ActionEvent event) {
        filteredDistributionData.setPredicate(family -> {
            boolean isNeverServed = (family.getLastAidDate() == null);
            return isNeverServed;
        });
        
    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        filteredDistributionData.setPredicate(family -> true);
    }

    @FXML
    private void handleOpenAddDistributionForm(ActionEvent event) {
        Family selected = tblDistributionFamilies.getSelectionModel().getSelectedItem();

        if (selected == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText("No Family Selected");
            alert.setContentText("Please select a target family from the distribution table first.");
            alert.showAndWait();
            loadDatabaseData();
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/CoordinatorAddAidDistributionForm.fxml"));
            javafx.scene.Parent root = loader.load();

            CoordinatorAddAidDistributionFormController popupController = loader.getController();
            popupController.setFamilyData(selected);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("GHADS - Record Distribution Transaction");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));

            stage.showAndWait();

        } catch (Exception e) {
            System.out.println("Critical Error loading Distribution Form:");
            e.printStackTrace();

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("UI Load Error");
            alert.setHeaderText("Could not open the registration window");
            alert.setContentText("Error details: " + e.getMessage());
            alert.showAndWait();
        }
    }
}