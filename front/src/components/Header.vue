<template>
    <header>
        <title>every chat</title>
        <link rel="shortcut icon" type="image/x-icon" href="https://cdn-icons-png.flaticon.com/512/5962/5962500.png">
        <input type="checkbox" id="toggle" class="dark-mode-checkbox" v-model="isDarkMode" @change="handleTheme">
        <!-- <label for="toggle" class="toggle-switch">
            <span class="toggle-button"></span>
        </label> -->
        <h1>{{ title }}</h1>
    </header>
</template>

<script>
export default {
    name: 'App',
    data() {
        return {
            title: '에브리 챗 :)',
            // isDarkMode: localStorage.getItem('isDarkMode') === 'true',
            isDarkMode: false,
            isFirstLoad: true,
        }
    },
    methods: {
        handleTheme() {
            if (!this.isFirstLoad) {
                document.getElementById('app').style.transition = "filter 0.2s ease-out";
                localStorage.setItem('isDarkMode', this.isDarkMode);
            } else {
                this.isFirstLoad = false;
            }

            const app = document.getElementById('app');

            const filterStyle = this.isDarkMode 
                ? "invert(100%) hue-rotate(180deg)" 
                : "invert(0%) hue-rotate(0deg)";

            app.style.filter = filterStyle;
            app.style.webkitFilter = filterStyle;
        }
    },
    mounted() {
        this.handleTheme();
    },
}
</script>

<style scoped>
header {
    margin-top: 5vh;
    margin-bottom: 1vh;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
}

@media (max-width: 767px) {
    header {
        margin-top: 0;
    }

    h1 {
        margin-bottom: 0;
    }
}

h1 {
    text-align: center;
    flex-grow: 1;
}

.dark-mode-checkbox {
    display: none;
}

.toggle-switch {
    width: 50px;
    height: 25px;
    position: absolute;
    top: 50%;
    left: 50px;
    border-radius: 30px;
    background-color: #fff;
    box-shadow: 0 0 16px 3px rgba(0, 0, 0, 0.15);
    cursor: pointer;
    transform: translateY(-50%);
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
