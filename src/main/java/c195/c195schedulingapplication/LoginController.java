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
    public Label c195label;
    @FXML
    String username;
    String password;
    Boolean successfulLogin = false;
    ResourceBundle rb;
    String warning;
    String error;
    String usernameWarning;
    String passwordWarning;
    String invalidUsername;
    String invalidPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //set the location label/
        TimeZone timezone = TimeZone.getDefault();
        String zoneId = timezone.getID();
        locationText.setText(zoneId);

        rb = ResourceBundle.getBundle("TextBundle2", Locale.getDefault());

        if (Locale.getDefault().getLanguage().equals("en") || Locale.getDefault().getLanguage().equals("fr")) {
            System.out.println(rb.getString("username"));
        }
        c195label.setText(rb.getString("C195"));
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("login"));
        locationLabel.setText(rb.getString("user"));
        warning = rb.getString("warning");
        error = rb.getString("error");
        usernameWarning = rb.getString("usernameWarning");
        passwordWarning = rb.getString("passwordWarning");
        invalidPassword = rb.getString("passwordError");
        invalidUsername = rb.getString("usernameError");
    }

    public void validateLogin() throws SQLException {
        //get the attempted password/username from the text field
        password = passwordTextField.getText();
        username = usernameTextField.getText();

        boolean validUsername = true;

        //check to see if the text fields are left blank
        if (username.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle(warning);
            errorMessage.setContentText(usernameWarning);
            errorMessage.showAndWait();
            return;
        }
        if (password.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle(warning);
            errorMessage.setContentText(passwordWarning);
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
            errorMessage.setTitle(error);
            errorMessage.setContentText(invalidUsername);
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
                errorMessage.setTitle(error);
                errorMessage.setContentText(invalidPassword);
                errorMessage.showAndWait();
                return;
            }
        }
        successfulLogin = true;
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
}