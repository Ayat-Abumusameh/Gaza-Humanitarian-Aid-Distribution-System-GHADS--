package controllers;

import dao.FamilyDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CoordinatorStatsController implements Initializable {

    @FXML private Label lblTotalFamilies;
    @FXML private Label lblFamiliesServed;
    @FXML private Label lblFamiliesNotServed;

    private FamilyDAO familyDAO = new FamilyDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        calculateFieldMetrics();
    }

    private void calculateFieldMetrics() {
        long total = familyDAO.countTotalFamilies();
        long served = familyDAO.countFamiliesServed();
        long awaiting = total - served;

        lblTotalFamilies.setText(String.valueOf(total));
        lblFamiliesServed.setText(String.valueOf(served));
        lblFamiliesNotServed.setText(String.valueOf(awaiting));
    }
}