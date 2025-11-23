package at.edu.c02.ledcontroller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the actual logic.
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
    @Override
    public void spinningLed(String color, int turns) throws IOException {
        turnOffAllLeds();

        for (int t = 0; t < turns; t++) {
            for (int i = 0; i < GROUP_LED_IDS.length; i++) {
                int id = GROUP_LED_IDS[i];

                apiService.setLight(id, color, true);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                apiService.setLight(id, "#000000", false);
            }
        }

        turnOffAllLeds();
    }

    @Override
    public void spinningWheel(int steps) throws IOException {
        JSONObject[] leds = getGroupLeds();
        int n = GROUP_LED_IDS.length;

        String[] colors = new String[n];
        boolean[] states = new boolean[n];

        for (int i = 0; i < n; i++) {
            JSONObject led = leds[i];
            colors[i] = led.getString("color");
            states[i] = led.getBoolean("on");
        }

        boolean allOffOrBlack = true;
        for (int i = 0; i < n; i++) {
            if (states[i] || !colors[i].equalsIgnoreCase("#000000")) {
                allOffOrBlack = false;
                break;
            }
        }

        if (allOffOrBlack) {
            String[] pattern = { "#f00", "#0f0", "#ff0" };
            for (int i = 0; i < n; i++) {
                colors[i] = pattern[i % pattern.length];
                states[i] = true;
            }

            for (int i = 0; i < n; i++) {
                apiService.setLight(GROUP_LED_IDS[i], colors[i], states[i]);
            }
        }
        for (int step = 0; step < steps; step++) {

            String lastColor = colors[n - 1];
            boolean lastState = states[n - 1];

            for (int i = n - 1; i > 0; i--) {
                colors[i] = colors[i - 1];
                states[i] = states[i - 1];
            }
            colors[0] = lastColor;
            states[0] = lastState;

            for (int i = 0; i < n; i++) {
                apiService.setLight(GROUP_LED_IDS[i], colors[i], states[i]);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted during spinningWheel", e);
            }
        }
    }


}
