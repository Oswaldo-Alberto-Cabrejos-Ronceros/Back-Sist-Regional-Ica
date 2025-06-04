package com.clinicaregional.clinica.entity;

import com.clinicaregional.clinica.enums.EstadoBloque;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.Filter;

@Entity
@Table(name = "horarios_bloque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class HorarioBloque extends EntidadConEstado{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "horario_bloque_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(nullable = false, name = "hora_fin")
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "estado_bloque")
    private EstadoBloque estadoBloque;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "disponibilidad_id", nullable = false)
    private Disponibilidad disponibilidad;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", unique = true)
    private Cita cita;
}
