package com.example.ygup.survey;

import com.example.ygup.entity.SurveyEntity;
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

        // 1) 엔티티로 매핑
        SurveyEntity entity = SurveyEntity.builder()
                .mood(req.mood())
                .foodStyle(req.foodStyle())
                .diningStyle(req.diningStyle())
                .timeSlot(req.timeSlot())
                .weather(req.weather())
                .tempBand(req.tempBand())
                .tempC(req.tempC())
                .latitude(req.location().latitude())
                .longitude(req.location().longitude())
                .comboCode(combo)
                .build();

        // 2) 저장 (첫 번째 입력이면 id=1)
        SurveyEntity saved = surveyRepository.save(entity);

        // 3) 응답 구성
        var snap = new SurveyCreateResponse.SurveySnapshot(
                req.mood(), req.foodStyle(), req.diningStyle(), req.timeSlot(),
                req.weather(), req.tempBand(), req.tempC(),
                req.location().latitude(), req.location().longitude()
        );

        return new SurveyCreateResponse(saved.getId(), combo, snap);
    }

    private String computeComboCode(SurveyCreateRequest req) {
        return (req.mood() == SurveyCreateRequest.Mood.QUIET ? "Q" : "N") +
                (req.foodStyle() == SurveyCreateRequest.FoodStyle.HEALTHY ? "H" : "E") +
                (req.diningStyle() == SurveyCreateRequest.DiningStyle.ALONE ? "A" : "T") +
                (req.timeSlot() == SurveyCreateRequest.TimeSlot.LUNCH ? "L" : "D");
    }
}
