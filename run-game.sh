#!/bin/bash

# Create bin directory if it doesn't exist
mkdir -p bin

echo "Compiling main sources..."
javac -d bin $(find src -name "*.java")
if [ $? -ne 0 ]; then
  exit 1
fi

echo "Starting game..."
java -cp "bin" simscli.Main
