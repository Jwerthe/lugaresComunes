@echo off
REM build-and-push.bat - Script para construir y subir imagen a DockerHub (Windows)

setlocal EnableDelayedExpansion

REM Configuración (CAMBIAR ESTOS VALORES)
set DOCKER_USERNAME=jwerther1969
set IMAGE_NAME=lugares-comunes-api
set TAG=%1
if "%TAG%"=="" set TAG=latest

echo.
echo 🚀 Build y Push a DockerHub - Lugares Comunes API
echo =================================================
echo.

REM Función para mostrar ayuda
if "%1"=="-h" goto :help
if "%1"=="--help" goto :help

REM Variables
set FULL_IMAGE_NAME=%DOCKER_USERNAME%/%IMAGE_NAME%:%TAG%
set BUILD_ARGS=
set DRY_RUN=false
set SKIP_TESTS=false

REM Procesar argumentos adicionales
:parse_args
if "%2"=="--no-cache" set BUILD_ARGS=--no-cache
if "%2"=="--skip-tests" set SKIP_TESTS=true
if "%2"=="--dry-run" set DRY_RUN=true
shift
if not "%2"=="" goto :parse_args

echo 📋 Configuración:
echo    Usuario DockerHub: %DOCKER_USERNAME%
echo    Imagen: %IMAGE_NAME%
echo    Tag: %TAG%
echo    Imagen completa: %FULL_IMAGE_NAME%
echo    Build args: %BUILD_ARGS%
echo.

REM 1. Verificar que Docker esté ejecutándose
echo 🔍 Verificando Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Docker no está ejecutándose
    pause
    exit /b 1
)
echo ✅ Docker OK
echo.

REM 2. Verificar login en DockerHub
echo 🔐 Verificando login en DockerHub...
if "%DRY_RUN%"=="false" (
    docker info | findstr /C:"Username: %DOCKER_USERNAME%" >nul
    if errorlevel 1 (
        echo ⚠️  No estás logueado en DockerHub
        echo Ejecutando: docker login
        docker login
        if errorlevel 1 (
            echo ❌ Error en login de DockerHub
            pause
            exit /b 1
        )
    ) else (
        echo ✅ Ya estás logueado en DockerHub
    )
)
echo.

REM 4. Construir imagen Docker
echo 🔨 Construyendo imagen Docker...
echo ^> docker build %BUILD_ARGS% -t %FULL_IMAGE_NAME% .
if "%DRY_RUN%"=="false" (
    docker build %BUILD_ARGS% -t %FULL_IMAGE_NAME% .
    if errorlevel 1 (
        echo ❌ Error construyendo imagen
        pause
        exit /b 1
    )
) else (
    echo   (dry-run - comando no ejecutado)
)
echo.

REM 5. Crear tag adicional como 'latest' si no es el tag principal
if not "%TAG%"=="latest" (
    set LATEST_IMAGE=%DOCKER_USERNAME%/%IMAGE_NAME%:latest
    echo 🏷️  Creando tag 'latest'...
    echo ^> docker tag %FULL_IMAGE_NAME% !LATEST_IMAGE!
    if "%DRY_RUN%"=="false" (
        docker tag %FULL_IMAGE_NAME% !LATEST_IMAGE!
    ) else (
        echo   (dry-run - comando no ejecutado)
    )
    echo.
)

REM 6. Subir imagen a DockerHub
echo 📤 Subiendo imagen a DockerHub...
echo ^> docker push %FULL_IMAGE_NAME%
if "%DRY_RUN%"=="false" (
    docker push %FULL_IMAGE_NAME%
    if errorlevel 1 (
        echo ❌ Error subiendo imagen
        pause
        exit /b 1
    )
) else (
    echo   (dry-run - comando no ejecutado)
)
echo.

if not "%TAG%"=="latest" (
    echo ^> docker push !LATEST_IMAGE!
    if "%DRY_RUN%"=="false" (
        docker push !LATEST_IMAGE!
        if errorlevel 1 (
            echo ❌ Error subiendo tag latest
            pause
            exit /b 1
        )
    ) else (
        echo   (dry-run - comando no ejecutado)
    )
    echo.
)

REM 7. Verificar que la imagen se subió correctamente
if "%DRY_RUN%"=="false" (
    echo 🔍 Verificando imagen en DockerHub...
    docker manifest inspect %FULL_IMAGE_NAME% >nul 2>&1
    if errorlevel 1 (
        echo ❌ No se pudo verificar la imagen en DockerHub
        pause
        exit /b 1
    )
    echo ✅ Imagen verificada en DockerHub
)
echo.

echo ✅ ¡Proceso completado exitosamente!
echo =================================================
echo 📦 Imagen disponible en:
echo    %FULL_IMAGE_NAME%
if not "%TAG%"=="latest" (
    echo    !LATEST_IMAGE!
)
echo.
echo 📋 Siguiente paso:
echo    Copia el archivo 'docker-compose.prod.yml' a tu VM de Azure
echo    Ejecuta: docker-compose -f docker-compose.prod.yml up -d
echo.
echo 🔧 Comandos útiles:
echo    Ver imágenes locales: docker images ^| findstr %IMAGE_NAME%
echo    Eliminar imagen local: docker rmi %FULL_IMAGE_NAME%
echo    Ver tags en DockerHub: https://hub.docker.com/r/%DOCKER_USERNAME%/%IMAGE_NAME%/tags
echo.

goto :end

:help
echo Uso: %0 [TAG] [OPTIONS]
echo.
echo Argumentos:
echo   TAG                 Tag para la imagen (default: latest)
echo.
echo Opciones:
echo   -h, --help          Mostrar esta ayuda
echo   --no-cache          Construir sin usar cache
echo   --skip-tests        Saltar tests antes de construir
echo   --dry-run           Solo mostrar comandos sin ejecutar
echo.
echo Ejemplos:
echo   %0                  # Construir y subir como 'latest'
echo   %0 v1.0.0          # Construir y subir como 'v1.0.0'
echo   %0 latest --no-cache # Construir sin cache
echo.

:end
if "%DRY_RUN%"=="false" pause