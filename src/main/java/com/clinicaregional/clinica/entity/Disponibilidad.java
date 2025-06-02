package com.clinicaregional.clinica.entity;

import jakarta.persistence.Entity;

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
public class Disponibilidad {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "disponibilidad_id")
    private Long id;

    private DiaSemana diaSemana;
    
    private LocalTime horaInicio;

    private LocalTime horaFin;

    private String notas;

    @OneToMany
    @JoinColumn(name = "medico_id")
    private Medico medico;

}
