@echo off
REM build-and-run.bat - Script para Windows para construir y ejecutar Lugares Comunes API

echo 🚀 Construyendo y ejecutando Lugares Comunes API...

REM Verificar si se proporcionó un argumento
if "%1"=="" goto menu
if "%1"=="-h" goto help
if "%1"=="--help" goto help
if "%1"=="-d" goto docker
if "%1"=="--docker" goto docker
if "%1"=="-l" goto local
if "%1"=="--local" goto local
if "%1"=="-t" goto test
if "%1"=="--test" goto test
if "%1"=="-c" goto clean
if "%1"=="--clean" goto clean
if "%1"=="-b" goto build
if "%1"=="--build" goto build
if "%1"=="--stop" goto stop
if "%1"=="--logs" goto logs

echo ❌ Opción desconocida: %1
goto help

:help
echo Uso: %0 [OPCIÓN]
echo.
echo Opciones:
echo   -h, --help          Mostrar esta ayuda
echo   -d, --docker        Ejecutar con Docker
echo   -l, --local         Ejecutar localmente
echo   -t, --test          Ejecutar tests
echo   -c, --clean         Limpiar proyecto
echo   -b, --build         Solo construir
echo   --stop              Detener contenedores Docker
echo   --logs              Ver logs de Docker
goto end

:menu
echo 🤔 ¿Cómo quieres ejecutar el proyecto?
echo 1) Docker (recomendado)
echo 2) Local
echo 3) Solo tests
set /p choice="Selecciona una opción (1-3): "

if "%choice%"=="1" goto docker
if "%choice%"=="2" goto local
if "%choice%"=="3" goto test
echo ❌ Opción inválida
goto end

:docker
echo 🐳 Ejecutando con Docker...

REM Verificar si Docker está disponible
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Docker no está instalado o no está disponible
    goto end
)

REM Verificar si docker-compose está disponible
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: docker-compose no está instalado o no está disponible
    goto end
)

echo 📦 Construyendo contenedores...
docker-compose down
docker-compose build

echo 🚀 Iniciando servicios...
docker-compose up -d

echo ⏳ Esperando a que los servicios estén listos...
timeout /t 10 /nobreak > nul

echo ✅ Servicios ejecutándose correctamente!
echo.
echo 🌐 API: http://localhost:8080/api
echo 🗄️  Adminer: http://localhost:8081
echo 📊 Health: http://localhost:8080/api/auth/health
echo.
echo 📋 Usuarios de prueba:
echo    👤 Admin: admin@puce.edu.ec / admin123
echo    🎓 Estudiante: estudiante@puce.edu.ec / estudiante123
echo    👩‍🏫 Docente: docente@puce.edu.ec / docente123
echo.
echo Ver logs: docker-compose logs -f
goto end

:local
echo 🏠 Ejecutando localmente...

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java no está instalado
    goto end
)

REM Verificar Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven no está instalado
    goto end
)

REM Configurar variables de entorno
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=lugares_comunes
set DB_USERNAME=root
set DB_PASSWORD=password
set JWT_SECRET=local_jwt_secret_key_for_development_only
set SPRING_PROFILES_ACTIVE=dev

echo 🔧 Construyendo proyecto...
call mvn clean package -DskipTests

if errorlevel 1 (
    echo ❌ Error en la construcción
    goto end
)

echo ✅ Construcción exitosa!
echo 🚀 Iniciando aplicación...

REM Buscar el archivo JAR
for %%f in (target\*.jar) do set JAR_FILE=%%f

if exist "%JAR_FILE%" (
    java -jar "%JAR_FILE%"
) else (
    echo ❌ No se encontró el archivo JAR
)
goto end

:test
echo 🧪 Ejecutando tests...
call mvn test

if errorlevel 1 (
    echo ❌ Algunos tests fallaron
) else (
    echo ✅ Todos los tests pasaron!
)
goto end

:clean
echo 🧹 Limpiando proyecto...

REM Limpiar Maven
call mvn clean

REM Limpiar Docker si está disponible
docker --version >nul 2>&1
if not errorlevel 1 (
    docker-compose down
    docker system prune -f
    docker volume prune -f
)

echo ✅ Proyecto limpio!
goto end

:build
echo 🔨 Construyendo proyecto...
call mvn clean package -DskipTests

if errorlevel 1 (
    echo ❌ Error en la construcción
) else (
    echo ✅ Construcción exitosa!
    echo 📦 JAR ubicado en: target\
    dir target\*.jar
)
goto end

:stop
echo 🛑 Deteniendo contenedores...
docker-compose down
echo ✅ Contenedores detenidos
goto end

:logs
echo 📋 Mostrando logs...
docker-compose logs -f
goto end

:end
pause