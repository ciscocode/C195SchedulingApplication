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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

import static helper.JDBC.connection;


public class AddAppointmentViewController implements Initializable {
    public TextField apptIDTextField;
    public TextField titleTextField;
    public TextField descriptionTextField;
    public TextField locationTextField;
    public TextField typeTextField;
    public ComboBox contactBox;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public ComboBox customerIDBox;
    public ComboBox userIDBox;
    public Spinner startHourSpinner;
    public Spinner startMinuteSpinner;
    public Spinner endMinuteSpinner;
    public Spinner endHourSpinner;
    int Appointment_ID;
    String Title;
    String Description;
    String Location;
    String Type;
    LocalDateTime StartTime;
    LocalDateTime EndTime;
    LocalDateTime Create_Date;
    String Created_By;
    LocalDateTime Last_Update;
    String Last_Updated_By;
    int Customer_ID;
    int User_ID;
    int Contact_ID;
    boolean successfulAddition = false;

    public void insertAppt() throws SQLException {
        //Start by connecting to the database
        JDBC.openConnection();

        //get the last Appointment ID used from the database. This is so that I can increment the ID by one for the new appointment
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Appointment_ID FROM appointments WHERE Appointment_ID = (SELECT MAX(Appointment_ID) FROM appointments)");
        while (resultSet.next()) {
            Appointment_ID = resultSet.getInt("Appointment_ID");
        }
        resultSet.close();

        //then increment the id by one
        Appointment_ID++;

        //set the create date, last update time
        Create_Date = LocalDateTime.now();
        Timestamp createDateTimestamp = Timestamp.valueOf(Create_Date);
        Last_Update = LocalDateTime.now();
        Timestamp lastUpdateTimestamp = Timestamp.valueOf(Last_Update);

        //set the user names. CHANGE THESE LATER
        Created_By = "cisco";
        Last_Updated_By = "cisco";

        //gather the title, description, location, and type from the text fields
        Title = titleTextField.getText();
        Description = descriptionTextField.getText();
        Location = locationTextField.getText();
        Type = typeTextField.getText();

        //check for input errors
        if (Title.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a title");
            errorMessage.showAndWait();
            return;
        }
        if (Description.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a description");
            errorMessage.showAndWait();
            return;
        }
        if (Location.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a location");
            errorMessage.showAndWait();
            return;
        }
        if (Type.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a type");
            errorMessage.showAndWait();
            return;
        }

        //check for unselected combo boxes
        if (customerIDBox.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must select a customer id");
            errorMessage.showAndWait();
            return;
        }

        if (userIDBox.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must select a user id");
            errorMessage.showAndWait();
            return;
        }

        if (contactBox.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must select a contact");
            errorMessage.showAndWait();
            return;
        }

        //combo boxes - customer id, user id,
        Customer_ID = Integer.valueOf((String) customerIDBox.getValue());
        User_ID = Integer.valueOf((String) userIDBox.getValue());

        //run a query to get the contact ID using the contact name selected from the combo box
        String Contact = (String) contactBox.getValue();
        String contactQuery = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        PreparedStatement contactStatement = connection.prepareStatement(contactQuery);
        contactStatement.setString(1,Contact);
        ResultSet contactResultSet = contactStatement.executeQuery();
        while (contactResultSet.next()) {
            Contact_ID = contactResultSet.getInt("Contact_ID");
        }

        //check for unselected hour/minutes on spinners
        if (startHourSpinner.getValue() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must specify the hour value in the appointment start time");
            errorMessage.showAndWait();
            return;
        }
        if (startMinuteSpinner.getValue() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must specify the minute value in the appointment start time");
            errorMessage.showAndWait();
            return;
        }
        if (endHourSpinner.getValue() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must specify the hour value in the appointment end time");
            errorMessage.showAndWait();
            return;
        }
        if (endMinuteSpinner.getValue() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must specify the minute value in the appointment end time");
            errorMessage.showAndWait();
            return;
        }

        //check for empty date pickers
        if (startDatePicker.getValue() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must specify a start date");
            errorMessage.showAndWait();
            return;
        }

        if (endDatePicker.getValue() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must specify an end date");
            errorMessage.showAndWait();
            return;
        }

        //get the start time
        LocalDate startDate = startDatePicker.getValue();
        Integer startHour = (Integer) startHourSpinner.getValue();
        Integer startMinute = (Integer) startMinuteSpinner.getValue();
        StartTime = LocalDateTime.of(startDate, LocalTime.of(startHour,startMinute));
        Timestamp startTimestamp = Timestamp.valueOf(StartTime);

        //get the end time
        LocalDate endDate = endDatePicker.getValue();
        Integer endHour = (Integer) endHourSpinner.getValue();
        Integer endMinute = (Integer) endMinuteSpinner.getValue();
        EndTime = LocalDateTime.of(endDate, LocalTime.of(endHour,endMinute));
        Timestamp endTimestamp = Timestamp.valueOf(EndTime);

        //use all the inputs to create a new appointment
        Appointment newAppt = new Appointment(
                Appointment_ID,
                Title,
                Description,
                Location,
                Type,
                StartTime,
                EndTime,
                Create_Date,
                Created_By,
                Last_Update,
                Last_Updated_By,
                Customer_ID,
                User_ID,
                Contact_ID
        );

        //once a new appointment is created push it into the MySQL database
        String insertQuery = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setInt(1,Appointment_ID);
        insertStatement.setString(2,Title);
        insertStatement.setString(3,Description);
        insertStatement.setString(4,Location);
        insertStatement.setString(5,Type);
        insertStatement.setTimestamp(6,startTimestamp);
        insertStatement.setTimestamp(7,endTimestamp);
        insertStatement.setTimestamp(8,createDateTimestamp);
        insertStatement.setString(9,Created_By);
        insertStatement.setTimestamp(10,lastUpdateTimestamp);
        insertStatement.setString(11,Last_Updated_By);
        insertStatement.setInt(12,Customer_ID);
        insertStatement.setInt(13,User_ID);
        insertStatement.setInt(14,Contact_ID);

        insertStatement.executeUpdate();

        successfulAddition = true;
        JDBC.closeConnection();
    }
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //load the spinners with possible hour/minute options
        SpinnerValueFactory<Integer> startHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12);
        SpinnerValueFactory<Integer> endHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12);
        SpinnerValueFactory<Integer> startMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(00, 59);
        SpinnerValueFactory<Integer> endMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(00, 59);

        startHourSpinner.setValueFactory(startHourValueFactory);
        endHourSpinner.setValueFactory(endHourValueFactory);
        startMinuteSpinner.setValueFactory(startMinuteValueFactory);
        endMinuteSpinner.setValueFactory(endMinuteValueFactory);

        //load the customer ID, user ID, and Contact combo boxes
        try {
            //create the observable array lists you'll use for the Combo Boxes
            ObservableList<String> customerIDList = FXCollections.observableArrayList();
            ObservableList<String> userIDList = FXCollections.observableArrayList();
            ObservableList<String> contactList = FXCollections.observableArrayList();

            //connect to the database
            JDBC.openConnection();

            //run a query for the Customer IDs
            Statement customerStatement = connection.createStatement();
            ResultSet customerResultSet = customerStatement.executeQuery("SELECT DISTINCT Customer_ID FROM appointments");

            //add the customer ids into the observable array list
            while(customerResultSet.next()) {
                customerIDList.add(customerResultSet.getString("Customer_ID"));
            }
            //set the items into the combo box
            customerIDBox.setItems(customerIDList);

            //run a query for the User IDs
            Statement userStatement = connection.createStatement();
            ResultSet userResultSet = userStatement.executeQuery("SELECT DISTINCT User_ID FROM appointments");

            //add the user ids into the observable array list
            while(userResultSet.next()) {
                userIDList.add(userResultSet.getString("User_ID"));
            }

            //set the items into the combo box
            userIDBox.setItems(userIDList);

            //run a query to get the contact names
            Statement contactStatement = connection.createStatement();
            ResultSet contactResultSet = contactStatement.executeQuery("SELECT Contact_Name FROM contacts");

            //add the contacts into the observable array list
            while(contactResultSet.next()) {
                contactList.add(contactResultSet.getString("Contact_Name"));
            }

            //set the items into the combo box
            contactBox.setItems(contactList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        JDBC.closeConnection();
    }
    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        insertAppt();

        if (successfulAddition == false) {
            return;
        }

        Parent root = FXMLLoader.load(getClass().getResource("appointment-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1400, 800);
        stage.setTitle("Appointments");
        stage.setScene(scene);
        stage.show();
    }
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("appointment-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1400, 800);
        stage.setTitle("Appointments");
        stage.setScene(scene);
        stage.show();
    }
}
