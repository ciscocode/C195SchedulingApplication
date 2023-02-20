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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import static helper.JDBC.connection;

/** This class has the functionality to add a customer to the MySQL database.*/
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
    boolean successfulAddition = false;

    //Since the list of countries is small at Only 3, I created an ObservableArrayList for it to simplify things
    ObservableList<String> countryList = FXCollections.observableArrayList("U.S", "UK","Canada");

    /**This method inserts a customer into the database while also checking for user input error. */
    public void insertCustomer() throws SQLException {

        //Start by connecting to the database
        JDBC.openConnection();

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

        if (!inputErrorCheck()) {
            return;
        };

        //create the other parameters needed to create a Customer
        createDate = LocalDateTime.now();
        Timestamp createDateTimestamp = Timestamp.valueOf(createDate);
        createdBy = JDBC.userName; //REMEMBER TO CHANGE THIS WHEN SUBMITTING
        lastUpdate = LocalDateTime.now();
        Timestamp lastUpdateTimestamp = Timestamp.valueOf(createDate);

        lastUpdatedBy = JDBC.userName; //fix later

        //find the country and division
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
        successfulAddition = true;
        JDBC.closeConnection();
    }

    /** This method initializes by setting the available countries into the country combo box. */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set the country combo box items
        countryBox.setItems(countryList);
    }

    /**This method populates the divisions combo box based on the country selected by the user
     * @param actionEvent The method is called when a user selects a country in the country combo box
     * @throws SQLException
     */
    public void onCountrySelection(ActionEvent actionEvent) throws SQLException {
        JDBC.openConnection();

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
        JDBC.closeConnection();
    }

    /**This method checks for user input error such as blank text fields or unselected combo boxes. */
    public boolean inputErrorCheck() {
        //check for input errors
        if (name.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a name");
            errorMessage.showAndWait();
            return false;
        }

        if (address.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter an address");
            errorMessage.showAndWait();
            return false;
        }

        if (postalCode.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a postal code");
            errorMessage.showAndWait();
            return false;
        }

        if (phoneNumber.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a phone number");
            errorMessage.showAndWait();
            return false;
        }
        //check for unselected country/division combo boxes
        if (countryBox.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must select a country!");
            errorMessage.showAndWait();
            return false;
        }

        if (divisionBox.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must select a State or Province!");
            errorMessage.showAndWait();
            return false;
        }
        return true;
    }

    /**This method finds the division id based on the selected division. The division id is needed when adding a customer to the database
     * @param actionEvent This method is called when the user selects a division from the combo box
     * @throws SQLException
     */
    public void onDivisionSelection(ActionEvent actionEvent) throws SQLException {
        JDBC.openConnection();

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

        JDBC.closeConnection();
    }

    /**This method calls the insertCustomer method. If a customer was successfully added to the database then the user will be sent to the customer table view*
     * @param actionEvent this method is called when the user clicks the save button
     * @throws IOException
     * @throws SQLException
     */
    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        insertCustomer();

        if (successfulAddition == false) {
            return;
        }

        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

    /**This method cancels the addition of a customer and returns the user to the customer table view
     * @param actionEvent this method is called when the user clicks the cancel button
     * @throws IOException
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

}
