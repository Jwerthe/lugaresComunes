#!/bin/bash
# build-and-run.sh - Script para construir y ejecutar el proyecto

echo "🚀 Construyendo y ejecutando Lugares Comunes API..."

# Función para mostrar ayuda
show_help() {
    echo "Uso: $0 [OPCIÓN]"
    echo ""
    echo "Opciones:"
    echo "  -h, --help          Mostrar esta ayuda"
    echo "  -d, --docker        Ejecutar con Docker"
    echo "  -l, --local         Ejecutar localmente"
    echo "  -t, --test          Ejecutar tests"
    echo "  -c, --clean         Limpiar proyecto"
    echo "  -b, --build         Solo construir"
    echo "  --stop              Detener contenedores Docker"
    echo "  --logs              Ver logs de Docker"
    echo "  --db-reset          Resetear base de datos"
}

# Función para ejecutar con Docker
run_docker() {
    echo "🐳 Ejecutando con Docker..."
    
    # Verificar si Docker está ejecutándose
    if ! docker info > /dev/null 2>&1; then
        echo "❌ Error: Docker no está ejecutándose"
        exit 1
    fi
    
    # Construir y ejecutar
    echo "📦 Construyendo contenedores..."
    docker-compose down
    docker-compose build
    
    echo "🚀 Iniciando servicios..."
    docker-compose up -d
    
    echo "⏳ Esperando a que los servicios estén listos..."
    sleep 10
    
    # Verificar estado de los servicios
    if docker-compose ps | grep -q "Up"; then
        echo "✅ Servicios ejecutándose correctamente!"
        echo ""
        echo "🌐 API: http://localhost:8080/api"
        echo "🗄️  Adminer: http://localhost:8081"
        echo "📊 Health: http://localhost:8080/api/auth/health"
        echo ""
        echo "📋 Usuarios de prueba:"
        echo "   👤 Admin: admin@puce.edu.ec / admin123"
        echo "   🎓 Estudiante: estudiante@puce.edu.ec / estudiante123"
        echo "   👩‍🏫 Docente: docente@puce.edu.ec / docente123"
        echo ""
        echo "Ver logs: docker-compose logs -f"
    else
        echo "❌ Error iniciando servicios"
        docker-compose ps
        exit 1
    fi
}

# Función para ejecutar localmente
run_local() {
    echo "🏠 Ejecutando localmente..."
    
    # Verificar Java
    if ! command -v java &> /dev/null; then
        echo "❌ Java no está instalado"
        exit 1
    fi
    
    # Verificar Maven
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven no está instalado"
        exit 1
    fi
    
    # Configurar variables de entorno para desarrollo local
    export DB_HOST=localhost
    export DB_PORT=3306
    export DB_NAME=lugares_comunes
    export DB_USERNAME=root
    export DB_PASSWORD=password
    export JWT_SECRET=local_jwt_secret_key_for_development_only
    export SPRING_PROFILES_ACTIVE=dev
    
    echo "🔧 Construyendo proyecto..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "✅ Construcción exitosa!"
        echo "🚀 Iniciando aplicación..."
        java -jar target/*.jar
    else
        echo "❌ Error en la construcción"
        exit 1
    fi
}

# Función para ejecutar tests
run_tests() {
    echo "🧪 Ejecutando tests..."
    mvn test
    
    if [ $? -eq 0 ]; then
        echo "✅ Todos los tests pasaron!"
    else
        echo "❌ Algunos tests fallaron"
        exit 1
    fi
}

# Función para limpiar proyecto
clean_project() {
    echo "🧹 Limpiando proyecto..."
    
    # Limpiar Maven
    mvn clean
    
    # Limpiar Docker
    docker-compose down
    docker system prune -f
    docker volume prune -f
    
    echo "✅ Proyecto limpio!"
}

# Función para solo construir
build_only() {
    echo "🔨 Construyendo proyecto..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "✅ Construcción exitosa!"
        echo "📦 JAR ubicado en: target/"
        ls -la target/*.jar
    else
        echo "❌ Error en la construcción"
        exit 1
    fi
}

# Función para detener Docker
stop_docker() {
    echo "🛑 Deteniendo contenedores..."
    docker-compose down
    echo "✅ Contenedores detenidos"
}

# Función para ver logs
show_logs() {
    echo "📋 Mostrando logs..."
    docker-compose logs -f
}

# Función para resetear base de datos
reset_database() {
    echo "🗄️ Reseteando base de datos..."
    docker-compose down
    docker volume rm $(docker volume ls -q | grep mysql) 2>/dev/null
    docker-compose up -d mysql-db
    echo "✅ Base de datos reseteada"
}

# Procesar argumentos
case "${1:-}" in
    -h|--help)
        show_help
        ;;
    -d|--docker)
        run_docker
        ;;
    -l|--local)
        run_local
        ;;
    -t|--test)
        run_tests
        ;;
    -c|--clean)
        clean_project
        ;;
    -b|--build)
        build_only
        ;;
    --stop)
        stop_docker
        ;;
    --logs)
        show_logs
        ;;
    --db-reset)
        reset_database
        ;;
    "")
        echo "🤔 ¿Cómo quieres ejecutar el proyecto?"
        echo "1) Docker (recomendado)"
        echo "2) Local"
        echo "3) Solo tests"
        read -p "Selecciona una opción (1-3): " choice
        
        case $choice in
            1) run_docker ;;
            2) run_local ;;
            3) run_tests ;;
            *) echo "❌ Opción inválida"; exit 1 ;;
        esac
        ;;
    *)
        echo "❌ Opción desconocida: $1"
        show_help
        exit 1
        ;;
esac