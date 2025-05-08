package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.ServicioSeguroDTO;

import java.util.List;
import java.util.Optional;

public interface ServicioSeguroService {
    List<ServicioSeguroDTO> listarServicioSeguro();
    List<ServicioSeguroDTO> listarPorServicio(Long servicioId);
    List<ServicioSeguroDTO> listarPorSeguro(Long seguroId);
    List<ServicioSeguroDTO> listarPorCobertura(Long coberturaId);
    Optional<ServicioSeguroDTO> getSeguroServicioById(Long id);
    Optional<ServicioSeguroDTO> getSeguroServicioBySeguroAndServicio(Long seguroId, Long servicioId);
    ServicioSeguroDTO createServicioSeguro(ServicioSeguroDTO servicioSeguroDTO);
    void deleteServicioSeguro(Long id);

}
