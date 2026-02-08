@echo off
setlocal enabledelayedexpansion

if exist ".env" call :load_env

if "%~1"=="" goto help

if /i "%~1"=="help" goto help
if /i "%~1"=="build" goto build
if /i "%~1"=="test" goto test
if /i "%~1"=="test-one" goto test_one
if /i "%~1"=="dev" goto dev
if /i "%~1"=="run" goto run
if /i "%~1"=="start" goto run
if /i "%~1"=="clean" goto clean
if /i "%~1"=="flyway-migrate" goto flyway_migrate
if /i "%~1"=="flyway-clean" goto flyway_clean
if /i "%~1"=="flyway-info" goto flyway_info
if /i "%~1"=="flyway-validate" goto flyway_validate
if /i "%~1"=="flyway-repair" goto flyway_repair
if /i "%~1"=="docker-build" goto docker_build
if /i "%~1"=="kind-load" goto kind_load
if /i "%~1"=="k8s-restart" goto k8s_restart
if /i "%~1"=="k8s-logs" goto k8s_logs
if /i "%~1"=="k8s-delete-pods" goto k8s_delete_pods
if /i "%~1"=="curl-health" goto curl_health
if /i "%~1"=="curl-swagger" goto curl_swagger
if /i "%~1"=="hash" goto hash
if /i "%~1"=="redeploy" goto redeploy
if /i "%~1"=="tls-generate" goto tls_generate
if /i "%~1"=="tls-apply" goto tls_apply
if /i "%~1"=="tls-delete" goto tls_delete

echo Unknown target: %~1
exit /b 1

:help
@echo PillMind Backend - Makefile.bat Commands
@echo -------------------------------------
@echo Makefile.bat build             - Gradle build
@echo Makefile.bat test              - Run tests
@echo Makefile.bat test-one TEST     - Run a specific test class or method
@echo Makefile.bat dev               - Run app with hot reload
@echo Makefile.bat run               - Run app (local)
@echo Makefile.bat start             - Run app (alias for run)
@echo Makefile.bat clean             - Clean build
@echo Makefile.bat flyway-migrate    - Run Flyway migrations
@echo Makefile.bat flyway-clean      - Clean Flyway schema (DANGEROUS)
@echo Makefile.bat flyway-info       - Show Flyway status
@echo Makefile.bat flyway-validate   - Validate Flyway migrations
@echo Makefile.bat flyway-repair     - Repair Flyway metadata
@echo Makefile.bat docker-build      - Build Docker image
@echo Makefile.bat kind-load         - Load image into kind cluster
@echo Makefile.bat k8s-restart       - Restart deployment in cluster
@echo Makefile.bat k8s-logs          - Tail logs from backend pods
@echo Makefile.bat k8s-delete-pods   - Delete backend pods (force restart)
@echo Makefile.bat curl-health       - Check health endpoint via ingress
@echo Makefile.bat curl-swagger      - Check Swagger UI via ingress
@echo Makefile.bat hash PASSWORD     - Generate BCrypt hash for a password
@echo Makefile.bat redeploy          - Build, load to kind, and restart pods
@echo Makefile.bat tls-generate      - Generate self-signed TLS cert/key (dev)
@echo Makefile.bat tls-apply         - Create/Update Kubernetes TLS secret
@echo Makefile.bat tls-delete        - Delete Kubernetes TLS secret
exit /b 0

:build
call .\gradlew.bat build
exit /b %errorlevel%

:test
call .\gradlew.bat test
exit /b %errorlevel%

:test_one
if "%~2"=="" (
  echo Usage: Makefile.bat test-one SignUpControllerIntegrationTest.shouldCreateUserAndPersistToDatabase
  exit /b 1
)
call .\gradlew.bat test --tests "*%~2"
exit /b %errorlevel%

:dev
call .\gradlew.bat -t run
exit /b %errorlevel%

:run
call .\gradlew.bat run
exit /b %errorlevel%

:clean
call .\gradlew.bat clean
exit /b %errorlevel%

:flyway_migrate
call .\gradlew.bat flywayMigrate
exit /b %errorlevel%

:flyway_clean
call .\gradlew.bat flywayClean
exit /b %errorlevel%

:flyway_info
call .\gradlew.bat flywayInfo
exit /b %errorlevel%

:flyway_validate
call .\gradlew.bat flywayValidate
exit /b %errorlevel%

:flyway_repair
call .\gradlew.bat flywayRepair
exit /b %errorlevel%

:docker_build
set IMAGE=pillmind-backend:latest
set DOCKERFILE=docker/Dockerfile
call docker build -t %IMAGE% -f %DOCKERFILE% .
exit /b %errorlevel%

:kind_load
set IMAGE=pillmind-backend:latest
set KIND_CLUSTER=pillmind
call kind load docker-image %IMAGE% --name %KIND_CLUSTER%
exit /b %errorlevel%

:k8s_restart
set APP_NAME=pillmind-backend
set NAMESPACE=pillmind-dev
call kubectl rollout restart deployment %APP_NAME% -n %NAMESPACE%
exit /b %errorlevel%

:k8s_logs
set APP_NAME=pillmind-backend
set NAMESPACE=pillmind-dev
call kubectl logs -n %NAMESPACE% -l app=%APP_NAME% --tail=100 -f
exit /b %errorlevel%

:k8s_delete_pods
set APP_NAME=pillmind-backend
set NAMESPACE=pillmind-dev
call kubectl delete pods -n %NAMESPACE% -l app=%APP_NAME%
exit /b %errorlevel%

:curl_health
set DOMAIN=pillmind.192.168.1.7.nip.io
call curl -k -i https://%DOMAIN%/api/health
exit /b %errorlevel%

:curl_swagger
set DOMAIN=pillmind.192.168.1.7.nip.io
call curl -k -I https://%DOMAIN%/swagger-ui
exit /b %errorlevel%

:hash
if "%~2"=="" (
  echo Usage: Makefile.bat hash yourPass
  exit /b 1
)
call .\gradlew.bat generateHash -Ppassword="%~2"
exit /b %errorlevel%

:redeploy
call %~f0 docker-build
if errorlevel 1 exit /b %errorlevel%
call %~f0 kind-load
if errorlevel 1 exit /b %errorlevel%
call %~f0 k8s-delete-pods
exit /b %errorlevel%

:tls_generate
set DOMAIN=pillmind.192.168.1.7.nip.io
set CERT_DIR=certs
set TLS_SECRET=pillmind-tls
if not exist %CERT_DIR% mkdir %CERT_DIR%
call openssl req -x509 -nodes -newkey rsa:2048 -days 365 -subj "/CN=%DOMAIN%" -addext "subjectAltName=DNS:%DOMAIN%" -keyout %CERT_DIR%\%TLS_SECRET%.key -out %CERT_DIR%\%TLS_SECRET%.crt
exit /b %errorlevel%

:tls_apply
set CERT_DIR=certs
set TLS_SECRET=pillmind-tls
set NAMESPACE=pillmind-dev
call kubectl -n %NAMESPACE% create secret tls %TLS_SECRET% --cert=%CERT_DIR%\%TLS_SECRET%.crt --key=%CERT_DIR%\%TLS_SECRET%.key --dry-run=client -o yaml | kubectl apply -f -
exit /b %errorlevel%

:tls_delete
set TLS_SECRET=pillmind-tls
set NAMESPACE=pillmind-dev
call kubectl -n %NAMESPACE% delete secret %TLS_SECRET%
exit /b 0

:load_env
for /f "usebackq delims=" %%L in (".env") do (
  set "line=%%L"
  if not "!line!"=="" (
    if not "!line:~0,1!"=="#" (
      for /f "tokens=1* delims==" %%K in ("!line!") do (
        set "key=%%K"
        set "val=%%L"
        if "!val:~0,1!"=="\"" if "!val:~-1!"=="\"" set "val=!val:~1,-1!"
        if "!val:~0,1!"=="'" if "!val:~-1!"=="'" set "val=!val:~1,-1!"
        if not defined !key! set "!key!=!val!"
      )
    )
  )
)
exit /b 0
