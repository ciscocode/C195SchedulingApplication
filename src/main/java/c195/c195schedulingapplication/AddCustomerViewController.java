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
import java.util.ResourceBundle;

public class AddCustomerViewController implements Initializable {

    public TextField nameTextField;
    public TextField addressTextField;
    public TextField postalCodeTextField;
    public TextField phoneTextField;
    public ComboBox countryBox;
    public ComboBox divisionBox;
    //Id, name, address, postal, phone, country, division
    int id;
    String name;
    String address;
    String postalCode;
    String phoneNumber;
    String country;
    String division;

    int division_id;

    //Since the list of countries is small at Only 3, I created an ObservableArrayList for it to simplify things
    ObservableList<String> countryList = FXCollections.observableArrayList("U.S", "UK","Canada");

    public void insertCustomer() {
        //get the text from the text inputs
        name = nameTextField.getText();
        address = addressTextField.getText();
        postalCode = postalCodeTextField.getText();
        phoneNumber = phoneTextField.getText();
        country = (String) countryBox.getValue(); //might not work
        System.out.println(country);

        //String machineIdString = machineIdTextField.getText();
    }
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set the country combo box items
        countryBox.setItems(countryList);
    }

    public void onCountrySelection(ActionEvent actionEvent) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //get the value of the country & use this to get the division id.
        country = (String) countryBox.getValue();
        division_id = countryList.indexOf(country) + 1;
        System.out.println(division_id);

        //run the query to find the divisions by country id
        String sql = "SELECT Division FROM first_level_divisions WHERE Country_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,division_id);
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

    public void onSave(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();

        //insertCustomer();
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
