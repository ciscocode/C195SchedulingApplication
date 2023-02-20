package c195.c195schedulingapplication;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import static helper.JDBC.connection;

/**This class runs the appointment view controller and loads the table with the appropriate data.*/
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

    /**
     * This method intializes the class by running a query from the database and loading the appointments on the table view
     * @param url
     * @param resourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //establish connection to database
            JDBC.openConnection();

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
            startTimeCol.setCellValueFactory(new PropertyValueFactory<>("LocalStartTimeIn12HourFormat"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<>("LocalEndTimeIn12HourFormat"));
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
            JDBC.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * This method sends the user to the add appointment view
     * @param actionEvent this method is called when the user clicks the Add Appointment button
     * @throws IOException
     */
    public void onAddAppt(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("add-appointment-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 500, 700);
        stage.setTitle("Add Appointment");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method sends the user to the Update Appointment view
     * @param actionEvent this method is called when the user clicks on the Update Appointment button
     * @throws IOException
     * @throws SQLException
     */
    public void onUpdateAppt(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader updateApptLoader = new FXMLLoader();
        updateApptLoader.setLocation(getClass().getResource("update-appointment-view.fxml"));
        updateApptLoader.load();
        UpdateAppointmentViewController updateApptController = updateApptLoader.getController();
        if (appointmentTable.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setTitle("Error");
            errorMessage.setContentText("Must select an appointment");
            errorMessage.showAndWait();
            return;
        }
        updateApptController.sendApptData((Appointment) appointmentTable.getSelectionModel().getSelectedItem());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent scene = updateApptLoader.getRoot();
        stage.setTitle("Update Appointment");
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method deletes a selected appointment from the table view
     * @param actionEvent this method is called when a user selects an appt and clicks the Delete Appointment button
     * @throws IOException
     * @throws SQLException
     */
    public void onDeleteAppt(ActionEvent actionEvent) throws IOException, SQLException {
        JDBC.openConnection();

        //confirm with the user
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this appointment?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //get the Appointment ID from the appointment the user selects on the table
            Appointment selectedRow = (Appointment) appointmentTable.getSelectionModel().getSelectedItem();
            int Appointment_ID = selectedRow.getAppointment_ID();

            //use this appointment ID to run a query to delete the appointment from the table
            String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,Appointment_ID);
            preparedStatement.executeUpdate();

            Alert successMessage = new Alert(Alert.AlertType.INFORMATION);
            successMessage.setTitle("Deletion Successful");
            successMessage.setContentText("Appointment " + selectedRow.getAppointment_ID()  + " of type " + selectedRow.getType() + " has been successfully deleted");
            successMessage.showAndWait();

            //then update the table view
            data.remove(selectedRow);
            appointmentTable.setItems(data);
        }
        JDBC.closeConnection();
    }

    /**
     * This method sends the user back to the main menu
     * @param actionEvent This method is called when the user clicks the Main Menu button
     * @throws IOException
     */
    public void onReturnToMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method displayes all appointments on the table view
     * @param actionEvent this method is called when the user clicks View All
     */
    public void onViewAll(ActionEvent actionEvent) {
        appointmentTable.setItems(data);
    }

    /**
     * This method filters the appointments in the table view by the current month
     * Note: A month according to UTC time
     * @param actionEvent this method is called when a user clicks the sort by month button
     */
    public void onSortByMonth(ActionEvent actionEvent) {
        appointmentTable.setItems(dataByMonth);
    }

    /**
     * This method filters the appointments in the table view by the current week
     * Note: A week according to UTC time
     * @param actionEvent this method is called when a user clicks the sort by week button
     */
    public void onSorthByWeek(ActionEvent actionEvent) {
        appointmentTable.setItems(dataByWeek);
    }
}
