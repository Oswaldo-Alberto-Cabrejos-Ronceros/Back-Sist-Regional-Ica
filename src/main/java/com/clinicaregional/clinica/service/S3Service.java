package com.clinicaregional.clinica.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
     String subirArchivo(MultipartFile archivo, String rutaDestino);
     void eliminarArchivo(String key);
     byte[] recuperarArchivo(String key);
}
