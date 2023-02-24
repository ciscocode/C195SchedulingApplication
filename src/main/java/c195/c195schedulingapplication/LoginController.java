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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TimeZone;
import javafx.scene.control.Button;

import static helper.JDBC.connection;

/**This class includes the functionality necessary for a user to log into the application.*/
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
    public static boolean initialized = false;

    /**
     * This method is intialized and sets the users locatio, and language of the login screen based on the users Locale
     * @param url
     * @param resourceBundle
     */
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

    /**
     * This method validates the user log in by checking for empty textfields or invalid username/passwords
     * This method also keeps a log of successful/unsuccessful log in attempts
     * @throws SQLException
     * @throws IOException
     */
    public void validateLogin() throws SQLException, IOException {
        //get the attempted password/username from the text field
        password = passwordTextField.getText();
        username = usernameTextField.getText();

        FileWriter fileWriter = new FileWriter("login_activity.txt", true);
        PrintWriter loginFile = new PrintWriter(fileWriter);

        boolean validUsername = true;

        /**This is a lambda expression to consolidate my error alert when a user leaves the Password or Username text fields blank**/
        Runnable showAlert = () -> {
            Alert errorMessage = new Alert(Alert.AlertType.WARNING);
            errorMessage.setTitle(warning);
            if (username.isBlank()) {
                errorMessage.setContentText(usernameWarning);
            } else if (password.isBlank()) {
                errorMessage.setContentText(passwordWarning);
            }
            errorMessage.showAndWait();
            return;
        };

        if (username.isBlank() || password.isBlank()) {
            showAlert.run();
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
            loginFile.print(" Timestamp: " + Timestamp.valueOf(LocalDateTime.now()) + " : " + "Unsuccessful login. The user " + username + " does not exist" + "\n");
            loginFile.close();
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
                loginFile.print(" Timestamp: " + Timestamp.valueOf(LocalDateTime.now()) + " : " +  "Unsuccessful login. The user " + username + " entered an invalid password password" + "\n");
                loginFile.close();
                return;
            }
        }
        successfulLogin = true;
        loginFile.print(" Timestamp: " + Timestamp.valueOf(LocalDateTime.now()) + " : " + " Successful login by " + username  + "\n");
        loginFile.close();
        JDBC.closeConnection();
    }

    /**
     * This method calls the validate login method. If a login is successful the user is sent to the main menu of the application
     * @param actionEvent this method is called when the user clicks the Login button
     * @throws IOException
     * @throws SQLException
     */
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