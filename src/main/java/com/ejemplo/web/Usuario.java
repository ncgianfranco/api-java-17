package com.ejemplo.web;

public class Usuario {

    private String nombre;
    private String email;

    public Usuario(String nombre, String email){
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre(){
        return this.nombre;
    }

    public String getEmail(){
        return this.email;
    }
    
}
