package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.request.ResultadoRequest;
import com.clinicaregional.clinica.dto.response.ResultadoResponse;
import com.clinicaregional.clinica.entity.Cita;
import com.clinicaregional.clinica.entity.HistorialClinico;
import com.clinicaregional.clinica.entity.Resultado;
import org.springframework.stereotype.Component;

@Component
public class ResultadoMapper {
    //de request a entity
    public Resultado toEntity(ResultadoRequest resultadoRequest) {
        //generamos cita
        Cita cita = new Cita();
        cita.setId(resultadoRequest.getCitaId());
        //generamos historial
        HistorialClinico historialClinico = new HistorialClinico();
        historialClinico.setId(resultadoRequest.getHistorialClinicoId());

        return Resultado.builder().diagnostico(resultadoRequest.getDiagnostico()).tratamiento(resultadoRequest.getTratamiento()).notasResultado(resultadoRequest.getNotasResultado())
                .cita(cita).historialClinico(historialClinico).build();
    }

    //de entity a response
    public ResultadoResponse toResponse(Resultado resultado) {
        return new ResultadoResponse(resultado.getId(), resultado.getDiagnostico(), resultado.getTratamiento(), resultado.getNotasResultado(), resultado.getContieneArchivo()
                , resultado.getCita().getId(), resultado.getHistorialClinico().getId());
    }
}
