package com.clinicaregional.clinica.entity;

import com.clinicaregional.clinica.enums.EstadoBloque;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "horarios_bloque")
public class HorarioBloque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "horario_bloque_id")
    private Long id;

    private String nombre;

    private LocalDate fecha;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_bloque", nullable = false)
    private EstadoBloque estadoBloque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id")
    private Cita cita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disponibilidad_id", nullable = false)
    private Disponibilidad disponibilidad;
}
