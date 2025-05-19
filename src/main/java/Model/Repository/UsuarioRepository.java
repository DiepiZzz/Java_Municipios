package Model.Repository;

import Model.Entities.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.List;

public class UsuarioRepository {

    private static final String PERSISTENCE_UNIT_NAME = "UnidadPersistencia";
    private EntityManagerFactory emf;

    public UsuarioRepository() {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            System.err.println("Error initializing EntityManagerFactory for UsuarioRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            try {
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            } catch (Exception e) {
                System.err.println("Error re-initializing EntityManagerFactory for UsuarioRepository: " + e.getMessage());
                e.printStackTrace();
                throw new IllegalStateException("EntityManagerFactory is not available.");
            }
        }
        return emf.createEntityManager();
    }


    public Usuario findByUsername(String username) {
        EntityManager em = getEntityManager();
        Usuario usuario = null;
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class);
            query.setParameter("username", username);
            usuario = query.getSingleResult();
        } catch (NoResultException e) {
            usuario = null;
        } catch (Exception e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
            usuario = null;
        } finally {
            em.close();
        }
        return usuario;
    }

    public Usuario findByEmail(String email) {
        EntityManager em = getEntityManager();
        Usuario usuario = null;
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
            query.setParameter("email", email);
            usuario = query.getSingleResult();
        } catch (NoResultException e) {
            usuario = null;
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            e.printStackTrace();
            usuario = null;
        } finally {
            em.close();
        }
        return usuario;
    }


    public List<Usuario> findAll() {
        EntityManager em = getEntityManager();
        List<Usuario> usuarios = null;
        try {
            usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        } catch (Exception e) {
            System.err.println("Error finding all users: " + e.getMessage());
            e.printStackTrace();
            usuarios = null;
        } finally {
            em.close();
        }
        return usuarios;
    }

    public void save(Usuario usuario) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            if (usuario.getUsername() == null) {
                throw new IllegalArgumentException("Username cannot be null when saving a new Usuario.");
            }
            Usuario mergedUsuario = em.merge(usuario);

            em.getTransaction().commit();
            System.out.println("Usuario saved/merged: " + mergedUsuario.getUsername());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error saving/merging user " + usuario.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(String username) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            Usuario usuarioAEliminar = em.find(Usuario.class, username);
            if (usuarioAEliminar != null) {
                em.remove(usuarioAEliminar);
                em.getTransaction().commit();
                System.out.println("Usuario deleted: " + username);
            } else {
                System.out.println("User not found for deletion: " + username);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error deleting user " + username + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

    public void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
