package c195.c195schedulingapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateCustomerViewController {
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }

    public void onSave(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customer-database-view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene((Parent) root, 1000, 700);
        stage.setTitle("Customers");
        stage.setScene(scene);
        stage.show();
    }
}
