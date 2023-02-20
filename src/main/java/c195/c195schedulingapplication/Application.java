package c195.c195schedulingapplication;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import helper.JDBC;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/** This class launches the application.*/
public class Application extends javafx.application.Application {

    /** This method starts the program and loads a resource bundle based on the Locale of the user. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("login-view.fxml"));
        ResourceBundle rb = ResourceBundle.getBundle("TextBundle2", Locale.getDefault());
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    /** This is the main method. This launches our program*/
    public static void main(String[] args) {
        JDBC.openConnection();
        launch();
        JDBC.closeConnection();
    }
}