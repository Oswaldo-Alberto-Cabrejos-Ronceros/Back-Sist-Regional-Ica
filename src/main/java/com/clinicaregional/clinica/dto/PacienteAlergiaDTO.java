package com.clinicaregional.clinica.dto;

import com.clinicaregional.clinica.enums.Gravedad;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PacienteAlergiaDTO {

    private Long id;

    @NotNull(message = "Paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "Alergia es obligatorio")
    private AlergiaDTO alergia;

    @NotNull(message = "Gravedad es obligatorio")
    private Gravedad gravedad;
}
