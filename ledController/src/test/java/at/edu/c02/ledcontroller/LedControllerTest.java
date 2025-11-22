package at.edu.c02.ledcontroller;

import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for LedController
 */
public class LedControllerTest {

    /**
     * Einfacher Mock für ApiService, der nur Aufrufe protokolliert
     * und keine echten HTTP-Requests macht.
     */
    private static class MockApiService implements ApiService {

        // protokollierte IDs, für die getLight(...) aufgerufen wurde
        final List<Integer> calledGetLightIds = new ArrayList<>();
        final List<Integer> calledSetLightIds = new ArrayList<>();

        @Override
        public JSONObject getLights() {
            // Für diese Story brauchen wir getLights() im Test nicht.
            // Wenn es aufgerufen wird, ist etwas falsch.
            fail("getLights() should not be called in getGroupLeds() test");
            return null; // wird nie erreicht
        }

        @Override
        public JSONObject getLight(int id) {
            calledGetLightIds.add(id);

            // Wir können eine einfache Dummy-JSON-Antwort zurückgeben
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("color", "#000000");
            obj.put("on", true);
            return obj;
        }

        @Override
        public JSONObject setLight(int id, String color, boolean state) {
            calledSetLightIds.add(id);

            // Dummy JSON response
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("color", color);
            obj.put("state", state);
            return obj;
        }

    }

    @Test
    public void testGetGroupLedsCallsCorrectApiMethods() throws IOException {
        // Arrange
        MockApiService mockApiService = new MockApiService();
        LedControllerImpl controller = new LedControllerImpl(mockApiService);

        // Act
        JSONObject[] groupLeds = controller.getGroupLeds();

        // Assert: Anzahl der Aufrufe
        assertEquals(
                "getLight() should be called once for each group LED",
                LedControllerImpl.GROUP_LED_IDS.length,
                mockApiService.calledGetLightIds.size()
        );

        // Assert: IDs stimmen überein
        for (int i = 0; i < LedControllerImpl.GROUP_LED_IDS.length; i++) {
            int expectedId = LedControllerImpl.GROUP_LED_IDS[i];
            int actualId = mockApiService.calledGetLightIds.get(i);
            assertEquals("Unexpected LED id in getLight() call at index " + i, expectedId, actualId);
        }

        // Optional: Prüfen, dass Rückgabearray die gleiche Länge hat
        assertEquals(LedControllerImpl.GROUP_LED_IDS.length, groupLeds.length);
    }

    @Test
    public void testTurnOffAllLeds() throws IOException {
        MockApiService mockApiService = new MockApiService();
        LedControllerImpl controller = new LedControllerImpl(mockApiService);

        controller.turnOffAllLeds();

        assertEquals(
                "setLight() should be called once for each group LED",
                LedControllerImpl.GROUP_LED_IDS.length,
                mockApiService.calledSetLightIds.size()
        );

        for (int i = 0; i < LedControllerImpl.GROUP_LED_IDS.length; i++) {
            int expectedId = LedControllerImpl.GROUP_LED_IDS[i];
            int actualId = mockApiService.calledSetLightIds.get(i);

            assertEquals(
                    "Unexpected LED ID in setLight() at index " + i,
                    expectedId,
                    actualId
            );
        }
    }
    @Test
    public void testSpinningLedOneTurn() throws IOException {
        MockApiService mockApiService = new MockApiService();
        LedControllerImpl controller = new LedControllerImpl(mockApiService);

        int turns = 1;

        controller.spinningLed("#ff0000", turns);

        int ledCount = LedControllerImpl.GROUP_LED_IDS.length;
        int expectedTotalCalls = ledCount
                + 2 * ledCount
                + ledCount;

        assertEquals(
                "setLight() should be called correct number of times",
                expectedTotalCalls,
                mockApiService.calledSetLightIds.size()
        );

        for (int id : LedControllerImpl.GROUP_LED_IDS) {
            int occurrences = Collections.frequency(mockApiService.calledSetLightIds, id);
            assertEquals(
                    "LED " + id + " should be switched 4 times for 1 turn",
                    4,
                    occurrences
            );
        }
    }

    // Dummy-Test kannst du behalten oder löschen
    @Test
    public void dummyTest() {
        assertEquals(1, 1);
    }
}
