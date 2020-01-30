#!/bin/bash
# Grabs and kill a process from the pidlist that has the word myapp

pid=`ps aux | grep parser | awk '{print $2}'`
kill -9 $pid

pid=`ps aux | grep Leertaak2 | awk '{print $2}'`
kill -9 $pid