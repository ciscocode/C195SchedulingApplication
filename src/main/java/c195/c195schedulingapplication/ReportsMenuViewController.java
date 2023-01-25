package c195.c195schedulingapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReportsMenuViewController {
    public void onCustomersByMonth(ActionEvent actionEvent) {
    }

    public void onCustomersByType(ActionEvent actionEvent) {
    }

    public void onContactSchedule(ActionEvent actionEvent) {
    }

    public void onCustomerByCountry(ActionEvent actionEvent) {
    }

    public void onMenu(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 542, 210);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }
}
