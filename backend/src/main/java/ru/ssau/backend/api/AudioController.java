package ru.ssau.backend.api;

import java.util.HashMap;
import java.util.Map;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ukolov-victor
 */

@RestController
@RequestMapping("/audio")
public class AudioController {

    @Value("${s3.bucket}")
    private String bucket;

    @Autowired
    private MinioClient minioClient;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> UploadAudio(@RequestParam("file") MultipartFile file) {
        try {
           if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(this.bucket).build())) {
               minioClient.makeBucket(MakeBucketArgs.builder().bucket(this.bucket).build());
           }
           minioClient.putObject(PutObjectArgs.builder()
                           .bucket(this.bucket)
                           .object(file.getOriginalFilename())
                           .stream(file.getInputStream(), file.getSize(), -1)
                           .contentType(file.getContentType())
                           .build()
           );
            return new ResponseEntity<>(new HashMap<>(){{ put("message", "File has been successfully uploaded"); }}, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println("minio error: " + ex.getMessage());
            return new ResponseEntity<>(new HashMap<>(){{ put("message", "internal error"); }}, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
