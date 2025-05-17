//package ru.ssau.backend.service;
//
///**
// * @author ukolov-victor
// */
//import java.util.concurrent.CompletableFuture;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KafkaProducerService {
//
//    private static final String AUDIO_TOPIC = "audio";
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    @Autowired
//    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendMessage(String message) {
//        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(AUDIO_TOPIC, message);
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                System.out.println("Sent message=[" + message +
//                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
//            } else {
//                System.out.println("Unable to send message=[" +
//                        message + "] due to : " + ex.getMessage());
//            }
//        });
//    }
//}
