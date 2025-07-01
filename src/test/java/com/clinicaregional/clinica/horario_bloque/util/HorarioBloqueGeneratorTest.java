package com.clinicaregional.clinica.horario_bloque.util;

import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;
import com.clinicaregional.clinica.util.HorarioBloqueGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HorarioBloqueGeneratorTest {

    @Autowired
    private HorarioBloqueGenerator horarioBloqueGenerator;

    @Test
    @DisplayName("Generar bloques debe retornar lista con duración exacta")
    void generarBloques_duracionExacta() {
        // Arrange
        Disponibilidad disponibilidad = Disponibilidad.builder()
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(9, 0))
                .build();

        LocalDate fecha = LocalDate.of(2025, 6, 21);
        int duracionMinutos = 30;

        // Act
        List<HorarioBloque> bloques = HorarioBloqueGenerator.generarBloques(disponibilidad, fecha, duracionMinutos);

        // Assert
        assertThat(bloques).hasSize(2);

        assertThat(bloques.get(0).getHoraInicio()).isEqualTo(LocalTime.of(8, 0));
        assertThat(bloques.get(0).getHoraFin()).isEqualTo(LocalTime.of(8, 30));
        assertThat(bloques.get(1).getHoraInicio()).isEqualTo(LocalTime.of(8, 30));
        assertThat(bloques.get(1).getHoraFin()).isEqualTo(LocalTime.of(9, 0));

        assertThat(bloques.get(0).getEstadoBloque()).isEqualTo(EstadoBloque.DISPONIBLE);
        assertThat(bloques.get(0).getFecha()).isEqualTo(fecha);
    }

    @Test
    @DisplayName("Generar bloques debe ignorar tiempo restante menor a duración")
    void generarBloques_ignoraTiempoRestante() {
        // Arrange
        Disponibilidad disponibilidad = Disponibilidad.builder()
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(10, 50))
                .build();

        LocalDate fecha = LocalDate.of(2025, 6, 21);
        int duracionMinutos = 30;

        // Act
        List<HorarioBloque> bloques = HorarioBloqueGenerator.generarBloques(disponibilidad, fecha, duracionMinutos);

        // Assert
        assertThat(bloques).hasSize(1);
        assertThat(bloques.get(0).getHoraInicio()).isEqualTo(LocalTime.of(10, 0));
        assertThat(bloques.get(0).getHoraFin()).isEqualTo(LocalTime.of(10, 30));
    }

    @Test
    @DisplayName("Generar bloques debe retornar lista vacía si no cabe ningún bloque")
    void generarBloques_sinEspacioSuficiente() {
        // Arrange
        Disponibilidad disponibilidad = Disponibilidad.builder()
                .horaInicio(LocalTime.of(11, 0))
                .horaFin(LocalTime.of(11, 20))
                .build();

        LocalDate fecha = LocalDate.of(2025, 6, 21);
        int duracionMinutos = 30;

        // Act
        List<HorarioBloque> bloques = HorarioBloqueGenerator.generarBloques(disponibilidad, fecha, duracionMinutos);

        // Assert
        assertThat(bloques).isEmpty();
    }
}
