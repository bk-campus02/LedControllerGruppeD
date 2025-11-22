package at.edu.c02.ledcontroller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This class should handle all HTTP communication with the server.
 * Each method here should correspond to an API call, accept the correct parameters and return the response.
 * Do not implement any other logic here - the ApiService will be mocked to unit test the logic without needing a server.
 */
public class ApiServiceImpl implements ApiService {
    /**
     * This method calls the `GET /getLights` endpoint and returns the response.
     * TODO: When adding additional API calls, refactor this method. Extract/Create at least one private method that
     * handles the API call + JSON conversion (so that you do not have duplicate code across multiple API calls)
     *
     * @return `getLights` response JSON object
     * @throws IOException Throws if the request could not be completed successfully
     */

    private final String groupId;

    public ApiServiceImpl() {
        this.groupId = loadSecretFromFile("secret.txt");
    }

    private String loadSecretFromFile(String path) {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path))).trim();
        } catch (IOException e) {
            throw new RuntimeException("Could not load X-Hasura-Group-ID from file: " + path, e);
        }
    }

    private JSONObject doRequest(String urlString) throws IOException {
        // Connect to the server
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // and send a GET request
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Hasura-Group-ID", groupId);

        int responseCode = connection.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error: request to " + urlString + " failed with response code " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        int character;

        while((character = reader.read()) != -1) {
            sb.append((char) character);
        }

        return new JSONObject(sb.toString());
    }

    private JSONObject doRequestWithBody(String urlString, String method, JSONObject body) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setRequestProperty("X-Hasura-Group-ID", groupId);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error: request to " + urlString + " failed with response code " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        int character;
        while ((character = reader.read()) != -1) {
            sb.append((char) character);
        }

        return new JSONObject(sb.toString());
    }

    @Override
    public JSONObject getLights() throws IOException {
        return doRequest("https://balanced-civet-91.hasura.app/api/rest/getLights");
    }

    @Override
    public JSONObject getLight(int id) throws IOException {
        return doRequest("https://balanced-civet-91.hasura.app/api/rest/lights/" + id);
    }

    public JSONObject setLight(int id, String color, boolean state) throws IOException {
        JSONObject body = new JSONObject();
        body.put("id", id);
        body.put("color", color);
        body.put("state", state);

        return doRequestWithBody(
                "https://balanced-civet-91.hasura.app/api/rest/setLight",
                "PUT",
                body
        );
    }

}
