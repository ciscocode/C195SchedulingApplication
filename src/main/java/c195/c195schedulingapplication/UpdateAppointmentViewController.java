package c195.c195schedulingapplication;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    public void updateAppt() throws SQLException {
        //connect to database
        Connection connection = getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //gather all the inputs from text fields - title, description, location, type
        Title = titleTextField.getText();
        Description = descriptionTextField.getText();
        Location = locationTextField.getText();
        Type = typeTextField.getText();

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

        //load the customer ID, user ID, and Contact combo boxes
        try {
            //create the observable array lists you'll use for the Combo Boxes
            ObservableList<String> customerIDList = FXCollections.observableArrayList();
            ObservableList<String> userIDList = FXCollections.observableArrayList();
            ObservableList<String> contactList = FXCollections.observableArrayList();

            //connect to the database
            Connection connection = getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

            //run a query for the Customer IDs
            Statement customerStatement = connection.createStatement();
            ResultSet customerResultSet = customerStatement.executeQuery("SELECT DISTINCT Customer_ID FROM appointments");

            //add the customer ids into the observable array list
            while(customerResultSet.next()) {
                customerIDList.add(customerResultSet.getString("Customer_ID"));
            }
            //set the items into the combo box
            customerIDBox.setItems(customerIDList);
            customerIDBox.setValue(appt.getCustomer_ID());

            //run a query for the User IDs
            Statement userStatement = connection.createStatement();
            ResultSet userResultSet = userStatement.executeQuery("SELECT DISTINCT User_ID FROM appointments");

            //add the user ids into the observable array list
            while(userResultSet.next()) {
                userIDList.add(userResultSet.getString("User_ID"));
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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }

    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        updateAppt();

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
