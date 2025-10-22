package org.baeldung.spring;

import org.baeldung.persistence.repository.security.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SavedRequestAwareAuthenticationSuccessHandler savedRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Ajouter l'intercepteur pour sauvegarder les URLs demandées
        registry.addInterceptor(savedRequestInterceptor)
                .addPathPatterns("/hebergement/cart_hebergement", "/hebergement/cart_hebergement.html",
                                 "/hebergement/payment_hebergement", "/hebergement/payment_hebergement.html",
                                 "/hebergement/confirmation_hebergement", "/hebergement/confirmation_hebergement.html",
                                 "/hebergement/wishlist", "/hebergement/wishlist.html", "/hebergement/wishlist/**",
                                 "/evenment/cart_evenment", "/evenment/cart_evenment.html",
                                 "/evenment/payment_evenment", "/evenment/payment_evenment.html",
                                 "/evenment/confirmation_evenment", "/evenment/confirmation_evenment.html");
    }
}
