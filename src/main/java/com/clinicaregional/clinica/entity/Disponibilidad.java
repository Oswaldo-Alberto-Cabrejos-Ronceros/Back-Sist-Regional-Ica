package com.clinicaregional.clinica.entity;

import java.time.LocalTime;

import org.hibernate.annotations.Filter;

import com.clinicaregional.clinica.enums.DiaSemana;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "disponibilidades")
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class Disponibilidad extends EntidadConEstado{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "disponibilidad_id")
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;
    
    private LocalTime horaInicio;

    private LocalTime horaFin;

    private String notas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

}
