<template>
  <body>
    <div class="black-bg" v-show="popState">
      <div class="white-bg">
        <form v-on:submit.prevent="handlePop">
          <p>닉네임을 입력해주세요.</p>
          <input v-model="sender" type="text" autofocus required>
          <p></p>
          <button v-on:keyup.enter="submit">확인</button>
        </form>
      </div>
    </div>

    <div class="chat-box scrollbar" ref="messages">
      <div class="exit" v-show="!popState">
        <button @click="exitRoom">나가기</button>
      </div>

      <div v-show="!isEnd">
        <button @click="getMessage">지난 채팅 더보기</button>
      </div>

      <div v-for="(item, idx) in previousList" :key="idx" class="previous-chatting" ref="previous-chatting">
        {{ item.sender }} : {{ item.message }}
      </div>

      <div v-for="(item, idx) in reciveList" :key="idx" class="recive-chatting">
        {{ item.sender }} : {{ item.message }}
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
      url: "http://everychat.kro.kr:8082",
      sender: "",
      message: "",
      reciveList: [],
      previousList: [],
      popState: true,
      messagePage: 0,
      isEnd: false,
      channelName: this.$route.params.channelName,
      channelId: this.$route.params.channelId,
      pageSize: 0
    }
  },
  created() {
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
      console.log("Send message: " + this.message);

      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          channelId: this.channelId,
          type: "message",
          sender: this.sender,
          message: this.message
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
      const response = await axios.get(this.url + "/message/" + this.messagePage);

      this.previousList.unshift(...response.data.data.messageList);
      this.messagePage++;

      if (response.data.data.pageSize == this.messagePage) {
        this.isEnd = true;
        return;
      }
    },
    exitRoom() {
      this.stompClient.disconnect();
      this.$router.push('/');
    }
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
}
</script>
  
<style>
@import "../assets/css/MainBox.css";
@import "../assets/css/BlackBg.css";
@import "../assets/css/WhiteBg.css";

body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin: 0;
}

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

button {
  color: #444444;
  background: #F3F3F3;
  border: 2px #DADADA solid;
  padding: 8px 20px;
  border-radius: 1em;
  font-weight: bold;
  font-size: 10pt;
  outline: none;
  font-family: 'mabi';
  cursor: pointer;
}

button:hover {
  border: 2px #C6C6C6 solid;
  box-shadow: 1px 1px 1px #EAEAEA;
  color: #333333;
  background: #F7F7F7;
}

button:active {
  box-shadow: inset 1px 1px 1px #DFDFDF;
}

.exit {
  float: left;
  position: fixed
}

</style>
  