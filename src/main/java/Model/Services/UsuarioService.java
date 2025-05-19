package Model.Services;

import Model.Entities.Usuario;
import Model.Entities.PasswordRecoveryToken;
import Model.Repository.UsuarioRepository;
import Model.Repository.PasswordRecoveryTokenRepository;

import java.util.List;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.security.SecureRandom;
import java.util.Base64;

import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private PasswordRecoveryTokenRepository tokenRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
        this.tokenRepository = new PasswordRecoveryTokenRepository();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario autenticar(String username, String password) {
        System.out.println("DEBUG AUTH: Intento de autenticar usuario: " + username);

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("DEBUG AUTH: Datos de login nulos/vacíos.");
            return null;
        }

        Usuario usuario = usuarioRepository.findByUsername(username);
        System.out.println("DEBUG AUTH: Usuario encontrado en DB: " + (usuario != null ? usuario.getUsername() : "null"));

        if (usuario != null) {
            String storedHashedPassword = usuario.getPassword();
            System.out.println("DEBUG AUTH: Password plana ingresada (NO LOG EN PROD): [NO MOSTRAR]");
            System.out.println("DEBUG AUTH: Password almacenada (hashed): " + storedHashedPassword);

            boolean passwordMatch = verifyPassword(password, storedHashedPassword);
            System.out.println("DEBUG AUTH: Resultado de verifyPassword (BCrypt): " + passwordMatch);

            if (passwordMatch) {
                System.out.println("Usuario '" + username + "' autenticado con éxito.");
                return usuario;
            } else {
                System.out.println("Intento de autenticación fallido: Contraseña incorrecta para usuario '" + username + "'.");
                return null;
            }
        } else {
            System.out.println("Intento de autenticación fallido: Username '" + username + "' no encontrado.");
            return null;
        }
    }

    public boolean crearUsuario(Usuario usuario) {
        if (usuario == null || usuario.getUsername() == null || usuario.getUsername().trim().isEmpty() || usuario.getPassword() == null || usuario.getPassword().trim().isEmpty() || usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() || usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            System.err.println("Error de creación: Datos de usuario incompletos o vacíos.");
            return false;
        }

        Usuario usuarioExistente = usuarioRepository.findByUsername(usuario.getUsername());

        if (usuarioExistente != null) {
            System.out.println("Intento de crear usuario fallido: Username '" + usuario.getUsername() + "' ya existe.");
            return false;
        } else {
            try {
                String hashedPassword = hashPassword(usuario.getPassword());
                usuario.setPassword(hashedPassword);

                usuarioRepository.save(usuario);
                System.out.println("Usuario '" + usuario.getUsername() + "' creado con éxito.");
                return true;
            } catch (Exception e) {
                System.err.println("Error al guardar el nuevo usuario '" + usuario.getUsername() + "': " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.findByUsername(username);
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public void actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getUsername() == null) {
            System.err.println("Error al actualizar usuario: Objeto usuario o username es null.");
            return;
        }
        usuarioRepository.save(usuario);
        System.out.println("Usuario '" + usuario.getUsername() + "' actualizado.");
    }

    public void eliminarUsuario(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Error al eliminar usuario: Username es null o vacío.");
            return;
        }
        usuarioRepository.delete(username);
        System.out.println("Solicitud de eliminación para usuario: " + username + " procesada.");
    }

    public PasswordRecoveryToken requestPasswordRecovery(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Solicitud de recuperación con email vacío.");
            return null;
        }

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            System.out.println("Solicitud de recuperación para email no encontrado: " + email + ". No se generó token ni se envió email.");
            return null;
        }

        String token = generateSecureToken();

        Date expiryDate = calculateExpiryDate(60 * 60 * 1000);

        PasswordRecoveryToken recoveryToken = new PasswordRecoveryToken(token, usuario, expiryDate, false);

        try {
            tokenRepository.save(recoveryToken);
            System.out.println("Token de recuperación generado y guardado para usuario: " + usuario.getUsername() + " (" + token + ")");

            String baseUrl = "http://localhost:8080/Java_Municipios";
            String recoveryLink = baseUrl + "/resetPassword?token=" + token;

            sendPasswordRecoveryEmail(email, recoveryLink);

            return recoveryToken;

        } catch (Exception e) {
            System.err.println("Error en el proceso de solicitud de recuperación para usuario " + usuario.getUsername() + " (" + email + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Usuario validateRecoveryToken(String tokenString) {
        if (tokenString == null || tokenString.trim().isEmpty()) {
            System.out.println("Validación de token fallida: Token nulo o vacío.");
            return null;
        }
        PasswordRecoveryToken recoveryToken = tokenRepository.findByToken(tokenString);

        if (recoveryToken != null) {
            System.out.println("Validación de token exitosa para usuario: " + recoveryToken.getUser().getUsername());
            return recoveryToken.getUser();
        } else {
            System.out.println("Validación de token fallida: Token '" + tokenString + "' inválido, expirado o usado.");
            return null;
        }
    }

    public boolean resetPassword(String tokenString, String newPlainPassword) {
        System.out.println("DEBUG RESET: Intento de restablecer password para token: " + tokenString);

        if (tokenString == null || tokenString.trim().isEmpty() || newPlainPassword == null || newPlainPassword.trim().isEmpty()) {
            System.err.println("DEBUG RESET: Error de restablecimiento: Token o nueva password vacíos.");
            return false;
        }

        PasswordRecoveryToken recoveryToken = tokenRepository.findByToken(tokenString);

        if (recoveryToken == null) {
            System.err.println("DEBUG RESET: Restablecimiento fallido: Token '" + tokenString + "' inválido, expirado o usado. No se puede restablecer la contraseña.");
            return false;
        }

        Usuario userToUpdate = recoveryToken.getUser();
        if (userToUpdate == null) {
            System.err.println("DEBUG RESET: Error interno: Token válido pero sin usuario asociado. Token: " + tokenString);
            return false;
        }

        try {
            String hashedNewPassword = hashPassword(newPlainPassword);
            System.out.println("DEBUG RESET: Nueva password hasheada (NO LOG EN PROD): [NO MOSTRAR]");
            System.out.println("DEBUG RESET: Usuario a actualizar: " + userToUpdate.getUsername());

            userToUpdate.setPassword(hashedNewPassword);
            usuarioRepository.save(userToUpdate);
            System.out.println("DEBUG RESET: Usuario actualizado con nueva password.");

            tokenRepository.markTokenAsUsed(tokenString);
            System.out.println("DEBUG RESET: Token marcado como usado: " + tokenString);

            System.out.println("Contraseña restablecida con éxito para usuario: " + userToUpdate.getUsername() + " usando token " + tokenString);
            return true;

        } catch (Exception e) {
            System.err.println("DEBUG RESET: Error al restablecer contraseña para usuario " + userToUpdate.getUsername() + " con token " + tokenString + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        System.out.println("DEBUG HASH: Usando BCrypt para hashear password.");
        return this.passwordEncoder.encode(password);
    }

    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        System.out.println("DEBUG VERIFY: Usando BCrypt para verificar password.");
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            System.out.println("DEBUG VERIFY: Hashed password from DB is null or empty.");
            return false;
        }
        return this.passwordEncoder.matches(plainPassword, hashedPassword);
    }

    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String randomPart = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String token = UUID.randomUUID().toString().replace("-", "") + randomPart;
        if (token.length() > 255) {
            token = token.substring(0, 255);
        }
        System.out.println("Generated Token (Secure): " + token);
        return token;
    }

    private Date calculateExpiryDate(long milliseconds) {
        return new Date(System.currentTimeMillis() + milliseconds);
    }

    private void sendPasswordRecoveryEmail(String recipientEmail, String recoveryLink) throws MessagingException {

        final String username = "Diepiposa007@gmail.com";
        final String password = "pbhj byfb kygm qjkt";
        final String smtpHost = "smtp.gmail.com";
        final String smtpPort = "587";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);


        boolean useTLS = true;
        boolean useSSL = false;

        if (useTLS) {
            props.put("mail.smtp.starttls.enable", "true");

            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        }
        if (useSSL) {
            props.put("mail.smtp.ssl.enable", "true");
        }

        props.put("mail.debug", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));

            message.setSubject("Recuperación de Contraseña");

            String emailContent = "Hola,<br><br>"
                    + "Has solicitado restablecer tu contraseña.<br>"
                    + "Haz clic en el siguiente enlace para continuar:<br>"
                    + "<a href=\"" + recoveryLink + "\">" + recoveryLink + "</a><br><br>"
                    + "Este enlace expirará pronto.<br><br>"
                    + "Si no solicitaste esto, ignora este email.<br>";

            message.setContent(emailContent, "text/html");

            Transport.send(message);

            System.out.println("Email de recuperación enviado con éxito a: " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Error al enviar email de recuperación a " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace();

            throw e;
        }
    }
}
