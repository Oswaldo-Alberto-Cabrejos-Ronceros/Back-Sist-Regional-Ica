package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.entity.TipoDocumento;
import org.springframework.stereotype.Component;

@Component
public class TipoDocumentoMapper {
    public TipoDocumentoDTO mapToTipoDocumentoDTO(TipoDocumento tipoDocumento) {
        return new TipoDocumentoDTO(
                tipoDocumento.getId(),
                tipoDocumento.getNombre(),
                tipoDocumento.getDescripcion()
        );
    }

    public TipoDocumento mapToTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO) {
        return new TipoDocumento(
                tipoDocumentoDTO.getId(),
                tipoDocumentoDTO.getNombre(),
                tipoDocumentoDTO.getDescripcion()
        );
    }
}
