package com.ejemplo.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/usuarios")
public class UsuariosServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Usuario> lista = UsuarioRepository.obtenerTodos();
            request.setAttribute("usuarios", lista);
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

        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        if (nombre == null || email == null || nombre.isEmpty() || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("error", "Se requieren nombre y email");
            out.print(gson.toJson(jsonResponse));
            return;
        }

        boolean agregado = UsuarioRepository.agregarUsuario(new Usuario(nombre, email));
        if (agregado) {
            jsonResponse.addProperty("mensaje", "Usuario registrado correctamente");
            out.print(gson.toJson(jsonResponse));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "No se pudo registrar el usuario");
            out.print(gson.toJson(jsonResponse));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        String nombre = request.getParameter("nombre");

        if (nombre == null || nombre.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("error", "nombre requerido");
            out.print(gson.toJson(jsonResponse));
            return;
        }

        boolean eliminado = UsuarioRepository.eliminar(nombre);

        if (eliminado) {
            jsonResponse.addProperty("mensaje", "Usuario '" + nombre + "' eliminado");
            out.print(gson.toJson(jsonResponse));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.addProperty("error", "Usuario '" + nombre + "' no encontrado");
            out.print(gson.toJson(jsonResponse));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        // Leer JSON desde el body
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        Usuario putUsuario = null;
        try {
            putUsuario = gson.fromJson(sb.toString(), Usuario.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("error", "JSON inválido");
            out.print(gson.toJson(jsonResponse));
            return;
        }

        if (putUsuario == null || putUsuario.getNombre() == null || putUsuario.getEmail() == null ||
            putUsuario.getNombre().isEmpty() || putUsuario.getEmail().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("error", "Se requieren nombre y email");
            out.print(gson.toJson(jsonResponse));
            return;
        }

        boolean actualizado = UsuarioRepository.actualizarEmail(putUsuario.getNombre(), putUsuario.getEmail());

        if (actualizado) {
            jsonResponse.addProperty("mensaje", "Usuario " + putUsuario.getNombre() + " actualizado con " + putUsuario.getEmail());
            out.print(gson.toJson(jsonResponse));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.addProperty("error", "Usuario " + putUsuario.getNombre() + " no encontrado");
            out.print(gson.toJson(jsonResponse));
        }
    }
}
