package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByRol_Id(Long rolId);
}