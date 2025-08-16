package com.example.ygup.survey;

import com.example.ygup.entity.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> { }
