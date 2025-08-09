-- Inicialización de la base de datos Lugares Comunes
-- Este archivo se ejecuta automáticamente cuando se crea el contenedor MySQL

-- Configuración de la base de datos
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "-05:00"; -- Zona horaria Ecuador

-- Usar la base de datos
USE lugares_comunes;

-- Crear usuario adicional para la aplicación si no existe
CREATE USER IF NOT EXISTS 'app_user'@'%' IDENTIFIED BY 'app_password';
GRANT ALL PRIVILEGES ON lugares_comunes.* TO 'app_user'@'%';
FLUSH PRIVILEGES;

-- Las tablas serán creadas automáticamente por JPA/Hibernate
-- Este script solo incluye datos de ejemplo

-- Datos de ejemplo para lugares (se insertan después de que Hibernate cree las tablas)
-- Nota: Estos INSERT se ejecutarán solo si las tablas existen

-- Insertar datos de ejemplo para el campus PUCE
DELIMITER $$

CREATE PROCEDURE InsertSampleData()
BEGIN
    DECLARE table_count INT DEFAULT 0;
    
    -- Verificar si la tabla places existe
    SELECT COUNT(*) INTO table_count 
    FROM information_schema.tables 
    WHERE table_schema = 'lugares_comunes' AND table_name = 'places';
    
    -- Si la tabla existe, insertar datos de ejemplo
    IF table_count > 0 THEN
        
        -- Insertar lugares de ejemplo
        INSERT IGNORE INTO places (
            id, name, category, description, what3words, latitude, longitude, 
            is_available, place_type, capacity, schedule, building_name, 
            floor_number, room_code, created_at, updated_at
        ) VALUES 
        (
            UUID(), 
            'Aula A-101', 
            'Aula', 
            'Aula de clases magistrales con capacidad para 40 estudiantes. Equipada con proyector y sistema de audio.',
            'música.ejemplo.libertad',
            -0.210959,
            -78.487259,
            true,
            'CLASSROOM',
            40,
            'Lunes a Viernes 7:00 - 22:00',
            'Edificio Principal',
            1,
            'A-101',
            NOW(),
            NOW()
        ),
        (
            UUID(), 
            'Laboratorio de Informática 1', 
            'Laboratorio', 
            'Laboratorio con 30 computadoras actualizadas, ideal para clases de programación y diseño.',
            'código.digital.futuro',
            -0.211100,
            -78.487000,
            false,
            'LABORATORY',
            30,
            'Lunes a Viernes 8:00 - 20:00',
            'Edificio de Tecnología',
            1,
            'LAB-101',
            NOW(),
            NOW()
        ),
        (
            UUID(), 
            'Biblioteca Central', 
            'Biblioteca', 
            'Biblioteca principal del campus con más de 50,000 libros y salas de estudio.',
            'silencio.libros.conocimiento',
            -0.210800,
            -78.487500,
            true,
            'LIBRARY',
            150,
            'Lunes a Viernes 6:00 - 23:00, Sábados 8:00 - 18:00',
            'Biblioteca Central',
            1,
            'BIB-001',
            NOW(),
            NOW()
        ),
        (
            UUID(), 
            'Cafetería Central', 
            'Cafetería', 
            'Cafetería principal del campus con variedad de comidas y bebidas.',
            'comida.campus.sabor',
            -0.210700,
            -78.487300,
            true,
            'CAFETERIA',
            80,
            'Lunes a Viernes 7:00 - 18:00',
            'Edificio Central',
            1,
            'CAF-001',
            NOW(),
            NOW()
        ),
        (
            UUID(), 
            'Auditorio Magna', 
            'Auditorio', 
            'Auditorio principal para eventos y conferencias con capacidad para 200 personas.',
            'evento.grande.escenario',
            -0.210600,
            -78.487400,
            true,
            'AUDITORIUM',
            200,
            'Previa reservación',
            'Edificio Cultural',
            1,
            'AUD-001',
            NOW(),
            NOW()
        ),
        (
            UUID(), 
            'Oficina de Admisiones', 
            'Oficina', 
            'Oficina principal de admisiones y trámites estudiantiles.',
            'trámite.estudiante.ayuda',
            -0.210500,
            -78.487200,
            true,
            'OFFICE',
            10,
            'Lunes a Viernes 8:00 - 17:00',
            'Edificio Administrativo',
            2,
            'ADM-201',
            NOW(),
            NOW()
        );
        
        -- Crear usuario administrador de ejemplo
        INSERT IGNORE INTO users (
            id, email, password, full_name, user_type, is_active, created_at, updated_at
        ) VALUES (
            UUID(),
            'admin@puce.edu.ec',
            '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',  -- password: password
            'Administrador del Sistema',
            'ADMIN',
            true,
            NOW(),
            NOW()
        );
        
        -- Crear usuario estudiante de ejemplo
        INSERT IGNORE INTO users (
            id, email, password, full_name, student_id, user_type, is_active, created_at, updated_at
        ) VALUES (
            UUID(),
            'estudiante@puce.edu.ec',
            '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',  -- password: password
            'Estudiante de Prueba',
            'EST001',
            'STUDENT',
            true,
            NOW(),
            NOW()
        );

    END IF;
END$$

DELIMITER ;

-- Ejecutar el procedimiento (se ejecutará cuando Hibernate haya creado las tablas)
-- Nota: Como Hibernate crea las tablas después, esto podría no funcionar inmediatamente
-- Se recomienda ejecutar los datos de ejemplo manualmente después del primer startup

SET FOREIGN_KEY_CHECKS = 1;

-- Mensaje de finalización
SELECT 'Base de datos inicializada correctamente para Lugares Comunes' as message;