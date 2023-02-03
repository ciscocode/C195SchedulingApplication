package c195.c195schedulingapplication;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static helper.JDBC.connection;


import static java.sql.DriverManager.getConnection;

public class UpdateAppointmentViewController {
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
    boolean successfulUpdate = false;


    public void updateAppt() throws SQLException {
        //connect to database
        JDBC.openConnection();

        //gather all the inputs from text fields - title, description, location, type
        Appointment_ID = Integer.parseInt(apptIDTextField.getText());
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
        Customer_ID = (int) customerIDBox.getValue();
        User_ID = (int) userIDBox.getValue();

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

        //check to see if the end date of the appointment is valid
        if (endDate.isBefore(startDate)) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("The end date can not be before the start date");
            errorMessage.showAndWait();
            return;
        }

        if (startDate.isBefore(LocalDate.now())) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You can't set an appointment in the past. Please select a valid date.");
            errorMessage.showAndWait();
            return;
        }

        if (endDate.isBefore(LocalDate.now())) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You can't set an appointment in the past. Please select a valid date.");
            errorMessage.showAndWait();
            return;
        }

        //check to see if the start date is on a weekend
        if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("An appointment can not be scheduled on the weekend.");
            errorMessage.showAndWait();
            return;
        }

        //check to see if the end date is on a weekend
        if (endDate.getDayOfWeek() == DayOfWeek.SATURDAY || endDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("An appointment can not be set on the weekend.");
            errorMessage.showAndWait();
            return;
        }

        //run a query to update title
        String titleQuery = "UPDATE appointments SET Title = ? WHERE Appointment_ID = ?";
        PreparedStatement titleStatement = connection.prepareStatement(titleQuery);
        titleStatement.setString(1,Title);
        titleStatement.setInt(2,Appointment_ID);
        titleStatement.executeUpdate();

        //run a query to update description
        String descriptionQuery = "UPDATE appointments SET Description = ? WHERE Appointment_ID = ?";
        PreparedStatement descriptionStatement = connection.prepareStatement(descriptionQuery);
        descriptionStatement.setString(1,Description);
        descriptionStatement.setInt(2,Appointment_ID);
        descriptionStatement.executeUpdate();

        //run a query to update location
        String locationQuery = "UPDATE appointments SET Location = ? WHERE Appointment_ID = ?";
        PreparedStatement locationStatement = connection.prepareStatement(locationQuery);
        locationStatement.setString(1,Location);
        locationStatement.setInt(2,Appointment_ID);
        locationStatement.executeUpdate();

        //run a query to update type
        String typeQuery = "UPDATE appointments SET Type = ? WHERE Appointment_ID = ?";
        PreparedStatement typeStatement = connection.prepareStatement(typeQuery);
        typeStatement.setString(1,Type);
        typeStatement.setInt(2,Appointment_ID);
        typeStatement.executeUpdate();

        //run a query to update Customer ID
        String customerQuery = "UPDATE appointments SET Customer_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement customerStatement = connection.prepareStatement(customerQuery);
        customerStatement.setInt(1,Customer_ID);
        customerStatement.setInt(2,Appointment_ID);
        customerStatement.executeUpdate();

        //run a query to update User ID
        String userQuery = "UPDATE appointments SET User_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement userStatement = connection.prepareStatement(userQuery);
        userStatement.setInt(1,User_ID);
        userStatement.setInt(2,Appointment_ID);
        userStatement.executeUpdate();

        //run a query to update contact ID
        String updateContactQuery = "UPDATE appointments SET Contact_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement updateContactStatement = connection.prepareStatement(updateContactQuery);
        updateContactStatement.setInt(1,User_ID);
        updateContactStatement.setInt(2,Appointment_ID);
        updateContactStatement.executeUpdate();

        //run a query to update start time
        String startTimeQuery = "UPDATE appointments SET Start = ? WHERE Appointment_ID = ?";
        PreparedStatement startTimeStatement = connection.prepareStatement(startTimeQuery);
        startTimeStatement.setTimestamp(1,startTimestamp);
        startTimeStatement.setInt(2,Appointment_ID);
        startTimeStatement.executeUpdate();

        //run a query to update end time
        String endTimeQuery = "UPDATE appointments SET End = ? WHERE Appointment_ID = ?";
        PreparedStatement endTimeStatement = connection.prepareStatement(endTimeQuery);
        endTimeStatement.setTimestamp(1,endTimestamp);
        endTimeStatement.setInt(2,Appointment_ID);
        endTimeStatement.executeUpdate();

        successfulUpdate = true;
        JDBC.closeConnection();
    }

    public void sendApptData(Appointment appt) throws SQLException {
        //fill in the text fields
        apptIDTextField.setText(String.valueOf(appt.getAppointment_ID()));
        titleTextField.setText(String.valueOf(appt.getTitle()));
        typeTextField.setText(String.valueOf(appt.getType()));
        descriptionTextField.setText(String.valueOf(appt.getDescription()));
        locationTextField.setText(String.valueOf(appt.getLocation()));

        //load the spinners with possible hour/minute options
        SpinnerValueFactory<Integer> startHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12);
        SpinnerValueFactory<Integer> endHourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12);
        SpinnerValueFactory<Integer> startMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(00, 59);
        SpinnerValueFactory<Integer> endMinuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(00, 59);

        startHourSpinner.setValueFactory(startHourValueFactory);
        endHourSpinner.setValueFactory(endHourValueFactory);
        startMinuteSpinner.setValueFactory(startMinuteValueFactory);
        endMinuteSpinner.setValueFactory(endMinuteValueFactory);

        //set the values of the spinners
        startHourSpinner.getValueFactory().setValue(appt.getStartTime().getHour());
        endHourSpinner.getValueFactory().setValue(appt.getEndTime().getHour());
        startMinuteSpinner.getValueFactory().setValue(appt.getStartTime().getMinute());
        endMinuteSpinner.getValueFactory().setValue(appt.getEndTime().getMinute());

        //set the values of the Date Pickers for the Start & End date
        startDatePicker.setValue(appt.getStartTime().toLocalDate());
        endDatePicker.setValue(appt.getEndTime().toLocalDate());

        LocalDate today = LocalDate.now();
        //set the valid dates for the start date
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(today) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                }
            }
        });

        //set the valid dates for the end date
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(today) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                }
            }
        });

        //load the customer ID, user ID, and Contact combo boxes
        try {
            //create the observable array lists you'll use for the Combo Boxes
            ObservableList<Integer> customerIDList = FXCollections.observableArrayList();
            ObservableList<Integer> userIDList = FXCollections.observableArrayList();
            ObservableList<String> contactList = FXCollections.observableArrayList();

            //connect to the database
            JDBC.openConnection();

            //run a query for the Customer IDs
            Statement customerStatement = connection.createStatement();
            ResultSet customerResultSet = customerStatement.executeQuery("SELECT DISTINCT Customer_ID FROM customers");

            //add the customer ids into the observable array list
            while(customerResultSet.next()) {
                customerIDList.add(customerResultSet.getInt("Customer_ID"));
            }
            //set the items into the combo box
            customerIDBox.setItems(customerIDList);
            customerIDBox.setValue(appt.getCustomer_ID());

            //run a query for the User IDs
            Statement userStatement = connection.createStatement();
            ResultSet userResultSet = userStatement.executeQuery("SELECT DISTINCT User_ID FROM users");

            //add the user ids into the observable array list
            while(userResultSet.next()) {
                userIDList.add(userResultSet.getInt("User_ID"));
            }

            //set the items into the combo box
            userIDBox.setItems(userIDList);
            userIDBox.setValue(appt.getUser_ID());

            //run a query to get the contact names
            Statement contactStatement = connection.createStatement();
            ResultSet contactResultSet = contactStatement.executeQuery("SELECT Contact_Name FROM contacts");

            //add the contacts into the observable array list
            while(contactResultSet.next()) {
                contactList.add(contactResultSet.getString("Contact_Name"));
            }

            //set the items into the combo box
            contactBox.setItems(contactList);
            contactBox.setValue(appt.getContactName());

            JDBC.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        updateAppt();

        if (successfulUpdate == false) {
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

    public void onStartDateSelection(ActionEvent actionEvent) {
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(startDatePicker.getValue()) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                }
            }
        });
    }
}
