package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersonaTest {

    @Test
    void testCrearPersonaConNombreYDireccion() {
        Persona p = new Persona("Juan Perez", "Calle 10");

        assertEquals("Juan Perez", p.getNombre());
        assertEquals("Calle 10", p.getDireccion());
    }

    @Test
    void testAgregarTelefono() {
        Persona p = new Persona("Ana", "Calle 5");
        p.agregarTelefono(new Telefono("555-1234567"));

        List<Telefono> telefonos = p.getTelefonos();
        assertEquals(1, telefonos.size());
        assertEquals("555-1234567", telefonos.get(0).getNumero());
    }

    @Test
    void testPersonaSinTelefonosInicialmenteVacia() {
        Persona p = new Persona("Luis", "Calle 8");
        assertTrue(p.getTelefonos().isEmpty());
    }
}