package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.AlergiaDTO;
import com.clinicaregional.clinica.entity.Alergia;
import org.springframework.stereotype.Component;

@Component
public class AlergiaMapper {
    public AlergiaDTO mapToAlergiaDTO(Alergia alergia) {
        return new AlergiaDTO(
                alergia.getId(),
                alergia.getNombre(),
                alergia.getTipoAlergia()
        );
    }
    public Alergia mapToAlergia(AlergiaDTO alergiaDTO) {
        return new Alergia(
                alergiaDTO.getId(),
                alergiaDTO.getNombre(),
                alergiaDTO.getTipoAlergia()
        );
    }
}
