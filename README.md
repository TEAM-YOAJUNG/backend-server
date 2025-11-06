# Gach-dong Backend Server

[![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Gach-dong** is a modern microservices-based backend platform for university club management and recruitment. Built with Spring Boot 3 and cloud-native technologies, it provides a scalable and maintainable solution for managing student clubs, applications, and notifications.

[한국어 문서](#한국어-설명) | [English Documentation](#english-documentation)

---

## 한국어 설명

### 📌 프로젝트 소개

**각동(Gach-dong)**은 대학교 동아리 관리 및 신입 부원 모집을 위한 마이크로서비스 아키텍처 기반 백엔드 플랫폼입니다.

![서비스 페이지](https://i.imgur.com/sfTB3Oe.png)

### 🎯 주요 기능

- 🔐 **사용자 인증 및 권한 관리**: JWT 기반 인증, 이메일 인증, 관리자 권한 관리
- 👥 **동아리 관리**: 동아리 정보 등록/수정, 활동 이력 관리, 연락처 관리
- 📝 **모집 공고 관리**: 커스텀 신청서 작성, 다단계 전형 프로세스 설정
- 📄 **지원서 관리**: 온라인 지원, 파일 업로드, 임시 저장, 지원 내역 조회
- 👤 **사용자 프로필**: 프로필 이미지 업로드/관리 (Google Cloud Storage)
- 🔔 **알림 시스템**: 지원 결과, 모집 공고 등 실시간 알림
- 📊 **모니터링**: Prometheus & Grafana 기반 메트릭 수집 및 시각화

### 🏗️ 아키텍처

#### Cloud Architecture

![아키텍처](https://i.imgur.com/lS1R6Mx.png)

#### 마이크로서비스 구조

```
┌─────────────────┐
│   API Gateway   │  ← 진입점 (JWT 인증, 라우팅)
│   Port: 8080    │
└────────┬────────┘
         │
    ┌────┴────────────────────────────┐
    │                                 │
    ▼                                 ▼
┌─────────┐  ┌──────────┐  ┌─────────────┐  ┌──────────────┐
│  Auth   │  │   Club   │  │ Application │  │     User     │
│ Service │  │ Service  │  │   Service   │  │   Service    │
└─────────┘  └──────────┘  └─────────────┘  └──────────────┘
     │             │              │                  │
     └─────────────┴──────────────┴──────────────────┘
                          │
                    ┌─────┴─────┐
                    │   MySQL   │
                    │   Redis   │
                    │    GCP    │
                    └───────────┘
```

### 🛠️ 기술 스택

#### Backend Framework
- **Java 17** (Amazon Corretto)
- **Spring Boot 3.3.5**
- **Spring Cloud Gateway** (API Gateway)
- **Spring Data JPA** (ORM)
- **Spring Security** (인증/권한)

#### Database & Cache
- **MySQL 8.0** (주 데이터베이스)
- **Redis** (세션, 캐시, 토큰 블랙리스트)

#### Cloud & DevOps
- **Docker & Docker Compose** (컨테이너화)
- **Jib** (컨테이너 이미지 빌드)
- **Google Cloud Storage** (파일 저장소)
- **Istio Service Mesh** (서비스 메시 관리)

#### Monitoring & Observability
- **Prometheus** (메트릭 수집)
- **Grafana** (시각화)
- **Spring Boot Actuator** (헬스 체크)
- **Micrometer** (메트릭 계측)

#### Authentication & Security
- **JWT (JJWT 0.11.5)** (토큰 기반 인증)
- **BCrypt** (비밀번호 암호화)
- **Email Verification** (SMTP)

#### API Documentation
- **OpenAPI 3.0** (API 명세)
- **Swagger UI** (API 문서 UI)

#### Build & Development
- **Gradle** (빌드 도구)
- **Lombok** (보일러플레이트 코드 제거)

### 📦 서비스 구성

| 서비스 | 포트 | 설명 |
|--------|------|------|
| **API Gateway** | 8080 | 모든 클라이언트 요청의 진입점, JWT 인증 및 라우팅 |
| **Auth Service** | - | 사용자/관리자 인증, 회원가입, 토큰 발급 |
| **User Service** | - | 사용자 프로필 이미지 관리 |
| **Club Service** | - | 동아리 정보 및 모집 공고 관리 |
| **Application Service** | - | 지원서 작성 및 제출, 파일 업로드 |
| **Notification Service** | - | 알림 발송 (이메일, 푸시 등) |
| **Admin Service** | - | 관리자 권한 부여 및 초대 코드 관리 |

### 🚀 로컬 개발 환경 설정

#### 사전 요구사항

- **JDK 17** 이상
- **Docker & Docker Compose**
- **Gradle 8.0+**
- **MySQL 8.0** (또는 Docker로 실행)
- **Redis** (또는 Docker로 실행)

#### 환경 변수 설정

각 서비스별로 다음 환경 변수를 설정해야 합니다:

```bash
# Database
export DB_HOST=localhost
export DB_USER=your_db_user
export DB_PASSWORD=your_db_password

# JWT
export JWT_VERIFY_KEY=your_jwt_secret_key
export JWT_USER_SECRET=your_user_jwt_secret
export JWT_ADMIN_SECRET=your_admin_jwt_secret

# Redis
export REDIS_HOST=localhost
export REDIS_PASSWORD=your_redis_password

# Email (Gmail SMTP)
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password

# Google Cloud Storage
export GCP_PROJECT_ID=your_gcp_project_id
export GCP_BUCKET_NAME=your_bucket_name
export GCP_SERVICE_KEY=/path/to/service-account-key.json
```

#### Docker Compose로 실행

```bash
# MySQL 및 서비스 실행 (로컬 개발용)
cd docker
docker-compose -f docker-compose.local.yml up -d

# 또는 개발 환경용
docker-compose -f docker-compose.dev.yml up -d
```

#### Gradle로 빌드 및 실행

```bash
# 전체 프로젝트 빌드
./gradlew clean build

# 특정 서비스 실행 (예: API Gateway)
cd api-gateway
./gradlew bootRun

# 또는 로컬 개발 스크립트 사용
chmod +x local.sh
./local.sh
```

#### Docker 이미지 빌드 (Jib)

```bash
# 환경 변수 설정 후
export IMAGE_NAME=your-registry/service-name
export IMAGE_TAG=latest
export SPRING_PROFILES_ACTIVE=dev
export SERVER_PORT=8080

# 이미지 빌드 및 푸시
./gradlew jibDockerBuild  # 로컬 Docker 데몬에 빌드
./gradlew jib             # 레지스트리에 직접 푸시
```

### 📚 API 문서

서비스 실행 후, 다음 URL에서 Swagger UI를 통해 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui.html
```

#### 주요 API 엔드포인트

**인증 (Auth Service)**
```
POST   /auth/public/api/v1/register          # 회원가입
POST   /auth/public/api/v1/login             # 로그인
POST   /auth/public/api/v1/send-verification-code  # 인증 코드 발송
POST   /auth/api/v1/refresh-token            # 토큰 갱신
POST   /auth/api/v1/logout                   # 로그아웃
```

**동아리 (Club Service)**
```
GET    /club/public/api/v1/                  # 전체 동아리 목록
GET    /club/public/api/v1/{clubId}          # 동아리 상세 정보
GET    /club/public/api/v1/recruitments      # 모집 공고 목록
POST   /club/admin/api/v1/create             # 동아리 생성 (관리자)
POST   /club/admin/api/v1/recruitment/create # 모집 공고 생성 (관리자)
```

**지원 (Application Service)**
```
GET    /application/api/v1/list              # 내 지원 내역
POST   /application/api/v1/{recruitmentId}   # 지원서 제출
PUT    /application/api/v1/{recruitmentId}   # 지원서 수정
DELETE /application/api/v1/apply/{recruitmentId} # 지원 취소
```

**사용자 (User Service)**
```
POST   /user/api/v1/upload-profile-image     # 프로필 이미지 업로드
GET    /user/api/v1/profile-image/{userId}   # 프로필 이미지 조회
DELETE /user/api/v1/delete-profile-image     # 프로필 이미지 삭제
```

### 📊 모니터링

#### Actuator 엔드포인트

각 서비스는 Spring Boot Actuator를 통해 헬스 체크 및 메트릭을 제공합니다:

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Prometheus Metrics
curl http://localhost:8080/actuator/prometheus
```

#### Prometheus & Grafana

Prometheus는 각 서비스의 `/actuator/prometheus` 엔드포인트에서 메트릭을 수집하며, Grafana 대시보드를 통해 시각화됩니다.

### 🔒 보안

- **JWT 기반 인증**: Access Token (1일) + Refresh Token (7일)
- **비밀번호 암호화**: BCrypt 알고리즘 사용
- **이메일 인증**: 가천대학교 이메일(@gachon.ac.kr) 도메인 검증
- **토큰 블랙리스트**: Redis를 통한 로그아웃 토큰 관리
- **CORS 설정**: 허용된 도메인만 API 접근 가능
- **입력 검증**: Jakarta Bean Validation을 통한 요청 데이터 검증

### 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 서비스 테스트
cd club-service
./gradlew test

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

### 📝 코드 퀄리티

이 프로젝트는 다음 코드 품질 이슈를 개선 중입니다:

- ✅ **예외 처리 표준화**: 통합 예외 핸들러 적용
- ✅ **로깅 개선**: System.out 대신 SLF4J 사용
- ✅ **의존성 주입**: 생성자 주입 패턴 적용
- ✅ **코드 중복 제거**: 공통 로직 유틸리티화
- ✅ **테스트 커버리지 확대**: 단위 테스트 및 통합 테스트 추가
- ✅ **문서화**: JavaDoc 및 API 명세 보강

### 🤝 기여 방법

1. 이 저장소를 Fork 합니다
2. 새로운 브랜치를 생성합니다 (`git checkout -b feature/amazing-feature`)
3. 변경사항을 커밋합니다 (`git commit -m 'feat: Add amazing feature'`)
4. 브랜치에 푸시합니다 (`git push origin feature/amazing-feature`)
5. Pull Request를 생성합니다

#### 커밋 컨벤션

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅, 세미콜론 누락 등
refactor: 코드 리팩토링
test: 테스트 코드 추가
chore: 빌드 업무, 패키지 매니저 수정 등
```

### 📄 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

### 👨‍💻 팀 정보

**TEAM-YOAJUNG**

- 문의: [GitHub Issues](https://github.com/TEAM-YOAJUNG/backend-server/issues)
- 웹사이트: [gachdong.club](https://gachdong.club)

---

## English Documentation

### 📌 Project Overview

**Gach-dong** is a microservices-based backend platform for university club management and recruitment built with Spring Boot 3 and cloud-native technologies.

### 🎯 Key Features

- 🔐 **Authentication & Authorization**: JWT-based auth, email verification, admin role management
- 👥 **Club Management**: Club information CRUD, activity history, contact management
- 📝 **Recruitment Management**: Custom application forms, multi-stage selection process
- 📄 **Application Management**: Online application submission, file uploads, draft saving
- 👤 **User Profile**: Profile image upload/management via Google Cloud Storage
- 🔔 **Notification System**: Real-time notifications for application results and announcements
- 📊 **Monitoring**: Prometheus & Grafana metrics collection and visualization

### 🏗️ Architecture

#### Microservices Pattern

This project follows the **Gateway Pattern** with Service Mesh architecture:

- **API Gateway**: Single entry point, JWT validation, request routing
- **Service Mesh**: Istio for service-to-service communication
- **Cloud-Native**: Docker containerization, GCP integration
- **Database per Service**: Each service maintains its own data domain

#### Services Overview

| Service | Description |
|---------|-------------|
| **API Gateway** | Routes requests, validates JWT tokens, aggregates Swagger docs |
| **Auth Service** | User/Admin authentication, registration, token management |
| **User Service** | User profile image storage and retrieval |
| **Club Service** | Club information and recruitment posting management |
| **Application Service** | Application form submission and document management |
| **Notification Service** | Notification delivery (email, push) |
| **Admin Service** | Admin authorization and invite code management |

### 🛠️ Tech Stack

**Core Technologies**
- Java 17 (Amazon Corretto)
- Spring Boot 3.3.5
- Spring Cloud Gateway
- Spring Data JPA
- Spring Security

**Infrastructure**
- MySQL 8.0 (Primary Database)
- Redis (Cache, Session Store)
- Google Cloud Storage (File Storage)
- Docker & Docker Compose
- Istio Service Mesh

**Monitoring**
- Prometheus
- Grafana
- Spring Boot Actuator
- Micrometer

**Build & Deploy**
- Gradle
- Jib (Container Image Build)

### 🚀 Getting Started

#### Prerequisites

- JDK 17 or higher
- Docker & Docker Compose
- Gradle 8.0+
- MySQL 8.0
- Redis

#### Environment Variables

Set the following environment variables:

```bash
# Database
export DB_HOST=localhost
export DB_USER=your_db_user
export DB_PASSWORD=your_db_password

# JWT
export JWT_VERIFY_KEY=your_jwt_secret_key
export JWT_USER_SECRET=your_user_jwt_secret
export JWT_ADMIN_SECRET=your_admin_jwt_secret

# Redis
export REDIS_HOST=localhost
export REDIS_PASSWORD=your_redis_password

# Email (Gmail SMTP)
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password

# Google Cloud Storage
export GCP_PROJECT_ID=your_gcp_project_id
export GCP_BUCKET_NAME=your_bucket_name
export GCP_SERVICE_KEY=/path/to/service-account-key.json
```

#### Running with Docker Compose

```bash
# Start services with Docker Compose
cd docker
docker-compose -f docker-compose.local.yml up -d
```

#### Building with Gradle

```bash
# Build all services
./gradlew clean build

# Run specific service
cd api-gateway
./gradlew bootRun

# Or use local development script
chmod +x local.sh
./local.sh
```

### 📚 API Documentation

Access Swagger UI after starting the services:

```
http://localhost:8080/swagger-ui.html
```

### 📊 Monitoring

**Health Check**
```bash
curl http://localhost:8080/actuator/health
```

**Prometheus Metrics**
```bash
curl http://localhost:8080/actuator/prometheus
```

### 🔒 Security

- **JWT Authentication**: Access Token (1 day) + Refresh Token (7 days)
- **Password Encryption**: BCrypt algorithm
- **Email Verification**: University email domain validation
- **Token Blacklist**: Redis-based logout token management
- **CORS**: Configured for authorized domains only

### 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific service
cd club-service
./gradlew test
```

### 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### 👨‍💻 Contact

**TEAM-YOAJUNG**

- Issues: [GitHub Issues](https://github.com/TEAM-YOAJUNG/backend-server/issues)
- Website: [gachdong.club](https://gachdong.club)

---

**Built with ❤️ by TEAM-YOAJUNG**
