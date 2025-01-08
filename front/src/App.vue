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

input, input::placeholder {
  font-family: "mabi";
  font-size: 1em;
}

textarea {
  font-family: "mabi";
  font-size: 1em;
  flex: 1;
  border: none;
  padding: .5em 3em .5em 2em;
  width: 100%;
  height: auto;
  resize: none;
  overflow-y: auto;
}

textarea::placeholder {
  font-family: "mabi";
  font-size: 1em;
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

textarea:focus {
  outline: none;
  border: none;
  box-shadow: none;
}
</style>
