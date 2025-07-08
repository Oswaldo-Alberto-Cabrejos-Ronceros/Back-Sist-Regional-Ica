package com.clinicaregional.clinica.repository;

import com.clinicaregional.clinica.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByRol_Id(Long rolId);

    // para obtener usuario por correo
    Optional<Usuario> findByCorreo(String correo);

    // si existe por correo
    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndEstadoIsTrue(String correo1String);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.correo = :correo AND u.estado = true")
    Optional<Usuario> findByCorreoWithRol(@Param("correo") String correo);

    Optional<Usuario> findByIdAndEstadoIsTrue(Long id);
}