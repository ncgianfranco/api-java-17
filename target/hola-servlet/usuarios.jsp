<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ejemplo.web.Usuario" %>

<html>
<head>
    <title>Lista de Usuarios</title>
</head>
<body>
<h1>Usuarios</h1>
<ul>
<%
    List<Usuario> lista = (List<Usuario>) request.getAttribute("usuarios");
    if (lista != null) {
        for (Usuario u : lista) {
%>
    <li><%= u.getNombre() %> - <%= u.getEmail() %></li>
<%
        }
    } else {
%>
    <li>No hay usuarios</li>
<%
    }
%>
</ul>
</body>
</html>
