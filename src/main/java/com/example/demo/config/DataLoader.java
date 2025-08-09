package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.PlaceRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

    @Override
    public void run(String... args) {
        // Solo cargar datos en desarrollo y docker
        String[] activeProfiles = env.getActiveProfiles();
        boolean shouldLoadData = activeProfiles.length == 0 || // default profile
                Arrays.asList(activeProfiles).contains("dev") ||
                Arrays.asList(activeProfiles).contains("docker");

        if (shouldLoadData && placeRepository.count() == 0) {
            logger.info("🔄 Cargando datos de ejemplo...");
            loadSampleUsers();
            loadSamplePlaces();
            logger.info("✅ Datos de ejemplo cargados exitosamente!");
        } else if (placeRepository.count() > 0) {
            logger.info("ℹ️ La base de datos ya contiene datos, omitiendo carga inicial");
        }
    }

    private void loadSampleUsers() {
        // Usuario administrador
        if (!userRepository.existsByEmail("admin@puce.edu.ec")) {
            User admin = new User();
            admin.setEmail("admin@puce.edu.ec");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrador del Sistema");
            admin.setUserType(UserType.ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);
            logger.info("👤 Usuario administrador creado: admin@puce.edu.ec");
        }

        // Usuario estudiante de ejemplo
        if (!userRepository.existsByEmail("estudiante@puce.edu.ec")) {
            User student = new User();
            student.setEmail("estudiante@puce.edu.ec");
            student.setPassword(passwordEncoder.encode("estudiante123"));
            student.setFullName("Juan Carlos Estudiante");
            student.setStudentId("EST2024001");
            student.setUserType(UserType.STUDENT);
            student.setIsActive(true);
            userRepository.save(student);
            logger.info("🎓 Usuario estudiante creado: estudiante@puce.edu.ec");
        }

        // Usuario docente de ejemplo
        if (!userRepository.existsByEmail("docente@puce.edu.ec")) {
            User teacher = new User();
            teacher.setEmail("docente@puce.edu.ec");
            teacher.setPassword(passwordEncoder.encode("docente123"));
            teacher.setFullName("María Elena Docente");
            teacher.setUserType(UserType.TEACHER);
            teacher.setIsActive(true);
            userRepository.save(teacher);
            logger.info("👩‍🏫 Usuario docente creado: docente@puce.edu.ec");
        }
    }

    private void loadSamplePlaces() {
        // Aula A-101
        Place aula101 = new Place();
        aula101.setName("Aula A-101");
        aula101.setCategory("Aula");
        aula101.setDescription("Aula de clases magistrales con capacidad para 40 estudiantes. Equipada con proyector y sistema de audio.");
        aula101.setWhat3words("música.ejemplo.libertad");
        aula101.setLatitude(new BigDecimal("-0.210959"));
        aula101.setLongitude(new BigDecimal("-78.487259"));
        aula101.setIsAvailable(true);
        aula101.setPlaceType(PlaceType.CLASSROOM);
        aula101.setCapacity(40);
        aula101.setSchedule("Lunes a Viernes 7:00 - 22:00");
        aula101.setBuildingName("Edificio Principal");
        aula101.setFloorNumber(1);
        aula101.setRoomCode("A-101");
        aula101.setEquipment(Set.of("Proyector", "Sistema de audio", "Pizarra digital", "Aire acondicionado"));
        aula101.setAccessibilityFeatures(Set.of("Acceso para sillas de ruedas", "Asientos preferenciales"));
        placeRepository.save(aula101);

        // Laboratorio de Informática
        Place lab1 = new Place();
        lab1.setName("Laboratorio de Informática 1");
        lab1.setCategory("Laboratorio");
        lab1.setDescription("Laboratorio con 30 computadoras actualizadas, ideal para clases de programación y diseño.");
        lab1.setWhat3words("código.digital.futuro");
        lab1.setLatitude(new BigDecimal("-0.211100"));
        lab1.setLongitude(new BigDecimal("-78.487000"));
        lab1.setIsAvailable(false);
        lab1.setPlaceType(PlaceType.LABORATORY);
        lab1.setCapacity(30);
        lab1.setSchedule("Lunes a Viernes 8:00 - 20:00");
        lab1.setBuildingName("Edificio de Tecnología");
        lab1.setFloorNumber(1);
        lab1.setRoomCode("LAB-101");
        lab1.setEquipment(Set.of("30 Computadoras", "Proyector", "Software especializado", "Red de alta velocidad"));
        lab1.setAccessibilityFeatures(Set.of("Acceso para sillas de ruedas", "Mesas ajustables"));
        placeRepository.save(lab1);

        // Biblioteca Central
        Place biblioteca = new Place();
        biblioteca.setName("Biblioteca Central");
        biblioteca.setCategory("Biblioteca");
        biblioteca.setDescription("Biblioteca principal del campus con más de 50,000 libros y salas de estudio.");
        biblioteca.setWhat3words("silencio.libros.conocimiento");
        biblioteca.setLatitude(new BigDecimal("-0.210800"));
        biblioteca.setLongitude(new BigDecimal("-78.487500"));
        biblioteca.setIsAvailable(true);
        biblioteca.setPlaceType(PlaceType.LIBRARY);
        biblioteca.setCapacity(150);
        biblioteca.setSchedule("Lunes a Viernes 6:00 - 23:00, Sábados 8:00 - 18:00");
        biblioteca.setBuildingName("Biblioteca Central");
        biblioteca.setFloorNumber(1);
        biblioteca.setRoomCode("BIB-001");
        biblioteca.setEquipment(Set.of("Catálogo digital", "Salas de estudio", "WiFi gratuito", "Computadoras públicas"));
        biblioteca.setAccessibilityFeatures(Set.of("Acceso para sillas de ruedas", "Elevador", "Baños adaptados"));
        placeRepository.save(biblioteca);

        // Cafetería Central
        Place cafeteria = new Place();
        cafeteria.setName("Cafetería Central");
        cafeteria.setCategory("Cafetería");
        cafeteria.setDescription("Cafetería principal del campus con variedad de comidas y bebidas.");
        cafeteria.setWhat3words("comida.campus.sabor");
        cafeteria.setLatitude(new BigDecimal("-0.210700"));
        cafeteria.setLongitude(new BigDecimal("-78.487300"));
        cafeteria.setIsAvailable(true);
        cafeteria.setPlaceType(PlaceType.CAFETERIA);
        cafeteria.setCapacity(80);
        cafeteria.setSchedule("Lunes a Viernes 7:00 - 18:00");
        cafeteria.setBuildingName("Edificio Central");
        cafeteria.setFloorNumber(1);
        cafeteria.setRoomCode("CAF-001");
        cafeteria.setEquipment(Set.of("Microondas", "Neveras", "Mesas y sillas", "TV"));
        cafeteria.setAccessibilityFeatures(Set.of("Acceso para sillas de ruedas", "Mesas adaptadas"));
        placeRepository.save(cafeteria);

        // Auditorio Magna
        Place auditorio = new Place();
        auditorio.setName("Auditorio Magna");
        auditorio.setCategory("Auditorio");
        auditorio.setDescription("Auditorio principal para eventos y conferencias con capacidad para 200 personas.");
        auditorio.setWhat3words("evento.grande.escenario");
        auditorio.setLatitude(new BigDecimal("-0.210600"));
        auditorio.setLongitude(new BigDecimal("-78.487400"));
        auditorio.setIsAvailable(true);
        auditorio.setPlaceType(PlaceType.AUDITORIUM);
        auditorio.setCapacity(200);
        auditorio.setSchedule("Previa reservación");
        auditorio.setBuildingName("Edificio Cultural");
        auditorio.setFloorNumber(1);
        auditorio.setRoomCode("AUD-001");
        auditorio.setEquipment(Set.of("Sistema de sonido profesional", "Proyectores duales", "Escenario", "Iluminación"));
        auditorio.setAccessibilityFeatures(Set.of("Acceso para sillas de ruedas", "Asientos reservados", "Rampas"));
        placeRepository.save(auditorio);

        // Oficina de Admisiones
        Place admisiones = new Place();
        admisiones.setName("Oficina de Admisiones");
        admisiones.setCategory("Oficina");
        admisiones.setDescription("Oficina principal de admisiones y trámites estudiantiles.");
        admisiones.setWhat3words("trámite.estudiante.ayuda");
        admisiones.setLatitude(new BigDecimal("-0.210500"));
        admisiones.setLongitude(new BigDecimal("-78.487200"));
        admisiones.setIsAvailable(true);
        admisiones.setPlaceType(PlaceType.OFFICE);
        admisiones.setCapacity(10);
        admisiones.setSchedule("Lunes a Viernes 8:00 - 17:00");
        admisiones.setBuildingName("Edificio Administrativo");
        admisiones.setFloorNumber(2);
        admisiones.setRoomCode("ADM-201");
        admisiones.setEquipment(Set.of("Ventanillas de atención", "Sistema de turnos", "Computadoras"));
        admisiones.setAccessibilityFeatures(Set.of("Acceso para sillas de ruedas", "Ventanilla baja"));
        placeRepository.save(admisiones);

        // Laboratorio de Química
        Place labQuimica = new Place();
        labQuimica.setName("Laboratorio de Química");
        labQuimica.setCategory("Laboratorio");
        labQuimica.setDescription("Laboratorio especializado en química con equipos de seguridad completos.");
        labQuimica.setWhat3words("química.experimento.ciencia");
        labQuimica.setLatitude(new BigDecimal("-0.211200"));
        labQuimica.setLongitude(new BigDecimal("-78.486900"));
        labQuimica.setIsAvailable(true);
        labQuimica.setPlaceType(PlaceType.LABORATORY);
        labQuimica.setCapacity(25);
        labQuimica.setSchedule("Lunes a Viernes 8:00 - 18:00");
        labQuimica.setBuildingName("Edificio de Ciencias");
        labQuimica.setFloorNumber(2);
        labQuimica.setRoomCode("LAB-QUI-201");
        labQuimica.setEquipment(Set.of("Mesas de trabajo", "Campanas extractoras", "Equipos de medición", "Kit de emergencia"));
        labQuimica.setAccessibilityFeatures(Set.of("Rutas de evacuación", "Duchas de emergencia"));
        placeRepository.save(labQuimica);

        // Cancha de Deportes
        Place cancha = new Place();
        cancha.setName("Cancha Deportiva");
        cancha.setCategory("Deportes");
        cancha.setDescription("Cancha multiuso para fútbol, básquet y voleibol.");
        cancha.setWhat3words("deporte.juego.ejercicio");
        cancha.setLatitude(new BigDecimal("-0.210400"));
        cancha.setLongitude(new BigDecimal("-78.487600"));
        cancha.setIsAvailable(true);
        cancha.setPlaceType(PlaceType.SERVICE);
        cancha.setCapacity(50);
        cancha.setSchedule("Lunes a Viernes 6:00 - 20:00");
        cancha.setBuildingName("Complejo Deportivo");
        cancha.setFloorNumber(1);
        cancha.setRoomCode("DEP-001");
        cancha.setEquipment(Set.of("Arcos de fútbol", "Canastas de básquet", "Red de voleibol", "Vestuarios"));
        cancha.setAccessibilityFeatures(Set.of("Graderías adaptadas", "Acceso nivel"));
        placeRepository.save(cancha);

        logger.info("📍 {} lugares de ejemplo creados", placeRepository.count());
    }
}
