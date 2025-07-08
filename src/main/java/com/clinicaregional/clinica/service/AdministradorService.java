package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;
import com.clinicaregional.clinica.dto.response.MyInfoAdministrador;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AdministradorService {
    List<AdministradorDTO> listarAdministradores();
    Optional<AdministradorDTO> getAdministradorById(Long id);
    MyInfoAdministrador getMyInfoAdministrador(Long id);
    AdministradorDTO createAdministrador(RegisterAdministradorRequest registerAdministradorRequest);
    AdministradorDTO updateAdministrador(Long id,AdministradorDTO administradorDTO, MultipartFile imagen);
    void deleteAdministrador(Long id);
}
