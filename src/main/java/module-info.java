module org.openjfx.rexpotify {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.json;

    // Add the following line to require the automatic module of org.json


    opens org.openjfx.rexpotify to javafx.fxml;
    exports org.openjfx.rexpotify;
    exports org.openjfx.rexpotify.Controllers;
    opens org.openjfx.rexpotify.Controllers to javafx.fxml;
    exports org.openjfx.rexpotify.models;
    opens org.openjfx.rexpotify.models to javafx.fxml;
}
