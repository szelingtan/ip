import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class Parser {
    /**
     * Parses a deadline string and returns a formatted string representation.
     * Accepted Date and Time formats: d/MM/yyyy HHmm, yyyy-MM-dd HHmm
     * Accepted Date formats: d/MM/yyyy, yyyy-MM-dd
     * For date only: returns "MMM dd yyyy" format (e.g., "Dec 04 2024")
     * For date and time: returns "MMM dd yyyy, HH:mm" format (e.g., "Dec 04 2024, 18:00")
     *
     * @param deadline The deadline string to parse
     * @return String formatted according to the requirements
     * @throws IllegalArgumentException if the deadline format is invalid
     */
    public static String parseDeadline(String deadline) {
        try {
            // Remove any leading/trailing whitespace
            deadline = deadline.trim();


            // Split into date and time components
            String[] parts = deadline.split("\\s+");


            // Parse the date first
            LocalDate date;
            try {
                // Try parsing as d/MM/yyyy format
                date = LocalDate.parse(parts[0],
                        DateTimeFormatter.ofPattern("d/MM/yyyy"));
            } catch (DateTimeParseException e) {
                // If that fails, try yyyy-MM-dd format
                date = LocalDate.parse(parts[0],
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }


            // Check if we have a time component
            if (parts.length > 1) {
                // Validate time format (e.g., "1800")
                String time = parts[1];
                if (!time.matches("\\d{4}")) {
                    throw new IllegalArgumentException(
                            "Invalid time format. Expected time (e.g., 1800)"
                    );
                }


                // Extract hours and minutes from time
                int hours = Integer.parseInt(time.substring(0, 2));
                int minutes = Integer.parseInt(time.substring(2));


                // Create LocalDateTime from date and time components
                LocalDateTime dateTime = date.atTime(hours, minutes);


                // Format according to requirements for date+time
                return dateTime.format(DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm"));


            } else {
                // Format according to requirements for date-only
                return date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
            }


        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. Expected: d/MM/yyyy or yyyy-MM-dd", e
            );
        }
    }


}


