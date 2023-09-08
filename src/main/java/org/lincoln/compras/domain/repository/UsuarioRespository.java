package org.lincoln.compras.domain.repository;

import org.lincoln.compras.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRespository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLogin(String login);
}
