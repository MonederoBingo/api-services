start cmd /k ..\configuration-service\start.bat
ping 127.0.0.1 -n 10 > nul
start cmd /k ..\authorization-service\start.bat
start cmd /k ..\points-configuration-service\start.bat
