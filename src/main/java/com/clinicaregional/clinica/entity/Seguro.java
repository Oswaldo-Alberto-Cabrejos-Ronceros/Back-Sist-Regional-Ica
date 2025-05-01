package com.clinicaregional.clinica.entity;

import com.clinicaregional.clinica.enums.EstadoSeguro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seguros")
@SuperBuilder
@Filter(name="estadoActivo", condition = "estado = :estado")
public class Seguro extends EntidadConEstado{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    private EstadoSeguro estadoSeguro;
}