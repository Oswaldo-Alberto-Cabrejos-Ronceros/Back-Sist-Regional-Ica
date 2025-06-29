package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.SeguroDTO;
import com.clinicaregional.clinica.enums.EstadoSeguro;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface SeguroService {
    List<SeguroDTO> listarSeguros();

    Optional<SeguroDTO> getSeguroById(Long id);

    Optional<SeguroDTO> getSeguroByNombre(String nombre);

    SeguroDTO createSeguro(SeguroDTO seguroDTO, MultipartFile imagen);

    SeguroDTO updateSeguro(Long id,SeguroDTO seguroDTO, MultipartFile imagen);

    SeguroDTO updateEstadoSeguro(Long id, EstadoSeguro estadoSeguro);

    void deleteSeguro(Long id);

}
