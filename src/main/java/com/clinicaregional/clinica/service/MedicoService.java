package com.clinicaregional.clinica.service;

import java.util.List;

import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;
import com.clinicaregional.clinica.dto.response.MyInfoMedico;
import org.springframework.web.multipart.MultipartFile;

public interface MedicoService {

        List<MedicoResponseDTO> obtenerMedicos();

        List<MedicoResponsePublicDTO> obtenerMedicosPublic();

        MedicoResponseDTO obtenerMedicoPorId(Long id);

        MyInfoMedico obtenerMyInfoMedico(Long id);

        MedicoResponseDTO actualizarMedico(Long id, MedicoRequestDTO dto, MultipartFile imagen);

        MedicoResponseDTO guardarMedico(MedicoRequestDTO dto, MultipartFile imagen);

        void eliminarMedico(Long id);

        MedicoResponseDTO obtenerMedicoPorUsuarioId(Long usuarioId);

}
