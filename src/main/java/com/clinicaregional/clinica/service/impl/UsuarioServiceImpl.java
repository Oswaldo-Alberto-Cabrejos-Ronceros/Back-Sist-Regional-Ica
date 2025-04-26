package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.UsuarioRequestDTO;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.mapper.UsuarioMapper;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.repository.RolRepository;
import com.clinicaregional.clinica.service.UsuarioService;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl extends FiltroEstado implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UsuarioDTO> listarUsuarios() {
        activarFiltroEstado(true);
        return usuarioRepository.findAll().stream().map(usuarioMapper::mapToUsuarioDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        activarFiltroEstado(true);
        return usuarioRepository.findByIdAndEstadoIsTrue(id).map(usuarioMapper::mapToUsuarioDTO);
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<Usuario> obtenerPorIdContenxt(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        activarFiltroEstado(true);
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UsuarioDTO> obtenerPorRol(Long rolId) {
        activarFiltroEstado(true);
        return usuarioRepository.findByRol_Id(rolId).stream().map(usuarioMapper::mapToUsuarioDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UsuarioDTO guardar(UsuarioRequestDTO request) {
        activarFiltroEstado(true);
        if (usuarioRepository.existsByCorreoAndEstadoIsTrue(request.getCorreo())) {
            throw new IllegalStateException("Ya existe un usuario con el correo ingresado");
        }

        Usuario usuario = usuarioMapper.mapFromUsuarioRequestDTOToUsuario(request);

        //hasheamos la contraseña

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        rolRepository.findById(request.getRol().getId()).ifPresentOrElse(usuario::setRol, () -> {
            throw new IllegalStateException("El rol especificado no existe");
        });

        return usuarioMapper.mapToUsuarioDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    @Override
    public UsuarioDTO actualizar(Long id, UsuarioRequestDTO request) {
        activarFiltroEstado(true);
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("No existe un usuario con el id:" + id));
        //hasheamos la contraseña
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        rolRepository.findById(request.getRol().getId()).ifPresentOrElse(usuario::setRol, () -> {
            throw new IllegalStateException("El rol especificado no existe");
        });
        Usuario usuarioSaved = usuarioRepository.save(usuario);
        return usuarioMapper.mapToUsuarioDTO(usuarioSaved);
    }


    @Transactional
    @Override
    public void eliminar(Long id) {
        activarFiltroEstado(true);
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(id).orElseThrow(() -> new RuntimeException("No existe un usuario con el id:" + id));
        usuario.setEstado(false);//borrado logico
        usuarioRepository.save(usuario);
    }

}

