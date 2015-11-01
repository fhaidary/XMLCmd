@echo off
cd target
dir /b xmlcmd*.jar > tmpFile
set /p Jar= < tmpFile
java -jar %Jar% %*
cd ..
