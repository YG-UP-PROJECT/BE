package com.example.ygup.survey;

import com.example.ygup.entity.SurveyEntity;
import com.example.ygup.survey.dto.SurveyCreateRequest;
import com.example.ygup.survey.dto.SurveyCreateResponse;
import com.example.ygup.domain.SurveyEnums.*;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;

    // 역곡동(역곡역 중심 근처) 고정 좌표 + 표시용 문자열
    private static final double FIXED_LAT = 37.4860;
    private static final double FIXED_LNG = 126.8130;
    private static final String LOCATION_NAME = "부천시 역곡동";

    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    public SurveyCreateResponse handleSurvey(SurveyCreateRequest req) {
        String combo = computeComboCode(req);

        // 요청 위치는 무시하고 역곡동으로 고정 저장
        SurveyEntity entity = SurveyEntity.builder()
                .mood(req.mood())
                .foodStyle(req.foodStyle())
                .diningStyle(req.diningStyle())
                .timeSlot(req.timeSlot())
                .weather(req.weather())
                .tempC(req.tempC())
                .latitude(FIXED_LAT)          // 내부 계산/참고용으로는 좌표 유지
                .longitude(FIXED_LNG)
                .locationName(LOCATION_NAME)  // ★ 문자열 주소 저장
                .comboCode(combo)
                .build();

        SurveyEntity saved = surveyRepository.save(entity);

        // 응답은 좌표 대신 문자열 주소만 내려줌
        var snap = new SurveyCreateResponse.SurveySnapshot(
                req.mood(),
                req.foodStyle(),
                req.diningStyle(),
                req.timeSlot(),
                req.weather(),
                req.tempC(),
                LOCATION_NAME                  // ★ "부천시 역곡동"
        );

        return new SurveyCreateResponse(saved.getId(), combo, snap);
    }

    private String computeComboCode(SurveyCreateRequest req) {
        return (req.mood() == Mood.QUIET ? "Q" : "N") +
                (req.foodStyle() == FoodStyle.HEALTHY ? "H" : "E") +
                (req.diningStyle() == DiningStyle.ALONE ? "A" : "T") +
                (req.timeSlot() == TimeSlot.LUNCH ? "L" : "D");
    }
}
