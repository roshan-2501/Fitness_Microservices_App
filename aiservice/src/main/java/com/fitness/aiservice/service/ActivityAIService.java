package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt=createPromptActivity(activity);
        String aiResponse= geminiService.getAnswer(prompt);
        log.info("RESPONSE FROM AI :{} "+aiResponse);
        return processAiResponse(activity,aiResponse);
    }

    //convert json data into objects
    private Recommendation processAiResponse(Activity activity,String aiResponse){
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(aiResponse);

            JsonNode textNode=rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent=textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();
//            log.info("PARSED RESPONSE FROM AI :{} "+jsonContent);
            JsonNode analysisJson=mapper.readTree(jsonContent);
            JsonNode analysisNode=analysisJson.path("analysis");

            StringBuilder fullAnalysis=new StringBuilder();
            addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall:");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace:");
            addAnalysisSection(fullAnalysis,analysisNode,"HeatRate","Heart Rate:");
            addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","Calories:");

            List<String> improvements=extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions =extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety =extractSafety(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before starting with your exercide ",
                        "Stay hydrated",
                        "Listen to your body and make changes"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafety(JsonNode safetyNode) {
        List<String> safety =new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(item ->
                safety.add(item.asText())
            );
        }
        return safety.isEmpty()?
                Collections.singletonList("Follow general safety safety guidelines"):
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionNode) {
        List<String> sugesstions =new ArrayList<>();
        if(suggestionNode.isArray()){
            suggestionNode.forEach(suggestion ->{
                String workout=suggestion.path("workout").asText();
                String description=suggestion.path("description").asText();
                sugesstions.add(String.format("%s : %s",workout,description));
            });
        }
        return sugesstions.isEmpty()?
                Collections.singletonList("No specific suggestions provided"):
                sugesstions;

    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements=new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement ->{
                String area=improvement.path("area").asText();
                String detail=improvement.path("recommendation").asText();
                improvements.add(String.format("%s : %s",area,detail));
            });
        }
        return improvements.isEmpty()?
                Collections.singletonList("No specific improvements provided"):
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }


    private String createPromptActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
