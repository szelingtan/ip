import java.io.*;
import java.util.*;

public class Storage {
    private static final String FILE_PATH = "./src/data/tringa.txt";

    public static void writeToFile(List<Task> lst)
        throws IOException {
        FileWriter fw = new FileWriter(FILE_PATH);
        for (Task e : lst) {
            fw.write(e.toString());
        }
        fw.close();
    }
}
