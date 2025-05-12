package ru.ssau.backend.service;

import java.util.*;
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

    @Value("filename")
    private String filenameTag;

    @Value("user")
    private String userTag;

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

    public boolean uploadAudio(MultipartFile file, String username, String model) {
        System.out.println("!!! upload " + model + " " + username);
        if (model.isEmpty()) {
            System.out.println("model is empty");
            return false;
        }
        String uuid = UUID.randomUUID().toString();
        String filename = file.getOriginalFilename();
        if (filename == null) {
            System.out.println("filename is null");
            return false;
        }
        Map<String, String> metadata = Map.of(filenameTag, filename, userTag, uuid);
        try {
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(this.bucket)
                    .object(uuid)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .userMetadata(metadata)
                    .build()
            );
            Long userid = usersRepository.getUserIdByUsername(username);
            if (userid == null) {
                System.out.println("user hasn't been found");
                return false;
            }
            Audio savedAudio = audioRepository.save(new Audio(uuid, file.getOriginalFilename(), file.getSize(), System.currentTimeMillis(), response.etag(), userid, this.bucket, uploadedStatus, model));
            mlService.Notify(uuid, model);
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
        return audios.stream()
                .sorted(Comparator.comparing(Audio::getUploadedTimestamp))
                .collect(Collectors.toMap(Audio::getId, Function.identity(), (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
