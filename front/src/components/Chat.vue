<template>
  <body>
    <div class="black-bg" v-show="popState">
      <div class="white-bg">
        <form v-on:submit.prevent="handlePop">
          <p>닉네임을 입력해주세요.</p>
          <input v-model="sender" type="text" autofocus required>
          <p></p>
          <button @click="sendEnter" v-on:keyup.enter="submit">확인</button>
        </form>
      </div>
    </div>

    <div class="chat-title">
      <tr>
        <td style="width:5vw;">
          <button @click="exitRoom">나가기</button>
        </td>
        <td style="width:15vw;">
          <div style="margin-top:5px; font-weight: bold; font-size: 20px;">
            {{ channelName }}
          </div>
        </td>
        <td style="width:5vw;">
          <div>
            인원 수 : {{ roomCount }}
          </div>
        </td>
      </tr>
    </div>

    <div class="chat-box scrollbar" ref="messages">
      

      <div v-show="!isEnd">
        <button @click="getMessage">지난 채팅 더보기</button>
      </div>

      <div v-for="(item, idx) in previousList" :key="idx" class="previous-chatting" ref="previous-chatting">
        {{ item.sender }} ({{ item.ip.substring(0, item.ip.indexOf('.', 5)) }}) : {{ item.message }}
      </div>

      <div v-for="(item, idx) in reciveList" :key="idx" class="recive-chatting">
        <div :class="{ blue: item.type != 'message' }">
          {{ item.sender }}
          <a :class="[item.type != 'message' ? blue : grey]"> ({{ item.ip.substring(0, item.ip.indexOf('.', 5)) }})</a>
          :
          {{ item.message }}
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
      // url: "http://everychat.kro.kr:8082",
      url: "http://localhost:8080",
      sender: "",
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
      ip: "",
    }
  },
  created() {
    this.getMessage();
    this.connect();
    this.findMyIp();
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
          ip: this.ip,
        };
        this.stompClient.send("/pub/chat", JSON.stringify(msg), {})
      }
    },
    sendEnter() {
      if (this.sender == "") return;

      console.log("Send status: enter");
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          channelId: this.channelId,
          type: "enter",
          sender: this.sender,
          message: "채팅방에 입장하였습니다.",
          ip: this.ip,
        };
        this.stompClient.send("/pub/chat", JSON.stringify(msg), {})
      }
    },
    sendLeave() {
      if (this.sender == "") return;

      console.log("Send status: leave");
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          channelId: this.channelId,
          type: "leave",
          sender: this.sender,
          message: "채팅방에서 퇴장하였습니다.",
          ip: this.ip,
        };
        this.stompClient.send("/pub/chat", JSON.stringify(msg), {})
      }
    },
    connect() {
      const serverURL = this.url + "/ws";
      let socket = new SockJS(serverURL);
      this.stompClient = Stomp.over(socket);

      console.log('소켓 연결 시도');

      this.stompClient.connect(
        {},
        // eslint-disable-next-line
        frame => {
          console.log('소켓 연결 성공');

          setTimeout(() => {
            this.stompClient.subscribe("/topic/" + this.channelId, res => {
              this.reciveList.push(JSON.parse(res.body));
              this.roomCount = JSON.parse(res.body).count;
            });
          }, 500);

        },
        error => {
          console.log('소켓 연결 실패', error);
          this.connect();
        },

      );
    },
    handlePop() {
      this.popState = !this.popState;
    },
    async getMessage() {
      const response = await axios.get(this.url + "/message/" + this.channelId + "/" + this.messagePage);

      this.previousList.unshift(...response.data.data.messageList);
      this.messagePage++;

      if (response.data.data.pageSize == this.messagePage || response.data.data.pageSize == 0) {
        this.isEnd = true;
        return;
      }
    },
    exitRoom() {
      this.sendLeave();
      this.stompClient.disconnect();
      this.$router.push('/');
    },
    async findMyIp() {
      const response = await axios.get('https://ipwho.is');
      this.ip = response.data.ip;
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
    }
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
  color: #acaaaa;
}

.chat-title {
  border-radius: 0.5em;
  padding: 10px;
  border: 2px solid #b3b0b0;
  width: 25vw;
  height: 3.5vh;
}
</style>
  