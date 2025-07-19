package com.clinicaregional.clinica.service;

import java.util.List;

import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.request.MedicoUpdateDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;
import com.clinicaregional.clinica.dto.response.MyInfoMedico;

public interface MedicoService {

        List<MedicoResponseDTO> obtenerMedicos();

        List<MedicoResponsePublicDTO> obtenerMedicosPublic();

        MedicoResponseDTO obtenerMedicoPorId(Long id);

        MyInfoMedico obtenerMyInfoMedico(Long id);

        MedicoResponseDTO actualizarMedico(Long id, MedicoUpdateDTO dto);

        MedicoResponseDTO guardarMedico(MedicoRequestDTO dto);

        void eliminarMedico(Long id);

        MedicoResponseDTO obtenerMedicoPorUsuarioId(Long usuarioId);

}
