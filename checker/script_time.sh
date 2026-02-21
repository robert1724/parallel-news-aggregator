#!/bin/bash

# script care rulează checker.sh cu un test dat de 10 ori
# apel: ./run10.sh test_5

TEST=$1

if [ -z "$TEST" ]; then
  echo "Usage: $0 <test_name>"
  exit 1
fi

for i in {1..10}; do
  echo "===== Rulare $i din 10 pentru $TEST ====="
  ./checker.sh "$TEST"
done
