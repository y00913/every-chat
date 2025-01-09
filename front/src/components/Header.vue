<template>
    <header>
        <title>every chat</title>
        <link rel="shortcut icon" type="image/x-icon" href="https://cdn-icons-png.flaticon.com/512/5962/5962500.png">
        <input type="checkbox" id="toggle" class="dark-mode-checkbox" v-model="isDarkMode" @change="handleTheme">
        <label for="toggle" class="toggle-switch">
            <span class="toggle-button"></span>
        </label>
        <h1>{{ title }}</h1>
    </header>
</template>

<script>
export default {
    name: 'App',
    data() {
        return {
            title: '에브리 챗 :)',
            isDarkMode: localStorage.getItem('isDarkMode') === 'true',
            isFirstLoad: true,
        }
    },
    methods: {
        handleTheme() {
            if (!this.isFirstLoad) {
                document.getElementById('app').style.transition = "filter 0.5s ease, background-color 0.5s ease";
                this.isFirstLoad = false;
            }

            localStorage.setItem('isDarkMode', this.isDarkMode);

            if (this.isDarkMode) {
                document.getElementById('app').style.filter = "invert(100%) hue-rotate(180deg)";
                document.getElementById('app').style.backgroundColor = "#121212";
                document.getElementById('app').style.webkitFilter = "invert(100%) hue-rotate(180deg)";
            } else {
                document.getElementById('app').style.filter = "invert(0%) hue-rotate(0deg)";
                document.getElementById('app').style.backgroundColor = "#ffffff";
                document.getElementById('app').style.webkitFilter = "invert(0%) hue-rotate(0deg)";
            }
        }
    },
    mounted() {
        this.handleTheme();
    },
}
</script>

<style>
header {
    margin-top:5vh;
    margin-bottom: 1vh;
}

h1 {
    text-align: center;
}

.dark-mode-checkbox {
    display: none;
}

.toggle-switch {
    width: 50px;
    height: 25px;
    display: block;
    position: fixed;
    top: 50px;
    left: 50px;
    border-radius: 30px;
    background-color: #fff;
    box-shadow: 0 0 16px 3px rgba(0, 0, 0, 0.15);
    cursor: pointer;
}

.toggle-button {
    width: 20px;
    height: 20px;
    position: absolute;
    top: 50%;
    left: 4px;
    transform: translateY(-50%);
    border-radius: 50%;
    background: black;
}

.dark-mode-checkbox:checked + .toggle-switch .toggle-button {
    left: calc(100% - 24px);
}

.toggle-switch, .toggle-button {
    transition: all 0.2s ease-in;
}
</style>
