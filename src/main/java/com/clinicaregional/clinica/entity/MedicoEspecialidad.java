
package com.clinicaregional.clinica.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.EmbeddedId;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "medico_especialidad")
@SuperBuilder
//para filtro
@Filter(name = "estadoActivo", condition = "estado = :estado")
public class MedicoEspecialidad extends EntidadConEstado {

    @EmbeddedId
    private MedicoEspecialidadId id;

    private LocalDate desdeFecha;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId("medicoId")
    @JoinColumn(name = "medico_id", insertable = false, updatable = false)
    private Medico medico;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId("especialidadId")
    @JoinColumn(name = "especialidad_id", insertable = false, updatable = false)
    private Especialidad especialidad;
}