# Project #3: Turing Machine Simulator

* Author: Jordan Pittman, Caleb Pollock
* Class: CS361 Section 1
* Semester: Spring 2025

## Overview

This Java program simulates a deterministic Turing Machine with a bi infinite tape, parsing the machine's encoding and input string from a file. It executes the machine's transitions, halts at the accepting state, and outputs the content of visited tape cells.

## Reflection

### Caleb

This project was nice. I had read a couple of months ago about the HashLife algorithm for computing the game of life (Conway) and felt like it could be applied to this problem. I fooled around with it a bit and came up with a method of caching blocks already seen and replaying them when they were encountered again. It runs pretty fast so I am happy.

### Jordan

Overall, this project went really smooth due to Caleb's work he put in. After reviewing the code, I supplied the initial tape string loading functionality which distributes the intput across TMBlocks of size 256. Due to the nature of the project, I created a new branch to test this without affecting the main codebase. 


## Compiling and Using

To compile and run:
```
javac tm/TMSimulator.java
java tm.TMSimulator <input file>
```

## Sources Used

 * https://en.wikipedia.org/wiki/Hashlife
 * https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function
 * https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html 
 * https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
 * https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html

