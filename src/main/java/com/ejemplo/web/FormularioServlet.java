package com.ejemplo.web;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/formulario")
public class FormularioServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Usa POST y pasa el parametro nombre\"}");
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");

        //indicar que la respuesta sera json
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // 1. obtenemos el valo de los parametros pasado por metodo post
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");

        
        // 3. crear una cadena json de respuesta
        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean valido = true;

        if(nombre == null || nombre.isEmpty()){
            json.append("\"error_nombre\":\"El nombre es obligatorio\",");
            valido = false;
        }

        if(email == null || email.isEmpty()){
            json.append("\"error_email\":\"El email es obligatorio\",");
            valido = false;
        }

        if (valido) {
            Usuario nuevo = new Usuario(nombre, email);
            UsuarioRepository.agregarUsuario(nuevo);
            json.append(String.format("\"saludo\": \"Usuario %s guardado\"", nombre));
        } else {
            if (json.charAt(json.length() -1) == ',') {
                json.deleteCharAt(json.length() -1);
            }
        }

        json.append("}");
        response.getWriter().write(json.toString());

        // 4. escribir la respuesta json al cliente
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    
}
