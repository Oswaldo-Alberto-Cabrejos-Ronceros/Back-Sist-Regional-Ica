package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.dto.request.EspecialidadRequest;
import com.clinicaregional.clinica.dto.response.EspecialidadResponse;
import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.exception.DuplicateResourceException;
import com.clinicaregional.clinica.exception.ResourceNotFoundException;
import com.clinicaregional.clinica.mapper.EspecialidadMapper;
import com.clinicaregional.clinica.repository.EspecialidadRepository;
import com.clinicaregional.clinica.service.EspecialidadService;
import com.clinicaregional.clinica.service.S3ServicePublic;
import com.clinicaregional.clinica.util.FiltroEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final EspecialidadMapper especialidadMapper;
    private final FiltroEstado filtroEstado;
    private final S3ServicePublic s3Service;

    @Transactional(readOnly = true)
    @Override
    public List<EspecialidadResponse> listarEspecialidades() {
        filtroEstado.activarFiltroEstado(true);
        List<EspecialidadResponse> especialidades = especialidadRepository.findAll()
                .stream()
                .map(especialidadMapper::toResponse)
                .toList();
        return especialidades.stream().map(this::agregarUrlImage).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<EspecialidadResponse> getEspecialidadById(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una especialidad con el id " + id));
        EspecialidadResponse especialidadResponse = especialidadMapper.toResponse(especialidad);
        return Optional.of(this.agregarUrlImage(especialidadResponse));
    }

    @Transactional
    @Override
    public EspecialidadResponse guardarEspecialidad(EspecialidadRequest especialidadRequest, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);
        if (especialidadRepository.existsByNombre(especialidadRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe una especialidad con el nombre ingresado");
        }
        Especialidad especialidad = especialidadMapper.toEntity(especialidadRequest);
        if (imagen != null) {
            String key = s3Service.subirArchivo(imagen, "especialidad" + especialidad.getNombre());
            especialidad.setImagen(key);
        }
        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);
        return this.agregarUrlImage(especialidadMapper.toResponse(savedEspecialidad));
    }

    @Transactional
    @Override
    public EspecialidadResponse actualizarEspecialidad(Long id, EspecialidadRequest especialidadRequest, MultipartFile imagen) {
        filtroEstado.activarFiltroEstado(true);
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con ID: " + id));
        if (!especialidad.getNombre().equalsIgnoreCase(especialidadRequest.getNombre()) &&
                especialidadRepository.existsByNombre(especialidadRequest.getNombre())) {
            throw new DuplicateResourceException("Ya existe una especialidad con el nombre ingresado");
        }
        especialidad.setNombre(especialidadRequest.getNombre());
        especialidad.setDescripcion(especialidadRequest.getDescripcion());
        especialidad.setImagen(especialidadRequest.getImagen());

        if (imagen != null) {
            if (especialidad.getImagen() != null) {
                s3Service.eliminarArchivo(especialidad.getImagen());
            }
            String key = s3Service.subirArchivo(imagen, "servicios" + especialidad.getNombre());
            especialidad.setImagen(key);
        }

        Especialidad updatedEspecialidad = especialidadRepository.save(especialidad);
        return this.agregarUrlImage(especialidadMapper.toResponse(updatedEspecialidad));
    }

    @Transactional
    @Override
    public void eliminarEspecialidad(Long id) {
        filtroEstado.activarFiltroEstado(true);
        Especialidad especialidad = especialidadRepository.findByIdAndEstadoIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));
        especialidad.setEstado(false); // borrado lógico
        if (especialidad.getImagen() != null) {
            s3Service.eliminarArchivo(especialidad.getImagen());
        }
        especialidadRepository.save(especialidad);
    }

    //funcion para obtener el url
    private EspecialidadResponse agregarUrlImage(EspecialidadResponse especialidadResponse) {
        if (especialidadResponse.getImagen() != null) {
            String imageUrl = s3Service.generarUrlPublico(especialidadResponse.getImagen());
            especialidadResponse.setImagen(imageUrl);
        }
        return especialidadResponse;
    }
}
