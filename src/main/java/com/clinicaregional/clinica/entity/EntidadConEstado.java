package com.clinicaregional.clinica.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
//para filtro
@FilterDef(name = "estadoActivo", parameters = @ParamDef(name = "estado", type = Boolean.class))
@Filter(name = "estadoActivo", condition = "estado = :estado")
@MappedSuperclass
public abstract class EntidadConEstado {
    private Boolean estado=true;
}
