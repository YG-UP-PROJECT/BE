package com.example.ygup.survey;

import com.example.ygup.entity.SurveyEntity;
import com.example.ygup.enums.*;
import com.example.ygup.survey.dto.SurveyCreateRequest;
import com.example.ygup.survey.dto.SurveyCreateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @Transactional
    public SurveyCreateResponse handleSurvey(SurveyCreateRequest req) {
        String combo = computeComboCode(req);

        // 위치 고정 적용 (부천시 역곡동)
        String fixedLocationName = "부천시 역곡동";
        Double fixedLat = 37.4839;
        Double fixedLng = 126.8131;

        SurveyEntity entity = SurveyEntity.builder()
                .mood(req.mood())
                .foodStyle(req.foodStyle())
                .diningStyle(req.diningStyle())
                .timeSlot(req.timeSlot())
                .weather(req.weather())
                .tempC(req.tempC())
                .comboCode(combo)
                .locationName(fixedLocationName)
                .latitude(fixedLat)
                .longitude(fixedLng)
                .build();

        SurveyEntity saved = surveyRepository.save(entity);

        var snap = new SurveyCreateResponse.SurveySnapshot(
                saved.getMood(), saved.getFoodStyle(), saved.getDiningStyle(),
                saved.getTimeSlot(), saved.getWeather(), saved.getTempC(),
                saved.getLocationName()
        );

        return new SurveyCreateResponse(saved.getId(), saved.getComboCode(), snap);
    }

    @Transactional(readOnly = true)
    public SurveyCreateResponse getLatestSurvey() {
        SurveyEntity latest = surveyRepository.findTopByOrderByIdDesc();
        if (latest == null) {
            return new SurveyCreateResponse(null, null, null);
        }
        var snap = new SurveyCreateResponse.SurveySnapshot(
                latest.getMood(), latest.getFoodStyle(), latest.getDiningStyle(),
                latest.getTimeSlot(), latest.getWeather(), latest.getTempC(),
                latest.getLocationName()
        );

        return new SurveyCreateResponse(latest.getId(), latest.getComboCode(), snap);
    }

    private String computeComboCode(SurveyCreateRequest req) {
        return (req.mood() == Mood.QUIET ? "Q" : "N") +
                (req.foodStyle() == FoodStyle.HEALTHY ? "H" : "E") +
                (req.diningStyle() == DiningStyle.ALONE ? "A" : "T") +
                // LUNCH -> D(낮), DINNER -> N(밤)
                (req.timeSlot() == TimeSlot.LUNCH ? "D" : "N");
    }
}
