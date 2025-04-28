package com.clinicaregional.clinica.tipoDocumento.repository;


import com.clinicaregional.clinica.entity.TipoDocumento;
import com.clinicaregional.clinica.repository.TipoDocumentoRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TipoDocumentoRepositoryTest {

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Test
    @DisplayName("Guardar y verificar existencia por nombre y estado activo")
    void testExistsByNombreAndEstadoIsTrue() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.builder()
                .nombre("DNI")
                .descripcion("Documento Nacional de Identidad")
                .estado(true)
                .build();

        tipoDocumentoRepository.save(tipoDocumento);

        // Act
        boolean exists = tipoDocumentoRepository.existsByNombreAndEstadoIsTrue("DNI");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Buscar por ID y estado activo")
    void testFindByIdAndEstadoIsTrue() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.builder()
                .nombre("Pasaporte")
                .descripcion("Documento de viaje")
                .estado(true)
                .build();

        tipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        // Act
        Optional<TipoDocumento> encontrado = tipoDocumentoRepository.findByIdAndEstadoIsTrue(tipoDocumento.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Pasaporte");
    }

    @Test
    @DisplayName("No encontrar si estado es false")
    void testFindByIdAndEstadoIsTrue_whenEstadoIsFalse() {
        // Arrange
        TipoDocumento tipoDocumento = TipoDocumento.builder()
                .nombre("Carnet de Extranjería")
                .descripcion("Documento para extranjeros")
                .estado(false)
                .build();

        tipoDocumento = tipoDocumentoRepository.save(tipoDocumento);

        // Act
        Optional<TipoDocumento> encontrado = tipoDocumentoRepository.findByIdAndEstadoIsTrue(tipoDocumento.getId());

        // Assert
        assertThat(encontrado).isNotPresent();
    }
}
