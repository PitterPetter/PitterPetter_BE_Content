 # PitterPetter Content Service

PitterPetter의 콘텐츠 관리를 담당하는 마이크로서비스입니다. 커플들의 데이트 다이어리, 댓글, 이미지 관리를 제공합니다.

## 🏗️ 아키텍처 개요

### MSA 환경 구성
- **Config Server**: 중앙화된 설정 관리
- **Kubernetes**: 컨테이너 오케스트레이션
- **Gateway**: API 게이트웨이를 통한 라우팅
- **Auth Service**: JWT 기반 인증/인가
- **Content Service**: 콘텐츠 관리 (현재 서비스)

### 기술 스택
- **Backend**: Spring Boot 3.4.10, Java 17
- **Database**: PostgreSQL 15
- **Storage**: Google Cloud Storage (GCS)
- **Cache**: Caffeine (Spring Cache)
- **Communication**: OpenFeign (MSA 통신)
- **Documentation**: Swagger/OpenAPI 3.0
- **Security**: Spring Security, JWT
- **Build**: Gradle 8.x

## 📁 프로젝트 구조

```
src/main/java/PitterPatter/loventure/content/
├── ContentApplication.java                 # 메인 애플리케이션
├── domain/                              # 도메인별 기능
│   ├── diary/                            # 다이어리 도메인
│   │   ├── application/                  # 애플리케이션 계층
│   │   │   ├── dto/                      # DTO 클래스들
│   │   │   └── usecase/                  # 유스케이스 구현
│   │   ├── domain/                       # 도메인 계층
│   │   │   └── entity/                   # 엔터티
│   │   ├── service/                      # 서비스 계층
│   │   └── ui/                           # 컨트롤러
│   ├── comment/                          # 댓글 도메인
│   └── image/                            # 이미지 도메인
└── global/                               # 글로벌 설정
    ├── annotation/                       # 커스텀 어노테이션
    ├── common/                          # 공통 클래스
    ├── config/                          # 설정 클래스
    ├── error/                           # 에러 처리
    ├── infra/                           # 인프라 계층
    └── security/                        # 보안 설정
```

## 🚀 주요 기능

### 1. 다이어리 관리
- **생성**: 커플의 데이트 다이어리 작성
- **조회**: 다이어리 목록 및 상세 조회 (페이지네이션)
- **수정**: 다이어리 내용 수정
- **삭제**: 다이어리 삭제
- **이미지 첨부**: 다이어리에 이미지 첨부 가능

### 2. 댓글 관리
- **작성**: 다이어리에 댓글 작성
- **수정**: 본인이 작성한 댓글 수정
- **삭제**: 본인이 작성한 댓글 삭제

### 3. 이미지 관리
- **업로드**: GCS를 통한 이미지 업로드 (Presigned URL 방식)
- **다운로드**: 이미지 다운로드 URL 생성
- **상태 관리**: 업로드 상태 추적 (PENDING, UPLOADED, FAILED)
- **삭제**: 이미지 삭제 (GCS + DB)

## 🔧 API 엔드포인트

### 다이어리 API
```
POST   /api/diaries                    # 다이어리 생성
GET    /api/diaries                    # 다이어리 목록 조회
GET    /api/diaries/{diaryId}          # 다이어리 상세 조회
POST   /api/diaries/{diaryId}          # 다이어리 수정
DELETE /api/diaries/{diaryId}          # 다이어리 삭제
```

### 댓글 API
```
POST   /api/diaries/{diaryId}/comments           # 댓글 생성
PUT    /api/diaries/{diaryId}/comments/{commentId}  # 댓글 수정
DELETE /api/diaries/{diaryId}/comments/{commentId}  # 댓글 삭제
```

### 이미지 API
```
PATCH  /api/images/{imageId}/complete   # 이미지 업로드 완료
PATCH  /api/images/{imageId}/fail       # 이미지 업로드 실패
DELETE /api/images/{imageId}            # 이미지 삭제
```

## 🗄️ 데이터베이스 스키마

### Diary (다이어리)
```sql
CREATE TABLE diary (
    diary_id VARCHAR(20) PRIMARY KEY,     -- TSID
    couple_id VARCHAR(20) NOT NULL,        -- 커플 ID
    user_id VARCHAR(20) NOT NULL,         -- 작성자 ID
    author_name VARCHAR(50) NOT NULL,      -- 작성자 이름
    course_id VARCHAR(20) NOT NULL,       -- 코스 ID
    rating DOUBLE,                         -- 평점
    title VARCHAR(200) NOT NULL,           -- 제목
    content TEXT NOT NULL,                 -- 내용
    image_id VARCHAR(20),                 -- 이미지 ID (FK)
    created_at TIMESTAMP,                 -- 생성일시
    updated_at TIMESTAMP                  -- 수정일시
);
```

### Comment (댓글)
```sql
CREATE TABLE comment (
    comment_id VARCHAR(20) PRIMARY KEY,    -- TSID
    diary_id VARCHAR(20) NOT NULL,        -- 다이어리 ID (FK)
    user_id VARCHAR(20) NOT NULL,          -- 작성자 ID
    author_name VARCHAR(50) NOT NULL,      -- 작성자 이름
    content TEXT NOT NULL,                 -- 댓글 내용
    created_at TIMESTAMP,                  -- 생성일시
    updated_at TIMESTAMP                  -- 수정일시
);
```

### Image (이미지)
```sql
CREATE TABLE image (
    image_id VARCHAR(20) PRIMARY KEY,       -- TSID
    uuid VARCHAR(36) UNIQUE NOT NULL,     -- UUID
    object_path VARCHAR(255) NOT NULL,    -- GCS 객체 경로
    image_type VARCHAR(20) NOT NULL,       -- 이미지 타입 (DIARY, PROFILE)
    reference_id VARCHAR(20),              -- 참조 ID
    status VARCHAR(20) NOT NULL,          -- 상태 (PENDING, UPLOADED, FAILED)
    content_type VARCHAR(100) NOT NULL,   -- MIME 타입
    size_bytes BIGINT NOT NULL,           -- 파일 크기
    original_file_name VARCHAR(255) NOT NULL, -- 원본 파일명
    created_at TIMESTAMP,                 -- 생성일시
    updated_at TIMESTAMP                  -- 수정일시
);
```

## 🔐 보안 및 인증

### JWT 토큰 기반 인증
- **토큰 추출**: `@CurrentUser`, `@CurrentCouple` 어노테이션으로 자동 추출
- **권한 검증**: 커플별 데이터 접근 제어
- **CORS 설정**: 허용된 도메인에서만 접근 가능

### 보안 설정
```java
// CORS 허용 도메인
- https://loventure.us
- https://*.loventure.us
- http://localhost:* (개발환경)

// JWT 시크릿 키
jwt.secret: ${JWT_SECRET}
jwt.expiration: 3600000 (1시간)
```

## 🏪 외부 서비스 연동

### Auth Service 통신
```java
@FeignClient(name = "authClient", url = "${auth.service.url}")
public interface AuthClient {
    @GetMapping("/internal/user/{userId}")
    UserProfileResponse getUserById(@PathVariable String userId);
}
```

### Google Cloud Storage
- **업로드**: Presigned URL 방식으로 클라이언트 직접 업로드
- **다운로드**: 서버에서 Presigned URL 생성
- **삭제**: 서버에서 GCS 객체 삭제

## 📊 캐싱 전략

### Caffeine Cache
```java
@Cacheable(value = "imageDownloadUrls", key = "#imageId")
public String generateDownloadUrl(String imageId) {
    // 이미지 다운로드 URL 캐싱 (55분)
}
```

## 🚀 배포 및 운영

### Docker 컨테이너
```dockerfile
# Multi-stage build
FROM eclipse-temurin:17-jdk-jammy AS build
# ... 빌드 과정

FROM eclipse-temurin:17-jre-jammy AS runtime
# ... 런타임 설정
```

### Kubernetes 배포
- **Config Server**: 중앙화된 설정 관리
- **Service Discovery**: 내부 서비스 간 통신
- **Health Check**: Actuator 엔드포인트 활용

### 환경별 설정
- **local**: Mock Auth Server 사용
- **prod**: 실제 Auth Service 연동

## 🧪 테스트

### 로컬 개발 환경
```bash
# PostgreSQL 컨테이너 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun
```

### API 문서
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8082/v3/api-docs

## 📝 주요 설계 원칙

### 1. DDD (Domain-Driven Design)
- **도메인별 분리**: Diary, Comment, Image 도메인
- **계층화 아키텍처**: Application, Domain, Service, UI 계층

### 2. MSA 통신
- **OpenFeign**: Auth Service와의 비동기 통신
- **Circuit Breaker**: 서비스 장애 시 대응

### 3. 이미지 처리
- **Presigned URL**: 클라이언트 직접 업로드로 서버 부하 감소
- **상태 관리**: 업로드 진행 상황 추적
- **캐싱**: 다운로드 URL 캐싱으로 성능 최적화

### 4. 에러 처리
- **통합 에러 처리**: `GlobalExceptionHandler`
- **에러 코드 체계**: 도메인별 에러 코드 정의
- **로깅**: 구조화된 로깅으로 디버깅 용이

## 🔄 데이터 플로우

### 다이어리 생성 플로우
1. **요청 수신**: JWT 토큰에서 사용자 정보 추출
2. **사용자 조회**: Auth Service에서 사용자 이름 조회
3. **다이어리 저장**: Diary 엔터티 생성 및 저장
4. **이미지 처리**: 이미지가 있는 경우 메타데이터 생성 및 Presigned URL 발급
5. **응답 반환**: 생성된 다이어리 정보 반환

### 이미지 업로드 플로우
1. **메타데이터 생성**: Image 엔터티 생성 (PENDING 상태)
2. **Presigned URL 발급**: GCS 업로드용 URL 생성
3. **클라이언트 업로드**: 클라이언트가 직접 GCS에 업로드
4. **완료 콜백**: `/api/images/{imageId}/complete` 호출
5. **상태 업데이트**: Image 상태를 UPLOADED로 변경

## 🛠️ 개발 가이드

### 로컬 개발 환경 설정
1. **PostgreSQL 실행**: `docker-compose up -d`
2. **환경 변수 설정**: `application-local.yml` 참조
3. **애플리케이션 실행**: `./gradlew bootRun`

### 새로운 기능 추가
1. **도메인 설계**: DDD 원칙에 따라 도메인별 분리
2. **API 설계**: RESTful API 설계 원칙 준수
3. **에러 처리**: `ErrorCode`에 새로운 에러 코드 추가
4. **테스트 작성**: 단위 테스트 및 통합 테스트 작성

## 📈 성능 최적화

### 데이터베이스 최적화
- **인덱스**: `couple_id`, `user_id` 등 자주 조회되는 컬럼에 인덱스
- **페이지네이션**: 대용량 데이터 조회 시 페이지네이션 적용

### 캐싱 전략
- **이미지 URL**: 다운로드 URL 캐싱 (55분)
- **사용자 정보**: Auth Service 응답 캐싱

### 이미지 처리 최적화
- **Presigned URL**: 서버 부하 감소
- **파일 크기 제한**: 5MB 이하로 제한
- **지원 형식**: JPEG, PNG, GIF, WebP, HEIC, HEIF

## 🔍 모니터링 및 로깅

### 로깅 레벨
```yaml
logging:
  level:
    PitterPatter.loventure.content: DEBUG
    org.hibernate.SQL: debug
```

### Health Check
- **Actuator**: `/actuator/health` 엔드포인트
- **데이터베이스**: PostgreSQL 연결 상태 확인

## 📚 추가 문서

- [API 문서](http://localhost:8082/swagger-ui.html)
- [CORS 설정 가이드](docs/TROUBLESHOOTING_CORS_IMAGE_UPLOAD.md)
- [ID 타입 불일치 해결](docs/TROUBLESHOOTING_ID_TYPE_MISMATCH.md)
- [프레젠테이션 스크립트](docs/PRESENTATION_SCRIPT.md)

---

**PitterPetter Content Service** - 커플들의 소중한 추억을 담는 콘텐츠 관리 서비스
