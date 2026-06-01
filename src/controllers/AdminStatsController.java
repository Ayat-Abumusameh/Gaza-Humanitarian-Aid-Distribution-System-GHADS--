package controllers;

import dao.FamilyDAO;
import dao.OrganizationDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminStatsController implements Initializable {

    @FXML
    private Label lblTotalOrganizations;
    @FXML
    private Label lblTotalCoordinators;
    @FXML
    private Label lblTotalFamilies;
    @FXML
    private Label lblFamiliesServed;
    @FXML
    private Label lblFamiliesNotServed;

    private FamilyDAO familyDAO = new FamilyDAO();
    private OrganizationDAO orgDAO = new OrganizationDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSystemStatistics();
    }

    private void loadSystemStatistics() {
        dao.UserDAO userDAO = new dao.UserDAO(); 

        long totalOrgs = orgDAO.countTotalOrganizations(); 
        long totalFamiliesRegistered = familyDAO.countTotalFamilies();
        long servedFamilies = familyDAO.countFamiliesServed();
        long totalCoordinators = userDAO.countTotalCoordinators(); 
        long notServedFamilies = totalFamiliesRegistered - servedFamilies;

        lblTotalOrganizations.setText(String.valueOf(totalOrgs));
        lblTotalCoordinators.setText(String.valueOf(totalCoordinators));
        lblTotalFamilies.setText(String.format("%,d", totalFamiliesRegistered)); 
        lblFamiliesServed.setText(String.valueOf(servedFamilies));
        lblFamiliesNotServed.setText(String.valueOf(notServedFamilies));
    }
}