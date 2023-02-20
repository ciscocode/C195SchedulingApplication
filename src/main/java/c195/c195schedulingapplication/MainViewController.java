package c195.c195schedulingapplication;

import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static c195.c195schedulingapplication.LoginController.initialized;
import static helper.JDBC.connection;

public class MainViewController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (initialized == true) {
            return;
        }

        JDBC.openConnection();
        try {
            boolean apptSoon = false;
            int apptID = 0;
            LocalDateTime apptDateTime = null;
            String formattedTime = "";

            //get the current time in the Eastern Standard Time (EST) zone
            ZoneId est = ZoneId.of("America/New_York");
            LocalDateTime now = LocalDateTime.now(est);

            //get time zone of users device
            ZoneId deviceTimeZone = ZoneId.systemDefault();

            //create 12-hour date/time formatter to later display time in a more readable format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

            //set 15 minutes after
            LocalDateTime fifteenMinutesFromNow = now.plusMinutes(15);
            //convert the local time to UTC
            ZoneId utc = ZoneId.of("UTC");

            LocalDateTime fifteenMinutesFromNowUtc = fifteenMinutesFromNow.atZone(est).withZoneSameInstant(utc).toLocalDateTime();
            LocalDateTime nowUTC = now.atZone(est).withZoneSameInstant(utc).toLocalDateTime();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM appointments WHERE Start BETWEEN ? AND ?");
            statement.setTimestamp(1, Timestamp.valueOf(nowUTC));
            statement.setTimestamp(2, Timestamp.valueOf(fifteenMinutesFromNowUtc));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                apptID = resultSet.getInt(1);
                apptDateTime = resultSet.getTimestamp("Start").toLocalDateTime();

                //convert the UTC time in the database to the local time zone
                LocalDateTime dateTimeLocal = apptDateTime.atZone(utc).withZoneSameInstant(deviceTimeZone).toLocalDateTime();
                formattedTime = dateTimeLocal.format(formatter);

                apptSoon = true;
            }

            if (apptSoon == true) {
                Alert errorMessage = new Alert(Alert.AlertType.INFORMATION);
                errorMessage.setTitle("Reminder");
                errorMessage.setContentText("Appointment " + apptID + " is scheduled within the next 15 minutes. The appointment is scheduled on " + formattedTime);
                errorMessage.showAndWait();
            } else {
                Alert errorMessage = new Alert(Alert.AlertType.INFORMATION);
                errorMessage.setTitle("Reminder");
                errorMessage.setContentText("There are no appointments scheduled within the next 15 minutes");
                errorMessage.showAndWait();
            }
            JDBC.closeConnection();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        initialized = true;
    }

    public void onCustomers(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

    public void onAppointments(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("appointment-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1400, 800);
        stage.setTitle("Appointments");
        stage.setScene(scene);
        stage.show();
    }

    public void onReports(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("reports-menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 360, 469);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();
    }
}

