<template>
  <body>

  </body>
</template>

<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

export default {
  name: 'App',
  created() {
    this.connect();
  },
  methods: {
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

<style>

</style>
