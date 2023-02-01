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
import java.util.ResourceBundle;
import static helper.JDBC.connection;

import static java.sql.DriverManager.getConnection;

public class ContactAppointmentReportViewController implements Initializable {
    public ComboBox contactBox;
    public Label selectionLabel;
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

    ObservableList<String> contactList = FXCollections.observableArrayList();
    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public void onReturnToReportMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("reports-menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 360, 469);
        stage.setTitle("Reports Menu");
        stage.setScene(scene);
        stage.show();
    }


    public void loadContactBox() throws SQLException {
        JDBC.openConnection();

        //run a query to get the contact names
        Statement contactStatement = connection.createStatement();
        ResultSet contactResultSet = contactStatement.executeQuery("SELECT Contact_Name FROM contacts");

        //add the contacts into the observable array list
        while(contactResultSet.next()) {
            contactList.add(contactResultSet.getString("Contact_Name"));
        }

        //set the items into the combo box
        contactBox.setItems(contactList);

        JDBC.closeConnection();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadContactBox();

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
                appointmentList.add(appointment);
            }

            //set items into the table view
            appointmentTable.setItems(appointmentList);

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

            JDBC.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void onSelectedOption(ActionEvent actionEvent) throws SQLException {
        JDBC.openConnection();

        int contactID = 0;
        String selectedOption = (String) contactBox.getSelectionModel().getSelectedItem();
        ObservableList<Appointment> filteredByContact = FXCollections.observableArrayList();

        //Find the contact ID of the selected contact
        String contactIDConversionQuery = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        PreparedStatement contactIDStatement = connection.prepareStatement(contactIDConversionQuery);
        contactIDStatement.setString(1,selectedOption);
        ResultSet contactIDResultSet = contactIDStatement.executeQuery();
        while (contactIDResultSet.next()) {
            contactID = contactIDResultSet.getInt("Contact_ID");
        }

        //use the contact ID to filter the appointment results
        String filterQuery = "SELECT * FROM appointments WHERE Contact_ID = ?";
        PreparedStatement filterStatement = connection.prepareStatement(filterQuery);
        filterStatement.setInt(1,contactID);

        //load results into the observableList
        ResultSet monthResultSet = filterStatement.executeQuery();
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
            filteredByContact.add(appointment);
        }
        //fill in the table
        appointmentTable.setItems(filteredByContact);
        JDBC.closeConnection();
    }

    public void onViewAll(ActionEvent actionEvent) {
        appointmentTable.setItems(appointmentList);
    }
}
