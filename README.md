# BE
4팀 백엔드 팀장 박찬현

# feat: 장소 검색 백엔드 추가 (카카오+구글 통합)

## 개요
- 카카오맵/구글 플레이스 API를 통합한 **장소 검색 API** 추가

## 환경설정
```yaml
# src/main/resources/application.yml
place:
  kakao:
    rest-key: ${KAKAO_REST_KEY}
  google:
    api-key: ${GOOGLE_API_KEY}
server:
  port: 8080

## 테스트
GET /places/search
http://localhost:8080/places/search?query=역곡 고깃집&limit=3

## 응답 결과
    {
        "name": "원조춘천숯불닭갈비 본관",
        "category": "음식점 > 한식 > 육류,고기 > 닭요리",
        "address": "경기 부천시 원미구 역곡동 76-3",
        "roadAddress": "경기 부천시 원미구 역곡로 17",
        "kakaoX": 126.81102120248265,
        "kakaoY": 37.486724580640065,
        "kakaoPlaceUrl": "http://place.map.kakao.com/16764162",
        "googleRating": 4.4,
        "googleUserRatingsTotal": 159,
        "googleReviews": [
            {
                "author": "한영빈 (Youngbin Han)",
                "text": "맛있습니다. 미리 조리해서 나오다보니 좀 빨리 먹을 수 있어 좋은 거 같네요. 고기도 다 구워주셔서 편하기도 하구요. 물 막국수는 좀 심심한게 닭갈비랑 같이 먹으면 맛있습니다.",
                "rating": 5
            },
            {
                "author": "안봄날",
                "text": "역곡에서 ㅈ한손가락에 꼽을만큼 맛있는 닭갈비집. 경인문고 옆 본점과 길 건너편 분점 모두 맛은 같고. 분점은 좀 쾌적하다면 본점은 옛날식 분위기지만 왠지 정감있는 분위기.",
                "rating": 5
            },
            {
                "author": "서군",
                "text": "여기 로컬 맛집이라고 친구 소개로 갔는데, 너무 장사 잘 되는 곳이라 2호점까지 생겼다고 합니다. 일단 맛이 진짜 최고..너무 맛있고, 숯불의 향이 진짜 죽여줍니다. 그리고 이모님들이 닭갈비 정성스럽게 구워주시고 양도 생각했던 것 보다 엄청 많아요. 진짜 배부르게 두둑히 먹었습니다! 진짜 춘천에서 먹었던 맛이라 똑같아요. 아니 더 맛있었던 것 같기도 합니다. 완전히 강력 추천합니다ㅠㅠㅠㅠㅠ",
                "rating": 4
            }
        ]
    }
