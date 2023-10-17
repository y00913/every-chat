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
    <form v-on:submit.prevent="sendMessage">
      메세지 :
      <input v-model="message" type="text">

      <button v-on:keyup.enter="submit">완료</button>
    </form>

    <div v-for="(item, idx) in reciveList" :key="idx">
      <h4> {{ item.sender }} : {{ item.message }} </h4>
    </div>
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
  components: {
  }
}
</script>

<style>
body {
  text-align: center;
}

.black-bg {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  position: fixed;
  padding: 20px;
}

.white-bg {
  width: 50%;
  background: white;
  border-radius: 8px;
  padding: 20px;
}</style>
