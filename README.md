# Lugares Comunes - Backend API

API REST desarrollada en Spring Boot para la aplicación móvil "Lugares Comunes" de la PUCE.

## 🚀 Características

- **Spring Boot 3.5.4** con Java 17
- **Autenticación JWT** con Spring Security
- **Base de datos MySQL** con JPA/Hibernate
- **Dockerizado** con Docker Compose
- **APIs RESTful** siguiendo mejores prácticas
- **Validación de datos** con Bean Validation
- **Manejo de excepciones** globalizado
- **Logging** configurado con SLF4J
- **CORS** habilitado para desarrollo

## 📋 Requisitos Previos

- Java 17+
- Maven 3.6+
- Docker y Docker Compose
- MySQL 8.0+ (si no usas Docker)

## 🛠️ Instalación y Configuración

### Opción 1: Con Docker (Recomendado)

1. **Clona el repositorio:**
```bash
git clone <tu-repositorio>
cd lugaresComunes-backend
```

2. **Crea la estructura de directorios:**
```bash
mkdir -p docker/mysql
```

3. **Copia el archivo init.sql al directorio correcto:**
```bash
# Asegúrate de que el archivo docker/mysql/init.sql esté en su lugar
```

4. **Ejecuta con Docker Compose:**
```bash
docker-compose up -d
```

5. **Verifica que los servicios estén ejecutándose:**
```bash
docker-compose ps
```

La API estará disponible en: `http://localhost:8080/api`

### Opción 2: Desarrollo Local

1. **Configura MySQL:**
```sql
CREATE DATABASE lugares_comunes;
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'app_password';
GRANT ALL PRIVILEGES ON lugares_comunes.* TO 'app_user'@'localhost';
```

2. **Configura las variables de entorno:**
```bash
export DB_HOST=localhost
export DB_USERNAME=app_user
export DB_PASSWORD=app_password
export JWT_SECRET=tu_jwt_secret_muy_seguro
```

3. **Ejecuta la aplicación:**
```bash
mvn spring-boot:run
```

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_HOST` | Host de MySQL | `mysql-db` |
| `DB_PORT` | Puerto de MySQL | `3306` |
| `DB_NAME` | Nombre de la BD | `lugares_comunes` |
| `DB_USERNAME` | Usuario de BD | `root` |
| `DB_PASSWORD` | Contraseña de BD | `root_password` |
| `JWT_SECRET` | Clave secreta JWT | (generada) |
| `JWT_EXPIRATION` | Expiración JWT (ms) | `86400000` |
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `ALLOWED_ORIGINS` | Orígenes CORS | `*` |

## 📚 API Endpoints

### 🔐 Autenticación

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Iniciar sesión | No |
| POST | `/api/auth/register` | Registrar usuario | No |
| GET | `/api/auth/me` | Usuario actual | Sí |
| GET | `/api/auth/validate-email` | Validar email | No |
| POST | `/api/auth/logout` | Cerrar sesión | No |

### 📍 Lugares

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/places` | Listar todos los lugares | No |
| GET | `/api/places/{id}` | Obtener lugar por ID | No |
| GET | `/api/places/search?q={query}` | Buscar lugares | No |
| GET | `/api/places/type/{type}` | Lugares por tipo | No |
| GET | `/api/places/available` | Lugares disponibles | No |
| GET | `/api/places/building/{name}` | Lugares por edificio | No |
| GET | `/api/places/what3words?code={code}` | Lugar por what3words | No |
| GET | `/api/places/nearby` | Lugares cercanos | No |
| POST | `/api/places` | Crear lugar | Admin |
| PUT | `/api/places/{id}` | Actualizar lugar | Admin |
| DELETE | `/api/places/{id}` | Eliminar lugar | Admin |

### ⭐ Favoritos

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/favorites` | Listar favoritos | Sí |
| POST | `/api/favorites/{placeId}` | Agregar favorito | Sí |
| DELETE | `/api/favorites/{placeId}` | Remover favorito | Sí |
| GET | `/api/favorites/check/{placeId}` | Verificar favorito | Sí |
| PUT | `/api/favorites/toggle/{placeId}` | Toggle favorito | Sí |
| GET | `/api/favorites/count` | Contar favoritos | Sí |
| DELETE | `/api/favorites/clear` | Limpiar favoritos | Sí |

## 📱 Ejemplos de Uso

### Registro de Usuario
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "estudiante@puce.edu.ec",
    "password": "password123",
    "fullName": "Juan Pérez",
    "studentId": "EST001",
    "userType": "STUDENT"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "estudiante@puce.edu.ec",
    "password": "password123"
  }'
```

### Buscar Lugares
```bash
curl "http://localhost:8080/api/places/search?q=aula"
```

### Agregar a Favoritos
```bash
curl -X POST http://localhost:8080/api/favorites/{place-id} \
  -H "Authorization: Bearer {jwt-token}"
```

## 🗄️ Base de Datos

### Tablas Principales

- `users` - Usuarios del sistema
- `places` - Lugares del campus
- `user_favorites` - Favoritos de usuarios
- `place_reports` - Reportes de lugares
- `navigation_history` - Historial de navegación

### Tipos de Usuario

- `STUDENT` - Estudiante
- `TEACHER` - Docente
- `ADMIN` - Administrador
- `STAFF` - Personal administrativo
- `VISITOR` - Visitante

### Tipos de Lugar

- `CLASSROOM` - Aula
- `LABORATORY` - Laboratorio
- `LIBRARY` - Biblioteca
- `CAFETERIA` - Cafetería
- `OFFICE` - Oficina
- `AUDITORIUM` - Auditorio
- `SERVICE` - Servicio

## 🔧 Administración

### Acceso a Base de Datos

Adminer está disponible en: `http://localhost:8081`

Credenciales:
- **Servidor:** `mysql-db`
- **Usuario:** `root`
- **Contraseña:** `root_password`
- **Base de datos:** `lugares_comunes`

### Logs

Para ver los logs en tiempo real:

```bash
# Logs de la aplicación
docker-compose logs -f lugares-comunes-api

# Logs de MySQL
docker-compose logs -f mysql-db
```

### Health Check

Verifica el estado de la aplicación:

```bash
curl http://localhost:8080/api/auth/health
curl http://localhost:8080/api/places/health
```

## 🚀 Despliegue en Azure

### Preparación para Azure

1. **Modifica docker-compose.yml para producción:**
```yaml
# Cambiar variables de entorno para producción
environment:
  DB_PASSWORD: ${AZURE_DB_PASSWORD}
  JWT_SECRET: ${AZURE_JWT_SECRET}
  ALLOWED_ORIGINS: "https://tu-dominio.com"
```

2. **Usa Azure Container Registry:**
```bash
# Tag y push de la imagen
docker build -t tu-registry.azurecr.io/lugares-comunes:latest .
docker push tu-registry.azurecr.io/lugares-comunes:latest
```

### Azure Container Instances

```bash
az container create \
  --resource-group tu-resource-group \
  --name lugares-comunes-api \
  --image tu-registry.azurecr.io/lugares-comunes:latest \
  --ports 8080 \
  --environment-variables \
    DB_HOST=tu-mysql-server.mysql.database.azure.com \
    DB_USERNAME=tu-usuario \
    DB_PASSWORD=tu-password
```

## 🧪 Testing

### Ejecutar Tests
```bash
mvn test
```

### Test de Endpoints
```bash
# Health check
curl http://localhost:8080/api/auth/health

# Obtener lugares
curl http://localhost:8080/api/places
```

## 📝 Notas Importantes

1. **Seguridad:** Cambia las contraseñas y JWT secret en producción
2. **CORS:** Configura orígenes específicos para producción
3. **SSL:** Habilita HTTPS en producción
4. **Backup:** Configura backups automáticos de MySQL
5. **Monitoreo:** Implementa monitoreo con Azure Application Insights

## 🐛 Troubleshooting

### Problemas Comunes

1. **Error de conexión a BD:**
```bash
docker-compose down
docker volume prune -f
docker-compose up -d
```

2. **Puerto ya en uso:**
```bash
# Cambiar puertos en docker-compose.yml
ports:
  - "8081:8080"  # Cambiar puerto externo
```

3. **Memoria insuficiente:**
```bash
# Aumentar memoria en docker-compose.yml
environment:
  JAVA_OPTS: "-Xmx1g -Xms512m"
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature
3. Commit tus cambios
4. Push a la rama
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está licenciado bajo [MIT License](LICENSE).

---

**Desarrollado para PUCE - Lugares Comunes 🎓📍**