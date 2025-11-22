package at.edu.c02.ledcontroller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class handles the actual logic
 */
public class LedControllerImpl implements LedController {

    // TODO: Diese IDs ggf. an die Angabe anpassen!
    // Gruppen-LED-IDs zentral definieren (wird später auch für andere Stories praktisch)
    static final int[] GROUP_LED_IDS = { 1, 2, 3, 4 };

    private final ApiService apiService;

    public LedControllerImpl(ApiService apiService)
    {
        this.apiService = apiService;
    }

    @Override
    public void demo() throws IOException
    {
        // Call `getLights`, the response is a json object in the form `{ "lights": [ { ... }, { ... } ] }`
        JSONObject response = apiService.getLights();
        // get the "lights" array from the response
        JSONArray lights = response.getJSONArray("lights");
        // read the first json object of the lights array
        JSONObject firstLight = lights.getJSONObject(0);
        // read int and string properties of the light
        System.out.println("First light id is: " + firstLight.getInt("id"));
        System.out.println("First light color is: " + firstLight.getString("color"));
    }

    @Override
    public JSONObject getLights() throws IOException {
        return apiService.getLights();
    }

    @Override
    public JSONObject getLight(int id) throws IOException {
        return apiService.getLight(id);
    }

    @Override
    public JSONObject[] getGroupLeds() throws IOException {
        JSONObject[] result = new JSONObject[GROUP_LED_IDS.length];

        for (int i = 0; i < GROUP_LED_IDS.length; i++) {
            int ledId = GROUP_LED_IDS[i];
            // für jede Gruppen-LED den Status via ApiService holen
            result[i] = apiService.getLight(ledId);
        }

        return result;
    }
}
