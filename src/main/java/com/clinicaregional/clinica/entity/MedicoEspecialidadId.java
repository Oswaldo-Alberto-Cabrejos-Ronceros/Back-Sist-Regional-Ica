package com.clinicaregional.clinica.entity;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoEspecialidadId implements Serializable {

    private Long medicoId;
    private Long especialidadId;

    @Override
    public boolean equals(Object o) {
        // Si el objeto comparado es el mismo objeto actual (misma referencia en
        // memoria), son iguales
        if (this == o)
            return true;

        // Si el objeto comparado NO es de tipo MedicoEspecialidadId, no son iguales
        if (!(o instanceof MedicoEspecialidadId))
            return false;

        // Se convierte el objeto a MedicoEspecialidadId para comparar sus campos
        MedicoEspecialidadId that = (MedicoEspecialidadId) o;

        // Se comparan los valores de medicoId y especialidadId
        return Objects.equals(medicoId, that.medicoId) &&
                Objects.equals(especialidadId, that.especialidadId);
    }

    @Override
    public int hashCode() {
        // Se genera un hash único basado en medicoId y especialidadId
        // Esto es crucial para que las estructuras como HashMap o HashSet funcionen
        // correctamente
        return Objects.hash(medicoId, especialidadId);
    }
}
