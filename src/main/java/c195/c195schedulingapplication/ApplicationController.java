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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class ApplicationController implements Initializable {
    @FXML
    public Label locationText;
    ObservableList<String> languageList = FXCollections.observableArrayList("English", "French");
    @FXML
    public ComboBox languageBox;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set the language combo box
        languageBox.setValue("English");
        languageBox.setItems(languageList);

        //set the location label
        TimeZone timezone = TimeZone.getDefault();
        String zoneId = timezone.getID();
        locationText.setText(zoneId);
    }

    public void onLogin(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }
}