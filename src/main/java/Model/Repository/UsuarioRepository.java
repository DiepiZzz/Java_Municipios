package Model.Repository; // Ajusta el paquete

import Model.Entities.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.List;

public class UsuarioRepository {

    // Usa el mismo nombre de la unidad de persistencia que tienes en persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "UnidadPersistencia";
    // Gestión básica de EMF/EM para este ejemplo manual.
    // En una aplicación real, usarías inyección o un patrón más sofisticado.
    private EntityManagerFactory emf;

    public UsuarioRepository() {
         try {
             emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
         } catch (Exception e) {
             System.err.println("Error initializing EntityManagerFactory for UsuarioRepository: " + e.getMessage());
             e.printStackTrace();
             // Considerar lanzar una excepción o manejar el error
         }
    }

    private EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
             // Re-inicializar si es null o está cerrado
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
            // Consulta JPQL para buscar por username
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class);
            query.setParameter("username", username);
            usuario = query.getSingleResult();
        } catch (NoResultException e) {
            // Si no se encuentra, retorna null (comportamiento esperado)
            usuario = null;
        } catch (Exception e) {
             // Otros errores durante la consulta
             System.err.println("Error finding user by username: " + e.getMessage());
             e.printStackTrace();
             usuario = null; // Asegurarse de retornar null o manejar el error
        } finally {
            em.close();
        }
        return usuario;
    }

    /**
     * Método para buscar un usuario por su dirección de email.
     * Necesario para la recuperación de contraseña.
     */
    public Usuario findByEmail(String email) {
         EntityManager em = getEntityManager();
        Usuario usuario = null;
        try {
            // Consulta JPQL para buscar por email
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
                 // Para Usuario, el ID/PK es el username, que nunca debería ser null si es nuevo
                 throw new IllegalArgumentException("Username cannot be null when saving a new Usuario.");
            }
             // findByUsername para saber si ya existe y decidir entre persist o merge
             // O confiar en que JPA lo manejará (merge si ya existe en contexto o DB)
             // Método más simple: merge. Si es nuevo, persist. Si ya existe, merge.
             // La forma más robusta implica verificar la existencia antes en el servicio o usar persist/merge condicional.
             // Vamos a usar merge, que funciona para ambos casos si la entidad está "detached".
            Usuario mergedUsuario = em.merge(usuario); // merge devuelve la entidad en estado managed

            em.getTransaction().commit();
            // Opcional: actualizar la referencia original si es importante que tenga estado managed
            // usuario.setUsername(mergedUsuario.getUsername()); // Ya lo tiene
            // usuario = mergedUsuario; // Si quieres que la referencia 'usuario' sea ahora managed
             System.out.println("Usuario saved/merged: " + mergedUsuario.getUsername());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error saving/merging user " + usuario.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-lanzar para manejo en la capa superior
        } finally {
            em.close();
        }
    }

     public void delete(String username) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            // Es necesario encontrar la entidad en el contexto actual para eliminarla
            Usuario usuarioAEliminar = em.find(Usuario.class, username);
            if (usuarioAEliminar != null) {
                em.remove(usuarioAEliminar);
                em.getTransaction().commit();
                 System.out.println("Usuario deleted: " + username);
            } else {
                System.out.println("User not found for deletion: " + username);
                 if (em.getTransaction().isActive()) {
                     em.getTransaction().rollback(); // Rollback si no se encontró
                 }
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error deleting user " + username + ": " + e.getMessage());
            e.printStackTrace();
             throw e; // Re-lanzar
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