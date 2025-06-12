package com.clinicaregional.clinica.controller;

import com.clinicaregional.clinica.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/prueba/s3")
public class S3ControllerPrueba {

    private final S3Service s3Service;

    @Autowired
    public S3ControllerPrueba(S3Service s3Service) {
        this.s3Service=s3Service;
    }

    @PostMapping
    public ResponseEntity<String> subirArchivo(@RequestParam MultipartFile archivo){
        return ResponseEntity.status(HttpStatus.CREATED).body(s3Service.subirArchivo(archivo,"images"));
    }

    @GetMapping("/{key}")
    public ResponseEntity<byte[]> recuperarArchivo(@PathVariable String key){
        //recuperamos archivo
        byte [] archivo = s3Service.recuperarArchivo(key);
        //configuramos los headers de la respuesta
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",key);

        return new ResponseEntity<>(archivo,httpHeaders,HttpStatus.OK);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> eliminarArchivo(@PathVariable String key){
        s3Service.eliminarArchivo(key);
        return ResponseEntity.noContent().build();
    }

}
