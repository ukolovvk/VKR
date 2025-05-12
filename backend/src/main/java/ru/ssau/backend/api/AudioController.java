package ru.ssau.backend.api;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.backend.entity.Audio;
import ru.ssau.backend.service.AudioService;

/**
 * @author ukolov-victor
 */

@RestController
@RequestMapping("/audio")
public class AudioController {

    private final AudioService audioService;

    @Autowired
    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> UploadAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username,
            @RequestParam("model") String model) {
        if (audioService.uploadAudio(file, username, model))
           return new ResponseEntity<>(new HashMap<>(){{ put("message", "File has been successfully uploaded"); }}, HttpStatus.OK);
        return new ResponseEntity<>(new HashMap<>(){{ put("message", "internal error"); }}, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/history")
    public ResponseEntity<Map<Long, Audio>> GetHistory(@RequestParam("username") String username) {
        Map<Long, Audio> result = audioService.getHistory(username);
        if (result == null)
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
