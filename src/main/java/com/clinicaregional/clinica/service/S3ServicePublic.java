package com.clinicaregional.clinica.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3ServicePublic {
    String subirArchivo(MultipartFile archivo, String rutaDestino);

    void eliminarArchivo(String key);

    String generarUrlPublico(String key);
}
