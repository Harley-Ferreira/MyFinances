package com.easyall.myfinances.model.repository;

import com.easyall.myfinances.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    //Query methods findBy... findBy...And...
    Optional<Usuario> findByEmail(String email);
}
