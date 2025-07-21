# 프로젝트명: 채팅 시스템

## 프로젝트 개요  
- 순수 Java 기반의 소켓 프로그래밍을 경험한 이후, 보다 발전된 실시간 채팅 시스템을 직접 설계하고 구현해보고 싶어 시작한 프로젝트입니다. 
- 채팅 서버와 API 서버를 분리하고, JWT 기반 인증을 통해 사용자 상태를 관리하여 Stateful 환경에서도 수평 확장이 가능하도록 구성했습니다.
- 1:1 메시징, 단체 메시징, 채팅 히스토리 관리, 서버 확장성 등을 고려해 구조화했습니다.

### 기술 스택  
- **백엔드**: Java 17, Spring Boot 3.4, Spring Security, WebSocket (STOMP)
- **인프라**: Redis (Stream, Pub/Sub), Docker, Nginx
- **데이터베이스**: MySQL
- **프론트엔드**: HTML, Vanilla JS

### 시스템 아키텍처
```
사용자 ─────▶ Nginx ───▶ chatting-api (REST API 서버) ──▶ MySQL
                 │
                 └────▶ chatting-core (STOMP WebSocket 서버)
                                    │
                     Redis (Stream per room + Pub/Sub)
```
- Nginx
  - 포트에 따라 API와 채팅 서버로 트래픽 분기
  - core 서버는 least_conn 방식으로, 소켓 연결이 적은 서버에 분산 처리
- chatting-api
  - 사용자 인증/회원가입/메시지 히스토리 API 제공  
- chatting-core
  - STOMP 기반 WebSocket 채팅 처리  
- Redis
  - Stream: 채팅방별 또는 1대1 채팅에서 사용자별 수신용 메시지 기록
  - Pub/Sub: 메시지 전파 및 실시간 수신 처리

### 주요 기능
- JWT 기반 로그인 및 인증
- WebSocket(STOMP) 기반의 실시간 1:1 및 단체 채팅
- Redis Stream을 활용한 채팅 기록 저장 및 복원 기능
- Pub/Sub 구조로 효율적인 실시간 메시지 전달
- Nginx 부하 분산 처리


### 주요 성과 및 학습 내용  
- 채팅 서버와 API 서버를 분리해 **확장성과 유지보수성을 고려한 아키텍처 설계**
- JWT 인증을 통해 **WebSocket의 stateful 특성 보완**
- Redis Stream vs Pub/Sub 방식의 차이 및 운영 특성 직접 비교 및 적용
- Spring Boot 기반 **멀티 모듈 구성**, Docker/Nginx를 통한 환경 구축 경험

### 실행 방법

```
# 1. 빌드
./gradlew build

# 2. Docker Compose 실행
docker-compose up --build
```
- 기본 접속 주소: http://localhost
- WebSocket endpoint: /ws-chat
- 2명 이상 테스트하려면 다른 브라우저 또는 시크릿 창에서 접속
