package com.clinicaregional.clinica.entity;

import java.time.LocalDateTime;
import com.clinicaregional.clinica.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cita_id")
    private Long id;
    private LocalDateTime fechaHora;
    private EstadoCita estadoCita;
    private String notas;
    private String antecedentes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguro_id")
    private Seguro seguro;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cobertura_id")
    private Cobertura cobertura;
}
