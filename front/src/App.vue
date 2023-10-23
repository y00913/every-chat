<template>
  <div id="app">
    <Header></Header>

    <body>
      <ChatLayout />
    </body>

    <Footer></Footer>
  </div>
</template>

<script>
import Header from './components/Header.vue'
import ChatLayout from './components/Chat.vue'
import Footer from './components/Footer.vue'

export default {
  name: 'App',
  data() {
    return {

    }
  },
  methods: {
  },
  watch: {
  },
  components: {
    Header,
    ChatLayout,
    Footer,
  }
}
</script>

<style>
#app {
  font-family: 'mabi';
}

@font-face {
  font-family: "mabi";
  src: url('./assets/Mabinogi_Classic_TTF.ttf') format('truetype')
}
</style>
