package com.clinicaregional.clinica.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "opiniones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 300, nullable = false)
    private String comentario;

    @Column(nullable = false)
    private Integer calificacion; //Calificación de 1 a 5

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    //Relación con Cita
    //@OneToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "cita_id", nullable = false)
    //private Cita cita;

    @Column(nullable = false)
    private Boolean visible = true; // Indica si la opinión es visible para otros usuarios

}
