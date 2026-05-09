@echo off
title SmartWarehouse - E-Commerce Storage Management System
chcp 65001 >nul 2>&1
cls
echo Compiling SmartWarehouse System...
echo.

:: Compile all Java source files
javac -d out ecommerce\exceptions\LowStockException.java ^
             ecommerce\exceptions\InsufficientStockException.java ^
             ecommerce\exceptions\ProductNotFoundException.java ^
             ecommerce\interfaces\Trackable.java ^
             ecommerce\interfaces\Manageable.java ^
             ecommerce\products\Category.java ^
             ecommerce\products\Product.java ^
             ecommerce\products\ProductManager.java ^
             ecommerce\warehouse\Rack.java ^
             ecommerce\warehouse\StorageManager.java ^
             ecommerce\shipment\Shipment.java ^
             ecommerce\shipment\ShipmentManager.java ^
             ecommerce\main\EmployeeRole.java ^
             ecommerce\main\Employee.java ^
             ecommerce\main\Main.java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Check Java installation.
    pause
    exit /b 1
)

echo Compilation successful! Starting system...
echo.

:: Run the application
java -cp out ecommerce.main.Main

pause
