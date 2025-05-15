
package Model.Repository;


import Model.Entities.Usuario;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class UsuarioRepository {

    private EntityManagerFactory emf;

    public UsuarioRepository() {
        // Asegúrate de que "miUnidadDePersistencia" coincide con el nombre en tu archivo persistence.xml
        emf = Persistence.createEntityManagerFactory("UnidadPersistencia");
    }

    public Usuario findByUsername(String username) {
        EntityManager em = emf.createEntityManager();
        Usuario usuario = em.find(Usuario.class, username);
        em.close();
        return usuario;
    }

    public List<Usuario> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        em.close();
        return usuarios;
    }

    public void save(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        if (usuario.getUsername() == null || findByUsername(usuario.getUsername()) == null) {
            em.persist(usuario); // Nuevo usuario
        } else {
            em.merge(usuario); // Usuario existente (actualizar)
        }
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        // Es necesario traer la entidad al contexto actual para eliminarla
        Usuario usuarioAEliminar = em.find(Usuario.class, usuario.getUsername());
        if (usuarioAEliminar != null) {
            em.remove(usuarioAEliminar);
        }
        em.getTransaction().commit();
        em.close();
    }

   
}
