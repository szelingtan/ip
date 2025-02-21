package tringaa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import tringaa.tasks.Event;

class EventTest {
    private Event event;

    @Test
    void testGetStartWithValidStartTime() {
        // Arrange
        event = new Event("Team Meeting", "2pm", "4pm");
        String expected = "2pm";

        // Act
        String result = event.getStart();

        // Assert
        assertEquals(expected, result, "Should return the exact start time string");
    }

    @Test
    void testGetStartWithEmptyStartTime() {
        // Arrange
        event = new Event("Team Meeting", "", "4pm");

        // Act
        String result = event.getStart();

        // Assert
        assertEquals("", result, "Should return empty string for empty start time");
    }

    @Test
    void testGetStartWithNullStartTime() {
        // Arrange
        event = new Event("Team Meeting", null, "4pm");

        // Act
        String result = event.getStart();

        // Assert
        assertNull(result, "Should return null for null start time");
    }

    @Test
    void testGetStartWithSpaces() {
        // Arrange
        event = new Event("Team Meeting", "2 pm", "4 pm");
        String expected = "2 pm";

        // Act
        String result = event.getStart();

        // Assert
        assertEquals(expected, result, "Should preserve spaces in start time");
    }
}
