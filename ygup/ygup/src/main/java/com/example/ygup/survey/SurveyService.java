package com.example.ygup.survey;

import com.example.ygup.entity.SurveyEntity;
import com.example.ygup.enums.*;
import com.example.ygup.survey.dto.SurveyCreateRequest;
import com.example.ygup.survey.dto.SurveyCreateResponse;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    public SurveyCreateResponse handleSurvey(SurveyCreateRequest req) {
        String combo = computeComboCode(req);

        SurveyEntity entity = SurveyEntity.builder()
                .mood(req.mood())
                .foodStyle(req.foodStyle())
                .diningStyle(req.diningStyle())
                .timeSlot(req.timeSlot())
                .weather(req.weather())
                .tempC(req.tempC())
                .comboCode(combo)
                .build();

        SurveyEntity saved = surveyRepository.save(entity);

        var snap = new SurveyCreateResponse.SurveySnapshot(
                saved.getMood(), saved.getFoodStyle(), saved.getDiningStyle(),
                saved.getTimeSlot(), saved.getWeather(), saved.getTempC()
        );

        return new SurveyCreateResponse(saved.getId(), saved.getComboCode(), snap);
    }

    public SurveyCreateResponse getLatestSurvey() {
        SurveyEntity latest = surveyRepository.findTopByOrderByIdDesc();

        var snap = new SurveyCreateResponse.SurveySnapshot(
                latest.getMood(), latest.getFoodStyle(), latest.getDiningStyle(),
                latest.getTimeSlot(), latest.getWeather(), latest.getTempC()
        );

        return new SurveyCreateResponse(latest.getId(), latest.getComboCode(), snap);
    }

    private String computeComboCode(SurveyCreateRequest req) {
        return (req.mood() == Mood.QUIET ? "Q" : "N") +
                (req.foodStyle() == FoodStyle.HEALTHY ? "H" : "E") +
                (req.diningStyle() == DiningStyle.ALONE ? "A" : "T") +
                (req.timeSlot() == TimeSlot.LUNCH ? "L" : "D");
    }
}
