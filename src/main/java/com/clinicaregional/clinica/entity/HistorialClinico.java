package com.clinicaregional.clinica.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "historial_clinico")
@Filter(name = "estadoActivo", condition = "estado = :estado")

public class HistorialClinico extends EntidadConEstado{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
}
