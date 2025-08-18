# Lugares Comunes - Backend API

API REST desarrollada en Spring Boot para la aplicación móvil "Lugares Comunes" de la PUCE con **Sistema de Navegación con Rutas**.

## 🚀 Características

- **Spring Boot 3.5.4** con Java 17
- **Autenticación JWT** con Spring Security
- **Base de datos MySQL** con JPA/Hibernate
- **Sistema de Rutas de Navegación** con GPS
- **Sistema de Propuestas** de rutas por usuarios
- **Sistema de Calificaciones** y contribuciones
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

3. **Ejecuta con Docker Compose:**
```bash
docker-compose up -d
```

4. **Verifica que los servicios estén ejecutándose:**
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
| GET | `/api/auth/health` | Health check auth | No |

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

### 🗺️ Rutas de Navegación

#### 🌐 Endpoints Públicos (sin autenticación)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/routes/destinations` | Lista destinos disponibles con cantidad de rutas |
| GET | `/api/routes/to/{placeId}` | Rutas disponibles a un destino específico |
| GET | `/api/routes/{routeId}/points` | Puntos detallados de una ruta |
| GET | `/api/routes/nearest?lat=X&lng=Y&destination=placeId` | Ruta más cercana al usuario |
| GET | `/api/routes/{routeId}/details` | Información completa de una ruta |
| GET | `/api/routes/health` | Health check rutas |

#### 🔐 Endpoints Protegidos (requieren JWT)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/routes/{routeId}/rating` | Calificar una ruta (1-5 estrellas) |
| GET | `/api/routes/{routeId}/my-rating` | Ver mi calificación de una ruta |

#### 🛡️ Endpoints Solo Admin

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/routes` | Crear ruta oficial directamente |
| PUT | `/api/routes/{routeId}` | Actualizar ruta existente |
| DELETE | `/api/routes/{routeId}` | Eliminar ruta |
| GET | `/api/routes/analytics` | Estadísticas de uso de rutas |

### 💡 Propuestas de Rutas

#### 🔐 Endpoints Protegidos (cualquier usuario logueado)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/routes/proposals` | Enviar propuesta de nueva ruta |
| GET | `/api/routes/proposals/my` | Ver mis propuestas enviadas |

#### 🛡️ Endpoints Solo Admin

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/routes/proposals/pending` | Ver propuestas pendientes |
| PUT | `/api/routes/proposals/{proposalId}/approve?notes=comentario` | Aprobar propuesta y crear ruta |
| PUT | `/api/routes/proposals/{proposalId}/reject?notes=comentario` | Rechazar propuesta |

### 🧭 Navegación

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/navigation/start` | Registrar inicio de navegación | Sí |
| POST | `/api/navigation/complete` | Registrar finalización de navegación | Sí |
| GET | `/api/navigation/history` | Obtener historial de navegación del usuario | Sí |

### 👥 Gestión de Usuarios

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| PUT | `/api/users/{userId}/promote` | Promover usuario a ADMIN | Admin |
| GET | `/api/users/contributors` | Ver usuarios con más contribuciones | Admin |
| GET | `/api/users/promotions/recent` | Ver promociones recientes | Admin |

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

## 📱 Ejemplos de Uso Detallados

### 🔐 Autenticación

#### Registro de Usuario
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

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "estudiante@puce.edu.ec",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": "uuid-here",
      "email": "estudiante@puce.edu.ec",
      "fullName": "Juan Pérez",
      "userType": "STUDENT",
      "contributionScore": 0
    }
  }
}
```

### 🗺️ Sistema de Rutas

#### Obtener Destinos Disponibles
```bash
curl http://localhost:8080/api/routes/destinations
```

**Response:**
```json
{
  "success": true,
  "message": "Destinos disponibles obtenidos exitosamente",
  "count": 4,
  "data": [
    {
      "id": "uuid-here",
      "name": "Biblioteca Central",
      "category": "Biblioteca",
      "placeType": "LIBRARY",
      "latitude": 19.3326,
      "longitude": -99.1844,
      "routeCount": 2,
      "isRouteDestination": true
    }
  ]
}
```

#### Obtener Ruta Más Cercana
```bash
curl "http://localhost:8080/api/routes/nearest?lat=19.3310&lng=-99.1830&destination=place-uuid"
```

#### Obtener Puntos de una Ruta
```bash
curl http://localhost:8080/api/routes/route-uuid/points
```

**Response:**
```json
{
  "success": true,
  "message": "Puntos de ruta obtenidos exitosamente",
  "count": 5,
  "data": [
    {
      "id": "point-uuid",
      "latitude": 19.3310,
      "longitude": -99.1830,
      "orderIndex": 0,
      "pointType": "START",
      "instruction": "Punto de inicio en la entrada principal",
      "distanceFromPrevious": 0
    },
    {
      "id": "point-uuid-2",
      "latitude": 19.3320,
      "longitude": -99.1840,
      "orderIndex": 1,
      "pointType": "WAYPOINT",
      "instruction": "Camina derecho por el sendero principal",
      "distanceFromPrevious": 150
    }
  ]
}
```

#### Calificar una Ruta
```bash
curl -X POST http://localhost:8080/api/routes/route-uuid/rating \
  -H "Authorization: Bearer jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "rating": 4,
    "comment": "Excelente ruta, muy clara y rápida"
  }'
```

### 💡 Propuestas de Rutas

#### Enviar Propuesta
```bash
curl -X POST http://localhost:8080/api/routes/proposals \
  -H "Authorization: Bearer jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Ruta accesible a la Cafetería",
    "description": "Propongo una ruta sin escalones para personas con movilidad reducida",
    "fromLatitude": 19.3315,
    "fromLongitude": -99.1835,
    "fromDescription": "Estacionamiento Norte",
    "toPlaceId": "place-uuid",
    "proposedPoints": "{\"points\": [{\"lat\": 19.3315, \"lng\": -99.1835, \"description\": \"Inicio\"}]}"
  }'
```

#### Admin: Aprobar Propuesta
```bash
curl -X PUT "http://localhost:8080/api/routes/proposals/proposal-uuid/approve?notes=Excelente propuesta" \
  -H "Authorization: Bearer admin-jwt-token"
```

### 🧭 Navegación

#### Iniciar Navegación
```bash
curl -X POST http://localhost:8080/api/navigation/start \
  -H "Authorization: Bearer jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "fromLatitude": 19.3310,
    "fromLongitude": -99.1830,
    "toPlaceId": "place-uuid",
    "routeId": "route-uuid"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Navegación iniciada exitosamente",
  "data": {
    "id": "navigation-uuid",
    "fromLat": 19.3310,
    "fromLng": -99.1830,
    "toPlace": {...},
    "routeUsed": {...},
    "navigationStartedAt": "2024-01-15 10:30:00"
  }
}
```

#### Completar Navegación
```bash
curl -X POST http://localhost:8080/api/navigation/complete \
  -H "Authorization: Bearer jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "navigationId": "navigation-uuid",
    "routeCompleted": true
  }'
```

### 🛡️ Administración

#### Crear Ruta Oficial
```bash
curl -X POST http://localhost:8080/api/routes \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ruta Principal a Biblioteca",
    "description": "Ruta oficial optimizada",
    "fromLatitude": 19.3310,
    "fromLongitude": -99.1830,
    "fromDescription": "Entrada Principal",
    "toPlaceId": "place-uuid",
    "totalDistance": 350,
    "estimatedTime": 5,
    "difficulty": "EASY",
    "isActive": true,
    "routePoints": [
      {
        "latitude": 19.3310,
        "longitude": -99.1830,
        "orderIndex": 0,
        "pointType": "START",
        "instruction": "Inicio en entrada principal",
        "distanceFromPrevious": 0
      },
      {
        "latitude": 19.3326,
        "longitude": -99.1844,
        "orderIndex": 1,
        "pointType": "END",
        "instruction": "Has llegado a la biblioteca",
        "distanceFromPrevious": 350
      }
    ]
  }'
```

#### Ver Analytics de Rutas
```bash
curl http://localhost:8080/api/routes/analytics \
  -H "Authorization: Bearer admin-jwt-token"
```

#### Promover Usuario a Admin
```bash
curl -X PUT http://localhost:8080/api/users/user-uuid/promote \
  -H "Authorization: Bearer admin-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "toUserType": "ADMIN",
    "reason": "Usuario con excelentes contribuciones"
  }'
```

## 🗄️ Base de Datos

### Tablas Principales

#### Tablas Existentes
- `users` - Usuarios del sistema (actualizada con contributionScore)
- `places` - Lugares del campus (actualizada con campos de rutas)
- `user_favorites` - Favoritos de usuarios
- `place_reports` - Reportes de lugares
- `navigation_history` - Historial de navegación (actualizada)

#### Nuevas Tablas del Sistema de Rutas
- `routes` - Rutas principales de navegación
- `route_points` - Puntos detallados de cada ruta
- `route_proposals` - Propuestas de rutas enviadas por usuarios
- `route_ratings` - Calificaciones de rutas (1-5 estrellas)
- `user_promotions` - Log de promociones de usuarios

### Tipos de Usuario

- `VISITOR` - Visitante (nuevo tipo por defecto)
- `STUDENT` - Estudiante
- `TEACHER` - Docente
- `ADMIN` - Administrador
- `STAFF` - Personal administrativo

### Tipos de Lugar

- `CLASSROOM` - Aula
- `LABORATORY` - Laboratorio
- `LIBRARY` - Biblioteca
- `CAFETERIA` - Cafetería
- `OFFICE` - Oficina
- `AUDITORIUM` - Auditorio
- `SERVICE` - Servicio
- `PARKING` - Estacionamiento (nuevo)
- `RECREATION` - Recreación (nuevo)
- `ENTRANCE` - Entrada (nuevo)

### Tipos de Punto de Ruta

- `START` - Punto de inicio
- `WAYPOINT` - Punto intermedio
- `TURN` - Punto de giro
- `LANDMARK` - Punto de referencia
- `END` - Punto final

### Dificultad de Rutas

- `EASY` - Fácil
- `MEDIUM` - Medio
- `HARD` - Difícil

## 👥 Usuarios de Prueba

El sistema carga automáticamente usuarios de prueba:

| Email | Password | Tipo | Descripción |
|-------|----------|------|-------------|
| `admin@campus.edu` | `admin123` | ADMIN | Administrador principal |
| `visitor@campus.edu` | `visitor123` | VISITOR | Visitante activo |
| `student@campus.edu` | `student123` | STUDENT | Estudiante de prueba |

## 🎯 Sistema de Contribuciones

### Puntuación por Actividades

- **Calificar ruta:** 5 puntos
- **Propuesta enviada:** 10 puntos
- **Propuesta aprobada:** 50 puntos
- **Completar navegación:** 2 puntos

### Promoción de Usuarios

- Los usuarios con alto puntaje de contribución pueden ser promovidos a ADMIN
- Solo administradores pueden promover usuarios
- Se mantiene un log completo de todas las promociones

## 🧪 Testing

### Ejecutar Tests
```bash
mvn test
```

### Flujo de Testing Completo

1. **Health checks:**
```bash
curl http://localhost:8080/api/routes/health
curl http://localhost:8080/api/auth/health
```

2. **Login y obtener token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "visitor@campus.edu", "password": "visitor123"}'
```

3. **Explorar rutas:**
```bash
curl http://localhost:8080/api/routes/destinations
curl http://localhost:8080/api/places
```

4. **Navegar y calificar:**
```bash
# Iniciar navegación
curl -X POST http://localhost:8080/api/navigation/start \
  -H "Authorization: Bearer token" \
  -H "Content-Type: application/json" \
  -d '{...}'

# Completar navegación
curl -X POST http://localhost:8080/api/navigation/complete \
  -H "Authorization: Bearer token" \
  -H "Content-Type: application/json" \
  -d '{...}'

# Calificar ruta
curl -X POST http://localhost:8080/api/routes/route-id/rating \
  -H "Authorization: Bearer token" \
  -H "Content-Type: application/json" \
  -d '{"rating": 5, "comment": "Excelente!"}'
```

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

# Logs específicos de rutas
docker-compose logs -f lugares-comunes-api | grep -i route
```

### Monitoring y Métricas

#### Endpoints de Health Check
- `/api/auth/health` - Estado del sistema de autenticación
- `/api/routes/health` - Estado del sistema de rutas
- `/api/routes/proposals/health` - Estado de propuestas
- `/api/navigation/health` - Estado de navegación
- `/api/users/health` - Estado de gestión de usuarios

#### Métricas Importantes
```bash
# Estadísticas de rutas (requiere token admin)
curl http://localhost:8080/api/routes/analytics \
  -H "Authorization: Bearer admin-token"

# Top contributors
curl http://localhost:8080/api/users/contributors \
  -H "Authorization: Bearer admin-token"
```
## 🛠 Troubleshooting

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

4. **Error de JWT o filtros:**
```bash
# Verificar que JwtAuthenticationFilter tenga @Component
# Verificar que las rutas públicas estén alineadas
```

5. **Error de validación de rutas:**
```bash
# Verificar coordenadas GPS válidas
# Verificar que los puntos tengan orden secuencial
# Verificar que tenga punto START y END
```

## 🔍 API Documentation

### Códigos de Estado HTTP

- **200 OK** - Operación exitosa
- **201 Created** - Recurso creado exitosamente
- **400 Bad Request** - Error en los datos enviados
- **401 Unauthorized** - Token JWT inválido o faltante
- **403 Forbidden** - Sin permisos para la operación
- **404 Not Found** - Recurso no encontrado
- **500 Internal Server Error** - Error interno del servidor

### Formato de Respuestas

Todas las respuestas siguen este formato estándar:

```json
{
  "success": true/false,
  "message": "Descripción del resultado",
  "data": {...}, // Solo en respuestas exitosas
  "count": 5, // Solo en listas
  "timestamp": 1642678800000, // Solo en errores
  "errorCode": "ERROR_CODE" // Solo en errores
}
```

*Sistema de Navegación con Rutas GPS - Version 2.0*