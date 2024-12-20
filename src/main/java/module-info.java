module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires me.xdrop.fuzzywuzzy;
    requires com.opencsv;
    requires java.prefs;

    opens com.example to javafx.fxml;
    opens com.example.model to javafx.base;

    exports com.example;
}
