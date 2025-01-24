<template>
    <div class="black-bg" v-show="popState ">
      <div class="white-bg">
        <form v-on:submit.prevent="handlePop">
          <p>닉네임을 입력해주세요.</p>
          <input :spellcheck="false" v-model="sender" type="text" autofocus placeholder="닉네임" required>
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
      </div>

      <hr>

      <div class="scrollbar chat-list" ref="messages">
        <div v-for="(item, idx) in reciveList" :key="idx" class="recive-chatting">
          <div :class="{ 'blue': item.type !== 'message' }">
            <div class="chat-date">
              {{ formatDate(item.createAt) }} 
            </div>
            {{ item.sender }}
            <a :class="[item.type != 'message' ? 'blue' : 'grey']"> 
              ({{ item.ip }})</a>
            :
            <a v-dompurify-html="formatMessage(item.message)" class="message-content"></a>
          </div>
        </div>

        <div v-for="(item, idx) in previousList" :key="idx" class="previous-chatting" ref="previous-chatting">
          <div class="chat-date">
            {{ formatDate(item.createAt) }} 
          </div>
          <div>
            {{ item.sender }} ({{ item.ip }})
            : 
            <a v-dompurify-html="formatMessage(item.message)" class="message-content"></a>
          </div>
        </div>

        <div v-show="!isEnd">
          <button @click="getMessage">지난 채팅 더보기</button>
        </div>        

        <div v-show="!this.connected">
          연결이 해제되었습니다.
        </div>
      </div>

      <hr>

      <div class="chat-div">
        <form class="chat-form" v-on:submit.prevent="sendMessage">
          <div class="textarea-wrapper">
            <textarea 
              spellcheck="false"
              class="textarea-chat"
              v-model="message" 
              placeholder="메시지 입력" 
              rows="1" 
              @keydown="handleKeyDown" 
              @blur="keepFocus" 
              required>
            </textarea>
            <button type="submit" class="textarea-button">입력</button>
          </div>
        </form>
      </div>
    </div>
</template>
  
<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import axios from "axios"

export default {
  name: 'App',
  data() {
    return {
      url: process.env.VUE_APP_SERVER_URL,
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
      isLeave: false,
      retryCount: 0,
      maxRetries: 5,
      isMobile: false,
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
      if(!this.message || this.message.trim() === "") return;

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

      localStorage.setItem('sender', this.sender);

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

      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          channelId: this.channelId,
          type: "leave",
          sender: this.sender,
          message: "채팅방에서 퇴장하였습니다.",
        };
        this.stompClient.send("/pub/chat", JSON.stringify(msg), {})
      }

      this.isLeave = true
    },
    connect() {
      if (this.isConnecting || this.isLeave) {
        return;
      }

      this.isConnecting = true;
      const serverURL = this.url + "/ws";
      let socket = new SockJS(serverURL);
      var options = { debug: false, protocols: ['v11.stomp', 'v12.stomp'] };
      this.stompClient = Stomp.over(socket, options);

      this.stompClient.heartbeat.outgoing = 10000;
      this.stompClient.heartbeat.incoming = 10000;

      this.stompClient.connect(
        {},
        // eslint-disable-next-line
        frame => {
          this.retryCount = 0;
          this.isConnecting = false;

          this.stompClient.subscribe("/topic/" + this.channelId, res => {
            let response = JSON.parse(res.body);

            if (response.type === "count") {
              this.roomCount = response.message;
            }
            else {
              this.reciveList.unshift(response);
            }
          });
        },
        // eslint-disable-next-line
        error => {
          this.isConnecting = false;

          if (this.retryCount < this.maxRetries) {
            this.retryCount++;
            setTimeout(() => {
              this.connect();
            }, 5000);
          } else {
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
          window.location.href = process.env.VUE_APP_CLIENT_URL;
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

      this.previousList.push(...response.data.data.messageList.reverse());
      this.messagePage++;
      this.lastChat = response.data.data.messageList.length - 1;

      if (response.data.data.pageSize == this.messagePage || response.data.data.pageSize == 0) {
        this.isEnd = true;
        return;
      }
    },
    exitRoom() {
      this.$router.go(-1);
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
    handleKeyDown(e) {
      if (this.mobile && e.key === 'Enter') {
        e.preventDefault();
        this.message += '\n';
      }

      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        this.sendMessage();
      }
    },
    formatMessage(message) {
      let formattedMessage = message.replace(/\n/g, '<br>');

      const imageRegex = /(https?:\/\/[^\s]+?\.(?:png|jpg|jpeg|gif)(?:[^\s]*)?)/gi;

      formattedMessage = formattedMessage.replace(imageRegex, (url) => {
        return `
          <br>
          <img 
            src="${url}" 
            alt="Image" 
            class="chat-img" 
          />`;
      });

      const youtubeRegex =
        /https?:\/\/(?:www\.)?youtube\.com\/(?:watch\?v=|shorts\/)([a-zA-Z0-9_-]+)(?:&[\w=]*)*|https?:\/\/youtu\.be\/([a-zA-Z0-9_-]+)/g;

      formattedMessage = formattedMessage.replace(youtubeRegex, (url, id1, id2) => {
        const videoId = id1 || id2;
        return `
          <br>
          <iframe 
            src="https://www.youtube.com/embed/${videoId}" 
            frameborder="0" 
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
            allowfullscreen
            class="youtube-iframe">
          </iframe>`;
      });

      return formattedMessage;
    },
    keepFocus(e) {
      e.target.focus();
    },
  },
  mounted() {
    this.isMobile = /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);
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

.previous-chatting {
  margin: 12px;
  color: #acaaaa;
}

.recive-chatting {
  margin: 12px;
}

.blue {
  color: #4FADBD;
}

.grey {
  color: #858484;
}

.chat-list {
  max-width: 1000px;
  width: 100%;
  height: 55vh;
  display: flex;
  flex-direction: column-reverse;
}

.chat-div {
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-form {
  width: 50vw;
  min-width: 300px;
  max-height: 600px;
}

.textarea-chat {
  padding: .5em 1em .5em 1em;
  width: 100%;
  flex: 1;
}

.textarea-wrapper {
  display: flex;
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
  background-color: #fff;
  align-items: center;
}

button.textarea-button {
  border: none;
}

.message-content {
  word-wrap: break-word;
}

.chat-img {
  width: auto;
  height: auto;
  max-width: 200px;
  max-height: 200px;
}

.youtube-iframe {
  width: 100%;
  height: auto;
  aspect-ratio: 16 / 9;
  max-width: 450px;
}

@media (max-width: 767px) {
  .chat-list {
    height: 71vh;
  }
  
  .chat-img {
    max-width: 100px;
    max-height: 100px;
  }

  .youtube-iframe {
    max-width: 250px;
  }
}

</style>
  