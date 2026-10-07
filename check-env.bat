@echo off
chcp 65001 >nul
echo ===== Environment Check =====
echo(
echo [1] JDK version
java -version
echo(
echo [2] Maven version
call mvn -v
echo(
echo [3] Node / npm
node -v
call npm -v
echo(
echo [4] MySQL connection
mysql -u root -p123456 -e "SELECT VERSION();"
echo(
echo [5] Port check
netstat -ano | findstr ":3306 :6379 :8848 :8090"
echo(
echo Check completed!
pause
