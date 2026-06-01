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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import dao.OrganizationDAO;
import models.Organization;

public class AddOrganizationFormController implements Initializable {

    @FXML
    private TextField txtOrgName;
    @FXML
    private TextField txtOrgType;
    @FXML
    private TextArea txtOrgContactInfo;

    private OrganizationDAO orgDAO = new OrganizationDAO();
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void handleSaveOrganization(ActionEvent event) {
        String name = txtOrgName.getText();
        String type = txtOrgType.getText();
        String contact = txtOrgContactInfo.getText();

        OrganizationDAO orgDAO = new OrganizationDAO();
        boolean success;

        if (isEditMode && organizationToEdit != null) {
            organizationToEdit.setName(name);
            organizationToEdit.setType(type);
            organizationToEdit.setContactInfo(contact);

            success = orgDAO.updateOrganization(organizationToEdit);
        } else {
            Organization newOrg = new Organization();
            newOrg.setName(name);
            newOrg.setType(type);
            newOrg.setContactInfo(contact);

            success = orgDAO.addOrganization(newOrg);
        }

        if (success) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Save fail", "sth wrong while save in database");
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        txtOrgName.clear();
        txtOrgType.clear();
        txtOrgContactInfo.clear();
        txtOrgName.requestFocus();
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
    
    private Organization organizationToEdit;

    public void setOrganizationToEdit(Organization org) {
        this.organizationToEdit = org;
        this.isEditMode = true;
        txtOrgName.setText(org.getName());
        txtOrgType.setText(org.getType());
        txtOrgContactInfo.setText(org.getContactInfo());
    }
}