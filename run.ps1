Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "        Lost & Found - Campus Mystery Finder" -ForegroundColor Yellow
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Starting Spring Boot Web Server on http://localhost:8080 ..." -ForegroundColor Green
Write-Host ""
Write-Host "Default Logins:" -ForegroundColor White
Write-Host "  - Student: student@campus.edu (Instant access)" -ForegroundColor Gray
Write-Host "  - Admin:   admin@campus.edu   / Password: Admin@123" -ForegroundColor Gray
Write-Host ""
Write-Host "Press Ctrl+C anytime to stop the server." -ForegroundColor White
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

if (Test-Path "target\app.args") {
    & java "@target\app.args" com.campus.lostfound.CampusApplication
} else {
    & ".\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
}
