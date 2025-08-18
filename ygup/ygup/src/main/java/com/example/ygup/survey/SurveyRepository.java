package com.example.ygup.survey;

import com.example.ygup.entity.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {
    SurveyEntity findTopByOrderByIdDesc();  // 가장 최근 설문 1개 조회
}
