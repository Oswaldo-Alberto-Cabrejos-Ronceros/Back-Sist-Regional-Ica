package com.clinicaregional.clinica.service.impl;

import com.clinicaregional.clinica.service.S3ServicePublic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class S3ServicePublicImpl implements S3ServicePublic {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Autowired
    public S3ServicePublicImpl(S3Client s3Client, S3Presigner s3Presigner) {

        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    //bucket name

    @Value("${AWS_S3_BUCKET_NAME_PUBLIC}")
    private String bucketName;

    @Override
    public String subirArchivo(MultipartFile archivo, String rutaDestino) {
        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        try {
            //construimos clave(ruta)
            String key = rutaDestino + "/" + nombreArchivo;

            //creamos solicitud putObject
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key)
                    .contentType(archivo.getContentType()).build();
            //subimos
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(archivo.getInputStream(), archivo.getSize()));
            //retornamos key
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Error al subir el archivo");
        }
    }

    @Override
    public void eliminarArchivo(String key) {
        try {
            //generamos deleteObject
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(key)
                    .build();
            //borramos
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar archivo");
        }
    }

    @Override
    public String generarUrlPublico(String key) {
        Duration duracion = Duration.ofMinutes(15);
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().signatureDuration(duracion).getObjectRequest(
                    getObjectRequest
            ).build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el url publico");
        }
    }
}
