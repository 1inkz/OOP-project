@echo off
setlocal

if not exist bin mkdir bin

set "JUNIT_JAR=lib\junit-platform-console-standalone-1.10.2.jar"
if not exist "%JUNIT_JAR%" (
  echo Missing %JUNIT_JAR%
  exit /b 1
)

echo Compiling main and test sources...
for /r src %%f in (*.java) do @echo %%f>> sources-main.txt
for /r test %%f in (*.java) do @echo %%f>> sources-test.txt
type sources-main.txt sources-test.txt > sources-all.txt
javac -d bin -cp "%JUNIT_JAR%" @sources-all.txt
if errorlevel 1 (
  del sources-main.txt sources-test.txt sources-all.txt >nul 2>nul
  exit /b 1
)

del sources-main.txt sources-test.txt sources-all.txt >nul 2>nul

echo Running tests...
java -jar "%JUNIT_JAR%" --class-path "bin" --scan-classpath
