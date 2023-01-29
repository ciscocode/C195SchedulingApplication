package c195.c195schedulingapplication;

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

import static java.sql.DriverManager.getConnection;

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

    ObservableList<String> monthOptions = FXCollections.observableArrayList();
    ObservableList<String> typeOptions = FXCollections.observableArrayList();

    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public void onReturnToReportMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("reports-menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 360, 469);
        stage.setTitle("Reports Menu");
        stage.setScene(scene);
        stage.show();
    }

    public void onFilterByType(ActionEvent actionEvent) throws SQLException {
        selectionLabel.setText("Select Type");
        selectionBox.setItems(typeOptions);
    }

    public void onFilterByMonth(ActionEvent actionEvent) throws SQLException {
        selectionLabel.setText("Select Month");
        selectionBox.setItems(monthOptions);
    }

    public void loadTypeList() throws SQLException {
        Connection connection = getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        String sql = "SELECT Type FROM appointments";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            typeOptions.add(resultSet.getString("Type"));
        }
        connection.close();
    }

    public void loadMonthList() throws SQLException {
        Connection connection = getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

        String sql = "SELECT DISTINCT MONTHNAME(Start) FROM appointments;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            monthOptions.add(resultSet.getString("MONTHNAME(Start)"));
        }
        connection.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadMonthList();
            loadTypeList();

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

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
            }

            //set items into the table view
            appointmentTable.setItems(appointmentList);

            //set cell values
            apptIDCol.setCellValueFactory(new PropertyValueFactory<>("Appointment_ID"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
            contactCol.setCellValueFactory(new PropertyValueFactory<>("ContactName"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
            startTimeCol.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
            customerIDCol.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
            userIDCol.setCellValueFactory(new PropertyValueFactory<>("User_ID"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void onSelectedOption(ActionEvent actionEvent) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/client_schedule", "cisco", "Bunnysql23$");

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
            }
            //fill in the table
            appointmentTable.setItems(filteredByMonth);
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
            }
            //fill in the table
            appointmentTable.setItems(filteredByType);
        }
        connection.close();
    }

    public void onViewAll(ActionEvent actionEvent) {
        appointmentTable.setItems(appointmentList);
    }
}
