package com.ejemplo.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    
    private static final String URL = "jdbc:sqlite:/home/gianfranco/workspace/java-web/hola-servlet/db/usuarios.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            throw new SQLException("No se encontró el driver SQLITE");
        }
        return DriverManager.getConnection(URL);
    }

}
