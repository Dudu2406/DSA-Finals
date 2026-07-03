module com.example.dsafinals {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.kordamp.ikonli.fontawesome6;
    requires java.prefs;
    requires com.google.gson;

    opens com.example.dsafinals to javafx.fxml;
    exports com.example.dsafinals;
    exports com.example.dsafinals.controllers;
    opens com.example.dsafinals.controllers to javafx.fxml;
    opens com.example.dsafinals.model to com.google.gson;
}