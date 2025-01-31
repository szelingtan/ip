package tringaa;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.format.DateTimeParseException;

class DeadlineTest {
    private Deadline deadline;

    @Test
    void getDeadline_ValidDate_ReturnsFormattedDate() {
        // Arrange
        deadline = new Deadline("Submit assignment", "2024-01-31");
        String expected = "Jan 31 2024";

        // Act
        String result = deadline.getDeadline();

        // Assert
        assertEquals(expected, result, "Deadline should be formatted as 'MMM dd yyyy'");
    }

    @Test
    void constructor_InvalidDateFormat_ThrowsException() {
        // Arrange & Act & Assert
        assertThrows(DateTimeParseException.class,
                () -> new Deadline("Submit assignment", "31-01-2024"),
                "Constructor should throw DateTimeParseException for invalid date format");
    }

    @Test
    void constructor_NullDate_ThrowsException() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class,
                () -> new Deadline("Submit assignment", null),
                "Constructor should throw NullPointerException for null date");
    }

    @Test
    void getDeadline_FutureDates_ReturnsFormattedDate() {
        // Arrange
        deadline = new Deadline("Future task", "2025-12-25");
        String expected = "Dec 25 2025";

        // Act
        String result = deadline.getDeadline();

        // Assert
        assertEquals(expected, result, "Future dates should be formatted correctly");
    }
}