package c195.c195schedulingapplication;

import helper.JDBC;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static helper.JDBC.connection;


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

    public int getAppointment_ID() {
        return Appointment_ID;
    }

    public void setAppointment_ID(int appointment_ID) {
        this.Appointment_ID = appointment_ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public LocalDateTime getStartTime() {
        return StartTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.StartTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return EndTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.EndTime = endTime;
    }

    public LocalDateTime getCreate_Date() {
        return Create_Date;
    }

    public void setCreate_Date(LocalDateTime create_Date) {
        this.Create_Date = create_Date;
    }

    public String getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(String created_By) {
        this.Created_By = created_By;
    }

    public LocalDateTime getLast_Update() {
        return Last_Update;
    }

    public void setLast_Update(LocalDateTime last_Update) {
        this.Last_Update = last_Update;
    }

    public String getLast_Updated_By() {
        return Last_Updated_By;
    }

    public void setLast_Updated_By(String last_Updated_By) {
        this.Last_Updated_By = last_Updated_By;
    }

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(int customer_ID) {
        this.Customer_ID = customer_ID;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        this.User_ID = user_ID;
    }

    public int getContact_ID() {
        return Contact_ID;
    }

    public void setContact_ID(int contact_ID) {
        this.Contact_ID = contact_ID;
    }

    public LocalDateTime getLocalStartTime() {
        //get time zone of users device
        ZoneId deviceTimeZone = ZoneId.systemDefault();
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime startDateTimeLocal = StartTime.atZone(utc).withZoneSameInstant(deviceTimeZone).toLocalDateTime();
        return startDateTimeLocal;
    }

    public LocalDateTime getLocalEndTime() {
        ZoneId deviceTimeZone = ZoneId.systemDefault();
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime endDateTimeLocal = EndTime.atZone(utc).withZoneSameInstant(deviceTimeZone).toLocalDateTime();
        return endDateTimeLocal;
    }

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
