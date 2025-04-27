package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.enums.TipoAlergia;

import java.util.List;
import java.util.Optional;

public interface AlergiaService {

    List<AlergiaDTO> listarAlergias();

    List<AlergiaDTO> listarAlergiasPorTipo(TipoAlergia tipoAlergia);

    Optional<AlergiaDTO> getAlergiaPorId(Long id);

    AlergiaDTO crearAlergia(AlergiaDTO alergiaDTO);

    AlergiaDTO updateAlergia(Long id, AlergiaDTO alergiaDTO);

    void eliminarAlergia(Long id);

}
