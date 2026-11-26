@echo off
title Lost ^& Found - Campus Mystery Finder
cls
echo ================================================================
echo         Lost ^& Found - Campus Mystery Finder
echo ================================================================
echo.
echo Starting Spring Boot Web Server on http://localhost:8080 ...
echo.
echo Default Logins:
echo   - Student: student@campus.edu (Instant access)
echo   - Admin:   admin@campus.edu   / Password: Admin@123
echo.
echo Press Ctrl+C anytime to stop the server.
echo ================================================================
echo.

if exist "target\app.args" (
    java @target\app.args com.campus.lostfound.CampusApplication
) else (
    "%~dp0apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
)
pause
