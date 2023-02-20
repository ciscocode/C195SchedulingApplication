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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import static helper.JDBC.connection;

/**This class creates the customer database table view.*/
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

    /**
     * This method intializes the table view by running a query which gathers all customer data
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //establish connection to database
            JDBC.openConnection();
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

            JDBC.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method deletes a selected customer from the database
     * @param actionEvent this method is called when a user selects a customer and clicks the Delete Customer button
     * @throws SQLException
     */
    public void onDeleteCustomer(ActionEvent actionEvent) throws SQLException {
        JDBC.openConnection();

        //confirm with the user
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this customer?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            //get the Customer ID from the customer the user selects on the table
            Customer selectedRow = customerTable.getSelectionModel().getSelectedItem();
            customer_ID = selectedRow.getCustomer_ID();
            System.out.print(customer_ID);

            //check to see if the customer still has existing appointments
            boolean allAppointmentsDeleted = false;
            String checkQuery = "SELECT * FROM appointments WHERE Customer_ID = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setInt(1,customer_ID);
            ResultSet resultSet = checkStatement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                allAppointmentsDeleted = true;
            }

            if (allAppointmentsDeleted == true) {
                //use this customer ID to run a query to delete the customer from the table
                String sql = "DELETE FROM customers WHERE Customer_ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,customer_ID);
                preparedStatement.executeUpdate();

                Alert successMessage = new Alert(Alert.AlertType.INFORMATION);
                successMessage.setTitle("Deletion Successful");
                successMessage.setContentText("Customer " + customer_ID + " has been successfully deleted");
                successMessage.showAndWait();

                //then update the table view
                data.remove(selectedRow);
                customerTable.setItems(data);
            } else {
                Alert errorMessage = new Alert(Alert.AlertType.ERROR);
                errorMessage.setTitle("Warning");
                errorMessage.setContentText("You can not delete a customer with existing appointments");
                errorMessage.showAndWait();
                return;
            }
        }
        JDBC.closeConnection();
    }

    /**
     * This method returns the user to the main menu
     * @param actionEvent this method is called when the user clicks the Return to Menu button
     * @throws IOException
     */
    public void onReturnToMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method sends the user to the Add Customer view
     * @param actionEvent this method is called when the user clicks the Add Customer button
     * @throws IOException
     */
    public void onAddCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("add-customer-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 500, 500);
        stage.setTitle("Add Customer");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method updates a selected customer from the table view
     * @param actionEvent this method is called when a user selects a customer and clicks the Update Customer button
     * @throws IOException
     * @throws SQLException
     */
    public void onUpdateCustomer(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader updateCustomerLoader = new FXMLLoader();
        updateCustomerLoader.setLocation(getClass().getResource("update-customer-view.fxml"));
        updateCustomerLoader.load();
        UpdateCustomerViewController updateController = updateCustomerLoader.getController();

        if (customerTable.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setTitle("Error");
            errorMessage.setContentText("Must select a customer");
            errorMessage.showAndWait();
            return;
        }

        updateController.sendCustomerData(customerTable.getSelectionModel().getSelectedItem());
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Parent scene = updateCustomerLoader.getRoot();
        stage.setTitle("Update Customer");
        stage.setScene(new Scene(scene));
        stage.show();
    }
}
