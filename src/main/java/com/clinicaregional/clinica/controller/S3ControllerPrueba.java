package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.service.S3Service;
import com.clinicaregional.clinica.service.S3ServicePublic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/prueba/s3")
public class S3ControllerPrueba {

    private final S3Service s3Service;
    private final S3ServicePublic s3ServicePublic;

    @Autowired
    public S3ControllerPrueba(S3Service s3Service, S3ServicePublic s3ServicePublic) {
        this.s3Service=s3Service;
        this.s3ServicePublic=s3ServicePublic;
    }

    @PostMapping
    public ResponseEntity<String> subirArchivo(@RequestParam MultipartFile archivo){
        return ResponseEntity.status(HttpStatus.CREATED).body(s3Service.subirArchivo(archivo,"images"));
    }

    @GetMapping
    public ResponseEntity<byte[]> recuperarArchivo(@RequestParam String key){
        //recuperamos archivo
        byte [] archivo = s3Service.recuperarArchivo(key);
        //configuramos los headers de la respuesta
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",key);

        return new ResponseEntity<>(archivo,httpHeaders,HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarArchivo(@RequestParam String key){
        s3Service.eliminarArchivo(key);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/link-public")
    public ResponseEntity<String> obtenerLinkPublic(@RequestParam String key){
        return ResponseEntity.ok(s3ServicePublic.generarUrlPublico(key));
    }

}
