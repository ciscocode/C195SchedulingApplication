package c195.c195schedulingapplication;

import helper.JDBC;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static helper.JDBC.connection;

/**This creates the appointment class along with its variables, and getters, and setters.*/
public class Appointment {
    private int Appointment_ID;
    private String Title;
    private String Description;
    private String Location;
    private String Type;
    private LocalDateTime StartTime;
    private LocalDateTime EndTime;
    private LocalDateTime Create_Date;
    private String Created_By;
    private LocalDateTime Last_Update;
    private String Last_Updated_By;
    private int Customer_ID;
    private int User_ID;
    private int Contact_ID;

    public Appointment(int appointment_ID,
                       String title,
                       String description,
                       String location,
                       String type,
                       LocalDateTime startTime,
                       LocalDateTime endTime,
                       LocalDateTime create_Date,
                       String created_By,
                       LocalDateTime last_Update,
                       String last_Updated_By,
                       int customer_ID,
                       int user_ID,
                       int contact_ID) {
                            this.Appointment_ID = appointment_ID;
                            this.Title = title;
                            this.Description = description;
                            this.Location = location;
                            this.Type = type;
                            this.StartTime = startTime;
                            this.EndTime = endTime;
                            this.Create_Date = create_Date;
                            this.Created_By = created_By;
                            this.Last_Update = last_Update;
                            this.Last_Updated_By = last_Updated_By;
                            this.Customer_ID = customer_ID;
                            this.User_ID = user_ID;
                            this.Contact_ID = contact_ID;
    }

    /**
     * @return the Appointment ID
     */
    public int getAppointment_ID() {
        return Appointment_ID;
    }

    /**
     * sets the appointment id
     * @param appointment_ID
     */
    public void setAppointment_ID(int appointment_ID) {
        this.Appointment_ID = appointment_ID;
    }

    /**
     * @return returns the title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @param title sets the title
     */
    public void setTitle(String title) {
        this.Title = title;
    }

    /**
     * @return returns the description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @param description sets the description
     */
    public void setDescription(String description) {
        this.Description = description;
    }

    /**
     * @return returns the location
     */
    public String getLocation() {
        return Location;
    }

    /**
     * @param location sets the location
     */
    public void setLocation(String location) {
        this.Location = location;
    }

    /**
     * @return returns the type
     */
    public String getType() {
        return Type;
    }

    /**
     * @param type sets the tupe
     */
    public void setType(String type) {
        this.Type = type;
    }

    /**
     * @return returns the start time
     */
    public LocalDateTime getStartTime() {
        return StartTime;
    }

    /**
     * @param startTime sets the start time
     */
    public void setStartTime(LocalDateTime startTime) {
        this.StartTime = startTime;
    }

    /**
     * @return returns the end time
     */
    public LocalDateTime getEndTime() {
        return EndTime;
    }

    /**
     * @param endTime sets the end time
     */
    public void setEndTime(LocalDateTime endTime) {
        this.EndTime = endTime;
    }

    /**
     * @return returns the create date
     */
    public LocalDateTime getCreate_Date() {
        return Create_Date;
    }

    /**
     * @param create_Date sets the create date
     */
    public void setCreate_Date(LocalDateTime create_Date) {
        this.Create_Date = create_Date;
    }

    /**
     * @return returns the user who creates the appt
     */
    public String getCreated_By() {
        return Created_By;
    }

    /**
     * @param created_By sets the created by user
     */
    public void setCreated_By(String created_By) {
        this.Created_By = created_By;
    }

    /**
     * @return returns the last updated time
     */
    public LocalDateTime getLast_Update() {
        return Last_Update;
    }

    /**
     * @param last_Update sets the last updated time
     */
    public void setLast_Update(LocalDateTime last_Update) {
        this.Last_Update = last_Update;
    }

    /**
     * @return returns the username of the user who last updated the appt
     */
    public String getLast_Updated_By() {
        return Last_Updated_By;
    }

    /**
     * @param last_Updated_By sets the username of the person who last updated the appt
     */
    public void setLast_Updated_By(String last_Updated_By) {
        this.Last_Updated_By = last_Updated_By;
    }

    /**
     * @return returns the customer id
     */
    public int getCustomer_ID() {
        return Customer_ID;
    }

    /**
     * @param customer_ID sets the customer id
     */
    public void setCustomer_ID(int customer_ID) {
        this.Customer_ID = customer_ID;
    }

    /**
     * @return returns the user id
     */
    public int getUser_ID() {
        return User_ID;
    }

    /**
     * @param user_ID sets the user id
     */
    public void setUser_ID(int user_ID) {
        this.User_ID = user_ID;
    }

    /**
     * @return returns the contact id
     */
    public int getContact_ID() {
        return Contact_ID;
    }

    /**
     * @param contact_ID sets the contact id
     */
    public void setContact_ID(int contact_ID) {
        this.Contact_ID = contact_ID;
    }

    /**
     * This method takes the start time and converts it to the users local time
     * @return startDateTimeLocal this stores the start time in the users local time
     */
    public LocalDateTime getLocalStartTime() {
        //get time zone of users device
        ZoneId deviceTimeZone = ZoneId.systemDefault();
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime startDateTimeLocal = StartTime.atZone(utc).withZoneSameInstant(deviceTimeZone).toLocalDateTime();
        return startDateTimeLocal;
    }

    /**
     * This sets the local start time in a user friendly 12 hour format
     * @return formattedTime - the start date time of the appointment
     */
    public String getLocalStartTimeIn12HourFormat() {
        //create 12-hour date/time formatter to later display time in a more readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

        //get time zone of users device
        ZoneId deviceTimeZone = ZoneId.systemDefault();
        LocalDateTime startDateTimeLocal = StartTime.atZone(deviceTimeZone).toLocalDateTime();

        String formattedTime = startDateTimeLocal.format(formatter);
        return formattedTime;
    }

    /**
     * This sets the local end time in a user friendly 12 hour format
     * @return formattedTime - the end date time of the appointment
     */
    public String getLocalEndTimeIn12HourFormat() {
        //create 12-hour date/time formatter to later display time in a more readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

        //get time zone of users device
        ZoneId deviceTimeZone = ZoneId.systemDefault();
        LocalDateTime endDateTimeLocal = EndTime.atZone(deviceTimeZone).toLocalDateTime();

        String formattedTime = endDateTimeLocal.format(formatter);
        return formattedTime;
    }

    /**
     * This method takes the end time and converts it to the users local time
     * @return startDateTimeLocal this stores the end time in the users local time
     */
    public LocalDateTime getLocalEndTime() {
        ZoneId deviceTimeZone = ZoneId.systemDefault();
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime endDateTimeLocal = EndTime.atZone(utc).withZoneSameInstant(deviceTimeZone).toLocalDateTime();
        return endDateTimeLocal;
    }

    /**
     * This method gets the contact name of the appointment using the contact id
     * @return returns the contact name
     * @throws SQLException
     */
    public String getContactName() throws SQLException {
        //establish connection to database
        JDBC.openConnection();

        String sql = "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        //set the value of the parameter
        preparedStatement.setInt(1,Contact_ID);

        //execute prepared statement
        ResultSet resultSet = preparedStatement.executeQuery();

        String contactName = null;
        if (resultSet.next()) {
            contactName = resultSet.getString("Contact_Name");
        }
        JDBC.closeConnection();

        return contactName;
    }

}
