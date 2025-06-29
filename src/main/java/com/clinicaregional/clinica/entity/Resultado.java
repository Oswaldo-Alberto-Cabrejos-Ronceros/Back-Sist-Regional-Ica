package com.clinicaregional.clinica.entity;

import org.hibernate.annotations.Filter;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resultados")
@Filter(name = "estadoActivo", condition = "estado = :estado")
@Builder
public class Resultado extends EntidadConEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diagnostico;

    private String tratamiento;

    private String notasResultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id")
    private Cita cita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_clinico_id")
    private HistorialClinico historialClinico;

}
