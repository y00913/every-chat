<template>
  <body>
    <form v-on:submit.prevent="sendMessage">
      유저이름 :
      <input v-model="sender" type="text">
      메세지 :
      <input v-model="message" type="text">

      <button v-on:keyup.enter="submit">완료</button>
    </form>

    <div v-for="(item, idx) in reciveList" :key="idx">
      <h4> 유저이름 : {{ item.sender }} </h4>
      <h4> 내용 : {{ item.message }} </h4>
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
      reciveList: []
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
    }
  },
  components: {
  }
}
</script>

<style></style>
