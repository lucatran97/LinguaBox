package com.example.linguabox;

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
    private OkHttpClient client = new OkHttpClient();

    /**
     * This private function provides the basic POST request to a URL. The data to be posted is a JSON string
     * @param url The url of our server endpoint
     * @param json The data we want to post
     * @return A response string
     * @throws IOException
     */
    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
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

    public String sendMessage(String sessionID, String message){
        //return post(...,...);
        return null;
    }

}
