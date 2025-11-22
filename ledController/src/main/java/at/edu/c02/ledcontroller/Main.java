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
        ApiService apiService = new ApiServiceImpl();
        LedController ledController = new LedControllerImpl(apiService);

        String input = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!input.equalsIgnoreCase("exit")) {
            System.out.println("=== LED Controller ===");
            System.out.println("Enter 'demo' to send a demo request");
            System.out.println("Enter 'lights' to call getLights() and fetch all lights");
            System.out.println("Enter 'light <id>' to call getLight(id) and get a specific LED");
            System.out.println("Enter 'groupstatus' to show the status of all group LEDs of Gruppe D");
            System.out.println("Enter 'status' to show the status of a single LED");
            System.out.println("Enter 'set' to change a LED");
            System.out.println("Enter 'turnoff' to turn all group LED's off");
            System.out.println("Enter 'spinningled' to start the spinning LED effect");
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

            else if (input.equalsIgnoreCase("set")) {
                try {
                    System.out.println("Please specify LED ID:");
                    int id = Integer.parseInt(reader.readLine());

                    System.out.println("Please specify color (e.g. #f00):");
                    String color = reader.readLine().trim();

                    System.out.println("Should the LED be on? (true/false):");
                    boolean state = Boolean.parseBoolean(reader.readLine().trim());

                    JSONObject response = apiService.setLight(id, color, state);
                    System.out.println("Response from setLight:");
                    System.out.println(response.toString(2));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. ID must be a number, state must be true/false.");
                } catch (IOException e) {
                    System.out.println("Error calling setLight: " + e.getMessage());
                }
            }

            else if (input.equalsIgnoreCase("turnoff")) {
                try {
                    ledController.turnOffAllLeds();
                    System.out.println("All group LEDs have been turned off.");
                } catch (IOException e) {
                    System.out.println("Error turning off LEDs: " + e.getMessage());
                }
            }
            else if (input.equalsIgnoreCase("spinningled")) {
                try {
                    System.out.println("Please specify color (e.g. #f00):");
                    String color = reader.readLine().trim();

                    System.out.println("How many turns should the spinning LED do?");
                    String turnsInput = reader.readLine().trim();
                    int turns = Integer.parseInt(turnsInput);

                    System.out.println("Starting spinning LED effect with color " + color +
                            " for " + turns + " turns...");
                    ledController.spinningLed(color, turns);
                    System.out.println("Spinning LED effect finished.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number for turns. Please enter an integer.");
                } catch (IOException e) {
                    System.out.println("Error while running spinning LED effect: " + e.getMessage());
                }
            }




        }
    }
}

