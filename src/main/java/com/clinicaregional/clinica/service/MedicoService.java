package com.clinicaregional.clinica.service;

import java.util.List;

import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;

public interface MedicoService {

        List<MedicoResponseDTO> obtenerMedicos();
    
        MedicoResponseDTO actualizarMedico(Long id, MedicoRequestDTO dto); 
    
        MedicoResponseDTO guardarMedico(MedicoRequestDTO dto); 
    
        void eliminarMedico(Long id);

}
