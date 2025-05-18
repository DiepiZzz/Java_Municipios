package Model.Services; // Asegura que este es el nombre correcto de tu paquete de servicios

import Model.Entities.Usuario; // Importar la entidad Usuario
import Model.Entities.PasswordRecoveryToken; // Importar la entidad token
import Model.Repository.UsuarioRepository; // Importar el repositorio de Usuario
import Model.Repository.PasswordRecoveryTokenRepository; // Importar el repositorio de tokens

import java.util.List;
import java.util.Date; // Para fechas
import java.util.Properties; // Para configuración de email
import java.util.UUID; // Para generar parte del token
import java.security.SecureRandom; // Para generar parte segura del token
import java.util.Base64; // Para codificar bytes aleatorios

// Importaciones para envío de email (JavaMail API)
// Asegúrate de tener la dependencia javax.mail:mail o jakarta.mail:jakarta.mail-api en tu pom.xml
import javax.mail.*;
import javax.mail.internet.*;
// Importaciones de activation si usas FileDataSource para adjuntos, aunque aquí no se usan
// import javax.activation.*;

// *** Importación para BCrypt hashing ***
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private PasswordRecoveryTokenRepository tokenRepository; // Repositorio de tokens

    // *** Instancia del encoder BCrypt ***
    private final BCryptPasswordEncoder passwordEncoder; // <<-- Declara el encoder BCrypt


    /**
     * Constructor de la clase de servicio. Inicializa los repositorios que
     * utilizará y el encoder BCrypt.
     */
    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
        this.tokenRepository = new PasswordRecoveryTokenRepository(); // Instanciar el repositorio de tokens
        // *** Inicializa el encoder BCrypt ***
        this.passwordEncoder = new BCryptPasswordEncoder(); // <<-- Inicialízalo aquí
    }

    /**
     * Intenta autenticar un usuario con username y password. ¡Ahora usando BCrypt!
     *
     * @param username El nombre de usuario ingresado.
     * @param password La contraseña PLANA ingresada.
     * @return El objeto Usuario autenticado si las credenciales son correctas,
     * de lo contrario null.
     */
    public Usuario autenticar(String username, String password) {
        System.out.println("DEBUG AUTH: Intento de autenticar usuario: " + username); // Log inicio

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("DEBUG AUTH: Datos de login nulos/vacíos.");
            // En el controlador (LoginController), deberías poner un mensaje de error en el request antes de reenviar a la página.
            return null;
        }

        Usuario usuario = usuarioRepository.findByUsername(username);
        System.out.println("DEBUG AUTH: Usuario encontrado en DB: " + (usuario != null ? usuario.getUsername() : "null")); // Log si se encontró

        if (usuario != null) {
            String storedHashedPassword = usuario.getPassword();
            // *** Log seguro: No imprimas la password plana en producción ***
            System.out.println("DEBUG AUTH: Password plana ingresada (NO LOG EN PROD): [NO MOSTRAR]");
            System.out.println("DEBUG AUTH: Password almacenada (hashed): " + storedHashedPassword); // Log password almacenada

            // *** Usar el método verifyPassword con BCrypt ***
            boolean passwordMatch = verifyPassword(password, storedHashedPassword);
            System.out.println("DEBUG AUTH: Resultado de verifyPassword (BCrypt): " + passwordMatch); // Log resultado de verificación

            if (passwordMatch) {
                System.out.println("Usuario '" + username + "' autenticado con éxito.");
                return usuario; // Retorna el usuario autenticado
            } else {
                System.out.println("Intento de autenticación fallido: Contraseña incorrecta para usuario '" + username + "'.");
                 // En el controlador (LoginController), deberías poner un mensaje de error en el request antes de reenviar a la página.
                return null; // Contraseña incorrecta
            }
        } else {
            System.out.println("Intento de autenticación fallido: Username '" + username + "' no encontrado.");
             // En el controlador (LoginController), deberías poner un mensaje de error en el request antes de reenviar a la página.
            return null; // Usuario no encontrado
        }
    }

    /**
     * Intenta crear un nuevo usuario. Verifica si el username ya existe antes
     * de guardar. ¡Ahora HASHEA la contraseña usando BCrypt ANTES de guardar!
     *
     * @param usuario El objeto Usuario a crear (con password PLANA).
     * @return true si el usuario se creó con éxito, false si el username ya
     * existe o si hubo un error.
     */
    public boolean crearUsuario(Usuario usuario) {
        if (usuario == null || usuario.getUsername() == null || usuario.getUsername().trim().isEmpty() || usuario.getPassword() == null || usuario.getPassword().trim().isEmpty() || usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() || usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            System.err.println("Error de creación: Datos de usuario incompletos o vacíos.");
            return false;
        }

        // 1. Verificar si ya existe un usuario con ese username
        Usuario usuarioExistente = usuarioRepository.findByUsername(usuario.getUsername());

        if (usuarioExistente != null) {
            // El usuario ya existe, no se puede crear
            System.out.println("Intento de crear usuario fallido: Username '" + usuario.getUsername() + "' ya existe.");
            return false;
        } else {
            // El username no existe, proceder con la creación

            try {
                // *** ¡HASHEA LA CONTRASEÑA ANTES DE GUARDAR USANDO BCrypt! ***
                String hashedPassword = hashPassword(usuario.getPassword()); // <<-- Llama al método hashPassword (ahora con BCrypt)
                usuario.setPassword(hashedPassword); // Establece la contraseña HASHEADA

                usuarioRepository.save(usuario); // El save del repositorio maneja persist
                System.out.println("Usuario '" + usuario.getUsername() + "' creado con éxito.");
                return true;
            } catch (Exception e) {
                System.err.println("Error al guardar el nuevo usuario '" + usuario.getUsername() + "': " + e.getMessage());
                e.printStackTrace();
                return false; // Indica fallo general (DB, etc.)
            }
        }
    }

    /**
     * Busca un usuario por su nombre de usuario (username).
     *
     * @param username El username a buscar.
     * @return El Usuario encontrado, o null si no existe.
     */
    public Usuario obtenerUsuarioPorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Busca un usuario por su dirección de email.
     *
     * @param email El email a buscar.
     * @return El Usuario encontrado, o null si no existe.
     */
    public Usuario obtenerUsuarioPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        // El UsuarioRepository ya tiene el método findByEmail que añadimos
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Obtiene la lista de todos los usuarios en la base de datos.
     *
     * @return Una lista de Usuarios.
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Actualiza los datos de un usuario existente. ¡Si se actualiza la
     * contraseña, debe ser HASHEADA ANTES (si viene plana)!
     *
     * @param usuario El objeto Usuario con los datos actualizados.
     * (Asumimos que la contraseña en este objeto ya viene hasheada si se cambió).
     */
    public void actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getUsername() == null) {
            System.err.println("Error al actualizar usuario: Objeto usuario o username es null.");
            return;
        }
        // Nota: Si en el proceso de actualización la contraseña se envía plana,
        // DEBES hashearla aquí ANTES de guardarla en la DB.
        // Si ya viene hasheada (ej. la obtuviste de la DB y no la cambiaste), no necesitas hashear de nuevo.
        usuarioRepository.save(usuario); // El save del repositorio hace merge
        System.out.println("Usuario '" + usuario.getUsername() + "' actualizado.");
    }

    /**
     * Elimina un usuario por su nombre de usuario (username).
     *
     * @param username El username del usuario a eliminar.
     */
    public void eliminarUsuario(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Error al eliminar usuario: Username es null o vacío.");
            return;
        }
        // El repositorio ya tiene la lógica de buscar y eliminar
        usuarioRepository.delete(username);
        System.out.println("Solicitud de eliminación para usuario: " + username + " procesada.");
    }

    // --- Métodos de Recuperación de Contraseña ---
    /**
     * Procesa la solicitud de recuperación de contraseña por email. Busca
     * usuario por email, genera token, lo guarda, construye enlace y ENVÍA
     * EMAIL.
     *
     * @param email El email del usuario que solicita la recuperación.
     * @return El token guardado si el email existe y el proceso
     * (generación/guardado/envío) fue exitoso, de lo contrario null.
     */
    public PasswordRecoveryToken requestPasswordRecovery(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Solicitud de recuperación con email vacío.");
            return null;
        }

        // 1. Buscar al usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email);

        // Por seguridad, si el usuario no existe, no revelamos esa información al solicitante.
        // Aun así, simulamos el envío de email (sin enviar realmente) o mostramos el mismo mensaje genérico.
        // La opción de enviar el email solo si existe es más directa, que es la que seguimos aquí.
        if (usuario == null) {
            System.out.println("Solicitud de recuperación para email no encontrado: " + email + ". No se generó token ni se envió email.");
            return null; // No revelar si el email existe
        }

        // El usuario existe, proceder a generar token y enviar email
        // 2. Generar un token seguro
        String token = generateSecureToken(); // Método auxiliar

        // 3. Calcular fecha de expiración (ej. 1 hora = 60 minutos * 60 segundos * 1000 ms)
        Date expiryDate = calculateExpiryDate(60 * 60 * 1000); // Método auxiliar

        // 4. Crear y guardar el objeto token en la base de datos
        PasswordRecoveryToken recoveryToken = new PasswordRecoveryToken(token, usuario, expiryDate, false);

        try {
            tokenRepository.save(recoveryToken);
            System.out.println("Token de recuperación generado y guardado para usuario: " + usuario.getUsername() + " (" + token + ")");

            // 5. Construir el enlace de recuperación (Necesitas la base URL de tu aplicación)
            // ESTO ES COMPLEJO: La base URL (ej. http://localhost:8080/Java_Municipios)
            // no debería estar hardcodeada aquí. Debería pasarse desde el controlador
            // o leerse de un archivo de configuración.
            String baseUrl = "http://localhost:8080/Java_Municipios"; // <<-- AJUSTA ESTA BASE URL PARA TU SERVIDOR/APP
            String recoveryLink = baseUrl + "/resetPassword?token=" + token; // /resetPassword es el controlador que manejará este enlace GET

            // 6. ENVIAR EL EMAIL
            sendPasswordRecoveryEmail(email, recoveryLink); // Método auxiliar para enviar email

            return recoveryToken; // Retorna el token guardado si todo fue bien (email enviado)

        } catch (Exception e) { // Capturamos Exception general aquí para manejar errores de DB o Email
            System.err.println("Error en el proceso de solicitud de recuperación para usuario " + usuario.getUsername() + " (" + email + "): " + e.getMessage());
            e.printStackTrace();
            // !!! IMPORTANTE: Si el email falla (MessagingException), deberías eliminar el token de la DB !!!
            // Aquí simplemente retornamos null, pero lo ideal es borrar el token.
            // Puedes refactorizar para capturar MessagingException específicamente y borrar el token.
            // Ej:
            // try {
            //    tokenRepository.save(recoveryToken);
            //    sendPasswordRecoveryEmail(email, recoveryLink);
            //    return recoveryToken;
            // } catch (MessagingException e) {
            //    System.err.println("Error enviando email... ");
            //    tokenRepository.deleteByToken(token); // <-- Elimina el token si el email falla
            //    e.printStackTrace();
            //    return null; // Indica fallo
            // } catch (Exception e) { // Otros errores (DB al guardar token, etc.)
            //    System.err.println("Error guardando token u otro error... ");
            //    e.printStackTrace();
            //    return null; // Indica fallo
            // }

            return null; // Indica fallo (al guardar token o enviar email)
        }
    }

    /**
     * Valida un token de recuperación dado cuando el usuario hace clic en el
     * enlace. Busca el token, verifica que no esté usado y no haya expirado.
     *
     * @param tokenString El valor del token string a validar.
     * @return El Usuario asociado si el token es válido y activo, de lo
     * contrario null.
     */
    public Usuario validateRecoveryToken(String tokenString) {
        if (tokenString == null || tokenString.trim().isEmpty()) {
            System.out.println("Validación de token fallida: Token nulo o vacío.");
            return null;
        }
        // findByToken en TokenRepository ya hace las verificaciones de usado y expirado y busca el token
        PasswordRecoveryToken recoveryToken = tokenRepository.findByToken(tokenString);

        if (recoveryToken != null) {
            // El token es válido y activo según findByToken
            System.out.println("Validación de token exitosa para usuario: " + recoveryToken.getUser().getUsername());
            return recoveryToken.getUser(); // Devuelve el usuario asociado
        } else {
            // Token inválido, expirado o ya usado (findByToken retorna null)
            System.out.println("Validación de token fallida: Token '" + tokenString + "' inválido, expirado o usado.");
            return null;
        }
    }

    /**
     * Restablece la contraseña del usuario asociado a un token válido. Verifica
     * la validez del token de nuevo. ¡HASHEA la nueva contraseña! Marca el
     * token como usado.
     *
     * @param tokenString El valor del token string.
     * @param newPlainPassword La nueva contraseña (PLANA - ¡DEBES HASHEARLA!).
     * @return true si el restablecimiento fue exitoso, false si el token es
     * inválido o hubo error.
     */
    public boolean resetPassword(String tokenString, String newPlainPassword) {
        System.out.println("DEBUG RESET: Intento de restablecer password para token: " + tokenString); // Log inicio

        if (tokenString == null || tokenString.trim().isEmpty() || newPlainPassword == null || newPlainPassword.trim().isEmpty()) {
            System.err.println("DEBUG RESET: Error de restablecimiento: Token o nueva password vacíos.");
            return false; // Validación básica
        }

        // 1. Validar el token DE NUEVO (crucial por seguridad) justo antes de usarlo
        PasswordRecoveryToken recoveryToken = tokenRepository.findByToken(tokenString); // Reusa findByToken para validar estado y expiración

        if (recoveryToken == null) {
            System.err.println("DEBUG RESET: Restablecimiento fallido: Token '" + tokenString + "' inválido, expirado o usado. No se puede restablecer la contraseña.");
            return false; // Token inválido, expirado o ya usado
        }

        Usuario userToUpdate = recoveryToken.getUser();
        if (userToUpdate == null) {
            System.err.println("DEBUG RESET: Error interno: Token válido pero sin usuario asociado. Token: " + tokenString);
            // Considerar loggear esto como un error grave interno
            return false; // Error interno
        }

        // 2. Actualizar la contraseña del usuario
        try {
            // *** ¡HASHEA LA newPlainPassword USANDO BCrypt ANTES DE GUARDARLA! ***
            String hashedNewPassword = hashPassword(newPlainPassword); // <<-- Llama al método hashPassword (ahora con BCrypt)
            System.out.println("DEBUG RESET: Nueva password hasheada (NO LOG EN PROD): [NO MOSTRAR]"); // Log seguro
            System.out.println("DEBUG RESET: Usuario a actualizar: " + userToUpdate.getUsername()); // Log usuario

            userToUpdate.setPassword(hashedNewPassword); // Establece el password HASHEADO
            usuarioRepository.save(userToUpdate); // Guarda el usuario actualizado (save hace merge)
            System.out.println("DEBUG RESET: Usuario actualizado con nueva password."); // Log

            // 3. Marcar el token como usado INMEDIATAMENTE después del reset exitoso
            tokenRepository.markTokenAsUsed(tokenString);
            System.out.println("DEBUG RESET: Token marcado como usado: " + tokenString); // Log

            System.out.println("Contraseña restablecida con éxito para usuario: " + userToUpdate.getUsername() + " usando token " + tokenString);
            return true; // Éxito

        } catch (Exception e) {
            System.err.println("DEBUG RESET: Error al restablecer contraseña para usuario " + userToUpdate.getUsername() + " con token " + tokenString + ": " + e.getMessage());
            e.printStackTrace();
            // Manejar el error (DB al guardar, etc.)
            return false; // Fallo en el proceso
        }
    }

    // --- Implementación de los métodos de Hashing con BCrypt ---

    /**
     * Hashea una contraseña plana usando BCrypt.
     * Implementación segura con BCryptPasswordEncoder.
     * @param password La contraseña plana a hashear.
     * @return La contraseña hasheada (formato BCrypt).
     */
    private String hashPassword(String password) {
        // Usar el encoder BCrypt para hashear
        System.out.println("DEBUG HASH: Usando BCrypt para hashear password.");
        return this.passwordEncoder.encode(password);
    }

    /**
     * Verifica una contraseña plana contra una contraseña hasheada usando BCrypt.
     * Implementación segura con BCryptPasswordEncoder.
     * @param plainPassword La contraseña plana ingresada por el usuario.
     * @param hashedPassword La contraseña hasheada (formato BCrypt) almacenada en la base de datos.
     * @return true si la contraseña plana coincide con la hasheada, false de lo contrario.
     */
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
         // Usar el encoder BCrypt para verificar
         System.out.println("DEBUG VERIFY: Usando BCrypt para verificar password.");
         // BCrypt.matches retorna false si el hash almacenado es null, vacío o no es un hash BCrypt válido.
         if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
             System.out.println("DEBUG VERIFY: Hashed password from DB is null or empty.");
             return false;
         }
         return this.passwordEncoder.matches(plainPassword, hashedPassword);
    }


    // --- Métodos Auxiliares (generateSecureToken, calculateExpiryDate, sendPasswordRecoveryEmail) ---

    /**
     * Genera un token seguro y único para recuperación de contraseña.
     * Usa SecureRandom y UUID para mayor seguridad.
     * Ajusta la longitud según tu columna 'token' en la DB.
     */
     private String generateSecureToken() {
        // Ejemplo usando SecureRandom y UUID (mejor que solo UUID)
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 32 bytes = 256 bits de aleatoriedad
        random.nextBytes(bytes);
        String randomPart = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        // Combinar UUID con aleatoriedad extra para mayor unicidad y seguridad
        String token = UUID.randomUUID().toString().replace("-", "") + randomPart;
        // Ajusta la longitud final según el tamaño de tu columna 'token' en la base de datos (ej. VARCHAR(255) o más)
        // Si la longitud es muy larga, truncar o ajustar el tamaño de los bytes.
        // Un hash BCrypt es de 60 caracteres, tu token podría ser más largo.
        // Asegúrate de que la columna token en la DB es suficiente grande (ej. VARCHAR(255) o TEXT)
        if (token.length() > 255) { // Ejemplo: si tu columna es VARCHAR(255)
             token = token.substring(0, 255);
        }
        System.out.println("Generated Token (Secure): " + token); // Cambiado el log
        return token;
     }

    /**
     * Calcula la fecha de expiración.
     *
     * @param milliseconds validez en milisegundos desde ahora.
     * @return La fecha y hora de expiración.
     */
    private Date calculateExpiryDate(long milliseconds) {
        return new Date(System.currentTimeMillis() + milliseconds);
    }


    /**
     * Envía el email de recuperación de contraseña. Requiere configuración del
     * servidor SMTP y credenciales. DEBE MANEJAR LAS CREDENCIALES DE FORMA
     * SEGURA (NO HARDCODEADAS).
     *
     * @param recipientEmail La dirección de email del destinatario.
     * @param recoveryLink El enlace de recuperación que se incluirá en el
     * email.
     * @throws MessagingException Si ocurre un error durante el envío del email.
     */
     private void sendPasswordRecoveryEmail(String recipientEmail, String recoveryLink) throws MessagingException { // <<-- Lanza MessagingException

        // --- Configuración del servidor de email ---
        // **** DEBES CAMBIAR ESTOS VALORES ****
        // ** Idealmente, lee estos valores de forma SEGURA (variables de entorno, archivo de configuración externo) **
        final String username = "Diepiposa007@gmail.com"; // <-- Tu dirección de email remitente
        final String password = "pbhj byfb kygm qjkt"; // <-- La contraseña de tu email (¡USA CONFIGURACIÓN SEGURA!)
        final String smtpHost = "smtp.gmail.com"; // <-- El servidor SMTP de tu proveedor (ej. smtp.gmail.com para Gmail)
        final String smtpPort = "587"; // <-- El puerto del servidor SMTP (Comunes: 587 para STARTTLS, 465 para SSL)

        // ** Configura las propiedades según tu servidor SMTP **
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // Generalmente true

        // Propiedades Esenciales para Conexión
        props.put("mail.smtp.host", smtpHost); // <-- Indica el servidor SMTP
        props.put("mail.smtp.port", smtpPort); // <-- Indica el puerto


        // Configuración de seguridad (TLS o SSL)
        // Para Gmail en puerto 587, usamos STARTTLS (useTLS = true, useSSL = false)
        boolean useTLS = true; // <-- Ajusta a true si tu servidor SMTP usa STARTTLS en puerto 587
        boolean useSSL = false; // <-- Ajusta a true si tu servidor SMTP usa SSL directo en puerto 465

        if (useTLS) {
            props.put("mail.smtp.starttls.enable", "true"); // Habilitar STARTTLS

            // *** Intenta forzar el uso de protocolos TLS modernos para el handshake ***
            // Esto ayuda con el error "No appropriate protocol" en entornos modernos.
            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3"); // ¡Separados por ESPACIO!

            // En algunos entornos antiguos o con problemas de compatibilidad, puede ser útil
            // especificar la fábrica de sockets SSL. Con JDK 23 y Tomcat moderno, no suele ser necesario.
            // try {
            //     MailSSLSocketFactory sf = new MailSSLSocketFactory();
            //     sf.setTrustAllHosts(true); // ¡PELIGROSO en producción! Solo para depuración/pruebas si falla el certificado.
            //     props.put("mail.smtp.ssl.socketFactory", sf);
            // } catch (Exception e) {
            //     System.err.println("Error configuring SSL socket factory: " + e.getMessage());
            //     e.printStackTrace();
            // }

        }
        if (useSSL) { // Configuración para SSL directo en puerto 465 (menos común ahora)
            props.put("mail.smtp.ssl.enable", "true"); // Habilitar SSL/TLS en puerto dedicado
            // La lista de protocolos también iría aquí si aplicara
            // props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
            // props.put("mail.smtp.ssl.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // A veces necesario para SSL directo
        }

        // Debugging opcional: activa esto para ver la conversación entre tu app y el servidor SMTP
        props.put("mail.debug", "true"); // <<-- Asegúrate de que esté activa

        // Crear una sesión de email
        // Se autentica usando el username y password proporcionados
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                // Proporciona el usuario y la contraseña para la autenticación SMTP
                return new PasswordAuthentication(username, password);
            }
        });

        // El envío del mensaje DEBE estar dentro de un try-catch en el método que LLAMA
        // a sendPasswordRecoveryEmail (ej. requestPasswordRecovery), no dentro de este.
        // Aquí, si falla la creación del mensaje o el envío, la excepción MessagingException se lanzará.
        try {
            // Crear el mensaje de email
            Message message = new MimeMessage(session);

            // Establecer el remitente del email
            message.setFrom(new InternetAddress(username)); // La dirección "De" del email

            // Establecer el destinatario del email
            // InternetAddress.parse permite manejar múltiples destinatarios separados por coma, aunque aquí es solo uno
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));

            // Establecer el asunto del email
            message.setSubject("Recuperación de Contraseña"); // Asunto del correo

            // Cuerpo del email (puedes usar texto plano o HTML)
            // Usamos HTML para que el enlace sea clickeable
            String emailContent = "Hola,<br><br>"
                    + "Has solicitado restablecer tu contraseña.<br>"
                    + "Haz clic en el siguiente enlace para continuar:<br>"
                    + "<a href=\"" + recoveryLink + "\">" + recoveryLink + "</a><br><br>" // Incluye el enlace
                    + "Este enlace expirará pronto.<br><br>" // El tiempo de expiración lo defines en calculateExpiryDate (ej. 1 hora)
                    + "Si no solicitaste esto, ignora este email.<br>";

            // Establecer el contenido del mensaje como HTML
            message.setContent(emailContent, "text/html");

            // Enviar el mensaje. Si algo sale mal aquí, se lanza una MessagingException.
            Transport.send(message);

            System.out.println("Email de recuperación enviado con éxito a: " + recipientEmail);

        } catch (MessagingException e) {
            // Si ocurre un error al enviar el email (ej. conexión, autenticación, destinatario inválido)
            System.err.println("Error al enviar email de recuperación a " + recipientEmail + ": " + e.getMessage());
            e.printStackTrace(); // Imprimir el stack trace para depuración

            // ** CRUCIAL: RELANZAR LA EXCEPCIÓN **
            // Esto le dice al método que llamó (ej. requestPasswordRecovery) que el envío falló.
            // El método que llamó debe capturar esta excepción y manejarla (ej. eliminar el token de la DB).
            throw e;
        }
    }

    // Opcional: Método para cerrar los EntityManagerFactory si es necesario al finalizar la aplicación
    // public void closeFactories() {
    //     if (usuarioRepository != null) usuarioRepository.closeEntityManagerFactory();
    //     if (tokenRepository != null) tokenRepository.closeEntityManagerFactory();
    // }
}
