module c195.c195schedulingapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens c195.c195schedulingapplication to javafx.fxml;
    exports c195.c195schedulingapplication;
}