
package Model.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Usuario") 
public class Usuario {

    @Id 
    private String username;

    private String password;
    private String nombre;
    private String email;

   
    public Usuario() {
    }

   
    public Usuario(String username, String password, String nombre, String email) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.email = email;
    }

   
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

   
}