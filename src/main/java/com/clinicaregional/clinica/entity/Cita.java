package com.clinicaregional.clinica.entity;

import java.sql.Date;
import java.sql.Time;

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
    private Date fecha;
    private Time hora;
    private boolean estadoCita;
    private String notas;
    private String antecedentes;
}
