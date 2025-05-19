package Model.Services;

import Model.Entities.Municipio;
import Model.Repository.MunicipioRepository;

import java.util.List;

public class MunicipioService {

    private MunicipioRepository municipioRepository;

    public MunicipioService() {
        this.municipioRepository = new MunicipioRepository();
    }

    public Municipio obtenerMunicipioPorId(Integer id) {
        return municipioRepository.findById(id);
    }

    public List<Municipio> obtenerTodosLosMunicipios() {
        return municipioRepository.findAll();
    }

    public void crearMunicipio(Municipio municipio) {

        municipioRepository.save(municipio);
    }

    public void actualizarMunicipio(Municipio municipio) {
         if (municipio == null || municipio.getId() == null) {
             System.err.println("Error updating municipio: Municipio object or ID is invalid.");
             throw new IllegalArgumentException("Municipio object and ID cannot be null for updating.");
         }
         System.out.println("DEBUG Service: Updating municipio: " + municipio.getNombre() + " (ID: " + municipio.getId() + ")");
         municipioRepository.save(municipio);
    }

    public void eliminarMunicipio(Integer id) {

        municipioRepository.deleteById(id);
    }
}
