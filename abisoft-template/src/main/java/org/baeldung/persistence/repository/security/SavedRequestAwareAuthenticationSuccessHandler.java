package org.baeldung.persistence.repository.security;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Intercepteur qui sauvegarde l'URL demandée avant redirection vers la page de connexion
 * Cela permet de rediriger l'utilisateur vers la page qu'il tentait d'accéder après une connexion réussie
 */
@Component
public class SavedRequestAwareAuthenticationSuccessHandler implements HandlerInterceptor {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Vérifier si l'accès à cette URL nécessite une authentification
        if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED || 
            response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
            
            // Sauvegarder l'URL demandée dans la session
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                String redirectUrl = savedRequest.getRedirectUrl();
                HttpSession session = request.getSession(true);
                session.setAttribute("SAVED_URL", redirectUrl);
            }
        }
        return true;
    }
}
