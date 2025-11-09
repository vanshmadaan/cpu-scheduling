import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Schedulers {

    public static void runFCFS(List<Process> processes) {
        System.out.println("\nRunning First-Come, First-Served (FCFS)...");

        // 'current_time' is our main "clock" for the simulation
        int current_time = 0;
        // 'processes_completed' will count how many processes are finished
        int processes_completed = 0;
        //this will hold the process that's on the CPU
        Process currently_running_process = null;
        
        // The "Ready Queue" for FCFS is a standard FIFO Queue
        Queue<Process> ready_queue = new LinkedList<>();

        // This list will store the log for our Gantt Chart
        // e.g., [P1, P1, P1, P2, P2, IDLE, P3]
        List<String> gantt_chart_log = new ArrayList<>();
        
        // We need to make a fresh copy of the processes for this simulation
        // This lets us re-run other algorithms later without the data being changed
        List<Process> processes_to_arrive = new ArrayList<>();
        for (Process p : processes) {

            // Reset the process state just in case it was changed by another algorithm
            p.remaining_burst_time = p.burst_time;
            p.completion_time = 0;
            p.waiting_time = 0;
            p.turnaround_time = 0;
            processes_to_arrive.add(p);
        }

        // Sort the list of incoming processes by their arrival time
        // This way we can easily check when they arrive
        Collections.sort(processes_to_arrive); 
        
        // This list will hold processes after they are completely finished
        List<Process> completed_processes = new ArrayList<>();

        // We keep looping as long as we haven't finished all the processes
        while (processes_completed < processes.size()) {

            // a. Check for new arrivals
            // Check if any processes from our "to arrive" list have an arrival time
            // that is less than or equal to the current time
            while (!processes_to_arrive.isEmpty() && 
                   processes_to_arrive.get(0).arrival_time <= current_time) {
                
                // If a process has arrived, move it from the "to arrive" list
                // and add it to the "ready queue"
                ready_queue.add(processes_to_arrive.remove(0));
            }

            // b. SCHEDULER LOGIC (FCFS)
            // If CPU is free and queue is not empty, get the next 
            
            if (currently_running_process == null && !ready_queue.isEmpty()) {
                // then we take the first process from the front of the queue
                // This is the "First-Come, First-Served" part
                currently_running_process = ready_queue.poll();
            }

            // c. RUN THE PROCESS
            // If there is a process on the CPU...
            if (currently_running_process != null) {

                // ...simulate it running for one "tick" by decreasing its remaining time
                currently_running_process.remaining_burst_time--;

                // Check if the process just finished
                if (currently_running_process.remaining_burst_time == 0) {

                    // --- Process is finished, so we calculate its stats ---
                    
                    // Mark the time it finished (current_time + 1 because it finishes at the end of this tick)
                    int completion = current_time + 1;
                    currently_running_process.completion_time = completion;

                    // Turnaround Time = Completion Time - Arrival Time
                    currently_running_process.turnaround_time = 
                        completion - currently_running_process.arrival_time;

                    // Waiting Time = Turnaround Time - Burst Time
                    currently_running_process.waiting_time = 
                        currently_running_process.turnaround_time - currently_running_process.burst_time;

                    // Add it to our list of completed processes
                    completed_processes.add(currently_running_process);
                    // Free up the CPU
                    currently_running_process = null;
                    // Count one more completed process
                    processes_completed++;
                }
            }

            // --- Record this tick for the Gantt Chart ---
            // At the end of every tick, we log what was on the CPU
            if (currently_running_process != null) {
                // If a process was running, log its PID
                gantt_chart_log.add(currently_running_process.pid);
            } else {
                // If the CPU was free, log it as [IDLE]
                gantt_chart_log.add("[IDLE]");
            }
            
            // Move our main clock forward by one tick
            current_time++;
        }
        
        // DONE - Print results
        // The loop is finished, so all processes are done.
        // We call the 'printResults' function from our Statistics class
        // and pass it all the data we just calculated.
        Statistics.printResults(completed_processes, current_time, gantt_chart_log);
    }





    /**
     * Runs the Non-Preemptive Shortest-Job-First (SJF) scheduling algorithm.
     */
    public static void runSJF_NP(List<Process> processes) {
        System.out.println("\nRunning SJF (Non-Preemptive)...");

        int current_time = 0;
        int processes_completed = 0;
        Process currently_running_process = null;

        List<String> gantt_chart_log = new ArrayList<>();

        // We use a PriorityQueue instead of a normal one.
        // This queue automatically sorts processes by the one with the
        // smallest burst time, so the "shortest job" is always at the front.
        PriorityQueue<Process> ready_queue = new PriorityQueue<>(
            Comparator.comparingInt(p -> p.burst_time)  // This tells it to sort by burst_time
        );
        
        // Make copies of the processes so we can run other algos later
        List<Process> processes_to_arrive = new ArrayList<>();

        for (Process p : processes) {
            // Reset all the values for a clean run
            p.remaining_burst_time = p.burst_time;
            p.completion_time = 0;
            p.waiting_time = 0;
            p.turnaround_time = 0;
            processes_to_arrive.add(p);
        }
        // We still sort the original list by arrival time
        Collections.sort(processes_to_arrive); 
        
        List<Process> completed_processes = new ArrayList<>();

        // Keep looping until all processes are marked as finished
        while (processes_completed < processes.size()) {

            // a. Check for new arrivals
            // Add any process that has arrived to the ready queue
            while (!processes_to_arrive.isEmpty() && 
                   processes_to_arrive.get(0).arrival_time <= current_time) {

                // When we .add() to the PriorityQueue, it automatically
                // sorts it based on the burst time.
                ready_queue.add(processes_to_arrive.remove(0));
            }

            // b. SCHEDULER LOGIC (SJF Non-Preemptive)
            // If the CPU is free and there are processes waiting...
            if (currently_running_process == null && !ready_queue.isEmpty()) {
                
                // ...grab the next process.
                // Because this is a PriorityQueue, .poll() automatically
                // pulls the process with the SHORTEST burst time.
                currently_running_process = ready_queue.poll(); 
            }

            // (Since this is "non-preemptive", we don't interrupt a process that's already running)

            // c. RUN THE PROCESS
            // (Same as FCFS)
            // If a process is on the CPU, let it run for one tick
            if (currently_running_process != null) {
                currently_running_process.remaining_burst_time--;

                // Check if it just finished
                if (currently_running_process.remaining_burst_time == 0) {
                    // It's done, so calculate its stats
                    int completion = current_time + 1;
                    currently_running_process.completion_time = completion;
                    currently_running_process.turnaround_time = 
                        completion - currently_running_process.arrival_time;
                    currently_running_process.waiting_time = 
                        currently_running_process.turnaround_time - currently_running_process.burst_time;

                    // Save the finished process
                    completed_processes.add(currently_running_process);
                    // Free the CPU
                    currently_running_process = null;
                    // Count it
                    processes_completed++;
                }
            }
            
            // Record what ran (or didn't run) in this tick for the Gantt chart
            if (currently_running_process != null) {
                gantt_chart_log.add(currently_running_process.pid);
            } else {
                gantt_chart_log.add("[IDLE]");
            }
            
            // Move the clock forward
            current_time++; 
        }
        
        // DONE - Print results
        // Send our lists to the Statistics class to be printed
        Statistics.printResults(completed_processes, current_time, gantt_chart_log);
    }



    // This function runs the SRTF (Shortest Remaining Time First) simulation
    // This is the PREEMPTIVE version of SJF
    public static void runSRTF(List<Process> processes) {
        System.out.println("\nRunning SRTF (Preemptive SJF)...");

        int current_time = 0;
        int processes_completed = 0;
        Process currently_running_process = null;

        List<String> gantt_chart_log = new ArrayList<>();

        // The PriorityQueue now sorts by REMAINING burst time, not the total.
        // This is so .peek() always shows us the process with the shortest time left.
        PriorityQueue<Process> ready_queue = new PriorityQueue<>(
            Comparator.comparingInt(p -> p.remaining_burst_time)
        );
        
        // Make copies and reset all processes
        List<Process> processes_to_arrive = new ArrayList<>();
        for (Process p : processes) {
            // Reset state for this run
            p.remaining_burst_time = p.burst_time;
            p.completion_time = 0;
            p.waiting_time = 0;
            p.turnaround_time = 0;
            processes_to_arrive.add(p);
        }
        Collections.sort(processes_to_arrive); // Sort by arrival time
        
        // List to hold finished processes
        List<Process> completed_processes = new ArrayList<>();

        // MAIN CLOCK LOOP
        while (processes_completed < processes.size()) {

            // a. Check for new arrivals (Same as FCFS)
            // Add any newly arrived processes to the ready queue
            while (!processes_to_arrive.isEmpty() && 
                   processes_to_arrive.get(0).arrival_time <= current_time) {
                
                // .add() to the PriorityQueue will sort it automatically
                ready_queue.add(processes_to_arrive.remove(0));
            }

            // b. SCHEDULER LOGIC (SRTF)
        
            // --- This is the PREEMPTION logic ---
            // We check this every tick
            // IF a process is running
            // AND the ready queue isn't empty
            // AND the "best" process in the ready queue (.peek())
            // has less time left than the one currently running...
            if (currently_running_process != null && !ready_queue.isEmpty() && 
                ready_queue.peek().remaining_burst_time < currently_running_process.remaining_burst_time) {
                
                // ...then we PREEMPT!
                // We interrupt the running process and put it back in the ready queue
                ready_queue.add(currently_running_process);
                // And we start the new, shorter process
                currently_running_process = ready_queue.poll();
            }
            
            // If the CPU is free and there are processes waiting, start the
            // one with the shortest remaining time.
            if (currently_running_process == null && !ready_queue.isEmpty()) {
                currently_running_process = ready_queue.poll();
            }

            // c. RUN THE PROCESS (Same as FCFS/SJF)
            // If a process is on the CPU, run it for one tick
            if (currently_running_process != null) {
                currently_running_process.remaining_burst_time--;

                if (currently_running_process.remaining_burst_time == 0) {
                    // Process is finished so we calculate its stats
                    int completion = current_time + 1;
                    currently_running_process.completion_time = completion;
                    currently_running_process.turnaround_time = 
                        completion - currently_running_process.arrival_time;
                    currently_running_process.waiting_time = 
                        currently_running_process.turnaround_time - currently_running_process.burst_time;

                    completed_processes.add(currently_running_process);
                    currently_running_process = null;
                    processes_completed++;
                }
            }
            
            // Log for the Gantt chart
            if (currently_running_process != null) {
                gantt_chart_log.add(currently_running_process.pid);
            } else {
                gantt_chart_log.add("[IDLE]");
            }
            
            current_time++; 
        }
        
        // DONE - Print results
        // Send our lists to the Statistics class to be printed
        Statistics.printResults(completed_processes, current_time, gantt_chart_log);
    }




    /**
     * Runs the Non-Preemptive Priority scheduling algorithm.
     * (Assumes lower number = higher priority)
     */
    public static void runPriority_NP(List<Process> processes) {
        System.out.println("\nRunning Priority (Non-Preemptive)...");

        int current_time = 0;
        int processes_completed = 0;
        Process currently_running_process = null;

        List<String> gantt_chart_log = new ArrayList<>();
        
        // The PriorityQueue now sorts by PRIORITY instead of burst time.
        PriorityQueue<Process> ready_queue = new PriorityQueue<>(
            Comparator.comparingInt(p -> p.priority)
        );
        
        // Make copies of the processes so we can run other algos later
        List<Process> processes_to_arrive = new ArrayList<>();
        for (Process p : processes) {
            p.remaining_burst_time = p.burst_time;
            p.completion_time = 0;
            p.waiting_time = 0;
            p.turnaround_time = 0;
            processes_to_arrive.add(p);
        }
        // Sort the incoming list by arrival time, as usual
        Collections.sort(processes_to_arrive);
        
        // This list holds processes after they finish
        List<Process> completed_processes = new ArrayList<>();

        // MAIN CLOCK LOOP (Same logic as SJF-NP)
        // Keep looping until all processes are done
        while (processes_completed < processes.size()) {

            // a. Check for new arrivals (Same as before)
            // Add any process that has arrived to the ready queue
            while (!processes_to_arrive.isEmpty() && 
                   processes_to_arrive.get(0).arrival_time <= current_time) {
                
                // .add() to the PriorityQueue will sort it by priority
                ready_queue.add(processes_to_arrive.remove(0));
            }

            // b. SCHEDULER LOGIC (Non-Preemptive)
            // This is the "Non-Preemptive" part.
            // We ONLY check for a new process if the CPU is free.
            if (currently_running_process == null && !ready_queue.isEmpty()) {
                // The .poll() command automatically grabs the process with the
                // highest priority (lowest number) because it's a PriorityQueue.
                currently_running_process = ready_queue.poll(); 
            }

            // c. RUN THE PROCESS (Same as before)
            // If a process is on the CPU, let it run
            if (currently_running_process != null) {
                currently_running_process.remaining_burst_time--;

                if (currently_running_process.remaining_burst_time == 0) {
                    int completion = current_time + 1;
                    currently_running_process.completion_time = completion;
                    currently_running_process.turnaround_time = 
                        completion - currently_running_process.arrival_time;
                    currently_running_process.waiting_time = 
                        currently_running_process.turnaround_time - currently_running_process.burst_time;

                    completed_processes.add(currently_running_process);
                    currently_running_process = null;
                    processes_completed++;
                }
            }
            
            if (currently_running_process != null) {
                gantt_chart_log.add(currently_running_process.pid);
            } else {
                gantt_chart_log.add("[IDLE]");
            }
            
            current_time++; 
        }
        
        // DONE - Print results
        Statistics.printResults(completed_processes, current_time, gantt_chart_log);
    }



    /**
     * Runs the Preemptive Priority scheduling algorithm.
     * (Assumes lower number = higher priority)
     */
    public static void runPriority_P(List<Process> processes) {
        System.out.println("\nRunning Priority (Preemptive)...");

        int current_time = 0;
        int processes_completed = 0;
        Process currently_running_process = null;

        List<String> gantt_chart_log = new ArrayList<>();
        
        // The "Ready Queue" is a PriorityQueue that sorts by PRIORITY
        // (We assume a lower number means a higher priority)
        PriorityQueue<Process> ready_queue = new PriorityQueue<>(
            Comparator.comparingInt(p -> p.priority)
        );
        
        List<Process> processes_to_arrive = new ArrayList<>();
        for (Process p : processes) {
            p.remaining_burst_time = p.burst_time;
            p.completion_time = 0;
            p.waiting_time = 0;
            p.turnaround_time = 0;
            processes_to_arrive.add(p);
        }
        Collections.sort(processes_to_arrive);
        
        List<Process> completed_processes = new ArrayList<>();

        // MAIN CLOCK LOOP
        while (processes_completed < processes.size()) {

            // a. Check for new arrivals
            while (!processes_to_arrive.isEmpty() && 
                   processes_to_arrive.get(0).arrival_time <= current_time) {
                
                ready_queue.add(processes_to_arrive.remove(0));
            }

            // b. SCHEDULER LOGIC (Preemptive Priority)
            
            // --- This is the PREEMPTION logic ---
            // We check this every tick.
            // IF a process is running
            // AND the ready queue isn't empty
            // AND the "best" process in the ready queue (.peek())
            // has a *higher priority* (lower number) than the one running...
            if (currently_running_process != null && !ready_queue.isEmpty() && 
                ready_queue.peek().priority < currently_running_process.priority) {
                
                // ...then we PREEMPT!
                // We interrupt the running process and put it back in the ready queue
                ready_queue.add(currently_running_process);
                // And we start the new, higher-priority process
                currently_running_process = ready_queue.poll();
            }
            
            // --- This part is for when the CPU is IDLE ---
            // If the CPU is free and there are processes waiting...
            if (currently_running_process == null && !ready_queue.isEmpty()) {
                // ...start the one with the highest priority
                currently_running_process = ready_queue.poll();
            }

            // c. RUN THE PROCESS
            // If a process is on the CPU, run it for one tick
            if (currently_running_process != null) {
                currently_running_process.remaining_burst_time--;

                if (currently_running_process.remaining_burst_time == 0) {
                    int completion = current_time + 1;
                    currently_running_process.completion_time = completion;
                    currently_running_process.turnaround_time = 
                        completion - currently_running_process.arrival_time;
                    currently_running_process.waiting_time = 
                        currently_running_process.turnaround_time - currently_running_process.burst_time;

                    completed_processes.add(currently_running_process);
                    currently_running_process = null;
                    processes_completed++;
                }
            }
            
            if (currently_running_process != null) {
                gantt_chart_log.add(currently_running_process.pid);
            } else {
                gantt_chart_log.add("[IDLE]");
            }
            
            current_time++; 
        }
        
        // DONE - Print results
        Statistics.printResults(completed_processes, current_time, gantt_chart_log);
    }




    // This function runs the Round Robin simulation
    // We pass in the 'scanner' from Main so we can ask for the time quantum
    public static void runRR(List<Process> processes, Scanner scanner) {

    // We need to ask the user how long each "turn" should be
    System.out.print("\nEnter the Time Quantum for Round Robin: ");
    int time_quantum = scanner.nextInt();
        System.out.println("Running Round Robin (RR) with Time Quantum = " + time_quantum + "...");

        int current_time = 0;
        int processes_completed = 0;
        Process currently_running_process = null;

        List<String> gantt_chart_log = new ArrayList<>();
        
        // --- RR: Use a standard FIFO Queue ---
        Queue<Process> ready_queue = new LinkedList<>();
        
        // Make copies of the processes for a clean run
        List<Process> processes_to_arrive = new ArrayList<>();
        for (Process p : processes) {
            // Reset state for this run
            p.remaining_burst_time = p.burst_time;
            p.completion_time = 0;
            p.waiting_time = 0;
            p.turnaround_time = 0;
            // Also reset the 'current_quantum' counter for this process
            p.current_quantum = 0; 
            processes_to_arrive.add(p);
        }
        Collections.sort(processes_to_arrive); // Sort by arrival time
        
        List<Process> completed_processes = new ArrayList<>();

        // MAIN CLOCK LOOP
        while (processes_completed < processes.size()) {

            // a. Check for new arrivals (Identical)
            while (!processes_to_arrive.isEmpty() && 
                   processes_to_arrive.get(0).arrival_time <= current_time) {
                
                ready_queue.add(processes_to_arrive.remove(0));
            }

            // b. SCHEDULER LOGIC (RR)
            // If the CPU is free and there are processes waiting...
            if (currently_running_process == null && !ready_queue.isEmpty()) {
                // ...get the next process from the front of the queue
                currently_running_process = ready_queue.poll(); 
                // Reset its "turn" timer to 0
                currently_running_process.current_quantum = 0; 
            }

            // c. RUN THE PROCESS (This is the main Round Robin logic)
            // If a process is on the CPU...
            if (currently_running_process != null) {
                
                // Run the process for one time unit
                currently_running_process.remaining_burst_time--;
                // ...and increase its "turn" timer
                currently_running_process.current_quantum++;

                // Check if the process FINISHED
                if (currently_running_process.remaining_burst_time == 0) {
                    // Process is finished
                    int completion = current_time + 1;
                    currently_running_process.completion_time = completion;
                    currently_running_process.turnaround_time = 
                        completion - currently_running_process.arrival_time;
                    currently_running_process.waiting_time = 
                        currently_running_process.turnaround_time - currently_running_process.burst_time;

                    completed_processes.add(currently_running_process);
                    currently_running_process = null; // CPU is now free
                    processes_completed++;
                
                // Check if the process's TIME QUANTUM EXPIRED
                } else if (currently_running_process.current_quantum == time_quantum) {
                    
                    // Preempt! The process is not done, but its turn is over.
                    // Put it at the END of the ready queue.
                    ready_queue.add(currently_running_process);
                    // Free up the CPU so a new process can start
                    currently_running_process = null; 
                }
            }
            
            if (currently_running_process != null) {
                gantt_chart_log.add(currently_running_process.pid);
            } else {
                gantt_chart_log.add("[IDLE]");
            }
            
            current_time++; 
        }
        
        // DONE - Print results
        Statistics.printResults(completed_processes, current_time, gantt_chart_log);
    }





    /**
     * Runs the Multi-Level Queue scheduling algorithm.
     * - Queue 1 (High Priority): priority < 3, runs Round Robin (RR)
     * - Queue 2 (Low Priority): priority >= 3, runs First-Come, First-Served (FCFS)
     * - Queue 1 always runs first. It preempts Queue 2.
     */
    public static void runMultiLevelQueue(List<Process> processes, Scanner scanner) {
        
        // We only need a time quantum for our High Priority queue,
        // because we decided it will run Round Robin.
        System.out.print("\nEnter the Time Quantum for the High Priority (RR) Queue: ");
        int time_quantum = scanner.nextInt();
        System.out.println("Running Multi-Level Queue...");

        int current_time = 0;
        int processes_completed = 0;
        Process currently_running_process = null;

        List<String> gantt_chart_log = new ArrayList<>();
        
        // This is the key part: We now have TWO Ready Queues

        // Queue 1: High Priority (for Round Robin)
        Queue<Process> high_priority_queue = new LinkedList<>(); 
        // Queue 2: Low Priority (for FCFS)
        Queue<Process> low_priority_queue = new LinkedList<>();  
        
        // Make copies
        List<Process> processes_to_arrive = new ArrayList<>();
        for (Process p : processes) {
            p.remaining_burst_time = p.burst_time;
            p.completion_time = 0;
            p.waiting_time = 0;
            p.turnaround_time = 0;
            p.current_quantum = 0; // Reset the RR timer
            processes_to_arrive.add(p);
        }
        Collections.sort(processes_to_arrive);
        
        List<Process> completed_processes = new ArrayList<>();

        // MAIN CLOCK LOOP
        while (processes_completed < processes.size()) {

            // a. Check for new arrivals
            // This flag is important. We need to know if a high-priority
            // process just showed up, so we can check for preemption.
            boolean new_high_priority_arrival = false;
            while (!processes_to_arrive.isEmpty() && 
                   processes_to_arrive.get(0).arrival_time <= current_time) {
                
                Process new_process = processes_to_arrive.remove(0);
                
                // This is our sorting logic
                // We decided that priority < 3 is "High Priority"
                if (new_process.priority < 3) {
                    high_priority_queue.add(new_process);
                    // Set the flag to true
                    new_high_priority_arrival = true;
                } else {
                    // Everything else is "Low Priority"
                    low_priority_queue.add(new_process);
                }
            }

            // b. SCHEDULER LOGIC (PREEMPTION)
            // If a high-priority process just arrived and a
            // low-priority process is currently running, PREEMPT IT.
            if (new_high_priority_arrival && currently_running_process != null &&
                currently_running_process.priority >= 3) {
                
                // Put the low-priority process back in its queue
                low_priority_queue.add(currently_running_process);
                currently_running_process = null; // Free the CPU
            }

            // --- Decide which process to run ---
            // If CPU is free, check high-priority queue first
            if (currently_running_process == null && !high_priority_queue.isEmpty()) {
                // If it has something, run it (RR style)
                currently_running_process = high_priority_queue.poll();
                currently_running_process.current_quantum = 0; // Reset its timer
            
            // ONLY if the high-priority queue is empty...
            } else if (currently_running_process == null && !low_priority_queue.isEmpty()) {
                // ...do we check the low-priority queue.
                    // Run this process (FCFS style)
                currently_running_process = low_priority_queue.poll();
            }


            // c. RUN THE PROCESS
            // If a process is on the CPU...
            if (currently_running_process != null) {
                // ...run it for one tick.
                currently_running_process.remaining_burst_time--;
                
                // --- Check if it's a HIGH-PRIORITY (RR) process ---
                if (currently_running_process.priority < 3) {
                    // It's a Round Robin process, so track its quantum
                    currently_running_process.current_quantum++;
                    
                    if (currently_running_process.remaining_burst_time == 0) {
                        // Process finished (RR)
                        int completion = current_time + 1;
                        currently_running_process.completion_time = completion;
                        currently_running_process.turnaround_time = completion - currently_running_process.arrival_time;
                        currently_running_process.waiting_time = currently_running_process.turnaround_time - currently_running_process.burst_time;

                        completed_processes.add(currently_running_process);
                        currently_running_process = null;
                        processes_completed++;
                    
                    // Check if its quantum expired
                    } else if (currently_running_process.current_quantum == time_quantum) {
                        // Quantum expired (RR)
                        // Put it back at the end of its queue
                        high_priority_queue.add(currently_running_process);
                        currently_running_process = null;
                    }
                
                // --- Else, it's a low-priority (FCFS) process ---
                } else {
                    if (currently_running_process.remaining_burst_time == 0) {
                        // It's an FCFS process, so we just check if it's finished.
                        // No quantum check needed.
                        int completion = current_time + 1;
                        currently_running_process.completion_time = completion;
                        currently_running_process.turnaround_time = completion - currently_running_process.arrival_time;
                        currently_running_process.waiting_time = currently_running_process.turnaround_time - currently_running_process.burst_time;

                        completed_processes.add(currently_running_process);
                        currently_running_process = null;
                        processes_completed++;
                    }
                    // (No quantum check, it's FCFS)
                }
            }
            
            if (currently_running_process != null) {
                gantt_chart_log.add(currently_running_process.pid);
            } else {
                gantt_chart_log.add("[IDLE]");
            }
            
            current_time++; 
        }
        
        // DONE - Print results
        Statistics.printResults(completed_processes, current_time, gantt_chart_log);
    }

}
