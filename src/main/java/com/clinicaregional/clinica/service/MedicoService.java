package com.clinicaregional.clinica.service;

import java.util.List;

import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;

public interface MedicoService {

        List<MedicoResponseDTO> obtenerMedicos();

        List<MedicoResponsePublicDTO> obtenerMedicosPublic();

        MedicoResponseDTO actualizarMedico(Long id, MedicoRequestDTO dto);

        MedicoResponseDTO guardarMedico(MedicoRequestDTO dto);

        void eliminarMedico(Long id);

}
