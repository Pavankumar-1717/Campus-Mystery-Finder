package com.campus.lostfound.config;

import com.campus.lostfound.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Allow public static assets and auth routes
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/") ||
            uri.startsWith("/uploads/") || uri.startsWith("/login") || uri.startsWith("/admin/setup") ||
            uri.startsWith("/h2-console") || uri.equals("/error")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // If not logged in, redirect to login page (Condition 8)
        if (currentUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Restrict admin routes to admin role only
        if (uri.startsWith("/admin") && !currentUser.isAdmin()) {
            response.sendRedirect("/?error=Admin+access+required");
            return false;
        }

        return true;
    }
}
