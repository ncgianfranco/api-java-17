package com.ejemplo.web;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/usuarios")
public class UsuariosServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            String nombre = request.getParameter("nombre");

            if (nombre != null && !nombre.isEmpty()) {
                Usuario u = usuarioDAO.buscarPorNombre(nombre);
                if (u != null) {
                    response.getWriter().print(gson.toJson(u));
                } else {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                }
            } else {
                List<Usuario> lista = usuarioDAO.obtenerTodos();
                response.getWriter().print(gson.toJson(lista));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            Usuario usuario = gson.fromJson(request.getReader(), Usuario.class);

            if (usuario.getNombre() == null || usuario.getEmail() == null ||
                usuario.getNombre().isEmpty() || usuario.getEmail().isEmpty()) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Se requieren nombre y email");
                return;
            }

            boolean agregado = usuarioDAO.insertar(usuario);
            if (agregado) {
                sendMessage(response, "Usuario registrado correctamente");
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo registrar el usuario");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            Usuario usuario = gson.fromJson(request.getReader(), Usuario.class);

            if (usuario.getNombre() == null || usuario.getEmail() == null ||
                usuario.getNombre().isEmpty() || usuario.getEmail().isEmpty()) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Se requieren nombre y email");
                return;
            }

            boolean actualizado = usuarioDAO.actualizarEmail(usuario.getNombre(), usuario.getEmail());
            if (actualizado) {
                sendMessage(response, "Usuario " + usuario.getNombre() + " actualizado con " + usuario.getEmail());
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Usuario " + usuario.getNombre() + " no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String nombre = request.getParameter("nombre");

        if (nombre == null || nombre.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "nombre requerido");
            return;
        }

        try {
            boolean eliminado = usuarioDAO.eliminar(nombre);
            if (eliminado) {
                sendMessage(response, "Usuario '" + nombre + "' eliminado");
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Usuario '" + nombre + "' no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor");
        }
    }

    // Helpers para JSON uniforme
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().print(gson.toJson(new ErrorResponse(message)));
    }

    private void sendMessage(HttpServletResponse response, String message) throws IOException {
        response.getWriter().print(gson.toJson(new MessageResponse(message)));
    }

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

