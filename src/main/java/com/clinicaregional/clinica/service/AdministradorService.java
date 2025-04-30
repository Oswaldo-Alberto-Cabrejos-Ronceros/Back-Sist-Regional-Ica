package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.request.RegisterAdministradorRequest;

import java.util.List;
import java.util.Optional;

public interface AdministradorService {
    List<AdministradorDTO> listarAdministradores();
    Optional<AdministradorDTO> getAdministradorById(Long id);
    AdministradorDTO createAdministrador(RegisterAdministradorRequest registerAdministradorRequest);
    AdministradorDTO updateAdministrador(Long id,AdministradorDTO administradorDTO);
    void deleteAdministrador(Long id);
}
