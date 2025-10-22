package org.baeldung.web.controller.registration;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.model.Privilege;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.registration.OnRegistrationCompleteEvent;
import org.baeldung.persistence.repository.security.ISecurityUserService;
import org.baeldung.service.IUserService;
import org.baeldung.service.MailClient;
import org.baeldung.web.dto.PasswordDto;
import org.baeldung.web.dto.UserDto;
import org.baeldung.web.error.InvalidOldPasswordException;
import org.baeldung.web.error.UserAlreadyExistException;
import org.baeldung.web.util.GenericResponse;
import org.baeldung.web.util.MailContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    @Autowired
    private AuthenticationManager authenticationManager;

    public RegistrationController() {
        super();
    }

    // Registration

    @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse registerUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        // if (accountDto.getPrestationId() != null) {
        // Projet projet = new Projet();
        // projet.setOwner(registered);
        // projet.setPaymentStatus(PaymentStatus.NOT_PAID);
        // projet.setPrestation(prestationRepository.findById(accountDto.getPrestationId()).get());
        // projetRepository.save(projet);
        // }
        eventPublisher
                .publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerUserAccount(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "matchPassword", required = false) String matchPassword,
            @RequestParam(value = "tel", required = false) String tel,
            HttpServletRequest request) {

        LOGGER.debug("Registering user account with information: firstName={}, lastName={}, email={}, regType={}",
                firstName, lastName, email);

        Map<String, String> errors = new HashMap<>();

        try {
            // Validate common fields using messages from message source
            if (firstName == null || firstName.trim().isEmpty()) {
                errors.put("firstName", messages.getMessage("champs.user", null, request.getLocale()));
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                errors.put("lastName", messages.getMessage("champs.user", null, request.getLocale()));
            }
            if (email == null || email.trim().isEmpty()) {
                errors.put("email", messages.getMessage("champs.user", null, request.getLocale()));
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.put("email", messages.getMessage("ValidEmail.user.email", null, request.getLocale()));
            }
            if (password == null || password.trim().isEmpty()) {
                errors.put("password", messages.getMessage("champs.user", null, request.getLocale()));
            }
            if (!password.equals(matchPassword)) {
                errors.put("matchPassword", messages.getMessage("PasswordMatches.user", null, request.getLocale()));
            }
            if (tel != null && !tel.isEmpty() && !tel.matches("\\d{10}")) {
                errors.put("tel", messages.getMessage("ValidTel.user.tel", null, request.getLocale()));
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }

            // Register the user
            UserDto userDto = new UserDto();
            userDto.setFirstName(firstName);
            userDto.setLastName(lastName);
            userDto.setEmail(email);
            userDto.setPassword(password);
            userDto.setTel(tel);

            User registered = userService.registerNewUserAccount(userDto);

            registered.setRoles(new ArrayList<>(Arrays.asList(roleRepository.findByName("ROLE_CLIENT"))));

            userService.saveRegisteredUser(registered);

            // Wrap email sending in try-catch to handle mail errors separately
            try {
                String token = UUID.randomUUID().toString();
                userService.createVerificationTokenForUser(registered, token);
                sendResendVerificationTokenEmail(getAppUrl(request), request.getLocale(),
                        new VerificationToken(token), registered);
            } catch (org.springframework.mail.MailAuthenticationException e) {
                LOGGER.error("Mail authentication failed during registration", e);
                // Continue with registration but return a warning
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Registration successful but email notification failed");
                response.put("warning", messages.getMessage("error.mail.authentication", null, request.getLocale()));
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.ok(new GenericResponse("success"));

        } catch (UserAlreadyExistException e) {
            errors.put("email", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            LOGGER.error("Unexpected error during registration", e);
            // Check if it's a mail authentication error
            if (e.getCause() instanceof javax.mail.AuthenticationFailedException) {
                errors.put("global", messages.getMessage("error.mail.authentication", null, request.getLocale()));
            } else {
                errors.put("global", messages.getMessage("error.unexpected", null, request.getLocale()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
        }
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(final HttpServletRequest request, final Model model,
            @RequestParam("token") final String token, RedirectAttributes ra) throws UnsupportedEncodingException {
        Locale locale = request.getLocale();
        final String result = userService.validateVerificationToken(token);
        if (result.equals("valid")) {

            final User user = userService.getUser(token);

            // if (user.isUsing2FA()) {
            // model.addAttribute("qr", userService.generateQRUrl(user));
            // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }
            final HttpSession session = request.getSession(false);
            if (session != null) {
                session.setMaxInactiveInterval(30 * 60);
                session.setAttribute("user1", user);
            }
            authWithoutPassword(user);
            // ra.addAttribute("user", user);
            System.out.println("user=" + user.toString());

            // System.out.println("projetccccccccccccccccccccccccc: " +
            // user.getProjets().get(0));

            // boolean hasUnpaidProject = user.getProjets().stream()
            // .anyMatch(projet -> projet.getPaymentStatus() == PaymentStatus.NOT_PAID); //
            // Assuming UNPAID is the
            // // enum/status for unpaid projects

            // if (hasUnpaidProject) {
            // // Redirect to the checkout page if there are unpaid projects

            // return "redirect:/Client/checkout";
            // }
            if (user.getRoles().contains(roleRepository.findByName("ROLE_EMPLOYEE"))) {
                return "redirect:/Employee/index";
            } else {
                return "redirect:/index";
            }
        }

        model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    // user activation - verification

    @RequestMapping(value = "/user/resendRegistrationToken", method = RequestMethod.GET)
    @ResponseBody
    public GenericResponse resendRegistrationToken(final HttpServletRequest request,
            @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(getAppUrl(request), request.getLocale(), newToken, user));
        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

    // @RequestMapping(value = "/user/resetPassword", method = RequestMethod.POST)
    // @ResponseBody
    // public GenericResponse resetPassword(final HttpServletRequest request,
    // @RequestParam("email") final String userEmail, final Locale locale) {
    // final User user = userService.findUserByEmail(userEmail);
    // if (user != null) {

    // final String token = UUID.randomUUID().toString();
    // userService.createPasswordResetTokenForUser(user, token);

    // // mailClient.prepareAndSend(userEmail, "Bonjour","fff");
    // sendResetTokenEmail(getAppUrl(request), request.getLocale(), token, user);
    // }
    // return new GenericResponse(messages.getMessage("message.resetPasswordEmail",
    // null, request.getLocale()));
    //     }

    // Reset password
    @RequestMapping(value = "/user/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse resetPassword(final HttpServletRequest request,
            @RequestParam("email") final String userEmail, final Locale locale) {
        final User user = userService.findUserByEmail(userEmail);
        if (user != null) {
            String[] messages1 = new String[5];
            final String message = messages.getMessage("message.resetPassword", null,
                    locale);
            final String message2 = messages.getMessage("message.registration.tete",
                    null, locale);
            final String message4 = messages.getMessage("message.registration.fin", null,
                    locale);
            final String message3 = messages.getMessage("message.resetPassword.text",
                    null, locale);
            final String message5 = messages.getMessage("message.resetPassword.text1",
                    null, locale);

            messages1[0] = message2 + " " + user.getFirstName() + ",";
            messages1[1] = message;
            messages1[2] = message3;
            messages1[3] = message4;
            messages1[4] = message5;
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            Path p = Paths.get("header.jpg");
            final String url = getAppUrl(request) + "/old/user/changePassword?id=" + user.getId() + "&token="
                    + token;

            mailClient.sendEmail(userEmail,
                    messages.getMessage("message.resetPassword.text", null, request.getLocale()) + "\n"
                            + messages.getMessage("message.resetPassword.text1", null, request.getLocale()),
                    messages.getMessage("message.resetPassword", null, request.getLocale()),
                    messages.getMessage("message.updatePassword", null, request.getLocale()), url,
                    messages.getMessage("user.greeting", null, request.getLocale()), request.getContextPath());
            // mailClient.prepareAndSend(constructResetTokenEmail(getAppUrl(request),
            // request.getLocale(), token, user),
            // p.getFileName().toString(), "header.jpg", messages1);
            // mailSender.send(constructResetTokenEmail(getAppUrl(request),
            // request.getLocale(), token, user));
            // mailClient.prepareAndSend(userEmail, "Bonjour","fff");
            return new GenericResponse(messages.getMessage("message.resetPasswordEmail",
                    null, request.getLocale()));
        }
        return new GenericResponse((String) null, messages.getMessage("message.regError", null, request.getLocale()));

    }

    @RequestMapping(value = "/user/changePassword", method = RequestMethod.GET)
    public String showChangePasswordPage(final Locale locale, final Model model, @RequestParam("id") final long id,
            @RequestParam("token") final String token) {
        final String result = securityUserService.validatePasswordResetToken(id, token);
        if (result != null) {
            model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
            return "redirect:/login?lang=" + locale.getLanguage();
        }
        return "redirect:/changePassword.html?lang=" + locale.getLanguage();
    }

    @RequestMapping(value = "/user/savePassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    }

    // change user password
    @RequestMapping(value = "/user/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = userService.findUserByEmail(
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    @RequestMapping(value = "/user/update/2fa", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse modifyUser2FA(@RequestParam("use2FA") final boolean use2FA)
            throws UnsupportedEncodingException {
        final User user = userService.updateUser2FA(use2FA);
        if (use2FA) {
            return new GenericResponse(userService.generateQRUrl(user));
        }
        return null;
    }

    // ============== NON-API ============

    // private SimpleMailMessage constructResendVerificationTokenEmail(final String
    // contextPath, final Locale locale,
    // final VerificationToken newToken, final User user) {
    // final String confirmationUrl = contextPath + "/registrationConfirm?token=" +
    // newToken.getToken();
    // final String message = messages.getMessage("message.resendToken", null,
    // locale);
    // return constructEmail("Resend Registration Token", message + " \r\n" +
    // confirmationUrl, user);
    // }

    // private SimpleMailMessage constructResetTokenEmail(final String contextPath,
    // final Locale locale,
    // final String token, final User user) {
    // final String url = contextPath + "/user/changePassword?id=" + user.getId() +
    // "&token=" + token;
    // // final String message = messages.getMessage("message.resetPassword", null,
    // // locale);
    // final String message1 = messages.getMessage("message.resetPassword.objet",
    // null, locale);
    // // final String message2 = messages.getMessage("message.registration.tete",
    // // null, locale);
    // // return constructEmail(message1, message2+" "+user.getFirstName()+","+ "
    // \r\n"
    // // +message + " \r\n" + url, user);
    // return constructEmail(message1, url, user);
    // }

    // private SimpleMailMessage constructEmail(String subject, String body, User
    // user) {
    // final SimpleMailMessage email = new SimpleMailMessage();
    // email.setSubject(subject);
    // email.setText(body);
    // email.setTo(user.getEmail());
    // email.setFrom(env.getProperty("support.email"));
    // return email;
    // }
    // send email with a template
    private void sendEmail(String email, String text, String sujet, String action, String url, String greeting,
            String contextPath) {
        try {
            MailContent mailContent = new MailContent(null, sujet, null, null);
            String[] bodyMessages = { greeting, text };
            mailContent.buildListOfBodyContent(bodyMessages);
            mailContent.defaultFootter();
            // mailContent.defaultLogo();
            mailContent.setLogo(contextPath + "/assets/images/logo1.png");
            mailContent.setAction(action);
            mailContent.setUrl(url);
            mailClient.prepareAndSendCustom(email, mailContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // send email with template
    private void sendResetTokenEmail(final String contextPath, final Locale locale, final String token,
            final User user) {
        final String url = contextPath + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        final String subject = messages.getMessage("password.change.subject", null, locale);
        final String action = messages.getMessage("password.resset.action", null, locale);
        final String greeting = messages.getMessage("user.greeting", null, locale) + " " + user.getFirstName();
        sendEmail(user.getEmail(), message, subject, action, url, greeting, contextPath);
    }

    private void sendResendVerificationTokenEmail(final String contextPath, final Locale locale,
            final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/registrationConfirm?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        final String subject = messages.getMessage("user.registration.confirm.subject", null, locale);
        final String action = messages.getMessage("registration.confirm.action", null, locale);
        final String greeting = messages.getMessage("user.greeting", null, locale) + " " + user.getFirstName();
        // constructEmail("Resend Registration Token", message + " \r\n" +
        // confirmationUrl, user);
        sendEmail(user.getEmail(), message, subject, action, confirmationUrl, greeting, contextPath);
    }

    private void sendResendVerificationTokenEmailForEmp(final String contextPath, final Locale locale,
            final VerificationToken newToken, final User user) {
        // final String confirmationUrl = contextPath + "/registrationConfirm?token=" +
        // newToken.getToken();
        final String message = messages.getMessage("message.resendTokenEmp", null, locale);
        final String subject = messages.getMessage("user.registration.confirm.subject", null, locale);
        // final String action = messages.getMessage("registration.confirm.action",
        // null, locale);
        final String greeting = messages.getMessage("user.greeting", null, locale) + " " + user.getFirstName();
        // constructEmail("Resend Registration Token", message + " \r\n" +
        // confirmationUrl, user);
        sendEmail(user.getEmail(), message, subject, null, null, greeting, contextPath);
    }
    //

    // ============== NON-API ============

    private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale,
            final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale,
            final String token, final User user) {
        final String url = contextPath + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        // final String message = messages.getMessage("message.resetPassword", null,
        // locale);
        final String message1 = messages.getMessage("message.resetPassword.objet", null, locale);
        // final String message2 = messages.getMessage("message.registration.tete",
        // null, locale);
        // return constructEmail(message1, message2+" "+user.getFirstName()+","+ " \r\n"
        // +message + " \r\n" + url, user);
        return constructEmail(message1, url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    public void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) {
            LOGGER.error("Error while login ", e);
        }
    }

    public void authWithAuthManager(HttpServletRequest request, String username, String password) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        authToken.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        // SecurityContextHolder.getContext());
    }

    public void authWithoutPassword(User user) {
        List<Privilege> privileges = user.getRoles().stream().map(role -> role.getPrivileges())
                .flatMap(list -> list.stream()).distinct().collect(Collectors.toList());
        List<GrantedAuthority> authorities = privileges.stream().map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Save file logic (implement based on your needs)
    public String saveFile(MultipartFile file, long userID) throws IOException {
        // Get the absolute path for saving the file in the project directory
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "employeeDocuments");

        // Check if the directory exists, if not, create it
        if (Files.notExists(uploadDir)) {
            Files.createDirectories(uploadDir); // Creates the directory and any necessary parent directories
        }

        // Create the destination file with a unique name based on the current time and
        // user ID
        String filename = System.currentTimeMillis() + "_" + userID + "_" + file.getOriginalFilename();
        Path dest = uploadDir.resolve(filename);

        // Transfer the file to the destination path
        file.transferTo(dest.toFile());

        // Return the absolute path of the saved file
        // return dest.toString();
        return "uploads/employeeDocuments/" + filename;
    }

    private final SimpleMailMessage constructEmailMessageForEmp(final User user) {
        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";
        // final String confirmationUrl = event.getAppUrl() +
        // "/registrationConfirm.html?token=" + token;
        final String message = "You registered successfully. We will send you a confirmation message to your email account. if ou're accepted";
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private final SimpleMailMessage constructEmailMessage(final User user, HttpServletRequest request) {
        String appUrl = constructAppUrl(request);
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";

        // Get the current URL dynamically using ServletUriComponentsBuilder
        final String confirmationUrl = appUrl + "/registrationConfirm.html?token=" + token;

        final String message = "You registered successfully. We will send you a confirmation message to your email account";
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\n" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));

        return email;
    }

    private String constructAppUrl(HttpServletRequest request) {
        String scheme = request.getScheme(); // "http" or "https"
        String serverName = request.getServerName(); // "example.com"
        int serverPort = request.getServerPort(); // 80, 443, or custom port
        String contextPath = request.getContextPath(); // Application context path

        // Construct the base URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        // Include the port if it's not a default port
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);
        return url.toString();
    }

}
