package ru.ssau.backend.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.backend.entity.Audio;
import ru.ssau.backend.repo.AudioRepository;
import ru.ssau.backend.repo.UsersRepository;

/**
 * @author ukolov-victor
 */
@Service
public class AudioService {

    @Value("${s3.bucket}")
    private String bucket;

    @Value("uploaded")
    private String uploadedStatus;

    private final AudioRepository audioRepository;
    private final MinioClient minioClient;
    private final UsersRepository usersRepository;
    private final MLService mlService;

    @Autowired
    public AudioService(AudioRepository audioRepository, MinioClient minioClient, UsersRepository usersRepository, MLService mlService) {
        this.audioRepository = audioRepository;
        this.minioClient = minioClient;
        this.usersRepository = usersRepository;
        this.mlService = mlService;
    }

    public boolean uploadAudio(MultipartFile file, String username) {
        try {
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(this.bucket)
                    .object(file.getOriginalFilename())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            Long userid = usersRepository.getUserIdByUsername(username);
            if (userid == null) return false;
            Audio savedAudio = audioRepository.save(new Audio(file.getOriginalFilename(), file.getSize(), System.currentTimeMillis(), response.etag(), userid, this.bucket, uploadedStatus));
            mlService.Notify(response.etag());
            return savedAudio.getId() > 0;
        } catch (Exception ex) {
            System.out.println("failed to save audio: " + ex.getMessage());
            return false;
        }
    }

    public Map<Long, Audio> getHistory(String username) {
        Long userId = usersRepository.getUserIdByUsername(username);
        if (userId == null) {
            return null;
        }
        List<Audio> audios = audioRepository.findByUserId(userId);
        return audios.stream().collect(Collectors.toMap(Audio::getId, Function.identity()));
    }
}
