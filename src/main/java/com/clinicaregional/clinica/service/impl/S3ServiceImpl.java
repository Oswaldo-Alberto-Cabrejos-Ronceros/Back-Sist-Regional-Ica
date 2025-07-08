package com.clinicaregional.clinica.service.impl;
import com.clinicaregional.clinica.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Autowired
    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    //bucketname

    @Value("${AWS_S3_BUCKET_NAME}")
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
    public byte[] recuperarArchivo(String key) {
        try {
            //construimos getObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
            //objeto recuperado
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            //lo retornamos como un arreglo de bytes
            return s3Object.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
