package com.example.linguabox;

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
    private static JSONObject post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            JSONObject mainObject = new JSONObject(response.body().string());
            return mainObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String parseMessage(String message, String email, String language){
        return "{\"message\": \""+message+"\", \"email\": \""+email+"\", \"language\": \""+language+"\"}";
    }

    public static Message sendMessage(String email, String message, String language){
        try {
            JSONObject mainObject = post("https://linguabox.azurewebsites.net/chat", parseMessage(message, email, language));
            return new Message(mainObject.getString("message"), mainObject.getString("translation"), false);
        } catch (IOException e) {
            e.printStackTrace();
            return new Message("Cannot connect to the server. If you just begin the chat, please wait a few seconds for the server to be online.", null, false);
        } catch (JSONException e) {
            e.printStackTrace();
            return new Message("The program received invalid response from the server.", null, false);
        }
    }

    public static String signIn(String email) throws IOException, JSONException {
        try {
            JSONObject mainObject = post("https://linguabox.azurewebsites.net/users", "{\"email\": \""+email+"\"}");
            return mainObject.getString("message");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
