# BE
4팀 백엔드 팀장 박찬현

## 개요
- 공공데이터포털 한국관광공사 TourAPI 연동
- 기능: 키워드 검색, 반경 검색, (선택) 상세/싱크, 로컬 CRUD

## 실행
### dev
```bash
./gradlew bootRun
# 또는 IntelliJ Run (기본 dev)
```

### prod
```bash
./gradlew clean build
java -jar ygup/ygup/build/libs/ygup-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 프로필/환경변수
- 활성 프로필: dev(기본), prod  
- 필수 환경변수  
  - `TOURAPI_SERVICE_KEY` : 공공데이터포털 디코딩 키  
- 포트: 8080  

### DB
- 스키마: `matbti`  
- 예) `jdbc:mysql://localhost:3306/matbti?useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true`  

### CORS
- dev: `http://localhost:3000` 허용 (WebConfig)  
- prod: 배포 도메인만 허용 (예: `https://front.example.com`)  

## API
1) 주변 검색 (거리순)  
   - `GET /api/attractions/nearby`  
   - Query: `lat(double), lng(double), radius(int, 기본 800), page(int, 기본1), size(int, 기본20)`  
   - 응답: `contentId,title,addr,thumbnail,mapX,mapY,category,distanceMeters`  
   - 예시  
     ```
     GET /api/attractions/nearby?lat=37.5&lng=126.8&radius=800&page=1&size=3
     ```

2) 키워드 검색(보조)  
   - `GET /api/attractions/search`  
   - Query: `keyword, page, size (옵션)`  
   - 선택적으로 `mapX,mapY` 포함 시 `distanceMeters` 추가  
   - 예시  
     ```
     GET /api/attractions/search?keyword=부천&page=1&size=10
     GET /api/attractions/search?keyword=부천&mapX=126.97&mapY=37.56&page=1&size=10
     ```

3) 외부→로컬 싱크(운영/캐시)  -> 프론트 사용 X
   - `POST /api/attractions/sync/{contentId}`  

4) 로컬 CRUD(내부 관리용)  -> 프론트 사용 X
   - `GET /api/attractions`  
   - `GET /api/attractions/local/{contentId}`  
   - `POST /api/attractions`  
   - `PUT /api/attractions/local/{contentId}`  
   - `DELETE /api/attractions/local/{contentId}`  

### 에러/예외 규칙
- `400`: 잘못된 파라미터 (예: 타입 불일치, 범위 오류)  
- `404`: 리소스 없음  
- `5xx`: 서버/외부 API 실패  

예시:
```json
{ "message": "파라미터 'lat' 형식이 잘못되었습니다." }
```

### 파라미터 제약
- lat: -90 ~ 90  
- lng: -180 ~ 180  
- radius: 1 ~ 2000 (기본 800)  
- size: 1 ~ 50 (프론트 통일 nearby=3)  

### 데이터 모델(요약)
- `Attraction(contentId(PK), title, addr, mapX, mapY, category, ... )`  
- `AttractionImage(id, contentId(FK), url, ... )`  
- DTO: `AttractionSummaryDto, AttractionDetailDto, AttractionImageDto, PageResponse<T>`  


