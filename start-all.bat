@echo off
chcp 65001 >nul
cd /d D:\study\Textbook_Mall\book-mall\backend

set DB_PASSWORD=123456
set DRUID_LOGIN_PASSWORD=admin
set JWT_SECRET=book-mall-jwt-secret-key-2024-secure

echo Starting all services...
echo DB_PASSWORD=%DB_PASSWORD%

start "User"     cmd /k "java -jar User\target\User-1.0-SNAPSHOT.jar"
start "Admin"    cmd /k "java -jar Admin\target\Admin-1.0-SNAPSHOT.jar"
start "Book"     cmd /k "java -jar Book\target\Book-1.0-SNAPSHOT.jar"
start "Category" cmd /k "java -jar Category\target\Category-1.0-SNAPSHOT.jar"
start "Cart"     cmd /k "java -jar Cart\target\Cart-1.0-SNAPSHOT.jar"
start "Order"    cmd /k "java -jar Order\target\Order-1.0-SNAPSHOT.jar"
start "Payment"  cmd /k "java -jar Payment\target\Payment-1.0-SNAPSHOT.jar"
start "Address"  cmd /k "java -jar Address\target\Address-1.0-SNAPSHOT.jar"
start "Review"   cmd /k "java -jar Review\target\Review-1.0-SNAPSHOT.jar"
start "Gateway"  cmd /k "java -jar Gateway\target\Gateway-1.0-SNAPSHOT.jar"

echo.
echo All services started in separate windows.
echo Wait 30 seconds, then test:
echo   curl http://localhost:8090/api/books
pause