package c195.c195schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TimeZone;

public class LoginController implements Initializable {
    @FXML
    public Label locationText;
    public Button loginButton;
    public Label languageLabel;
    public Label passwordLabel;
    public Label usernameLabel;
    public Label locationLabel;
    ObservableList<String> languageList = FXCollections.observableArrayList("English", "French");
    @FXML
    public ComboBox languageBox;
    //ResourceBundle rb = ResourceBundle.getBundle("login", Locale.getDefault());


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

    public void onLogin(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    public void onLanguageSelection(ActionEvent actionEvent) {
        //gather the selected country_ID from the combo box
        String selectedLanguage = (String) languageBox.getValue();
        //if (selectedLanguage == "France")

    }
}