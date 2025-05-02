package com.clinicaregional.clinica.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OpinionRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del médico es obligatorio")
    private Long medicoId;

    // Validación de cita
    // @NotNull(message = "El ID de la cita es obligatorio")
    // private Long citaId;

    @NotBlank(message = "El comentario no debe estar vacío")
    @Size(max = 300, message = "El comentario no debe exceder los 300 caracteres")
    private String comentario;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

}
