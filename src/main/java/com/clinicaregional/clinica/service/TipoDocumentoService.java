package com.clinicaregional.clinica.service;

import com.clinicaregional.clinica.dto.TipoDocumentoDTO;
import com.clinicaregional.clinica.entity.TipoDocumento;

import java.util.List;
import java.util.Optional;

public interface TipoDocumentoService {

    List<TipoDocumentoDTO> listarTipoDocumento();

    Optional<TipoDocumentoDTO> getTipoDocumentoById(Long id);

    //para mentener contexto

    Optional<TipoDocumento> getTipoDocumentoByIdContext(Long id);

    TipoDocumentoDTO createTipoDocumento(TipoDocumentoDTO tipoDocumento);

    TipoDocumentoDTO updateTipoDocumento(Long id, TipoDocumentoDTO tipoDocumento);

    void deleteTipoDocumento(Long id);
}
