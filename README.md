# CPU Scheduling Simulator
This is a Java-based simulator created for a Data Structures and Algorithms project. Its purpose is to implement, run, and compare seven different CPU scheduling algorithms.

The program reads a set of processes from an input.txt file, simulates the chosen algorithm, and then prints a full Gantt Chart and a table of performance statistics.

## Features - 

**Simulation Engine**: A tick-based clock (current_time) that accurately simulates process arrivals, execution, and preemption.

**Data Structures**: Uses fundamental data structures like Queues (for FCFS/RR) and Priority Queues (for SJF/Priority) to manage the ready state.

**Complete Statistics**: Calculates and displays the Average Waiting Time and Average Turnaround Time.

**Gantt Chart**: Provides a compressed, text-based Gantt chart for a visual timeline of the simulation.

## Implemented Algorithms -
### The simulator supports 7 different algorithms:

First-Come, First-Served (FCFS)

Shortest-Job-First (SJF) (Non-Preemptive)

Shortest-Remaining-Time-First (SRTF) (Preemptive)

Priority (Non-Preemptive)

Priority (Preemptive)

Round Robin (RR) (Requires a Time Quantum input)

Multi-Level Queue (Uses RR for a high-priority queue and FCFS for a low-priority queue)
