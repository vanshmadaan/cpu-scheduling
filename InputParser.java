import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputParser {

    /**
     * Reads a formatted text file and returns a list of Process objects.
     * * Expected format in input.txt (comma-separated):
     * PID, ArrivalTime, BurstTime, Priority
     * P1, 0, 5, 2
     * P2, 1, 3, 1
     */
    public static List<Process> loadProcesses(String filename) {

        // this list will hold all the processes we load from the file
        List<Process> processes = new ArrayList<>();

        // use a Scanner to read the file, line by line.
        try (Scanner scanner = new Scanner(new File(filename))) {
            
            // keep reading the file as long as there's another line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine(); // read the current line

                // check for empty lines or lines that are comments (start with #)
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    // Skip empty lines or comments
                    continue;
                }
                
                // split the line into parts using the comma
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    // Print an error and skip this broken line
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }
                
                try {
                    // try to "parse" (or convert) the string parts into numbers
                    String pid = parts[0].trim(); // part 0 is the PID
                    int at = Integer.parseInt(parts[1].trim()); // part 1 is Arrival Time
                    int bt = Integer.parseInt(parts[2].trim()); // part 2 is Burst Time
                    int pri = Integer.parseInt(parts[3].trim()); // part 3 is Priority
                    
                    // create the new Process object and add it to our main list
                    processes.add(new Process(pid, at, bt, pri));
                    
                } catch (NumberFormatException e) {
                    // this "catches" an error if 'parseInt' fails (e.g., if burst time was "abc")
                    System.err.println("Skipping line with invalid number: " + line);
                }
            }
            
        } catch (FileNotFoundException e) {
            // this "catches" the error if the input.txt file wasn't found
            System.err.println("ERROR: Input file not found: " + filename);
            return null; // or throw an exception
        }
        // just a message to confirm how many processes we loaded
        System.out.println("Successfully loaded " + processes.size() + " processes.");

        // Send the finished list back to Main.java
        return processes;
    }
}
