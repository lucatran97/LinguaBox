package com.example.linguabox;

import android.util.Log;

import org.json.JSONArray;
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


    /**
     * This public function uses the private POST and returns the response object in a String.
     * @param url The url of our server endpoint
     * @param json The data we want to post
     * @return A response string
     * @throws IOException object is pointed to null
     * @throws JSONException error in JSON response object
     */
    public static String publicPostGetString(String url, String json) throws IOException, JSONException {
        JSONObject response =  post(url, json);
        Log.e("RESPONSE", response.toString());
        String translation = response.getString("translation");
        return  translation;
    }

    /**
     * This private func parse the message in a String. String will contain message, email, and language
     * @param message
     * @param email
     * @param language
     * @return A String
     */
    private static String parseMessage(String message, String email, String language){
        return "{\"message\": \""+message+"\", \"email\": \""+email+"\", \"language\": \""+language+"\"}";
    }


    /**
     * This function will send the message String from parseMessage to the server and return the responses.
     * @param email User's email
     * @param message
     * @param language
     * @return A Response Message from server
     */
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

    /**
     * This function sends users' info to our server to sign them in to the app. Return the status of the signIn in String
     * @param email User's email
     * @return A String indicating the singIn's status
     * @throws IOException
     * @throws JSONException
     */
    public static String signIn(String email) throws IOException, JSONException {
        try {
            JSONObject mainObject = post("https://linguabox.azurewebsites.net/users", "{\"email\": \""+email+"\"}");
            JSONArray mainArray = mainObject.getJSONArray("progress");
            UserAccount.setProgress(mainArray);
            return mainObject.getString("status");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
