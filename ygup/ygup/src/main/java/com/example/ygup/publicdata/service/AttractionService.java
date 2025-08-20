package com.example.ygup.publicdata.service;

import com.example.ygup.publicdata.dto.*;
import com.example.ygup.publicdata.entity.Attraction;
import com.example.ygup.publicdata.entity.AttractionImage;
import com.example.ygup.publicdata.exception.NotFoundException;
import com.example.ygup.publicdata.repository.AttractionImageRepository;
import com.example.ygup.publicdata.repository.AttractionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttractionService {

    private final TourApiClient client;
    private final AttractionRepository attractionRepo;
    private final AttractionImageRepository imageRepo;

    public AttractionService(TourApiClient client,
                             AttractionRepository attractionRepo,
                             AttractionImageRepository imageRepo) {
        this.client = client;
        this.attractionRepo = attractionRepo;
        this.imageRepo = imageRepo;
    }

    // 외부 API
    public PageResponse<AttractionSummaryDto> searchKeyword(String keyword, int page, int size) {
        return client.searchKeyword(keyword, page, size);
    }

    public PageResponse<AttractionSummaryDto> nearby(double lat, double lng, int radius, int page, int size) {
        return client.locationBased(lat, lng, radius, page, size);
    }

    public AttractionDetailDto detail(long contentId, Integer contentTypeId) {
        return client.getDetail(contentId, contentTypeId);
    }

    public AttractionDetailDto detail(long contentId) {
        return detail(contentId, null);
    }

    // DB CRUD
    @Transactional
    public Attraction create(Attraction a) {
        if (a.getContentId() == null) throw new IllegalArgumentException("contentId는 필수입니다 (TourAPI 기준 PK).");
        return attractionRepo.save(a);
    }

    public Attraction get(long contentId) {
        return attractionRepo.findById(contentId)
                .orElseThrow(() -> new NotFoundException("Not found contentId=" + contentId));
    }

    @Transactional
    public Attraction update(long contentId, Attraction update) {
        Attraction exist = get(contentId);
        exist.setTitle(update.getTitle());
        exist.setAddr1(update.getAddr1());
        exist.setAddr2(update.getAddr2());
        exist.setTel(update.getTel());
        exist.setMapX(update.getMapX());
        exist.setMapY(update.getMapY());
        exist.setFirstImage(update.getFirstImage());
        exist.setCat1(update.getCat1());
        exist.setCat2(update.getCat2());
        exist.setCat3(update.getCat3());
        exist.setAreaCode(update.getAreaCode());
        exist.setSigunguCode(update.getSigunguCode());
        return attractionRepo.save(exist);
    }

    @Transactional
    public void delete(long contentId) {
        imageRepo.deleteByContentId(contentId);
        attractionRepo.deleteById(contentId);
    }

    public List<Attraction> all() { return attractionRepo.findAll(); }

    @Transactional
    public AttractionDetailDto syncFromExternal(long contentId) {
        AttractionDetailDto dto = client.getDetail(contentId, null);
        Attraction a = attractionRepo.findById(contentId).orElseGet(Attraction::new);
        a.setContentId(dto.getContentId());
        a.setTitle(dto.getTitle());
        a.setAddr1(dto.getAddr());
        a.setTel(dto.getTel());
        a.setMapX(dto.getMapX());
        a.setMapY(dto.getMapY());
        a.setFirstImage(dto.getFirstImage());
        attractionRepo.save(a);

        imageRepo.deleteByContentId(contentId);
        for (AttractionImageDto img : dto.getImages()) {
            AttractionImage e = new AttractionImage();
            e.setContentId(contentId);
            e.setOriginUrl(img.getOriginUrl());
            e.setSmallUrl(img.getSmallUrl());
            imageRepo.save(e);
        }
        return dto;
    }
}
