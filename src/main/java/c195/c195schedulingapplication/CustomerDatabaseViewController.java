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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;


public class CustomerDatabaseViewController implements Initializable {


    public TableView customerTable;
    public TableColumn IDCol;
    public TableColumn nameCol;
    public TableColumn addressCol;
    public TableColumn stateCol;
    public TableColumn postalCol;
    public TableColumn countryCol;
    public TableColumn phoneCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //establish connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

            //execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

            //create observableList to store data
            ObservableList<Customer> data = FXCollections.observableArrayList();

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

    public void onReturnToMainMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }
}
