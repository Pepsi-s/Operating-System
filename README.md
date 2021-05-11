# Operating-System

Overview:

Two CPU process scheduling and memory management system process is based on the single CPU 
 system but with two processors in symmetric multiprocessing mode.
 
The first CPU uses the exactly structure as the CPU used in the single CPU system, which
 is RR(Q=8) + RR(Q=8) + FCFS. The second uses RR(Q=8) + RR(Q=8) for the first two
 priority levels, but the bottom level uses the SJF algorithm.
 It uses the two bit clock algorithm and the modified code from pervious CPU Scheduling
 project. In addition, it uses a while loop to simulate the CPU working overtime. There are
 detailed comments in the code if you would like to take a look. 
