package com.clinicaregional.clinica.especialidades.repository;

import com.clinicaregional.clinica.entity.Especialidad;
import com.clinicaregional.clinica.repository.EspecialidadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EspecialidadRepositoryTest {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Test
    @DisplayName("Debe guardar y recuperar una especialidad activa")
    void guardarYRecuperarEspecialidadActiva() {
        // Arrange
        Especialidad especialidad = Especialidad.builder()
                .nombre("Cardiología")
                .descripcion("Especialidad del corazón")
                .imagen("cardiologia.png")
                .estado(true)
                .build();

        // Act
        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);
        Optional<Especialidad> encontrada = especialidadRepository.findByIdAndEstadoIsTrue(savedEspecialidad.getId());

        // Assert
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNombre()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("Debe retornar vacío si la especialidad está inactiva")
    void noRecuperarEspecialidadInactiva() {
        // Arrange
        Especialidad especialidad = Especialidad.builder()
                .nombre("Dermatología")
                .descripcion("Especialidad de la piel")
                .imagen("dermatologia.png")
                .estado(false)
                .build();

        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);

        // Act
        Optional<Especialidad> encontrada = especialidadRepository.findByIdAndEstadoIsTrue(savedEspecialidad.getId());

        // Assert
        assertThat(encontrada).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar existencia por nombre")
    void existeEspecialidadPorNombre() {
        // Arrange
        Especialidad especialidad = Especialidad.builder()
                .nombre("Neurología")
                .descripcion("Especialidad del sistema nervioso")
                .imagen("neurologia.png")
                .estado(true)
                .build();

        especialidadRepository.save(especialidad);

        // Act
        boolean exists = especialidadRepository.existsByNombre("Neurología");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe retornar falso si el nombre no existe")
    void noExisteEspecialidadPorNombre() {
        // Act
        boolean exists = especialidadRepository.existsByNombre("Oncología");

        // Assert
        assertThat(exists).isFalse();
    }
}
