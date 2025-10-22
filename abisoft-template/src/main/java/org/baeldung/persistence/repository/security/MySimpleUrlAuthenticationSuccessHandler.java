package org.baeldung.persistence.repository.security;

import org.baeldung.persistence.model.User;
import org.baeldung.service.AdresseMacService;
import org.baeldung.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@Component("myAuthenticationSuccessHandler")
public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AdresseMacService adresseMacService;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication) throws IOException {

        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(30 * 60);

            String username;
            if (authentication.getPrincipal() instanceof User) {
                username = ((User) authentication.getPrincipal()).getEmail();
            } else {
                username = authentication.getName();
            }
            
            // Récupérer l'URL de redirection sauvegardée dans la session
            String savedUrl = (String) session.getAttribute("SAVED_URL");
            session.removeAttribute("SAVED_URL");
            LoggedUser user = new LoggedUser(username, activeUserStore);
            session.setAttribute("user", user);
            session.setAttribute("user1", authentication.getPrincipal());
        }
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
        loginNotification(authentication, request);
    }

    private void loginNotification(Authentication authentication, HttpServletRequest request) {
        try {
            if (authentication.getPrincipal() instanceof User) {
                deviceService.verifyDevice(((User) authentication.getPrincipal()), request);
                adresseMacService.verifyDevice(((User) authentication.getPrincipal()), request);
            }
        } catch (Exception e) {
            logger.error("An error occurred while verifying device or location", e);
            throw new RuntimeException(e);
        }

    }

    protected void handle(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication) throws IOException {
        HttpSession session = request.getSession(false);
        String savedUrl = null;
        if (session != null) {
            savedUrl = (String) session.getAttribute("SAVED_URL");
            session.removeAttribute("SAVED_URL");
        }
        
        // Déterminer l'URL cible
        final String targetUrl = (savedUrl != null) ? savedUrl : determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            System.out.println("Utilisateur OAuth2 connecté : " + oidcUser.getAttribute("email"));
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(final Authentication authentication) {
        boolean isUser = false;
        boolean isAdmin = false;
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.err.println("here");
        for (final GrantedAuthority grantedAuthority : authorities) {
            String role = grantedAuthority.getAuthority();
            System.err.println(role + (" determineTargetUrl"));
            switch (role) {
                case "SUPERADMIN_PRIVILEGE":
                case "ADMIN_PRIVILEGE":
                case "EVENEMENT_ADMIN_PRIVILEGE":
                case "HEBERGEMENT_ADMIN_PRIVILEGE":
                case "PROPRIETAIRE_PRIVILEGE":
                    return "/admin/index";
                case "CLIENT_PRIVILEGE":
                    return "/index";
            }
        }
        
        throw new IllegalStateException("No valid roles found for the user.");
    }

    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}