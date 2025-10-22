package org.baeldung.spring;

import java.util.Locale;

import org.baeldung.validation.EmailValidator;
import org.baeldung.validation.PasswordMatchesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Configuration
@ComponentScan(basePackages = { "org.baeldung.web" })
@EnableWebMvc
//@PropertySource("classpath:messages_fr_FR.properties", encoding="UTF-8")
@PropertySource(value = "classpath:messages_fr_FR.properties", encoding="UTF-8")
public class MvcConfig implements WebMvcConfigurer {

    public MvcConfig() {
        super();
    }

    @Autowired
    private MessageSource messageSource;

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");

        registry.addViewController("/graph_chartist").setViewName("SuperAdmin/graph_chartist");
        registry.addViewController("/contacts").setViewName("SuperAdmin/contacts");
        registry.addViewController("/mailbox").setViewName("SuperAdmin/mailbox");
        registry.addViewController("/mail_detail").setViewName("SuperAdmin/mail_detail");
        registry.addViewController("/layouts").setViewName("SuperAdmin/layouts");
        registry.addViewController("/metrics").setViewName("SuperAdmin/metrics");
        registry.addViewController("/widgets").setViewName("SuperAdmin/widgets");
        registry.addViewController("/form_basic").setViewName("SuperAdmin/form_basic");
        registry.addViewController("/form_advanced").setViewName("SuperAdmin/form_advanced");
        registry.addViewController("/form_wizard").setViewName("SuperAdmin/form_wizard");
        registry.addViewController("/form_file_upload").setViewName("SuperAdmin/form_file_upload");
        registry.addViewController("/form_editors").setViewName("SuperAdmin/form_editors");
        registry.addViewController("/form_autocomplete").setViewName("SuperAdmin/form_autocomplete");
        registry.addViewController("/form_markdown").setViewName("SuperAdmin/form_markdown");
        registry.addViewController("/contacts").setViewName("SuperAdmin/contacts");
       // registry.addViewController("/profile").setViewName("SuperAdmin/profile");
        registry.addViewController("/profile_2").setViewName("SuperAdmin/profile_2");
        registry.addViewController("/contacts_2").setViewName("SuperAdmin/contacts_2");
        registry.addViewController("/projects").setViewName("SuperAdmin/projects");
//        registry.addViewController("/project").setViewName("SuperAdmin/projet");
        registry.addViewController("/project_detail").setViewName("SuperAdmin/project_detail");
        registry.addViewController("/activity_stream").setViewName("SuperAdmin/activity_stream");
        registry.addViewController("/teams_board").setViewName("SuperAdmin/teams_board");
        registry.addViewController("/clients").setViewName("SuperAdmin/clients");
        registry.addViewController("/full_height").setViewName("SuperAdmin/full_height");
        registry.addViewController("/vote_list").setViewName("SuperAdmin/vote_list");
        registry.addViewController("/file_manager").setViewName("SuperAdmin/file_manager");
        registry.addViewController("/calendar").setViewName("SuperAdmin/calendar");
        registry.addViewController("/issue_tracker").setViewName("SuperAdmin/issue_tracker");
        registry.addViewController("/blog").setViewName("SuperAdmin/blog");
        registry.addViewController("/article").setViewName("SuperAdmin/article");
        registry.addViewController("/faq").setViewName("SuperAdmin/faq");
        registry.addViewController("/timeline").setViewName("SuperAdmin/timeline");
        registry.addViewController("/pin_board").setViewName("SuperAdmin/pin_board");
        registry.addViewController("/search_results").setViewName("SuperAdmin/search_results");
        registry.addViewController("/lockscreen").setViewName("SuperAdmin/lockscreen");
        registry.addViewController("/invoice").setViewName("SuperAdmin/invoice");
        registry.addViewController("/login").setViewName("SuperAdmin/login");
        registry.addViewController("/login_two_columns").setViewName("SuperAdmin/login_two_columns");
        registry.addViewController("/forgot_password").setViewName("SuperAdmin/forgot_password");
        registry.addViewController("/register").setViewName("SuperAdmin/register");
        registry.addViewController("/404").setViewName("SuperAdmin/404");
        registry.addViewController("/500").setViewName("SuperAdmin/500");
        registry.addViewController("/empty_page").setViewName("SuperAdmin/empty_page");
        registry.addViewController("/toastr_notifications").setViewName("SuperAdmin/toastr_notifications");
        registry.addViewController("/nestable_list").setViewName("SuperAdmin/nestable_list");
        registry.addViewController("/agile_board").setViewName("SuperAdmin/agile_board");
        registry.addViewController("/timeline_2").setViewName("SuperAdmin/timeline_2");
        registry.addViewController("/diff").setViewName("SuperAdmin/diff");
        registry.addViewController("/pdf_viewer").setViewName("SuperAdmin/pdf_viewer");
        registry.addViewController("/i18support").setViewName("SuperAdmin/i18support");
        registry.addViewController("/sweetalert").setViewName("SuperAdmin/sweetalert");
        registry.addViewController("/idle_timer").setViewName("SuperAdmin/idle_timer");
        registry.addViewController("/truncate").setViewName("SuperAdmin/truncate");
        registry.addViewController("/password_meter").setViewName("SuperAdmin/password_meter");
        registry.addViewController("/spinners").setViewName("SuperAdmin/spinners");
        registry.addViewController("/spinners_usage").setViewName("SuperAdmin/spinners_usage");
        registry.addViewController("/tinycon").setViewName("SuperAdmin/tinycon");
        registry.addViewController("/google_maps").setViewName("SuperAdmin/google_maps");
        registry.addViewController("/datamaps").setViewName("SuperAdmin/datamaps");
        registry.addViewController("/social_buttons").setViewName("SuperAdmin/social_buttons");
        registry.addViewController("/code_editor").setViewName("SuperAdmin/code_editor");
        registry.addViewController("/modal_window").setViewName("SuperAdmin/modal_window");
        registry.addViewController("/clipboard").setViewName("SuperAdmin/clipboard");
        registry.addViewController("/text_spinners").setViewName("SuperAdmin/text_spinners");
        registry.addViewController("/forum_main").setViewName("SuperAdmin/forum_main");
        registry.addViewController("/validation").setViewName("SuperAdmin/validation");
        registry.addViewController("/tree_view").setViewName("SuperAdmin/tree_view");
        registry.addViewController("/loading_buttons").setViewName("SuperAdmin/loading_buttons");
        registry.addViewController("/chat_view").setViewName("SuperAdmin/chat_view");
        registry.addViewController("/masonry").setViewName("SuperAdmin/masonry");
        registry.addViewController("/tour").setViewName("SuperAdmin/tour");
        registry.addViewController("/typography").setViewName("SuperAdmin/typography");
        registry.addViewController("/icons").setViewName("SuperAdmin/icons");
        registry.addViewController("/draggable_panels").setViewName("SuperAdmin/draggable_panels");
        registry.addViewController("/buttons").setViewName("SuperAdmin/buttons");
        registry.addViewController("/video").setViewName("SuperAdmin/video");
        registry.addViewController("/tabs_panels").setViewName("SuperAdmin/tabs_panels");
        registry.addViewController("/tabs").setViewName("SuperAdmin/tabs");
        registry.addViewController("/notifications").setViewName("SuperAdmin/notifications");
        registry.addViewController("/helper_classes").setViewName("SuperAdmin/helper_classes");
        registry.addViewController("/badges_labels").setViewName("SuperAdmin/badges_labels");
        registry.addViewController("/table_basic").setViewName("SuperAdmin/table_basic");
        registry.addViewController("/table_data_tables").setViewName("SuperAdmin/table_data_tables");
        registry.addViewController("/table_foo_table").setViewName("SuperAdmin/table_foo_table");
        registry.addViewController("/jq_grid").setViewName("SuperAdmin/jq_grid");
        registry.addViewController("/ecommerce_products_grid").setViewName("SuperAdmin/ecommerce_products_grid");
        registry.addViewController("/ecommerce_product_list").setViewName("SuperAdmin/ecommerce_product_list");
        registry.addViewController("/ecommerce_product").setViewName("SuperAdmin/ecommerce_product");
        registry.addViewController("/ecommerce_product_detail").setViewName("SuperAdmin/ecommerce_product_detail");
        registry.addViewController("/ecommerce-cart").setViewName("SuperAdmin/ecommerce-cart");
        registry.addViewController("/ecommerce-orders").setViewName("SuperAdmin/ecommerce-orders");
        registry.addViewController("/ecommerce_payments").setViewName("SuperAdmin/ecommerce_payments");
        registry.addViewController("/basic_gallery").setViewName("SuperAdmin/basic_gallery");
        registry.addViewController("/slick_carousel").setViewName("SuperAdmin/slick_carousel");
        registry.addViewController("/carousel").setViewName("SuperAdmin/carousel");
        registry.addViewController("/landing").setViewName("SuperAdmin/landing");
        registry.addViewController("/package").setViewName("SuperAdmin/package");
        registry.addViewController("/grid_options").setViewName("SuperAdmin/grid_options");
        registry.addViewController("/mail_compose").setViewName("SuperAdmin/mail_compose");
        registry.addViewController("/email_template").setViewName("SuperAdmin/email_template");
        registry.addViewController("/dashboard_2").setViewName("SuperAdmin/dashboard_2");
        registry.addViewController("/dashboard_3").setViewName("SuperAdmin/dashboard_3");
        registry.addViewController("/dashboard_5").setViewName("SuperAdmin/dashboard_5");
        registry.addViewController("/dashboard_4_1").setViewName("SuperAdmin/dashboard_4_1");
       // registry.addViewController("/homepage.html");
        registry.addViewController("/Client/index").setViewName("Client/index");;
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("SuperAdmin/index").setViewName("SuperAdmin/index");
        registry.addViewController("/contact").setViewName("contact");
        registry.addViewController("/login");
      //  registry.addRedirectViewController("/SuperAdmin/login", "/login");
       // registry.addViewController("/SuperAdmin/login").setViewName("/login");;
        registry.addViewController("/index");
        registry.addViewController("/loginRememberMe");
        registry.addViewController("/customLogin");
        registry.addViewController("/registration.html");
        registry.addViewController("/registrationCaptcha.html");
        registry.addViewController("/logout.html");
        registry.addViewController("/homepage.html");
        registry.addViewController("/expiredAccount.html");
        registry.addViewController("/badUser.html");
        registry.addViewController("/emailError.html");
        registry.addViewController("/home.html");
        registry.addViewController("/invalidSession.html");
        registry.addViewController("/console.html");
        registry.addViewController("/admin.html");
        registry.addViewController("/successRegister.html");
        registry.addViewController("/forgetPassword.html");
        registry.addViewController("/updatePassword.html");
        registry.addViewController("/changePassword.html");
        registry.addViewController("/users.html");
        registry.addViewController("/qrcode.html");



        // modification du 5/7/25

        //registry.addViewController("/infosTransports").setViewName("infosTransports");
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
      //registry.addResourceHandler("/resources/**").addResourceLocations("/", "/resources/");
        // Configuration pour servir les images d'hébergement depuis le dossier uploads
        String uploadDir = "uploads";
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
                
        // Configuration existante pour les autres ressources statiques
        registry.addResourceHandler(
        		"/email_templates/**",
        		"/Admin/**","/assets",
        		"/SuperAdmin/**",
        		"/Client/**",
        		"/css1/**",
                "/webjars/**",
                "/resources/**",
                "/img/**",
                "/images/**",
                "/img1/**",
                "/contactform/**",
                "/font/**",
                "/fonts/**",
                "/ico/**",
                "/css/**",
                "/vendor/**",
                "/scss/**",
                "/skins/**",
                "/js1/**",
                "/font-awesome/**",
                "/js/**",
                "/assets/**",
                "/public/**")
                .addResourceLocations(
                		"classpath:/static/SuperAdmin/email_templates/",
                		"classpath:/static/SuperAdmin/",
                		"classpath:/static/Admin/",
                		"classpath:/static/SuperAdmin/css1/",
                    "classpath:/META-INF/resources/webjars/",
                    "classpath:/resources/",
                    "classpath:/static/img/",
                    "classpath:/static/images/",
                    "classpath:/static/SuperAdmin/img1/",
                    "classpath:/static/contactform/",
                    "classpath:/static/font/",
                    "classpath:/static/fonts/",
                    "classpath:/static/ico/",
                    "classpath:/static/css/",
                    "classpath:/static/vendor/",
                    "classpath:/static/scss/",
                    "classpath:/static/skins/",
                    "classpath:/static/SuperAdmin/js1/",
                    "classpath:/static/SuperAdmin/font-awesome/",
                    "classpath:/static/js/",
                    "classpath:/static/assets/",
                    "classpath:/static/public/");
    }

    @Bean

    public LocaleResolver localeResolver(){
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();

        localeResolver.setDefaultLocale(Locale.US);

        return  localeResolver;

    }



    @Bean

    public LocaleChangeInterceptor localeChangeInterceptor(){

        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();

        localeChangeInterceptor.setParamName("lang");

        return localeChangeInterceptor;

    }



    @Override

    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(localeChangeInterceptor());

    }

    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
        return (factory) -> factory.setRegisterDefaultServlet(true);
    }
    
    
    @Bean
    public EmailValidator usernameValidator() {
        return new EmailValidator();
    }
   

    @Bean
    public PasswordMatchesValidator passwordMatchesValidator() {
        return new PasswordMatchesValidator();
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }



}