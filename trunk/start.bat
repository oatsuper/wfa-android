@echo off
net stop NA_WFA_SRV
del /s /q /f "C:\work\tools\jboss\standalone\log\*.*" 
rd /s /q "C:\work\tools\jboss\standalone\tmp" 
rd /s /q "C:\work\tools\jboss\standalone\data" 
net start NA_WFA_SRV