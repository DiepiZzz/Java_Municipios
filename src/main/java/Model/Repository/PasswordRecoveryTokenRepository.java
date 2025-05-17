package Model.Repository; // Asegura que este es el nombre correcto de tu paquete de repositorios

import Model.Entities.PasswordRecoveryToken; // Importar la entidad Token
import Model.Entities.Usuario; // Necesario para el fetch join en findByToken

// Importaciones de JPA - Usar javax.persistence para JPA 2.2
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.Date; // Para comparar fechas


public class PasswordRecoveryTokenRepository {

    // Usa el mismo nombre de la unidad de persistencia que tienes en persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "UnidadPersistencia";
    // Gestión básica de EMF. En una aplicación real, usarías inyección o un patrón más avanzado.
    private EntityManagerFactory emf;

    public PasswordRecoveryTokenRepository() {
         try {
             // Intenta crear el EMF. Esto puede fallar si persistence.xml no se encuentra o es inválido.
             emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
              System.out.println("EntityManagerFactory initialized for PasswordRecoveryTokenRepository.");
         } catch (Exception e) {
             System.err.println("Error initializing EntityManagerFactory for PasswordRecoveryTokenRepository: " + e.getMessage());
             e.printStackTrace();
             // En una aplicación real, podrías querer relanzar una excepción
             // o tener un mecanismo para indicar que la capa de persistencia no está disponible.
             // Aquí, simplemente logeamos y emf quedará null.
         }
    }

    /**
     * Obtiene una instancia de EntityManager.
     * Lanza IllegalStateException si el EMF no se inicializó correctamente.
     */
    private EntityManager getEntityManager() {
        // Verifica si el EMF es nulo o está cerrado y intenta re-inicializar (básico)
        if (emf == null || !emf.isOpen()) {
             System.out.println("EntityManagerFactory is null or closed. Attempting to re-initialize.");
             try {
                 emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                  System.out.println("EntityManagerFactory re-initialized for PasswordRecoveryTokenRepository.");
             } catch (Exception e) {
                  System.err.println("Error re-initializing EntityManagerFactory for PasswordRecoveryTokenRepository: " + e.getMessage());
                  e.printStackTrace();
                  // Si falla la re-inicialización, lanzamos una excepción.
                  throw new IllegalStateException("EntityManagerFactory is not available and could not be re-initialized.");
             }
        }
         // Si EMF está bien, crea y retorna un nuevo EntityManager
        return emf.createEntityManager();
    }

    /**
     * Guarda un nuevo token de recuperación en la base de datos.
     * Usa persist() ya que el token es la PK y se genera en el servicio.
     * @param token El objeto PasswordRecoveryToken a guardar.
     */
    public void save(PasswordRecoveryToken token) {
        if (token == null) {
             System.err.println("Error saving token: Input token is null.");
             throw new IllegalArgumentException("Token cannot be null for saving.");
        }
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            // persist() se usa para nuevas entidades. Si un token con el mismo ID ya existe, lanzará una excepción.
            em.persist(token);
            em.getTransaction().commit();
             System.out.println("Password recovery token saved with ID: " + token.getToken());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error saving password recovery token with ID " + token.getToken() + ": " + e.getMessage());
            e.printStackTrace();
             // Relanzar la excepción para que la capa de servicio la maneje (ej. para reportar fallo al usuario)
             throw e;
        } finally {
             if (em != null) em.close();
        }
    }

     /**
     * Busca un token válido por su valor de token string.
     * Un token es válido si existe, no está marcado como usado y no ha expirado.
     * @param token El valor del token string a buscar.
     * @return El objeto PasswordRecoveryToken si se encuentra un token válido y activo, de lo contrario null.
     */
    public PasswordRecoveryToken findByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.out.println("Search by token: Input token string is null or empty.");
            return null;
        }
        EntityManager em = getEntityManager();
        PasswordRecoveryToken foundToken = null;
        try {
             // Consulta JPQL: Selecciona el token y FETCH (carga) el usuario asociado para evitar N+1
             // Condiciones: token coincide, no está usado (used=false), y no ha expirado (expiryDate > ahora)
             TypedQuery<PasswordRecoveryToken> query = em.createQuery(
                "SELECT t FROM PasswordRecoveryToken t JOIN FETCH t.user WHERE t.token = :token AND t.used = false AND t.expiryDate > :now",
                PasswordRecoveryToken.class);
            query.setParameter("token", token);
            query.setParameter("now", new Date()); // Compara con la fecha y hora actual del servidor

            foundToken = query.getSingleResult();
            // Si getSingleResult() no encuentra resultados, lanza NoResultException, que capturamos.
            System.out.println("Token found by token (valid/unused/not expired query): " + (foundToken != null ? foundToken.getToken() : "null"));

        } catch (NoResultException e) {
            // No se encontró un token que cumpla todas las condiciones (válido, no usado, no expirado)
            foundToken = null; // Asegurarse de que sea null
            System.out.println("No valid, unused, non-expired token found for string: " + token);
        } catch (Exception e) {
             System.err.println("Error finding password recovery token by token string '" + token + "': " + e.getMessage());
             e.printStackTrace();
             // Otros errores (DB, sintaxis JPQL, etc.)
             foundToken = null; // Asegurarse de que sea null en caso de error
        } finally {
            if (em != null) em.close();
        }
        return foundToken;
    }

     /**
     * Marca un token específico como usado después de que la contraseña ha sido restablecida con éxito.
     * @param tokenString El valor del token string a marcar como usado.
     */
     public void markTokenAsUsed(String tokenString) {
        if (tokenString == null || tokenString.trim().isEmpty()) {
            System.err.println("Error marking token as used: Input token string is null or empty.");
            return; // No se puede marcar un token nulo/vacío
        }
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
             // Encontrar la entidad Token por su PK (el string del token)
             PasswordRecoveryToken tokenToUpdate = em.find(PasswordRecoveryToken.class, tokenString);
             if (tokenToUpdate != null) {
                 tokenToUpdate.setUsed(true); // Establecer a true
                 // merge no es estrictamente necesario aquí si la entidad está en estado 'managed',
                 // pero es seguro si pudiera estar en estado 'detached'.
                 em.merge(tokenToUpdate);
                 em.getTransaction().commit();
                  System.out.println("Token '" + tokenString + "' marked as used successfully.");
             } else {
                  // El token no fue encontrado en la DB (quizás ya expiró, fue borrado, o el string era incorrecto)
                  System.out.println("Token '" + tokenString + "' not found to mark as used. It might not exist or already be processed.");
                  // Rollback la transacción si estaba activa pero no se encontró el token
                  if (em.getTransaction().isActive()) {
                     em.getTransaction().rollback();
                 }
             }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error marking token '" + tokenString + "' as used: " + e.getMessage());
            e.printStackTrace();
             // Relanzar la excepción para manejar el fallo en la capa superior
             throw e;
        } finally {
             if (em != null) em.close();
        }
    }

     // Opcional: método para eliminar tokens expirados periódicamente
     // public void cleanExpiredTokens() { ... }

    /**
     * Cierra el EntityManagerFactory.
     * Idealmente llamado al finalizar la aplicación o en un hook de destrucción.
     */
    public void closeEntityManagerFactory() {
         if (emf != null && emf.isOpen()) {
             emf.close();
             System.out.println("EntityManagerFactory closed for PasswordRecoveryTokenRepository.");
         }
    }

}