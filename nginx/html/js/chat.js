let stompClient = null;
let currentUser = null;
let authToken = null;
let subscribedRooms = new Set();

const logoutBtn = document.getElementById('logoutBtn');
const chatSection = document.getElementById('chatSection');

// 로그아웃
logoutBtn.addEventListener('click', async () => {
  const response = await fetch('/api/logout', { method: 'POST' });
  const text = await response.text();
  alert(text);
  location.reload();
});

// stomp 연결
function connectStomp() {
  const socket = new SockJS('/ws-chat');
  stompClient = Stomp.over(socket);

  stompClient.connect(
    { Authorization: 'Bearer ' + authToken }, // connect headers에 token 추가
    function (frame) {
      console.log('Connected: ' + frame);

      stompClient.subscribe('/user/' + currentUser + '/queue/messages', function (message) {
        const receivedMessage = JSON.parse(message.body);
        displayMessage('[DM]', receivedMessage.sender, receivedMessage.content);
      });

    }, function (error) {
      console.error('Connection error:', error);
      alert("서버와 연결에 실패했습니다. 다시 시도하거나 관리자에게 문의하세요.");
    });
}


// 방 입장
document.getElementById('joinRoomBtn').addEventListener('click', () => {
  const roomId = document.getElementById('roomInput').value.trim();

  if (!roomId) {
    alert("방 ID를 입력하세요");
    return;
  }

  if (subscribedRooms.has(roomId)) {
    alert("이미 구독된 방입니다.");
    return;
  }

  stompClient.subscribe('/topic/' + roomId, function (message) {
    const receivedMessage = JSON.parse(message.body);
    displayMessage('[방]', receivedMessage.sender, receivedMessage.content);
  });

  subscribedRooms.add(roomId);
  alert(roomId + " 방에 입장했습니다.");

  // 이전 메시지 불러오기
  fetch(`/api/chat/${roomId}/messages`)
    .then(response => {
      if (!response.ok) {
        throw new Error('메시지를 불러오는 데 실패했습니다.');
      }
      return response.json();
    })
    .then(messages => {
      const container = document.getElementById('messagesRoom');
      container.innerHTML = ''; // 이전 내용 초기화

      messages.forEach(msg => {
        displayMessage('[방]', msg.sender, msg.content);
      });

      // 맨 아래로 스크롤
      container.scrollTop = container.scrollHeight;
    })
    .catch(error => {
      console.error('이전 메시지 로딩 오류:', error);
    });
});

// 방 메시지 전송
document.getElementById('sendRoomBtn').addEventListener('click', () => {
  const content = document.getElementById('roomMessageInput').value.trim();
  const roomId = document.getElementById('roomInput').value;

  if (!content || !roomId) {
    alert("방 ID와 메시지를 입력하세요.");
    return;
  }

  const message = {
    messageType: "ROOM",
    roomId: roomId,
    sender: currentUser,
    content: content
  };

  stompClient.send("/app/chat.sendRoomMessage", {}, JSON.stringify(message));
  document.getElementById('roomMessageInput').value = '';
});

// DM 메시지 전송
document.getElementById('sendDmBtn').addEventListener('click', () => {
  const content = document.getElementById('dmMessageInput').value.trim();
  const receiver = document.getElementById('receiverInput').value;

  if (!content || !receiver) {
    alert("수신자와 메시지를 입력하세요.");
    return;
  }

  const message = {
    messageType: "DIRECT",
    sender: currentUser,
    receiver: receiver,
    content: content
  };

  stompClient.send("/app/chat.sendDirectMessage", {}, JSON.stringify(message));
  document.getElementById('dmMessageInput').value = '';
});

function displayMessage(prefix, sender, content) {
  const targetDiv = prefix === "[DM]"
    ? document.getElementById('messagesDirect')
    : document.getElementById('messagesRoom');

  const messageElement = document.createElement('p');
  messageElement.textContent = `${prefix} ${sender}: ${content}`;
  targetDiv.appendChild(messageElement);
  targetDiv.scrollTop = targetDiv.scrollHeight;
}

// 새로고침 시 로그인 상태 복원
window.onload = function () {
    const token = localStorage.getItem("accessToken");
    const username = localStorage.getItem("currentUser");

    if (token && username) {
        authToken = token;
        currentUser = username;
        connectStomp();
    } else {
        alert("로그인이 필요합니다.");
        window.location.href = "/index.html";
    }
};

// 페이지 종료 시 disconnect
window.addEventListener('beforeunload', function () {
  if (stompClient) {
    stompClient.disconnect();
  }
});