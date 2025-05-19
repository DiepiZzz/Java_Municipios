package ModelTest;

import Model.Entities.Municipio;
import Model.Repository.MunicipioRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

public class ModeloTestRun {

    public static void main(String[] args) {
        System.out.println("Iniciando pruebas manuales de MunicipioRepository...");

        try {
            testMunicipioRepository();
        } catch (Exception e) {
            System.err.println("Error durante las pruebas de MunicipioRepository: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Pruebas manuales de MunicipioRepository finalizadas.");
    }

    public static void testMunicipioRepository() throws Exception {
        System.out.println("\n--- Prueba Manual: Municipio Repository ---");

        MunicipioRepository repository = new MunicipioRepository();

        System.out.println("Setup: Limpiando tabla Municipios...");
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("UnidadPersistencia");
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Municipio").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error durante Setup/Limpieza de Municipios: " + e.getMessage());
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
        System.out.println("Setup: Tabla Municipios limpiada.");


        System.out.println("Prueba: Guardar y Buscar por ID...");
        Municipio nuevoMunicipio = new Municipio(
            "NombreMunicipioTest",
            "DepartamentoTest",
            "PaisTest",
            "AlcaldeTest",
            "GobernadorTest",
            "PatronoTest",
            10000,
            2000,
            5,
            10,
            "Descripcion de prueba"
        );

        repository.save(nuevoMunicipio);

        if (nuevoMunicipio.getId() == null) {
            System.err.println("FAIL: El ID del municipio no se generó después de guardar.");
        } else {
            System.out.println("PASS: Municipio guardado con ID: " + nuevoMunicipio.getId());
        }


        Municipio encontradoPorId = repository.findById(nuevoMunicipio.getId());

        if (encontradoPorId != null && "NombreMunicipioTest".equals(encontradoPorId.getNombre())) {
            System.out.println("PASS: FindById correcto.");
        } else {
            System.err.println("FAIL: FindById falló.");
        }


        System.out.println("Prueba: FindAll...");
        Municipio otroMunicipio = new Municipio("OtroNombre", "OtroDepto", "OtroPais", "OtroAlcalde", "OtroGobernador", "OtroPatrono", 5000, 1000, 2, 5, "Otra descripcion");
        repository.save(otroMunicipio);

        List<Municipio> todos = repository.findAll();

        if (todos != null && todos.size() == 2) {
            System.out.println("PASS: FindAll correcto (2 municipios).");
        } else {
            System.err.println("FAIL: FindAll incorrecto. Encontrados: " + (todos != null ? todos.size() : "null"));
        }

        System.out.println("Prueba: Eliminar...");
        if (nuevoMunicipio.getId() != null) {
            repository.deleteById(nuevoMunicipio.getId());
            Municipio eliminado = repository.findById(nuevoMunicipio.getId());
            if (eliminado == null) {
                System.out.println("PASS: Eliminar correcto.");
            } else {
                System.err.println("FAIL: Eliminar falló. El municipio aún existe.");
            }
        } else {
            System.err.println("FAIL: No se pudo probar eliminar porque el municipio de prueba no tenía ID.");
        }


        System.out.println("Teardown: Limpiando tabla Municipios...");
        try {
            emf = Persistence.createEntityManagerFactory("UnidadPersistencia");
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Municipio").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error durante Teardown/Limpieza final de Municipios: " + e.getMessage());
             if (em != null && em.getTransaction().isActive()) {
                 em.getTransaction().rollback();
             }
        } finally {
             if (em != null) em.close();
             if (emf != null) emf.close();
        }
        System.out.println("Teardown: Tabla Municipios limpiada.");


        System.out.println("--- Fin Prueba Manual: Municipio Repository ---");
    }
}
