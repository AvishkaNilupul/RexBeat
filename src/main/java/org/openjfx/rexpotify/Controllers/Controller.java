package org.openjfx.rexpotify.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.openjfx.rexpotify.Api.ApiHandler;
import org.openjfx.rexpotify.Api.ApiResponseParser;
import org.openjfx.rexpotify.AppConfig;
import org.openjfx.rexpotify.downloaders.RapidApiResponse;
import org.openjfx.rexpotify.downloaders.VideoInfoCallback;
import org.openjfx.rexpotify.models.Slist;
import org.openjfx.rexpotify.models.Song;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class Controller implements Initializable, ListController.SongClickListener,RapidApiResponse.VideoInfoCallback {


    @FXML
    public TextField searchtxt,ytLink;
    public Label songName, Artist, SongLabel;
    public Label rsong1, rsong2, rsong3, rsong4,songtitle;
    public HBox searchbox1;
    public Pane librarryPane;
    public Button addToLib;
    public Button playButton;
    public Button exDownload;
    public Pane oDownloads;
    public Slider volumeSlider;

    @FXML
    private ImageView mainArtist, artistp1, artistp2, artistp3, artistp4;
    @FXML
    private Pane searchpane, artistpane,networkPane;
    @FXML
    private Button searchbtn1, searchbtn;
    @FXML
    private VBox listView;
    private String currentSongName;

    private Media media;
    private MediaPlayer mediaPlayer;

    private List<Slist> TestAdd;

    public static String Slink;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String jarDir = null;
        try {
            jarDir = new File(Controller.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String configDir = jarDir + File.separator + "config";
        String configPath = configDir + File.separator + "config.properties";
        File directoryConfig = new File(configDir);
        if (!directoryConfig.exists()) {
            if (directoryConfig.mkdirs()) {
                System.out.println("Directory created: " + configDir);
            } else {
                System.err.println("Failed to create directory: " + configDir);
            }
        }

        // Create the config file if it doesn't exist
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    System.out.println("Config file created: " + configPath);
                } else {
                    System.err.println("Failed to create config file: " + configPath);
                }
            } catch (IOException e) {
                System.err.println("Error creating config file: " + e.getMessage());
            }
        }

        load();
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSlider.getValue()*0.01);

            }
        });
    }


    public void load() {
        listView.getChildren().clear();

        TestAdd = loadSongsFromConfig(); // Set TestAdd to the loaded songs

        try {
            for (int i = 0; i < TestAdd.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/org/openjfx/rexpotify/List.fxml"));
                HBox box = fxmlLoader.load();
                ListController listController = fxmlLoader.getController();
                listController.setData(TestAdd.get(i));
                listController.setSongClickListener(this);
                listView.getChildren().add(box);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private List<Slist> loadSongsFromConfig() {
        List<Slist> songs = new ArrayList<>();
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(AppConfig.CONFIG_FILE_PATH)) {
            prop.load(input);

            for (String key : prop.stringPropertyNames()) {
                if (key.endsWith(".songName") && prop.containsKey(key.replace(".songName", ".songLink"))) {
                    Slist slist = new Slist();
                    slist.setName(prop.getProperty(key));
                    slist.setImageSrc(prop.getProperty(key.replace(".songName", ".songLink")));  // Corrected line
                    songs.add(slist);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public void searchbtn(ActionEvent event) throws Exception {
        String text = searchtxt.getText().replaceAll("\\s+", "");
        if (text.isEmpty()) {
            artistpane.toFront();
        } else {
            if (event.getSource() == searchbtn) {
                searchpane.toFront();
                // api
                ApiHandler apiHandler = new ApiHandler();
                ApiResponseParser responseParser = new ApiResponseParser();
                String apiUrl = "http://157.245.148.29:8080/api/songs/by-song-name/" + text;
                //http://157.245.148.29:8080/api/songs/by-song-name/side
                String apiResponse = apiHandler.makeApiRequest(apiUrl);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode parsedResponse = responseParser.parseJson(apiResponse);

                List<String> songNames = new ArrayList<>();
                List<String> imageLinks = new ArrayList<>();
                List<String> sdLink = new ArrayList<>();

                if (parsedResponse.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) parsedResponse;

                    Song song = null;
                    for (JsonNode songNode : arrayNode) {
                        song = objectMapper.treeToValue(songNode, Song.class);
                        songNames.add(song.getSongName());
                        imageLinks.add(song.getSongLink());
                        sdLink.add(song.getSdLink());
                    }
                    Slink = sdLink.get(0);
                    String artistName = song.getArtist().getArtistName();
                    Artist.setText(artistName);
                    currentSongName = songNames.get(0);

                } else {
                    System.out.println("No results found.");
                }

                songName.setText(songNames.get(0));
                Image image = new Image(imageLinks.get(0));
                mainArtist.setImage(image);

                // need fixing !
                if (songNames.size() > 1) {
                    rsong1.setText(songNames.get(1));
                }
                if (songNames.size() > 2) {
                    rsong2.setText(songNames.get(2));
                }
            }
        }
    }

    public void searchbtn1(ActionEvent event) {
        if (isInternetAvailable()){
            if (event.getSource() == searchbtn1) {
                artistpane.toFront();
                searchbox1.toFront();
            }
        }else {
            networkPane.toFront();
        }
    }
    public boolean isInternetAvailable(){
        //Check if network available or not 
        try{
            URL url = new URL("http://157.245.148.29:8080/api/songs/by-song-name/test");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;

        }catch (Exception e){
            return false;

        }
    }

    public void LIbrary(ActionEvent event) {
        load();
        librarryPane.toFront();
    }


    public void addToLib(ActionEvent event) throws URISyntaxException {
        if (songName != null && !songName.getText().isEmpty()) {
            String songNameToAdd = songName.getText();
            System.out.println(songNameToAdd);
            String imageLinkToAdd = mainArtist.getImage().getUrl();

            String songLink = Slink;

            // Get the directory where the JAR file is located
            String jarDir = new File(Controller.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();

            // Use the obtained directory to construct the relative path
            String destPath = jarDir + File.separator + "songs" + File.separator + songNameToAdd + ".mp3";

            // Extracting directory path from the destination path
            String directoryPath = destPath.substring(0, destPath.lastIndexOf(File.separator));

            // Creating the directory if it doesn't exist
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Directory created: " + directoryPath);
                } else {
                    System.err.println("Failed to create directory: " + directoryPath);
                }
            }

            try {
                downloadSong(songLink, destPath);
                System.out.println("Song downloaded successfully!");
            } catch (IOException e) {
                System.err.println("Error downloading the song: " + e.getMessage());
            }

            // Call the saveConfig method to save the data to the properties file
            AppConfig.saveConfig("song", songNameToAdd, imageLinkToAdd, destPath);
        } else {
            System.out.println("Song name is null or empty. Cannot save.");
        }
    }


    private void downloadSong(String songLink, String destPath) throws IOException {
        URL url = new URL(songLink);
        try (InputStream inputStream = url.openStream();
             FileOutputStream fileOutputStream = new FileOutputStream(destPath)) {

            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, len);
            }
        }
        System.out.println("Download Done!");
    }

    @Override
    public void onSongClicked(String songName) {
        currentSongName = songName; // Set currentSongName when a song is clicked
        if (this.mediaPlayer == null) {

        } else {
            mediaPlayer.stop();
        }

        // Handle the song click event from the list
        String destPath = getDestPathFromConfig(songName);
        if (destPath != null) {
            System.out.println("Destination path for song " + songName + ": " + destPath);
            SongLabel.setText(songName);

            try {
                // Create the Path object from the URI
                Path path = Paths.get(URI.create(destPath));

                media = new Media(path.toUri().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Destination path not found for song " + songName);
        }
    }


    private String getDestPathFromConfig(String songName) {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(AppConfig.CONFIG_FILE_PATH)) {
            prop.load(input);

            for (String key : prop.stringPropertyNames()) {
                if (key.endsWith(".songName") && prop.getProperty(key).equals(songName)) {
                    // Convert relative path to URI using Paths
                    String relativePath = prop.getProperty(key.replace(".songName", ".dest"));
                    File file = new File(relativePath);

                    // Convert File to URI in a platform-independent way
                    URI uri = file.toURI();

                    // Convert URI to string and replace backslashes with forward slashes
                    String uriString = uri.toString().replace("\\", "/");

                    return uriString;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void forward(ActionEvent event) {
        if (TestAdd != null && currentSongName != null) {
            System.out.println("Current Song Name: " + currentSongName);
            int currentIndex = getCurrentSongIndex();
            System.out.println("Current Index: " + currentIndex);

            if (currentIndex != -1 && currentIndex < TestAdd.size() - 1) {
                String nextSongName = TestAdd.get(currentIndex + 1).getName();
                System.out.println("Next Song Name: " + nextSongName);
                playSongByName(nextSongName);
            } else {
                System.out.println("No next song available.");
            }
        } else {
            System.out.println("No songs in the list or current song name is null.");
        }
    }
    public void backwards(ActionEvent event) {
        if (TestAdd != null && currentSongName != null) {
            System.out.println("Current Song Name: " + currentSongName);
            int currentIndex = getCurrentSongIndex();
            System.out.println("Current Index: " + currentIndex);

            if (currentIndex != -1 && currentIndex > 0) {
                String previousSongName = TestAdd.get(currentIndex - 1).getName();
                System.out.println("Previous Song Name: " + previousSongName);
                playSongByName(previousSongName);
            } else {
                System.out.println("No previous song available.");
            }
        } else {
            System.out.println("No songs in the list or current song name is null.");
        }
    }



    private int getCurrentSongIndex() {
        if (TestAdd != null && currentSongName != null) {
            for (int i = 0; i < TestAdd.size(); i++) {
                if (currentSongName.equals(TestAdd.get(i).getName())) {
                    System.out.println("Current Index: " + i);
                    return i;
                }
            }
        }
        return -1;
    }

    private void playSongByName(String songName) {
        // Find the Slist object with the given songName
        Slist nextSong = TestAdd.stream()
                .filter(slist -> slist.getName().equals(songName))
                .findFirst()
                .orElse(null);

        if (nextSong != null) {
            onSongClicked(songName);
        } else {
            System.out.println("Song not found: " + songName);
        }
    }



    public void playButton(ActionEvent event) {
        boolean playing = mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
        if (playing){
            mediaPlayer.pause();
            FontAwesomeIconView iconView = (FontAwesomeIconView) playButton.getGraphic();
            iconView.setGlyphName("PLAY");

        }else {
            mediaPlayer.play();
            FontAwesomeIconView iconView = (FontAwesomeIconView) playButton.getGraphic();
            iconView.setGlyphName("PAUSE");

        }
    }

    public void ytdownload(ActionEvent event) {
        RapidApiResponse.extractVideoInfo(ytLink.getText(), this);
    }

    @Override
    public void onVideoInfoReceived(RapidApiResponse response) throws URISyntaxException {
        songtitle.setText(response.getTitle());
        if (response.getTitle() != null) {
            String songNameToAdd = response.getTitle();
            String imageLinkToAdd = "NotAvailable";

            String jarDir = new File(Controller.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();

            String destPath = jarDir + File.separator + "songs" + File.separator + songNameToAdd + ".mp3";

            String directoryPath = destPath.substring(0, destPath.lastIndexOf(File.separator));

            File directory = new File(directoryPath);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Directory created: " + directoryPath);
                } else {
                    System.err.println("Failed to create directory: " + directoryPath);
                }
            }
            try {
                downloadSong(response.getLink(), destPath);
                System.out.println("Song downloaded successfully!");
            } catch (IOException e) {
                System.err.println("Error downloading the song: " + e.getMessage());
            }
            AppConfig.saveConfig("song", songNameToAdd, imageLinkToAdd, destPath);


        } else {
            System.out.println("Song name is null or empty. Cannot save.");

        }
    }

    public void exDownload(ActionEvent event) {
        oDownloads.toFront();

    }
}
