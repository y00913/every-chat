<template>
  <Header v-if="showHeader" />
  <main :class="{ 'chat-main': $route.name === 'Chat' }">
    <router-view />
  </main>
  <Footer v-if="showFooter" />
</template>

<script>
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'

export default {
  name: 'App',
  data() {
    return {
      isMobile: false,
    }
  },
  methods: {
    checkMobileView() {
      this.isMobile = window.innerWidth < 768;
      localStorage.setItem('isMobile', this.isMobile);
    },
  },
  watch: {
  },
  components: {
    Header,
    Footer,
  },
  mounted() {
    this.checkMobileView();
  },
  computed: {
    showHeader() {
      return this.$route.name !== 'Chat';
    },
    showFooter() {
      return this.$route.name !== 'Chat';
    },
  }
}
</script>

<style>
#app {
  font-family: 'mabi';
  width: 100vw;
  height: auto;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

@font-face {
  font-family: "mabi";
  src: url('./assets/font/Mabinogi_Classic_TTF.ttf') format('truetype')
}

main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

body {
  text-align: center;
  margin: 0;
  background-color: #f8f9fa;
}

button {
  color: #444444;
  background: #F3F3F3;
  border: 2px #DADADA solid;
  padding: 8px 14px 6px 14px;
  border-radius: 1em;
  font-weight: bold;
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

input {
  font-family: "mabi";
  font-size: 14px;
  padding: 3px 10px 3px 10px;
  outline: none;
  box-shadow: none;
  width: 50%
}

textarea {
  font-family: "mabi";
  font-size: 1em;
  height: auto;
  overflow-y: auto;
  resize: none;
  outline: none;
  border: none;
  box-shadow: none;
}

textarea::placeholder {
  font-family: "mabi";
  font-size: 14px;
}

button.textarea-button {
  border: none;
}

.chat-main {
  height: 100vh;
}


@media (max-width: 767px) {
  button {
    padding: 6px 6px 4px 6px;
    border-radius: 10px;
  }
}
</style>
