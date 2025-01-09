<template>
    <header style="margin-top:5vh; margin-bottom: 1vh; display: flex; align-items: center; justify-content: space-between;">
        <title>every chat</title>
        <link rel="shortcut icon" type="image/x-icon" href="https://cdn-icons-png.flaticon.com/512/5962/5962500.png">
        <h1 style="flex-grow: 1; text-align: center;">{{ title }}</h1>
        <input type="checkbox" v-model="isDarkMode" @change="handleTheme" style="margin-left: auto;">
    </header>
</template>

<script>
export default {
    name: 'App',
    data() {
        return {
            title: '에브리 챗 :)',
            isDarkMode: localStorage.getItem('isDarkMode') === 'true',
        }
    },
    methods: {
        handleTheme() {
            localStorage.setItem('isDarkMode', this.isDarkMode);

            if (this.isDarkMode) {
                document.documentElement.style.transition = "filter 0.5s ease, background-color 0.5s ease";
                document.documentElement.style.filter = "invert(100%) hue-rotate(180deg)";
                document.documentElement.style.backgroundColor = "#121212";
                document.documentElement.style.webkitFilter = "invert(100%) hue-rotate(180deg)";
            } else {
                document.documentElement.style.transition = "filter 0.5s ease, background-color 0.5s ease";
                document.documentElement.style.filter = "invert(0%) hue-rotate(0deg)";
                document.documentElement.style.backgroundColor = "#ffffff";
                document.documentElement.style.webkitFilter = "invert(0%) hue-rotate(0deg)";
            }
        }
    },
    mounted() {
        this.handleTheme();
    },
}
</script>

<style></style>
