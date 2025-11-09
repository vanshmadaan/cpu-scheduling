import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        
        // 1. Load processes from the text file first
        List<Process> processes = InputParser.loadProcesses("input.txt");
        
        // If the file wasn't found or was empty, we can't do anything.
        if (processes == null || processes.isEmpty()) {
            System.out.println("No processes to schedule. Exiting.");
            return;
        }

        // Create a single scanner to read all user input
        Scanner scanner = new Scanner(System.in);
        int choice;   // This variable will hold the user's menu choice

        // Use a "do-while" loop to show the menu at least once
        // and keep showing it until the user presses 8 (Exit)
        do {
            System.out.println("\n--- CPU Scheduling Simulator ---");
            System.out.println("1. First-Come, First-Served (FCFS)");
            System.out.println("2. SJF (Non-Preemptive)");
            System.out.println("3. SJF (Preemptive / SRTF)");
            System.out.println("4. Priority (Non-Preemptive)");
            System.out.println("5. Priority (Preemptive)");
            System.out.println("6. Round Robin (RR)");
            System.out.println("7. Multi-Level Queue");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                // Call the FCFS function from our Schedulers class
                    Schedulers.runFCFS(processes);
                    break;
                case 2:
                    Schedulers.runSJF_NP(processes);
                    break;
                case 3:
                    Schedulers.runSRTF(processes);
                    break;
                case 4:
                    Schedulers.runPriority_NP(processes);
                    break;
                case 5:
                    Schedulers.runPriority_P(processes);
                    break;
                case 6:
                    // Call the Round Robin function
                    // We pass the scanner so it can ask for the time quantum
                    Schedulers.runRR(processes, scanner);
                    break;
                case 7:
                    // Call the Multi-Level Queue function
                    // We also pass the scanner for the time quantum
                    Schedulers.runMultiLevelQueue(processes, scanner);
                    break;
                case 8:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            } 
        } while (choice != 8);

        // We're done, so close the scanner to free up resources
        scanner.close();
    }
}
