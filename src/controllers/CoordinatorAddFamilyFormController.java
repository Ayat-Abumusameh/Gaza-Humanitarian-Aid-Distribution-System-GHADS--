package controllers;

import dao.FamilyDAO;
import models.Family;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CoordinatorAddFamilyFormController implements Initializable {

    @FXML private TextField txtNationalId, txtHouseholdName, txtFamilySize, txtPhone, txtLocation;
    @FXML private ComboBox<String> comboVulnerability;
    @FXML
    private DatePicker dateRegistration;
    @FXML
    private DatePicker dateLastAid; 

    private FamilyDAO familyDAO = new FamilyDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboVulnerability.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));
    }

    @FXML
    private void handleFieldSave(ActionEvent event) {
        String nId = txtNationalId.getText().trim();
        String name = txtHouseholdName.getText().trim();
        String phone = txtPhone.getText().trim();
        int size = Integer.parseInt(txtFamilySize.getText().trim());
        String vul = comboVulnerability.getValue();
        String location = txtLocation.getText().trim();
        LocalDate regDate = dateRegistration.getValue();
        LocalDate aidDate = dateLastAid.getValue();

        if (familyDAO.isFamilyExists(nId)) {
            showError("Registration Denied", "Duplicate Family", "The National ID [" + nId + "] already exists.");
            return;
        }
        if (phone.length() != 10 || (!phone.startsWith("059") && !phone.startsWith("056"))) {
            showInfo("Validation Error", "Phone number not correctly", "Phone must 10 numbers start with 059 or 056.");
            return;
        }
        
        Family newFamily = new Family();
        newFamily.setHouseholdName(name);
        newFamily.setPhone(phone);
        newFamily.setLocation(location);
        newFamily.setFamilySize(size);
        newFamily.setNationalId(nId);
        newFamily.setVulnerabilityLevel(vul);
        newFamily.setRegistrationDate(regDate);
        newFamily.setLastAidDate(aidDate);

        if (familyDAO.addFamily(newFamily)) {
            showInfo("Success", "Registered", "Family record added successfully.");
            closeWindow(event);
        } else {
            showError("System Error", "Save Failed", "Could not save to database.");
        }
    }
    
    @FXML
    private void handleReset(ActionEvent event) {
        txtNationalId.clear();
        txtHouseholdName.clear();
        txtFamilySize.clear();
        txtPhone.clear();
        txtLocation.clear();
        
        comboVulnerability.getSelectionModel().clearSelection();
        dateRegistration.setValue(null);
        dateLastAid.setValue(null);
        txtNationalId.requestFocus();
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}