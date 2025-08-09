@echo off
echo 🔧 Solucionando problemas de Docker para Lugares Comunes...

REM Verificar Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker no está instalado o no está en el PATH
    echo 💡 Instala Docker Desktop desde: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo ✅ Docker encontrado

REM Verificar si Docker está ejecutándose
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker no está ejecutándose
    echo 💡 Inicia Docker Desktop y espera a que se complete la inicialización
    pause
    exit /b 1
)

echo ✅ Docker está ejecutándose

REM Limpiar contenedores e imágenes previos
echo 🧹 Limpiando contenedores e imágenes previos...
docker-compose down --remove-orphans 2>nul
docker system prune -f 2>nul

REM Verificar conectividad a Docker Hub
echo 🌐 Verificando conectividad a Docker Hub...
docker pull hello-world >nul 2>&1
if errorlevel 1 (
    echo ❌ No se puede conectar a Docker Hub
    echo 💡 Verifica tu conexión a internet o configuración de proxy
    pause
    exit /b 1
)

echo ✅ Conectividad a Docker Hub OK

REM Limpiar imagen hello-world
docker rmi hello-world >nul 2>&1

REM Intentar construir sin cache
echo 🔨 Construyendo proyecto sin cache...
docker-compose build --no-cache --pull

if errorlevel 1 (
    echo ❌ Error en la construcción
    echo.
    echo 🔍 Posibles soluciones:
    echo 1. Verificar que todos los archivos estén en su lugar
    echo 2. Verificar conexión a internet
    echo 3. Intentar con: docker-compose build --no-cache mysql-db
    echo 4. Luego: docker-compose build --no-cache lugares-comunes-api
    pause
    exit /b 1
)

echo ✅ Construcción exitosa

REM Ejecutar servicios
echo 🚀 Iniciando servicios...
docker-compose up -d

if errorlevel 1 (
    echo ❌ Error iniciando servicios
    echo 📋 Mostrando logs para diagnóstico...
    docker-compose logs
    pause
    exit /b 1
)

REM Esperar a que los servicios estén listos
echo ⏳ Esperando a que los servicios estén listos...
timeout /t 15 /nobreak > nul

REM Verificar estado de los servicios
echo 📊 Verificando estado de servicios...
docker-compose ps

REM Test de conectividad a la API
echo 🔍 Verificando API...
timeout /t 5 /nobreak > nul
curl -s http://localhost:8080/api/auth/health >nul 2>&1
if errorlevel 1 (
    echo ⚠️ La API aún no responde, pero los contenedores están ejecutándose
    echo 💡 Espera unos minutos más y verifica en: http://localhost:8080/api/auth/health
) else (
    echo ✅ API respondiendo correctamente
)

echo.
echo 🎉 ¡Configuración completada!
echo.
echo 🌐 Servicios disponibles:
echo    API: http://localhost:8080/api
echo    Adminer: http://localhost:8081
echo    Health: http://localhost:8080/api/auth/health
echo.
echo 📋 Usuarios de prueba:
echo    👤 Admin: admin@puce.edu.ec / admin123
echo    🎓 Estudiante: estudiante@puce.edu.ec / estudiante123
echo    👩‍🏫 Docente: docente@puce.edu.ec / docente123
echo.
echo 🔧 Comandos útiles:
echo    Ver logs: docker-compose logs -f
echo    Detener: docker-compose down
echo    Reiniciar: docker-compose restart

pause