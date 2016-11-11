#!/usr/bin/env bash

#present directory
myPath=$(pwd)

batch_input=$myPath"/paymo_input/batch_payment.txt"
stream_input=$myPath"/paymo_input/stream_payment.txt"
output1=$myPath"/paymo_output/output1.txt"
output2=$myPath"/paymo_output/output2.txt"
output3=$myPath"/paymo_output/output3.txt"
output4=$myPath"/paymo_output/output4.txt"

cd src
chmod 777 *.java

javac *.java
java PaymoAntiFraud $batch_input $stream_input $output1 $output2 $output3 $output4
