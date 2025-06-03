package com.clinicaregional.clinica.entity;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import com.clinicaregional.clinica.enums.EstadoCita;

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
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cita_id")
    private Long citaId;

    private LocalDate fecha;

    private Time hora;

    @Enumerated(EnumType.STRING)
    private EstadoCita estadoCita;

    private String notas;

    private String antecedentes;
}
