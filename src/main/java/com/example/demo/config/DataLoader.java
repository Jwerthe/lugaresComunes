package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private RouteProposalRepository proposalRepository;

    @Autowired
    private RouteRatingRepository ratingRepository;

    @Autowired
    private NavigationHistoryRepository navigationHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (shouldLoadData()) {
            logger.info("🚀 Iniciando carga de datos de ejemplo para sistema de rutas...");
            
            // Crear usuarios de ejemplo
            createSampleUsers();
            
            // Crear lugares de ejemplo (si no existen)
            createSamplePlaces();
            
            // Crear rutas de ejemplo
            createSampleRoutes();
            
            // Crear propuestas de ejemplo
            createSampleProposals();
            
            // Crear calificaciones de ejemplo
            createSampleRatings();
            
            // Crear historial de navegación de ejemplo
            createSampleNavigationHistory();
            
            logger.info("✅ Datos de ejemplo cargados exitosamente");
        }
    }

    private boolean shouldLoadData() {
        // Solo cargar datos si no hay rutas en la base de datos
        return routeRepository.count() == 0;
    }

    private void createSampleUsers() {
        logger.info("👥 Creando usuarios de ejemplo...");

        // Admin principal
        if (!userRepository.existsByEmail("admin@campus.edu")) {
            User admin = new User();
            admin.setEmail("admin@campus.edu");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrador del Campus");
            admin.setUserType(UserType.ADMIN);
            admin.setContributionScore(100);
            userRepository.save(admin);
            logger.info("✅ Admin creado: admin@campus.edu");
        }

        // Usuario visitante activo
        if (!userRepository.existsByEmail("visitor@campus.edu")) {
            User visitor = new User();
            visitor.setEmail("visitor@campus.edu");
            visitor.setPassword(passwordEncoder.encode("visitor123"));
            visitor.setFullName("Visitante Activo");
            visitor.setUserType(UserType.VISITOR);
            visitor.setContributionScore(45);
            userRepository.save(visitor);
            logger.info("✅ Visitante creado: visitor@campus.edu");
        }

        // Estudiante
        if (!userRepository.existsByEmail("student@campus.edu")) {
            User student = new User();
            student.setEmail("student@campus.edu");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setFullName("Estudiante de Prueba");
            student.setStudentId("20240001");
            student.setUserType(UserType.STUDENT);
            student.setContributionScore(25);
            userRepository.save(student);
            logger.info("✅ Estudiante creado: student@campus.edu");
        }
    }

    private void createSamplePlaces() {
        logger.info("📍 Verificando lugares de ejemplo...");

        if (placeRepository.count() == 0) {
            logger.info("🏗️ Creando lugares de ejemplo...");

            // Biblioteca Central
            Place library = createPlace(
                "Biblioteca Central",
                "Biblioteca",
                "Biblioteca principal del campus universitario",
                PlaceType.LIBRARY,
                new BigDecimal("19.3326"), new BigDecimal("-99.1844"),
                "what3words.library"
            );
            library.setSchedule("Lunes a Viernes: 7:00 AM - 10:00 PM, Sábados: 8:00 AM - 6:00 PM");
            library.setCapacity(500);
            library.setBuildingName("Edificio Central");
            library.setFloorNumber(2);
            placeRepository.save(library);

            // Cafetería Principal
            Place cafeteria = createPlace(
                "Cafetería Principal",
                "Comidas",
                "Cafetería principal con variedad de opciones de comida",
                PlaceType.CAFETERIA,
                new BigDecimal("19.3320"), new BigDecimal("-99.1840"),
                "what3words.cafeteria"
            );
            cafeteria.setSchedule("Lunes a Viernes: 7:00 AM - 8:00 PM");
            cafeteria.setCapacity(200);
            cafeteria.setBuildingName("Edificio de Servicios");
            cafeteria.setFloorNumber(1);
            placeRepository.save(cafeteria);

            // Laboratorio de Cómputo
            Place labComputo = createPlace(
                "Laboratorio de Cómputo A",
                "Laboratorio",
                "Laboratorio equipado con computadoras de última generación",
                PlaceType.LABORATORY,
                new BigDecimal("19.3330"), new BigDecimal("-99.1850"),
                "what3words.lab.computo"
            );
            labComputo.setCapacity(40);
            labComputo.setEquipment(new HashSet<>(Arrays.asList("Computadoras", "Proyector", "Aire acondicionado")));
            labComputo.setBuildingName("Edificio de Ingeniería");
            labComputo.setFloorNumber(3);
            placeRepository.save(labComputo);

            // Auditorio Principal
            Place auditorio = createPlace(
                "Auditorio Principal",
                "Eventos",
                "Auditorio principal para eventos y conferencias",
                PlaceType.AUDITORIUM,
                new BigDecimal("19.3315"), new BigDecimal("-99.1835"),
                "what3words.auditorio"
            );
            auditorio.setCapacity(800);
            auditorio.setEquipment(new HashSet<>(Arrays.asList("Sistema de sonido", "Proyector 4K", "Escenario")));
            auditorio.setAccessibilityFeatures(new HashSet<>(Arrays.asList("Rampa de acceso", "Asientos preferenciales")));
            placeRepository.save(auditorio);

            // Entrada Principal
            Place entrance = createPlace(
                "Entrada Principal",
                "Acceso",
                "Entrada principal del campus universitario",
                PlaceType.ENTRANCE,
                new BigDecimal("19.3310"), new BigDecimal("-99.1830"),
                "what3words.entrance"
            );
            entrance.setBuildingName("Portería Principal");
            placeRepository.save(entrance);

            logger.info("✅ {} lugares de ejemplo creados", placeRepository.count());
        }
    }

    private Place createPlace(String name, String category, String description, PlaceType type, 
                             BigDecimal lat, BigDecimal lng, String what3words) {
        Place place = new Place();
        place.setName(name);
        place.setCategory(category);
        place.setDescription(description);
        place.setPlaceType(type);
        place.setLatitude(lat);
        place.setLongitude(lng);
        place.setWhat3words(what3words);
        place.setIsAvailable(true);
        place.setIsRouteDestination(true);
        return place;
    }

    private void createSampleRoutes() {
        logger.info("🗺️ Creando rutas de ejemplo...");

        User admin = userRepository.findByEmail("admin@campus.edu").orElse(null);
        if (admin == null) return;

        List<Place> places = placeRepository.findAll();
        if (places.size() < 2) return;

        Place entrance = places.stream()
                .filter(p -> p.getName().contains("Entrada"))
                .findFirst().orElse(places.get(0));
        
        Place library = places.stream()
                .filter(p -> p.getName().contains("Biblioteca"))
                .findFirst().orElse(places.get(1));

        Place cafeteria = places.stream()
                .filter(p -> p.getName().contains("Cafetería"))
                .findFirst().orElse(places.get(2));

        // Ruta 1: Entrada Principal → Biblioteca
        Route routeToLibrary = new Route();
        routeToLibrary.setName("Entrada Principal → Biblioteca Central");
        routeToLibrary.setDescription("Ruta directa desde la entrada principal hasta la biblioteca central");
        routeToLibrary.setFromLatitude(entrance.getLatitude());
        routeToLibrary.setFromLongitude(entrance.getLongitude());
        routeToLibrary.setFromDescription("Entrada Principal del Campus");
        routeToLibrary.setToPlace(library);
        routeToLibrary.setTotalDistance(350);
        routeToLibrary.setEstimatedTime(5);
        routeToLibrary.setDifficulty(RouteDifficulty.EASY);
        routeToLibrary.setCreatedBy(admin);
        routeToLibrary.setAverageRating(4.2);
        routeToLibrary.setTotalRatings(8);
        routeToLibrary.setTimesUsed(15);
        routeRepository.save(routeToLibrary);

        // Crear puntos para ruta a biblioteca
        createRoutePoint(routeToLibrary, 0, RoutePointType.START, entrance.getLatitude(), entrance.getLongitude(), 
                        "Inicia en la entrada principal del campus", 0);
        createRoutePoint(routeToLibrary, 1, RoutePointType.WAYPOINT, 
                        new BigDecimal("19.3318"), new BigDecimal("-99.1838"), 
                        "Camina derecho por el sendero principal", 120);
        createRoutePoint(routeToLibrary, 2, RoutePointType.TURN, 
                        new BigDecimal("19.3322"), new BigDecimal("-99.1842"), 
                        "Gira a la izquierda hacia el edificio central", 80);
        createRoutePoint(routeToLibrary, 3, RoutePointType.LANDMARK, 
                        new BigDecimal("19.3324"), new BigDecimal("-99.1843"), 
                        "Pasa por la fuente central", 50);
        createRoutePoint(routeToLibrary, 4, RoutePointType.END, library.getLatitude(), library.getLongitude(), 
                        "Has llegado a la Biblioteca Central", 100);

        // Ruta 2: Entrada Principal → Cafetería
        Route routeToCafeteria = new Route();
        routeToCafeteria.setName("Entrada Principal → Cafetería Principal");
        routeToCafeteria.setDescription("Ruta rápida a la cafetería para una comida rápida");
        routeToCafeteria.setFromLatitude(entrance.getLatitude());
        routeToCafeteria.setFromLongitude(entrance.getLongitude());
        routeToCafeteria.setFromDescription("Entrada Principal del Campus");
        routeToCafeteria.setToPlace(cafeteria);
        routeToCafeteria.setTotalDistance(280);
        routeToCafeteria.setEstimatedTime(4);
        routeToCafeteria.setDifficulty(RouteDifficulty.EASY);
        routeToCafeteria.setCreatedBy(admin);
        routeToCafeteria.setAverageRating(4.5);
        routeToCafeteria.setTotalRatings(12);
        routeToCafeteria.setTimesUsed(25);
        routeRepository.save(routeToCafeteria);

        // Crear puntos para ruta a cafetería
        createRoutePoint(routeToCafeteria, 0, RoutePointType.START, entrance.getLatitude(), entrance.getLongitude(), 
                        "Inicia en la entrada principal", 0);
        createRoutePoint(routeToCafeteria, 1, RoutePointType.TURN, 
                        new BigDecimal("19.3315"), new BigDecimal("-99.1835"), 
                        "Gira a la derecha hacia el edificio de servicios", 100);
        createRoutePoint(routeToCafeteria, 2, RoutePointType.LANDMARK, 
                        new BigDecimal("19.3318"), new BigDecimal("-99.1838"), 
                        "Pasa por el jardín de estudiantes", 120);
        createRoutePoint(routeToCafeteria, 3, RoutePointType.END, cafeteria.getLatitude(), cafeteria.getLongitude(), 
                        "Has llegado a la Cafetería Principal", 60);

        // Actualizar contadores de rutas en los lugares
        library.incrementRouteCount();
        cafeteria.incrementRouteCount();
        placeRepository.saveAll(Arrays.asList(library, cafeteria));

        logger.info("✅ {} rutas de ejemplo creadas", routeRepository.count());
    }

    private void createRoutePoint(Route route, int order, RoutePointType type, BigDecimal lat, BigDecimal lng, 
                                 String instruction, int distance) {
        RoutePoint point = new RoutePoint();
        point.setRoute(route);
        point.setOrderIndex(order);
        point.setPointType(type);
        point.setLatitude(lat);
        point.setLongitude(lng);
        point.setInstruction(instruction);
        point.setDistanceFromPrevious(distance);
        routePointRepository.save(point);
    }

    private void createSampleProposals() {
        logger.info("💡 Creando propuestas de ejemplo...");

        User visitor = userRepository.findByEmail("visitor@campus.edu").orElse(null);
        User student = userRepository.findByEmail("student@campus.edu").orElse(null);
        
        if (visitor == null || student == null) return;

        List<Place> places = placeRepository.findAll();
        if (places.isEmpty()) return;

        Place destination = places.stream()
                .filter(p -> p.getName().contains("Laboratorio"))
                .findFirst().orElse(places.get(0));

        // Propuesta pendiente del visitante
        RouteProposal proposal1 = new RouteProposal();
        proposal1.setProposedBy(visitor);
        proposal1.setTitle("Ruta alternativa al Laboratorio de Cómputo");
        proposal1.setDescription("Propongo una ruta más rápida que evita las escaleras principales y usa el elevador");
        proposal1.setFromLatitude(new BigDecimal("19.3312"));
        proposal1.setFromLongitude(new BigDecimal("19.3312"));
        proposal1.setFromDescription("Entrada lateral del edificio");
        proposal1.setToPlace(destination);
        proposal1.setProposedPoints("{\"points\": [{\"lat\": 19.3312, \"lng\": -99.1832, \"description\": \"Punto de inicio\"}, {\"lat\": 19.3328, \"lng\": -99.1848, \"description\": \"Usar elevador\"}]}");
        proposal1.setStatus(ProposalStatus.PENDING);
        proposalRepository.save(proposal1);

        // Propuesta aprobada del estudiante
        RouteProposal proposal2 = new RouteProposal();
        proposal2.setProposedBy(student);
        proposal2.setTitle("Ruta accesible a la Cafetería");
        proposal2.setDescription("Ruta sin escalones para personas con movilidad reducida");
        proposal2.setFromLatitude(new BigDecimal("19.3325"));
        proposal2.setFromLongitude(new BigDecimal("19.3325"));
        proposal2.setFromDescription("Estacionamiento sur");
        proposal2.setToPlace(placeRepository.findAll().stream()
                .filter(p -> p.getName().contains("Cafetería"))
                .findFirst().orElse(destination));
        proposal2.setStatus(ProposalStatus.APPROVED);
        proposal2.setReviewedBy(userRepository.findByEmail("admin@campus.edu").orElse(null));
        proposal2.setReviewedAt(LocalDateTime.now().minusDays(2));
        proposal2.setAdminNotes("Excelente propuesta. Ruta aprobada e implementada.");
        proposalRepository.save(proposal2);

        logger.info("✅ {} propuestas de ejemplo creadas", proposalRepository.count());
    }

    private void createSampleRatings() {
        logger.info("⭐ Creando calificaciones de ejemplo...");

        User visitor = userRepository.findByEmail("visitor@campus.edu").orElse(null);
        User student = userRepository.findByEmail("student@campus.edu").orElse(null);
        
        List<Route> routes = routeRepository.findAll();
        
        if (visitor == null || student == null || routes.isEmpty()) return;

        Route route1 = routes.get(0);

        // Calificación del visitante
        RouteRating rating1 = new RouteRating();
        rating1.setRoute(route1);
        rating1.setUser(visitor);
        rating1.setRating(4);
        rating1.setComment("Ruta clara y bien señalizada. Me ayudó mucho en mi primera visita.");
        ratingRepository.save(rating1);

        // Calificación del estudiante
        RouteRating rating2 = new RouteRating();
        rating2.setRoute(route1);
        rating2.setUser(student);
        rating2.setRating(5);
        rating2.setComment("Perfecta para llegar rápido a la biblioteca. La uso todos los días.");
        ratingRepository.save(rating2);

        if (routes.size() > 1) {
            Route route2 = routes.get(1);
            
            RouteRating rating3 = new RouteRating();
            rating3.setRoute(route2);
            rating3.setUser(visitor);
            rating3.setRating(4);
            rating3.setComment("Muy conveniente para el almuerzo.");
            ratingRepository.save(rating3);
        }

        logger.info("✅ {} calificaciones de ejemplo creadas", ratingRepository.count());
    }

    private void createSampleNavigationHistory() {
        logger.info("🧭 Creando historial de navegación de ejemplo...");

        User visitor = userRepository.findByEmail("visitor@campus.edu").orElse(null);
        User student = userRepository.findByEmail("student@campus.edu").orElse(null);
        
        List<Route> routes = routeRepository.findAll();
        List<Place> places = placeRepository.findAll();
        
        if (visitor == null || student == null || routes.isEmpty() || places.isEmpty()) return;

        // Navegación completada del visitante usando ruta
        NavigationHistory nav1 = new NavigationHistory();
        nav1.setUser(visitor);
        nav1.setFromLat(new BigDecimal("19.3310"));
        nav1.setFromLng(new BigDecimal("-99.1830"));
        nav1.setToPlace(places.get(0));
        nav1.setRouteUsed(routes.get(0));
        nav1.setNavigationStartedAt(LocalDateTime.now().minusHours(2));
        nav1.setNavigationCompletedAt(LocalDateTime.now().minusHours(2).plusMinutes(6));
        nav1.setDurationSeconds(360); // 6 minutos
        nav1.setRouteCompleted(true);
        navigationHistoryRepository.save(nav1);

        // Navegación del estudiante sin ruta específica
        NavigationHistory nav2 = new NavigationHistory();
        nav2.setUser(student);
        nav2.setFromLat(new BigDecimal("19.3315"));
        nav2.setFromLng(new BigDecimal("-99.1835"));
        nav2.setToPlace(places.size() > 1 ? places.get(1) : places.get(0));
        nav2.setNavigationStartedAt(LocalDateTime.now().minusDays(1));
        nav2.setNavigationCompletedAt(LocalDateTime.now().minusDays(1).plusMinutes(8));
        nav2.setDurationSeconds(480); // 8 minutos
        navigationHistoryRepository.save(nav2);

        logger.info("✅ {} registros de navegación de ejemplo creados", navigationHistoryRepository.count());
    }
}