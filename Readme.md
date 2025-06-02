# 프로젝트명: 멀티스레드 채팅 프로그램

## 프로젝트 개요  
### 목표
- Java로 클라이언트-서버 기반의 채팅 시스템을 구현
- 실시간 메시지 송수신 및 귓속말, 접속자 목록 확인 등의 기능을 제공함으로써 네트워킹, 소켓 프로그래밍, 동시성 제어, 객체 직렬화를 학습 및 적용
  

### 주요 기능
- 클라이언트와 서버 간 TCP 소켓을 이용한 양방향 통신
- 멀티스레딩을 통한 동시 접속 처리
- 메시지 타입에 따른 브로드캐스트, 귓속말(@username), WHOISIN, LOGOUT 명령 지원
- 서버 로그를 통한 접속/해제 기록 관리 및 사용자 활동 모니터링
- ExecutorService를 활용한 스레드 풀 도입으로 자원 관리 최적화 및 성능 개선

### 사용 기술
- 언어: Java
- 네트워킹: Socket 프로그래밍 (TCP/IP)
- 동시성 처리: 멀티스레딩, ExecutorService, BlockingQueue, ConcurrentLinkedQueue
- 빌드 도구: Maven
- 테스트: JUnit 5

### 주요 성과 및 학습 내용  
- 사용자마다 새 스레드를 생성하던 구조에서, **리소스 소모 문제를 해결하기 위해 ExecutorService 기반 스레드 풀을 도입하여 동시 처리 효율 향상**
- 메시지 수신과 처리를 단일 흐름에서 처리하던 구조에서 병목이 발생해 흐름이 멈추는 문제를 방지하고자, **BlockingQueue를 활용한 생산자-소비자 구조로 분리하여 메시지 처리 안정성 개선**
- 공유 리스트로 클라이언트 연결을 관리하던 중 동시 접근 충돌 위험이 있어, **ConcurrentLinkedQueue로 전환해 스레드 안전한 연결 관리와 브로드캐스트 지원**


## 실행
1. 2개 이상의 terminal 실행
2. java -jar chatting_program-1.0-SNAPSHOT.jar
3. Server 또는 Client 선택
- Server 측 예제
  - Server가 먼저 실행되어 listen 상태 유지
```dtd
> Enter Server or Client
Server
> Enter Port Number (default: 7000):

20:03:09 Server waiting for Clients on port 7000.

```

- Client 측 예제
  - username (default anonymous)
  - server port number (default localhost)
  - server ip address (default 7000)

```dtd
> Enter Server or Client
Client
> Enter the username (default: Anonymous):
tester
> Enter Server Port Number (default: 7000):

> Enter the Server Address (default: localhost):

Connection accepted localhost/127.0.0.1:7000

Welcome to the chatroom.Instructions:
1. 활성화된 모든 클라이언트에게 브로드캐스트 메시지를 보내려면, 메시지를 입력하세요.
2. 원하는 클라이언트에게 귓속말를 보내려면 @username 메시지 형식으로 입력하세요.
3. 활성화된 클라이언트 목록을 보려면 따옴표 없이 WHOISIN을 입력하세요.
4. LOGOUT을 입력하면 서버에서 로그아웃됩니다.


> id: 0
> 20:05:44 tester  *** tester has joined the chat room. ***
>
```
