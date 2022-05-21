@echo off
title Login DataBase Install Console

:start
echo Starting L2J Login DataBase Install.
echo.

java -Xms128m -Xmx256m -jar dbinst_ls.jar

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end

:restart
echo.
echo Admin Restarted Login DataBase Install.
echo.
goto start

:error
echo.
echo Login Server terminated abnormally!
echo.

:end
echo.
echo Login Server Terminated.
echo.
pause