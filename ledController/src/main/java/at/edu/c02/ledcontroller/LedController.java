package at.edu.c02.ledcontroller;

import org.json.JSONObject;

import java.io.IOException;

public interface LedController {
    void demo() throws IOException;

    // neue implementationen
    JSONObject getLights() throws IOException;
    JSONObject getLight(int id) throws IOException;
}
