# 1:N Socket Chatting Program

스레드 활용 소켓 채팅 프로그램  

## 목표
- 한 쌍의 socket으로 unique한 connection 생성의 이해
- 서버 프로세스에서 client 수 만큼 스레드를 생성하여 connection 생성 및 관리

## 기능
- Client 1:1 채팅 
- Client 1:N 채팅 
- Client 로그아웃
- Server에 현재 접속 중인 Client 확인

## 구조 
### server, client 연결 
<img width="80%" alt="client server 연결" src="https://github.com/user-attachments/assets/e7af32f2-cc6f-4eed-b1b2-a584a610e528">

### client 1:1 통신 
<img width="80%" alt="client 1:1 통신" src="https://github.com/user-attachments/assets/3dfa200e-f80a-456f-93b8-5e2e120a3392">

### client 1:N 통신
<img width="80%" alt="client 1:N 통신" src="https://github.com/user-attachments/assets/2a37104e-d8c6-405e-88e9-b14e1b8bc7a6">

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
1. Simply type the message to send broadcast to all active clients
2. Type '@username message' without quotes to send message to desired client
3. Type 'WHOISIN' without quotes to see list of active clients
4. Type 'LOGOUT' without quotes to logoff from server


> id: 0
> 20:05:44 tester  *** tester has joined the chat room. ***
>
```
