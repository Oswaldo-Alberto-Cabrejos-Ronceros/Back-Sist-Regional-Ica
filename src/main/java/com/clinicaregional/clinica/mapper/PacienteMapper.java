package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.dto.PacienteSimpleDTO;
import com.clinicaregional.clinica.dto.response.MyInfoPaciente;
import com.clinicaregional.clinica.dto.response.PacienteResponseDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Usuario;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper {

    private final TipoDocumentoMapper tipoDocumentoMapper;
    private final UsuarioMapper usuarioMapper;
    private final SeguroMapper seguroMapper;

    @Autowired
    public PacienteMapper(TipoDocumentoMapper tipoDocumentoMapper, UsuarioMapper usuarioMapper,
            SeguroMapper seguroMapper) {
        this.tipoDocumentoMapper = tipoDocumentoMapper;
        this.usuarioMapper = usuarioMapper;
        this.seguroMapper = seguroMapper;

    }

    public PacienteDTO mapToPacienteDTO(Paciente paciente) {
        return new PacienteDTO(
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
                paciente.getImagenUrl(),
                paciente.getTipoSangre(),
                paciente.getAntecedentes(),
                paciente.getUsuario() != null ? usuarioMapper.mapToUsuarioDTO(paciente.getUsuario()) : null);
    }

    public Paciente mapToPaciente(PacienteDTO pacienteDTO) {
        return Paciente.builder()
                .id(pacienteDTO.getId())
                .nombres(pacienteDTO.getNombres())
                .apellidos(pacienteDTO.getApellidos())
                .fechaNacimiento(pacienteDTO.getFechaNacimiento())
                .sexo(pacienteDTO.getSexo())
                .tipoDocumento(tipoDocumentoMapper.mapToTipoDocumento(pacienteDTO.getTipoDocumento()))
                .numeroIdentificacion(pacienteDTO.getNumeroIdentificacion())
                .nacionalidad(pacienteDTO.getNacionalidad())
                .telefono(pacienteDTO.getTelefono())
                .direccion(pacienteDTO.getDireccion())
                .imagenUrl(pacienteDTO.getImagenUrl())
                .tipoSangre(pacienteDTO.getTipoSangre())
                .antecedentes(pacienteDTO.getAntecedentes())
                .usuario(pacienteDTO.getUsuario() != null ? usuarioMapper.mapToUsuario(pacienteDTO.getUsuario()) : null)
                .build();
    }

    // Para crear un paciente sin usuario
    public PacienteSimpleDTO mapToPacienteSimpleDTO(Paciente paciente) {
         return new PacienteSimpleDTO(
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
            paciente.getImagenUrl(),
            paciente.getEmail(), 
            paciente.getTipoSangre(),
            paciente.getModalidadDeAtencion(),
            paciente.getAntecedentes(),
            paciente.getSeguro() != null ? seguroMapper.mapToSeguroDTO(paciente.getSeguro()) : null,
            paciente.getNumeroDePoliza(),
            paciente.getContactoDeEmergenciaNombre(),
            paciente.getContactoDeEmergenciaTelefono()
    );
    }

    // Convertir el PACIENTESIMPLEDTO a PACIENTE
    public Paciente mapToPacienteSimple(PacienteSimpleDTO dto) {
        Paciente paciente = new Paciente();
        paciente.setId(dto.getId());
        paciente.setNombres(dto.getNombres());
        paciente.setApellidos(dto.getApellidos());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setSexo(dto.getSexo());
        paciente.setTipoDocumento(tipoDocumentoMapper.mapToTipoDocumento(dto.getTipoDocumento()));
        paciente.setNumeroIdentificacion(dto.getNumeroIdentificacion());
        paciente.setNacionalidad(dto.getNacionalidad());
        paciente.setTelefono(dto.getTelefono());
        paciente.setDireccion(dto.getDireccion());
        paciente.setImagenUrl(dto.getImagenUrl());
        paciente.setEmail(dto.getEmail());
        paciente.setTipoSangre(dto.getTipoSangre());
        paciente.setAntecedentes(dto.getAntecedentes());
        paciente.setContactoDeEmergenciaNombre(dto.getContactoDeEmergenciaNombre());
        paciente.setContactoDeEmergenciaTelefono(dto.getContactoDeEmergenciaTelefono());
        paciente.setModalidadDeAtencion(dto.getModalidadDeAtencion());
        paciente.setNumeroDePoliza(dto.getNumeroDePoliza());
        if (dto.getSeguro() != null) {
            paciente.setSeguro(seguroMapper.mapToSeguro(dto.getSeguro()));
        }
        return paciente;
    }

    public MyInfoPaciente mapToMyInfoPaciente(Paciente paciente) {
        return new MyInfoPaciente(
                paciente.getNombres(),
                paciente.getApellidos(),
                paciente.getNumeroIdentificacion(),
                paciente.getUsuario().getCorreo(),
                paciente.getFechaNacimiento(),
                paciente.getSexo(),
                paciente.getNacionalidad(),
                paciente.getDireccion(),
                paciente.getImagenUrl());
    }

    public PacienteResponseDTO mapToPacienteResponseDTO(Paciente paciente) {
        int edad = calcularEdad(paciente.getFechaNacimiento());
        return new PacienteResponseDTO(
                paciente.getNombres(),
                paciente.getApellidos(),
                paciente.getNumeroIdentificacion(),
                paciente.getTelefono(),
                paciente.getSexo(),
                edad,
                paciente.getAntecedentes());
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null)
            return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

}
