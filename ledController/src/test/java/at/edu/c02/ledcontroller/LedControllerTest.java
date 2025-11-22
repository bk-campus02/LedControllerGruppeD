package at.edu.c02.ledcontroller;

import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
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

    // Dummy-Test kannst du behalten oder löschen
    @Test
    public void dummyTest() {
        assertEquals(1, 1);
    }
}
