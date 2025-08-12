package com.ejemplo.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/usuarios")
public class UsuariosServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            String nombre = request.getParameter("nombre");
            PrintWriter out = response.getWriter();

            if (nombre != null && !nombre.isEmpty()) {
                Usuario u = UsuarioRepository.buscar(nombre);
                if (u != null) {
                    // Serializamos el objeto Usuario a JSON usando Gson
                    String usuarioJson = gson.toJson(u);
                    out.print(usuarioJson);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ErrorResponse("Usuario no encontrado")));
                }
            } else {
                List<Usuario> lista = UsuarioRepository.obtenerTodos();
                String listaJson = gson.toJson(lista);
                out.print(listaJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(gson.toJson(new ErrorResponse("Error interno del servidor")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");

        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");

        PrintWriter out = response.getWriter();

        if (nombre == null || email == null || nombre.isEmpty() || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse("Se requieren nombre y email")));
            return;
        }

        try {
            boolean agregado = UsuarioRepository.agregarUsuario(new Usuario(nombre, email));
            if (agregado) {
                out.print(gson.toJson(new MessageResponse("Usuario registrado correctamente")));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gson.toJson(new ErrorResponse("No se pudo registrar el usuario")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Error interno del servidor")));
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
            out.print(gson.toJson(new ErrorResponse("nombre requerido")));
            return;
        }

        try {
            boolean eliminado = UsuarioRepository.eliminar(nombre);
            if (eliminado) {
                out.print(gson.toJson(new MessageResponse("Usuario '" + nombre + "' eliminado")));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ErrorResponse("Usuario '" + nombre + "' no encontrado")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Error interno del servidor")));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Leemos el body (se espera application/x-www-form-urlencoded)
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            // Parseamos parámetros manualmente
            String[] params = sb.toString().split("&");
            String nombre = null, nuevoEmail = null;
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    if ("nombre".equals(keyValue[0])) nombre = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                    if ("email".equals(keyValue[0])) nuevoEmail = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                }
            }

            if (nombre == null || nuevoEmail == null || nombre.isEmpty() || nuevoEmail.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ErrorResponse("Se requieren nombre y email")));
                return;
            }

            boolean actualizado = UsuarioRepository.actualizarEmail(nombre, nuevoEmail);

            if (actualizado) {
                out.print(gson.toJson(new MessageResponse("Usuario " + nombre + " actualizado con " + nuevoEmail)));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ErrorResponse("Usuario " + nombre + " no encontrado")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Error interno del servidor")));
        }
    }

    // Clases internas para respuestas JSON uniformes
    private static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }

    private static class MessageResponse {
        private final String mensaje;
        public MessageResponse(String mensaje) { this.mensaje = mensaje; }
        public String getMensaje() { return mensaje; }
    }
}
