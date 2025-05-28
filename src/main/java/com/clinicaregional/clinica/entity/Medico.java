package com.clinicaregional.clinica.entity;

import java.time.LocalDateTime;

import com.clinicaregional.clinica.enums.TipoContrato;
import com.clinicaregional.clinica.enums.TipoMedico;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "medicos")
@SuperBuilder
//para filtro
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class Medico extends EntidadConEstado{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medico_id")
    private Long id;

    private String nombres;
    private String apellidos;
    private String numeroColegiatura;
    private String numeroRNE;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String direccion;
    private String descripcion;
    private String imagen;
    @Column(name = "fecha_contratacion")
    private LocalDateTime fechaContratacion;

    @Column(name = "tipo_Contrato")
    private TipoContrato tipoContrato;

    @Column(name = "tipo_medico")
    private TipoMedico tipoMedico;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

}
