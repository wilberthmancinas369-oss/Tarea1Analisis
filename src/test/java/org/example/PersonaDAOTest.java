package org.example;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersonaDAOTest {

    private PersonaDAO dao = new PersonaDAO();

    @Test
    void testInsertarYListarPersona() {
        Persona p = new Persona("Test Integracion", "Direccion Test");
        p.setTelefonos(new ArrayList<>(List.of(new Telefono("999-9999999"))));

        dao.insertar(p);

        List<Persona> personas = dao.listarTodas();
        boolean encontrada = personas.stream()
                .anyMatch(per -> per.getNombre().equals("Test Integracion"));

        assertTrue(encontrada);

        // limpieza: borrar la persona de prueba para no ensuciar la BD
        Persona insertada = personas.stream()
                .filter(per -> per.getNombre().equals("Test Integracion"))
                .findFirst()
                .orElseThrow();
        dao.eliminar(insertada.getId());
    }

    @Test
    void testActualizarPersona() {
        Persona p = new Persona("Test Update", "Direccion Original");
        dao.insertar(p);

        List<Persona> personas = dao.listarTodas();
        Persona insertada = personas.stream()
                .filter(per -> per.getNombre().equals("Test Update"))
                .findFirst()
                .orElseThrow();

        insertada.setDireccion("Direccion Modificada");
        dao.actualizar(insertada);

        List<Persona> actualizadas = dao.listarTodas();
        Persona modificada = actualizadas.stream()
                .filter(per -> per.getId() == insertada.getId())
                .findFirst()
                .orElseThrow();

        assertEquals("Direccion Modificada", modificada.getDireccion());

        dao.eliminar(modificada.getId());
    }

    @Test
    void testEliminarPersona() {
        Persona p = new Persona("Test Eliminar", "Direccion X");
        dao.insertar(p);

        List<Persona> personas = dao.listarTodas();
        Persona insertada = personas.stream()
                .filter(per -> per.getNombre().equals("Test Eliminar"))
                .findFirst()
                .orElseThrow();

        dao.eliminar(insertada.getId());

        List<Persona> despues = dao.listarTodas();
        boolean sigueExistiendo = despues.stream()
                .anyMatch(per -> per.getId() == insertada.getId());

        assertFalse(sigueExistiendo);
    }
}