module org.openjfx.rexpotify {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.json;


    opens org.openjfx.rexpotify to javafx.fxml;
    opens org.openjfx.rexpotify.models to com.fasterxml.jackson.databind; // Open to Jackson

    exports org.openjfx.rexpotify;
    exports org.openjfx.rexpotify.Controllers;
    opens org.openjfx.rexpotify.Controllers to javafx.fxml;
}
