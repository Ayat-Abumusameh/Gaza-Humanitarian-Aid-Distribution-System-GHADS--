package controllers;

import dao.AidDistributionDAO;
import dao.DuplicateAidException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.AidDistribution;
import models.Family;
import models.Organization;

public class CoordinatorAddAidDistributionFormController implements Initializable {

    @FXML private Label lblFamilyName;
    @FXML private Label lblVulnerabilityLevel;
    @FXML private Label lblLastAidDate;
    
    @FXML private TextField txtOrganizationName;
    @FXML private TextField txtAidDetails;
    @FXML private DatePicker dpDistributionDate;

    private Family selectedFamily;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dpDistributionDate.setValue(LocalDate.now());
    }

    public void setFamilyData(Family family) {
        this.selectedFamily = family;
        lblFamilyName.setText(family.getHouseholdName());
        lblVulnerabilityLevel.setText(family.getVulnerabilityLevel());
        
        if (family.getLastAidDate() != null) {
            lblLastAidDate.setText(family.getLastAidDate().toString());
        } else {
            lblLastAidDate.setText("Never Served");
        }
    }

    @FXML
    private void handleSaveDistribution(ActionEvent event) {
        
        String orgName = txtOrganizationName.getText().trim();
        LocalDate distributionDate = dpDistributionDate.getValue();

        if (orgName.isEmpty() || distributionDate == null) {
            showValidationAlert("Input Error", "Missing Fields", "Please fill required fields.");
            return;
        }

        AidDistribution newDist = new AidDistribution();
        newDist.setFamily(selectedFamily);
        Organization org = new Organization(); 
        org.setOrgId(1);
        newDist.setOrganization(org);

        int currentUserId = Session.getLoggedInUser().getUserId();
        newDist.setDistributedBy(currentUserId);
        newDist.setDistributionDate(distributionDate);

        AidDistributionDAO distDAO = new AidDistributionDAO();
        try {
            if (distDAO.recordAidDistribution(newDist)) {
                showValidationAlert("Success", "Saved", "Distribution recorded.");
                closeWindow(event);
            }
        } catch (DuplicateAidException e) {
            String detailedMessage = String.format(
            "Family Name: %s\n" +
            "Vulnerability Level: %s\n" +
            "Previous Organization: %s\n" +
            "Last Aid Date: %s\n\n" +
            "Duplicate aid detected within 30 days.",
            e.getFamilyName(), 
            e.getVulnerabilityLevel(), 
            e.getOrganizationName(), 
            e.getLastAidDate()
        );

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Rejected");
        alert.setHeaderText("30-Day Rule Violated");
        alert.setContentText(detailedMessage);
        alert.showAndWait();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleReset(ActionEvent event) {
        txtOrganizationName.clear();
        txtAidDetails.clear();
        dpDistributionDate.setValue(null);
        txtOrganizationName.requestFocus();
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showValidationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}