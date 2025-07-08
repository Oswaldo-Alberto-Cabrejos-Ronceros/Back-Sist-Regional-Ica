package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.PacienteConUserDTO;
import com.clinicaregional.clinica.dto.PacienteSinUserDTO;
import com.clinicaregional.clinica.dto.response.MyInfoPaciente;
import com.clinicaregional.clinica.dto.response.PacienteResponseDTO;
import com.clinicaregional.clinica.entity.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class PacienteMapper {

    private final TipoDocumentoMapper tipoDocumentoMapper;
    private final UsuarioMapper usuarioMapper;
    private final SeguroMapper seguroMapper;

    @Autowired
    public PacienteMapper(TipoDocumentoMapper tipoDocumentoMapper, UsuarioMapper usuarioMapper, SeguroMapper seguroMapper) {
        this.tipoDocumentoMapper = tipoDocumentoMapper;
        this.usuarioMapper = usuarioMapper;
        this.seguroMapper = seguroMapper;
    }

    public PacienteConUserDTO mapToPacienteDTO(Paciente paciente) {
        return new PacienteConUserDTO(
                paciente.getId(),
                paciente.getNombres(),
                paciente.getApellidos(),
                paciente.getFechaNacimiento(),
                paciente.getSexo(),
                paciente.getEmail(),
                tipoDocumentoMapper.mapToTipoDocumentoDTO(paciente.getTipoDocumento()),
                paciente.getNumeroIdentificacion(),
                paciente.getNacionalidad(),
                paciente.getTelefono(),
                paciente.getDireccion(),
                paciente.getImagenUrl(),
                paciente.getTipoSangre(),
                paciente.getAntecedentes(),
                paciente.getContactoDeEmergenciaNombre(),
                paciente.getContactoDeEmergenciaTelefono(),
                paciente.getModalidadDeAtencion(),
                paciente.getSeguro(),
                paciente.getNumeroDePoliza(),
                paciente.getUsuario()
        );
    }

    public Paciente mapToPaciente(PacienteConUserDTO dto) {
        return Paciente.builder()
                .id(dto.getId())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .fechaNacimiento(dto.getFechaNacimiento())
                .sexo(dto.getSexo())
                .email(dto.getEmail())
                .tipoDocumento(tipoDocumentoMapper.mapToTipoDocumento(dto.getTipoDocumento()))
                .numeroIdentificacion(dto.getNumeroIdentificacion())
                .nacionalidad(dto.getNacionalidad())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .imagenUrl(dto.getImagenUrl())
                .tipoSangre(dto.getTipoSangre())
                .antecedentes(dto.getAntecedentes())
                .contactoDeEmergenciaNombre(dto.getContactoDeEmergenciaNombre())
                .contactoDeEmergenciaTelefono(dto.getContactoDeEmergenciaTelefono())
                .modalidadDeAtencion(dto.getModalidadDeAtencion())
                .seguro(dto.getSeguro())
                .numeroDePoliza(dto.getNumeroDePoliza())
                .usuario(dto.getUsuario())
                .build();
    }

    public PacienteSinUserDTO mapToPacienteSinUserDTO(Paciente paciente) {
        return new PacienteSinUserDTO(
                paciente.getId(),
                paciente.getNombres(),
                paciente.getApellidos(),
                paciente.getFechaNacimiento(),
                paciente.getSexo(),
                tipoDocumentoMapper.mapToTipoDocumentoDTO(paciente.getTipoDocumento()),
                paciente.getNumeroIdentificacion(),
                paciente.getNacionalidad(),
                paciente.getTelefono(),
                paciente.getDireccion(),
                paciente.getEmail(),
                paciente.getModalidadDeAtencion(),
                paciente.getSeguro() != null ? seguroMapper.mapToSeguroDTO(paciente.getSeguro()) : null,
                paciente.getNumeroDePoliza(),
                paciente.getContactoDeEmergenciaNombre(),
                paciente.getContactoDeEmergenciaTelefono()
        );
    }

    public Paciente mapToPacienteSinUser(PacienteSinUserDTO dto) {
        return Paciente.builder()
                .id(dto.getId())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .fechaNacimiento(dto.getFechaNacimiento())
                .sexo(dto.getSexo())
                .email(dto.getEmail())
                .tipoDocumento(tipoDocumentoMapper.mapToTipoDocumento(dto.getTipoDocumento()))
                .numeroIdentificacion(dto.getNumeroIdentificacion())
                .nacionalidad(dto.getNacionalidad())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .modalidadDeAtencion(dto.getModalidadDeAtencion())
                .seguro(dto.getSeguro() != null ? seguroMapper.mapToSeguro(dto.getSeguro()) : null)
                .numeroDePoliza(dto.getNumeroDePoliza())
                .contactoDeEmergenciaNombre(dto.getContactoDeEmergenciaNombre())
                .contactoDeEmergenciaTelefono(dto.getContactoDeEmergenciaTelefono())
                .estado(true)
                .build();
    }

    public MyInfoPaciente mapToMyInfoPaciente(Paciente paciente) {
        return new MyInfoPaciente(
                paciente.getNombres(),
                paciente.getApellidos(),
                paciente.getNumeroIdentificacion(),
                paciente.getUsuario() != null ? paciente.getUsuario().getCorreo() : null,
                paciente.getFechaNacimiento(),
                paciente.getSexo(),
                paciente.getNacionalidad(),
                paciente.getDireccion(),
                paciente.getImagenUrl()
        );
    }

    public PacienteResponseDTO mapToPacienteResponseDTO(Paciente paciente) {
        return new PacienteResponseDTO(
                paciente.getNombres(),
                paciente.getApellidos(),
                paciente.getNumeroIdentificacion(),
                paciente.getTelefono(),
                paciente.getSexo(),
                calcularEdad(paciente.getFechaNacimiento()),
                paciente.getAntecedentes()
        );
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        return (fechaNacimiento != null) ? Period.between(fechaNacimiento, LocalDate.now()).getYears() : 0;
    }
}
