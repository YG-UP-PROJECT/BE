// src/main/java/com/example/ygup/publicdata/service/TourApiClient.java
package com.example.ygup.publicdata.service;

import com.example.ygup.publicdata.dto.AttractionDetailDto;
import com.example.ygup.publicdata.dto.AttractionImageDto;
import com.example.ygup.publicdata.dto.AttractionSummaryDto;
import com.example.ygup.publicdata.dto.PageResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
public class TourApiClient {

    private static final String PATH_SEARCH_KEYWORD = "/searchKeyword2";
    private static final String PATH_LOCATION_BASED = "/locationBasedList2";
    private static final String PATH_DETAIL_COMMON  = "/detailCommon2";
    private static final String PATH_DETAIL_IMAGE   = "/detailImage2";

    private final TourApiProperties props;
    private final WebClient webClient;

    // ⬇️ 추가: JSON/XML 파서를 모두 보유
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    public TourApiClient(TourApiProperties props, WebClient.Builder builder) {
        this.props = props;
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(props.getTimeoutMs()));
        this.webClient = builder
                .baseUrl(props.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                // ⬇️ 수정: Accept에 JSON, XML 둘 다 명시. GET에 Content-Type 강제하지 않음.
                .defaultHeaders(h -> {
                    h.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML));
                })
                .build();
    }

    /* ========= 외부 공개 메서드 ========= */

    public PageResponse<AttractionSummaryDto> searchKeyword(String keyword, int page, int size) {
        if (page <= 0) page = 1;
        if (size <= 0) size = props.getPageSizeDefault();

        Map<String, Object> p = new LinkedHashMap<>();
        p.put("keyword", safeStr(keyword));   // 공백/따옴표 제거 (URI 인코딩은 buildUri에서)
        p.put("pageNo", page);
        p.put("numOfRows", size);

        JsonNode root = get(PATH_SEARCH_KEYWORD, p);
        return toSummaryPage(root, page, size);
    }

    public PageResponse<AttractionSummaryDto> locationBased(double lat, double lng, int radius, int page, int size) {
        if (page <= 0) page = 1;
        if (size <= 0) size = props.getPageSizeDefault();
        if (radius <= 0) radius = 800;

        Map<String, Object> p = new LinkedHashMap<>();
        p.put("mapX", lng);
        p.put("mapY", lat);
        p.put("radius", radius);
        p.put("pageNo", page);
        p.put("numOfRows", size);
        p.put("arrange", "E");

        JsonNode root = get(PATH_LOCATION_BASED, p);
        return toSummaryPage(root, page, size);
    }

    public AttractionDetailDto getDetail(long contentId) {
        return getDetail(contentId, null);
    }

    public AttractionDetailDto getDetail(long contentId, Integer contentTypeId) {
        // 1) 공통 상세 먼저 시도
        Map<String, Object> p1 = new HashMap<>();
        p1.put("contentId", contentId);
        if (contentTypeId != null) p1.put("contentTypeId", contentTypeId);
        p1.put("overviewYN", "Y");
        p1.put("defaultYN", "Y");
        p1.put("firstImageYN", "Y");

        JsonNode common = get(PATH_DETAIL_COMMON, p1);

        // 아이템이 없고 타입을 모르거나 틀린 경우: 대표 타입으로 재시도
        if (firstItem(common) == null && contentTypeId == null) {
            int[] candidates = {12, 14, 38, 39}; // 관광지, 문화시설, 쇼핑, 음식점
            for (int ct : candidates) {
                p1.put("contentTypeId", ct);
                common = get(PATH_DETAIL_COMMON, p1);
                if (firstItem(common) != null) {
                    contentTypeId = ct; // 찾았으면 이미지 호출에도 같이 씀
                    break;
                }
            }
        }

        // 2) 이미지 목록
        Map<String, Object> p2 = new HashMap<>();
        p2.put("contentId", contentId);
        if (contentTypeId != null) p2.put("contentTypeId", contentTypeId);
        p2.put("imageYN", "Y");
        p2.put("subImageYN", "Y");
        JsonNode images = get(PATH_DETAIL_IMAGE, p2);

        // 3) 매핑
        AttractionDetailDto dto = new AttractionDetailDto();

        JsonNode itm = firstItem(common);
        if (itm != null) {
            dto.setContentId(getLong(itm, "contentid"));
            dto.setTitle(getText(itm, "title"));
            String addr1 = getText(itm, "addr1");
            String addr2 = getText(itm, "addr2");
            dto.setAddr(joinAddr(addr1, addr2));
            dto.setTel(getText(itm, "tel"));
            dto.setMapX(getDoubleObj(itm, "mapx"));
            dto.setMapY(getDoubleObj(itm, "mapy"));
            dto.setOverview(getText(itm, "overview"));
            String firstImage = firstNonEmpty(getText(itm, "firstimage"), getText(itm, "firstimage2"));
            dto.setFirstImage(firstImage);
        }

        List<AttractionImageDto> list = new ArrayList<>();
        for (JsonNode im : iterateItems(images)) {
            AttractionImageDto img = new AttractionImageDto();
            img.setOriginUrl(getText(im, "originimgurl"));
            img.setSmallUrl(getText(im, "smallimageurl"));
            if (isNotBlank(img.getOriginUrl()) || isNotBlank(img.getSmallUrl())) list.add(img);
        }
        dto.setImages(list);

        return dto;
    }

    /* ========= 내부 공통 ========= */

    /**
     * 응답 Content-Type을 확인해 JSON/XML로 분기 파싱.
     * - JSON: JsonNode로 바로
     * - XML (application/xml, text/xml): 문자열로 받고 XmlMapper.readTree(...)
     * - 그 외: 본문 첫 글자를 보고 추정 파싱 (방어)
     */
    private JsonNode get(String path, Map<String, ?> params) {
        URI uri = buildUri(path, params);
        return webClient.get()
                .uri(uri)
                .exchangeToMono(res -> {
                    MediaType ct = res.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);

                    if (MediaType.APPLICATION_JSON.isCompatibleWith(ct)) {
                        return res.bodyToMono(JsonNode.class);
                    } else if (MediaType.APPLICATION_XML.isCompatibleWith(ct) ||
                            MediaType.TEXT_XML.isCompatibleWith(ct)) {
                        return res.bodyToMono(String.class)
                                .map(xml -> {
                                    try {
                                        return xmlMapper.readTree(xml);
                                    } catch (Exception e) {
                                        throw new RuntimeException("XML 파싱 실패", e);
                                    }
                                });
                    } else {
                        // 서버가 Content-Type을 엉뚱하게 주는 경우를 대비한 방어 로직
                        return res.bodyToMono(String.class)
                                .map(body -> {
                                    try {
                                        String trimmed = body == null ? "" : body.trim();
                                        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                                            return objectMapper.readTree(trimmed);
                                        }
                                        return xmlMapper.readTree(trimmed);
                                    } catch (Exception e) {
                                        throw new RuntimeException("응답 파싱 실패(Content-Type=" + ct + ")", e);
                                    }
                                });
                    }
                })
                .block();
    }

    private URI buildUri(String path, Map<String, ?> params) {
        // serviceKey는 "디코딩 키"를 넣어도 자동 인코딩해서 안전하게 처리
        String sk = props.getServiceKey();
        if (sk != null && !sk.contains("%")) {
            sk = URLEncoder.encode(sk, StandardCharsets.UTF_8);
        }

        UriComponentsBuilder ub = UriComponentsBuilder
                .fromHttpUrl(props.getBaseUrl())
                .path(path)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "MatBTI")
                .queryParam("_type", "json")            // JSON 요청 시도 (무시될 수 있어도 유지)
                .queryParam("serviceKey", sk);

        if (params != null) {
            params.forEach((k, v) -> {
                if (v == null) return;
                Object value = v;
                if (v instanceof String s) {
                    // 공백/따옴표 제거 + UTF-8 인코딩
                    String cleaned = safeStr(s);
                    value = URLEncoder.encode(cleaned, StandardCharsets.UTF_8);
                }
                ub.queryParam(k, value);
            });
        }
        // true: 우리가 넣은 % 인코딩을 다시 건드리지 않게 보존
        return ub.build(true).toUri();
    }

    private static String safeStr(String s) {
        if (s == null) return null;
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    /* ===================== 매핑/유틸 ===================== */

    private PageResponse<AttractionSummaryDto> toSummaryPage(JsonNode root, int page, int size) {
        long total = getLong(safe(root, "response", "body"), "totalCount");
        List<AttractionSummaryDto> items = new ArrayList<>();
        for (JsonNode n : iterateItems(root)) {
            AttractionSummaryDto dto = new AttractionSummaryDto();
            dto.setContentId(getLong(n, "contentid"));
            dto.setTitle(getText(n, "title"));
            dto.setAddr(firstNonEmpty(getText(n, "addr1"), getText(n, "addr2")));
            dto.setMapX(getDoubleObj(n, "mapx"));
            dto.setMapY(getDoubleObj(n, "mapy"));
            dto.setThumbnail(firstNonEmpty(getText(n, "firstimage"), getText(n, "firstimage2")));
            dto.setCategory(firstNonEmpty(getText(n, "cat3"), getText(n, "cat2"), getText(n, "cat1")));
            // dist(Double) → 반올림 Long(meters)
            Double dist = getDoubleObj(n, "dist");
            dto.setDistanceMeters(dist == null ? null : Math.round(dist));
            items.add(dto);
        }
        return new PageResponse<>(page, size, total, items);
    }

    private Iterable<JsonNode> iterateItems(JsonNode root) {
        JsonNode body = safe(root, "response", "body");
        JsonNode items = safe(body, "items");
        JsonNode item = items == null ? null : items.get("item");
        List<JsonNode> list = new ArrayList<>();
        if (item == null || item.isNull()) return list;
        if (item.isArray()) item.forEach(list::add); else list.add(item);
        return list;
    }

    private JsonNode firstItem(JsonNode root) { for (JsonNode n : iterateItems(root)) return n; return null; }

    private JsonNode safe(JsonNode node, String... path) {
        JsonNode cur = node;
        if (cur == null) return null;
        for (String p : path) { if (cur == null) return null; cur = cur.get(p); }
        return (cur == null || cur.isMissingNode() || cur.isNull()) ? null : cur;
    }
    private String getText(JsonNode n, String field) { JsonNode v = n == null ? null : n.get(field); return (v == null || v.isNull()) ? null : v.asText(null); }
    private long getLong(JsonNode n, String field) { JsonNode v = n == null ? null : n.get(field); if (v == null || v.isNull()) return 0L; try { return Long.parseLong(v.asText()); } catch (Exception e) { return 0L; } }
    private Long getLongObj(JsonNode n, String field) { JsonNode v = n == null ? null : n.get(field); if (v == null || v.isNull()) return null; try { return Long.parseLong(v.asText()); } catch (Exception e) { return null; } }
    private Double getDoubleObj(JsonNode n, String field) { JsonNode v = n == null ? null : n.get(field); if (v == null || v.isNull()) return null; try { return Double.parseDouble(v.asText()); } catch (Exception e) { return null; } }
    private boolean isNotBlank(String s) { return s != null && !s.isBlank(); }
    private String firstNonEmpty(String... vals) { if (vals == null) return null; for (String s : vals) if (isNotBlank(s)) return s; return null; }
    private String joinAddr(String a1, String a2) { if (isNotBlank(a1) && isNotBlank(a2)) return a1 + " " + a2; return isNotBlank(a1) ? a1 : a2; }
}
