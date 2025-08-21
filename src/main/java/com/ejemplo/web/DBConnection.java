package com.ejemplo.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    
    private static final String URL = "jdbc:sqlite:/home/gianfranco/workspace/java-web/hola-servlet/db/usuarios.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            throw new RuntimeException("No se encontró el driver SQLITE", e);
        }
        
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

}
