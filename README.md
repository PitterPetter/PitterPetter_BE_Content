# 📄 Content Service (Loveventure)

블로그 게시물 및 마이페이지(개인/커플 프로필 관리) 기능을 제공하는 서비스.  
추천 코스에 대한 커플의 후기(블로그)를 저장하고, 마이페이지에서 개인/커플 정보를 관리할 수 있음.

---

## 🔧 Tech Stack

- **Lang/Runtime**: Java 17, Spring Boot 3.5.5  
- **Core**: Spring Web
- **Auth**: JWT (Auth Service 연동)  
- **Data**: Spring Data JPA, postgreSQL  
- **Docs**: springdoc-openapi 2.3.0 (Swagger UI)  
- **Test**: JUnit5

---

## 📂 프로젝트 구조

```
content-service/
├─ src/main/java/com/loventure/content
│  ├─ blog/            # 블로그(게시글 CRUD)
│  ├─ mypage/          # 마이페이지 CRUD
│  ├─ common/          # 공용 예외/유틸
│  ├─ config/          # JPA, Security, Swagger 설정
└─ src/test/java/...   # 단위/통합 테스트
```

---

## ✨ 주요 기능

### 📘 블로그 (Blog)

- 블로그 게시글 CRUD
  - **생성**: 제목, 내용, 이미지 업로드 지원
  - **조회**: 단건 조회, 목록 조회(페이징/검색 지원)
  - **수정/삭제**: 작성자만 가능
  - **좋아요**: 블로그 글에 좋아요 지원
  - **댓글**: 블로그 글에 댓글 달기 지원

- 추천된 코스 후기를 위해 구현 (Course Service와 연동 가능)

### 🙍 마이페이지 (MyPage)

- 개인 정보 관리 (닉네임, 프로필 이미지, 선호도)
- 커플 프로필 조회 (Auth Service의 페어링 정보 기반)
- 활동 기록 (내가 작성한 블로그 모아보기)

---

## 🔑 API 예시

```http
# 블로그 목록 조회
GET /api/blogs?page=0&size=10

# 마이페이지 조회
GET /api/mypage/me

# 마이페이지 수정
PUT /api/mypage/me
Body:
{
  "nickname": "러브벤처유저",
  "profileImageUrl": "https://s3.aws.../profile.png"
}
```

---

## 🐳 로컬 실행

```bash
./gradlew bootRun
```
---

## 📘 API 문서

- Swagger UI: `http://localhost:8082/swagger-ui.html`  
- OpenAPI JSON: `http://localhost:8082/v3/api-docs`

---

## 🔐 인증

- 모든 API는 **JWT 인증** 기반 (Auth Service 발급 토큰 사용)  
- 사용자와 커플 ID는 JWT Claim 기반으로 매핑  

---

## 🧩 브랜치 전략
* main: 배포용 안정 버전
* develop: 통합 개발 브랜치
* feature/*: 기능 단위 개발 브랜치

---

## 📜 커밋 규칙
* feat: 새로운 기능 추가
* fix: 버그 수정
* docs: 문서 수정
* refactor: 코드 리팩토링
* test: 테스트 코드
