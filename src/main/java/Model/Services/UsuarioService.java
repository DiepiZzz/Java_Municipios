
package Model.Services;

import Model.Entities.Usuario;
import Model.Repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository(); 
    }

    public Usuario autenticar(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario != null && usuario.getPassword().equals(password)) { 
            return usuario;
        }
        return null; 
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

  public boolean crearUsuario(Usuario usuario) { // <<-- CAMBIADO DE void A boolean

        // 1. Verificar si ya existe un usuario con ese username
        // Usamos obtenerUsuarioPorUsername() que ya tienes
        Usuario usuarioExistente = obtenerUsuarioPorUsername(usuario.getUsername());


        if (usuarioExistente != null) {
            // El usuario ya existe, no se puede crear
            System.out.println("Intento de crear usuario fallido: Username '" + usuario.getUsername() + "' ya existe.");
            return false; // <<-- Retorna false si el username ya existe
        } else {
            // El username no existe, proceder con la creación
            try {
                usuarioRepository.save(usuario); // El save del repositorio maneja persist o merge. Para nuevo, será persist.
                System.out.println("Usuario '" + usuario.getUsername() + "' creado con éxito.");
                return true; // <<-- Retorna true si se guarda con éxito
            } catch (Exception e) {
                // Manejar otros posibles errores de persistencia durante el save
                System.err.println("Error al guardar el nuevo usuario '" + usuario.getUsername() + "': " + e.getMessage());
                e.printStackTrace();
                // Podrías querer loggear la excepción o manejarla de forma más sofisticada
                return false; // <<-- Retorna false en caso de otro error de persistencia
            }
        }
    }

    public void actualizarUsuario(Usuario usuario) {
         
         usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(String username) {
         Usuario usuarioAEliminar = usuarioRepository.findByUsername(username);
         if (usuarioAEliminar != null) {
             usuarioRepository.delete(usuarioAEliminar);
         } else {
             System.out.println("Error: El usuario con username " + username + " no existe.");
         }
    }

    
}
