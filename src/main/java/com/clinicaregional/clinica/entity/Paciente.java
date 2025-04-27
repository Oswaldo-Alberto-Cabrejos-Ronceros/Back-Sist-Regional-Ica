package com.clinicaregional.clinica.entity;

import com.clinicaregional.clinica.enums.Sexo;
import com.clinicaregional.clinica.enums.TipoSangre;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pacientes")
@SuperBuilder
//para filtro
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class Paciente extends EntidadConEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;

    private String apellidos;

    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TipoDocumento tipoDocumento;

    private String numeroIdentificacion;

    private String nacionalidad;

    private String telefono;

    private String direccion;

    @Enumerated(EnumType.STRING)
    private TipoSangre tipoSangre;

    private String antecedentes;

    @OneToOne(cascade = CascadeType.ALL)
    private Usuario usuario;

}
