<template>
  <body>
    <div class="black-bg" v-show="popState">
      <div class="white-bg">
        <form v-on:submit.prevent="handlePop">
          <p>닉네임</p>
          <input v-model="sender" type="text">
          <button v-on:keyup.enter="submit">확인</button>
        </form>
      </div>
    </div>

    <h1>채팅</h1>

    <div class="chat-box scrollbar" ref="messages">
      <div v-for="(item, idx) in reciveList" :key="idx" class="chatting">
        {{ item.sender }} : {{ item.message }}
      </div>
    </div>

    <form v-on:submit.prevent="sendMessage">
      <input v-model="message" type="text" class="input-chat">
      <p></p>
      <button v-on:keyup.enter="submit">완료</button>
    </form>
  </body>
</template>

<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

export default {
  name: 'App',
  data() {
    return {
      sender: "",
      message: "",
      reciveList: [],
      popState: true
    }
  },
  created() {
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
          channelId: "e54ec9db-c36a-40d5-a380-4e26f3f1544e",
          type: "message",
          sender: this.sender,
          message: this.message
        };
        this.stompClient.send("/pub/chat", JSON.stringify(msg), {})
      }
    },
    connect() {
      const serverURL = "http://localhost:8080/ws";
      let socket = new SockJS(serverURL);
      this.stompClient = Stomp.over(socket);

      console.log('소켓 연결 시도');

      this.stompClient.connect(
        {},
        // eslint-disable-next-line
        frame => {
          // this.connected = true;

          console.log('소켓 연결 성공');

          this.stompClient.subscribe("/topic/e54ec9db-c36a-40d5-a380-4e26f3f1544e", res => {
            console.log('구독으로 받은 메세지', res.body);

            this.reciveList.push(JSON.parse(res.body));
          });
        },
        error => {
          console.log('소켓 연결 실패', error);
          // this.connected = false;
        }
      );
    },
    handlePop() {
      this.popState = !this.popState;
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
  components: {
  }
}
</script>

<style>
body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin: 0;
  font-family: 'mabi';
}

.input-chat {
  width: 25vw;
  height: 3vh;
}

.black-bg {
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  position: fixed;
  padding: 20px;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}

.white-bg {
  width: 30vw;
  height: 10vh;
  background: white;
  border-radius: 8px;
  position: fixed;
  padding: 20px;
  left: 50%;
  transform: translate(-50%, 0);
}

.chat-box {
  border-radius: 0.5em;
  padding: 10px;
  border: 2px solid #b3b0b0;
  margin: 50px;
  width: 25vw;
  height: 50vh;
  overflow-y: auto;
}

.chatting {
  margin: 20px;
  /* float: left; */
}

.scrollbar { 
  width: 25vw;
  height: 50vh;
  overflow-y: scroll;
}

.scrollbar::-webkit-scrollbar {
  width: 10px;
}
 
.scrollbar::-webkit-scrollbar-thumb {
  background: #666;
  border-radius: 20px;
}

.scrollbar::-webkit-scrollbar-track {
  background: #ddd;
  border-radius: 20px;
}

@font-face {
  font-family: "mabi";
  src: url('./assets/Mabinogi_Classic_TTF.ttf') format('truetype')
}
</style>
