package com.clinicaregional.clinica.entity;

import com.clinicaregional.clinica.enums.TurnoTrabajo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "recepcionistas")
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class Recepcionista extends EntidadConEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;
    private String numeroDocumento;

    @ManyToOne
    @JoinColumn(name = "tipo_documento_id")
    private TipoDocumento tipoDocumento;

    private String telefono;

    private String direccion;

    @Enumerated(EnumType.STRING)
    private TurnoTrabajo turnoTrabajo;

    private LocalDate fechaContratacion;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
}
