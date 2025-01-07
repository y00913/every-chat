<template>
  <body>
    <div class="black-bg" v-show="popState ">
      <div class="white-bg">
        <form v-on:submit.prevent="handlePop">
          <p>닉네임을 입력해주세요.</p>
          <input v-model="sender" type="text" autofocus required>
          <p></p>
          <button @click="sendEnter" v-on:keyup.enter="submit">확인</button>
        </form>
      </div>
    </div>

    <div class="chat-box">
      <div>
        <tr>
          <td style="width:200px;">
            <button @click="exitRoom">나가기</button>
          </td>
          <td style="width:600px;">
            <div style="margin-top:5px; font-weight: bold; font-size: 20px;">
              {{ channelName }}
            </div>
          </td>
          <td>

          </td>
          <td style="width:200px;">
            <div>
              인원 수 : {{ roomCount }}
            </div>
          </td>
        </tr>
        <hr>
      </div>

      <div class="scrollbar chat-list" ref="messages">
        <div v-show="!isEnd">
          <button @click="getMessage">지난 채팅 더보기</button>
        </div>

        <div v-for="(item, idx) in previousList" :key="idx" class="previous-chatting" ref="previous-chatting">
          <div class="chat-date">
            {{ formatDate(item.createAt) }} 
          </div>
          <div>
            {{ item.sender }} ({{ item.ip }}) : {{ item.message }}
          </div>
          <div v-show="idx === lastChat">
            <hr style="width:700px; height: 0.5px; background: #acaaaa;">
          </div>
        </div>

        <div v-for="(item, idx) in reciveList" :key="idx" class="recive-chatting">
          <div :class="{ 'blue': item.type !== 'message' }">
            <div class="chat-date">
              {{ formatDate(item.createAt) }} 
            </div>
            {{ item.sender }}
            <a :class="[item.type != 'message' ? 'blue' : 'grey']"> 
              ({{ item.ip }})</a>
            :
              {{ item.message }}
          </div>
        </div>

        <div v-show="!this.connected">
          연결이 해제되었습니다.
        </div>
      </div>
    </div>

    <form v-on:submit.prevent="sendMessage">
      <input v-model="message" type="text" class="input-chat" required>
      <p></p>
      <button v-on:keyup.enter="submit">입력</button>
    </form>
  </body>
</template>
  
<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import axios from "axios"

export default {
  name: 'App',
  data() {
    return {
      url: "https://everychat.kro.kr",
      // url: "http://localhost:8080",
      sender: localStorage.getItem('sender'),
      message: "",
      reciveList: [],
      previousList: [],
      popState: true,
      messagePage: 0,
      isEnd: false,
      channelName: this.$route.params.channelName,
      channelId: this.$route.params.channelId,
      pageSize: 0,
      member: [],
      roomCount: 0,
      connected: true,
      lastChat: 0,
      isConnecting: false,
    }
  },
  created() { 
    const isVerified = this.checkLockVerify();
    if (!isVerified) return;
    this.getMessage();
    this.connect();
  },
  methods: {
    // eslint-disable-next-line
    sendMessage(e) {
      this.send();
      this.message = "";
    },
    send() {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          channelId: this.channelId,
          type: "message",
          sender: this.sender,
          message: this.message,
        };
        this.stompClient.send("/pub/chat", JSON.stringify(msg), {})
      }
    },
    sendEnter() {
      if (!this.checkSender()) return;

      console.log("Send status: enter");
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          channelId: this.channelId,
          type: "enter",
          sender: this.sender,
          message: "채팅방에 입장하였습니다.",
        };
        this.stompClient.send("/pub/chat", JSON.stringify(msg), {})
      }
    },
    sendLeave() {
      if (!this.checkSender()) return;

      console.log("Send status: leave");
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          channelId: this.channelId,
          type: "leave",
          sender: this.sender,
          message: "채팅방에서 퇴장하였습니다.",
        };
        this.stompClient.send("/pub/chat", JSON.stringify(msg), {})
      }
    },
    connect() {
      if (this.isConnecting) {
        console.log("이미 연결 시도 중입니다.");
        return;
      }

      this.isConnecting = true;
      const serverURL = this.url + "/ws";
      let socket = new SockJS(serverURL);
      var options = { debug: false, protocols: ['v11.stomp', 'v12.stomp'] };
      this.stompClient = Stomp.over(socket, options);

      this.stompClient.heartbeat.outgoing = 10000;
      this.stompClient.heartbeat.incoming = 10000;

      console.log('소켓 연결 시도');

      let retryCount = 0;
      const maxRetries = 5;

      this.stompClient.connect(
        {},
        // eslint-disable-next-line
        frame => {
          console.log('소켓 연결 성공');
          this.isConnecting = false;

          this.stompClient.subscribe("/topic/" + this.channelId, res => {
            let response = JSON.parse(res.body);

            if (response.type === "count") {
              this.roomCount = response.message;
            }
            else {
              this.reciveList.push(response);
            }
          });
        },
        error => {
          console.log('소켓 연결 실패', error);
          this.isConnecting = false;

          if (retryCount < maxRetries) {
            retryCount++;
            console.log(`재시도 중... (${retryCount}/${maxRetries})`);
            setTimeout(() => {
              this.connect();
            }, 5000);
          } else {
            console.error("최대 재시도 횟수 도달, 연결 포기");
            this.connected = false;
          }
        },

      );
    },
    handlePop() {
      this.popState = !this.popState;
    },
    async checkLockVerify() {
      try {
        const response = await axios.get(this.url + "/api/channel/lock/" + this.channelId);
        if (!response.data.data) {
          alert('비밀번호를 입력해주세요.');
          window.location.href = "https://everychat.kro.kr";
          return false;
        }
        return true;
      } catch (error) {
        console.error('Error checking lock:', error);
        alert('오류가 발생했습니다. 다시 시도해주세요.');
        return false;
      }
    },
    async getMessage() {
      const response = await axios.get(this.url + "/api/message/" + this.channelId + "/" + this.messagePage);

      this.previousList.unshift(...response.data.data.messageList);
      this.messagePage++;
      this.lastChat = response.data.data.messageList.length - 1;

      if (response.data.data.pageSize == this.messagePage || response.data.data.pageSize == 0) {
        this.isEnd = true;
        return;
      }
    },
    exitRoom() {
      this.$router.push('/');
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    checkSender() {
      if(this.sender == null || this.sender == "") return false;
      return true;
    },
  },
  watch: {
    reciveList: {
      handler() {
        this.$nextTick(() => {
          let messages = this.$refs.messages;

          messages.scrollTo({ top: messages.scrollHeight, behavior: 'smooth' });
        })
      },

      deep: true
    },
  },
  mounted() {
    window.addEventListener('beforeunload', this.sendLeave);
  },
  beforeUnmount() {
    window.removeEventListener('beforeunload', this.sendLeave);
  },
  beforeRouteLeave() {
    this.sendLeave();
    this.stompClient.disconnect();
  }
}
</script>
  
<style>
@import "../assets/css/MainBox.css";
@import "../assets/css/BlackBg.css";
@import "../assets/css/WhiteBg.css";

.input-chat {
  width: 25vw;
  height: 3vh;
}

.previous-chatting {
  margin: 20px;
  color: #acaaaa;
}

.recive-chatting {
  margin: 20px;
}

.blue {
  color: #4FADBD;
}

.grey {
  color: #858484;
}

.chat-list {
  width: 1000px;
  height: 600px;
}
</style>
  