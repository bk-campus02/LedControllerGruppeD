package at.edu.c02.ledcontroller;

import org.json.JSONObject;

import java.io.IOException;

public interface LedController {
    void demo() throws IOException;

    // neue implementationen
    JSONObject getLights() throws IOException;
    JSONObject getLight(int id) throws IOException;

    // Gruppen-Status-Abfrage
    JSONObject[] getGroupLeds() throws IOException;

    // Story 2.2
    void turnOffAllLeds() throws IOException;
    void spinningLed(String color, int turns) throws IOException;
    void spinningWheel(int steps) throws IOException;

}
