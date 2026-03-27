@echo off
setlocal

if not exist bin mkdir bin

echo Compiling main sources...
for /r src %%f in (*.java) do @echo %%f>> sources-main.txt
javac -d bin @sources-main.txt
if errorlevel 1 (
  del sources-main.txt >nul 2>nul
  exit /b 1
)
del sources-main.txt >nul 2>nul

echo Starting game...
java -cp "bin" simscli.Main
