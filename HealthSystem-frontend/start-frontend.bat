@echo off
chcp 65001 >nul
title 前端服务

echo.
echo ========================================
echo   前端服务启动脚本
echo ========================================
echo.



:: 启动开发服务器
echo.
echo ========================================
echo    正在启动前端开发服务器...
echo ========================================
echo.
echo 🌐 前端地址: http://localhost:3000
echo 🔗 后端API: http://localhost:8080/
echo 💡 提示: 按 Ctrl+C 停止服务
echo.

:: 启动Vue开发服务器
call npm run dev

:: 如果程序异常退出
echo.
echo ❌ 前端服务异常退出
pause
