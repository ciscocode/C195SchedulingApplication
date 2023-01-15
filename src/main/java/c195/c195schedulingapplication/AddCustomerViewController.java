package c195.c195schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AddCustomerViewController implements Initializable {

    public TextField nameTextField;
    public TextField addressTextField;
    public TextField postalCodeTextField;
    public TextField phoneTextField;
    public ComboBox countryBox;
    public ComboBox divisionBox;
    //Id, name, address, postal, phone, country, division
    int customerID;
    String name;
    String address;
    String postalCode;
    String phoneNumber;
    String country;
    String division;
    LocalDateTime createDate;
    LocalDateTime lastUpdate;
    String createdBy;
    String lastUpdatedBy;
    int division_id;
    int country_id;

    //Since the list of countries is small at Only 3, I created an ObservableArrayList for it to simplify things
    ObservableList<String> countryList = FXCollections.observableArrayList("U.S", "UK","Canada");

    public void insertCustomer() throws SQLException {

        //Start by connecting to the database
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //get the last ID used from the database. This is so that I can increment the ID by one for the new customer
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Customer_ID FROM customers WHERE Customer_ID = (SELECT MAX(Customer_ID) FROM customers)");
        while (resultSet.next()) {
            customerID = resultSet.getInt("Customer_ID");
        }
        resultSet.close();

        //then increment the id by one
        customerID++;

        //gather the data from the text inputs
        name = nameTextField.getText();
        address = addressTextField.getText();
        postalCode = postalCodeTextField.getText();
        phoneNumber = phoneTextField.getText();

        //create the other parameters needed to create a Customer
        createDate = LocalDateTime.now();
        Timestamp createDateTimestamp = Timestamp.valueOf(createDate);
        createdBy = "cisco"; //REMEMBER TO CHANGE THIS WHEN SUBMITTING
        lastUpdate = LocalDateTime.now();
        Timestamp lastUpdateTimestamp = Timestamp.valueOf(createDate);

        lastUpdatedBy = "cisco";

        //I wont need this data to create a new Customer but I will use it in the table view.
        country = (String) countryBox.getValue();
        division = (String) divisionBox.getValue();

        //use the inputs above to create a new Customer object
        Customer newCustomer = new Customer(
                customerID,
                name,
                address,
                postalCode,
                phoneNumber,
                createDate,
                createdBy,
                lastUpdate,
                lastUpdatedBy,
                division_id
        );

        //Once a new customer is created push it to the MySQL Database
        String sql = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,customerID);
        preparedStatement.setString(2,name);
        preparedStatement.setString(3,address);
        preparedStatement.setString(4,postalCode);
        preparedStatement.setString(5,phoneNumber);
        preparedStatement.setTimestamp(6,createDateTimestamp);
        preparedStatement.setString(7,createdBy);
        preparedStatement.setTimestamp(8,lastUpdateTimestamp);
        preparedStatement.setString(9,lastUpdatedBy);
        preparedStatement.setInt(10,division_id);

        //ResultSet resultSet2 = preparedStatement.executeQuery();
        int rowsAffected = preparedStatement.executeUpdate();
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set the country combo box items
        countryBox.setItems(countryList);
    }

    public void onCountrySelection(ActionEvent actionEvent) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //get the value of the country & use this to get the country id.
        country = (String) countryBox.getValue();
        country_id = countryList.indexOf(country) + 1;
        System.out.println(country_id);

        //run the query to find the divisions by country id
        String sql = "SELECT Division FROM first_level_divisions WHERE Country_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,country_id);
        ResultSet resultSet = preparedStatement.executeQuery();

        //create observableList to store division list
        ObservableList<String> divisionList = FXCollections.observableArrayList();

        //add divisions into the list
        while (resultSet.next()) {
            String division = resultSet.getString(1);
            divisionList.add(division);
        }

        //then add the list to the combo box
        divisionBox.setItems(divisionList);
    }

    public void onDivisionSelection(ActionEvent actionEvent) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //gather the division from the selected box
        division = (String) divisionBox.getValue();

        String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, division);
        ResultSet resultSet = preparedStatement.executeQuery();

        //set the returned result as the division id
        while (resultSet.next()) {
            division_id = resultSet.getInt(1);
        }
    }

    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        insertCustomer();

        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

}
