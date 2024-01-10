package org.openjfx.rexpotify.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.openjfx.rexpotify.models.Slist;

import java.io.File;

public class ListController {

    @FXML
    private HBox box;

    @FXML
    private ImageView img;

    @FXML
    private Label name;

    private Slist data;
    private SongClickListener songClickListener;

    public interface SongClickListener {
        void onSongClicked(String songName);
    }

    public void setSongClickListener(SongClickListener listener) {
        this.songClickListener = listener;
    }

    public void setData(Slist slist) {
        try {
            File file = new File(slist.getImageSrc());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                img.setImage(image);
            } else {
                System.err.println("Image not found: " + slist.getImageSrc());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        name.setText(slist.getName());

        // Set up event handler for the HBox (or any other clickable element)
        box.setOnMouseClicked(event -> handleItemClick(slist));
    }

    private void handleItemClick(Slist slist) {
        if (slist != null && songClickListener != null) {
            songClickListener.onSongClicked(slist.getName());
        }
    }

    @FXML
    public void handleItemClick() {
        System.out.println("Item Clicked!");
    }
}
