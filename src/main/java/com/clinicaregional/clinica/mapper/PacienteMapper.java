package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.PacienteDTO;
import com.clinicaregional.clinica.entity.Paciente;
import com.clinicaregional.clinica.entity.Seguro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper {

    private final TipoDocumentoMapper tipoDocumentoMapper;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public PacienteMapper(TipoDocumentoMapper tipoDocumentoMapper,UsuarioMapper usuarioMapper) {
        this.tipoDocumentoMapper = tipoDocumentoMapper;
        this.usuarioMapper = usuarioMapper;
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
                paciente.getTipoSangre(),
                paciente.getAntecedentes(),
                paciente.getUsuario()!=null ? usuarioMapper.mapToUsuarioDTO(paciente.getUsuario()):null ,//usuario puede ser null
                paciente.getSeguro()!=null ? paciente.getSeguro().getId():null
        );
    }
    public Paciente mapToPaciente(PacienteDTO pacienteDTO) {
        Seguro seguro =new Seguro();
        seguro.setId(pacienteDTO.getSeguroId());
        return new Paciente(
                pacienteDTO.getId(),
                pacienteDTO.getNombres(),
                pacienteDTO.getApellidos(),
                pacienteDTO.getFechaNacimiento(),
                pacienteDTO.getSexo(),
                tipoDocumentoMapper.mapToTipoDocumento(pacienteDTO.getTipoDocumento()),
                pacienteDTO.getNumeroIdentificacion(),
                pacienteDTO.getNacionalidad(),
                pacienteDTO.getTelefono(),
                pacienteDTO.getDireccion(),
                pacienteDTO.getTipoSangre(),
                pacienteDTO.getAntecedentes(),
                pacienteDTO.getUsuario()!=null ? usuarioMapper.mapToUsuario(pacienteDTO.getUsuario()):null, //usuario puede ser null
                pacienteDTO.getSeguroId()!=null ? seguro:null
        );
    }
}
