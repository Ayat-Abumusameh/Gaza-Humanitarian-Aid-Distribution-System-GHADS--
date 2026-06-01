package controllers;

import dao.AidDistributionDAO;
import dao.DuplicateAidException;
import models.AidDistribution;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AidDistributionManagementController implements Initializable {

    @FXML
    private TextField txtSearchDistribution;
    @FXML
    private TableView<AidDistribution> tblDistributions;
    @FXML
    private TableColumn<AidDistribution, Integer> colDistributionId;
    @FXML
    private TableColumn<AidDistribution, Integer> colFamilyId;
    @FXML
    private TableColumn<AidDistribution, Integer> colOrganizationId;
    @FXML
    private TableColumn<AidDistribution, String> colDistributedBy;
    @FXML
    private TableColumn<AidDistribution, LocalDate> colDistributionDate;

    private AidDistributionDAO aidDAO = new AidDistributionDAO();
    private FilteredList<AidDistribution> filteredData;
    private ObservableList<AidDistribution> masterData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        masterData = FXCollections.observableArrayList();

        colDistributionId.setCellValueFactory(new PropertyValueFactory<>("distributionId"));
        colDistributedBy.setCellValueFactory(new PropertyValueFactory<>("distributedBy"));
        colDistributionDate.setCellValueFactory(new PropertyValueFactory<>("distributionDate"));

        colFamilyId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getFamily().getFamilyId()).asObject());
        colOrganizationId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getOrganization().getOrgId()).asObject());

        filteredData = new FilteredList<>(masterData, b -> true);

        txtSearchDistribution.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(dist -> {
                if (newValue == null || newValue.isEmpty()) 
                    return true;

                String filter = newValue.toLowerCase();
                String distBy = String.valueOf(dist.getDistributedBy());

                return distBy.contains(filter); 
            });
        });

        SortedList<AidDistribution> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblDistributions.comparatorProperty());
        tblDistributions.setItems(sortedData);

        refreshTable();
    }

    private void refreshTable() {
        List<AidDistribution> distributions = aidDAO.getAllDistributions();
        masterData.setAll(distributions);
    }

    @FXML
    private void handleRecordNewDistribution(AidDistribution newDist) {
        try {
            boolean success = aidDAO.recordAidDistribution(newDist);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Aid Handed Over", 
                        "The aid shipment has been recorded and the family ledger updated.");
                refreshTable();
            }
        } catch (DuplicateAidException ex) {
            String structuralErrorMsg = String.format(
                "Family Head: %s\n" +
                "Vulnerability Level: %s\n" +
                "Last Distributing Entity: %s\n" +
                "Previous Handout Date: %s\n\n" +
                "Operational Block: System prevents duplicate provisioning for non-critical units within a 30-day window.",
                ex.getFamilyName(), ex.getVulnerabilityLevel(), ex.getOrganizationName(), ex.getLastAidDate().toString()
            );
            
            showAlert(Alert.AlertType.ERROR, "Security & Compliance Check Failed", 
                    "Duplicate Distribution Blocked!", structuralErrorMsg);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Process Failed", "An internal error occurred while connecting to the persistent storage.");
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