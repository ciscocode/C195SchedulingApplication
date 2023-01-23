package c195.c195schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentViewController implements Initializable {
    public TableView appointmentTable;
    public TableColumn apptIDCol;
    public TableColumn descriptionCol;
    public TableColumn locationCol;
    public TableColumn contactCol;
    public TableColumn typeCol;
    public TableColumn startTimeCol;
    public TableColumn endTimeCol;
    public TableColumn customerIDCol;
    public TableColumn userIDCol;
    public TableColumn titleCol;
    public RadioButton allRadioButton;
    public RadioButton monthRadioButton;
    public RadioButton weekRadioButton;
    ObservableList<Appointment> data = FXCollections.observableArrayList();
    ObservableList<Appointment> dataByMonth = FXCollections.observableArrayList();
    ObservableList<Appointment> dataByWeek = FXCollections.observableArrayList();


    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //establish connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

            //execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM appointments");

            //Iterate through the result set and add each row to the ObservableList
            while (resultSet.next()) {
                Appointment appointment = new Appointment(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getTimestamp("Create_Date").toLocalDateTime(),
                        resultSet.getString("Created_By"),
                        resultSet.getTimestamp("Last_Update").toLocalDateTime(),
                        resultSet.getString("Last_Updated_By"),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID")
                        );
                data.add(appointment);
            }

            //set items into the table view
            appointmentTable.setItems(data);

            //set cell values
            apptIDCol.setCellValueFactory(new PropertyValueFactory<>("Appointment_ID"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
            contactCol.setCellValueFactory(new PropertyValueFactory<>("ContactName"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
            startTimeCol.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
            userIDCol.setCellValueFactory(new PropertyValueFactory<>("User_ID"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));

            //Run a query to filter by month
            String monthQuery = "SELECT * FROM appointments WHERE MONTH(Start) = MONTH(CURRENT_TIMESTAMP)";
            Statement monthStatement = connection.createStatement();
            ResultSet monthResultSet = monthStatement.executeQuery(monthQuery);

            //Iterate through the result set and add each row to the ObservableList
            while (monthResultSet.next()) {
                Appointment appointment = new Appointment(
                        monthResultSet.getInt("Appointment_ID"),
                        monthResultSet.getString("Title"),
                        monthResultSet.getString("Description"),
                        monthResultSet.getString("Location"),
                        monthResultSet.getString("Type"),
                        monthResultSet.getTimestamp("Start").toLocalDateTime(),
                        monthResultSet.getTimestamp("End").toLocalDateTime(),
                        monthResultSet.getTimestamp("Create_Date").toLocalDateTime(),
                        monthResultSet.getString("Created_By"),
                        monthResultSet.getTimestamp("Last_Update").toLocalDateTime(),
                        monthResultSet.getString("Last_Updated_By"),
                        monthResultSet.getInt("Customer_ID"),
                        monthResultSet.getInt("User_ID"),
                        monthResultSet.getInt("Contact_ID")
                );
                dataByMonth.add(appointment);
            }

            //Run a query to filter by week
            String weekQuery = "SELECT * FROM appointments WHERE WEEK(Start,3) = WEEK(CURRENT_TIMESTAMP,3)";
            Statement weekStatement = connection.createStatement();
            ResultSet weekResultSet = weekStatement.executeQuery(weekQuery);

            //Iterate through the result set and add each row to the ObservableList
            while (weekResultSet.next()) {
                Appointment appointment = new Appointment(
                        weekResultSet.getInt("Appointment_ID"),
                        weekResultSet.getString("Title"),
                        weekResultSet.getString("Description"),
                        weekResultSet.getString("Location"),
                        weekResultSet.getString("Type"),
                        weekResultSet.getTimestamp("Start").toLocalDateTime(),
                        weekResultSet.getTimestamp("End").toLocalDateTime(),
                        weekResultSet.getTimestamp("Create_Date").toLocalDateTime(),
                        weekResultSet.getString("Created_By"),
                        weekResultSet.getTimestamp("Last_Update").toLocalDateTime(),
                        weekResultSet.getString("Last_Updated_By"),
                        weekResultSet.getInt("Customer_ID"),
                        weekResultSet.getInt("User_ID"),
                        weekResultSet.getInt("Contact_ID")
                );
                dataByWeek.add(appointment);
            }

            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void onAddAppt(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("add-appointment-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 500, 700);
        stage.setTitle("Add Appointment");
        stage.setScene(scene);
        stage.show();
    }
    public void onUpdateAppt(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader updateApptLoader = new FXMLLoader();
        updateApptLoader.setLocation(getClass().getResource("update-appointment-view.fxml"));
        updateApptLoader.load();
        UpdateAppointmentViewController updateApptController = updateApptLoader.getController();
        if (appointmentTable.getSelectionModel().getSelectedItem() == null) {
            //insert error message
            return;
        }
        updateApptController.sendApptData((Appointment) appointmentTable.getSelectionModel().getSelectedItem());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent scene = updateApptLoader.getRoot();
        stage.setTitle("Update Appointment");
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void onDeleteAppt(ActionEvent actionEvent) throws IOException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //get the Appointment ID from the appointment the user selects on the table
        Appointment selectedRow = (Appointment) appointmentTable.getSelectionModel().getSelectedItem();
        int Appointment_ID = selectedRow.getAppointment_ID();

        //use this appointment ID to run a query to delete the appointment from the table
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,Appointment_ID);
        preparedStatement.executeUpdate();

        //then update the table view
        data.remove(selectedRow);
        appointmentTable.setItems(data);
    }
    public void onReturnToMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    public void onViewAll(ActionEvent actionEvent) {
        appointmentTable.setItems(data);
    }

    public void onSortByMonth(ActionEvent actionEvent) {
        appointmentTable.setItems(dataByMonth);
    }

    public void onSorthByWeek(ActionEvent actionEvent) {
        appointmentTable.setItems(dataByWeek);
    }
}
