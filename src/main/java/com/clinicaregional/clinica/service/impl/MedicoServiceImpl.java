package com.clinicaregional.clinica.service.impl;

import java.util.stream.Collectors;
import java.util.List;

import com.clinicaregional.clinica.dto.AdministradorDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponsePublicDTO;
import com.clinicaregional.clinica.dto.response.MyInfoMedico;
import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.service.S3ServicePublic;
import com.clinicaregional.clinica.service.TipoDocumentoService;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.exception.BadRequestException;
import com.clinicaregional.clinica.util.FiltroEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinicaregional.clinica.repository.MedicoRepository;
import com.clinicaregional.clinica.dto.request.MedicoRequestDTO;
import com.clinicaregional.clinica.dto.UsuarioDTO;
import com.clinicaregional.clinica.dto.request.UsuarioRequestDTO;
import com.clinicaregional.clinica.dto.RolDTO;
import com.clinicaregional.clinica.dto.response.MedicoResponseDTO;
import com.clinicaregional.clinica.entity.Medico;
import com.clinicaregional.clinica.entity.Rol;
import com.clinicaregional.clinica.entity.Usuario;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.MedicoMapper;
import com.clinicaregional.clinica.repository.UsuarioRepository;
import com.clinicaregional.clinica.service.MedicoService;
import com.clinicaregional.clinica.service.RolService;
import com.clinicaregional.clinica.service.UsuarioService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final TipoDocumentoService tipoDocumentoService;
    private final RolService rolService;
    private final FiltroEstado filtroEstado;
    private final S3ServicePublic s3Service;

    @Autowired
    public MedicoServiceImpl(
            MedicoRepository medicoRepository,
            MedicoMapper medicoMapper,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            TipoDocumentoService tipoDocumentoService,

            RolService rolService,
            FiltroEstado filtroEstado,S3ServicePublic s3Service) {

        this.medicoRepository = medicoRepository;
        this.medicoMapper = medicoMapper;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.tipoDocumentoService = tipoDocumentoService;
        this.rolService = rolService;
        this.filtroEstado = filtroEstado;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MedicoResponseDTO> obtenerMedicos() {
        filtroEstado.activarFiltroEstado(true);
        List<MedicoResponseDTO> medicos = medicoRepository.findAll()
                .stream()
                .map(medicoMapper::mapToMedicoResponseDTO)
                .toList();
        return medicos.stream().map(this::agregarUrlImage).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<MedicoResponsePublicDTO> obtenerMedicosPublic() {
        filtroEstado.activarFiltroEstado(true);

        List<MedicoResponsePublicDTO> medicos = medicoRepository.findAll().stream().map(medicoMapper::mapToMedicoResponsePublicDTO).toList();
        return medicos.stream().map(medico -> {
            if (medico.getImagen() != null) {
                String imageUrl = s3Service.generarUrlPublico(medico.getImagen());
                medico.setImagen(imageUrl);
            }
            return medico;
        }).collect(Collectors.toList());

    }

    @Transactional
    @Override
    public MedicoResponseDTO obtenerMedicoPorId(Long id) {
        filtroEstado.activarFiltroEstado(true);
        MedicoResponseDTO medicoResponseDTO = medicoMapper.mapToMedicoResponseDTO(medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con ID: " + id)));
        return this.agregarUrlImage(medicoResponseDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public MyInfoMedico obtenerMyInfoMedico(Long id) {
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con ID: " + id));
        if (medico.getImagen() != null) {
            String imageUrl = s3Service.generarUrlPublico(medico.getImagen());
            medico.setImagen(imageUrl);
        }
        return medicoMapper.mapToMyInfoMedico(medico);
    }

    @Transactional
    @Override
    public MedicoResponseDTO guardarMedico(MedicoRequestDTO dto, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);

        if (medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())) {
            throw new DuplicateResourceException("Ya existe un médico con el número de colegiatura ingresado");
        }

        // Validar RNE solo si es ESPECIALISTA
        if (dto.getTipoMedico().name().equals("ESPECIALISTA")) {
            if (dto.getNumeroRNE() == null || dto.getNumeroRNE().isBlank()) {
                throw new BadRequestException("El número RNE es obligatorio para médicos especialistas");
            }
            if (medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())) {
                throw new DuplicateResourceException("Ya existe un médico con el RNE ingresado");
            }
        } else {
            dto.setNumeroRNE(null); // limpiar por si se envió accidentalmente
        }

        // verificamos que exista tipo documento
        TipoDocumento tipoDocumento = tipoDocumentoService.getTipoDocumentoByIdContext(dto.getTipoDocumentoId())
                .orElseThrow(() -> new RuntimeException("No se encontró un tipo de documento con el id ingresado"));

        if (medicoRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new RuntimeException("Ya existe un medico con el numero de documento ingresado");
        }

        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado");
        }

        RolDTO rolMedico = rolService.obtenerRolPorNombre("MEDICO")
                .orElseThrow(() -> new IllegalStateException(
                        "Rol MEDICO no encontrado en el sistema"));
        // Crear usuario
        UsuarioRequestDTO newUsuario = new UsuarioRequestDTO();
        newUsuario.setCorreo(dto.getCorreo());
        newUsuario.setPassword(dto.getPassword());
        newUsuario.setRol(rolMedico);

        UsuarioDTO usuarioDTO = usuarioService.guardar(newUsuario);
        Usuario usuario1 = new Usuario();
        usuario1.setId(usuarioDTO.getId());

        Medico medico = Medico.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .numeroColegiatura(dto.getNumeroColegiatura())
                .numeroRNE(dto.getNumeroRNE())
                .tipoDocumento(tipoDocumento)
                .numeroDocumento(dto.getNumeroDocumento())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .descripcion(dto.getDescripcion())
                .imagen(dto.getImagen())
                .fechaContratacion(dto.getFechaContratacion())
                .tipoContrato(dto.getTipoContrato())
                .tipoMedico(dto.getTipoMedico())
                .usuario(usuario1)
                .estado(true)
                .build();
        if (imagen != null) {
            String key = s3Service.subirArchivo(imagen, "medico" + medico.getNombres());
            medico.setImagen(key);
        }
        Medico medicoSaved = medicoRepository.save(medico);
        return this.agregarUrlImage(medicoMapper.mapToMedicoResponseDTO(medicoSaved));
    }

    @Transactional
    @Override
    public MedicoResponseDTO actualizarMedico(Long id, MedicoRequestDTO dto, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);

        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con ID: " + id));

        Usuario usuario = medico.getUsuario(); // obtener usuario asociado al médico

        // Validar colegiatura si cambió
        if (!medico.getNumeroColegiatura().equals(dto.getNumeroColegiatura()) &&
                medicoRepository.existsByNumeroColegiatura(dto.getNumeroColegiatura())) {
            throw new DuplicateResourceException("Ya existe un médico con el número de colegiatura ingresado");
        }

        // Validar RNE si corresponde y cambió
        if (dto.getTipoMedico().name().equals("ESPECIALISTA")) {
            if (dto.getNumeroRNE() == null || dto.getNumeroRNE().isBlank()) {
                throw new BadRequestException("El número RNE es obligatorio para médicos especialistas");
            }
            if (!dto.getNumeroRNE().equals(medico.getNumeroRNE()) &&
                    medicoRepository.existsByNumeroRNE(dto.getNumeroRNE())) {
                throw new DuplicateResourceException("Ya existe un médico con el RNE ingresado");
            }
        } else {
            dto.setNumeroRNE(null); // limpiar si se envió por error
        }

        // Validar si el correo fue modificado y ya existe en otro usuario
        if (!usuario.getCorreo().equals(dto.getCorreo()) &&
                usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo ingresado");
        }

        // Validar si ese correo ya está asignado a otro médico
        Medico medicoConCorreo = medicoRepository.findByUsuarioCorreo(dto.getCorreo()).orElse(null);
        if (medicoConCorreo != null && !medicoConCorreo.getId().equals(medico.getId())) {
            throw new DuplicateResourceException("Ya existe un médico con el usuario ingresado");
        }

        // Actualizar datos del usuario
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(dto.getPassword());
        usuarioRepository.save(usuario);

        // Actualizar datos del médico
        medico.setNombres(dto.getNombres());
        medico.setApellidos(dto.getApellidos());
        medico.setNumeroColegiatura(dto.getNumeroColegiatura());
        medico.setNumeroRNE(dto.getNumeroRNE());
        medico.setTelefono(dto.getTelefono());
        medico.setDireccion(dto.getDireccion());
        medico.setDescripcion(dto.getDescripcion());
        medico.setImagen(dto.getImagen());
        medico.setFechaContratacion(dto.getFechaContratacion());
        medico.setTipoContrato(dto.getTipoContrato());
        medico.setTipoMedico(dto.getTipoMedico());

        if (imagen != null) {
            if (medico.getImagen() != null) {
                s3Service.eliminarArchivo(medico.getImagen());
            }
            String key = s3Service.subirArchivo(imagen, "medico" + medico.getNombres());
            medico.setImagen(key);
        }

        Medico actualizado = medicoRepository.save(medico);
        return this.agregarUrlImage(medicoMapper.mapToMedicoResponseDTO(actualizado));
    }


    @Transactional
    @Override
    public void eliminarMedico(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Medico medico = medicoRepository.findByIdAndEstadoIsTrue(id)

                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con ID: " + id));

        // 1. Primero marca el médico como inactivo
        medico.setEstado(false);

        medicoRepository.save(medico);

        //borra la imagen si la tiene
        if (medico.getImagen() != null) {
            s3Service.eliminarArchivo(medico.getImagen());
        }

        // 2. Desvincula el usuario (si existe)
        if (medico.getUsuario() != null) {
            Long usuarioId = medico.getUsuario().getId();

            // 3. Elimina el usuario en una nueva transacción
            try {
                usuarioService.eliminarUsuarioSinRelaciones(usuarioId);

                // 4. Actualiza el médico para establecer usuario_id como null
                medico.setUsuario(null);
                medicoRepository.save(medico);
            } catch (Exception e) {
                // Loggear el error pero continuar
                System.err.println("Error al eliminar usuario asociado: " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public MedicoResponseDTO obtenerMedicoPorUsuarioId(Long usuarioId) {
        filtroEstado.activarFiltroEstado(true);
        Medico medico = medicoRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado con usuario ID: " + usuarioId));
        return this.agregarUrlImage( medicoMapper.mapToMedicoResponseDTO(medico));
    }


    //funcion para obtener el url
    private MedicoResponseDTO agregarUrlImage(MedicoResponseDTO medicoResponseDTO) {
        if (medicoResponseDTO.getImagen() != null) {
            String imageUrl = s3Service.generarUrlPublico(medicoResponseDTO.getImagen());
            medicoResponseDTO.setImagen(imageUrl);
        }
        return medicoResponseDTO;
    }

}
