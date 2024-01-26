package org.openjfx.rexpotify.downloaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjfx.rexpotify.Api.ApiHandler;
import org.openjfx.rexpotify.Api.ApiKeys;
import org.openjfx.rexpotify.Api.ApiResponseParser;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class YTSearch {
    ApiHandler apiHandler = new ApiHandler();
    ApiResponseParser responseParser = new ApiResponseParser();
    String apiUrl;

    public YTSearch(String name ){
        apiUrl = "https://www.googleapis.com/youtube/v3/search?key="+ ApiKeys.ytToken +"="+name+"&type=video&part=snippet&category=music";

    }
    public YTSearch(){

    }

    public List<String> videoID() throws Exception {

        String apiResponse = apiHandler.makeApiRequest(apiUrl);
        JsonNode parsedResponse = responseParser.parseJson(apiResponse);

        List<String> videoIDs = new ArrayList<>();

        if (parsedResponse.has("items") && parsedResponse.get("items").isArray()) {
            for (JsonNode item : parsedResponse.get("items")) {
                if (item.has("id") && item.get("id").has("videoId")) {
                    String videoID = item.get("id").get("videoId").asText();
                    videoIDs.add(videoID);
                }
            }
        } else {
            System.out.println("No 'items' array found in the response.");
        }

        return videoIDs;

    }
    public List<String> videoNames() throws Exception {

        String apiResponse = apiHandler.makeApiRequest(apiUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode parsedResponse = responseParser.parseJson(apiResponse);

        List<String> titleNames = new ArrayList<>();

        if (parsedResponse.has("items") && parsedResponse.get("items").isArray()) {

            for (JsonNode item : parsedResponse.get("items")){
                if (item.has("snippet" ) && item.get("snippet").has("title")){
                    String titleName = item.get("snippet").get("title").asText();
                    titleNames.add(titleName);
                }
            }
        }

        return titleNames;



    }
    public List<String> thumbnails() throws Exception{
        String apiResponse = apiHandler.makeApiRequest(apiUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode parsedResponse = responseParser.parseJson(apiResponse);

        List<String> thumbnail = new ArrayList<>();
//        if (parsedResponse.has("items") && parsedResponse.get("items").isArray()) {
//
//            for (JsonNode item: parsedResponse.get("items").get("snippet").get("thumbnails")){
//                if (item.has("thumbnails") && item.get("thumbnails").has("url")){
//                    String tName = item.get("thumbnails").get("url").asText();
//                    thumbnail.add(tName);
//                }
//
//
//            }
//        }
        if (parsedResponse.has("items") && parsedResponse.get("items").isArray()) {
            for (JsonNode item : parsedResponse.get("items")) {
                if (item.has("snippet") && item.get("snippet").has("thumbnails")) {
                    JsonNode thumbnailsNode = item.get("snippet").get("thumbnails");
                    if (thumbnailsNode.has("high") && thumbnailsNode.get("high").has("url")) {
                        String url = thumbnailsNode.get("high").get("url").asText();
                        thumbnail.add(url);
                    }
                }
            }
        }
        return thumbnail;


    }

    public String youtube(String videoID) {
        //D27cE0ubMM4
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://youtube-mp36.p.rapidapi.com/dl?id=" + videoID))
                    .header("X-RapidAPI-Key", "e29d4f4bb9msh85dc38487359681p17609cjsn1d7659c8cf02")
                    .header("X-RapidAPI-Host", "youtube-mp36.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            RapidApiResponse rapidApiResponse = RapidApiResponse.fromJson(response.body());

            return rapidApiResponse.getLink();


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return videoID;
    }
}
