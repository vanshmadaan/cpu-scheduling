import java.util.List;
import java.util.Comparator;

public class Statistics {

    /**
     * Prints the Gantt chart in a compressed format.
     * The 'gantt_chart' is a simple list like [P1, P1, P1, P2, P2, IDLE, P3]
     * This function compresses it into: | P1 (0-3) | P2 (3-5) | [IDLE] (5-6) | P3 (6-7) |
     */
    private static void printGanttChart(List<String> gantt_chart) {
        System.out.println("\n--- Gantt Chart ---");
        // If the log is empty for some reason, just stop.
        if (gantt_chart.isEmpty()) {
            System.out.println("No chart to display.");
            return;
        }

        // We'll loop through the log, so we need to remember the last process we saw.
        String last_pid = gantt_chart.get(0); // Get the very first entry
        int start_time = 0; // All charts start at time 0

        // loop to find blocks of the same process, starting from the second item (index 1)
        for (int i = 1; i < gantt_chart.size(); i++) {
            String current_pid = gantt_chart.get(i);
            
            // check if the process ID has changed (e.g., from "P1" to "P2")
            if (!current_pid.equals(last_pid)) {

                // if it changed, it means the last process's block has ended.
                // so, we print the block for 'last_pid'
                // the block started at 'start_time' and ended at 'i'
                System.out.printf("| %s (%d-%d) ", last_pid, start_time, i);

                // Now, update our variables for the new block
                last_pid = current_pid;
                start_time = i;
            }

            // If the PID is the same (e.g., "P1" and then "P1" again), we do nothing
            // and just let the loop continue
        }
        
        // After the loop finishes, there's always one last block left to print
        // (the very last process that was running)
        System.out.printf("| %s (%d-%d) |\n", last_pid, start_time, gantt_chart.size());
    }




     /**
     * Main function to print all results.
     * It's called by the Schedulers class when a simulation is finished.
     */
    public static void printResults(List<Process> completed_processes, int total_time, List<String> gantt_chart_log) {
        
        // First, call our helper function to print the Gantt chart
        printGanttChart(gantt_chart_log);

        // Sort the list by PID (P1, P2, P3...) just to make the table look clean
        completed_processes.sort(Comparator.comparing(p -> p.pid));

        System.out.println("\n--- Final Results ---");
        System.out.println("PID\tArrival\tBurst\tPriority\tCompletion\tTurnaround\tWaiting");
        System.out.println("-------------------------------------------------------------------------");

        // We need these to calculate the averages at the end
        double total_wait = 0;
        double total_turnaround = 0;
        int n = completed_processes.size();  // Total number of processes

        // Loop through each process in the "completed" list
        for (Process p : completed_processes) {
            // Print its data in a formatted way. '\t' just adds a tab.
            System.out.printf("%s\t%d\t%d\t%d\t\t%d\t\t%d\t\t%d\n",
                p.pid, p.arrival_time, p.burst_time, p.priority,
                p.completion_time, p.turnaround_time, p.waiting_time);
            
            // Add this process's times to our running totals
            total_wait += p.waiting_time;
            total_turnaround += p.turnaround_time;
        }

        System.out.println("-------------------------------------------------------------------------");
        // '%.2f' formats the number to 2 decimal places
        
        System.out.printf("Average Waiting Time: %.2f\n", total_wait / n);
        System.out.printf("Average Turnaround Time: %.2f\n", total_turnaround / n);
        System.out.println("Total Time (Simulation Ticks): " + total_time);
    }

}