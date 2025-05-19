
package Model.Repository;


import Model.Entities.Municipio;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class MunicipioRepository {

    private EntityManagerFactory emf;

    public MunicipioRepository() {
      
        emf = Persistence.createEntityManagerFactory("UnidadPersistencia");
    }

    public Municipio findById(Integer id) {
        EntityManager em = emf.createEntityManager();
        Municipio municipio = em.find(Municipio.class, id);
        em.close();
        return municipio;
    }

    public List<Municipio> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Municipio> municipios = em.createQuery("SELECT m FROM Municipio m", Municipio.class).getResultList();
        em.close();
        return municipios;
    }

    public void save(Municipio municipio) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        if (municipio.getId() == null) {
            em.persist(municipio); 
        } else {
            em.merge(municipio); 
        }
        em.getTransaction().commit();
        em.close();
    }

    public void deleteById(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Municipio municipio = em.find(Municipio.class, id);
        if (municipio != null) {
            em.remove(municipio);
        }
        em.getTransaction().commit();
        em.close();
    }

    
}
