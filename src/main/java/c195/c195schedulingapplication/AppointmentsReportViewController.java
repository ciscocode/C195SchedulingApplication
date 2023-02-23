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
import java.util.ResourceBundle;
import static helper.JDBC.connection;


import static java.sql.DriverManager.getConnection;

/**This class generates a report of appointments. It can count appointments by Type or Month.*/
public class AppointmentsReportViewController implements Initializable {
    public RadioButton typeRadioButton;
    public RadioButton monthRadioButton;
    public ComboBox selectionBox;
    public Label selectionLabel;
    public TableView appointmentTable;
    public TableColumn apptIDCol;
    public TableColumn descriptionCol;
    public TableColumn locationCol;
    public TableColumn contactCol;
    public TableColumn typeCol;
    public TableColumn startTimeCol;
    public TableColumn endTimeCol;
    public TableColumn customerIDCol;
    public TableColumn userIDCol;
    public TableColumn titleCol;
    public Label countLabel;
    public RadioButton viewAllButton;
    int count = 0;
    int totalCount = 0;

    ObservableList<String> monthOptions = FXCollections.observableArrayList();
    ObservableList<String> typeOptions = FXCollections.observableArrayList();
    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    /**
     This method returns the user to the reports menu
     @param actionEvent the method is called when the user clicks the return button
     @throws IOException
     */
    public void onReturnToReportMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("reports-menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 360, 469);
        stage.setTitle("Reports Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method loads the selection box with a list of available types
     * @param actionEvent this method is called when the user selects the Type radio button
     * @throws SQLException
     */
    public void onFilterByType(ActionEvent actionEvent) throws SQLException {
        selectionLabel.setText("Select Type");
        selectionBox.setItems(typeOptions);
    }

    /**
     * This method loads the selection box with a list of available months
     * @param actionEvent this method is called when the user selects the Month radio button
     * @throws SQLException
     */
    public void onFilterByMonth(ActionEvent actionEvent) throws SQLException {
        selectionLabel.setText("Select Month");
        selectionBox.setItems(monthOptions);
    }

    /**
     * THis method loads an observable array list of appointments sorted by type
     * @throws SQLException
     */
    public void loadTypeList() throws SQLException {
        JDBC.openConnection();
        String sql = "SELECT DISTINCT Type FROM appointments";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            typeOptions.add(resultSet.getString("Type"));
        }
        JDBC.closeConnection();
    }

    /**
     * This method loads an observable array list of appointments by month
     * @throws SQLException
     */
    public void loadMonthList() throws SQLException {
        JDBC.openConnection();
        String sql = "SELECT DISTINCT MONTHNAME(Start) FROM appointments;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            monthOptions.add(resultSet.getString("MONTHNAME(Start)"));
        }
        JDBC.closeConnection();
    }

    /**
     * This initialize method starts by loading the avaialble lists of month and type
     * It also creates a table view of all available appointments and counts them
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadMonthList();
            loadTypeList();

            count = 0;

            JDBC.openConnection();

            //execute query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM appointments");

            //Iterate through the result set and add each row to the ObservableList
            while (resultSet.next()) {
                Appointment appointment = new Appointment(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getTimestamp("Create_Date").toLocalDateTime(),
                        resultSet.getString("Created_By"),
                        resultSet.getTimestamp("Last_Update").toLocalDateTime(),
                        resultSet.getString("Last_Updated_By"),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID")
                );
                appointmentList.add(appointment);
                count++;
            }
            //save this as total count so that you can refrence the value again in the View All action event below
            totalCount = count;
            countLabel.setText(String.valueOf(count));

            //set items into the table view
            appointmentTable.setItems(appointmentList);

            //set cell values
            apptIDCol.setCellValueFactory(new PropertyValueFactory<>("Appointment_ID"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
            contactCol.setCellValueFactory(new PropertyValueFactory<>("ContactName"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
            startTimeCol.setCellValueFactory(new PropertyValueFactory<>("LocalStartTimeIn12HourFormat"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<>("LocalEndTimeIn12HourFormat"));
            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
            userIDCol.setCellValueFactory(new PropertyValueFactory<>("User_ID"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        JDBC.closeConnection();
    }


    /**
     * This method displays all appointments of a selected Type or Month, and displayes their respective total count
     * @param actionEvent this method is called when a user makes a selection from the combo box
     * @throws SQLException
     */
    public void onSelectedOption(ActionEvent actionEvent) throws SQLException {
        //reset count to 0 everytime you load a new list
        count = 0;
        JDBC.openConnection();

        if (monthRadioButton.isSelected()) {
            String selectedOption = (String) selectionBox.getSelectionModel().getSelectedItem();
            ObservableList<Appointment> filteredByMonth = FXCollections.observableArrayList();

            //run a query to gather the filtered results
            String monthQuery = "SELECT * FROM appointments WHERE MONTHNAME(Start) = ?";
            PreparedStatement monthStatement = connection.prepareStatement(monthQuery);
            monthStatement.setString(1,selectedOption);

            //load results into the observableList
            ResultSet monthResultSet = monthStatement.executeQuery();
            while (monthResultSet.next()) {
                Appointment appointment = new Appointment(
                        monthResultSet.getInt("Appointment_ID"),
                        monthResultSet.getString("Title"),
                        monthResultSet.getString("Description"),
                        monthResultSet.getString("Location"),
                        monthResultSet.getString("Type"),
                        monthResultSet.getTimestamp("Start").toLocalDateTime(),
                        monthResultSet.getTimestamp("End").toLocalDateTime(),
                        monthResultSet.getTimestamp("Create_Date").toLocalDateTime(),
                        monthResultSet.getString("Created_By"),
                        monthResultSet.getTimestamp("Last_Update").toLocalDateTime(),
                        monthResultSet.getString("Last_Updated_By"),
                        monthResultSet.getInt("Customer_ID"),
                        monthResultSet.getInt("User_ID"),
                        monthResultSet.getInt("Contact_ID")
                );
                filteredByMonth.add(appointment);
                count++;
            }
            //fill in the table
            appointmentTable.setItems(filteredByMonth);
            countLabel.setText(String.valueOf(count));
        }

        if (typeRadioButton.isSelected()) {
            String selectedOption = (String) selectionBox.getSelectionModel().getSelectedItem();
            ObservableList<Appointment> filteredByType = FXCollections.observableArrayList();

            //run a query to gather the filtered results
            String typeQuery = "SELECT * FROM appointments WHERE TYPE = ?";
            PreparedStatement typeStatement = connection.prepareStatement(typeQuery);
            typeStatement.setString(1,selectedOption);

            //load results into the observableList
            ResultSet monthResultSet = typeStatement.executeQuery();
            while (monthResultSet.next()) {
                Appointment appointment = new Appointment(
                        monthResultSet.getInt("Appointment_ID"),
                        monthResultSet.getString("Title"),
                        monthResultSet.getString("Description"),
                        monthResultSet.getString("Location"),
                        monthResultSet.getString("Type"),
                        monthResultSet.getTimestamp("Start").toLocalDateTime(),
                        monthResultSet.getTimestamp("End").toLocalDateTime(),
                        monthResultSet.getTimestamp("Create_Date").toLocalDateTime(),
                        monthResultSet.getString("Created_By"),
                        monthResultSet.getTimestamp("Last_Update").toLocalDateTime(),
                        monthResultSet.getString("Last_Updated_By"),
                        monthResultSet.getInt("Customer_ID"),
                        monthResultSet.getInt("User_ID"),
                        monthResultSet.getInt("Contact_ID")
                );
                filteredByType.add(appointment);
                count++;
            }
            //fill in the table
            appointmentTable.setItems(filteredByType);
            countLabel.setText(String.valueOf(count));
        }
        JDBC.closeConnection();
    }

    /**
     * This method loads the table view with all appointments
     * @param actionEvent this method is called when the user clicks the view all radio button
     */
    public void onViewAll(ActionEvent actionEvent) {
        appointmentTable.setItems(appointmentList);
        countLabel.setText(String.valueOf(totalCount));
    }
}
