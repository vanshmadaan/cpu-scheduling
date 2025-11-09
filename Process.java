
// This class is the "blueprint" for a single process.
// We implement "Comparable" which lets us sort a list of processes.
public class Process implements Comparable<Process> {
    
    // These variables hold the data we read from the text file
    String pid; // the process ID
    int arrival_time; // when the process enters the system
    int burst_time; // the total CPU time needed by the process
    int priority; // the priority of the process (lower number = higher priority) 

    // --- FOR SCHEDULER USE ---
    // These variables are used by the schedulers while the simulation is running

    // This will count down as the process runs. Used for preemptive algorithms.
    int remaining_burst_time;

    // These are all 0 at the start and will be calculated when the process finishes
    int completion_time = 0;  // The time tick when the process finishes
    int waiting_time = 0;    // Total time spent waiting in the ready queue
    int turnaround_time = 0; // Total time from arrival to completion
    
    // This variable is for Round Robin
    // It tracks how long the process has been running in its current "turn"
    int current_quantum = 0;

    // This is the "constructor" - it's used to create a new Process object
    public Process(String pid, int arrival_time, int burst_time, int priority) {

        // "this.pid" refers to the variable for *this specific object*
        // The "pid" on the right is the value passed into the function
        this.pid = pid;
        this.arrival_time = arrival_time;
        this.burst_time = burst_time;
        this.priority = priority;

        // When a process is first created, its remaining time is the same as its total burst time
        this.remaining_burst_time = burst_time;
    }

    // This function is required by the "Comparable" interface
    // It tells Java how to sort a list of processes.
    // We want to sort them by their arrival_time.
    @Override
    public int compareTo(Process other) {
        // This easily compares our arrival time to the "other" process's arrival time
        return Integer.compare(this.arrival_time, other.arrival_time);
    }
}
