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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
public class CustomerCountryReportViewController implements Initializable {
    public TableView<Customer> customerTable;
    public TableColumn IDCol;
    public TableColumn nameCol;
    public TableColumn addressCol;
    public TableColumn stateCol;
    public TableColumn postalCol;
    public TableColumn countryCol;
    public TableColumn phoneCol;
    public ComboBox countryBox;
    int customer_ID;
    ObservableList<Customer> customerList = FXCollections.observableArrayList();
    ObservableList<String> countryList = FXCollections.observableArrayList("U.S", "UK","Canada");

    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //load country box
            countryBox.setItems(countryList);

            //establish connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

            //execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

            //Iterate through the result set and add each row to the ObservableList
            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getTimestamp("Create_Date").toLocalDateTime(),
                        resultSet.getString("Created_By"),
                        resultSet.getTimestamp("Last_Update").toLocalDateTime(),
                        resultSet.getString("Last_Updated_By"),
                        resultSet.getInt("Division_ID")
                );
                customerList.add(customer);
            }

            //set items into the table view
            customerTable.setItems(customerList);

            //set cell values
            IDCol.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("Customer_Name"));
            addressCol.setCellValueFactory(new PropertyValueFactory<>("Address"));
            stateCol.setCellValueFactory(new PropertyValueFactory<>("Division"));
            postalCol.setCellValueFactory(new PropertyValueFactory<>("Postal_Code"));
            countryCol.setCellValueFactory(new PropertyValueFactory<>("Country"));
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("Phone"));

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onReturnToMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("reports-menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 360, 469);
        stage.setTitle("Reports Menu");
        stage.setScene(scene);
        stage.show();
    }

    public void onSelection(ActionEvent actionEvent) throws SQLException {

        ObservableList<Customer> filteredList = FXCollections.observableArrayList();

        //connect to database
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //create a temporary table view consisting of a join query which adds the country id to customers table
        //this temporary view will be referenced when a user makes a selection from the country combo box
        String joinQuery = "CREATE TEMPORARY TABLE result_table AS SELECT customers.Customer_ID, customers.Customer_Name, customers.Postal_Code, customers.Phone, customers.Division_ID, customers.Address, customers.Create_Date, customers.Created_By, customers.Last_Update, customers.Last_Updated_By, first_level_divisions.Country_ID\n" +
                "FROM customers\n" +
                "LEFT JOIN first_level_divisions\n" +
                "ON customers.Division_ID = first_level_divisions.Division_ID";
        PreparedStatement joinStatement = connection.prepareStatement(joinQuery);
        joinStatement.executeUpdate();

        //gather the selected country_ID from the combo box
        String country = (String) countryBox.getValue();
        int country_id = countryList.indexOf(country) + 1;

        //Run a query to filter the results using the country ID
        String filterQuery = "SELECT * FROM result_table WHERE Country_ID = ?";
        PreparedStatement filterStatement = connection.prepareStatement(filterQuery);
        filterStatement.setInt(1,country_id);
        ResultSet filterResultSet = filterStatement.executeQuery();

        while (filterResultSet.next()) {
            Customer customer = new Customer(
                    filterResultSet.getInt("Customer_ID"),
                    filterResultSet.getString("Customer_Name"),
                    filterResultSet.getString("Address"),
                    filterResultSet.getString("Postal_Code"),
                    filterResultSet.getString("Phone"),
                    filterResultSet.getTimestamp("Create_Date").toLocalDateTime(),
                    filterResultSet.getString("Created_By"),
                    filterResultSet.getTimestamp("Last_Update").toLocalDateTime(),
                    filterResultSet.getString("Last_Updated_By"),
                    filterResultSet.getInt("Division_ID")
            );
            filteredList.add(customer);
        }
        customerTable.setItems(filteredList);
    }

    public void onViewAll(ActionEvent actionEvent) throws SQLException {
        customerTable.setItems(customerList);
    }
}
