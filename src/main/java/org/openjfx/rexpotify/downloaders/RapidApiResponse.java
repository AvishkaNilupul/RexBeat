package org.openjfx.rexpotify.downloaders;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RapidApiResponse {

    private String link;
    private String title;
    private long filesize;
    private double progress;
    private double duration;
    private String status;
    private String msg;


    public static RapidApiResponse fromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        RapidApiResponse response = new RapidApiResponse();
        response.setLink(jsonObject.getString("link"));
        response.setTitle(jsonObject.getString("title"));
        response.setFilesize(jsonObject.getLong("filesize"));
        response.setProgress(jsonObject.getDouble("progress"));
        response.setDuration(jsonObject.getDouble("duration"));
        response.setStatus(jsonObject.getString("status"));
        response.setMsg(jsonObject.getString("msg"));
        return response;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static void extractVideoInfo(String youtubeUrl, VideoInfoCallback videoInfoCallback) {
        String videoId = extractVideoId(youtubeUrl);
        youtube(videoId, videoInfoCallback);
    }

    public static String extractVideoId(String youtubeUrl) {
        String videoId = null;

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|\\/videos\\/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|\\/videos\\/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|\\/videos\\/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|\\/videos\\/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|\\/videos\\/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|\\/videos\\/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|\\/videos\\/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|\\/videos\\/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/)\\w+";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeUrl);

        if (matcher.find()) {
            videoId = matcher.group();
        }
        System.out.println(videoId);
        return videoId;
    }

    private static void youtube(String videoID, VideoInfoCallback callback) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://youtube-mp36.p.rapidapi.com/dl?id=" + videoID))
                    .header("X-RapidAPI-Key", "e29d4f4bb9msh85dc38487359681p17609cjsn1d7659c8cf02")
                    .header("X-RapidAPI-Host", "youtube-mp36.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            RapidApiResponse rapidApiResponse = RapidApiResponse.fromJson(response.body());

            callback.onVideoInfoReceived(rapidApiResponse);
           // System.out.println(rapidApiResponse.getTitle());


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // Callback interface
    public interface VideoInfoCallback {
        void onVideoInfoReceived(RapidApiResponse response) throws URISyntaxException;
    }
}
