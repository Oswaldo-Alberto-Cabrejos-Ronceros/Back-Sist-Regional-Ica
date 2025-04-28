package com.clinicaregional.clinica.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "administradores")
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class Administrador extends EntidadConEstado{
    @Id
    @GeneratedValue
    private Long id;

    private String nombres;
    private String apellidos;
    private String numeroDocumento;

    @ManyToOne
    @JoinColumn(name = "tipo_documento_id")
    private TipoDocumento tipoDocumento;

    private String telefono;

    private String direccion;

    private LocalDate fechaContratacion;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
}
