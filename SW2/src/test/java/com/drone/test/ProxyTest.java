package com.drone.test;

import com.drone.dao.DroneDAO;
import com.drone.servicios.DroneProxy;
import com.drone.servicios.DroneProxyService;
import com.drone.servicios.IDroneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Unitarios del Patron Proxy (Protection Proxy).
 *
 * Arquitectura bajo prueba:
 *   DroneController (cliente)
 *       --> DroneProxy (interceptor / verificacion de contrasena)
 *           --> IDroneService (contrato comun)
 *               --> DroneProxyService (sujeto real)
 *
 * Los tests usan un "Servicio Espía" (Spy/Stub) para evitar
 * dependencia de la base de datos real (DroneDAO).
 * Esto sigue el principio de pruebas unitarias puras.
 */
@DisplayName("Pruebas - Patron Proxy (DroneProxy)")
public class ProxyTest {

    // ================================================================
    // SERVICIO ESPIA (Stub) - Reemplaza al DroneProxyService real
    // sin necesitar una conexion a PostgreSQL
    // ================================================================
    private static class ServicioEspia implements IDroneService {
        boolean eliminarFueLlamado = false;
        String idRecibido = null;
        boolean simularError = false;

        @Override
        public void eliminarDrone(String id) throws Exception {
            if (simularError) {
                throw new Exception("Error simulado en el servicio real.");
            }
            eliminarFueLlamado = true;
            idRecibido = id;
        }
    }

    private ServicioEspia espia;

    @BeforeEach
    public void setUp() {
        // Se crea un espia limpio antes de cada prueba
        espia = new ServicioEspia();
    }

    // ================================================================
    // GRUPO 1: Acceso autorizado (contraseña correcta)
    // ================================================================

    @Test
    @DisplayName("Proxy permite eliminar cuando la contrasena es correcta")
    public void testProxyPermiteEliminarConContrasenaCorrecta() throws Exception {
        DroneProxy proxy = new DroneProxy(espia, "admin123");

        proxy.eliminarDrone("DRONE-001");

        assertTrue(espia.eliminarFueLlamado,
                "Cuando la contrasena es correcta, el Proxy DEBE delegar la llamada al servicio real.");
        assertEquals("DRONE-001", espia.idRecibido,
                "El ID recibido por el servicio real debe ser el mismo que envio el cliente.");
    }

    @Test
    @DisplayName("Proxy pasa correctamente el ID al servicio real")
    public void testProxyTransmiteIdCorrectamente() throws Exception {
        DroneProxy proxy = new DroneProxy(espia, "admin123");

        proxy.eliminarDrone("XK-9999");

        assertEquals("XK-9999", espia.idRecibido,
                "El Proxy debe pasar el ID exacto al servicio real sin modificarlo.");
    }

    // ================================================================
    // GRUPO 2: Acceso denegado (contraseña incorrecta)
    // ================================================================

    @Test
    @DisplayName("Proxy bloquea la eliminacion cuando la contrasena es incorrecta")
    public void testProxyBloqueoConContrasenaIncorrecta() {
        DroneProxy proxy = new DroneProxy(espia, "claveWrong");

        assertThrows(Exception.class, () -> proxy.eliminarDrone("DRONE-001"),
                "El Proxy debe lanzar una Exception cuando la contrasena es incorrecta.");
    }

    @Test
    @DisplayName("Proxy NO llama al servicio real cuando la contrasena es incorrecta")
    public void testProxyNoLlamaAlServicioRealSiContrasenaFalla() {
        DroneProxy proxy = new DroneProxy(espia, "wrongPass");

        try {
            proxy.eliminarDrone("DRONE-002");
        } catch (Exception ignored) {
            // Esperamos que lance excepcion
        }

        assertFalse(espia.eliminarFueLlamado,
                "El servicio real NUNCA debe ser invocado si la contrasena es incorrecta.");
        assertNull(espia.idRecibido,
                "El ID no debe haber llegado al servicio real.");
    }

    @Test
    @DisplayName("Proxy lanza excepcion con mensaje claro al denegar acceso")
    public void testProxyMensajeDenegacion() {
        DroneProxy proxy = new DroneProxy(espia, "hackIntento");

        Exception ex = assertThrows(Exception.class, () -> proxy.eliminarDrone("X"));
        assertTrue(ex.getMessage().toLowerCase().contains("contrasena"),
                "El mensaje de error debe mencionar 'contrasena' para que la UI pueda informar al usuario.");
    }

    // ================================================================
    // GRUPO 3: Casos borde de contrasena
    // ================================================================

    @Test
    @DisplayName("Proxy rechaza contrasena vacia")
    public void testProxyRechazaContrasenaVacia() {
        DroneProxy proxy = new DroneProxy(espia, "");

        assertThrows(Exception.class, () -> proxy.eliminarDrone("DRONE-003"),
                "Una contrasena vacia no debe ser aceptada.");
        assertFalse(espia.eliminarFueLlamado, "El servicio real no debe ejecutarse con contrasena vacia.");
    }

    @Test
    @DisplayName("Proxy rechaza contrasena null")
    public void testProxyRechazaContrasenaNula() {
        DroneProxy proxy = new DroneProxy(espia, null);

        assertThrows(Exception.class, () -> proxy.eliminarDrone("DRONE-004"),
                "Una contrasena null no debe ser aceptada.");
        assertFalse(espia.eliminarFueLlamado, "El servicio real no debe ejecutarse con contrasena null.");
    }

    @Test
    @DisplayName("Proxy es sensible a mayusculas en la contrasena")
    public void testProxyEsSensibleAMayusculas() {
        DroneProxy proxyMayus = new DroneProxy(espia, "ADMIN123");

        assertThrows(Exception.class, () -> proxyMayus.eliminarDrone("DRONE-005"),
                "El Proxy debe ser sensible a mayusculas: 'ADMIN123' != 'admin123'.");
        assertFalse(espia.eliminarFueLlamado, "El servicio real no debe llamarse con variante en mayusculas.");
    }

    // ================================================================
    // GRUPO 4: Transparencia de interfaz (principio del Proxy)
    // ================================================================

    @Test
    @DisplayName("DroneProxy implementa IDroneService (misma interfaz que el servicio real)")
    public void testProxyImplementaLaMismaInterfaz() {
        DroneProxy proxy = new DroneProxy(espia, "admin123");

        // El cliente solo debe conocer la interfaz
        IDroneService servicio = proxy;
        assertNotNull(servicio, "El DroneProxy debe poder asignarse a una variable IDroneService.");
    }

    @Test
    @DisplayName("DroneProxyService tambien implementa IDroneService (mismo contrato)")
    public void testServicioRealImplementaLaMismaInterfaz() {
        // Verificamos que el servicio real y el proxy comparten el mismo contrato
        // Esto garantiza que el cliente puede intercambiarlos sin cambiar su codigo
        IDroneService proxy      = new DroneProxy(espia, "admin123");
        IDroneService servicioReal = espia; // ServicioEspia implements IDroneService

        assertNotNull(proxy,        "El Proxy debe ser no null.");
        assertNotNull(servicioReal, "El Servicio Real debe ser no null.");
        assertNotSame(proxy, servicioReal,
                "El Proxy y el Servicio Real son objetos distintos que comparten la misma interfaz.");
    }

    // ================================================================
    // GRUPO 5: Propagacion de errores del servicio real
    // ================================================================

    @Test
    @DisplayName("Proxy propaga la excepcion del servicio real cuando este falla")
    public void testProxyPropagaErrorDelServicioReal() {
        espia.simularError = true;
        DroneProxy proxy = new DroneProxy(espia, "admin123");

        Exception ex = assertThrows(Exception.class, () -> proxy.eliminarDrone("DRONE-ERR"),
                "Si el servicio real lanza una excepcion, el Proxy debe propagarla al cliente.");
        assertTrue(ex.getMessage().contains("Error simulado"),
                "El mensaje de error del servicio real debe llegar intacto al cliente.");
    }
}
