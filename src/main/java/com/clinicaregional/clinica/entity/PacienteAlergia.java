package com.clinicaregional.clinica.entity;

import com.clinicaregional.clinica.enums.Gravedad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paciente_alergias",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"paciente_id","alergia_id"}
                )
        }
)
@SuperBuilder
//para filtro
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class PacienteAlergia extends EntidadConEstado{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alergia_id", nullable = false)
    private Alergia alergia;

    @Enumerated(EnumType.STRING)
    private Gravedad gravedad;
}
