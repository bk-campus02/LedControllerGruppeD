package at.edu.c02.ledcontroller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the actual logic
 */
public class LedControllerImpl implements LedController {

    // TODO: Diese IDs ggf. an die Angabe anpassen!
    // Gruppen-LED-IDs zentral definieren (wird später auch für andere Stories praktisch)

    public static final int[] GROUP_LED_IDS = {
            2, 10, 11, 12, 13, 14, 15, 16
    };

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
        JSONObject response = apiService.getLights();
        JSONArray lights = response.getJSONArray("lights");

        // Gruppenname D wird hardcoded - wie besprochen
        List<JSONObject> groupD = new ArrayList<>();

        for (int i = 0; i < lights.length(); i++) {
            JSONObject light = lights.getJSONObject(i);

            JSONObject group = light.optJSONObject("groupByGroup");
            if (group != null) {
                String name = group.optString("name", "");
                if ("D".equals(name)) {
                    groupD.add(light);
                }
            }
        }

        return groupD.toArray(new JSONObject[0]);
    }

    @Override
    public void turnOffAllLeds() throws IOException {

        int[] GROUP_LED_IDS = {
                2, 10, 11, 12, 13, 14, 15, 16
        };

        for (int id : GROUP_LED_IDS) {
            apiService.setLight(id, "#000000", false);
        }
    }

}
