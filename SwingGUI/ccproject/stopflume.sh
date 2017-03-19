#!/bin/bash

PID=$(pgrep startflume.sh)
var2=2
var3=$(($PID+$var2))
 

kill $PID
kill $var3

hadoop fs -rmr /output

hadoop jar /home/ubuntu/ccproject/ca.jar CrisisAnalysis /input /output



