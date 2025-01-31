package tringaa;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    private Event event;

    @Test
    void getStart_ValidStartTime_ReturnsStartTime() {
        // Arrange
        event = new Event("Team Meeting", "2pm", "4pm");
        String expected = "2pm";

        // Act
        String result = event.getStart();

        // Assert
        assertEquals(expected, result, "Should return the exact start time string");
    }

    @Test
    void getStart_EmptyStartTime_ReturnsEmptyString() {
        // Arrange
        event = new Event("Team Meeting", "", "4pm");

        // Act
        String result = event.getStart();

        // Assert
        assertEquals("", result, "Should return empty string for empty start time");
    }

    @Test
    void getStart_NullStartTime_ReturnsNull() {
        // Arrange
        event = new Event("Team Meeting", null, "4pm");

        // Act
        String result = event.getStart();

        // Assert
        assertNull(result, "Should return null for null start time");
    }

    @Test
    void getStart_WithSpaces_ReturnsOriginalString() {
        // Arrange
        event = new Event("Team Meeting", "2 pm", "4 pm");
        String expected = "2 pm";

        // Act
        String result = event.getStart();

        // Assert
        assertEquals(expected, result, "Should preserve spaces in start time");
    }
}