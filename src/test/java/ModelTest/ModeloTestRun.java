/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModelTest;

import Model.Entities.Municipio; // Asegúrate de que la importación a tu entidad sea correcta
import Model.Repository.MunicipioRepository; // Asegúrate de que la importación a tu repositorio sea correcta

// Importaciones de JPA - Usar javax.persistence para JPA 2.2
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

/**
 * Clase de prueba manual para MunicipioRepository.
 * Requiere una base de datos PostgreSQL en ejecución
 * y el archivo persistence.xml correctamente configurado
 * en src/test/resources/META-INF/
 */
public class ModeloTestRun {

    // Método principal para ejecutar la prueba manual
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

    // Método de prueba manual para MunicipioRepository (Requiere DB)
    public static void testMunicipioRepository() throws Exception {
        System.out.println("\n--- Prueba Manual: Municipio Repository ---");

        MunicipioRepository repository = new MunicipioRepository(); // Instanciar el repositorio

        // --- Setup: Limpiar datos ---
        System.out.println("Setup: Limpiando tabla Municipios...");
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory("UnidadPersistencia"); // *** Usar el nombre correcto de tu PU ***
            em = emf.createEntityManager();
            em.getTransaction().begin();
            // Asegúrate de que "Municipio" es el nombre de la entidad/tabla JPA si es diferente
            em.createQuery("DELETE FROM Municipio").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error durante Setup/Limpieza de Municipios: " + e.getMessage());
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Re-lanzar la excepción si la limpieza falla
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
        System.out.println("Setup: Tabla Municipios limpiada.");


        // --- Prueba: Guardar y Buscar por ID ---
        System.out.println("Prueba: Guardar y Buscar por ID...");
        Municipio nuevoMunicipio = new Municipio(
            "NombreMunicipioTest",
            "DepartamentoTest",
            "PaisTest",
            "AlcaldeTest",
            "GobernadorTest",
            "PatronoTest",
            10000, // numHabitantes
            2000, // numCasas
            5,    // numParques
            10,   // numColegios
            "Descripcion de prueba"
            // El ID se generará automáticamente si usas GenerationType.IDENTITY
        );

        repository.save(nuevoMunicipio); // Ejecutar el método a probar (debería persistir)

        // Verificar que se le asignó un ID si es autogenerado
        if (nuevoMunicipio.getId() == null) {
             System.err.println("FAIL: El ID del municipio no se generó después de guardar.");
        } else {
             System.out.println("PASS: Municipio guardado con ID: " + nuevoMunicipio.getId());
        }


        // Buscar el municipio usando el repositorio
        Municipio encontradoPorId = repository.findById(nuevoMunicipio.getId());

        if (encontradoPorId != null && "NombreMunicipioTest".equals(encontradoPorId.getNombre())) {
             System.out.println("PASS: FindById correcto.");
        } else {
             System.err.println("FAIL: FindById falló.");
        }


        // --- Prueba: FindAll ---
        System.out.println("Prueba: FindAll...");
        // Guardamos otro municipio para probar FindAll
        Municipio otroMunicipio = new Municipio("OtroNombre", "OtroDepto", "OtroPais", "OtroAlcalde", "OtroGobernador", "OtroPatrono", 5000, 1000, 2, 5, "Otra descripcion");
        repository.save(otroMunicipio);

        List<Municipio> todos = repository.findAll();

        if (todos != null && todos.size() == 2) { // Deberíamos tener 2 municipios guardados
             System.out.println("PASS: FindAll correcto (2 municipios).");
        } else {
             System.err.println("FAIL: FindAll incorrecto. Encontrados: " + (todos != null ? todos.size() : "null"));
        }

        // --- Prueba: Eliminar ---
         System.out.println("Prueba: Eliminar...");
         if (nuevoMunicipio.getId() != null) { // Aseguramos que tiene ID antes de intentar eliminar
             repository.delete(nuevoMunicipio.getId()); // Ejecutar el método de eliminar
             // Verificar que ya no existe
             Municipio eliminado = repository.findById(nuevoMunicipio.getId());
             if (eliminado == null) {
                 System.out.println("PASS: Eliminar correcto.");
             } else {
                 System.err.println("FAIL: Eliminar falló. El municipio aún existe.");
             }
         } else {
             System.err.println("FAIL: No se pudo probar eliminar porque el municipio de prueba no tenía ID.");
         }


        // --- Teardown: Limpiar datos finales ---
        System.out.println("Teardown: Limpiando tabla Municipios...");
         try {
             emf = Persistence.createEntityManagerFactory("UnidadPersistencia"); // Usar la misma PU
             em = emf.createEntityManager();
             em.getTransaction().begin();
             em.createQuery("DELETE FROM Municipio").executeUpdate(); // Limpiar de nuevo
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