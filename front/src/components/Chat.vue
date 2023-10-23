<template>
  <body>
    <div class="black-bg" v-show="popState">
      <div class="white-bg">
        <form v-on:submit.prevent="handlePop">
          <p>닉네임을 입력해주세요.</p>
          <input v-model="sender" type="text" autofocus>
          <p></p>
          <button v-on:keyup.enter="submit">확인</button>
        </form>
      </div>
    </div>

    <div class="chat-box scrollbar" ref="messages">
      <div v-show="!isEnd">
        <form v-on:submit.prevent="getMessage">
          <button>지난 채팅 더보기</button>
        </form>
      </div>

      <div v-for="(item, idx) in previousList" :key="idx" class="previous-chatting" ref="previous-chatting">
        {{ item.sender }} : {{ item.message }}
      </div>

      <div v-for="(item, idx) in reciveList" :key="idx" class="recive-chatting">
        {{ item.sender }} : {{ item.message }}
      </div>
    </div>

    <form v-on:submit.prevent="sendMessage">
      <input v-model="message" type="text" class="input-chat" autofocus>
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
      url: "http://218.237.234.37:8082",
      // url: "http://localhost:8080",
      sender: "",
      message: "",
      reciveList: [],
      previousList: [],
      popState: true,
      messagePage: 0,
      isEnd: false
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
          channelId: "e54ec9db-c36a-40d5-a380-4e26f3f1544e",
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

          setTimeout(function () {
            this.stompClient.subscribe("/topic/e54ec9db-c36a-40d5-a380-4e26f3f1544e", res => {
              this.reciveList.push(JSON.parse(res.body));
            });
          }, 1);
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

      if (response.data.data.length == 0) {
        this.isEnd = true;
        return;
      }

      this.previousList.unshift(...response.data.data);
      this.messagePage++;
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
  min-height: 10vh;
  height: auto;
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
  height: 55vh;
}

.previous-chatting {
  margin: 20px;
  color: #acaaaa;
}

.recive-chatting {
  margin: 20px;
}

.scrollbar {
  overflow: hidden;
  overflow-y: auto;
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
</style>
  