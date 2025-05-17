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

public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private PasswordRecoveryTokenRepository tokenRepository; // Repositorio de tokens

    // Usamos el mismo nombre de la unidad de persistencia solo como referencia.
    // La gestión real de EMF/EM está en los repositorios en este ejemplo.
    // private static final String PERSISTENCE_UNIT_NAME = "UnidadPersistencia";
    /**
     * Constructor de la clase de servicio. Inicializa los repositorios que
     * utilizará.
     */
    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
        this.tokenRepository = new PasswordRecoveryTokenRepository(); // Instanciar el repositorio de tokens
    }

    /**
     * Intenta autenticar un usuario con username y password. ¡ASUMIMOS que el
     * password guardado en DB está HASHEADO!
     *
     * @param username El nombre de usuario ingresado.
     * @param password La contraseña PLANA ingresada.
     * @return El objeto Usuario autenticado si las credenciales son correctas,
     * de lo contrario null.
     */
    public Usuario autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Intento de autenticación fallido: Username o password nulos/vacíos.");
            return null;
        }

        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario != null) {
            // Aquí debes COMPARAR el 'password' PLANA ingresada con el password HASHEADA de la DB.
            // ¡¡¡NUNCA COMPARES passwords planas o hashes si no usas una LIBRERÍA segura!!!
            // Necesitas la MISMA librería de hashing que usas para crear/resetear.

            // Ejemplo CONCEPTUAL (NECESITAS IMPLEMENTACIÓN SEGURA USANDO UNA LIBRERÍA)
            if (verifyPassword(password, usuario.getPassword())) { // <<-- USA TU MÉTODO DE VERIFICACIÓN DE HASHING SEGURO
                System.out.println("Usuario '" + username + "' autenticado con éxito.");
                return usuario; // Retorna el usuario autenticado
            } else {
                System.out.println("Intento de autenticación fallido: Contraseña incorrecta para usuario '" + username + "'.");
                return null; // Contraseña incorrecta
            }
        } else {
            System.out.println("Intento de autenticación fallido: Username '" + username + "' no encontrado.");
            return null; // Usuario no encontrado
        }
    }

    /**
     * Intenta crear un nuevo usuario. Verifica si el username ya existe antes
     * de guardar. ¡HASHEA la contraseña ANTES de guardar!
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
                // ¡¡¡HASHEA LA CONTRASEÑA ANTES DE GUARDAR!!!
                String hashedPassword = hashPassword(usuario.getPassword()); // <<-- USAR TU MÉTODO DE HASHING SEGURO
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
     * contraseña, debe ser HASHEADA ANTES!
     *
     * @param usuario El objeto Usuario con los datos actualizados.
     */
    public void actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getUsername() == null) {
            System.err.println("Error al actualizar usuario: Objeto usuario o username es null.");
            return;
        }
        // Lógica para hash de password si se está actualizando:
        // Necesitas saber si el password en el objeto 'usuario' es el plano nuevo o el hash viejo.
        // Un enfoque común es tener un DTO o un método setPasswordFromPlain() que hashee.
        // Aquí, asumimos que si llamas a este método, la password ya viene preparada (ej. hasheada si se cambió)
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

        } catch (Exception e) {
            System.err.println("Error en el proceso de solicitud de recuperación para usuario " + usuario.getUsername() + " (" + email + "): " + e.getMessage());
            e.printStackTrace();
            // Manejar el error: loggear, no re-lanzar si no quieres que falle la solicitud visiblemente
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
     * @param newPassword La nueva contraseña (PLANA - ¡DEBES HASHEARLA!).
     * @return true si el restablecimiento fue exitoso, false si el token es
     * inválido o hubo error.
     */
    public boolean resetPassword(String tokenString, String newPassword) {
        if (tokenString == null || tokenString.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            System.err.println("Error de restablecimiento: Token o nueva contraseña vacíos.");
            return false; // Validación básica
        }

        // 1. Validar el token DE NUEVO (crucial por seguridad) justo antes de usarlo
        PasswordRecoveryToken recoveryToken = tokenRepository.findByToken(tokenString); // Reusa findByToken para validar estado y expiración

        if (recoveryToken == null) {
            System.err.println("Restablecimiento fallido: Token '" + tokenString + "' inválido, expirado o usado. No se puede restablecer la contraseña.");
            return false; // Token inválido, expirado o ya usado
        }

        Usuario userToUpdate = recoveryToken.getUser();
        if (userToUpdate == null) {
            System.err.println("Error interno: Token válido pero sin usuario asociado. Token: " + tokenString);
            // Considerar loggear esto como un error grave interno
            return false; // Error interno
        }

        // 2. Actualizar la contraseña del usuario
        try {
            // ¡¡¡AQUÍ DEBES HASHEAR LA newPassword ANTES DE GUARDARLA!!!
            // Usa el MISMO MÉTODO y LIBRERÍA de hashing que en crearUsuario.
            String hashedPassword = hashPassword(newPassword); // <<-- USAR TU MÉTODO DE HASHING SEGURO

            userToUpdate.setPassword(hashedPassword); // Establece el password HASHEADO
            usuarioRepository.save(userToUpdate); // Guarda el usuario actualizado (save hace merge)

            // 3. Marcar el token como usado INMEDIATAMENTE después del reset exitoso
            tokenRepository.markTokenAsUsed(tokenString);

            System.out.println("Contraseña restablecida con éxito para usuario: " + userToUpdate.getUsername() + " usando token " + tokenString);
            return true; // Éxito

        } catch (Exception e) {
            System.err.println("Error al restablecer contraseña para usuario " + userToUpdate.getUsername() + " con token " + tokenString + ": " + e.getMessage());
            e.printStackTrace();
            // Manejar el error (DB al guardar, etc.)
            return false; // Fallo en el proceso
        }
    }

    // --- Métodos Auxiliares (DEBES IMPLEMENTAR o SUSTITUIR POR UNA LIBRERÍA) ---
    /**
     * Genera un token seguro y único. DEBE usar un generador de números
     * aleatorios criptográficamente seguro. El token debe ser suficientemente
     * largo para ser impredecible. La longitud y el formato deben coincidir con
     * lo que tu columna 'token' en la DB pueda almacenar.
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
        if (token.length() > 255) { // Ejemplo: si tu columna es VARCHAR(255)
            token = token.substring(0, 255);
        }
        System.out.println("Generated Token (Placeholder): " + token);
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
     * HASHEA la contraseña usando un algoritmo seguro (ej. bcrypt, Argon2).
     * NUNCA Almacenar contraseñas planas en la DB. NECESITAS UNA LIBRERÍA DE
     * HASHING SEGURA (ej. Spring Security Crypto, Apache Shiro Crypto, jBCrypt,
     * PBKDF2 con una implementación segura).
     *
     * @param password La contraseña plana a hashear.
     * @return El hash de la contraseña.
     */
    private String hashPassword(String password) {
        // ESTO ES UN PLACEHOLDER NO SEGURO. SUSTITUYE POR UNA LIBRERÍA REAL.
        System.out.println("ADVERTENCIA: Usando hashing placeholder. ¡Implementa hashing seguro!");
        // Un placeholder muy básico que NO ES SEGURO para producción:
        // Simplemente prefijamos para que sepamos que NO está hasheada correctamente.
        return "{UNSAFE_HASH_PLACEHOLDER}" + password;
    }

    /**
     * VERIFICA si una contraseña PLANA coincide con un hash ALMACENADO. Debe
     * usar la MISMA LIBRERÍA y algoritmo que hashPassword.
     *
     * @param plainPassword La contraseña plana ingresada por el usuario.
     * @param hashedPassword El hash de la contraseña almacenado en la base de
     * datos.
     * @return true si la contraseña plana coincide con el hash, false de lo
     * contrario.
     */
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        // ESTO ES UN PLACEHOLDER NO SEGURO. SUSTITUYE POR UNA LIBRERÍA REAL.
        System.out.println("ADVERTENCIA: Usando verificación de hashing placeholder. ¡Implementa verificación segura!");
        // Para el placeholder "{UNSAFE_HASH_PLACEHOLDER}" + password
        return hashedPassword != null && hashedPassword.equals("{UNSAFE_HASH_PLACEHOLDER}" + plainPassword);

        // Para hashing real, la lógica sería diferente, ej: bcrypt.checkpw(plainPassword, hashedPassword)
    }

    /**
     * Envía el email de recuperación de contraseña. Requiere configuración del
     * servidor SMTP y credenciales. DEBE MANEJAR LAS CREDENCIALES DE FORMA
     * SEGURA (NO HARDCODEADAS).
     *
     * @param recipientEmail La dirección de email del destinatario.
     * @param recoveryLink El enlace de recuperación que se incluirá en el
     * email.
     */
    private void sendPasswordRecoveryEmail(String recipientEmail, String recoveryLink) throws MessagingException { // <<-- Ahora lanza MessagingException

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

        // Configuración de seguridad (TLS o SSL)
        boolean useTLS = true; // <-- Ajusta a true si tu servidor SMTP usa STARTTLS en puerto 587
        boolean useSSL = false; // <-- Ajusta a true si tu servidor SMTP usa SSL directo en puerto 465

        if (useTLS) {
            props.put("mail.smtp.starttls.enable", "true"); // Habilitar STARTTLS
            props.put("mail.smtp.port", smtpPort); // Puerto para STARTTLS (normalmente 587)
        }
        if (useSSL) {
            props.put("mail.smtp.ssl.enable", "true"); // Habilitar SSL/TLS en puerto dedicado
            props.put("mail.smtp.port", smtpPort); // Puerto para SSL (normalmente 465)
            // props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Opcional: especificar protocolo TLS
            // Si usas 465 y falla, puede que necesites la siguiente línea (para Tomcat/GlassFish viejos a veces)
            // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        // Debugging opcional: activa esto para ver la conversación entre tu app y el servidor SMTP
            props.put("mail.debug", "true");

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
