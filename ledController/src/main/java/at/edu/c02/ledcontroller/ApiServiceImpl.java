package at.edu.c02.ledcontroller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    @Override
    // Gemeinsame Methode zum Ausführen eines GET-Requests + JSON-Parsing
    private JSONObject doRequest(String urlString) throws IOException {
        // Connect to the server
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // and send a GET request
        connection.setRequestMethod("GET");
        connection.setRequestProperty("X-Hasura-Group-ID", "Todo");

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

    @Override
    public JSONObject getLights() throws IOException {
        return doRequest("https://balanced-civet-91.hasura.app/api/rest/getLights");
    }

    @Override
    public JSONObject getLight(int id) throws IOException {
        return doRequest("https://balanced-civet-91.hasura.app/api/rest/getLight?id=" + id);
    }


}
