package com.clinicaregional.clinica.util;

import com.clinicaregional.clinica.entity.Disponibilidad;
import com.clinicaregional.clinica.entity.HorarioBloque;
import com.clinicaregional.clinica.enums.EstadoBloque;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioBloqueGenerator {

    public static List<HorarioBloque> generarBloques(Disponibilidad disponibilidad, LocalDate fecha, int duracionMinutos) {
        List<HorarioBloque> bloques = new ArrayList<>();

        LocalTime inicio = disponibilidad.getHoraInicio();
        LocalTime fin = disponibilidad.getHoraFin();

        while (inicio.plusMinutes(duracionMinutos).compareTo(fin) <= 0) {
            LocalTime siguienteFin = inicio.plusMinutes(duracionMinutos);

            bloques.add(HorarioBloque.builder()
                    .fecha(fecha)
                    .horaInicio(inicio)
                    .horaFin(siguienteFin)
                    .estadoBloque(EstadoBloque.DISPONIBLE)
                    .disponibilidad(disponibilidad)
                    .build());

            inicio = siguienteFin;
        }

        return bloques;
    }
}

