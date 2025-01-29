// Storage.java
import java.io.*;
import java.util.*;

public class Storage {
    private static String FILE_PATH = "./src/main/data/tringa.txt" ;

    public static List<String> load() throws Exception {
        List<String> lines = new ArrayList<>();
        try {
            // Create data directory if it doesn't exist
            File directory = new File(FILE_PATH).getParentFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Check if file exists, if not return empty list
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return lines;
            }

            // Read file using BufferedReader
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
            return lines;

        } catch (IOException e) {
            throw new Exception("Error loading from file: " + e.getMessage());
        }
    }

    public static void save(List<Task> tasks) throws IOException {
        try {
            // Create data directory if it doesn't exist
            File directory = new File(FILE_PATH).getParentFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Write to file using BufferedWriter
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
            for (Task task : tasks) {
                writer.write(task.toString());
                writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            throw new IOException("Error saving to file: " + e.getMessage());
        }
    }
}