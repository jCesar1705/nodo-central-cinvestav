package mx.cinvestav.central.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** Bloquea con 403 las peticiones a endpoints @SoloAdmin sin header X-Role: ADMINISTRADOR. */
public class RolInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod hm)) {
            return true;
        }
        boolean requiereAdmin =
                hm.getMethodAnnotation(SoloAdmin.class) != null
                || hm.getBeanType().isAnnotationPresent(SoloAdmin.class);

        if (!requiereAdmin) {
            return true;
        }

        String rol = req.getHeader("X-Role");
        if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {
            return true;
        }
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"Se requiere rol ADMINISTRADOR (header X-Role)\"}");
        return false;
    }
}
