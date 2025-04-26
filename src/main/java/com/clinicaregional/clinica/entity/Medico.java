package com.clinicaregional.clinica.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medico_id")
    private Long id;

    private String nombres;
    private String apellidos;
    private String numeroColegiatura;
    private String numeroRNE;
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
