package c195.c195schedulingapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    ObservableList<String> languageList = FXCollections.observableArrayList("English", "French");

    @FXML
    public ComboBox languageBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        languageBox.setValue("English");
        languageBox.setItems(languageList);
        System.out.println("hi");
    }

}