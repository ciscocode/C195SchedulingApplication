package c195.c195schedulingapplication;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import static helper.JDBC.connection;

/**This defines the user Class and creates its variables, getters, and setters.*/
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

    public Customer(int customer_ID,
                    String customer_Name,
                    String address,
                    String postal_Code,
                    String phone,
                    LocalDateTime create_Date,
                    String created_By,
                    LocalDateTime last_Update,
                    String last_Updated_By,
                    int division_ID) {
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

    /**
     * @return the customer id
     */
    public int getCustomer_ID() {
        return Customer_ID;
    }

    /**
     * @param customer_ID set the customer id
     */
    public void setCustomer_ID(int customer_ID) {
        this.Customer_ID = customer_ID;
    }

    /**
     * @return the customer name
     */
    public String getCustomer_Name() {
        return Customer_Name;
    }

    /**
     * @param customer_Name sets the customer name
     */
    public void setCustomer_Name(String customer_Name) {
        this.Customer_Name = customer_Name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return Address;
    }

    /**
     * @param address sets the address
     */
    public void setAddress(String address) {
        this.Address = address;
    }

    /**
     * @return the postal code
     */
    public String getPostal_Code() {
        return Postal_Code;
    }

    /**
     * @param postal_Code sets the postal code
     */
    public void setPostal_Code(String postal_Code) {
        this.Postal_Code = postal_Code;
    }

    /**
     * @return returns the phone number
     */
    public String getPhone() {
        return Phone;
    }

    /**
     * @param phone sets phone number
     */
    public void setPhone(String phone) {
        this.Phone = phone;
    }

    /**
     * @return the create date
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
     * @return the user who creates the customer
     */
    public String getCreated_By() {
        return Created_By;
    }

    /**
     * @param created_By sets the user who creates the customer
     */
    public void setCreated_By(String created_By) {
        this.Created_By = created_By;
    }

    /**
     * @return the last time the customers info was updated
     */
    public LocalDateTime getLast_Update() {
        return Last_Update;
    }

    /**
     * @param last_Update sets the last time customer info was udpaated
     */
    public void setLast_Update(LocalDateTime last_Update) {
        this.Last_Update = last_Update;
    }

    /**
     * @return the name of the user who last updated the customer
     */
    public String getLast_Updated_By() {
        return Last_Updated_By;
    }

    /**
     * @param last_Updated_By sets the username of the last person who udpated the customer
     */
    public void setLast_Updated_By(String last_Updated_By) {
        this.Last_Updated_By = last_Updated_By;
    }

    /**
     * @return the division id of the customer
     */
    public int getDivision_ID() {
        return Division_ID;
    }

    /**
     * @param division_ID sets the division id
     */
    public void setDivision_ID(int division_ID) {
        this.Division_ID = division_ID;
    }

    /**
     * This method runs a query using the customers division id to find the country name of the customer
     * @return country. The String of the country
     * @throws SQLException
     */
    public String getCountry() throws SQLException {
        //establish connection to database
        JDBC.openConnection();

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
        JDBC.closeConnection();
        return country;
    }

    /**
     * This method runs a query to return the division of the user
     * @return the division
     * @throws SQLException
     */
    public String getDivision() throws SQLException {
        //establish connection to database
        JDBC.openConnection();

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
        JDBC.closeConnection();
        return division;
    }
}
