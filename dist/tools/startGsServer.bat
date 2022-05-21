@echo off
title Game DataBase Install Console

:start
echo Starting L2J Game DataBase Install.
echo.

java -Xms128m -Xmx256m -jar dbinst_gs.jar

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end

:restart
echo.
echo Admin Restarted Game DataBase Install.
echo.
goto start

:error
echo.
echo Game Server terminated abnormally!
echo.

:end
echo.
echo Game Server Terminated.
echo.
pause