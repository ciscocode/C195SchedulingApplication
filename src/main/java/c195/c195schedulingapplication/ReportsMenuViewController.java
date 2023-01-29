package c195.c195schedulingapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReportsMenuViewController {
    public void onAppointmentReports(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("appointments-report-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1400, 800);
        stage.setTitle("Appointment Reports");
        stage.setScene(scene);
        stage.show();
    }

    public void onContactSchedule(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("contact-appointment-report-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1400, 800);
        stage.setTitle("Contact Appointment Reports");
        stage.setScene(scene);
        stage.show();
    }

    public void onCustomerByCountry(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-country-report-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customer by Country Report");
        stage.setScene(scene);
        stage.show();
    }

    public void onMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }
}
