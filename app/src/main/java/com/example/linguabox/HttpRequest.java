package com.example.linguabox;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class provides wrapper functions for requesting service at the server's APIs
 */
public class HttpRequest {
    //Static final variable for defining JSON format.
    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    //Our OkHttpClient object

    /**
     * This private function provides the basic POST request to a URL. The data to be posted is a JSON string
     * @param url The url of our server endpoint
     * @param json The data we want to post
     * @return A response string
     * @throws IOException
     */
    private static String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            JSONObject mainObject = new JSONObject(response.body().string());
            return mainObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
            return "Invalid JSON response.";
        }
    }

    private static String parseMessage(String message){
        return "{\"message\": \""+message+"\"}";
    }

    public static String sendMessage(String sessionID, String message){
        try {
            return post("https://linguabox.azurewebsites.net/chat", parseMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
            return "Cannot send POST request";
        }
    }

    //Might be added later for GET request
    /*private String get(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }*/

}
