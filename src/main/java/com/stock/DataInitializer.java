package com.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.stock.entidades.Usuario;
import com.stock.repositorio.UsuarioRepository;
import com.stock.utils.reporte.Rol;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si existe el usuario admin
        Usuario admin = usuarioRepository.findByUsuario("admin");
        
        if (admin == null) {
            System.out.println("--- CREANDO USUARIO ADMINISTRADOR POR DEFECTO ---");
            admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setUsuario("admin");
            admin.setEmail("admin@stock.com");
            admin.setPassword(passwordEncoder.encode("admin")); // Contraseña: admin
            admin.setRol(Rol.ADMIN);
            admin.setActivo(true);
            
            usuarioRepository.save(admin);
            System.out.println("--- USUARIO ADMIN CREADO: usuario='admin', password='admin' ---");
        } else {
            System.out.println("--- USUARIO ADMIN YA EXISTE ---");
        }
    }
}
