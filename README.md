# mayday-backend

Mayday 서비스의 백엔드 애플리케이션입니다.

Spring Boot 기반 REST API 서버로, 사용자 인증, 수입·지출 기록 관리, 영수증 OCR, AI 기반 경비 분석, 장부 조회 및 내보내기 기능을 제공합니다.

## 기술 스택

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* MySQL
* Gradle
* OpenAI API
* Google Cloud Vision API
* Nginx
* systemd

## 로컬 실행

### 1. 저장소 클론

```bash
git clone <repository-url>
cd mayday-backend
```

### 2. 환경변수 설정

실제 데이터베이스 비밀번호, JWT Secret, API Key, Google 서비스 계정 JSON 파일 등 민감한 정보는 저장소에 커밋하지 않습니다.

`.env.example`을 참고하여 로컬 환경에 필요한 값을 설정합니다.

| 환경변수                             | 필요 여부         | 설명                                                                                    |
| -------------------------------- | ------------- | ------------------------------------------------------------------------------------- |
| `DB_URL`                         | 권장            | JDBC URL. 기본값은 `jdbc:mysql://localhost:3306/mayday`                                   |
| `DB_USERNAME`                    | 권장            | DB 사용자명. 기본값은 `root`                                                                  |
| `DB_PASSWORD`                    | 필수            | DB 비밀번호                                                                               |
| `DB_DRIVER`                      | 선택            | JDBC 드라이버. MySQL 기본값은 `com.mysql.cj.jdbc.Driver`, PostgreSQL은 `org.postgresql.Driver` |
| `JWT_SECRET`                     | 필수            | HS256 서명용 시크릿. 최소 32바이트 이상 권장                                                         |
| `JWT_ACCESS_TOKEN_EXPIRATION`    | 선택            | Access Token 만료 시간(ms). 기본값 `3600000`                                                 |
| `OPENAI_API_KEY`                 | AI 분석 사용 시 필수 | OpenAI API Key                                                                        |
| `GOOGLE_VISION_API_KEY`          | OCR 사용 시 선택   | Google Cloud Vision API Key                                                           |
| `GOOGLE_APPLICATION_CREDENTIALS` | OCR 사용 시 권장   | Google 서비스 계정 JSON 파일의 절대 경로                                                          |
| `CORS_ALLOWED_ORIGINS`           | 선택            | 허용 Origin 목록. 쉼표로 구분, 기본값 `*`                                                         |
| `DEMO_LOGIN_ENABLED`             | 선택            | 데모 로그인 활성화 여부. 기본값 `false`                                                            |
| `DEMO_LOGIN_EMAIL`               | 선택            | 데모 계정 이메일                                                                             |
| `DEMO_LOGIN_PASSWORD`            | 선택            | 데모 계정 비밀번호                                                                            |

PowerShell 예시:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/mayday"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-db-password"
$env:JWT_SECRET = "replace-with-at-least-32-byte-secret-value"
$env:JWT_ACCESS_TOKEN_EXPIRATION = "3600000"
$env:OPENAI_API_KEY = "your-openai-api-key"
$env:GOOGLE_APPLICATION_CREDENTIALS = "C:\path\to\google-credentials.json"
```

OCR 인증은 `GOOGLE_APPLICATION_CREDENTIALS`를 이용한 Google 서비스 계정 JSON 방식을 권장합니다.

`GOOGLE_VISION_API_KEY` 방식도 fallback으로 사용할 수 있습니다.

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

기본 포트는 `8080`입니다.

## 인증

`/health`, `/auth/signup`, `/auth/login`, `/auth/demo-login`은 인증 없이 호출할 수 있습니다.

그 외 인증이 필요한 API는 로그인 응답으로 발급받은 Access Token을 `Authorization` 헤더에 전달합니다.

```text
Authorization: Bearer <access-token>
```

## 주요 API

### 인증 및 사용자

| Method   | Path                   | 설명                 |
| -------- | ---------------------- | ------------------ |
| `GET`    | `/health`              | 헬스 체크              |
| `POST`   | `/auth/signup`         | 회원가입               |
| `POST`   | `/auth/login`          | 로그인                |
| `POST`   | `/auth/demo-login`     | 심사위원 시연용 데모 로그인    |
| `POST`   | `/auth/logout`         | 로그아웃               |
| `PATCH`  | `/users/me/onboarding` | 온보딩 정보 저장          |
| `DELETE` | `/users/me`            | 회원 탈퇴 및 사용자 데이터 삭제 |
| `GET`    | `/users/me/summary`    | 마이페이지 요약           |

### 홈

| Method | Path            | 설명      |
| ------ | --------------- | ------- |
| `GET`  | `/home/summary` | 홈 화면 요약 |

### 수입

| Method   | Path                  | 설명           |
| -------- | --------------------- | ------------ |
| `POST`   | `/incomes`            | 수입 기록 생성     |
| `GET`    | `/incomes`            | 연도별 수입 목록 조회 |
| `GET`    | `/incomes/{incomeId}` | 수입 상세 조회     |
| `PATCH`  | `/incomes/{incomeId}` | 수입 기록 수정     |
| `DELETE` | `/incomes/{incomeId}` | 수입 기록 삭제     |

### 지출

| Method   | Path                           | 설명                 |
| -------- | ------------------------------ | ------------------ |
| `POST`   | `/expenses`                    | 지출 기록 생성           |
| `GET`    | `/expenses`                    | 연도별 지출 목록 조회       |
| `GET`    | `/expenses/{expenseId}`        | 지출 상세 조회           |
| `PATCH`  | `/expenses/{expenseId}`        | 지출 기록 수정           |
| `DELETE` | `/expenses/{expenseId}`        | 지출 기록 삭제           |
| `GET`    | `/expenses/categories`         | 계정과목 및 증빙 유형 옵션 조회 |
| `POST`   | `/expenses/ocr`                | 영수증 이미지 OCR        |
| `POST`   | `/expenses/text/parse`         | 텍스트 기반 지출 정보 파싱    |
| `POST`   | `/expenses/analyze`            | OpenAI 기반 지출 분석    |
| `POST`   | `/expenses/analyze/rule-based` | 룰 기반 지출 분석         |

### 장부

| Method | Path             | 설명             |
| ------ | ---------------- | -------------- |
| `GET`  | `/ledger`        | 연간 장부 조회       |
| `GET`  | `/ledger/years`  | 기록 보유 연도 목록 조회 |
| `GET`  | `/ledger/search` | 장부 검색 및 필터링    |
| `GET`  | `/ledger/export` | 장부 내보내기        |

## 테스트와 빌드

전체 테스트를 실행합니다.

```bash
./gradlew test
```

애플리케이션을 빌드합니다.

```bash
./gradlew clean build
```

실행 가능한 Spring Boot JAR만 생성하려면 다음 명령어를 사용할 수 있습니다.

```bash
./gradlew clean bootJar
```

테스트 프로필은 H2와 테스트용 Vision 설정을 사용합니다.

## 배포

운영 서버는 `main` 브랜치를 기준으로 배포합니다.

Spring Boot 애플리케이션을 Gradle로 빌드한 뒤 systemd 서비스로 실행하며, Nginx를 리버스 프록시로 사용하여 HTTPS 요청을 처리합니다.

### 운영 서버 배포

운영 서버에서 최신 `main` 브랜치를 가져옵니다.

```bash
git pull origin main
```

애플리케이션을 빌드합니다.

```bash
./gradlew clean build -x test
```

빌드 완료 후 systemd 서비스를 재시작합니다.

```bash
sudo systemctl restart mayday
sudo systemctl status mayday
```

애플리케이션 로그는 다음 명령어로 확인할 수 있습니다.

```bash
sudo journalctl -u mayday -n 100 --no-pager
```

실시간 로그 확인:

```bash
sudo journalctl -u mayday -f
```

운영 환경의 데이터베이스 비밀번호, JWT Secret, OpenAI API Key 및 Google 서비스 계정 인증 정보 등 민감한 값은 저장소에 포함하지 않고 서버 환경변수로 관리합니다.

### HTTPS

운영 환경에서는 Nginx를 리버스 프록시로 사용합니다.

클라이언트의 HTTPS 요청을 Nginx가 수신하고 내부 Spring Boot 애플리케이션으로 전달합니다.

```text
Client
  ↓ HTTPS
Nginx
  ↓
Spring Boot
```

### Docker

로컬 또는 별도의 컨테이너 환경에서는 제공된 `Dockerfile`을 사용할 수 있습니다.

```bash
docker build -t mayday-backend .
docker run --env-file .env -p 8080:8080 mayday-backend
```

## OCR 설정

OCR 기능은 Google Cloud Vision API를 사용합니다.

운영 환경에서는 API Key 방식보다 Google 서비스 계정 JSON을 이용한 인증 방식을 권장합니다.

```text
GOOGLE_APPLICATION_CREDENTIALS=/path/to/google-credentials.json
```

`/expenses/ocr` 호출 시 인증 또는 설정 관련 오류가 발생하면 다음 항목을 확인합니다.

* Google Cloud Vision API 활성화 여부
* Google Cloud 결제 설정
* 서비스 계정 권한
* `GOOGLE_APPLICATION_CREDENTIALS` 파일 경로
* 서비스 계정 JSON 파일 접근 권한

`GOOGLE_VISION_API_KEY`를 이용한 API Key 인증도 fallback으로 지원합니다.

## 보안

다음과 같은 민감한 정보는 Git 저장소에 커밋하지 않습니다.

* 데이터베이스 비밀번호
* JWT Secret
* OpenAI API Key
* Google Cloud API Key
* Google 서비스 계정 JSON
* 데모 계정 비밀번호

관련 값은 환경변수 또는 서버의 별도 Secret 설정을 통해 관리합니다.
