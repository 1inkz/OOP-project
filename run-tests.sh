#!/bin/bash

# Create bin directory if it doesn't exist
mkdir -p bin

JUNIT_JAR="lib/junit-platform-console-standalone-1.10.2.jar"
if [ ! -f "$JUNIT_JAR" ]; then
  echo "Missing $JUNIT_JAR"
  exit 1
fi

echo "Compiling main and test sources..."
javac -d bin -cp "$JUNIT_JAR" $(find src -name "*.java") $(find test -name "*.java")
if [ $? -ne 0 ]; then
  exit 1
fi

echo "Running tests..."
java -jar "$JUNIT_JAR" --class-path "bin" --scan-classpath
