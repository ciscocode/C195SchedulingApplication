package c195.c195schedulingapplication;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import static helper.JDBC.connection;

/**This class creates the functionality to update a customers data in the database.*/
public class UpdateCustomerViewController {
    public TextField customerIDTextField;
    public TextField nameTextField;
    public TextField addressTextField;
    public TextField postalCodeTextField;
    public TextField phoneTextField;
    public ComboBox countryBox;
    public ComboBox divisionBox;
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
    ObservableList<String> countryList = FXCollections.observableArrayList("U.S", "UK","Canada");
    boolean successfulUpdate = false;

    /**
     * This method runs a query to update the customers info based on user input
     * @throws SQLException
     */
    public void updateCustomer() throws SQLException {
        //connect to database
        JDBC.openConnection();

        //save the inputs from the text fields
        customerID = Integer.valueOf(customerIDTextField.getText());
        name = nameTextField.getText();
        address = addressTextField.getText();
        postalCode = postalCodeTextField.getText();
        phoneNumber = phoneTextField.getText();
        division = (String) divisionBox.getValue();

        //check for input errors
        if (name.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a name");
            errorMessage.showAndWait();
            return;
        }

        if (address.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter an address");
            errorMessage.showAndWait();
            return;
        }

        if (postalCode.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a postal code");
            errorMessage.showAndWait();
            return;
        }

        if (phoneNumber.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a phone number");
            errorMessage.showAndWait();
            return;
        }

        //check for country & division box selections
        if (countryBox.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must select a country!");
            errorMessage.showAndWait();
            return;
        }

        if (divisionBox.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must select a State or Province!");
            errorMessage.showAndWait();
            return;
        }

        //run a query to update each customer attribute
        String nameQuery = "UPDATE customers SET Customer_Name = ? WHERE Customer_ID = ?";
        PreparedStatement nameStatement = connection.prepareStatement(nameQuery);
        nameStatement.setString(1,name);
        nameStatement.setInt(2,customerID);
        nameStatement.executeUpdate();

        String addressQuery = "UPDATE customers SET Address = ? WHERE Customer_ID = ?";
        PreparedStatement addressStatement = connection.prepareStatement(addressQuery);
        addressStatement.setString(1,address);
        addressStatement.setInt(2,customerID);
        addressStatement.executeUpdate();

        String postalCodeQuery = "UPDATE customers SET Postal_Code = ? WHERE Customer_ID = ?";
        PreparedStatement postalCodeStatement = connection.prepareStatement(postalCodeQuery);
        postalCodeStatement.setString(1,postalCode);
        postalCodeStatement.setInt(2,customerID);
        postalCodeStatement.executeUpdate();

        String phoneQuery = "UPDATE customers SET Phone = ? WHERE Customer_ID = ?";
        PreparedStatement phoneStatement = connection.prepareStatement(phoneQuery);
        phoneStatement.setString(1,phoneNumber);
        phoneStatement.setInt(2,customerID);
        phoneStatement.executeUpdate();

        //find new division id
        String divisionQuery = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";
        PreparedStatement divisionStatement = connection.prepareStatement(divisionQuery);
        divisionStatement.setString(1,division);
        ResultSet resultSet = divisionStatement.executeQuery();

        while (resultSet.next()) {
            division_id = resultSet.getInt(1);
        }

        //Once you find the new division ID, then update it
        String updateDivisionIDQuery = "UPDATE customers SET Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement updateDivisionIDStatement = connection.prepareStatement(updateDivisionIDQuery);
        updateDivisionIDStatement.setInt(1,division_id);
        updateDivisionIDStatement.setInt(2,customerID);
        updateDivisionIDStatement.executeUpdate();

        successfulUpdate = true;
        JDBC.closeConnection();
    }

    /**
     * This method sends the selected customer data from the table onto the Update Customer form and populates the textfields and combo boxes with the customers data
     * @param customer the selected customer
     * @throws SQLException
     */
    public void sendCustomerData(Customer customer) throws SQLException {
        //set the text fields
        customerIDTextField.setText(String.valueOf(customer.getCustomer_ID()));
        nameTextField.setText(String.valueOf(customer.getCustomer_Name()));
        addressTextField.setText(String.valueOf(customer.getAddress()));
        phoneTextField.setText(String.valueOf(customer.getPhone()));
        postalCodeTextField.setText(String.valueOf(customer.getPostal_Code()));

        //set the country box
        countryBox.setItems(countryList);

        //find the division id of the customer
        division_id = customer.getDivision_ID();

        JDBC.openConnection();

        //use the division id to find the country
        String sql1 = "SELECT Country_ID from first_level_divisions WHERE Division_ID = ?";
        PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
        preparedStatement1.setInt(1,division_id);
        ResultSet resultSet1 = preparedStatement1.executeQuery();

        while (resultSet1.next()) {
            country_id = resultSet1.getInt(1);
            country = countryList.get(country_id-1);
        }
        countryBox.setValue(String.valueOf(country));

        //use the division id to find the division
        String sql2 = "SELECT Division from first_level_divisions WHERE Division_ID = ?";
        PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
        preparedStatement2.setInt(1,division_id);
        ResultSet resultSet2 = preparedStatement2.executeQuery();

        while (resultSet2.next()) {
            division = resultSet2.getString(1);
        }
        divisionBox.setValue(String.valueOf(division));

        //Run a query to update the Division Drop down box
        //run the query to find the divisions by country id
        String sql3 = "SELECT Division FROM first_level_divisions WHERE Country_ID = ?";
        PreparedStatement preparedStatement3 = connection.prepareStatement(sql3);
        preparedStatement3.setInt(1,country_id);
        ResultSet resultSet3 = preparedStatement3.executeQuery();

        //create observableList to store division list
        ObservableList<String> divisionList = FXCollections.observableArrayList();

        //add divisions into the list
        while (resultSet3.next()) {
            String division = resultSet3.getString(1);
            divisionList.add(division);
        }

        //then add the list to the combo box
        divisionBox.setItems(divisionList);

        JDBC.closeConnection();
    }

    /**
     * This method updated the available divisions after a user has selected a country
     * @param actionEvent this method is called when a user selects a country
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

    /**
     * This method finds the division id after a user has selected a division from the division combo box
     * @param actionEvent this method is called when the user selects a division
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

    /**
     * This method cancels the updating of the customer
     * @param actionEvent this method is called when the user selects the cancel button
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

    /**
     * This method saves the customer. If a save is successful it sends the user back to the main customer view
     * @param actionEvent this method is called when a user clicks the save button
     * @throws IOException
     * @throws SQLException
     */
    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        updateCustomer();

        if (successfulUpdate == false) {
            return;
        }

        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }
}
