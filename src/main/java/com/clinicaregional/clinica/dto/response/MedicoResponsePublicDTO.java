package com.clinicaregional.clinica.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicoResponsePublicDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String numeroColegiatura;
    private String numeroRNE;
    private String descripcion;
    private String imagen;
}
