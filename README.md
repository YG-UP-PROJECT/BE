# BE
4팀 백엔드 팀장 박찬현

# BE – AI 음식점 추천 백엔드 (MVP)

## Quick Start
- JDK **17+**
- 실행: `./gradlew bootRun`
- 기본 포트: **8080**

## Branch 규칙
- `main`: 배포/안정본 (직접 커밋 금지)
- `feature/*`: 기능 개발용 (예: `feature/weather-api`)

## 작업 플로우
1) `git checkout main && git pull`
2) `git checkout -b feature/기능명`
3) 개발 → `git add . && git commit -m "feat: ..."`
4) `git push origin feature/기능명`
5) PR 생성 → 리뷰 1명 이상 후 `main` 병합

## 커밋 컨벤션
- `feat: ...` 새 기능
- `fix: ...` 버그 수정
- `chore/docs/test: ...` 기타

## API (MVP 초안)
- `POST /api/recommendations`
  - req: 성향/기분/예산/선호/제외/좌표
  - res: 요약 + 후보 리스트(가게명/주소/거리/추천이유/메뉴)
