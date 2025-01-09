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
  background-color: white;
  min-height: 100vh;
}

@font-face {
  font-family: "mabi";
  src: url('./assets/font/Mabinogi_Classic_TTF.ttf') format('truetype')
}

body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin: 0;
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

input {
  font-family: "mabi";
  font-size: 14px;
  padding: 3px 10px 3px 10px;
  outline: none;
  box-shadow: none;
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
</style>
