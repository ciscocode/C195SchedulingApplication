package c195.c195schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class Customer {
    private int Customer_ID;
    private String Customer_Name;
    private String Address;
    private String Postal_Code;
    private String Phone;
    private LocalDateTime Create_Date;
    private String Created_By;
    private LocalDateTime Last_Update;
    private String Last_Updated_By;
    private int Division_ID;

    public Customer(int customer_ID, String customer_Name, String address, String postal_Code, String phone, LocalDateTime create_Date, String created_By, LocalDateTime last_Update, String last_Updated_By, int division_ID) {
        this.Customer_ID = customer_ID;
        this.Customer_Name = customer_Name;
        this.Address = address;
        this.Postal_Code = postal_Code;
        this.Phone = phone;
        this.Create_Date = create_Date;
        this.Created_By = created_By;
        this.Last_Update = last_Update;
        this.Last_Updated_By = last_Updated_By;
        this.Division_ID = division_ID;
    }

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(int customer_ID) {
        this.Customer_ID = customer_ID;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        this.Customer_Name = customer_Name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getPostal_Code() {
        return Postal_Code;
    }

    public void setPostal_Code(String postal_Code) {
        this.Postal_Code = postal_Code;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
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

    public int getDivision_ID() {
        return Division_ID;
    }

    public void setDivision_ID(int division_ID) {
        this.Division_ID = division_ID;
    }

    public String getCountry() throws SQLException {
        //establish connection to database
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //First, get the Country ID
        String sql1 = "SELECT Country_ID FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);

        //set the value of the parameter
        preparedStatement1.setInt(1,Division_ID);

        //execute prepared statement
        ResultSet resultSet1 = preparedStatement1.executeQuery();

        //then save the Country_ID in a variable
        int Country_ID = 0;
        if (resultSet1.next()) {
            Country_ID = resultSet1.getInt("Country_ID");
        }

        //Then we will repeat the same steps as above. This time using the Country ID to find the name of the country
        String sql2 = "SELECT Country FROM countries WHERE Country_ID = ?";
        PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
        preparedStatement2.setInt(1,Country_ID);
        ResultSet resultSet2 = preparedStatement2.executeQuery();
        String country = null;
        if (resultSet2.next()) {
            country = resultSet2.getString("Country");
        }

        return country;
    }

    public String getDivision() throws SQLException {
        //establish connection to database
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        String sql = "SELECT Division FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        //set the value of the parameter
        preparedStatement.setInt(1,Division_ID);

        //execute prepared statement
        ResultSet resultSet = preparedStatement.executeQuery();

        //then save the division
        String division = null;
        if (resultSet.next()) {
            division = resultSet.getString("Division");
        }
        return division;
    }
}
