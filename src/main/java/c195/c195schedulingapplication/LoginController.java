package c195.c195schedulingapplication;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TimeZone;

import static helper.JDBC.connection;

public class LoginController implements Initializable {
    @FXML
    public Label locationText;
    public Button loginButton;
    public Label languageLabel;
    public Label passwordLabel;
    public Label usernameLabel;
    public Label locationLabel;
    public TextField passwordTextField;
    public TextField usernameTextField;
    ObservableList<String> languageList = FXCollections.observableArrayList("English", "French");
    @FXML
    public ComboBox languageBox;
    //ResourceBundle rb = ResourceBundle.getBundle("login", Locale.getDefault());
    String username;
    String password;

    Boolean successfulLogin = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set the language combo box
        languageBox.setValue("English");
        languageBox.setItems(languageList);

        //set the location label/
        TimeZone timezone = TimeZone.getDefault();
        String zoneId = timezone.getID();
        locationText.setText(zoneId);

        /*
        rb = ResourceBundle.getBundle("languages/login", Locale.getDefault());
        //set the locale        rb = ResourceBundle.getBundle("languages/login", Locale.getDefault());

        Locale locale = Locale.getDefault();
        Locale.setDefault(locale);

        //language
        Locale france = new Locale("fr", "FR");
        Locale english = new Locale("en", "US");

        Scanner keyboard = new Scanner(System.in);

         */
        //String languageCode = keyboard.nextLine();

        /*if (languageCode.equals("fr")) {
            Locale.setDefault(france);
        } else {
            Locale.setDefault(english);
        }*/

        /*
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("login"));
        languageLabel.setText(rb.getString("language"));

         */

    }

    public void validateLogin() throws SQLException {
        //get the attempted password/username from the text field
        password = passwordTextField.getText();
        username = usernameTextField.getText();

        boolean validUsername = true;

        //check to see if the text fields are left blank
        if (username.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a username");
            errorMessage.showAndWait();
            return;
        }
        if (password.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle("Warning");
            errorMessage.setContentText("You must enter a password");
            errorMessage.showAndWait();
            return;
        }

        JDBC.openConnection();

        //run the query to see if the username is valid
        String sql = "SELECT User_Name, Password FROM users WHERE User_Name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,username);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.isBeforeFirst())  {
            validUsername = false;
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setTitle("Error");
            errorMessage.setContentText("The username entered does not exist.");
            errorMessage.showAndWait();
            return;
        }

        //if the username is valid then check for a matching password
        if (validUsername == true) {
            String sql2 = "SELECT User_Name, Password FROM users WHERE User_Name = ? AND Password = ?";
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement2.setString(1,username);
            preparedStatement2.setString(2,password);
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            if (!resultSet2.isBeforeFirst())  {
                Alert errorMessage = new Alert(Alert.AlertType.ERROR);
                errorMessage.setTitle("Error");
                errorMessage.setContentText("The password is invalid");
                errorMessage.showAndWait();
                return;
            }
        }
        successfulLogin = true;
        //user id
        //username
        //password
        //create date
        //created by
        //last update
        //last updated by

        JDBC.closeConnection();
    }

    public void onLogin(ActionEvent actionEvent) throws IOException, SQLException {
        validateLogin();
        if (successfulLogin == false) {
            return;
        }

        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /*public void fakelogin() {
        FXMLLoader updateApptLoader = new FXMLLoader();
        updateApptLoader.setLocation(getClass().getResource("update-appointment-view.fxml"));
        updateApptLoader.load();
        UpdateAppointmentViewController updateApptController = updateApptLoader.getController();
        if (appointmentTable.getSelectionModel().getSelectedItem() == null) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setTitle("Error");
            errorMessage.setContentText("Must select an appointment");
            errorMessage.showAndWait();
            return;
        }
        updateApptController.sendApptData((Appointment) appointmentTable.getSelectionModel().getSelectedItem());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent scene = updateApptLoader.getRoot();
        stage.setTitle("Update Appointment");
        stage.setScene(new Scene(scene));
        stage.show();
    }*/

    public void onLanguageSelection(ActionEvent actionEvent) {
        //gather the selected country_ID from the combo box
        String selectedLanguage = (String) languageBox.getValue();
        //if (selectedLanguage == "France")

    }
}