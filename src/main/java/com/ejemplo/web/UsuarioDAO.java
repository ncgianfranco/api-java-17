package com.ejemplo.web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    
    public List<Usuario> obtenerTodos() throws  SQLException{
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT nombre, email FROM usuarios";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

                while(rs.next()) {
                    usuarios.add(new Usuario( rs.getString("nombre"), rs.getString("email")));
                }
        }

        return usuarios;
         

    }

    public Usuario buscarPorNombre(String nombre) throws  SQLException{
        String sql = "SELECT nombre, email FROM usuarios where nombre = ?";
        
        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);

            try(ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return new Usuario(rs.getString("nombre"), rs.getString("email"));
                }
            }
        }

        return null;
    }

    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, email) VALUES (?, ?)";

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarEmail(String nombre, String nuevoEmail) throws  SQLException {
        String sql = "UPDATE usuarios SET email = ? where nombre = ?";
        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEmail);
            stmt.setString(2, nombre);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminar(String nombre) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE nombre = ?";
        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            return stmt.executeUpdate() > 0;
        }
    }
}
