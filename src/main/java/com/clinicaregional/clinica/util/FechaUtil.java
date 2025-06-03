package com.clinicaregional.clinica.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class FechaUtil {

    public static LocalDate obtenerProximaFecha(DayOfWeek diaDeseado) {
        LocalDate hoy = LocalDate.now();
        return hoy.with(java.time.temporal.TemporalAdjusters.nextOrSame(diaDeseado));
    }

    public static List<LocalDate> obtenerFechasDelMes(DayOfWeek diaSemana, YearMonth mes) {
        List<LocalDate> fechas = new ArrayList<>();

        LocalDate fecha = mes.atDay(1);

        while (fecha.getDayOfWeek() != diaSemana) {
            fecha = fecha.plusDays(1);
        }
        while (fecha.getMonth().equals(mes.getMonth())) {
            fechas.add(fecha);
            fecha = fecha.plusWeeks(1);
        }
        return fechas;
    }
}
