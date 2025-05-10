package com.clinicaregional.clinica.entity;

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
@SuperBuilder
@Table(name = "seguro_coberturas")
//para filtro
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class SeguroCobertura extends EntidadConEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Seguro seguro;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cobertura cobertura;
}
