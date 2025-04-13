#!/bin/bash
# javac TMSimulator.java

echo "Running:"

start="$(date +'%s.%N')"

#java tm.TMSimulator file0.txt > /dev/null
#java tm.TMSimulator file2.txt > /dev/null
java tm.TMSimulator file5.txt > /dev/null

echo "Run time: $(date +"%s.%N - ${start}" | bc)s"
