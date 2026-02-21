#!/bin/bash

for i in {1..10}; do
    echo "=== Rulare $i ==="
    make run ARGS="2 ../checker/input/tests/test_2/articles.txt ../checker/input/tests/test_2/inputs.txt"
    diff all_articles.txt ../checker/output/test_2/all_articles.txt
    diff reports.txt ../checker/output/test_2/reports.txt
    diff english.txt ../checker/output/test_2/english.txt
    diff keywords_count.txt ../checker/output/test_2/keywords_count.txt
    diff Arts_Culture_and_Entertainment.txt ../checker/output/test_2/Arts_Culture_and_Entertainment.txt
done
