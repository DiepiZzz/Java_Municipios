package Model.Repository;

import Model.Entities.PasswordRecoveryToken;
import Model.Entities.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.Date;


public class PasswordRecoveryTokenRepository {

    private static final String PERSISTENCE_UNIT_NAME = "UnidadPersistencia";
    private EntityManagerFactory emf;

    public PasswordRecoveryTokenRepository() {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            System.out.println("EntityManagerFactory initialized for PasswordRecoveryTokenRepository.");
        } catch (Exception e) {
            System.err.println("Error initializing EntityManagerFactory for PasswordRecoveryTokenRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            System.out.println("EntityManagerFactory is null or closed. Attempting to re-initialize.");
            try {
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                System.out.println("EntityManagerFactory re-initialized for PasswordRecoveryTokenRepository.");
            } catch (Exception e) {
                System.err.println("Error re-initializing EntityManagerFactory for PasswordRecoveryTokenRepository: " + e.getMessage());
                e.printStackTrace();
                throw new IllegalStateException("EntityManagerFactory is not available and could not be re-initialized.");
            }
        }
        return emf.createEntityManager();
    }

    public void save(PasswordRecoveryToken token) {
        if (token == null) {
            System.err.println("Error saving token: Input token is null.");
            throw new IllegalArgumentException("Token cannot be null for saving.");
        }
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(token);
            em.getTransaction().commit();
            System.out.println("Password recovery token saved with ID: " + token.getToken());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error saving password recovery token with ID " + token.getToken() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (em != null) em.close();
        }
    }

    public PasswordRecoveryToken findByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.out.println("Search by token: Input token string is null or empty.");
            return null;
        }
        EntityManager em = getEntityManager();
        PasswordRecoveryToken foundToken = null;
        try {
            TypedQuery<PasswordRecoveryToken> query = em.createQuery(
                "SELECT t FROM PasswordRecoveryToken t JOIN FETCH t.user WHERE t.token = :token AND t.used = false AND t.expiryDate > :now",
                PasswordRecoveryToken.class);
            query.setParameter("token", token);
            query.setParameter("now", new Date());

            foundToken = query.getSingleResult();
            System.out.println("Token found by token (valid/unused/not expired query): " + (foundToken != null ? foundToken.getToken() : "null"));

        } catch (NoResultException e) {
            foundToken = null;
            System.out.println("No valid, unused, non-expired token found for string: " + token);
        } catch (Exception e) {
            System.err.println("Error finding password recovery token by token string '" + token + "': " + e.getMessage());
            e.printStackTrace();
            foundToken = null;
        } finally {
            if (em != null) em.close();
        }
        return foundToken;
    }

    public void markTokenAsUsed(String tokenString) {
        if (tokenString == null || tokenString.trim().isEmpty()) {
            System.err.println("Error marking token as used: Input token string is null or empty.");
            return;
        }
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            PasswordRecoveryToken tokenToUpdate = em.find(PasswordRecoveryToken.class, tokenString);
            if (tokenToUpdate != null) {
                tokenToUpdate.setUsed(true);
                em.merge(tokenToUpdate);
                em.getTransaction().commit();
                System.out.println("Token '" + tokenString + "' marked as used successfully.");
            } else {
                System.out.println("Token '" + tokenString + "' not found to mark as used. It might not exist or already be processed.");
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
            throw e;
        } finally {
            if (em != null) em.close();
        }
    }

    public void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory closed for PasswordRecoveryTokenRepository.");
        }
    }

}
