package controllers;

import config.LocalDateConverter;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Date;

public class AddFamilyFormController implements Initializable {

    @FXML
    private TextField txtNationalId;
    @FXML
    private TextField txtHouseholdName;
    @FXML
    private TextField txtFamilySize;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private TextField txtLocation;
    @FXML
    private ComboBox<String> comboVulnerability;
    @FXML
    private DatePicker dateRegistration;
    @FXML
    private DatePicker dateLastAid;

    private FamilyDAO familyDAO = new FamilyDAO();
    private boolean isEditMode = false;
    private Family familyToEdit;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboVulnerability.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));
        dateRegistration.setValue(LocalDate.now());
    }
    
    @FXML
    private void handleSaveFamily(ActionEvent event) {
        String nationalId = txtNationalId.getText().trim();
        String householdName = txtHouseholdName.getText().trim();
        String sizeStr = txtFamilySize.getText().trim();
        String phone = txtPhoneNumber.getText().trim();
        String location = txtLocation.getText().trim();
        String vulnerability = comboVulnerability.getValue();
        LocalDate regDate = dateRegistration.getValue();
        LocalDate aidDate = dateLastAid.getValue();

        if (nationalId.isEmpty() || householdName.isEmpty() || sizeStr.isEmpty() || phone.isEmpty() || location.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Incomplete Form", "All textual fields are required.");
            return;
        }
        if (vulnerability == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Vulnerability Level Missing", "Please assign a vulnerability classification.");
            return;
        }
        if (!nationalId.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Format Error", "Invalid National ID", "The National ID must contain numeric digits only.");
            return;
        }
        if (phone.length() != 10 || (!phone.startsWith("059") && !phone.startsWith("056"))) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Phone number not correctly", "Phone must 10 numbers start with 059 or 056.");
            return;
        }
        boolean isDuplicate = isEditMode ? familyDAO.isNationalIdExists(nationalId, (long) familyToEdit.getFamilyId()) 
                                        : familyDAO.isNationalIdExists(nationalId, -1L);
        if (isDuplicate) {
            showAlert(Alert.AlertType.ERROR, "Security Violation", "Duplicate National ID", "This National ID is already registered.");
            return;
        }

        int familySize;
        try {
            familySize = Integer.parseInt(sizeStr);
            if (familySize <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Error", "Invalid Family Size", "Please enter a valid positive number.");
            return;
        }
        if (isEditMode) {
            familyToEdit.setNationalId(nationalId);
            familyToEdit.setHouseholdName(householdName);
            familyToEdit.setFamilySize(familySize);
            familyToEdit.setPhone(phone);
            familyToEdit.setLocation(location);
            familyToEdit.setVulnerabilityLevel(vulnerability);
            familyToEdit.setLastAidDate(aidDate);
            familyToEdit.setRegistrationDate(regDate);

            if (familyDAO.updateFamily(familyToEdit)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Updated", "Family details updated.");
                closeWindow(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Update Failed", "Database error.");
            }
        } else {
            Family newFamily = new Family();
            newFamily.setNationalId(nationalId);
            newFamily.setHouseholdName(householdName);
            newFamily.setFamilySize(familySize);
            newFamily.setPhone(phone);
            newFamily.setLocation(location);
            newFamily.setVulnerabilityLevel(vulnerability);
            newFamily.setLastAidDate(aidDate);
            newFamily.setRegistrationDate(regDate);

            if (familyDAO.addFamily(newFamily)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Saved", "Family added successfully.");
                closeWindow(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Save Failed", "Database error.");
            }
        }
    }

    public void setFamilyToEdit(Family family) {
        this.familyToEdit = family;
        this.isEditMode = true;
        txtNationalId.setText(family.getNationalId());
        txtHouseholdName.setText(family.getHouseholdName());
        txtFamilySize.setText(String.valueOf(family.getFamilySize()));
        txtPhoneNumber.setText(family.getPhone());
        txtLocation.setText(family.getLocation());
        comboVulnerability.setValue(family.getVulnerabilityLevel());

        if (family.getLastAidDate() != null) {
            dateLastAid.setValue(family.getLastAidDate());
        }
        txtNationalId.setEditable(false);
        dateRegistration.setValue(family.getRegistrationDate());
    }    
    
    @FXML
    private void handleReset(ActionEvent event) {
        txtNationalId.clear();
        txtHouseholdName.clear();
        txtFamilySize.clear();
        txtPhoneNumber.clear();
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

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}