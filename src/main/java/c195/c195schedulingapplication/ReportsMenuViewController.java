package c195.c195schedulingapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**This class created the Reports menu of the application.*/
public class ReportsMenuViewController {
    /**
     * This method sends the user to the Appointments report sections where users can see a count of appointments by Month or Type
     * @param actionEvent this method is called when the user clicks the Appointment Count Report button
     * @throws IOException
     */
    public void onAppointmentReports(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("appointments-report-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1400, 800);
        stage.setTitle("Appointment Reports");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method sends the user to the contact report which sorts appointments by contacts
     * @param actionEvent this method is called when the user clicks the contacts report button
     * @throws IOException
     */
    public void onContactSchedule(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("contact-appointment-report-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1400, 800);
        stage.setTitle("Contact Appointment Reports");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method opens the customer by country report
     * @param actionEvent This method is called when the user clicks the Customer by Country report button
     * @throws IOException
     */
    public void onCustomerByCountry(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-country-report-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customer by Country Report");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method sends the user back to the main menu
     * @param actionEvent this method is called when the user clicks the main menu button
     * @throws IOException
     */
    public void onMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }
}
