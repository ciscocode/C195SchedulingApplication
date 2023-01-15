package c195.c195schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

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


    /*public void sendData(int partIndex, Part part) {
        index = partIndex;

        idTextField.setText(String.valueOf(part.getId()));
        nameTextField.setText(part.getName());
        invTextField.setText(String.valueOf(part.getStock()));
        priceTextField.setText(String.valueOf(part.getPrice()));
        maxTextField.setText(String.valueOf(part.getMax()));
        minTextField.setText(String.valueOf(part.getMin()));

        if (part instanceof InHouse) {
            modifyInhouseRadioButton.setSelected(true);
            machineIdTextField.setText(String.valueOf(((InHouse) part).getMachineId()));
        } else {
            modifyOutsourcedRadioButton.setSelected(true);
            machineIdTextField.setText(String.valueOf(((Outsourced) part).getCompanyName()));
            modifyPartToggleLabel.setText("Company Name");
        }
    }*/

    /*
    ID
    Name
    Address
    Postal Code
    Phone
    Country
    State/Province
     */
    public void sendCustomerData(Customer customer) throws SQLException {
        customerIDTextField.setText(String.valueOf(customer.getCustomer_ID()));
        nameTextField.setText(String.valueOf(customer.getCustomer_Name()));
        addressTextField.setText(String.valueOf(customer.getAddress()));
        phoneTextField.setText(String.valueOf(customer.getPhone()));
        postalCodeTextField.setText(String.valueOf(customer.getPostal_Code()));

        countryBox.setItems(countryList);

        //find the division id of the customer
        division_id = customer.getDivision_ID();

        //use the division id to find the country
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");
        String sql1 = "SELECT Country_ID from first_level_divisions WHERE Division_ID = ?";
        PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
        preparedStatement1.setInt(1,division_id);
        ResultSet resultSet1 = preparedStatement1.executeQuery();

        while (resultSet1.next()) {
            int country_ID = resultSet1.getInt(1);
            country_ID--;
            country = countryList.get(country_ID);
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

    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

    public void onSave(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }
}
