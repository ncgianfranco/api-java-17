package com.ejemplo.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/usuarios")
public class UsuariosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Usuario> lista = UsuarioRepository.obtenerTodos();
            request.setAttribute("usuarios", lista);
            // Redirigimos a JSP para mostrar la lista
            request.getRequestDispatcher("/usuarios.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error obteniendo usuarios");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");

        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");

        if (nombre == null || email == null || nombre.isEmpty() || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\":\"Se requieren nombre y email\"}");
            return;
        }

        boolean agregado = UsuarioRepository.agregarUsuario(new Usuario(nombre, email));
        if (agregado) {
            response.getWriter().println("{\"mensaje\":\"Usuario registrado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"No se pudo registrar el usuario\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String nombre = request.getParameter("nombre");

        if (nombre == null || nombre.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"nombre requerido\"}");
            return;
        }

        boolean eliminado = UsuarioRepository.eliminar(nombre);

        if (eliminado) {
            out.printf("{\"mensaje\":\"Usuario '%s' eliminado\"}", nombre);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.printf("{\"error\":\"Usuario '%s' no encontrado\"}", nombre);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Leer parámetros del body (form-urlencoded)
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String[] params = sb.toString().split("&");
        String nombre = null, nuevoEmail = null;
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                if (keyValue[0].equals("nombre")) nombre = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                if (keyValue[0].equals("email")) nuevoEmail = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
            }
        }

        if (nombre == null || nuevoEmail == null || nombre.isEmpty() || nuevoEmail.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Se requieren nombre y email\"}");
            return;
        }

        boolean actualizado = UsuarioRepository.actualizarEmail(nombre, nuevoEmail);

        if (actualizado) {
            out.printf("{\"mensaje\":\"Usuario %s actualizado con %s\"}", nombre, nuevoEmail);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.printf("{\"error\":\"Usuario %s no encontrado\"}", nombre);
        }
    }
}
