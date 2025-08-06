package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListener {

    private final ActivityAIService aiService;

    @RabbitListener(queues="activity.queue")
    public void processActivity(Activity activity){
        log.info("Received activity for processing: {}",activity.getId());
        log.info("Generated Recommendation: {}",aiService.generateRecommendation(activity));

    }

}
