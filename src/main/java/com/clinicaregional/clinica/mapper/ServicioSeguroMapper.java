package com.clinicaregional.clinica.mapper;

import com.clinicaregional.clinica.dto.ServicioSeguroDTO;
import com.clinicaregional.clinica.entity.Cobertura;
import com.clinicaregional.clinica.entity.Seguro;
import com.clinicaregional.clinica.entity.Servicio;
import com.clinicaregional.clinica.entity.ServicioSeguro;
import org.springframework.stereotype.Component;

@Component
public class ServicioSeguroMapper {
    public ServicioSeguroDTO mapToServicioSeguroDTO(ServicioSeguro servicioSeguro) {
        return new ServicioSeguroDTO(
                servicioSeguro.getId(),
                servicioSeguro.getServicio().getId(),
                servicioSeguro.getSeguro().getId(),
                servicioSeguro.getCobertura().getId()
        );
    }

    public ServicioSeguro mapToServicioSeguro(ServicioSeguroDTO servicioSeguroDTO) {
        Servicio servicio = new Servicio();
        servicio.setId(servicioSeguroDTO.getServicioId());
        Seguro seguro = new Seguro();
        seguro.setId(servicioSeguroDTO.getSeguroId());
        Cobertura cobertura = new Cobertura();
        cobertura.setId(servicioSeguroDTO.getCoberturaId());
        return new ServicioSeguro(
                servicioSeguroDTO.getId(),
                servicio,
                seguro,
                cobertura
        );

    }

}
