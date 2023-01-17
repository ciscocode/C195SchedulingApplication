package c195.c195schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;


public class CustomerDatabaseViewController implements Initializable {


    public TableView<Customer> customerTable;
    public TableColumn IDCol;
    public TableColumn nameCol;
    public TableColumn addressCol;
    public TableColumn stateCol;
    public TableColumn postalCol;
    public TableColumn countryCol;
    public TableColumn phoneCol;
    int customer_ID;
    ObservableList<Customer> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //establish connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

            //execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

            //create observableList to store data

            //Iterate through the result set and add each row to the ObservableList
            while (resultSet.next()) {
//                LocalDateTime createdDate = (LocalDateTime) resultSet.getObject("Create_Date");
//                LocalDateTime lastUpdate = (LocalDateTime) resultSet.getObject("Last_Update");

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
                data.add(customer);
            }

            //set items into the table view
            customerTable.setItems(data);

            //set cell values
            IDCol.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("Customer_Name"));
            addressCol.setCellValueFactory(new PropertyValueFactory<>("Address"));
            stateCol.setCellValueFactory(new PropertyValueFactory<>("Division"));
            postalCol.setCellValueFactory(new PropertyValueFactory<>("Postal_Code"));
            countryCol.setCellValueFactory(new PropertyValueFactory<>("Country"));
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("Phone"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDeleteCustomer(ActionEvent actionEvent) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        //get the Customer ID from the customer the user selects on the table
        Customer selectedRow = customerTable.getSelectionModel().getSelectedItem();
        customer_ID = selectedRow.getCustomer_ID();
        System.out.print(customer_ID);

        //use this customer ID to run a query to delete the customer from the table
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,customer_ID);
        preparedStatement.executeUpdate();

        //then update the table view
        data.remove(selectedRow);
        customerTable.setItems(data);
    }

    public void onReturnToMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    public void onAddCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("add-customer-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 500, 500);
        stage.setTitle("Add Customer");
        stage.setScene(scene);
        stage.show();
    }

    public void onUpdateCustomer(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader updateCustomerLoader = new FXMLLoader();
        updateCustomerLoader.setLocation(getClass().getResource("update-customer-view.fxml"));
        updateCustomerLoader.load();
        UpdateCustomerViewController updateController = updateCustomerLoader.getController();
        /*if (customerTable.getSelectionModel().getSelectedItem() == null) {
            INESERT ERROT
            return;
        }*/
        updateController.sendCustomerData(customerTable.getSelectionModel().getSelectedItem());
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Parent scene = updateCustomerLoader.getRoot();
        stage.setTitle("Update Customer");
        stage.setScene(new Scene(scene));
        stage.show();
    }
}
