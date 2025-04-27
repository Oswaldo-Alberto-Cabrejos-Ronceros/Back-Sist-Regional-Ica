package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.RecepcionistaRequest;
import com.clinicaregional.clinica.dto.response.RecepcionistaResponse;
import com.clinicaregional.clinica.entity.Recepcionista;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecepcionistaMapper {

    private final UsuarioRepository usuarioRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public Recepcionista toEntity(RecepcionistaRequest request) {
        Usuario usuario = usuarioRepository.findByIdAndEstadoIsTrue(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + request.getUsuarioId()));

        TipoDocumento tipoDocumento = tipoDocumentoRepository.findByIdAndEstadoIsTrue(request.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado con id: " + request.getTipoDocumentoId()));

        return Recepcionista.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .numeroDocumento(request.getNumeroDocumento())
                .tipoDocumento(tipoDocumento)
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .turnoTrabajo(request.getTurnoTrabajo())
                .fechaContratacion(request.getFechaContratacion())
                .usuario(usuario)
                .estado(true)
                .build();
    }

    public RecepcionistaResponse toResponse(Recepcionista entity) {
        return RecepcionistaResponse.builder()
                .id(entity.getId())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .numeroDocumento(entity.getNumeroDocumento())
                .tipoDocumentoId(entity.getTipoDocumento().getId())
                .telefono(entity.getTelefono())
                .direccion(entity.getDireccion())
                .turnoTrabajo(entity.getTurnoTrabajo())
                .fechaContratacion(entity.getFechaContratacion())
                .usuarioId(entity.getUsuario().getId())
                .build();
    }
}
