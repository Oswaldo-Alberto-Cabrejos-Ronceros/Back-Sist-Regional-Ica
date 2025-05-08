package com.clinicaregional.clinica.entity;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estadoCita;
    private String notas;
    private String antecedentes;
}
