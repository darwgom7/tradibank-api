package com.darwgom.tradibankapi.domain.exceptions;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        System.out.println(":::::::::::::::::::::::::::::::::CustomAccessDeniedHandler Invoked:::::::::::::::::::::::::::::");
        // Personaliza aquí tu respuesta. Por ejemplo, estableciendo el estado HTTP a 403 Forbidden:
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // Y podrías escribir un mensaje personalizado en el cuerpo de la respuesta:
        response.getWriter().write("Access Denied x! " + accessDeniedException.getMessage());
    }
}

