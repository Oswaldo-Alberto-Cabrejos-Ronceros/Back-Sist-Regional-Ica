package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.PacienteAlergiaDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.PacienteAlergia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PacienteAlergiaMapper {

    private final AlergiaMapper alergiaMapper;

    @Autowired
    public PacienteAlergiaMapper(AlergiaMapper alergiaMapper) {
        this.alergiaMapper = alergiaMapper;
    }

    public PacienteAlergiaDTO mapToPacienteAlergiaDTO(PacienteAlergia pacienteAlergia) {
        return new PacienteAlergiaDTO(pacienteAlergia.getId(),
                pacienteAlergia.getPaciente().getId(),
                alergiaMapper.mapToAlergiaDTO(pacienteAlergia.getAlergia()), pacienteAlergia.getGravedad());
    }

    public PacienteAlergia mapToPacienteAlergia(PacienteAlergiaDTO pacienteAlergiaDTO) {
        Paciente paciente = new Paciente();
        paciente.setId(paciente.getId());
        return new PacienteAlergia(pacienteAlergiaDTO.getId(), paciente,
                alergiaMapper.mapToAlergia(pacienteAlergiaDTO.getAlergia()), pacienteAlergiaDTO.getGravedad());
    }
}
