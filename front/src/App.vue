<template>
  <Header>
    <Header/>
  </Header>
  <body>
    <Chat/>
  </body>
  <footer>
    <Footer/>
  </footer>
</template>

<script>
import Header from './components/Header.vue'
import Chat from './components/Chat.vue'
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
    Chat,
    Footer,
  }
}
</script>

<style>
body {
  font-family: 'mabi';
}

@font-face {
  font-family: "mabi";
  src: url('./assets/Mabinogi_Classic_TTF.ttf') format('truetype')
}
</style>
