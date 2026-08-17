package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    private static final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private static final String USER = "usuario1";
    private static final String PASS = "superpassword";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public List<Persona> listarTodas() {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM Personas";

        try (Connection conn = conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Persona p = new Persona(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion"));
                p.setTelefonos(listarTelefonos(conn, p.getId()));
                personas.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personas;
    }

    private List<Telefono> listarTelefonos(Connection conn, int personaId) throws SQLException {
        List<Telefono> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM Telefonos WHERE personaId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                telefonos.add(new Telefono(rs.getInt("id"), rs.getInt("personaId"), rs.getString("telefono")));
            }
        }
        return telefonos;
    }

    public void insertar(Persona p) {
        String sqlPersona = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDireccion());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int nuevoId = rs.getInt(1);
                p.setId(nuevoId);
                insertarTelefonos(conn, nuevoId, p.getTelefonos());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertarTelefonos(Connection conn, int personaId, List<Telefono> telefonos) throws SQLException {
        String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Telefono t : telefonos) {
                ps.setInt(1, personaId);
                ps.setString(2, t.getNumero());
                ps.executeUpdate();
            }
        }
    }

    public void actualizar(Persona p) {
        String sqlPersona = "UPDATE Personas SET nombre = ?, direccion = ? WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sqlPersona)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDireccion());
            ps.setInt(3, p.getId());
            ps.executeUpdate();

            eliminarTelefonosDePersona(conn, p.getId());
            insertarTelefonos(conn, p.getId(), p.getTelefonos());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarTelefonosDePersona(Connection conn, int personaId) throws SQLException {
        String sql = "DELETE FROM Telefonos WHERE personaId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ps.executeUpdate();
        }
    }

    public void eliminar(int personaId) {
        String sql = "DELETE FROM Personas WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personaId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}