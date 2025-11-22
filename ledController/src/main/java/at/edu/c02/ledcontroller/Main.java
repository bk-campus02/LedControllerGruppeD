package at.edu.c02.ledcontroller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    /**
     * This is the main program entry point. TODO: add new commands when implementing additional features.
     */
    public static void main(String[] args) throws IOException {
        LedController ledController = new LedControllerImpl(new ApiServiceImpl());

        String input = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!input.equalsIgnoreCase("exit")) {
            System.out.println("=== LED Controller ===");
            System.out.println("Enter 'demo' to send a demo request");
            System.out.println("Enter 'lights' to call getLights() and fetch all lights");
            System.out.println("Enter 'light <id>' to call getLight(id) and get a specific LED");
            System.out.println("Enter 'groupstatus' to show the status of all group LEDs of Gruppe D");
            System.out.println("Enter 'status' to show the status of a single LED");
            System.out.println("Enter 'exit' to exit the program");

            input = reader.readLine();

            if (input.equalsIgnoreCase("demo")) {
                ledController.demo();
            } else if (input.equalsIgnoreCase("lights")) {
                System.out.println(ledController.getLights().toString(2));
            } else if (input.toLowerCase().startsWith("light ")) {
                String[] parts = input.trim().split("\\s+");

                if (parts.length == 2) {
                    try {
                        int id = Integer.parseInt(parts[1]);
                        System.out.println(ledController.getLight(id).toString(2));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid id. Usage: light <number>");
                    }
                } else {
                    System.out.println("Invalid command. Usage: light <number>");
                }
            }

            else if (input.equalsIgnoreCase("groupstatus")) {
                JSONObject[] groupLeds = ledController.getGroupLeds();

                for (JSONObject led : groupLeds) {
                    int id = led.getInt("id");
                    boolean on = led.getBoolean("on");
                    String color = led.getString("color");

                    System.out.println("LED " + id + " is currently "
                            + (on ? "on" : "off") + ". Color: " + color + ".");
                }
            }

            else if (input.equalsIgnoreCase("status")) {
                System.out.println("Please specify LED ID:");
                String idInput = reader.readLine();

                try {
                    int id = Integer.parseInt(idInput);

                    JSONObject wrapper = ledController.getLight(id);
                    JSONObject led = wrapper.getJSONArray("lights").getJSONObject(0);

                    int ledId = led.getInt("id");
                    boolean on = led.getBoolean("on");
                    String color = led.getString("color");

                    System.out.println("LED " + ledId + " is currently "
                            + (on ? "on" : "off") + ". Color: " + color + ".");

                } catch (NumberFormatException e) {
                    System.out.println("Invalid id. Please enter a number.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

        }
    }
}

