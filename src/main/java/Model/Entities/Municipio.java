
package Model.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Municipios") 
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    private String nombre;
    private String departamento;
    private String pais; 
    private String alcalde;
    private String gobernador;
    private String patronoReligioso;
    private Integer numHabitantes;
    private Integer numCasas;
    private Integer numParques;
    private Integer numColegios;
    private String descripcion;

   
    public Municipio() {
    }

   
    public Municipio(String nombre, String departamento, String pais, String alcalde, String gobernador, String patronoReligioso, Integer numHabitantes, Integer numCasas, Integer numParques, Integer numColegios, String descripcion) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.pais = pais;
        this.alcalde = alcalde;
        this.gobernador = gobernador;
        this.patronoReligioso = patronoReligioso;
        this.numHabitantes = numHabitantes;
        this.numCasas = numCasas;
        this.numParques = numParques;
        this.numColegios = numColegios;
        this.descripcion = descripcion;
    }

   
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getAlcalde() {
        return alcalde;
    }

    public void setAlcalde(String alcalde) {
        this.alcalde = alcalde;
    }

    public String getGobernador() {
        return gobernador;
    }

    public void setGobernador(String gobernador) {
        this.gobernador = gobernador;
    }

    public String getPatronoReligioso() {
        return patronoReligioso;
    }

    public void setPatronoReligioso(String patronoReligioso) {
        this.patronoReligioso = patronoReligioso;
    }

    public Integer getNumHabitantes() {
        return numHabitantes;
    }

    public void setNumHabitantes(Integer numHabitantes) {
        this.numHabitantes = numHabitantes;
    }

    public Integer getNumCasas() {
        return numCasas;
    }

    public void setNumCasas(Integer numCasas) {
        this.numCasas = numCasas;
    }

    public Integer getNumParques() {
        return numParques;
    }

    public void setNumParques(Integer numParques) {
        this.numParques = numParques;
    }

    public Integer getNumColegios() {
        return numColegios;
    }

    public void setNumColegios(Integer numColegios) {
        this.numColegios = numColegios;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

   
}