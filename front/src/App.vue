<template>
  <div id="app">
    <Header></Header>

    <body>
      <router-view />
    </body>

    <Footer></Footer>
  </div>
</template>

<script>
import Header from './components/Header.vue'
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
  src: url('./assets/font/Mabinogi_Classic_TTF.ttf') format('truetype')
}
</style>
