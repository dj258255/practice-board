# 🏢 Practice Board - 완전한 모니터링 스택을 갖춘 게시판 시스템

## 📋 프로젝트 개요

Spring Boot 3.5.4 기반의 고가용성 게시판 애플리케이션으로, **로드밸런싱**, **완전한 모니터링 스택**, **부하 테스트**를 포함한 엔터프라이즈급 아키텍처를 구현했습니다.

## 🏗️ 시스템 아키텍처

```
                           🌍 사용자 요청
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Nginx (Port 80)  │  ← 로드밸런서
                    │   Load Balancer     │
                    └─────────┬───────────┘
                              │
                    ┌─────────┴───────────┐
                    │                     │
                    ▼                     ▼
        ┌───────────────────┐    ┌───────────────────┐
        │  Spring Boot App1 │    │  Spring Boot App2 │
        │    (Port 8081)    │    │    (Port 8082)    │
        └─────────┬─────────┘    └─────────┬─────────┘
                  │                        │
                  └──────────┬─────────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
            ▼                ▼                ▼
    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
    │   MySQL     │  │    Redis    │  │  RabbitMQ   │
    │ (Database)  │  │ (Session/   │  │ (Message    │
    │             │  │  Cache)     │  │  Queue)     │
    └─────────────┘  └─────────────┘  └─────────────┘

                      📊 모니터링 레이어
    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
    │ Prometheus  │  │   Grafana   │  │    Loki     │
    │  (메트릭)     │  │ (대시보드)   │  │  (로그)     │
    └─────────────┘  └─────────────┘  └─────────────┘
            ▲                ▲                ▲
            │                │                │
            └────────────────┼────────────────┘
                             │
                    ┌─────────────┐
                    │  Promtail   │
                    │ (로그 수집기) │
                    └─────────────┘
```

## 🚀 기술 스택

### 📦 애플리케이션 레이어
| 기술 | 버전 | 용도 |
|------|------|------|
| **Spring Boot** | 3.5.4 | 메인 애플리케이션 프레임워크 |
| **Spring Security** | 6.x | 인증/인가 + OAuth2 |
| **Spring Data JPA** | 3.x | ORM 및 데이터 접근 |
| **QueryDSL** | 5.1.0 | 동적 쿼리 생성 |
| **MapStruct** | 1.6.3 | DTO 매핑 |
| **Java** | 17 | 런타임 |

### 🗄️ 데이터베이스 & 메시징
| 기술 | 용도 |
|------|------|
| **MySQL 8.0** | 메인 데이터베이스 |
| **Redis 7** | 세션 저장소 + 캐시 |
| **RabbitMQ 4** | 메시지 큐 (이벤트 처리) |

### ⚖️ 인프라스트럭처
| 기술 | 용도 |
|------|------|
| **Nginx** | 로드밸런서 (least_conn) |
| **Docker** | 컨테이너화 |
| **Docker Compose** | 오케스트레이션 |

### 📊 모니터링 & 관측성
| 기술 | 용도 |
|------|------|
| **Prometheus** | 메트릭 수집 및 저장 |
| **Grafana** | 대시보드 및 시각화 |
| **Loki** | 로그 수집 및 저장 |
| **Promtail** | 로그 수집기 |

### 🧪 테스트 & 성능
| 기술 | 용도 |
|------|------|
| **K6** | 부하 테스트 |
| **InfluxDB** | 테스트 메트릭 저장 |

## 🔧 주요 기능

### 🏢 비즈니스 기능
- **게시판 시스템**: CRUD + 계층형 댓글
- **사용자 관리**: JWT + OAuth2 (카카오)
- **파일 업로드**: AWS S3 연동
- **실시간 알림**: RabbitMQ 기반 이벤트

### ⚡ 고가용성 기능
- **무중단 서비스**: 2개 인스턴스 + Nginx 로드밸런싱
- **세션 클러스터링**: Redis 기반 세션 공유
- **자동 복구**: 헬스체크 + failover
- **스케일링**: 수평적 확장 가능

### 📈 모니터링 기능
- **4개 자동 대시보드**: 시스템/RabbitMQ/Redis/MySQL
- **실시간 메트릭**: CPU, 메모리, 응답시간, DB 성능
- **중앙 로그**: 모든 서비스 로그 수집 및 검색
- **성능 테스트**: 자동화된 부하 테스트

## 🚀 빠른 시작

### 1️⃣ 환경 준비
```bash
# Docker & Docker Compose 설치 필요
docker --version
docker-compose --version
```

### 2️⃣ 모니터링만 실행
```bash
# 모니터링 스택만 시작
docker-compose up -d

# 또는 편리한 스크립트 사용
# Windows: start-monitoring.bat
# Linux/macOS: ./start-monitoring.sh
```

### 3️⃣ 완전한 애플리케이션 실행
```bash
# 전체 스택 (애플리케이션 + 모니터링)
docker-compose --profile app up -d
```

### 4️⃣ 부하 테스트 실행
```bash
# 성능 테스트
docker-compose --profile testing up k6
```

## 🌐 접속 정보

### 📱 애플리케이션
| 서비스 | URL | 설명 |
|--------|-----|------|
| **메인 애플리케이션** | http://localhost | Nginx를 통한 로드밸런싱 |
| **App Instance 1** | http://localhost:8081 | 직접 접속 |
| **App Instance 2** | http://localhost:8082 | 직접 접속 |
| **Swagger UI** | http://localhost/swagger-ui.html | API 문서 |

### 📊 모니터링 대시보드
| 서비스 | URL | 계정 |
|--------|-----|------|
| **Grafana** | http://localhost:3000 | admin/admin123 |
| **Prometheus** | http://localhost:9090 | - |
| **Loki** | http://localhost:3100 | - |

### 🔧 관리 도구
| 서비스 | URL | 계정 |
|--------|-----|------|
| **RabbitMQ Management** | http://localhost:15672 | devuser/devpassword |
| **MySQL** | localhost:3306 | app_user/app_password123 |
| **Redis** | localhost:6379 | - |

## 📊 Grafana 대시보드

자동으로 생성되는 4개의 대시보드:

1. **🖥️ System Overview**: 전체 서비스 상태 및 헬스체크
2. **🐰 RabbitMQ Monitoring**: 메시지 큐 상태, 처리량, 연결 수
3. **🔴 Redis Monitoring**: 캐시 성능, 메모리 사용률, 명령어 통계
4. **🗄️ MySQL Monitoring**: 데이터베이스 성능, 연결 수, 쿼리 통계

## 🧪 성능 테스트

K6를 통한 부하 테스트 시나리오:
```javascript
// 10명 동시 사용자, 9분간 테스트
stages: [
    { duration: '2m', target: 10 },  // 램프업: 0→10명
    { duration: '5m', target: 10 },  // 유지: 10명  
    { duration: '2m', target: 0 },   // 램프다운: 10→0명
]
```

**테스트 항목**:
- Nginx 로드밸런서 성능
- 개별 애플리케이션 인스턴스 응답
- API 엔드포인트 응답시간
- 데이터베이스 부하 테스트

## 🗂️ 프로젝트 구조

```
practice-board/
├── 📁 src/main/java/              # Spring Boot 소스코드
│   └── io/github/beom/practiceboard/
│       ├── 📁 config/             # 설정 클래스
│       ├── 📁 board/              # 게시판 도메인
│       ├── 📁 user/               # 사용자 도메인  
│       ├── 📁 comment/            # 댓글 도메인
│       ├── 📁 security/           # 보안 설정
│       └── 📁 event/              # 이벤트 처리
├── 📁 src/main/resources/         # 설정 파일
│   ├── application.properties     # 메인 설정
│   ├── application-prod.properties # 프로덕션 설정
│   └── log4j2.xml                # 로깅 설정
├── 📁 docker-compose.yml          # 컨테이너 오케스트레이션
├── 📁 nginx/                     # Nginx 설정
├── 📁 grafana/                   # Grafana 대시보드
├── 📁 prometheus/                # Prometheus 설정
├── 📁 loki/                      # Loki 설정
├── 📁 promtail/                  # Promtail 설정
├── 📁 k6/                        # 부하 테스트 스크립트
├── 📁 logs/                      # 애플리케이션 로그
└── 📁 .env                       # 환경 변수
```

## 🔐 보안 고려사항 & .gitignore

### ❌ GitHub에 올리면 안되는 파일들
우리의 `.gitignore`에서 제외하는 중요한 파일들:

```gitignore
# 🔐 보안 관련 파일들
.env                              # DB 패스워드, API 키 등
.env.*
src/main/resources/application.properties    # JWT 시크릿, OAuth 키
src/main/resources/application-prod.properties
src/main/resources/application-*.properties

# 🗂️ 런타임 생성 파일들  
logs/                             # 애플리케이션 로그
mysql_data/                       # DB 데이터
redis_data/                       # Redis 데이터
grafana_data/                     # Grafana 설정
prometheus_data/                  # 메트릭 데이터
build/                           # 빌드 결과물
```

### 🛡️ 실제 운영시 보안 강화 필요사항
1. **환경 변수**: 모든 비밀 정보를 환경 변수로 관리
2. **HTTPS**: SSL/TLS 인증서 적용
3. **DB 암호화**: 데이터베이스 암호화
4. **네트워크 보안**: VPN, 방화벽 설정
5. **정기 업데이트**: 의존성 보안 패치

## 🔍 로그 모니터링

Promtail이 수집하는 로그들:

### 📝 수집 대상
- **🚀 Spring Boot**: JSON + 표준 형식 로그
- **🌐 Nginx**: 액세스 로그 + 에러 로그
- **🗄️ MySQL**: 데이터베이스 에러/쿼리 로그
- **🐳 Docker**: 모든 컨테이너 로그
- **🖥️ System**: 시스템 레벨 로그

### 🔍 Grafana에서 로그 검색
```
# 에러 로그만 보기
{level="ERROR"}

# 특정 서비스 로그
{job="spring-boot"}
{job="nginx"} 

# HTTP 상태별
{status="500"}
{method="GET"}
```

## 🚦 실행 상태 확인

### ✅ 정상 동작 확인 체크리스트
- [ ] **Grafana**: http://localhost:3000 접속 가능
- [ ] **4개 대시보드** 모두 데이터 표시
- [ ] **애플리케이션**: http://localhost 응답  
- [ ] **로드밸런싱**: 8081, 8082 번갈아 응답
- [ ] **MySQL**: 연결 및 테이블 자동 생성
- [ ] **Redis**: 세션 저장 동작
- [ ] **RabbitMQ**: 큐 생성 및 메시지 처리
- [ ] **로그 수집**: Grafana에서 로그 검색 가능

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

---

**🎯 이 프로젝트는 실제 운영환경을 고려한 완전한 모니터링 스택을 갖춘 엔터프라이즈급 애플리케이션 아키텍처의 예시입니다.**
